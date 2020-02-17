/**
 * 
 */
package org.qunix.nqueens;

import java.util.Iterator;

/**
 * 
 * Interface for {@link Queen} {@link Iterator}
 * 
 * !!It is mandatory to call hasNext before each next call
 * 
 * @author burak
 *
 */
interface QueenIterator extends Iterator<Integer> {

  /**
   * If lastKnown true reset current position to last known queen position otherwise to 0
   * 
   * 
   * @param lastKnown queen position
   */
  public void reset(boolean lastKnown);

}
