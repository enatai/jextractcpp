/*
 * Copyright (c) 2020, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.openjdk.jextract.test.toolprovider;

import java.nio.file.Path;

import testlib.TestUtils;
import org.testng.annotations.Test;
import testlib.JextractToolRunner;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class Test8245767 extends JextractToolRunner {
    @Test
    public void testTypedefs() {
        Path test8245767Output = getOutputFilePath("test8245767_gen");
        Path test8245767H = getInputFilePath("test8245767.h");
        run("--output", test8245767Output.toString(), test8245767H.toString()).checkSuccess();
        try(TestUtils.Loader loader = TestUtils.classLoader(test8245767Output)) {
            Class<?> cls = loader.loadClass("test8245767_h");
            assertNotNull(cls);

            // check Point_t
            Class<?> point_tCls = loader.loadClass("Point_t");
            assertNotNull(point_tCls);

            // check Point
            Class<?> pointCls = loader.loadClass("Point");
            assertNotNull(pointCls);
            assertTrue(pointCls.isAssignableFrom(point_tCls));
        } finally {
            TestUtils.deleteDir(test8245767Output);
        }
    }
}
