/**
 * 
 */
package org.qunix.nqueens;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import org.qunix.bitset.BitSet;
import org.qunix.nqueens.exceptions.ChessBoardSizeOutOfRangeException;

/**
 * 
 * Small set impl. to store Queens first I start with black/white arrays to use buckets but end up
 * with this one
 * 
 * @author burak
 *
 */
final class QueenSet implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private BitSet rows; // reserved rows for existing queens
  private BitSet columns; // reserved cols for existing queens
  private BitSet hills; // reserved hills for existing queens
  private BitSet dales; // reserved dales for existing queens
  private int size = 0;
  private Queen[] queens;
  private int currentColumn; // used for rollback to last known position
  private int currentRow; // used for rollback to last known position
  private int dimension;
  private int firstQueenColumn;

  /**
   * 
   * Default constructor
   * 
   * @param capacity boar
   * @param dimension
   */
  QueenSet(int dimension) {
    if (dimension <= 0) {
      throw new ChessBoardSizeOutOfRangeException(dimension);
    }
    this.queens = new Queen[dimension];
    this.rows = new BitSet(dimension);
    this.columns = new BitSet(dimension);
    this.hills = new BitSet((dimension << 2) - 1);
    this.dales = new BitSet((dimension << 2) - 1);
    // set all sets to false (from 0 to its size)
    this.rows.resize(dimension);
    this.columns.resize(dimension);
    this.hills.resize((dimension << 2) - 1);
    this.dales.resize((dimension << 2) - 1);
    this.dimension = dimension;
  }



  /**
   * Adds a new queen into the list
   * 
   * @param q queen to add
   */
  void add(Queen q) {
    this.rows.on(this.currentRow = q.getRow());
    this.columns.on(this.currentColumn = q.getColumn());
    this.hills.on(q.getHill());
    this.dales.on(q.getDale());
    this.queens[size++] = q;
  }


  /**
   * 
   * @return size of list
   */
  public int size() {
    return this.size;
  }


  /**
   * @return current queens
   */
  public Queen[] getQueens() {
    Queen[] queenCopy = new Queen[dimension];
    System.arraycopy(queens, 0, queenCopy, 0, size);;
    return queenCopy;

  }

  /**
   * removes last queen and updates last known position to next column of removed queen. If next
   * column is out of range then removes also one before
   */
  public void rollback() {
    do {
      int index = --this.size;
      Queen queen = queens[index];
      queens[index] = null;
      rows.off(queen.getRow());
      columns.off(queen.getColumn());
      hills.off(queen.getHill());
      dales.off(queen.getDale());
      this.currentColumn = queen.getColumn() + 1;
      this.firstQueenColumn = index == 0 ? this.currentColumn : this.firstQueenColumn;
      this.currentRow = queen.getRow();
    } while (this.currentColumn == this.dimension && this.size != 0);

  }

  /**
   * @return the firstQueenColumn
   */
  public int getFirstQueenColumn() {
    return firstQueenColumn;
  }

  /**
   * @param row to check
   * @return true if already reserved
   */
  public boolean isRowReserved(int row) {
    return this.rows.get(row);
  }

  /**
   * @param column to check
   * @return true if already reserved
   */
  public boolean isColumnReserved(int column) {
    return this.columns.get(column);
  }

  /**
   * @param q queen to check
   * @return true if given queen doesnt threat others
   */
  public boolean isThreatening(Queen q) {
    return this.hills.get(q.getHill()) || this.dales.get(q.getDale()); // row&col check done by
                                                                       // iterators
  }

  @Override
  public String toString() {
    return Arrays.toString(queens);
  }

  /**
   * @return {@link Iterator} for columns
   */
  public QueenIterator columns() {
    return new QueenIterator() {

      int position = currentColumn;

      @Override
      public boolean hasNext() {
        while (position < dimension) {
          if (!columns.get(position)) {
            return true;
          }
          position++;
        }

        return false;
      }

      @Override
      public Integer next() {
        return position++;
      }

      public void reset(boolean lastKnown) {
        this.position = lastKnown ? currentColumn : 0;
      }

    };
  }

  /**
   * @return iterator for rows
   */
  public QueenIterator rows() {
    return new QueenIterator() {

      int position = currentRow;

      @Override
      public boolean hasNext() {
        while (position < dimension) {
          if (!rows.get(position)) {
            return true;
          }
          position++;
        }

        return false;
      }

      @Override
      public Integer next() {
        return position++;
      }

      public void reset(boolean lastKnown) {
        this.position = lastKnown ? currentRow : 0;
      }

    };
  }
}
