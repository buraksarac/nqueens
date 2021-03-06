/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved. DO NOT ALTER OR
 * REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License version 2 only, as published by the Free Software Foundation. Oracle
 * designates this particular file as subject to the "Classpath" exception as provided by Oracle in
 * the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version 2 along with this work;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA or visit www.oracle.com
 * if you need additional information or have any questions.
 */
package org.qunix.nqueens.bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.qunix.nqueens.NQueens;
import org.qunix.nqueens.leetcode.Solution;
import org.qunix.nqueens.leetcode.Solution2;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SingleThreadNqueensPerf {

  /*
   * JVMs are notoriously good at profile-guided optimizations. This is bad for benchmarks, because
   * different tests can mix their profiles together, and then render the "uniformly bad" code for
   * every test. Forking each test can help to evade this issue.
   *
   * JMH will fork the tests by default.
   */

  /*
   * Suppose we have this simple counter interface, and also have two implementations. Even though
   * those are semantically the same, from the JVM standpoint, those are distinct classes.
   */

  @Benchmark
  public void localImpl() {
    new NQueens(8).visit();
  }
  
  @Benchmark
  public void leetCodeOldImpl() {
    new Solution().solveNQueens(8);
  }
  
  @Benchmark
  public void leetCodeCountImpl() {
    new Solution2().totalNQueens(8);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder().include("." + SingleThreadNqueensPerf.class.getSimpleName()+ ".*").forks(1).build();

    new Runner(opt).run();
  }

}
