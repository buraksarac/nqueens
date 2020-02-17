/**
 * 
 */
package org.qunix.nqueens.exceptions;

/**
 * @author burak
 *
 */
public class ChessBoardSizeOutOfRangeException extends RuntimeException {

  private static final String message =
      "Invalid size requested! %s²  should be greater than 0 and less than 1073741824";; //extended ascii in comments ^^

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ChessBoardSizeOutOfRangeException(int n) {
    super(String.format(message, n));
  }

}
