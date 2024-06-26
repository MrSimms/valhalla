/*
 * Copyright (c) 2010, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

package jdk.javadoc.internal.doclets.formats.html.markup;

import jdk.javadoc.internal.doclets.toolkit.util.Utils;

/**
 * Enum representing HTML tag attributes.
 */
public enum HtmlAttr {
    ALT,
    ARIA_CONTROLS("aria-controls"),
    ARIA_EXPANDED("aria-expanded"),
    ARIA_LABEL("aria-label"),
    ARIA_LABELLEDBY("aria-labelledby"),
    ARIA_ORIENTATION("aria-orientation"),
    ARIA_SELECTED("aria-selected"),
    AUTOCOMPLETE,
    AUTOCAPITALIZE,
    CHECKED,
    CLASS,
    CLEAR,
    COLS,
    CONTENT,
    DATA_COPIED("data-copied"), // custom HTML5 data attribute
    DISABLED,
    FOR,
    HREF,
    HTTP_EQUIV("http-equiv"),
    ID,
    LANG,
    NAME,
    ONCLICK,
    ONKEYDOWN,
    ONLOAD,
    PLACEHOLDER,
    REL,
    ROLE,
    ROWS,
    SCOPE,
    SCROLLING,
    SRC,
    STYLE,
    SUMMARY,
    TABINDEX,
    TARGET,
    TITLE,
    TYPE,
    VALUE,
    WIDTH;

    private final String value;

    public enum Role {

        BANNER,
        CONTENTINFO,
        MAIN,
        NAVIGATION,
        REGION;

        private final String role;

        Role() {
            role = Utils.toLowerCase(name());
        }

        public String toString() {
            return role;
        }
    }

    public enum InputType {

        CHECKBOX,
        RESET,
        TEXT;

        private final String type;

        InputType() {
            type = Utils.toLowerCase(name());
        }

        public String toString() {
            return type;
        }
    }

    HtmlAttr() {
        this.value = Utils.toLowerCase(name());
    }

    HtmlAttr(String name) {
        this.value = name;
    }

    public String toString() {
        return value;
    }
}
