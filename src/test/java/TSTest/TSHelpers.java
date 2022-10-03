/* CISC/CMPE 422/835
 * Collection of helper methods to
 * - turn dependency lists into dependency matrix
 * - compute reachability between all nodes using Floyd-Warshall algorithm
 * - determine if there is a path between two nodes in either direction ('independence')
 */
package TSTest;

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TSHelpers {

    public static Integer[][] cloneM (Integer[][] mat) {
        int len = mat.length;
        Integer[][] mat2 = new Integer[len][len];
        for (int i=0; i<len; i++) {
            System.arraycopy(mat[i], 0, mat2[i], 0, len);
        }
        return mat2;
    }

    public static List<List<Integer>> cloneL (List<List<Integer>> depsL) {
        return new ArrayList<>(depsL);
    }

    public static Integer[][] buildDependencyMatrix (int numNodes, List<List<Integer>> depsL) {
        Integer[][] depsM = new Integer[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                List<Integer> dep = new ArrayList<>(Arrays.asList(i, j));
                if (depsL.contains(dep))
                    depsM[i][j] = 1;
                else
                    depsM[i][j] = 0;
            }
        }
        return depsM;
    }

    @Example
    public void ex1() {
        List<List<Integer>> depsL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        Assertions.assertThat(TSHelpers.aCyclic(depsL)).isTrue();
    }

    @Example
    public void ex2() {
        List<List<Integer>> depsL = new ArrayList<>(Arrays.asList(
                Arrays.asList(0,1),
                Arrays.asList(1,2),
                Arrays.asList(2,3),
                Arrays.asList(3,0)
        ));
        Assertions.assertThat(TSHelpers.aCyclic(depsL)).isTrue();
    }

    // check if there is a cycle in the dependency list
    public static boolean aCyclic(List<List<Integer>> depsL) {
        int maxNodeId = 0;
        for (int i=0; i<depsL.size(); i++) {
            if (depsL.get(i).get(0) > maxNodeId)
                maxNodeId = depsL.get(i).get(0);
            if (depsL.get(i).get(1) > maxNodeId)
                maxNodeId = depsL.get(i).get(1);
        }
        Integer[][] depsM = buildDependencyMatrix(maxNodeId+1, depsL);
        return TSHelpers.aCyclicM(depsM);
    }

    // use Floyd-Warshall algorithm to find all paths
    public static boolean aCyclicM(Integer[][] depsM) {
        int len = depsM.length;
        Integer[][] myDepsM = cloneM(depsM);
        for (int k=0; k<len; k++)
            for (int i=0; i<len; i++)
                for (int j=0; j<len; j++)
                    if (myDepsM[i][j]==0 && myDepsM[i][k]==1 && myDepsM[k][j]==1) {
                        myDepsM[i][j] = 1;
                    }
        for (int i=0; i<len; i++) {
            if (myDepsM[i][i] == 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOrdering(List<Integer> ord, List<List<Integer>> depsL) {
        int len = ord.size();
        Integer[] order = ord.toArray(new Integer[0]);
        for (int i=0; i<len-1; i++) {
            for (int j=i+1; j<len; j++) {
                if (depsL.contains(Arrays.asList(order[i],order[j])))
                    return false;
            }
        }
        return true;
    }

    public static boolean independent(int i, int j, int numNodes, List<List<Integer>> depsL) {
        Integer[][] depsM = buildDependencyMatrix(numNodes, depsL);
        return (independentM(i, j, depsM));
    }

    // nodes i, j are independent if j not reachable from i and vice versa
    public static boolean independentM(int i, int j, Integer[][] depsM) {
        return (!reachableM(i,j,depsM) && !reachableM(j,i,depsM));
    }

    public static boolean reachable(int from, int to, int numNodes, List<List<Integer>> depsL) {
        Integer[][] depsM = buildDependencyMatrix(numNodes, depsL);
        return (reachableM(from, to, depsM));
    }

    public static boolean reachableM(int from, int to, Integer[][] depsM) {
        int len = depsM.length;
        Integer[][] myDepsM = cloneM(depsM);
        for (int k=0; k<len; k++)
            for (int i=0; i<len; i++)
                for (int j=0; j<len; j++)
                    if (myDepsM[i][j]==0 && myDepsM[i][k]==1 && myDepsM[k][j]==1) {
                        myDepsM[i][j] = 1;
                    }
        return (myDepsM[from][to] == 1);
    }

}
