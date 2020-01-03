// VERSION OCTOBER 25, 2018

public class IntList {
    
    // The space allocated for the arrays that store the nodes. Each node will
    // consist of an int key and a successor, stored in two arrays to minimize
    // memory overhead.
    private static int SPACE;
    // The keys stored in the individual nodes.
    private static int[] key;
    // The successors of the individual nodes. Value 0 denotes no successor.
    // Nonnegative value means that the node has been allocated, whereas a
    // negative value means that the node is part of the free list, with
    // the negation of the value being the successor node index in the free list.
    private static int[] next;
    
    // Position of the first free node.
    private static int freeHead;
    
    // Call counts for various methods.
    private static long getKeyCount = 0;
    private static long getNextCount = 0;
    private static long setKeyCount = 0;
    private static long setNextCount = 0;
    
    // Current number of allocated nodes in the list
    private static int allocatedNodeCount = 0;
    
    /** 
     * Returns the current number of allocated nodes.
     * @return The current number of allocated nodes.
     */
    public static int getAllocatedNodeCount() {
        return allocatedNodeCount;
    }    
        
    /**
     * Should be called once in the beginning. Initializes the simulated node space.
     * @param maxn Number of total nodes to allocate space for initially.
     */
    public static void initialize(int maxn) {
        SPACE = maxn;
        key = new int[SPACE];
        next = new int[SPACE];
        freeHead = 1;
        for(int i = 1; i < SPACE - 1; i++) {
            next[i] = -(i+1);
        }
    }
    
    // Verify that the node n really exists and has been allocated for use.
    private static void verifyIndex(int n) {
        if(n < 1 || n >= SPACE || next[n] < 0) {
            throw new IllegalStateException("Node " + n + " is not currently allocated for use.");
        }
    }
    
    /**
     * Returns the key of node {@code n}.
     * @param n The index of node whose key is read.
     * @return The key of node {@code n}.
     */
    public static int getKey(int n) {
        verifyIndex(n);
        getKeyCount++;
        return key[n];
    }
    /**
     * Returns the successor of node {@code n}.
     * @param n The index of node whose successor is read.
     * @return The successor of node {@code n}.
     */
    public static int getNext(int n) {
        verifyIndex(n);
        getNextCount++;
        return next[n];        
    }
    
    /**
     * Assigns a new key to node {@code n}.
     * @param n The index of node whose key is assigned.
     * @return The previous key of node {@code n} before this assignment.
     */
    public static int setKey(int n, int k) {
        verifyIndex(n);
        if(lockValue != 0) {
            throw new IllegalStateException("Trying to modify a key while keys are locked.");
        }
        setKeyCount++;
        int result = key[n];
        key[n] = k;
        return result;
    }
    
    /**
     * Assigns a new successor node to node {@code n}.
     * @param n The index of node whose successor is assigned.
     * @return The previous successor of node {@code n} before this assignment.
     */
    public static int setNext(int n, int m) {
        verifyIndex(n);
        setNextCount++;
        int result = next[n];
        next[n] = m;
        return result;
    }
    
    /**
     * Allocates a new node with the given key.
     * @param k The key for the new node.
     * @return The index of the new allocated node.
     */
    public static int allocate(int k) {
        if(freeHead > 0) {
            int n = freeHead;
            freeHead = -next[freeHead];
            next[n] = 0;
            key[n] = k;
            allocatedNodeCount++;
            return n;
        }
        else {
            throw new IllegalStateException("No more space for nodes available.");
        }
    }
    
    /**
     * Allocates a chain of nodes for the keys in the parameter array.
     * @param keys The array of keys to convert into a linked list.
     * @return The index of the first node of the chain.
     */
    public static int allocate(int[] keys) {
        int prev = 0;
        for(int i = keys.length - 1; i >= 0; i--) {
            int n = allocate(keys[i]);
            setNext(n, prev);
            prev = n;
        }
        return prev;
    }
    
    /**
     * Releases the entire chain of nodes from the starting node. If you want to
     * release just one node, set its successor to 0 before calling this method.
     * @param n The first node of the chain to release.
     * @return The number of nodes that were released.
     */
    public static int release(int n) {
        int count = 0;
        while(n != 0) {
            verifyIndex(n);            
            int m = next[n];
            next[n] = -freeHead;            
            freeHead = n;
            n = m;
            allocatedNodeCount--;
            count++;            
        }
        return count;
    }
    
    // The rest of this class is for demonstration purposes. Read through these methods until
    // you understand what they do, and then use them as models in implementing the methods in
    // the course project two, "Linked Lists from Scratch".
    
