/**
 * 
 */
package org.qunix.nqueens;

/**
 * 
 * DTO for Queens to hold row, column and precalculated hill and dale
 * 
 * @author burak
 *
 */
public final class Queen {

  protected int row;
  protected int column;
  protected int hill;
  protected int dale;



  /**
   * I gave package access, otherwise {@link NQueens} had to instantiate new one each iteration
   * since its immutable
   */
  Queen() {}



  /**
   * @param row row
   * @param column col
   * @param n dimension to calculate hill and dale
   */
  Queen(int row, int column, int n) {
    super();
    setValues(row, column, n);
  }



  /**
   * @return the row
   */
  public int getRow() {
    return row;
  }


  /**
   * @return the column
   */
  public int getColumn() {
    return column;
  }

  /**
   * @return the hill
   */
  public int getHill() {
    return hill;
  }

  /**
   * @return the dale
   */
  public int getDale() {
    return dale;
  }

  /**
   * @param row row
   * @param column col
   * @param n dimension to calculate hill and dale
   */
  void setValues(int row, int column, int n) {
    this.row = row;
    this.column = column;
    this.hill = row - column + (n << 1);
    this.dale = row + column;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + column;
    result = prime * result + row;
    return result;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Queen other = (Queen) obj;
    if (column != other.column)
      return false;
    if (row != other.row)
      return false;
    return true;
  }



  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Queen [row=").append(row).append(", column=").append(column).append(", hill=")
        .append(hill).append(", dale=").append(dale).append("]");
    return builder.toString();
  }



}
