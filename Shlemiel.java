import java.util.*; // for Stack and HashSet

/**
 * Many array problems are easy to solve in {@code O(n^2)} time with the "Shlemiel"
 * approach, but with some clever thinking, they can be turned into {@code O(n)} time
 * single pass algorithms.
 * @author Ilkka Kokkarinen
 */
public class Shlemiel {
    
    /**
     * Given an integer array {@code a}, create the accumulation array {@code b}
     * whose each element equals the sum of elements in a up to that position.
     * @param a The original array to accumulate.
     * @return The accumulation array of the original array.
     */
    public static int[] accumulateShlemiel(int[] a) {
        int[] b = new int[a.length];
        for(int i = 0; i < b.length; i++) {
            int sum = 0;
            for(int j = 0; j <= i; j++) {
                sum += a[i];
            }
            b[i] = sum;
        }
        return b;
    }
    
    /**
     * Given an integer array {@code a}, create the accumulation array {@code b}
     * whose each element equals the sum of elements in a up to that position.
     * @param a The original array to accumulate.
     * @return The accumulation array of the original array.
     */
    public static int[] accumulate(int[] a) {
        int[] b = new int[a.length];
        b[0] = a[0];
        for(int i = 1; i < a.length; i++) {
            b[i] = b[i-1] + a[i];
        }
        return b;
    }
    
    /**
     * Given an integer array {@code a}, find and return the length of its
     * longest contiguous strictly ascending subarray.
     * @param a The array to search the ascending subarray in.
     * @return The length of the longest ascending subarray.
     */
    public static int longestAscendingSubarrayShlemiel(int[] a) {
        if(a.length == 0) { return 0; }
        int max = 1;        
        for(int i = 0; i < a.length - 1; i++) {
            int j = i + 1;
            while(j < a.length && a[j] > a[j-i]) { j++; }
            if(j > max) { max = j; }
        }
        return max;
    }
    
    /**
     * Given an integer array {@code a}, find and return the length of its
     * longest contiguous strictly ascending subarray.
     * @param a The array to search the ascending subarray in.
     * @return The length of the longest ascending subarray.
     */
    public static int longestAscendingSubarray(int[] a) {
        if(a.length == 0) { return 0; }
        int curr = 1; // The length of the current ascension.
        int max = 1; // The longest ascension that we have seen so far.
        for(int i = 1; i < a.length; i++) { // Start from second element
            if(a[i] > a[i-1]) {
                curr++;
                if(curr > max) { max = curr; }
            }
            else {
                curr = 1;
            }
        }
        return max;
    }
        
    /**
     * Given an integer array {@code a} guaranteed to be sorted in ascending order,
     * determine whether it contains two elements whose sum equals goal value {@code x}.
     * @param a The array to search the two elements in.
     * @return Whether two such elements exist.
     */
    public static boolean twoSummingElementsShlemiel(int[] a, int x) {
        for(int i = 0; i < a.length; i++) {
            for(int j = i + 1; j < a.length && a[i] + a[j] <= x; i++) {
                if(a[i] + a[j] == x) { return true; }
            }
        }
        return false;
    }
    
    /**
     * Given an integer array {@code a} guaranteed to be sorted in ascending order,
     * determine whether it contains two elements whose sum equals goal value {@code x}.
     * @param a The array to search the two elements in.
     * @return Whether two such elements exist.
     */
    public static boolean twoSummingElements(int[] a, int x) {
        int i = 0, j = a.length - 1;
        // Invariant: If such a pair exists in array, one exists between i and j, inclusive.
        while(i < j) {
            int sum = a[i] + a[j];
            if(sum == x) { return true; }
            else if(sum < x) { i++; } // smallest element is too small to work, advance left
            else { j--; } // largest element too large to work, advance right            
        }
        return false;        
    }
    
    // Puzzle for thought: suppose the previous task was to find three summing elements
    // that add up to x. Shlemiel would solve this in O(n^3) time. Can you solve this in
    // O(n^2) time? How about O(n) time?
    
