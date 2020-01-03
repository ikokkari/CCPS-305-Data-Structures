Skip to content
Search or jump to…

Pull requests
Issues
Marketplace
Explore
 
@ikokkari 
1
00ikokkari/MiscJava
 Code Issues 0 Pull requests 0 Actions Projects 0 Wiki Security Insights Settings
MiscJava/DynProg.java
@ikokkari ikokkari Create DynProg.java
4461c22 6 days ago
699 lines (653 sloc)  31.5 KB
  
import java.util.*;
import java.math.BigInteger;
import java.util.function.*;

// Dynamic programming methods for CPS 305 Data Structures, Ryerson University
// Created by Ilkka Kokkarinen, ilkka.kokkarinen@gmail.com

public class DynProg { 
    
    // Fibonacci numbers, the simplest possible starting point to demonstrate this mechanism.
    
    private static int fibRecCallCount = 0;
    // Recursive version, exponential time due to repeated subproblems.
    public static int fibonacci(int n) {
        fibRecCallCount++;
        if(n < 2) { return 1; }
        else { return fibonacci(n-1) + fibonacci(n-2); }
    }
    
    private static int fibMemCallCount = 0;
    private static BigInteger[] fibTable = new BigInteger[2];
    static {
        fibTable[0] = fibTable[1] = BigInteger.ONE;
    }
    // Memoized version, taming the exponential branching back to linear.
    public static BigInteger fibonacciMem(int n) {
        fibMemCallCount++;
        // Expand the memoization array if needed.
        if(n >= fibTable.length) {
            fibTable = Arrays.copyOf(fibTable, Math.max(n + 1, 2 * fibTable.length));
        }
        // If the result has not been memoized yet, compute it and memoize.
        if(fibTable[n] == null) {
            fibTable[n] = fibonacciMem(n-1).add(fibonacciMem(n-2));            
        }
        // The memoized solution returned in O(1) time.
        return fibTable[n];
    }
    
    // Dynamic programming version filling the table with a for-loop.
    public static BigInteger fibonacciDyn(int n) {
        // Expand the memoization array if needed.
        if(n >= fibTable.length) {
            fibTable = Arrays.copyOf(fibTable, Math.max(n + 1, 2 * fibTable.length));
        }
        // Might as well use the previously memoized result, if it is available.
        if(fibTable[n] != null) { return fibTable[n]; }
        // Loop through array and replace recursive calls with array lookups.
        for(int i = 2; i <= n; i++) {
            fibTable[i] = fibTable[i-1].add(fibTable[i-2]);
        }
        return fibTable[n];
    }
    
    // Rod cutting, exponential time recursive solution.
    public static int rodCutRec(int n, int[] value) {
        // If there is nothing to cut, there is nothing to gain.
        if(n < 1) { return 0; }
        int max = 0;
        // Loop through all possibilities for the first piece.
        for(int i = 1; i <= n; i++) {
            max = Math.max(max, value[i] + rodCutRec(n - i, value));
        }
        return max;
    }
    
    // Rod cutting with dynamic programming, also showing how to reconstruct solution.
    public static int[] rodCutDyn(int n, int[] value) {
        int[] best = new int[n + 1];
        int[] whereCut = new int[n + 1];
        // Step one: fill in the base cases of the recursion.
        best[1] = value[1]; // Redundant, but we are just making a point here.
        // Step two: fill in the rest of the array with a for-loop.
        for(int j = 2; j <= n; j++) {            
            int max = value[j]; whereCut[j] = j;
            // Loop through all possibilities for the first piece.
            for(int i = 1; i < j; i++) {
                int v = value[i] + best[j - i];
                if(v > max) { max = v; whereCut[j] = i; }
            }
            best[j] = max;
        }
        // The best value is now in element best[n]. Reconstruct the actual solution:
        ArrayList<Integer> result = new ArrayList<>();
        while(n > 0) {            
            result.add(whereCut[n]);
            n -= whereCut[n];            
        }
        return fromArrayList(result);
    }
    
    // Longest common subsequence, exponential time recursive solution.
    public static int lcsRec(String s1, String s2) {
        if(s1.length() == 0 || s2.length() == 0) return 0;
        if(s1.charAt(0) == s2.charAt(0)) {
            return 1 + lcsRec(s1.substring(1), s2.substring(1));
        }
        // Drop the first character of string s1, recurse
        int m1 = lcsRec(s1.substring(1), s2);
        // Drop the first character of string s2, recurse
        int m2 = lcsRec(s1, s2.substring(1));
        return m1 > m2 ? m1: m2;
    }
    
