/*
 * Copyright (c) 1997, 2025, Oracle and/or its affiliates. All rights reserved.
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
 *
 */

#include "oops/markWord.hpp"
#include "runtime/basicLock.inline.hpp"
#include "runtime/javaThread.hpp"
#include "runtime/objectMonitor.inline.hpp"
#include "utilities/ostream.hpp"

#ifdef _LP64
STATIC_ASSERT(markWord::klass_shift + markWord::klass_bits == 64);
// The hash (preceding klass bits) shall be a direct neighbor but not interleave
STATIC_ASSERT(markWord::klass_shift == markWord::hash_bits + markWord::hash_shift);
#endif

markWord markWord::displaced_mark_helper() const {
  assert(has_displaced_mark_helper(), "check");
  if (has_monitor()) {
    // Has an inflated monitor. Must be checked before has_locker().
    ObjectMonitor* monitor = this->monitor();
    return monitor->header();
  }
  if (has_locker()) {  // has a stack lock
    BasicLock* locker = this->locker();
    return locker->displaced_header();
  }
  // This should never happen:
  fatal("bad header=" INTPTR_FORMAT, value());
  return markWord(value());
}

void markWord::set_displaced_mark_helper(markWord m) const {
  assert(has_displaced_mark_helper(), "check");
  if (has_monitor()) {
    // Has an inflated monitor. Must be checked before has_locker().
    ObjectMonitor* monitor = this->monitor();
    monitor->set_header(m);
    return;
  }
  if (has_locker()) {  // has a stack lock
    BasicLock* locker = this->locker();
    locker->set_displaced_header(m);
    return;
  }
  // This should never happen:
  fatal("bad header=" INTPTR_FORMAT, value());
}

void markWord::print_on(outputStream* st, bool print_monitor_info) const {
  if (is_marked()) {  // last bits = 11
    st->print(" marked(" INTPTR_FORMAT ")", value());
  } else if (has_monitor()) {  // last bits = 10
    // have to check has_monitor() before is_locked()
    st->print(" monitor(" INTPTR_FORMAT ")=", value());
    if (print_monitor_info && !UseObjectMonitorTable) {
      ObjectMonitor* mon = monitor();
      if (mon == nullptr) {
        st->print("null (this should never be seen!)");
      } else {
        mon->print_on(st);
      }
    }
  } else if (is_locked()) {  // last bits != 01 => 00
    // thin locked
    st->print(" locked(" INTPTR_FORMAT ")", value());
  } else {
    st->print(" mark(");
    if (is_unlocked()) {   // last bits = 01
      st->print("is_unlocked");
      if (has_no_hash()) {
        st->print(" no_hash");
      } else {
        st->print(" hash=" INTPTR_FORMAT, hash());
      }
    } else {
      st->print("??");
    }
    st->print(" age=%d)", age());
  }
}

markWord markWord::flat_array_prototype(LayoutKind lk) {
  switch(lk) {
    case LayoutKind::ATOMIC_FLAT:
    case LayoutKind::NON_ATOMIC_FLAT:
      return markWord(null_free_flat_array_pattern);
      break;
    case LayoutKind::NULLABLE_ATOMIC_FLAT:
      return markWord(nullable_flat_array_pattern);
      break;
    default:
      ShouldNotReachHere();
  }
}
