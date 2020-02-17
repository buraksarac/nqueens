/**
 * 
 */
package org.qunix.nqueens;

import java.util.List;
import org.junit.Test;

/**
 * @author burak
 *
 */
public class ResultGenerator {

  @Test
  public void generate() {
    for(int i = 1; i< 20;i++) {
      List<Queen[]> queens = new NQueens(i).visit(i);
      System.out.println(queens.size());
    }
  }
}
