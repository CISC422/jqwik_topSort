/* CISC/CMPE 422/835
 * TopSort implementation
 */
package TSImpl;

import java.util.*;
import java.lang.*;
import java.lang.RuntimeException;

// Seeded sample bugs show that
// - testing should use several properties and examples
// - some properties may hold for the wrong reasons
//   (e.g., ordering may respect constraints, b/c it is empty as for Bug 3)
public class TopSort {

    enum Label {NONE, TEMP, PERM}  // nodes labeled PERM have been output (i.e., placed in the ordering); nodes labeled TEMP are currently under investigation

    public static class CyclicDependenciesException extends RuntimeException {
        public CyclicDependenciesException(String msg) {
            super(msg);
        }
    }

    // Computes topological ordering of a set of nodes. Assumes that
    // - nodes are represented as numbers from 0 to 'numNodes', and
    // - dependencies between nodes are given in a list of 2-element lists,
    //   i.e., '[i,j] in DependencyL' means that node i depends on node j
    public static List<Integer> topSort(int numNodes, List<List<Integer>> DependencyL) {
        List<Integer> ordering = new ArrayList<>();
        Label[] labels = new Label[numNodes];
        for (int i=0; i<numNodes; i++)
            labels[i] = Label.NONE;
        int i = findUnmarked(labels);
        while (i > -1) {     // correct
//          while (i > 0) {  // BUG 1: causes computed ordering to be empty
                             // BUG 1: property P1 holds, but P2 fails
            visit(i, labels, DependencyL, ordering);
            i = findUnmarked(labels);
        }
        return ordering;
    }

    // Returns a node that has not been ordered yet
    static int findUnmarked(Label[] labels) {
        boolean searchFromFront = true;  // (Math.random() < 0.5);  // randomizing the search can lead to different orderings to be found for the same deps
        if (searchFromFront) {
//            for (int i = 0; i < labels.length-1; i++)   // BUG 2: causes ordering to not contain all nodes, if no node depends on last node (i.e., numNodes-1);
                                                        // BUG 2: also prevents cycle to be detected, if last node depends on itself
                                                        // BUG 2: example tests and all properties except P2 and P4 succeed
            for (int i = 0; i < labels.length; i++)     // correct
                if (labels[i] != Label.PERM)
//                if (labels[i] == Label.NONE)          // also correct
                    return i;
            return -1;
        }
        else {
            for (int i = labels.length-1; i >= 0; i--)
                if (labels[i] != Label.PERM)            // correct
                    return i;
            return -1;
        }
    }

    // Explores nodes reachable from 'i'
    static void visit(int i, Label[] labels, List<List<Integer>> DependencyL, List<Integer> out) {
        if (labels[i] == Label.PERM)
            return;                       // i has already been output, so any dependency that a node under investigation has on i can be discharged
        if (labels[i] == Label.TEMP)
            throw new CyclicDependenciesException("Cyclic dependencies (node "+i+" depends on itself)");    // i depends on itself
 //       labels[i] = Label.PERM;  // BUG 3: causes output of ordering even in case of cyclic list
                                   // BUG 3: bug not found when only considering acyclic lists
        labels[i] = Label.TEMP;    // correct
        for (int j=0; j<labels.length; j++) {    // investigate all nodes that i depends on
            if (DependencyL.contains(Arrays.asList(i,j))) {
                visit(j, labels, DependencyL, out);
            }
        }
        labels[i] = Label.PERM;       // all nodes that i depends on have been investigated and output, so we can also output i
//        out.add(0,i);               // BUG 4: computed orderings violate constraints
        out.add(i);                   // correct
//        for (int k=0; k<Integer.MAX_VALUE; k++);  // BUG 5: execution takes too long
    }

    // OPERATIONS ON DEPENDENCY LISTS ====================================================

    // adds the dependency [i,j] to the dependency list
    public static List<List<Integer>> addDependency(int i, int j, List<List<Integer>> DependencyL) {
        DependencyL.add(Arrays.asList(i,j));
        return DependencyL;
    }

    // removes the dependency [i,j] from the dependency list
    public static List<List<Integer>> removeDependency(int i, int j, List<List<Integer>> DependencyL) {
        DependencyL.remove(Arrays.asList(i,j));
        return DependencyL;
    }

    // HELPERS ====================================================

    // returns string of dependency list in which dependencies are ordered
    public static String toStringSorted(List<List<Integer>> depList) {
        List<List<Integer>> depListCopy = new ArrayList<>();
        int len = depList.size();
        List<Integer> depI, depJ;
        for (List<Integer> integers : depList) {
            depI = integers;
            int j;
            for (j = 0; j < depListCopy.size(); j++) {
                depJ = depListCopy.get(j);
                if ((depI.get(0) < depJ.get(0)) || ((depI.get(0) == depJ.get(0)) && (depI.get(1) < depJ.get(1))))
                    break;
            }
            depListCopy.add(j, depI);
        }
        return depListCopy.toString();
    }

}
