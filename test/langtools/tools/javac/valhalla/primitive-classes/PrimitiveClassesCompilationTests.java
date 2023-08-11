/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

/**
 * PrimitiveClassesCompilationTests
 *
 * @test
 * @bug 8297207
 * @summary Negative compilation tests, and positive compilation (smoke) tests for Primitive Classes
 * @library /lib/combo /tools/lib
 * @modules
 *     jdk.compiler/com.sun.tools.javac.util
 *     jdk.compiler/com.sun.tools.javac.api
 *     jdk.compiler/com.sun.tools.javac.main
 *     jdk.compiler/com.sun.tools.javac.code
 *     jdk.jdeps/com.sun.tools.classfile
 * @build toolbox.ToolBox toolbox.JavacTask
 * @run testng PrimitiveClassesCompilationTests
 */

import java.io.File;

import java.util.List;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPool.CONSTANT_Class_info;
import com.sun.tools.classfile.ConstantPool.CONSTANT_Fieldref_info;
import com.sun.tools.classfile.ConstantPool.CONSTANT_Methodref_info;
import com.sun.tools.classfile.Field;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.Method;

import com.sun.tools.javac.code.Flags;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import tools.javac.combo.CompilationTestCase;

import toolbox.ToolBox;

@Test
public class PrimitiveClassesCompilationTests extends CompilationTestCase {

    private static String[] DEFAULT_OPTIONS = {"-XDenablePrimitiveClasses"};

    ToolBox tb = new ToolBox();

    public PrimitiveClassesCompilationTests() {
        setDefaultFilename("PrimitiveClassTest.java");
        setCompileOptions(DEFAULT_OPTIONS);
    }

    public void testSupers() {
        assertOK(
                """
                interface GoodSuperInterface {}
                abstract class GoodSuper extends Object {}
                value class PC extends GoodSuper implements GoodSuperInterface {}
                """);

        assertOK(
                """
                abstract class Integer extends Number {
                    public double doubleValue() { return 0; }
                    public float floatValue() { return 0; }
                    public long longValue() { return 0; }
                    public int intValue() { return 0; }
                }
                value class PC extends Integer {}
                """);

        assertOK(
                """
                value class PC extends Number {
                    public double doubleValue() { return 0; }
                    public float floatValue() { return 0; }
                    public long longValue() { return 0; }
                    public int intValue() { return 0; }
                }
                """);

        assertOK(
                """
                abstract class SuperWithStaticField {
                    static int x;
                }
                value class PC extends SuperWithStaticField {}
                """);

        assertOK(
                """
                abstract class SuperWithEmptyNoArgCtor {
                    public SuperWithEmptyNoArgCtor() {
                        // Programmer supplied ctor but injected super call
                    }
                }
                abstract class SuperWithEmptyNoArgCtor_01 extends SuperWithEmptyNoArgCtor {
                    public SuperWithEmptyNoArgCtor_01() {
                        super();  // programmer coded chaining no-arg constructor
                    }
                }
                abstract class SuperWithEmptyNoArgCtor_02 extends SuperWithEmptyNoArgCtor_01 {
                    // Synthesized chaining no-arg constructor
                }
                value class PC extends SuperWithEmptyNoArgCtor_02 {}
                """);

        assertFail("compiler.err.concrete.supertype.for.value.class",
                """
                class BadSuper {}
                value class PC extends BadSuper {}
                """);

        assertFail("compiler.err.instance.field.not.allowed",
                """
                abstract class SuperWithInstanceField {
                    int x;
                }
                abstract class SuperWithInstanceField_01 extends SuperWithInstanceField {}
                value class PC extends SuperWithInstanceField_01 {}
                """);

        assertFail("compiler.err.abstract.value.class.no.arg.constructor.must.be.empty",
                """
                abstract class SuperWithNonEmptyNoArgCtor {
                    public SuperWithNonEmptyNoArgCtor() {
                        System.out.println("Non-Empty");
                    }
                }
                abstract class SuperWithNonEmptyNoArgCtor_01 extends SuperWithNonEmptyNoArgCtor {}
                value class PC extends SuperWithNonEmptyNoArgCtor_01 {}
                """);

        assertFail("compiler.err.abstract.value.class.constructor.cannot.take.arguments",
                """
                abstract class SuperWithArgedCtor {
                    public SuperWithArgedCtor() {}
                    public SuperWithArgedCtor(String s) {
                    }
                }
                abstract class SuperWithArgedCtor_01 extends SuperWithArgedCtor {}
                value class PC extends SuperWithArgedCtor_01 {}
                """);

        assertFail("compiler.err.abstract.value.class.declares.init.block",
                """
                abstract class SuperWithInstanceInit {
                    {
                        System.out.println("Disqualified from being super");
                    }
                }
                abstract class SuperWithInstanceInit_01 extends SuperWithInstanceInit {
                    {
                        // Not disqualified since it is a meaningless empty block.
                    }
                }
                value class PC extends SuperWithInstanceInit_01 {}
                """);

        assertFail("compiler.err.super.class.method.cannot.be.synchronized",
                """
                abstract class SuperWithSynchronizedMethod {
                    synchronized void foo() {}
                }
                abstract class SuperWithSynchronizedMethod_1 extends SuperWithSynchronizedMethod {}
                value class PC extends SuperWithSynchronizedMethod_1 {}
                """);

        assertFail("compiler.err.abstract.value.class.cannot.be.inner",
                """
                class Outer {
                    abstract class InnerSuper {}
                }
                value class PC extends Outer.InnerSuper {}
                """);
    }

