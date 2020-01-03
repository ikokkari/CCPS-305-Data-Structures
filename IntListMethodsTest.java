// VERSION OCTOBER 29, 2018

import java.util.*;

public class IntListMethodsTest {

    // Compute a simple XOR checksum of the indices of the nodes hanging from n.
    private static long checksum(int n) {
        int check = 0;
        while(n != 0) {
            check = check ^ n;
            n = IntList.getNext(n);
        }
        return check;
    }
    
    // Run the test and return the measured total running time.
    public static int test(int seed, int rounds, int size, boolean verbose) {
        int[] data = new int[size];
        Random rng = new Random(seed);
        IntList.initialize(size + 5);
        int totalTime = 0;
        long startTime, endTime;
        for(int i = 0; i < rounds; i++) {
            for(int j = 0; j < size; j++) {
                data[j] = rng.nextInt(2 * size + 1) - size;
            }
            int lock = rng.nextInt();
            int k = rng.nextInt(6) + 2;
            int n = IntList.allocate(data);
            int total = 0;
            for(int e: data) { total += (e % k == 0) ? 0: 1; }
            if(verbose) { System.out.println("Original: " + IntList.toString(n)); }
            startTime = System.currentTimeMillis();
            n = IntListMethods.removeIfDivisible(n, k);
            endTime = System.currentTimeMillis();
            totalTime += (int)(endTime - startTime);
            if(verbose) { System.out.println("After removeIf(" + k + "): " + IntList.toString(n)); }
            int m = n, idx = 0, total2 = 0;
            while(m != 0) {
                total2++;
                while(data[idx] % k == 0) { idx++; }
                if(IntList.getKey(m) != data[idx]) {
                    System.out.println("ERROR: removeIf " + IntList.getKey(m) + " " + data[idx] );
                    return 9999999;
                }
                idx++;
                m = IntList.getNext(m);
            }
            if(total != total2) { 
                System.out.println("ERROR: removeIf removed wrong number of elements.");
                return 9999999;
            }
            Arrays.sort(data);
            long check1 = checksum(n);            
            IntList.lockKeys(lock);
            startTime = System.currentTimeMillis();
            n = IntListMethods.sort(n);
            endTime = System.currentTimeMillis();
            totalTime += (int)(endTime - startTime);
            IntList.unlockKeys(lock);
            long check2 = checksum(n);
            if(check1 != check2) {
                System.out.println("Your sort method does not rearrange the nodes in place.");
                return 9999998;
            }
            total2 = 0; m = n;
            while(m != 0) { total2++; m = IntList.getNext(m); }
            if(total != total2) {
                System.out.println("ERROR: sorting the list changed its length.");
                return 999999;
            }            
            if(verbose) { System.out.println("After sort: " + IntList.toString(n)); }
            m = n; idx = 0;
            while(m != 0) {
                while(data[idx] % k == 0) { idx++; }
                if(IntList.getKey(m) != data[idx]) {
                    System.out.println("ERROR: sort");
                    return 9999997;
                }
                idx++;
                m = IntList.getNext(m);
            }
            IntList.release(n);
        }       
        if(IntList.getAllocatedNodeCount() > 0) {
            System.out.println("ERROR: Memory leak of " + IntList.getAllocatedNodeCount() + " nodes.");
            return 9999996;
        }
        if(verbose) { IntList.printStatistics(); }
        return totalTime;
    }
    
    public static void main(String[] args) {
        int seed = Integer.parseInt(args[0]);
        int rounds = Integer.parseInt(args[1]);
        int size = Integer.parseInt(args[2]);  
        int total;
        try {
            total = test(seed, rounds, size, args.length > 3 && args[3].toLowerCase().startsWith("t"));
        }
        catch(IllegalStateException e) {
            System.out.println("Exception caught: " + e);
            total = 9999995;
        }
        System.out.println(total + " " + IntListMethods.getRyersonID() + " "
        + IntListMethods.getAuthorName());
    }
}
