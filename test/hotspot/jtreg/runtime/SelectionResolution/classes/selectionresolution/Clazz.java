/*
 * Copyright (c) 2016, 2025, Oracle and/or its affiliates. All rights reserved.
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

package selectionresolution;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.V1_8;


class Clazz extends ClassConstruct {

    /**
     * Construct a Class
     * @param name Name of Class
     * @param access Access for the Class
     */
    public Clazz(String name, int access, int index) {
        this(name, null, access, V1_8, index, new String[] { });
    }

    /**
     * Construct a Class
     * @param name Name of Class
     * @param extending Class being extended
     * @param access Access for the Class
     */
    public Clazz(String name, String extending, int access, int index) {
        this(name, extending, access, V1_8, index, new String[] { });
    }

    /**
     * Construct a Class
     * @param name Name of Class
     * @param extending Class being extended
     * @param access access for the Class
     * @param implementing Interfaces implemented
     */
    public Clazz(String name, String extending, int access, int index, String... implementing) {
        this(name, extending, access, V1_8, index, implementing);
    }

    /**
     * Construct a Class
     * @param name Name of Class
     * @param extending Class being extended
     * @param access Access for the Class
     * @param classFileVersion Class file version
     * @param implementing Interfaces implemented
     */
    public Clazz(String name, String extending, int access, int classFileVersion, int index, String... implementing) {
        super(name, extending == null ? "java/lang/Object" : extending, access | ACC_SUPER, classFileVersion, index, implementing);
        // Add the default constructor
        addMethod("<init>", "()V", ACC_PUBLIC).makeConstructor(extending, false);
    }
}