    public void testFinalFields() {
        String[] sources = new String[] {
                """
                value class Test {
                    final int x = 10;
                    Test() {
                        x = 10;
                    }
                }
                """,
                """
                value class Test {
                    final int x = 10;
                    void foo() {
                        x = 10;
                    }
                }
                """
        };
        for (String source : sources) {
            assertFail("compiler.err.cant.assign.val.to.var", source);
        }

        assertFail("compiler.err.var.might.already.be.assigned",
                """
                value class Test {
                    final int x;
                    Test() {
                        x = 10;
                        x = 10;
                    }
                }
                """
        );
    }

    public void testWithFieldNeg() {
        String[] sources = new String[] {
                """
                value final class A {
                    final int x = 10;
                    value final class B {
                        final A a = A.default;
                        void foo(A a) {
                            a.x = 100;
                        }
                    }
                }
                """,
                """
                value final class A {
                    static final int sx = 10;
                    value final class B {
                        final A a = A.default;
                        void foo(A a) {
                            a.sx = 100;
                        }
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    value final class B {
                        final A a = A.default;
                    }
                    void withfield(B b) {
                            b.a.x = 11;
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    void foo(A a) {
                        a.x = 100;
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    void foo(A a) {
                        (a).x = 100;
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    void foo(final A fa) {
                        fa.x = 100;
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    void foo() {
                        x = 100;
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    void foo() {
                        this.x = 100;
                    }
                }
                """,
                """
                value final class A {
                    final int x = 10;
                    void foo() {
                        A.this.x = 100;
                    }
                }
                """,
        };
        for (String source : sources) {
            assertFail("compiler.err.cant.assign.val.to.var", source);
        }
    }

    public void testIllegalModifiers() {
        assertFail("compiler.err.mod.not.allowed.here",
                """
                class Test {
                    value public void m() {}
                }
                """);
        assertFail("compiler.err.mod.not.allowed.here",
                """
                class Test {
                    void m() {
                        int[] ia = new value int[10];
                    }
                }
                """);
        assertFail("compiler.err.mod.not.allowed.here",
                """
                class Test {
                    void m() {
                        new value String("Hello");
                    }
                }
                """);
        /*
        // this test is passing, not sure if this is correct
        assertFail("compiler.err.mod.not.allowed.here",
                """
                class Test {
                    interface I {}
                    void m() {
                        new value I() {};
                    }
                }
                """);
        */
    }

    public void testValueClassesAsTypeParams() {
        String[] sources = new String[] {
                """
                import java.util.ArrayList;
                value class ValueOverGenericsTest {
                    ArrayList<ValueOverGenericsTest> ax = null;
                }
                """,
                """
                import java.util.ArrayList;
                value class ValueOverGenericsTest {
                    void foo(ArrayList<? extends ValueOverGenericsTest> p) {}
                }
                """,
                """
                import java.util.ArrayList;
                value class ValueOverGenericsTest {
                    void foo() {
                        new <ValueOverGenericsTest> ArrayList<Object>();
                    }
                }
                """,
                """
                import java.util.ArrayList;
                value class ValueOverGenericsTest {
                    void foo() {
                        this.<ValueOverGenericsTest>foo();
                    }
                }
                """,
                """
                import java.io.Serializable;
                value class ValueOverGenericsTest {
                    void foo() {
                        Object o = (ValueOverGenericsTest & Serializable) null;
                    }
                }
                """,
        };
        for (String source : sources) {
            assertOK(source);
        }
    }

    public void testLocalPrimitiveClasses() {
        assertFail("compiler.err.cant.inherit.from.final",
                """
                class ValueModifierTest {
                    interface Value {}
                    void goo() {
                        value class Value {}
                        new Value() {};
                    }
                }
                """);
        assertFail("compiler.err.cant.inherit.from.final",
                """
                class ValueModifierTest {
                    interface Value {}
                    void goo() {
                        value class Value {}
                        new value Value() {};
                    }
                }
                """);
    }

