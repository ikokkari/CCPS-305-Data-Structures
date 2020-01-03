import java.util.*;

// Find a dominating subset of vertices of an undirected graph so that every vertex either
// itself is in this subset, or has at least one neighbour that is in it.

public class DominatingSet {
    
    /**
     * Find and return a dominating set of size {@code k} or fewer in an undirected graph
     * whose vertices are integers 0, ..., {@code n-1}. This function assumes that the
     * graph is connected, and does not attempt to optimize the work by separating the
     * work for independent components.
     * @param neighbours List of neighbours of each vertex.
     * @param k The size of dominating set we are looking for.
     * @param giveup Force stop once {@code giveup} advances have been performed.
     * @param independent Whether the dominating set must also be independent, so that
     * no two nodes that are neighbouring each other are included in the set.
     * @param verbose Whether the method should output status reports.
     * @return List of vertices chosen into the dominating set, or {@code null} if no
     * dominating set of {@code k} elements or fewer exists.
     */
    public static List<Integer> dominatingSet(
        List<List<Integer>> neighbours,
        int k,
        long giveup,
        boolean independent,
        boolean verbose
    ) {
        // Counters for measurement and debugging.
        long forwardCheckingCount = 0, advanceCount = 0;
        
        // How many vertices there are in the graph.
        int n = neighbours.size();
        // Which level each vertex was taken into the solution.
        int[] takenAt = new int[n];
        // Which level each vertex got covered by some vertex in the solution.
        int[] coveredAt = new int[n];
        // Which level each vertex was discarded from the solution. Backtracking
        // should not consider a discarded vertex as a possibility to cover a vertex.
        int[] discardedAt = new int[n];
        // How many chances each vertex has left to become covered. Once this
        // becomes zero for some vertex, we can cut off the search right there.
        int[] chances = new int[n];
        // All uncovered vertices are kept in a dancing list with sentinel value n.
        int[] next = new int[n + 1];
        int[] prev = new int[n + 1];  
        
        // First sort all vertices in ascending order of degrees.
        ArrayList<Integer> perm = new ArrayList<>(n);
        for(int i = 0; i < n; i++) { perm.add(i); }
        Collections.sort(perm, (v1, v2) -> {
            return neighbours.get(v1).size() - neighbours.get(v2).size();
        });
        
        // Initialize the dancing list.              
        int curr = n;
        for(int i = 0; i < n; i++) {
            int c = perm.get(i);
            next[curr] = c; prev[c] = curr; curr = c;
            takenAt[i] = coveredAt[i] = discardedAt[i] = -2;
        }
        next[curr] = n; prev[n] = curr;      
        
        // List of vertices that can cover the given vertex.
        List<List<Integer>> canCover = new ArrayList<>(n);
        for(int i = 0; i < n; i++) {
            List<Integer> nb = new ArrayList<Integer>();
            // Vertex i can be covered by itself or by any of its neighbours.
            nb.add(i);
            for(Integer j: neighbours.get(i)) { nb.add(j); }
            // Sort neighbours of each vertex in descending order of degrees.
            Collections.sort(nb, (v1, v2) -> {
                return neighbours.get(v2).size() - neighbours.get(v1).size();
            });
            chances[i] = nb.size();
            canCover.add(nb);
        }
        
        // Forced selections of singleton and leaf vertices.
        for(int i = 0; i < n; i++) {
            if(coveredAt[i] == -1) { continue; }            
            List<Integer> nb = neighbours.get(i);
            // Any singleton vertex must necessarily be in the dominating set.
            if(nb.size() == 0) {
                next[prev[i]] = next[i]; prev[next[i]] = prev[i];
                next[i] = prev[i] = i;
                takenAt[i] = -1; coveredAt[i] = -1; k--;
            }
            // The neighbour of a leaf node might as well be part of the dominating set,
            // unless the dominating set is also constrained to be independent.
            else if(nb.size() == 1 && !independent) {
                int j = nb.get(0); // An untaken neighbour will cover this vertex...
                if(takenAt[j] == -2) {
                    takenAt[j] = -1; k--;
                    for(int jj: canCover.get(j)) { // ... and all its own neighbours.
                        next[prev[jj]] = next[jj]; prev[next[jj]] = prev[jj];
                        next[jj] = prev[jj] = jj;
                        coveredAt[jj] = -1;
                    }
                }                
            }
        }
        // If these forced moves already filled up everything, there is no solution.
        if(k < 0) { return null; }        
        
        // The uncovered vertex that we have chosen to be covered at level k.
        int[] toCover = new int[k + 1];
        Arrays.fill(toCover, n);
        // The list of current vertices to choose from at current level.
        List<Integer> cc = null;
        // Where we are at in the list of vertices that can be chosen at level k.
        int[] idx = new int[k + 1];        
        // Current level of backtracking.
        int level = 0;
        
        // Backtracking is go!
        while(0 <= level && next[n] != n) {
            // If the current level has no chosen vertex to cover, choose one.
            if(toCover[level] == n) {
                toCover[level] = next[n]; idx[level] = 0;
                assert coveredAt[toCover[level]] == -2;
                cc = canCover.get(toCover[level]);
                // Sort the choices in decreasing order of their remaining chances.
                Collections.sort(cc, (v1, v2) -> chances[v2] - chances[v1]);
            }            
            // If the current level has an untried possibility, try it.
            if(level < k && idx[level] < cc.size()) {                
                int v = cc.get(idx[level]);
                assert takenAt[v] == -2;
                // If that neighbour could be taken into the dominating set, try doing so.
                if(discardedAt[v] == -2 && (!independent || coveredAt[v] == -2)) {
                    takenAt[v] = level;
                    // The chosen vertex v covers all its uncovered neighbours.
                    for(Integer w: canCover.get(v)) {
                        if(coveredAt[w] == -2) {
                            coveredAt[w] = level;
                            next[prev[w]] = next[w]; prev[next[w]] = prev[w];
                        }
                    }                
                    if(++advanceCount == giveup) { level = -1; }
                    else { level++; } // Advance to next level in backtracking.
                }
                else { idx[level]++; } // Try the next vertex at this level.
            }
            // No possibilities remain at this level, so we must backtrack.
            else {
                toCover[level] = n;
                // Un-discard the vertices that were discarded at the current level.
                for(Integer v: cc) {
                    if(discardedAt[v] == level) { 
                        discardedAt[v] = -2;
                        for(Integer w: canCover.get(v)) { 
                            if(coveredAt[w] == -2) { ++chances[w]; }
                        }
                    }
                }
                // Backtrack one level, and advance to the next possibility there.
                if(--level > -1) {
                    cc = canCover.get(toCover[level]);
                    // Undo taking vertex v into the dominating set.
                    int v = canCover.get(toCover[level]).get(idx[level]);                    
                    takenAt[v] = -2; discardedAt[v] = level;
                    boolean forceCut = false;
                    for(Integer w: canCover.get(v)) {
                        // Vertices that were covered by this vertex no longer are.
                        if(coveredAt[w] == level) {
                            coveredAt[w] = -2; next[prev[w]] = w; prev[next[w]] = w;
                            if(--chances[w] == 0) { // Forward checking cutoff
                                forceCut = true; forwardCheckingCount++;
                            }
                        }
                    }
                    idx[level] = forceCut ? cc.size() : idx[level] + 1;                    
                }
            }            
        }
        if(verbose) {
            System.out.println("Forward checking cutoffs: " + forwardCheckingCount);
        }
        if(level == -1 || next[n] != n) { return null; }
        // Construct the solution for the vertices that are taken.
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < n; i++) { if(takenAt[i] != -2) { result.add(i); } }
        return result;
    }
        
    // Count the number of positions where the two words differ.
    private static int hammingDistance(String w1, String w2) {
        int dist = 0;
        for(int i = 0; i < w1.length(); i++) {
            if(w1.charAt(i) != w2.charAt(i)) { dist++; }
        }
        return dist;
    }
    
    public static void main(String[] args) throws java.io.FileNotFoundException  {        
        final int WORDS = 500;
        final boolean INDEPENDENT = false;
        final long GIVEUP = 100_000_000L;
        
        ArrayList<String> words = new ArrayList<String>();
        Scanner sc = new Scanner(new java.io.File("sgb-words.txt"));
        sc.useDelimiter("\\n");
        while(sc.hasNextLine() && words.size() < WORDS) {
            String word = sc.nextLine();            
            words.add(word);
        }
        sc.close();
        System.out.println("Read the " + words.size() + " most common words of sgb-words.txt.");     
        
        // Build and return the neighbour map of given list of words.
        final int HAMMINGMIN = 1; // Minimum Hamming distance for edge to exist.
        final int HAMMINGMAX = 2; // Maximum Hamming distance for edge to exist.
        List<List<Integer>> neighbours = new ArrayList<>();
        int edgeCount = 0;
        for(int i = 0; i < words.size(); i++) {
            String w1 = words.get(i);
            List<Integer> nb = new LinkedList<>();
            for(int j = 0; j < words.size(); j++) {
                String w2 = words.get(j);
                int d = hammingDistance(w1, w2);
                if(HAMMINGMIN <= d && d <= HAMMINGMAX) { nb.add(j); ++edgeCount; }
            }
            neighbours.add(nb);
        }
        
        List<List<Integer>> components = new ArrayList<>();
        LinkedList<Integer> frontier = new LinkedList<>();
        int[] componentIdx = new int[words.size()];
        int currComp = 1;
        for(int i = 0; i < words.size(); i++) {
            if(componentIdx[i] > 0) { continue; }
            List<Integer> comp = new ArrayList<>();
            // Perform a frontier search from current word to discover its component.
            frontier.clear(); frontier.add(i); comp.add(i); componentIdx[i] = currComp;
            while(frontier.size() > 0) {
                int v = frontier.removeFirst();
                for(int w: neighbours.get(v)) {
                    if(componentIdx[w] == 0) {
                        componentIdx[w] = currComp; frontier.add(w); comp.add(w);
                    }
                }
            }
            components.add(comp);
            currComp++;
        }
        System.out.print("The word graph contains " + (edgeCount / 2) + 
            " undirected edges and " + (currComp - 1) + " connected component"
            + (currComp == 2 ? "." : "s."));
        Collections.sort(components, (c1, c2) -> {
            int s1 = c1.size(); int s2 = c2.size();
            if(s1 < s2) { return -1; }
            else if(s1 > s2) { return +1; }
            else return 0;
        });
        
        List<String> result = new ArrayList<>(); // Global solution.
        long startTime = System.currentTimeMillis();
        // Construct the dominating set one component at the time.
        for(List<Integer> component: components) {
            if(component.size() > 5) {
                System.out.print("\nComponent of size " + component.size() + " -> ");
            }
            // Renumber the vertices in current component to start from 0.
            Map<Integer, Integer> convert = new HashMap<>();
            Map<Integer, Integer> convertBack = new HashMap<>();
            int w = 0;
            for(int v: component) {
                convert.put(v, w);
                convertBack.put(w++, v);
            }
            List<List<Integer>> convertedNeighbs = new ArrayList<>();
            for(int v: component) {
                List<Integer> nn = new ArrayList<Integer>();
                for(int vv: neighbours.get(v)) { nn.add(convert.get(vv)); }
                convertedNeighbs.add(nn);                
            }
            
            // Look for dominating set for the current component.
            int k = component.size();
            List<Integer> best = null;
            while(true) {
                List<Integer> compResult = 
                    dominatingSet(convertedNeighbs, k, GIVEUP, INDEPENDENT, false);
                if(compResult == null) {
                    for(int v: best) { 
                        String word = words.get(convertBack.get(v));
                        result.add(word);
                    }
                    break;
                }
                else {                    
                    best = new ArrayList<Integer>(compResult);
                    if(component.size() > 5) { System.out.print(best.size() + " "); }
                    k = compResult.size() - 1;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        
        // Verify that the returned dominating set really covers every vertex.
        outer:
        for(int i = 0; i < words.size(); i++) {
            if(result.contains(words.get(i))) { continue; }
            for(int j: neighbours.get(i)) {
                if(result.contains(words.get(j))) { continue outer; }
            }
            System.out.println("ERROR: " + words.get(i) + " is left uncovered!"); return;
        }                
        System.out.println("\nDominating set found in " + (endTime - startTime) + " ms.");
        System.out.println("Dominating set contains " + result.size() + " words. They are:");
        int cc = 0;
        for(String word: result) {
            System.out.print(word + " ");
            if(++cc % 12 == 0) { System.out.println(""); }
        }
        if(cc % 12 != 1) { System.out.println(""); }
    }
}