    // Dynamic programming solution, also showing how to reconstruct the actual solution,
    // instead of merely the optimal solution value, from the auxiliary table of directions.
    public static String lcsDyn(String s1, String s2) {
        int[][] table = new int[s1.length()+1][s2.length()+1];
        int[][] direction = new int[s1.length()+1][s2.length()+1];
        // Base cases with either i or j is 0 are already 0.
        // Fill in the rest of the table.
        for(int i = 1; i <= s1.length(); i++) {
            for(int j = 1; j <= s2.length(); j++) {
                // If the last characters of strings are different, drop one of them.
                if(s1.charAt(i-1) != s2.charAt(j-1)) {
                    // What happens if you drop the last character of first string.
                    int m1 = table[i-1][j];
                    // What happens if you drop the last character of second string.
                    int m2 = table[i][j-1];
                    // Whichever is better, is the solution to that subproblem.
                    table[i][j] = m1 > m2 ? m1: m2; 
                    direction[i][j] = m1 > m2 ? -1 : 1;
                }
                // If the last characters of strings are equal, solution is easy.
                else {
                    table[i][j] = table[i-1][j-1] + 1;
                }
            }
        }
        // Reconstruct the result from the table.
        StringBuilder result = new StringBuilder();
        // Start tracking solution from the original top level call.
        int i = s1.length(), j = s2.length();
        while(i > 0 && j > 0) {            
            if(direction[i][j] == 0) { result.append(s1.charAt(i-1)); i--; j--; }
            else if(direction[i][j] == -1) { i--; }
            else {j--; }
        }
        result.reverse(); // Solution was constructed in reverse order...
        return result.toString();                   
    }

    // Coin changing problem, exponential time recursive solution.
    // https://en.wikipedia.org/wiki/Change-making_problem
    public static int coinChangeRec(int[] coins, int k, int sum) {
        if(sum == 0) { return 0; } // If nothing to change, need zero coins.
        if(k == 0) { return sum; } // If only pennies remain, that is the solution.
        // If largest available coin is too big, use smaller coins.
        if(coins[k] > sum) { return coinChangeRec(coins, k-1, sum); }
        else {
            // Best solution if we take the largest available coin.
            int v1 = 1 + coinChangeRec(coins, k, sum-coins[k]);
            // Best solution if we don't take the largest available coin.
            int v2 = coinChangeRec(coins, k-1, sum);
            // Whichever is smaller, return that.
            return v1 < v2 ? v1: v2;
        }        
    }
    
    // Dynamic programming solution, again demonstrating how to reconstruct solution.
    public static int[] coinChangeDyn(int[] coins, int sum) {
        int k = coins.length;
        int[][] table = new int[k][sum+1];
        boolean[][] takeIt = new boolean[k][sum+1];
        // Step one: fill in the base cases of recursion into the table.
        for(int i = 0; i <= sum; i++) { table[0][i] = i; takeIt[0][i] = true; }
        // Step two: fill in the rest of the table.
        for(int i = 0; i <= sum; i++) {
            for(int j = 1; j < k; j++) {
                // If largest available coin is too big, use smaller coins.
                if(coins[j] > i) { table[j][i] = table[j-1][i]; }
                else {
                    // Best solution if we take the largest available coin.
                    int v1 = 1 + table[j][i-coins[j]];
                    // Best solution if we don't take the largest available coin.
                    int v2 = table[j-1][i];
                    // Whichever is smaller, use that.
                    if(v1 < v2) { takeIt[j][i] = true; table[j][i] = v1; }
                    else { table[j][i] = v2; }
                }
            }
        }
        // Reconstruct the solution from the table.
        ArrayList<Integer> result = new ArrayList<>();
        // Start reconstructing the solution from top level call.
        k--;
        while(sum > 0) {
            if(takeIt[k][sum]) { result.add(coins[k]); sum -= coins[k]; }
            else { k--; }
        }
        return fromArrayList(result);
    }
        
