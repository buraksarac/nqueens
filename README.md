# nqueens
  NQueens generates solutions for classical nqueens problem using brut-force approach via going
  through each decision tree using DFS. (https://en.wikipedia.org/wiki/Eight_queens_puzzle)

  
  This is my first attempt, its an iterative implementation without recursive calls and has
  multi thread support , also still there are lot of rooms to improve, max board size is limited in
  integer range (int that case max N size is 18, 19 result is 4,968,057,848) and multi threaded calls could be spread deeper into tree, that each new thread might create sub threads to handle
  their own next row <br> . I was able to beat https://leetcode.com/problems/n-queens/solution/ timings with multi thread after 12+ N size but still apple/oranges, that implementation could have mt support too.
 
 
 example usages: <br>

```
   new NQueens(n).visit();

   new NQueens(n).visit(threadCount);

   new NQueens(n).visit(firstRowQueenFromColumn, firstRowQueenToColumn);
```
  faster solutions I found : 
 *  for counting: https://leetcode.com/problems/n-queens-ii/solution/ 
 * Full generation: https://leetcode.com/problems/n-queens/solution/ 
 * Full generation: http://www.ic-net.or.jp/home/takaken/e/queen/ 
 * Full generation (I dont know Japaneese but I guess its fast:)): http://deepgreen.game.coocan.jp/NQueens/nqueen_index.htm
