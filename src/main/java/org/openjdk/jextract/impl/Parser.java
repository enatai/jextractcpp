/*
 *  Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *   Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 *
 */
package org.openjdk.jextract.impl;

import org.openjdk.jextract.Declaration;
import org.openjdk.jextract.clang.Cursor;
import org.openjdk.jextract.clang.CursorKind;
import org.openjdk.jextract.clang.Diagnostic;
import org.openjdk.jextract.clang.Index;
import org.openjdk.jextract.clang.LibClang;
import org.openjdk.jextract.clang.SourceLocation;
import org.openjdk.jextract.clang.SourceRange;
import org.openjdk.jextract.clang.TranslationUnit;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getSimpleName());
    private final TreeMaker treeMaker;

    public Parser() {
        this.treeMaker = new TreeMaker();
    }

    public Declaration.Scoped parse(Path path, Collection<String> args) {
        LOGGER.log(Level.FINE, "Parsing file: {0}", path);
        try (Index index = LibClang.createIndex(false) ;
             TranslationUnit tu = index.parse(path.toString(),
                d -> {
                    if (d.severity() > Diagnostic.CXDiagnostic_Warning) {
                        LOGGER.log(Level.FINE, "Parse error: {0}", d.toString());
                        throw new ClangException(d.toString());
                    }
                },
            true, args.toArray(new String[0])) ;
            MacroParserImpl macroParser = MacroParserImpl.make(treeMaker, tu, args,
                    path.toString().endsWith(".hpp")))
        {

            List<Declaration> decls = new ArrayList<>();
            Cursor tuCursor = tu.getCursor();
            LOGGER.log(Level.FINE, "Parsing file: {0}", path);
            tuCursor.forEach(c -> {
                SourceLocation loc = c.getSourceLocation();
                if (loc == null) {
                    LOGGER.log(Level.FINER, "Source location empty, ignoring ...");
                    return;
                }

                SourceLocation.Location src = loc.getFileLocation();
                if (src == null) {
                    return;
                }

                LOGGER.log(Level.FINER, "Parsing cursor kind: {0}", c.kind());
                if (c.isDeclaration()) {
                    LOGGER.log(Level.FINER, "Parsing declaration in file: {0}", path);
                    var count = new int[1];
                    if (c.kind() == CursorKind.UnexposedDecl ||
                            c.kind() == CursorKind.Namespace) {
                        c.forEach(t -> {
                            Declaration declaration = treeMaker.createTree(t);
                            if (declaration != null) {
                                count[0]++;
                                decls.add(declaration);
                            }
                        });
                    } else {
                        Declaration decl = treeMaker.createTree(c);
                        if (decl != null) {
                            count[0]++;
                            decls.add(decl);
                        }
                    }
                    LOGGER.log(Level.FINER, "Number of declarations parsed: {0}", count[0]);
                } else if (isMacro(c) && src.path() != null) {
                    LOGGER.log(Level.FINER, "Parsing macro definition in file: {0}", path);
                    SourceRange range = c.getExtent();
                    String[] tokens = c.getTranslationUnit().tokens(range);
                    LOGGER.log(Level.FINER, "Tokens: {0}", Arrays.toString(tokens));
                    Optional<Declaration.Constant> constant = macroParser.parseConstant(c, c.spelling(), tokens);
                    if (constant.isPresent()) {
                        decls.add(constant.get());
                    } else {
                        LOGGER.log(Level.FINER, "No constants present");
                    }
                } else {
                    LOGGER.log(Level.FINE, "Parsing of cursor is not supported and will be ignored: {0}", ClangUtils.toString(c));
                }
            });

            decls.addAll(macroParser.macroTable.reparseConstants());
            Declaration.Scoped rv = treeMaker.createHeader(tuCursor, decls);
            return rv;
        }
    }

    private boolean isMacro(Cursor c) {
        return c.isPreprocessing() && c.kind() == CursorKind.MacroDefinition;
    }
}