    // Two player coin game, exponential time recursive solution.
    public static int coinGameRec(int[] coins, int start, int end) {
        // If only one position remains, that coin is the value of the game.
        if(start == end) return coins[start];
        // Result for taking the coin at position start.
        int vs = coins[start] - coinGameRec(coins, start+1, end);
        // Result for taking the coin at position end.
        int ve = coins[end] - coinGameRec(coins, start, end-1);
        // Whichever is better, is the solution.
        return vs > ve? vs: ve;
    }

    // Two player coin game, dynamic programming version.
    public static int[] coinGameDyn(int[] coins) {
        int n = coins.length;
        int[][] coinGameValues = new int[n][n];
        int[][] move = new int[n][n];
        // Step one: fill in the base cases.
        for(int start = 0; start < n; start++) {
            coinGameValues[start][start] = coins[start];
            move[start][start] = start;
        }
        // Step two: fill in the rest of the table. To fill a 2D array in the
        // upper diagonal order, outer loop goes through differences of i and j.
        for(int width = 1; width < n; width++) {
            for(int start = 0; start + width < n; start++) {
                int end = start + width;
                int vs = coins[start] - coinGameValues[start+1][end];
                int ve = coins[end] - coinGameValues[start][end-1];
                if(vs > ve) { 
                    coinGameValues[start][end] = vs; move[start][end] = start; 
                }
                else {
                    coinGameValues[start][end] = ve; move[start][end] = end;
                }
            }
        }
        // Construct the principal variation (optimal move sequence) of this game.
        ArrayList<Integer> result = new ArrayList<>();
        int start = 0, end = n - 1;
        while(start <= end) {
            result.add(move[start][end]);
            if(move[start][end] == start) { start++; }
            else { end--; }            
        }
        return fromArrayList(result);
    }
    
    // For the rest of the problems, we shall only show the dynamic programming version.
    
    // Given a list of activities with hard start and end time, and profit gained from each
    // activity, choose a subset of mutually compatible activities that maximize total profit.
    // For simplicity, this method assumes that activities are sorted by increasing start time.
    public static int[] activitySelection(int[] start, int[] end, int[] profit) {
        int n = start.length;
        int[] table = new int[n];
        boolean[] take = new boolean[n];
        // Step one: fill in the base cases.
        table[n - 1] = profit[n - 1];
        // Step two: fill the table in reverse order.
        for(int i = n - 2; i >= 0; i--) {
            int j = i + 1; // Find the earliest activity that starts after this one ends.
            while(j < n && start[j] < end[i]) { j++; }
            int takeIt = profit[i] + (j < n ? table[j]: 0);
            int leaveIt = table[i + 1];
            if(takeIt >= leaveIt) { table[i] = takeIt; take[i] = true; }
            else { table[i] = leaveIt; take[i] = false; }
        }
        // Reconstruct the solution from the tables.
        ArrayList<Integer> result = new ArrayList<>();
        int i = 0;
        while(i < n) {
            if(take[i]) { 
                result.add(i);
                int j = i + 1;
                while(j < n && start[j] < end[i]) { j++; }
                i = j;
            }
            else { i++; }
        }
        return fromArrayList(result);
    }
    
    // If every activity has the same profit, this problem is the canonical example of greedy
    // algorithm that generates the solution in linear time. Sort activities in order of their
    // increasing finishing time, and then always take in the earlier finishing activity.
    
    // 0-1 knapsack optimization problem, with the subset sum as important special case.
    // https://en.wikipedia.org/wiki/Knapsack_problem    
    public static int[] knapsack(int capacity, int[] weight, int[] price) {
        int n = weight.length;
        int[][] table = new int[n + 1][capacity + 1];
        boolean[][] takeIt = new boolean[n + 1][capacity + 1];
        // All base cases are zero, so they are already filled in.
        // Step two: fill the rest of the table in a loop.
        for(int i = 1; i <= n; i++) {
            for(int c = 1; c <= capacity; c++) {
                // Option one: take the current item.
                int take = c >= weight[i-1] ? price[i-1] + table[i-1][c - weight[i-1]] : 0;
                // Option two: leave the current item.
                int leave = table[i-1][c];
                // Choose whichever way is better.
                if(take > leave) { takeIt[i-1][c] = true; }
                table[i-1][c] = Math.max(take, leave);                
            }
        }
        // The optimal solution is now in table[n][capacity]. Reconstruct solution...
        ArrayList<Integer> result = new ArrayList<>();
        while(n > 0 && capacity > 0) {
            if(takeIt[n-1][capacity]) { result.add(n-1); capacity -= weight[n-1]; }            
            n--;
        }
        return fromArrayList(result);
    }
        