    /**
     * Compute the n:th Fibonacci number using exponential recursion, therefore
     * computing the same subproblems over and over again.
     * @param n The position of the Fibonacci number to compute.
     * @return The n:th Fibonacci number.
     */
    public static int fibonacciRec(int n) {
        if(n < 2) { return 1; }
        else { return fibonacciRec(n-1) + fibonacciRec(n-2); }
    }
    
    /**
     * Compute the n:th Fibonacci number using a dynamic programming loop that
     * fills the array of subproblem solutions in an order that guarantees that
     * whenever the loop comes to given element, the elements that the recursion 
     * needs have already been computed, so recursive calls can be turned into
     * efficient O(1) array lookups.
     * @param n The position of the Fibonacci number to compute.
     * @return The n:th Fibonacci number.
     */
    public static int fibonacciDyn(int n) {
        int[] fib = new int[n+1];
        fib[0] = fib[1] = 1;
        for(int i = 2; i <= n; i++) {
            fib[i] = fib[i-1] + fib[i-2];
        }
        return fib[n];
    }
    
    /**
     *  Eliminate the heading, trailing and duplicated whitespaces from given text.
     *  For example, "  hello   there   world  " should become "hello there world".
     *  This method demonstrates the use of state machine paradigm to keep the logic
     *  uniform through the entire string or array, instead of having to deal with
     *  the first or the last element as a special case outside the loop.
     *  @param text The string from which to eliminate such whitespace.
     *  @return The text without heading, trailing or duplicated whitespaces.
     */
    public static String eliminateExtraSpaces(String text) {
        StringBuilder result = new StringBuilder(); // To make append O(1)
        boolean prevSpace = true; // Was previous character a space?
        boolean missSpace = false; // Is the result missing one space?
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean nowSpace = Character.isWhitespace(c);
            if(nowSpace) { 
                if(!prevSpace) { missSpace = true; }
            }
            else {
                if(missSpace) { result.append(" "); }
                missSpace = false;
                result.append(c);                 
            }
            prevSpace = nowSpace;
        }
        return result.toString();
    }
    
    /**
     * Evaluate the polynomial at the given point x.
     * @param coeff The coefficients of the polynomial.
     * @param x The point in which to evaluate the polynomial.
     * @return The value of the polynomial at point {@code x}.
     */
    public static double evaluatePolynomialShlemiel(double[] coeff, double x) {
        double sum = 0;
        for(int i = 0; i < coeff.length; i++) {
            double term = 0, pow = 1;
            // Evaluate each term from scratch in the linear inner loop.
            for(int j = 0; j < i; j++) { pow = pow * x; }
            sum += coeff[i] * pow;
        }
        return sum;
    }
    
    /**
     * Evaluate the polynomial at the given point x.
     * @param coeff The coefficients of the polynomial.
     * @param x The point in which to evaluate the polynomial.
     * @return The value of the polynomial at point {@code x}.
     */
    public static double evaluatePolynomialLinear(double[] coeff, double x) {
        double sum = 0, pow = 1;
        // Two multiplications and one add per coefficient.
        for(double e: coeff) {
            sum += e * pow; // Add the current power to the result...
            pow = pow * x; // and use it to compute the next power
        }
        return sum;
    }
    
    /**
     * Evaluate the polynomial at the given point x.
     * @param coeff The coefficients of the polynomial.
     * @param x The point in which to evaluate the polynomial.
     * @return The value of the polynomial at point {@code x}.
     */
    public static double evaluatePolynomialHorner(double[] coeff, double x) {
        double sum = coeff[coeff.length - 1];
        // One multiplication and one add per coefficient.
        for(int i = coeff.length - 2; i >= 0; i--) {
            sum = sum * x + coeff[i];
        }
        // This is more numerically stable when using floating point.
        return sum;
    }
    
    // Some fuzz testing to ensure that previous three methods work the same.
    public static boolean testPolynomial(int n, int seed) {
        Random rng = new Random(seed);
        double EPS = 0.00000001;
        for(int i = 0; i < n; i++) {
            double[] coeff = new double[rng.nextInt(7) + 1];
            for(int j = 0; j < coeff.length; j++) {
                coeff[j] = rng.nextInt(9) - 4;                
            }
            double x = rng.nextInt(11) - 5;
            double r1 = evaluatePolynomialShlemiel(coeff, x);
            double r2 = evaluatePolynomialLinear(coeff, x);
            double r3 = evaluatePolynomialHorner(coeff, x);
            // When using floating point numbers, results can be slightly different
            // since these algorithms perform different additions and multiplications.
            // So we compare results within small tolerance, not for strict equality.
            if(Math.abs(r1 - r2) > EPS || Math.abs(r1 - r3) > EPS) { return false; }            
        }
        return true;
    }
    
    /**
     * Checks whether there exists some index {@code i} into the array {@code a} so that
     * the sum of elements in the left subarray from beginning to {@code i} equals the
     * sum of elements in the right subarray from index {@code i + 1} to the end. 
     * @param a The array to determine if such splitting index exists.
     * @return {@code true} if at least one such index exists, {@code false} otherwise.
     */
    public static boolean canBalanceShlemiel(int[] a) {
        for(int i = 0; i < a.length; i++) { // possible splitting points
            int leftSum = 0, rightSum = 0;
            for(int j = 0; j <= i; j++) { leftSum += a[j]; }
            for(int j = i+1; j < a.length; j++) { rightSum += a[j]; }
            if(leftSum == rightSum) { return true; }
        }
        return false;
    }
    
    /**
     * Checks whether there exists some index {@code i} into the array {@code a} so that
     * the sum of elements in the left subarray from beginning to {@code i} equals the
     * sum of elements in the right subarray from index {@code i + 1} to the end. 
     * @param a The array to determine if such splitting index exists.
     * @return {@code true} if at least one such index exists, {@code false} otherwise.
     */
    public static boolean canBalance(int[] a) {
        int leftSum = 0, rightSum = 0;
        for(int e: a) { rightSum += e; }
        for(int i = 0; i < a.length; i++) {
            leftSum += a[i]; // O(1) update of leftSum and rightSum, instead of O(n)
            rightSum -= a[i];
            if(leftSum == rightSum) { return true; }
        }
        return false;
    }
    
    /**
     * Given an {@code n}-element integer array, determine whether it contains
     * each number from 1 to {@code n} exactly once.
     * @param a The array to check.
     * @param n The largest value to look for.
     * @return {@code true} if each number occurs exactly once, {@code false} otherwise.
     */
    public static boolean containsAllNumbersShlemiel(int[] a, int n) {
        for(int i = 1; i <= n; i++) { // numbers to look for
            boolean found = false;
            for(int e: a) {
                if(e == i) { found = true; break; }
            }
            if(!found) { return false; }
        }
        return true;
    }
    
    /**
     * Given an {@code n}-element integer array, determine whether it contains
     * each number from 1 to {@code n} exactly once.
     * @param a The array to check.
     * @param n The largest value to look for.
     * @return {@code true} if each number occurs exactly once, {@code false} otherwise.
     */
    public static boolean containsAllNumbersSorting(int[] a, int n) {
        // We can do better by sorting the array first.
        Arrays.sort(a); // O(n log n) stage dominates
        for(int i = 0; i < n; i++) { // O(n)
            if(a[i] != i+1) { return false; }
        }
        return true;
    }
    
    /**
     * Given an {@code n}-element integer array, determine whether it contains
     * each number from 1 to {@code n} exactly once.
     * @param a The array to check.
     * @param n The largest value to look for.
     * @return {@code true} if each number occurs exactly once, {@code false} otherwise.
     */
    public static boolean containsAllNumbersLinear(int[] a, int n) {
        // For a linear time solution, trade memory for time: use a boolean
        // array to remember which values we have already seen. (Also, allocate
        // one more element than needed to eliminate need for integer subtract.)
        boolean[] alreadySeen = new boolean[n + 1];
        for(int e : a) {
            if(e < 1 || e > n || alreadySeen[e]) { return false; }
            alreadySeen[e] = true;
        }
        return true;
    }        
    
    /**
     * Remove all strings from the given arraylist of strings whose length is shorter than {@code len}.
     * @param a The arraylist of strings to process.
     * @param len The threshold length for a string to remain in the list.
     */
    public static void removeShortStringsShlemiel(ArrayList<String> a, int len) {
        int i = 0;
        while(i < a.size()) {
            if(a.get(i).length() < len) { a.remove(i); } // remove from middle is O(n)
            else { i++; } // advance only if skipping the current element
        }
        // Total worst case running time is O(n) * O(n) = O(n ^ 2)
    }
    
    /**
     * Remove all strings from the given arraylist of strings whose length is shorter than {@code len}.
     * @param a The arraylist of strings to process.
     * @param len The threshold length for a string to remain in the list.
     */
    public static void removeShortStrings(ArrayList<String> a, int len) {
        ArrayList<String> tmp = new ArrayList<String>();
        for(String e : a) { // total of O(n) over n rounds
            if(e.length() >= len) { tmp.add(e); } // O(1) amortized add to end
        }
        a.clear(); // O(n) (references are set to null to enable garbage collection)
        a.addAll(tmp); // O(n)
        // Total worst case running time is 3 * O(n) = O(n)
    }
    
    public static long addRowMajorOrder(int[][] matrix, int n) {
        long sum = 0;
        for(int row = 0; row < n; row++) {
            for(int col = 0; col < n; col++) {
                sum += matrix[row][col];                
            }
        }
        return sum;
    }
    
    public static long addColumnMajorOrder(int[][] matrix, int n) {
        long sum = 0;
        for(int col = 0; col < n; col++) {
            for(int row = 0; row < n; row++) {            
                sum += matrix[row][col];                
            }
        }
        return sum;
    }
    
    public static void testMatrixAddOrders(int n) {
        Random rng = new Random(12345);
        int[][] matrix = new int[n][n];
        for(int row = 0; row < n; row++) {
            for(int col = 0; col < n; col++) {
                matrix[row][col] = rng.nextInt(100);
            }
        }
        long startTime = System.currentTimeMillis();
        long r1 = addRowMajorOrder(matrix, n);
        long endTime = System.currentTimeMillis();
        System.out.println("Adding in row major order took " + (endTime - startTime) + " ms.");
        startTime = System.currentTimeMillis();
        long r2 = addColumnMajorOrder(matrix, n);
        endTime = System.currentTimeMillis();
        System.out.println("Adding in column major order took " + (endTime - startTime) + " ms.");
        assert r1 == r2;
    }
    
    // Instructor's trusty old desktop Mac gave the following results for n = 10000:
    // Adding in row major order took 108 ms.
    // Adding in column major order took 2261 ms.

    /**
     * Given a string that consists of characters "()[]{}" only, determine whether
     * it is a properly parenthesized so that pairs of matching parentheses are
     * properly nested and balanced.
     * @param s The string to check for proper parentheses.
     * @return {@code true} if the string is properly parenthesized, {@code false} otherwise.
     */
    public static boolean isProperlyParenthesizedShlemiel(String s) {
        // Shlemiel's solution to check whether a string is properly parenthesized. Find
        // a pair of innermost parentheses, remove that, and check that the rest of the
        // string is properly parenthesized.
        while(s.length() > 0) {
            boolean changed = false;
            for(int i = 0; i < s.length()-1; i++) {
                String piece = s.substring(i, i + 2);
                if(piece.equals("()") || piece.equals("{}") || piece.equals("[]")) {
                    s = s.substring(0, i) + s.substring(i+2);
                    changed = true;
                    break;
                }
            }
            if(!changed) { return false; }
        }
        return true;
    }
    
    /**
     * Given a string that consists of characters "()[]{}" only, determine whether
     * it is a properly parenthesized so that pairs of matching parentheses are
     * properly nested and balanced.
     * @param s The string to check for proper parentheses.
     * @return {@code true} if the string is properly parenthesized, {@code false} otherwise.
     */
    public static boolean isProperlyParenthesized(String s) {
        // Stack-based solution to verify that string is properly parenthesized.
        Stack<Character> stack = new Stack<Character>();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '(' || c == '[' || c == '{') { stack.push(c); }
            else {
                if(stack.isEmpty()) { return false; } // More right parens than left ones
                char c2 = stack.pop();
                if(c == ')' && c2 != '(') { return false; }
                if(c == ']' && c2 != '[') { return false; }
                if(c == '}' && c2 != '{') { return false; }
            }
        }
        return stack.isEmpty();
    }
}
