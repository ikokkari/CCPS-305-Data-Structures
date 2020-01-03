import java.util.*;
import java.util.zip.*;

// VERSION OF AUGUST 31, 2018

public class IntervalUnionTest {

    public static int test(int seed, int n, int m, long expected, boolean verbose) {
        Random rng = new Random(seed);
        CRC32 check = new CRC32();
        long startTime = System.currentTimeMillis();
        IntervalUnion[] is = new IntervalUnion[n + m];
        int step = (int)(Math.sqrt(n + m)) + 1;
        for(int i = 0; i < n; i++) {
            int start = rng.nextInt(n + step);
            int end = start + rng.nextInt(step);
            is[i] = IntervalUnion.create(start, end);
            if(verbose) System.out.printf("%3d: %s\n", i, is[i]);
        }
        for(int i = n; i < n + m; i++) {
            int j1, j2;
            do {
                j1 = rng.nextInt(i);
                j2 = rng.nextInt(i);
            } while(j1 == j2);
            boolean op = (i < n + m / 2) || rng.nextBoolean();
            is[i] = op ? is[j1].union(is[j2]) : is[j1].intersection(is[j2]);
            if(verbose) {
                System.out.printf("%3d: %s of %d, %d is %s\n", i, (op?"union":"intersection"),j1, j2, is[i]);          
            }
            check.update(is[i].toString().getBytes());
            int v = rng.nextInt(10000);
            if(is[i].contains(v)) { check.update(v); } else { check.update(v + i); }
            check.update(is[i].getPieceCount());
        }
        HashSet<IntervalUnion> table = new HashSet<>();
        for(int i = 0; i < n+m; i++) {
            table.add(is[i]);
        }
        for(int i = 0; i < n+m; i++) {
            if(!table.contains(is[i])) { return 99999998; }
        }
        check.update(table.size());
        long endTime = System.currentTimeMillis();
        if(expected > 0 && check.getValue() != expected) {
            System.out.println("Checksum is " + check.getValue());
            return 99999999;
        }
        return (int)(endTime - startTime);
    }
    
    public static void main(String[] args) {
        int seed = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        int m = Integer.parseInt(args[2]);
        long expected = 0;
        if(args.length > 3) {
            expected = Long.parseLong(args[3]);
        }        
        int totalTime = test(seed, n, m, expected, expected == 0);            
        System.out.println(totalTime + " " + IntervalUnion.getRyersonID() + " "
        + IntervalUnion.getAuthorName());
    }
}
