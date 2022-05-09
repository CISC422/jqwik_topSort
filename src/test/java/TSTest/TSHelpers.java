package TSTest;

import net.jqwik.api.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TSHelpers {

    public static Integer[][] cloneM (Integer[][] M) {
        int len = M.length;
        Integer[][] CM = new Integer[len][len];
        for (int i=0; i<len; i++) {
            System.arraycopy(M[i], 0, CM[i], 0, len);
        }
        return CM;
    }

    public static List<List<Integer>> cloneL (List<List<Integer>> DependencyL) {
        return new ArrayList<>(DependencyL);
    }

    public static Integer[][] buildDependencyMatrix (int numNodes, List<List<Integer>> DependencyL) {
        Integer[][] DepM = new Integer[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                List<Integer> dep = new ArrayList<>(Arrays.asList(i, j));
                if (DependencyL.contains(dep))
                    DepM[i][j] = 1;
                else
                    DepM[i][j] = 0;
            }
        }
        return DepM;
    }

    @Example
    public void ex1() {
        List<List<Integer>> DepL1 = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        aCyclic(DepL1);
    }

    // check if there is a cycle in the dependency list
    public static boolean aCyclic(List<List<Integer>> DependencyL) {
        int maxNodeId = 0;
        for (int i=0; i<DependencyL.size(); i++) {
            if (DependencyL.get(i).get(0) > maxNodeId)
                maxNodeId = DependencyL.get(i).get(0);
            if (DependencyL.get(i).get(1) > maxNodeId)
                maxNodeId = DependencyL.get(i).get(1);
        }
        Integer[][] DependencyM = buildDependencyMatrix(maxNodeId+1, DependencyL);
        return TSHelpers.aCyclicM(DependencyM);
    }

    // use Floyd-Warshall algorithm to find all paths
    public static boolean aCyclicM(Integer[][] DepM) {
        int len = DepM.length;
        Integer[][] myDepM = cloneM(DepM);
        for (int k=0; k<len; k++)
            for (int i=0; i<len; i++)
                for (int j=0; j<len; j++)
                    if (myDepM[i][j]==0 && myDepM[i][k]==1 && myDepM[k][j]==1) {
                        myDepM[i][j] = 1;
                    }
        for (int i=0; i<len; i++) {
            if (myDepM[i][i] == 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOrdering(List<Integer> ordering, List<List<Integer>> DependencyL) {
        int len = ordering.size();
        Integer[] order = ordering.toArray(new Integer[0]);
        for (int i=0; i<len-1; i++) {
            for (int j=i+1; j<len; j++) {
//                if (DependsOn[order[i]][order[j]] == 1)
                if (DependencyL.contains(Arrays.asList(order[i],order[j])))
                    return false;
            }
        }
        return true;
    }

    public static boolean independent(int i, int j, int numNodes, List<List<Integer>> DependencyL) {
        Integer[][] DependencyM = buildDependencyMatrix(numNodes, DependencyL);
        return (independentM(i, j, DependencyM));
    }

    public static boolean independentM(int i, int j, Integer[][] DepM) {
        return (!reachableM(i,j,DepM) && !reachableM(j,i,DepM));
    }

    public static boolean reachable(int from, int to, int numNodes, List<List<Integer>> DependencyL) {
        Integer[][] DependencyM = buildDependencyMatrix(numNodes, DependencyL);
        return (reachableM(from, to, DependencyM));
    }

    public static boolean reachableM(int from, int to, Integer[][] DepM) {
        int len = DepM.length;
        Integer[][] myDepM = cloneM(DepM);
        for (int k=0; k<len; k++)
            for (int i=0; i<len; i++)
                for (int j=0; j<len; j++)
                    if (myDepM[i][j]==0 && myDepM[i][k]==1 && myDepM[k][j]==1) {
                        myDepM[i][j] = 1;
                    }
        return (myDepM[from][to] == 1);
    }

}
