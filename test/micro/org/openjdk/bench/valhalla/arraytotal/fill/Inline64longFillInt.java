/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates. All rights reserved.
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
package org.openjdk.bench.valhalla.arraytotal.fill;

import org.openjdk.bench.valhalla.arraytotal.util.StatesQ64long;
import org.openjdk.bench.valhalla.types.Int64;
import org.openjdk.bench.valhalla.types.Q64long;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;

import java.util.Arrays;

public class Inline64longFillInt extends StatesQ64long {

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static Int64 get(int i) {
        return new Q64long(i);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Val_fill0(Val_as_Val st) {
        Q64long[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (Q64long)v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Val_fill1(Val_as_Val st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = (Q64long)v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Ref_fill0(Val_as_Ref st) {
        Q64long[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (Q64long)v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Ref_fill1(Val_as_Ref st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = (Q64long)v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Ref_fill0(Ref_as_Ref st) {
        Q64long[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (Q64long)v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Ref_fill1(Ref_as_Ref st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = (Q64long)v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Int_fill0(Val_as_Int st) {
        Int64[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Int_fill1(Val_as_Int st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Int_fill0(Ref_as_Int st) {
        Int64[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Int_fill1(Ref_as_Int st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Int_as_Int_fill0(Int_as_Int st) {
        Int64[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Int_as_Int_fill1(Int_as_Int st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Obj_fill0(Val_as_Obj st) {
        Object[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Obj_fill1(Val_as_Obj st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Obj_fill0(Ref_as_Obj st) {
        Object[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Obj_fill1(Ref_as_Obj st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Int_as_Obj_fill0(Int_as_Obj st) {
        Object[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Int_as_Obj_fill1(Int_as_Obj st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Obj_as_Obj_fill0(Obj_as_Obj st) {
        Object[] arr = st.arr;
        Int64 v = get(42);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Obj_as_Obj_fill1(Obj_as_Obj st) {
        int len = st.arr.length;
        Int64 v = get(42);
        for (int i = 0; i < len; i++) {
            st.arr[i] = v;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Val_arrayfill(Val_as_Val st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Ref_arrayfill(Val_as_Ref st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Ref_arrayfill(Ref_as_Ref st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Int_arrayfill(Val_as_Int st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Int_arrayfill(Ref_as_Int st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Int_as_Int_arrayfill(Int_as_Int st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Val_as_Obj_arrayfill(Val_as_Obj st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Ref_as_Obj_arrayfill(Ref_as_Obj st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Int_as_Obj_arrayfill(Int_as_Obj st) {
        Arrays.fill(st.arr, get(42));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void Int_to_Obj_as_Obj_arrayfill(Obj_as_Obj st) {
        Arrays.fill(st.arr, get(42));
    }

}
