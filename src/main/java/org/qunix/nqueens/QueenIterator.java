/**
 * 
 */
package org.qunix.nqueens;

import java.util.Iterator;

/**
 * @author burak
 *
 */
public interface QueenIterator extends Iterator<Integer> {

  public void reset(boolean lastKnown);
  
}
