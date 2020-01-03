import java.io.*;
import java.util.*;

// VERSION OCTOBER 30, 2018

public class WordPackingTest {

    private static List<String> allWords;
    
    private static boolean readAllWords(String filename, boolean verbose) {
        if(allWords != null) { return false; }
        try {
            allWords = new ArrayList<String>();
            Scanner s = new Scanner(new File(filename));
            while(s.hasNextLine()) {
                allWords.add(s.nextLine());
            }
            s.close();
            if(verbose) { System.out.println("Read " + allWords.size() + " words from <" + filename + ">."); }
            return false;
        }
        catch(Exception e) {
            System.out.println("Unable to read file: " + e);
            return true;
        }
    }
    
    private static int totalTime = 0;
    
    public static int test(int seed, int n, boolean verbose) {
        if(readAllWords("sgb-words.txt", verbose)) { return 9999; }
        HashSet<String> wordsH = new HashSet<>();
        ArrayList<String> wordsA = new ArrayList<>();
        Random rng = new Random(seed);
        int[][] charCount = new int[5][26];
        outer:
        while(wordsA.size() < n) {
            String word = allWords.get(rng.nextInt(allWords.size()));
            if(wordsH.contains(word)) { continue; }
            for(int i = 0; i < 5; i++) {
                if(charCount[i][word.charAt(i) - 'a'] > n / 10) { continue outer; }
            }
            for(int i = 0; i < 5; i++) {
                charCount[i][word.charAt(i) - 'a']++;
            }
            wordsH.add(word);
            wordsA.add(word);
        }
        for(int i = 1; i < n; i++) {
            int j = rng.nextInt(i);
            String tmp = wordsA.get(i);
            wordsA.set(i, wordsA.get(j));
            wordsA.set(j, tmp);
        }
        if(verbose) { System.out.println("\nSeed " + seed + ": " + wordsA); }
        long startTime = System.currentTimeMillis();
        List<List<String>> result = WordPacking.wordPack(new ArrayList<String>(wordsA));
        long endTime = System.currentTimeMillis();
        totalTime += (int)(endTime - startTime);
        // Verify that no two words in same bin contain the same letter in same position.
        for(List<String> bin: result) {
            for(int i = 0; i < bin.size(); i++) {
                String w1 = bin.get(i);
                for(int j = i+1; j < bin.size(); j++) {
                    String w2 = bin.get(j);
                    for(int k = 0; k < 5; k++) {
                        if(w1.charAt(k) == w2.charAt(k)) { return 999997; }
                    }
                }
            }
        }
        // Verify that each word is exactly in one bin.
        for(List<String> bin: result) {
            for(String word: bin) {
                if(wordsH.contains(word)) {
                    wordsH.remove(word);
                }
                else { 
                    if(verbose) { System.out.println("ERROR with word: " + word); }
                    return 999999;
                }
            }
        }
        if(wordsH.size() > 0) {
            if(verbose) { System.out.println("ERROR: " + wordsH.size() + " word(s) missing from result."); }
            return 999998; 
        }
        if(verbose) {
            System.out.println("Solution found in " + (endTime - startTime) + " ms.");
            for(int i = 0; i < result.size(); i++) {
                System.out.println("Bin " + (i + 1) + ": " + result.get(i));
            }
        }
        return result.size();
    }
    
    public static void main(String[] args) {
        int seed = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        int rounds = Integer.parseInt(args[2]);
        int totalScore = 0;
        for(int s = seed; s < seed + rounds; s++) {
            try {
                int score = test(s, n, args.length > 3 && args[3].toLowerCase().startsWith("t"));
                totalScore += score;
            } catch(Exception e) { 
                System.out.println("ERROR: " + e);
                totalScore += 10000000;
            }
        }
        System.out.println(totalScore + " " + totalTime + " " + WordPacking.getRyersonID() + " "
        + WordPacking.getAuthorName());
    }
}
