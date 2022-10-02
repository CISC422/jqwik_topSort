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
        while (i > -1) {  // correct
//          while (i > 0) {  // bug 3: causes computed ordering to be empty;
            // resulting ordering does not violate any constraints, i.e., 'checkOrdering' holds
            // need property 'propCheckComputedOrdering3', caught by test 2
            visit(i, labels, DependencyL, ordering);
            i = findUnmarked(labels);
        }
        return ordering;
    }

    // Returns a node that has not been ordered yet
    static int findUnmarked(Label[] labels) {
        boolean searchFromFront = true;  // (Math.random() < 0.5);
        if (searchFromFront) {
            for (int i = 0; i < labels.length; i++)
                if (labels[i] != Label.PERM)  // correct
//                if (labels[i] == Label.NONE)   // bug?
                    return i;
            return -1;
        }
        else {
            for (int i = labels.length-1; i >= 0; i--)
                if (labels[i] != Label.PERM)  // correct
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
 //       labels[i] = Label.PERM;  // bug 1: causes output of ordering even in case of cyclic list
                                   // => bug not found when only considering acyclic lists (caught by 'test 2')
        labels[i] = Label.TEMP;    // correct
        for (int j=0; j<labels.length; j++) {    // investigate all nodes that i depends on
            if (DependencyL.contains(Arrays.asList(i,j))) {
                visit(j, labels, DependencyL, out);
            }
        }
        labels[i] = Label.PERM;       // all nodes that i depends on have been investigated and output, so we can also output i
//        out.add(0,i);          // bug 2: computed orderings violate constraints (caught by test 3)
        out.add(i);              // correct
//        for (int k=0; k<Integer.MAX_VALUE; k++);  // bug 4: execution takes too long
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
