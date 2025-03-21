/*
 * Copyright (c) 2020, 2025, Oracle and/or its affiliates. All rights reserved.
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


/*
 * @test
 *
 * @summary
 * VM Testbase keywords: [quick, jpda, jvmti, noras]
 * VM Testbase readme:
 * DESCRIPTION
 *     The test exercises JVMTI function GetObjectMonitorUsage.
 * COMMENTS
 *     This test checks that GetObjectMonitorUsage works with inline types and always
 *     returns information consistent with a never locked monitor
 *
 * @enablePreview
 * @library  /vmTestbase
 *           /test/lib
 * @run driver jdk.test.lib.FileInstaller . .
 * @run main/othervm/native -agentlib:objmonusage007 nsk.jvmti.GetObjectMonitorUsage.objmonusage007
 */
package nsk.jvmti.GetObjectMonitorUsage;

import java.io.PrintStream;

public class objmonusage007 {
    final static int JCK_STATUS_BASE = 95;
    final static int NUMBER_OF_THREADS = 32;

    static {
        try {
            System.loadLibrary("objmonusage007");
        } catch (UnsatisfiedLinkError err) {
            System.err.println("Could not load objmonusage7 library");
            System.err.println("java.library.path:"
                + System.getProperty("java.library.path"));
            throw err;
        }
    }

    native static int getResult();
    native static void check(Object o, Thread owner, int ec, int wc);

    static value class LocalValue {
        int i = 0;
    }

    public static void main(String argv[]) {
        argv = nsk.share.jvmti.JVMTITest.commonInit(argv);

        System.exit(run(argv, System.out) + JCK_STATUS_BASE);
    }

    public static int run(String argv[], PrintStream out) {
        LocalValue lv = new LocalValue();
        objmonusage007.check(lv, null, 0, 0);

        return getResult();
    }
}

