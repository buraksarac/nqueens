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
      "Invalid size requested! %s  should be greater than 0 and less than 18";; 

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ChessBoardSizeOutOfRangeException(int n) {
    super(String.format(message, n));
  }

}
