/**
 * 
 */
package org.qunix.nqueens;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.qunix.nqueens.exceptions.ChessBoardSizeOutOfRangeException;
import org.qunix.nqueens.leetcode.Solution;
import org.qunix.nqueens.leetcode.Solution2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author burak
 *
 */
public class NqueensTest {

  private static final Logger logger = LoggerFactory.getLogger(NQueens.class);

  private static final int[][] KNOWN_RESULTS =
      {{1, 1}, {10, 724}, {2, 0}, {3, 0}, {4, 2}, {5, 10}, {6, 4}, {7, 40}, {8, 92}};

  @Test(expected = ChessBoardSizeOutOfRangeException.class)
  public void constructorFailTest() {
    new NQueens(0);
  }

  @Test(expected = ChessBoardSizeOutOfRangeException.class)
  public void constructorFailNegativeTest() {
    new NQueens(-1);
  }

  @Test(expected = ChessBoardSizeOutOfRangeException.class)
  public void constructorFailMaxTest() {
    new NQueens(32769);
  }

  @Test
  public void resultTest() {

    for (int[] result : KNOWN_RESULTS) {
      Solution solution = new Solution(); // leetcode impl.
      Solution2 solution2 = new Solution2(); // leetcode impl.
      logger.info("Testing {}", result);
      List<Queen[]> queens = new NQueens(result[0]).visit();
      int sol = solution.solveNQueens(result[0]).size();
      int sol2 = solution2.totalNQueens(result[0]);
      logger.info("result size: {} ", queens.size());
      assertEquals(queens.size(), result[1]);
      logger.info("solution 1 size: {} ", sol);
      assertEquals(queens.size(), sol);
      logger.info("solution 2 size: {} ", sol2);
      assertEquals(queens.size(), sol2);
    }


  }

  @Test
  public void singleThreadTest() {
    List<Queen[]> queens = new NQueens(1).visit(1);
    assertEquals(queens.size(), 1);
    logger.info("Single thread result: {}", queens);
    assertEquals(queens.get(0)[0].getRow(), 0);
    assertEquals(queens.get(0)[0].getColumn(), 0);

  }
  
  @Test
  public void singleThreadsTest() {
    List<Queen[]> queens = new NQueens(4).visit();
    assertEquals(queens.size(), 2);
    logger.info("Single thread result size: {}", queens.size());
    queens.stream().forEach(a->{
      logger.info("Solution: {}",Arrays.toString(a));
    });
    assertEquals(queens.get(0)[0].getRow(), 0);
    assertEquals(queens.get(0)[0].getColumn(), 1);
    assertEquals(queens.get(1)[0].getRow(), 0);
    assertEquals(queens.get(1)[0].getColumn(), 2);

  }

  @Test
  public void multiThreadTest() {
    List<Queen[]> queens = new NQueens(8).visit(4);
    logger.info("Multi thread result for N:8 {}", queens);
    assertEquals(queens.size(), 92);

  }
}
