package TSImpl;

import java.util.*;

public class TopSort {

    public static class CyclicDependenciesException extends Exception {}

    enum Label {NONE, TEMP, PERM};

    static int findUnmarked(Label[] labels) {
        for (int i=0; i<labels.length; i++)
            if (labels[i] != Label.PERM)
                return i;
        return -1;
    }

    static void visit (int i, Label[] labels, Integer[][] DependsOn, List<Integer> out) throws CyclicDependenciesException {
        if (labels[i] == Label.PERM)
            return;                       // i has already been output, so any dependency that a node under investigation has on i can be discharged
        if (labels[i] == Label.TEMP)
            throw new CyclicDependenciesException();    // i depends on itself --- oh, no!
 //       labels[i] = Label.PERM;  // bug
        labels[i] = Label.TEMP; // correct
        for (int j=0; j<labels.length; j++) {    // investigate all nodes that i depends on
            if (DependsOn[i][j] == 1) {
                visit(j, labels, DependsOn, out);
            }
        }
        labels[i] = Label.PERM;       // all nodes that i depends on have been investigated and output, so we can also output i
//        out.add(0,i);  // bug
        out.add(i);
    }

    public static List<Integer> computeOrdering(Integer[][] DependsOn) throws CyclicDependenciesException {
        List<Integer> out = new ArrayList<>();
        int numNodes = DependsOn.length;
        Label[] labels = new Label[numNodes];
        for (int i=0; i<numNodes; i++)
            labels[i] = Label.NONE;
        int i = findUnmarked(labels);
        while (i > -1) {  // correct
 //       while (i > 0) {  // bug
            visit(i, labels, DependsOn, out);
            i = findUnmarked(labels);
        }
        return out;
    }

    public static Integer[][] addDependency(int i, int j, Integer[][] DM) {
        DM[i][j] = 1;
        return DM;
    }

    public static Integer[][] remDependency(int i, int j, Integer[][] DM) {
        DM[i][j] = 0;
        return DM;
    }

}