    // The longest palindromic subsequence problem.    
    public static String longestPalindromicSubsequence(String s) {
        int n = s.length();
        int[][] table = new int[n][n];
        // Step one: fill in the base cases.
        for(int i = 0; i < n; i++) { table[i][i] = 1; }
        // Step two: fill in the rest of the array. This is again the upper diagonal order.
        for(int width = 1; width < n; width++) {
            for(int i = 0; i + width < n; i++) {
                int j = i + width;
                if(s.charAt(i) == s.charAt(j)) {
                    table[i][j] = 1 + table[i+1][j-1];
                }
                else {
                    table[i][j] = Math.max(table[i][j-1], table[i+1][j]);
                }
            }
        }
        // Reconstruct the solution from the subproblem solutions in the table. Here we
        // have not constructed a waypoint table but recompute the directions from table.
        StringBuilder result = new StringBuilder();
        int i = 0, j = n - 1;
        while(i < j) {
            // If first and last characters of substring are equal, take that character.
            if(s.charAt(i) == s.charAt(j)) {
                result.append(s.charAt(i)); i++; j--;
            }
            else { // Otherwise, drop whichever end character works out better.
                if(table[i][j-1] > table[i+1][j]) { j--; } else { i++; }                
            }            
        }
        String first = result.toString();
        result.reverse();
        String second = result.toString();
        return first + s.charAt(i) + second;
    }
    
    // Given a paragraph of text as list of individual words, break those words into lines
    // whose length is at most maxLine characters to minimize the sum of badness values of
    // of individual lines, as given by the cost function. Add the potential of hyphenating
    // words to this and you get Donald Knuth's algorithm to justify lines in TeX.
    public static List<String> splitIntoLines(String[] words, int maxLine, IntUnaryOperator cost) {
        int n = words.length;
        // Best total cost for text that starts at word i.
        int[] table = new int[n+1];
        // If current line starts at i, next line starts at nextLine[i].
        int[] nextLine = new int[n];
        // Step one: fill in the base cases, this time from the right.
        int idx = n - 1;
        int len = words[idx].length();
        nextLine[idx] = n;
        while(idx > 0 && len + 1 + words[idx - 1].length() <= maxLine) {
            nextLine[--idx] = n;
            len += 1 + words[idx].length();
        }
        // Step two: fill in the rest of the table, this time from right to left.
        for(int i = idx - 1; i >= 0; i--) {            
            len = words[i].length();
            table[i] = cost.applyAsInt(len);
            int j = i + 1; nextLine[i] = j;
            while(j < n && len + 1 + words[j].length() <= maxLine) {
                len += 1 + words[j++].length();
                int c = cost.applyAsInt(len);
                if(c + table[j] < table[i]) {
                    table[i] = c + table[j]; nextLine[i] = j;
                }
            }                        
        }
        // Reconstruct the solution from the nextLine table.
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        while(i < n) {
            String line = "";
            for(int j = i; j < nextLine[i]; j++) {
                if(j > i) { line += " "; }
                line += words[j];                
            }
            result.add(line);
            i = nextLine[i];            
        }
        return result;
    }
    
