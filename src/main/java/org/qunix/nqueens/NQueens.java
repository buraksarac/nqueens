/**
 * 
 */
package org.qunix.nqueens;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.qunix.nqueens.exceptions.ChessBoardSizeOutOfRangeException;

/**
 * @author burak
 *
 */
public class NQueens {

  private static final int MAX_BOARD_SIZE = 32768;
  private static volatile NQueens instance;

  private int boardSize;
  private int n;

  private NQueens(int n) {
    this.n = n;
    this.boardSize = n * n;

    if (boardSize <= 0 && boardSize > MAX_BOARD_SIZE) {
      throw new ChessBoardSizeOutOfRangeException(boardSize);
    }
  }

  public static final NQueens getInstance(int n) {

    if (instance == null) {
      synchronized (NQueens.class) {
        if (instance == null) {
          instance = new NQueens(n);
        }
      }
    }

    return instance;

  }

  public List<Queen[]> permute(int numberOfThreads) {
    numberOfThreads = Math.min(n, numberOfThreads);
    ExecutorService service =
        Executors.newFixedThreadPool(numberOfThreads == 1 ? numberOfThreads : numberOfThreads - 1);
    List<Queen[]> results = new ArrayList<Queen[]>();
    try (AutoCloseable closeable = service::shutdown) {
      List<Callable<List<Queen[]>>> callables = new ArrayList<>();
      for (int t = 0; t < numberOfThreads; t++) {
        int qMin = t * n / numberOfThreads;
        int qMax = (t + 1) * n / numberOfThreads;
        System.out.println("Min " + qMin + " Max " + qMax);
        if (t == numberOfThreads - 1) {
          results.addAll(permute(qMin, qMax));
        } else {
          callables.add(() -> permute(qMin, qMax));
        }

      }

      List<Future<List<Queen[]>>> futures = service.invokeAll(callables);
      for (Future<List<Queen[]>> f : futures) {
        results.addAll(f.get());
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return results;
  }

  public List<Queen[]> permute() {
    return permute(0, n);
  }


  public List<Queen[]> permute(int fromColumn, int toColumn) {

    List<Queen[]> result = new ArrayList<>();
    QueenSet queens = new QueenSet(boardSize, n);
    Queen queen = new Queen();
    queens.add(new Queen(0, fromColumn, n));
    QueenIterator columns = queens.columns();
    QueenIterator rows = queens.rows();

    int row = 0, col = 0;
    rows: while (queens.getFirstQueenColumn() < toColumn) {
      if (!queens.isRowReserved(row) || !rows.hasNext() ) {
        queens.rollback();
        columns.reset(true);
        rows.reset(true);
      } else {
        columns.reset(false);
      }
      row = rows.next();

      while (columns.hasNext()) {
        col = columns.next();
        queen.setValues(row, col, n);
        if (queens.isThreatening(queen)) {
          continue;
        }
        queens.add(queen);
        if (queens.size() == n) {
          result.add(queens.getQueens());
          break;
        }
        queen = new Queen();
        continue rows;
      }

    } ;

    return result;
  }

}
