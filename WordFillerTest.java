import java.util.*;
import java.io.*;

// VERSION NOVEMBER 11, 2018

public class WordFillerTest {
  
    // The set of all words recognized by this puzzle.
    private static Set<String> allWords;        
    // The occurrence counts for all possible letters.   
    private static int[] charCount = new int[26];
    
    private static boolean readAllWords(String filename, boolean verbose) {
        if(allWords != null) { return false; }
        try {
            allWords = new HashSet<String>();
            Scanner s = new Scanner(new File(filename));
            while(s.hasNextLine()) {
                String word = s.nextLine();
                allWords.add(word);
                for(int i = 0; i < 5; i++) {
                    charCount[word.charAt(i) - 'a']++;
                }
            }
            s.close();
            if(verbose) { 
                System.out.println("Read " + allWords.size() + " words from <" + filename + ">.");                 
            }
            WordFiller.processWordList(new ArrayList<String>(allWords));
            return false;
        }
        catch(Exception e) {
            System.out.println("Unable to read file: " + e);
            return true;
        }
    }
    
    private static String generatePattern(int n, double p1, double p2, Random rng) {
        StringBuilder result = new StringBuilder();
        boolean prev = false;
        int stars = 0;
        for(int i = 0; i < n; i++) {
            if(stars > 4 || (prev && rng.nextDouble() < p1) || (!prev && rng.nextDouble() < p2)  ) {
                int v = rng.nextInt(allWords.size() * 5);
                int idx = 0;
                while(charCount[idx] <= v) { v -= charCount[idx]; idx++; }
                result.append((char)('a' + idx));                
                prev = true;
                stars = 0;
            }
            else {
                result.append('*');
                prev = false;
                stars++;
             }            
        }
        return result.toString();
    }
    
    // Total time taken by the measured method calls.
    private static int totalTime = 0;        
    
    // Try out the word filling method with a pattern that consists of only
    // asterisks, except that an individual character can be something else
    // with the probability prob.
    public static int testAllAsterisks(int n, double prob) {
        if(readAllWords("sgb-words.txt", true)) { return 0; }
        Random rng = new Random();
        String pattern = "";
        boolean prevWasForced = false;
        for(int i = 0; i < n; i++) { 
            if(prevWasForced || rng.nextDouble() > prob) { 
                pattern += "*"; prevWasForced = false;
            }
            else { 
                int idx = 25 - (int)(Math.sqrt(rng.nextInt(26 * 26)));
                pattern += "etaoinshrdlucmfgypwbvkxjqz".charAt(idx); 
                prevWasForced = true;
            }
        }
        System.out.println("Pattern length is " + n + ".");
        System.out.println("Pattern is " + pattern + ".");
        String result = pattern;
        try {
            result = WordFiller.fillPattern(pattern);
        }
        catch(Exception e) {
            System.out.println("ERROR: " + e);                
        }
        System.out.println("Result is  " + result + ".");
        int score = 0;
        for(int i = 0; i <= n - 5; i++) {            
            String word = result.substring(i, i + 5);
            if(allWords.contains(word)) { 
                System.out.print(word + " ");
                if(++score % 10 == 0) { System.out.println(""); }
            }
        } 
        if(score % 10 != 0) { System.out.println(""); }
        System.out.println("Total score is " + score + ".");
        return score;
    }
    
    // Run one test case of length n for the given random seed.
    public static int test(int seed, int n, boolean verbose) {
        if(readAllWords("sgb-words.txt", verbose)) { return 0; }
        Random rng = new Random(seed);
        String pattern = generatePattern(n, 0.3, 0.7, rng);
        if(verbose) { System.out.printf("\nSeed %10d: [%s]\n", seed, pattern); }
        long startTime = System.currentTimeMillis();
        String result;
        try {
            result = WordFiller.fillPattern(pattern);
        } catch(Exception e) {
            System.out.println("ERROR: " + e); return 0;
        }
        long endTime = System.currentTimeMillis();
        totalTime += (int)(endTime - startTime);
        if(verbose) { 
            System.out.println("Result returned in " + (endTime - startTime) + " ms.");
            System.out.println("Returned result: [" + result + "]"); 
        }
        int score = 0;
        if(result.length() != pattern.length()) { 
            if(verbose) { System.out.println("RESULT WRONG LENGTH!"); }
            return 0;
        }
        for(int i = 0; i < result.length(); i++) {
            if(pattern.charAt(i) != '*' && result.charAt(i) != pattern.charAt(i)) { 
                if(verbose) { System.out.println("RESULT INCONSISTENT WITH PATTERN!"); }
                return 0;             
            }
        }
        for(int i = 0; i <= n - 5; i++) {            
            String word = result.substring(i, i + 5);
            if(allWords.contains(word)) { 
                if(verbose) { System.out.print(word + " "); }
                score++;
            }
        }
        if(verbose) { System.out.println(""); }
        return score;
    }
    
    public static void main(String[] args) {
        int seed = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        int rounds = Integer.parseInt(args[2]);
        int total = 0;
        for(int s = seed; s < seed + rounds; s++) {
            try {
                int score = test(s, n, args.length > 3 && args[3].toLowerCase().startsWith("t"));
                total += score;
            } catch(Exception e) { System.out.println(e); }
        }
        System.out.println(total + " " + totalTime + " " + WordFiller.getRyersonID() + " "
        + WordFiller.getAuthorName());
    }
}
