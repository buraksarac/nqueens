/**
 * 
 */
package org.qunix.nqueens;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.qunix.nqueens.exceptions.ChessBoardSizeOutOfRangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * NQueens generates solutions for classical nqueens problem using brut-force approach via going
 * through each decision tree using DFS. (https://en.wikipedia.org/wiki/Eight_queens_puzzle)
 * 
 * Yet still there are faster solutions like https://leetcode.com/problems/n-queens-ii/solution/ or
 * https://leetcode.com/problems/n-queens/solution/ this class doesnt use recursive calls and has
 * multi thread support , yet still there is still room to improve, max board size is limited in
 * integer range and multi threaded calls currently just duplicates root of tree between each column
 * at first row according to thread count, that each new thread might create sub threads to handle
 * their own next row <br>
 * example usages: <br>
 * <code>
 *  <br>
 *  new NQueens(n).visit();
 *  <br>
 *  new NQueens(n).visit(threadCount);
 *  <br>
 *  new NQueens(n).visit(firstRowQueenFromColumn, firstRowQueenToColumn);
 *  <br>
 * </code>
 * 
 * 
 * @author burak
 *
 */
public class NQueens {

  private static final Logger logger = LoggerFactory.getLogger(NQueens.class);

  /**
   * Max board size, 19 is 621012754 int overflow^^
   */
  private static final int MAX_BOARD_SIZE = 18;

  private int boardSize;
  private int n;

  /**
   * Default constructor
   * 
   * @param n dimension
   */
  public NQueens(int n) {
    this.n = n;
    this.boardSize = n * n;

    // check board size if its in range
    if (n <= 0 || n > MAX_BOARD_SIZE) {
      logger.error(
          "Given dimension {} is not in allowed range (greater than zero or less than 18)", n);
      throw new ChessBoardSizeOutOfRangeException(n);
    }

    logger.debug("NQueens instantiated with dimension {} and board size {}", n, boardSize);
  }

  public int getCount() {
    return CountCache.RESULT_CACHE[n][1];
  }

  /**
   * Splits first row calculations equally between given number of threads, after execution complete
   * it will return {@link List} of array of {@link Queen} that each array will hold n amount of
   * {@link Queen}
   * 
   * 
   * @param numberOfThreads threads to create
   * @return {@link List} of {@link Queen}
   */
  public List<Queen[]> visit(int numberOfThreads) {

    logger.debug("Visit request for threads {}", numberOfThreads);
    if (numberOfThreads <= 0) {
      logger.error("Given thread count not in rande {}", numberOfThreads);
      throw new IllegalArgumentException("Invalid number of threads provided");
    } else if (numberOfThreads == 1) {
      logger.debug("Just one thread asked escalating to visit() method");
      return this.visit();
    }
    // check if threds are reasonable
    logger.debug("System available processors: ", Runtime.getRuntime().availableProcessors());
    numberOfThreads =
        Math.min(n, Math.min(Runtime.getRuntime().availableProcessors(), numberOfThreads));
    logger.debug("Number of threads being used: {}", numberOfThreads);
    // create executor, last thread will be main thread
    ExecutorService service = Executors.newFixedThreadPool(numberOfThreads - 1);

    List<Queen[]> results = new ArrayList<Queen[]>();

    try (AutoCloseable closeable = service::shutdown) {
      // callables instance for the invoker
      List<Callable<List<Queen[]>>> callables = new ArrayList<>();

      logger.debug("Creating thread callables");
      // share first row dimension between threads
      for (int t = 0; t < numberOfThreads; t++) {
        int qMin = t * n / numberOfThreads;
        int qMax = (t + 1) * n / numberOfThreads;
        logger.info("Thread {} will start from ROW:0,COL:{} to ROW:0,COL:{}", t, qMin, qMax);
        if (t == numberOfThreads - 1) {
          results.addAll(visit(qMin, qMax));
        } else {
          callables.add(() -> visit(qMin, qMax));
        }

      }

      List<Future<List<Queen[]>>> futures = service.invokeAll(callables);
      for (Future<List<Queen[]>> f : futures) {
        results.addAll(f.get());
      }

    } catch (Exception e) {
      logger.error("An unexpected error occured during execution", e);
      throw new RuntimeException(e);
    }

    return results;
  }

  /**
   * 
   * Calculates all possible positions by placing first row queen at each column of first row
   * 
   * @return {@link List} of {@link Queen}
   */
  public List<Queen[]> visit() {
    return visit(0, n);
  }


  /**
   * 
   * Calculates all possible positions by placing first row queen to fromColumn then iterates until
   * queen reaches to toColumn
   * 
   * @param fromColumn inclusive
   * @param toColumn exclusive
   * @return {@link List} of {@link Queen}
   */
  public List<Queen[]> visit(int fromColumn, int toColumn) {
    
    Instant now = Instant.now();

    logger.debug("Visit start fromColumn: {} and toColumn:{} ", fromColumn, toColumn);
    // result instance
    List<Queen[]> result = new ArrayList<>();
    // Small set instance backed by an array
    QueenSet queens = new QueenSet(n);

    // first queen instance
    queens.add(new Queen(0, fromColumn, n));
    if(n == 1) {
      result.add(queens.getQueens());
      return result;
    }

    // I have created iterators for col and row so
    // in that way I would be able to skip next unavailable col|row
    // after that I didnt have to check if each queen is in a invalid row or column
    // only i needed to check if its in a diagonal placement
    QueenIterator columns = queens.columns();
    QueenIterator rows = queens.rows();

    // temp variables
    int row = 0, col = 0;
    Queen queen = new Queen();
    // iterate until first queen reaches target column on first row
    rows: while (queens.getFirstQueenColumn() < toColumn) {

      // we scan a row but couldnt place anything
      // that means we can not reach target N so rollback last one
      // OR current row already placed and there is no more row left
      // so thats need rollback too
      if (!queens.isRowReserved(row) || !rows.hasNext()) {
        // remove last queen and move column pointer to
        // next to it so we will not visit again
        queens.rollback();
        columns.reset(true);
        rows.reset(true); // maybe this not needed:)
      } else {
        // normal iteration, row already increased, mov column to first
        columns.reset(false);
      }
      row = rows.next();

      while (columns.hasNext()) {
        col = columns.next();
        // set queen instance for this row & col
        queen.setValues(row, col, n);
        // check if this queen will threat existing ones
        if (queens.isThreatening(queen)) {
          continue;
        }
        // all good add queen
        queens.add(queen);
        queen = new Queen();
        // we achieve target, add to the results
        if (queens.size() == n) {
          result.add(queens.getQueens());
        }
        // there is no point for more iteration,
        // new queen added
        // move to next row
        continue rows;
      }

    } ;
    Instant end = Instant.now();
    logger.info("Thread {} took {} ms", Thread.currentThread().getId(), Duration.between(now, end).toMillis());
    return result;
  }

}