    public void testDefaultOnUnknownClass() {
        assertFail("compiler.err.cant.resolve.location",
                """
                class Test {
                    void m() {
                        Object o = Unknown.default;
                    }
                }
                """);
        assertFail("compiler.err.cant.resolve.location",
                """
                class Test {
                    void m() {
                        Object o = Unknown1.Unknown2.default;
                    }
                }
                """);
    }

    public void testUncheckedDefaultWarning() {
        String[] previousOptions = getCompileOptions();
        try {
            String[] testOptions = {"-Xlint:all", "-XDenablePrimitiveClasses"};
            setCompileOptions(testOptions);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    value class UncheckedDefault<E> {
                        void m() {
                            UncheckedDefault<String> foo = UncheckedDefault.default;
                        }
                    }
                    """);
        } finally {
            setCompileOptions(previousOptions);
        }
    }

    public void testSuperInvocation() {
        assertFail("compiler.err.call.to.super.not.allowed.in.value.ctor",
                """
                value class PC {
                    PC(String s) {
                        super();  // Error.
                    }
                }
                """);
    }

    public void testOverloadResolution() {
        assertOK(
                """
                class OverloadingPhaseTest {
                    static value class V {}
                    static String roo(V v, int i) {
                        return "";
                    }
                    static String roo(V v, Integer i) {
                        return "";
                    }
                    void m(V o) {
                        String result = roo(o, 0);
                    }
                }
                """);
        assertOK(
                """
                class OverloadingPhaseTest {
                    static value class V {}
                    static String roo(V v, int i) {
                        return "";
                    }
                    static String roo(V v, Integer i) {
                        return "";
                    }
                    void m(V o) {
                        String result = roo(o, Integer.valueOf(0));
                    }
                }
                """);
    }

    public void testNoVolatileFields() {
        assertFail("compiler.err.illegal.combination.of.modifiers",
                """
                value class Bar {
                    volatile int i = 0;
                }
                """);
    }

    public void testDualPath() {
        assertFail("compiler.err.already.defined",
                """
                value class DualPathInnerType  {
                    class Inner { }

                    static DualPathInnerType.Inner xi = new DualPathInnerType().new Inner();
                    DualPathInnerType.Inner xri = xi;

                    void f (DualPathInnerType.Inner xri) {}
                    void f (DualPathInnerType.Inner xri) {}
                }
                """);
    }

    public void testGenericArray() {
        String[] previousOptions = getCompileOptions();
        try {
            String[] testOptions = {"-Xlint:all", "-XDenablePrimitiveClasses"};
            setCompileOptions(testOptions);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo() {
                                Value<T>[] v = new Value[1];
                            }
                        }
                    }
                    """);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo() {
                                Value<Test>[] vx = new Value[1];
                            }
                        }
                    }
                    """);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo() {
                                Value<String>[] vs = new Value[1];
                            }
                        }
                    }
                    """);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo(Value<T>[] v) {
                                v = (Value<T> []) new Value[1];
                            }
                        }
                    }
                    """);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo(Value<Test>[] vx) {
                                vx = (Value<Test>[]) new Value[1];
                            }
                        }
                    }
                    """);
            assertOKWithWarning("compiler.warn.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo(Value<String>[] vs) {
                                vs = (Value<String>[]) new Value[1];
                            }
                        }
                    }
                    """);
            assertFail("compiler.err.prob.found.req",
                    """
                    class Test {
                        value class Value<T> {
                            T t = null;
                            void foo(Value<Test>[] vx, Value<String>[] vs) {
                                vx = vs;
                            }
                        }
                    }
                    """);
        } finally {
            setCompileOptions(previousOptions);
        }
    }

    public void testAdditionalGenericTests() {
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo() {
                        GenericInlineTest<String, Integer> g = new GenericInlineTest<String, Integer>();
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, High<String, Integer> h1) {
                        h1 = g;
                    }
                }
                """);
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, High<Integer, String> h2) {
                        h2 = g;
                    }
                }
                """);
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, Mid<String, Integer> m1) {
                        m1 = g;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, Mid<Integer, String> m2) {
                        m2 = g;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, Low<String, Integer> l1) {
                        l1 = g;
                    }
                }
                """);
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, Low<Integer, String> l2) {
                        l2 = g;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, Low<Integer, String> l2) {
                        l2 = g;
                        g = l2;
                    }
                }
                """);
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, Low<Integer, String> l2) {
                        l2 = g;
                        g = (GenericInlineTest<String, Integer>) l2;
                    }
                }
                """);
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, GenericInlineTest<String, Integer> r1) {
                        r1 = g;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, GenericInlineTest<Integer, String> r2) {
                        r2 = g;
                    }
                }
                """);
        assertOK(
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, GenericInlineTest<String, Integer> r1) {
                        r1 = g;
                        g = r1;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, GenericInlineTest<Integer, String> r2) {
                        r2 = g;
                        g = r2;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                abstract class Low<T, U> {}
                abstract class Mid<T, U> extends Low<U, T> {}
                abstract class High<T, U> extends Mid<U, T> {}

                value class GenericInlineTest<T, U> extends High<U, T> {
                    void foo(GenericInlineTest<String, Integer> g, GenericInlineTest<Integer, String> r2) {
                        r2 = g;
                        g = (GenericInlineTest<String, Integer>) r2;
                    }
                }
                """);
    }

    public void testPrimitiveAsTypeName() {
        String[] previousOptions = getCompileOptions();
        try {
            String[] testOptions = {"--source", "16"};
            setCompileOptions(testOptions);
            assertFail("compiler.err.feature.not.supported.in.source.plural",
                    """
                    class value {
                        value x;
                        value foo(int l) {}
                        Object o = new value value() {};
                    }
                    """);
            setCompileOptions(previousOptions);
            assertFail("compiler.err.restricted.type.not.allowed",
                    """
                    class value {}
                    """);
        } finally {
            setCompileOptions(previousOptions);
        }
    }

    public void testMiscThisLeak() {
        assertFail("compiler.err.this.exposed.prematurely",
                """
                class MiscThisLeak {
                    interface I {
                        void foo();
                    }
                    value class V {
                        int f;
                        V() {
                            I i = this::foo;
                        }

                        void foo() {}
                    }
                }
                """);
        assertOK(
                """
                class MiscThisLeak {
                    interface I {
                        void foo();
                    }
                    value class V {
                        int f;
                        V() {
                            I i = MiscThisLeak.this::foo;
                            f = 10;
                        }

                        void foo() {}
                    }
                    void foo() {}
                }
                """);
        assertFail("compiler.err.this.exposed.prematurely",
                """
                class MiscThisLeak {
                    interface I {
                        void foo();
                    }
                    value class V {
                        class K {}
                        int f;
                        V() {
                            new K();
                            f = 10;
                        }

                        void foo() {}
                    }
                    void foo() {}
                }
                """);
        assertFail("compiler.err.this.exposed.prematurely",
                """
                class MiscThisLeak {
                    interface I {
                        void foo();
                    }
                    value class V {
                        class K {}
                        int f;
                        V() {
                            this.new K();
                            f = 10;
                        }

                        void foo() {}
                    }
                    void foo() {}
                }
                """);
        assertOK(
                """
                class MiscThisLeak {
                    interface I {
                        void foo();
                    }
                    value class V {
                        class K {}
                        int f;
                        V() {
                            f = 10;
                            I i = this::foo;
                        }
                        void foo() {}
                    }
                    void foo() {}
                }
                """);
    }

    public void testCovariantArrayTest() {
        assertFail("compiler.err.prob.found.req",
                """
                class CovariantArrayTest {
                    value class V {
                        public final int v1;
                        private V () { v1 = 0; }
                    }
                    void m(int[] ia, Object[] oa) {
                        oa = (Object[])ia;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                class CovariantArrayTest {
                    value class V {
                        public final int v1;
                        private V () { v1 = 0; }
                    }
                    void m(int[] ia, Object[] oa) {
                        oa = ia;
                    }
                }
                """);
        assertOK(
                """
                class CovariantArrayTest {
                    value class V {
                        public final int v1;
                        private V () { v1 = 0; }
                    }
                    void m(int[] ia, Object[] oa) {
                        V[] va = new V[1];
                        Object[] oa2 = (Object[])va;
                        oa2 = va;
                    }
                }
                """);
        assertFail("compiler.err.prob.found.req",
                """
                class CovariantArrayTest {
                    value class V {
                        public final int v1;
                        private V () { v1 = 0; }
                    }
                    void m(int[] ia, Object[] oa) {
                        V[] va = new V[1];
                        Object[] oa2 = (Object[])va;
                        oa2 = va;
                        va = oa2;
                    }
                }
                """);
        assertOK(
                """
                class CovariantArrayTest {
                    value class V {
                        public final int v1;
                        private V () { v1 = 0; }
                    }
                    void m(int[] ia, Object[] oa) {
                        V[] va = new V[1];
                        Object[] oa2 = (Object[])va;
                        oa2 = va;
                        va = (V[]) oa2;
                    }
                }
                """);
    }

    public void testConflictingSuperInterface() {
        assertFail("compiler.err.cant.inherit.diff.arg",
                """
                class ConflictingSuperInterfaceTest {
                    interface I<T> {}
                    static abstract class S implements I<String> {}
                    value static class Foo extends S implements I<Integer> {
                        String s = "";
                    }
                }
                """);
    }
}