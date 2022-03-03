package TSTest;

import java.util.List;

public class TSHelpers {

    public static Integer[][] clone (Integer[][] M) {
        int len = M.length;
        Integer[][] CM = new Integer[len][len];
        for (int i=0; i<len; i++) {
            for (int j = 0; j < len; j++) {
                CM[i][j] = M[i][j];
            }
        }
        return CM;
    }
    public static boolean aCyclic(Integer[][] DM) {
        int len = DM.length;
        Integer[][] myDM = clone(DM);
        for (int k=0; k<len; k++)
            for (int i=0; i<len; i++)
                for (int j=0; j<len; j++)
                    if (myDM[i][j]==0 && myDM[i][k]==1 && myDM[k][j]==1) {
                        myDM[i][j] = 1;
                    }
        for (int i=0; i<len; i++) {
            if (myDM[i][i] == 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOrdering (List<Integer> ordering, Integer[][] DependsOn) {
        int len = ordering.size();
        Integer[] order = new Integer[len];
        order = ordering.toArray(order);
        for (int i=0; i<len-1; i++) {
            for (int j=i+1; j<len; j++) {
                if (DependsOn[order[i]][order[j]] == 1)
                    return false;
            }
        }
        return true;
    }

    public static boolean reachable(int from, int to, Integer[][] DM) {
        int len = DM.length;
        Integer[][] myDM = clone(DM);
        for (int k=0; k<len; k++)
            for (int i=0; i<len; i++)
                for (int j=0; j<len; j++)
                    if (myDM[i][j]==0 && myDM[i][k]==1 && myDM[k][j]==1) {
                        myDM[i][j] = 1;
                    }
        return (myDM[from][to] == 1);
    }

}