    /**
     * Return a string representation of the chain from the given start node.
     * @param n The first node of the chain to release.
     * @return The string representation of chain, with keys listed between square brackets
     * separated by commas and spaces.
     */
    public static String toString(int n) {
        StringBuilder result = new StringBuilder("[");
        boolean first = true;
        while(n != 0) {
            if(!first) { result.append(", ");}
            first = false;
            result.append(IntList.getKey(n));
            n = IntList.getNext(n);
        }
        result.append("]");
        return result.toString();
    }  
    
    /**
     * Removes the first occurrence of the given key in the chain from the given start node.
     * @param n The first node of the chain to perform the key removal in.
     * @param k The key to remove.
     * @return The index of the new first node of the chain. This can be the same as the original
     * first node, if nothing is removed or the removed node is not the first. Removing the only
     * node of the chain produces answer 0 to indicate that the chain is now empty.
     */
    public static int removeFirst(int n, int k) {
        // Empty list must be handled as a special case.
        if(n == 0) { return 0; }
        // Removing the first node is also a special case.
        if(IntList.getKey(n) == k) {
            // Remove the first node from the chain.
            int m = IntList.setNext(n, 0);
            IntList.release(n);
            // The original second node is the new first node of this chain.
            return m;            
        }
        // Otherwise, find the predecessor of the node that we wish to remove.
        int first = n; // Original first node for safekeeping
        int m = IntList.getNext(n); // Successor of the current node
        while(m != 0 && IntList.getKey(m) != k) {
            n = m; // Indices n and m traverse in lockstep, n one step behind m.
            m = IntList.getNext(m);
        }
        // If we found a node to remove, remove it.
        if(m != 0) {
            // The successor of m becomes the successor of n.
            IntList.setNext(n, IntList.setNext(m, 0));
            IntList.release(m);
        }
        // Either way, the original first node of the chain is still the first node.
        return first;
    }
    
    /**
     * Reverse the nodes in the chain from the given start node, using the classic
     * technique of three pointers to three consecutive nodes, moving forward in
     * lockstep and turning the direction of one pointer per round.
     * @param n The start node of the chain to reverse.
     * @return The index of the first node of the reversed chain. 
     */
    public static int reverse(int n) {
        if(n == 0) { return 0; } // Empty list, do nothing.
        int m = IntList.getNext(n);
        if(m == 0) { return n; } // One-node list.
        IntList.setNext(n, 0);        
        int o = IntList.getNext(m);
        while(m != 0) {
            IntList.setNext(m, n); // Flip the middle node next pointer to point to predecessor.
            n = m; m = o; if(o != 0) { o = IntList.getNext(o); } // Lockstep is the best step.       
        }
        return n;
    }
    
    /**
     * For debugging purposes, output the counts of how many times each method has been called.
     */
    public static void printStatistics() {        
        System.out.print("Method call counts: getKey " + getKeyCount);
        System.out.print(", setKey " + setKeyCount);
        System.out.print(", getNext " + getNextCount);
        System.out.println(", setNext " + setNextCount + ".");
    }
    
    // Locking mechanism to enforce that keys cannot be reassigned during sorting, but
    // the sorting algorithm is forced to rearrange the actual nodes. Prevents some
    // solutions that would here be considered unsportsmanlike for this problem.
    private static int lockValue = 0;
    
    public static void lockKeys(int value) {
        if(lockValue == 0) { lockValue = value; }
    }
    
    public static void unlockKeys(int value) {
        if(lockValue == value) { lockValue = 0; }
        else {
            throw new IllegalStateException("Cheater! Trying to unlock keys during sorting!");
        }
    }
    
    public static void main(String[] args) {
        IntList.initialize(100); // Keep it small in this demo...
        int[] data = {17, 42, 99, 16, -5, 107, -3, 92};
        int n = IntList.allocate(data);
        System.out.println("Node count after creating the list: " + IntList.getAllocatedNodeCount());
        System.out.println("Original: " + IntList.toString(n));
        n = IntList.reverse(n);
        System.out.println("Reversed: " + IntList.toString(n));
        n = IntList.removeFirst(n, 99);
        System.out.println("Remove 99: " + IntList.toString(n));
        n = IntList.removeFirst(n, 75);
        System.out.println("Remove 75: " + IntList.toString(n));
        n = IntList.removeFirst(n, 17);
        System.out.println("Remove 17: " + IntList.toString(n));        
        n = IntList.reverse(n);
        System.out.println("Reversed again: " + IntList.toString(n));
        IntList.release(n);
        System.out.println("Node count after releasing everything: " + IntList.getAllocatedNodeCount());
    }
}