    // Helper method to compute distance between point (x1, y1) and (x2, y2).
    private static double dist(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2, dy = y1 - y2;
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    // Shortest bitonic tour. Your instructor has fond memories of this one. Assumes that the
    // towns of the tour are sorted in ascending y-order and that there are no y-duplicates.
    // https://en.wikipedia.org/wiki/Bitonic_tour
    public static double shortestBitonicTour(int[] x, int[] y) {
        int n = x.length;
        double[][] table = new double[n][n];
        // Step one: fill in the base cases.
        for(int i = 0; i < n; i++) {
            table[i][n-1] = dist(x[i], y[i], x[n-1], y[n-1]);
        }
        // Step two: fill in the rest of the array using a loop.
        for(int j = n - 2; j >= 0; j--) {
            for(int i = 0; i <= j; i++) {
                // What we get if we connect town i to town j+1.
                double fromI = dist(x[i], y[i], x[j+1], y[j+1]) + table[j][j+1];
                // What we get if we connect town j to town j+1.
                double fromJ = dist(x[j], y[j], x[j+1], y[j+1]) + table[i][j+1];
                table[i][j] = Math.max(fromI, fromJ);
            }            
        }
        return table[0][0];        
    }
    
    // Minimum ink convex polygon triangulation. This could be easily modified for other cost
    // functions such as making the angles as wide as possible, or minimizing the area
    // difference between smallest and largest triangles. This method assumes that the corner
    // points of a convex polygon are in arrays x and y.
    public static List<String> minimumInkTriangulation(int[] x, int[] y, boolean verbose) {   
        int n = x.length;
        double[][] table = new double[n][n];
        int[][] waypoint = new int[n][n];
        // Step one: fill in the base cases.
        for(int i = 0; i < n - 1; i++) {
            table[i][i+1] = dist(x[i],y[i],x[i+1],y[i+1]);
        }
        // Step two: fill the rest of the array with loop. Again, the upper diagonal order.
        for(int width = 2; width < n; width++) {
            for(int i = 0; i + width < n; i++) {
                int j = i + width;
                table[i][j] = Double.POSITIVE_INFINITY;
                for(int k = i + 1; k < j; k++) {
                    double tmp = table[i][k] + table[k][j] + dist(x[i],y[i],x[j],y[j]);
                    if(tmp < table[i][j]) {
                        table[i][j] = tmp; waypoint[i][j] = k;
                    }
                }
            }
        }
        if(verbose) { System.out.printf("Optimal cost is %.3f.\n", table[0][n-1]); }
        return reconstructTriangulation(0, n - 1, waypoint);
    }
    
    private static List<String> reconstructTriangulation(int i, int j, int[][] waypoint) {
        if(j - i < 2) { return Collections.emptyList(); }
        List<String> left = reconstructTriangulation(i, waypoint[i][j], waypoint);
        List<String> right = reconstructTriangulation(waypoint[i][j], j, waypoint);
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(left);
        result.add("(" + i + ", " + waypoint[i][j] + ", " + j + ")");
        result.addAll(right);
        return result;
    }
    
    // String partitioning. Illustrates the general principle of breaking problems that allow
    // arbitrary division into simple decisions of "find the best splitting place and recurse".
    public static List<String> bestPartition(String text, Function<String, Integer> cf) {
        int n = text.length();
        int[][] table = new int[n][n];
        // Maintain an auxiliary array to remember the best splitting positions.
        int[][] split = new int[n][n];
        // Step one: fill in the base cases.
        for(int i = 0; i < n; i++) { table[i][i] = 1; split[i][i] = -1; }
        // Step two: fill in the rest of the array with loop. Again, the upper diagonal order.
        for(int width = 1; width < n; width++) {
            for(int i = 0; i + width < n; i++) {
                int j = i + width;
                table[i][j] = cf.apply(text.substring(i, j+1));
                split[i][j] = -1;
                for(int k = i; k < j; k++) {
                    int v = table[i][k] + table[k+1][j];
                    if(v < table[i][j]) {
                        split[i][j] = k;
                        table[i][j] = v;
                    } 
                }
            }
        }
        // Reconstruct the solution from the table.
        return reconstruct(text, 0, n - 1, split);
    }
    
    private static List<String> reconstruct(String text, int i, int j, int[][] split) {
        int k = split[i][j];
        if(k == -1) { return Arrays.asList(text.substring(i, j+1)); }        
        List<String> left = reconstruct(text, i, k, split);
        List<String> right = reconstruct(text, k + 1, j, split);
        ArrayList<String> result = new ArrayList<>();
        result.addAll(left);
        result.addAll(right);
        return result;
    }
    
    // To try out bestPartition, a function object to check if a string is a palindrome.
    private static Function<String, Integer> isPalindrome = text -> {
        int i = 0, j = text.length() - 1;
        while(i < j) {
            if(text.charAt(i++) != text.charAt(j--)) { return 1000000; }
        }
        return 1;        
    };
    
    private static Set<String> words = new HashSet<String>();
    static {
        try {
            Scanner s = new Scanner(new java.io.File("words_alpha.txt"));
            while(s.hasNextLine()) {
                String word = s.nextLine();
                boolean isGood = true;
                for(int i = 0; i < word.length(); i++) {
                    char c = word.charAt(i);
                    if(c < 'a' || c > 'z') { isGood = false; break; }
                }
                if(isGood) { words.add(word); }
            } 
            s.close();
            System.out.println("Read " + words.size() + " words from words.txt.");
        }
        catch(Exception e) {
            System.out.println("Unable to read the file words.txt: " + e);
            words = null;
        }
    }
    
    // Another function to test whether given string is a word. 
    private static Function<String, Integer> isWord = word -> {
        if(words == null) { return word.length(); }
        int score = word.length() * word.length();
        if(words.contains(word)) { return -score; }
        return score;        
    };
    
    public static int[] viterbi(double[][] p, char[] emits, String obs, int start) {
        int n = emits.length;
        int m = obs.length();
        double[][] table = new double[n][m];
        int[][] waypoint = new int[n][m];
        // Compute path probabilities of length j, based on those of length j - 1. 
        for(int j = 0; j < m; j++) {
            char c = obs.charAt(m - j - 1);
            for(int i = 0; i < n; i++) {
                if(emits[i] == c) {
                    for(int k = 0; k < n; k++) {
                        double pp = p[i][k] * (j > 0 ? table[k][j-1]: 1.0);
                        if(pp > table[i][j]) { table[i][j] = pp; waypoint[i][j] = k; }
                    }                    
                }
            }
        }
        // Reconstruct the solution from waypoints.
        int[] result = new int[m];
        result[0] = start;
        for(int j = 1; j < m; j++) { 
            result[j] = waypoint[result[j-1]][m - j];
        }
        return result;
    }
    
    // Helper method for some earlier methods.
    private static int[] fromArrayList(ArrayList<Integer> a) {
        int[] b = new int[a.size()];
        for(int i = 0; i < b.length; i++) { b[i] = a.get(i); }
        return b;
    }
    
    // Utility method for the demonstration in the main method.
    private static void shift(int[] a) {
        int first = a[0];
        for(int i = 1; i < a.length; i++) {
            a[i-1] = a[i];
        }
        a[a.length-1] = first;
    }
    
    public static void main(String[] args) {
        
        System.out.println("Fibonacci(10) equals " + fibonacci(10) + ".");
        System.out.println("Using memoization, Fibonacci(100) equals " + fibonacciMem(100) + ".");
        System.out.println("Using dynamic programming, Fibonacci(200) equals " + fibonacciDyn(200) + ".");
        System.out.println("Recursive call count is " + fibRecCallCount + ".");
        System.out.println("Memoized call count is " + fibMemCallCount + ".");
        
        // From https://www.geeksforgeeks.org/cutting-a-rod-dp-13/
        int[] prices = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        int rodLen = 8;
        System.out.println("\nCutting rod of " + rodLen + " with prices " + Arrays.toString(prices) + ".");
        System.out.println("Recursive best value: " + rodCutRec(rodLen, prices));
        System.out.println("Dynamic split: " + Arrays.toString(rodCutDyn(rodLen, prices)));
        
        int[] coins = {1, 4, 7, 13, 28, 52, 91, 365 };
        int amount = 500;
        System.out.println("\nChanging " + amount + " coins for " + Arrays.toString(coins) + ":");
        System.out.println("Recursive total coins: " + coinChangeRec(coins, coins.length-1, amount));
        System.out.println("Dynamic split: " + Arrays.toString(coinChangeDyn(coins, amount)));

        String s1 = "data structures", s2 = "algorithms";
        System.out.println("\nCalculating longest common subsequence of '" + s1 + "' and '" + s2 + "':");
        System.out.println("Recursive subsequence length: " + lcsRec(s1, s2));
        System.out.println("Dynamic split: " + lcsDyn(s1, s2));
        
        int[] coins2 = {6, 9, 2, 5, 4, 3, 7, 6, 9, 2, 1};
        System.out.println("\nPlaying the coin game for " + Arrays.toString(coins2) + ":");
        System.out.println("Recursive game value: " + coinGameRec(coins2, 0, coins2.length-1));        
        System.out.println("Dynamic principal variation: " + Arrays.toString(coinGameDyn(coins2)));
        
        int[] start = {1, 2, 3, 6};
        int[] end = {2, 100, 5, 19};
        int[] profit = {50, 200, 20, 100};
        System.out.print("\nActivity selection problem solution: ");
        int[] result = activitySelection(start, end, profit);
        int totalProfit = 0;
        for(int i: result) {
            totalProfit += profit[i];
            System.out.print("(#" + i + " for " + profit[i] + ") ");            
        }
        System.out.println("\nTotal profit is " + totalProfit + ".");
        
        int[] weights = { 10, 5, 11, 12, 3, 11, 4 };
        int[] prices2 = { 4, 3, 5, 8, 2, 9, 6 };
        System.out.println("\nKnapsack with weights " + Arrays.toString(weights) +
        ", prices " + Arrays.toString(prices2) + ", capacity 25:");
        int[] solution = knapsack(25, weights, prices2);
        int totalWeight = 0, totalPrice = 0;
        for(int i = 0; i < solution.length; i++) {
            System.out.println("Taking item " + solution[i] + " of weight " + weights[solution[i]]
            + " and price " + prices2[solution[i]] + ".");
            totalWeight += weights[solution[i]];
            totalPrice += prices2[solution[i]];
        }
        System.out.println("Total weight is " + totalWeight + " for a total price of " + totalPrice + ".");
        
        String[] lorem = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
        + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
        + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris "
        + "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in "
        + "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla "
        + "pariatur. Excepteur sint occaecat cupidatat non proident, sunt in "
        + "culpa qui officia deserunt mollit anim id est laborum.").split(" ");
        int[] lens = {33, 55, 78};
        for(int len: lens) {
            System.out.println("\nLorem ipsum justified to " + len + " characters:");
            List<String> lines = splitIntoLines(lorem, len, x -> x <= len ? (len - x) * (len - x): 1000000);
            for(String line: lines) { 
                System.out.print(line);
                for(int p = 0; p < len - line.length(); p++) { System.out.print(" "); }
                System.out.println("|");
            }
        }
        
        int[] xs = {4, 7, 3, 8, 7, 2, 6, 4, 6, 1, 7, 5, 4};
        int[] ys = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        System.out.printf("\nShortest bitonic tour length is %.3f.\n", shortestBitonicTour(xs, ys));
        
        System.out.println("\nComputing the minimum ink triangulation: ");
        int[] xp = {0, 5, 6, 4, 2, -1};
        int[] yp = {0, 0, 4, 6, 3, 1};
        // Sanity check to ensure that the answer does not change when points are rotated.
        // (As an aside, this is a good way to sanity test all geometric algorithms.)
        for(int i = 0; i < xp.length; i++) {
            System.out.println(minimumInkTriangulation(xp, yp, true));
            shift(xp); shift(yp);            
        }
        
        String lps = "ryerson university computer science";
        System.out.println("\nFinding longest palindromic subsequence of '" + lps + "'.");
        System.out.println("It is '" + longestPalindromicSubsequence(lps) + "'.");
        
        String bp = "ilkkamarkuskokkarinen";
        System.out.println("\nBest partition of '" + bp + "' to palindromes:");
        System.out.println(bestPartition(bp, isPalindrome));
        if(words != null) {
            String bp2 = "nowisthetimeforallgoodmentocometotheaidoftheircountry";
            System.out.println("Best partition of '" + bp2 + "' to words:");
            System.out.println(bestPartition(bp2, isWord));
        }
        
        // Transition probabilities of the Hidden Markov model.
        double[][] p = { 
            {0, 0.5, 0.5, 0},
            {0.25, 0, 0.25, 0.5},
            {0.5, 0.25, 0, 0.25},
            {0.25, 0.25, 0.25, 0.25} 
        };
        // The characters emitted by the HMM in each state.
        char[] emits = { 'a', 'b', 'a', 'b' };
        String[] obs = { "abbbb", "aaaaa", "abaabaab", "abbbbbabb" };
        System.out.println("\nLast, let us compute some maximum probability paths with Viterbi.");
        for(String ob: obs) {
            System.out.print("Maximum probability Viterbi path for " + ob + ": ");
            System.out.println(Arrays.toString(viterbi(p, emits, ob, 0)));
        }
    }
}
© 2020 GitHub, Inc.
Terms
Privacy
Security
Status
Help
Contact GitHub
Pricing
API
Training
Blog
About
