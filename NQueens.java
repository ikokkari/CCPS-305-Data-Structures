public class NQueens {
    
    /**
     * The classic n-queens problem. How many ways can you place {@code n} chess queens
     * on an n*n chessboard so that no two queens are on the same row, column or diagonal?
     * The solution is the backtracking algorithm that by law has to be taught first.
     * @param n The number of queens to place.
     * @return The count of possible ways.
     */
    public static int nQueens(int n) {
        // Top level call of the backtracking algorithm creates the auxiliary arrays
        // that will speed up the decision making at each level of the recursion.
        return nQueens(n, new boolean[n], new boolean[2*n], new boolean[2*n]);
     }    
     
    // How many ways can you place the remaining n queens, with rows etc. already taken? The
    // answer and the running time are exponential with respect to n. For how big value of n
    // can you still wait for the result?
    
    private static int nQueens(int n, boolean[] cols, boolean[] ne, boolean[] se) {
        if(n == 0) { return 1; } // All queens successfully placed.
        // Loop through all the possibilities to place the current queen in row n-1.
        int row = n - 1; // The row in which the current queen will be placed.
        int sum = 0; // Sum of solutions for the current recursion level.
        for(int col = 0; col < cols.length; col++) {
            if(cols[col]) { continue; } // This column was already taken.
            if(ne[cols.length - row + col]) { continue; } // This diagonal was already taken.
            if(se[row + col]) { continue; } // This diagonal was already taken.
            // Place the queen in position (row, col) and update the auxiliary arrays.
            cols[col] = true;
            ne[cols.length - row + col] = true;
            se[row + col] = true;
            // Recursively add up all the possible ways to place the remaining n - 1 queens.        
            sum += nQueens(n - 1, cols, ne, se);     
            // Undo the (row, col) placement of the current queen before backtracking.
            cols[col] = false;
            ne[cols.length - row + col] = false;
            se[row + col] = false;
        }
        return sum;
    }
    
    // Possible ways to speed up the backtracking, left as exercise for the reader:
    // - Forward checking if some choices have left no possible places for queen in some future row.
    // - Dancing links to quickly find out the columns that are available in the current row.
    // - Using symmetries to eliminate duplicate branches.
    
    public static void main(String[] args) {
        for(int n = 1; n < 16; n++) {
            System.out.print("Solving for " + n + " queens...");
            System.out.println(nQueens(n) + " solutions found.");
        }
        System.out.println("And we are all done!");
    }
}
