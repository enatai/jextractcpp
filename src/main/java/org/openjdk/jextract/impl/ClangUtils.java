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
import org.openjdk.jextract.Type.Delegated;
import org.openjdk.jextract.Type.Function;
import org.openjdk.jextract.clang.Cursor;
import org.openjdk.jextract.clang.CursorKind;
import org.openjdk.jextract.clang.SourceLocation;
import org.openjdk.jextract.clang.SourceLocation.Location;
import org.openjdk.jextract.clang.Type;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.lang.foreign.MemoryLayout;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

/**
 * General utility functions for Clang
 */
class ClangUtils {

    public static String toString(Cursor c) {
        var str = String.format("name=[%s], kind=[%s]", c.displayName(), c.kind());
        if (!Objects.equals(c.displayName(), c.spelling()))
            str += ", spelling=[" + c.spelling() + "]";
        str += Optional.ofNullable(c.getSourceLocation()).map(SourceLocation::getFileLocation).map(loc -> {
            return String.format(", file location=%s(%s)", loc.path(), loc.line());
        }).orElse("");
        return "clang cursor[" + str + "]";
    }

    public static String toString(Type t) {
        return String.format("clang type[type=[%s], kind=[%s], canonical type=[%s]]", t.spelling(), t.kind(), t.canonicalType().spelling());
    }

    public static String getNativeName(org.openjdk.jextract.Declaration.Function funcTree) {
        return (String)funcTree.getAttribute("LINK").orElse(List.of(funcTree.name())).get(0);
    }
}