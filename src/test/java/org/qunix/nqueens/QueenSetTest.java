/**
 * 
 */
package org.qunix.nqueens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.qunix.nqueens.exceptions.ChessBoardSizeOutOfRangeException;

/**
 * @author burak
 *
 */
public class QueenSetTest {

  @Test(expected = ChessBoardSizeOutOfRangeException.class)
  public void constructFailTest() {
    new QueenSet(0);
  }

  @Test
  public void constructTest() {
    QueenSet set = new QueenSet(8);
    assertTrue(set.getFirstQueenColumn() == 0);
    for (int i = 0; i < 8; i++) {
      assertFalse(set.isColumnReserved(i));
      assertFalse(set.isRowReserved(i));
    }
  }

  @Test
  public void addTest() {
    QueenSet set = new QueenSet(8);
    assertTrue(set.getFirstQueenColumn() == 0);
    for (int i = 0; i < 8; i++) {
      assertFalse(set.isColumnReserved(i));
      assertFalse(set.isRowReserved(i));
    }
    set.add(new Queen(0, 2, 8));
    assertFalse(set.isColumnReserved(1));
    assertFalse(set.isRowReserved(1));
    assertFalse(set.isColumnReserved(3));
    assertFalse(set.isRowReserved(2));
    assertTrue(set.isColumnReserved(2));
    assertTrue(set.isRowReserved(0));

  }

  @Test
  public void rollBackTest() {
    QueenSet set = new QueenSet(8);
    Queen q1 = new Queen(0, 0, 8);
    Queen q2 = new Queen(1, 2, 8);
    set.add(q1);
    set.add(q2);
    set.add(new Queen(5, 6, 8));
    set.rollback();
    Queen[] queens = set.getCopyOfQueens();
    assertEquals(2, set.size());
    assertEquals(q2, queens[1]);
    assertEquals(q1, queens[0]);
    set.rollback();
    assertEquals(1, set.size());
    assertEquals(q1, queens[0]);
    set.rollback();
    assertEquals(0, set.size());
  }

  @Test
  public void threatTest() {
    QueenSet set = new QueenSet(8);
    Queen q1 = new Queen(0, 0, 8);
    Queen q2 = new Queen(1, 2, 8);
    set.add(q1);
    set.add(q2);
    set.add(new Queen(5, 6, 8));
    assertTrue(set.isThreatening(new Queen(0, 1, 8)));
  }

  @Test
  public void reservedColumnRowTest() {
    QueenSet set = new QueenSet(8);
    Queen q1 = new Queen(0, 0, 8);
    Queen q2 = new Queen(1, 2, 8);
    set.add(q1);
    set.add(q2);
    set.add(new Queen(5, 6, 8));
    assertFalse(set.isColumnReserved(1));
    assertFalse(set.isColumnReserved(3));
    assertFalse(set.isColumnReserved(4));
    assertFalse(set.isColumnReserved(5));
    assertFalse(set.isRowReserved(2));
    assertFalse(set.isRowReserved(3));
    assertFalse(set.isRowReserved(4));

    assertTrue(set.isColumnReserved(0));
    assertTrue(set.isColumnReserved(2));
    assertTrue(set.isColumnReserved(6));
    assertTrue(set.isRowReserved(0));
    assertTrue(set.isRowReserved(1));
    assertTrue(set.isRowReserved(5));
  }

  @Test
  public void columnIteratorTest() {
    QueenSet set = new QueenSet(8);
    Queen q1 = new Queen(0, 0, 8);
    Queen q2 = new Queen(1, 2, 8);
    set.add(q1);
    set.add(q2);
    set.add(new Queen(5, 6, 8));
    QueenIterator columns = set.columns();
    assertTrue(columns.hasNext());
    assertTrue(7 == columns.next()); // last added was 6
  }

  @Test
  public void columnIteratorResetTest() {
    QueenSet set = new QueenSet(8);
    Queen q1 = new Queen(0, 0, 8);
    Queen q2 = new Queen(1, 2, 8);
    set.add(q1);
    set.add(q2);
    set.add(new Queen(5, 6, 8));
    QueenIterator columns = set.columns();
    assertTrue(columns.hasNext());
    assertTrue(7 == columns.next()); // last added was 6
    columns.reset(false);
    assertTrue(columns.hasNext());
    assertTrue(1 == columns.next()); // move to begging, 0 reserved so 1 expected
    columns.reset(true);
    assertTrue(columns.hasNext());
    assertTrue(7 == columns.next()); // last added was 6
  }

  /*
   * row tests...
   */
}
