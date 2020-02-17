/**
 * 
 */
package org.qunix.nqueens;

import java.io.Serializable;
import java.util.Arrays;
import org.qunix.bitset.BitSet;

/**
 * @author burak
 *
 */
public final class QueenSet implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private BitSet rows;
  private BitSet columns;
  private BitSet hills;
  private BitSet dales;
  private int size = 0;
  private Queen[] queens;
  private int currentColumn;
  private int currentRow;
  private int dimension;
  private int firstQueenColumn;

  /**
   * 
   */
  QueenSet(int capacity, int dimension) {
    this.queens = new Queen[capacity];
    this.rows = new BitSet(dimension);
    this.columns = new BitSet(dimension);
    this.hills = new BitSet((dimension << 2) - 1);
    this.dales = new BitSet((dimension << 2) - 1);
    this.rows.resize(dimension);
    this.columns.resize(dimension);
    this.hills.resize((dimension << 2) - 1);
    this.dales.resize((dimension << 2) - 1);
    this.dimension = dimension;
  }



  void add(Queen q) {
    this.rows.on(this.currentRow = q.getRow());
    this.columns.on(this.currentColumn = q.getColumn());
    this.hills.on(q.getHill());
    this.dales.on(q.getDale());
    this.queens[size++] = q;
  }


  public int size() {
    return this.size;
  }


  public Queen[] getQueens() {
    return queens;

  }

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

  public boolean isRowReserved(int row) {
    return this.rows.get(row);
  }

  public boolean isColumnReserved(int column) {
    return this.columns.get(column);
  }

  public boolean isThreatening(Queen q) {
    return this.hills.get(q.getHill()) || this.dales.get(q.getDale()); // row&col check done by iterator
  }

  @Override
  public String toString() {
    return Arrays.toString(queens);
  }

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
