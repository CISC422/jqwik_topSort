package TSImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopSort2 {

    public static class CyclicDependenciesException extends Exception {}

    enum Label {NONE, TEMP, PERM}

    static int findUnmarked(Label[] labels) {
        for (int i=0; i<labels.length; i++)
            if (labels[i] != Label.PERM)
                return i;
        return -1;
    }

    static void visitM (int i, Label[] labels, Integer[][] DependencyM, List<Integer> out) throws CyclicDependenciesException {
        if (labels[i] == Label.PERM)
            return;                       // i has already been output, so any dependency that a node under investigation has on i can be discharged
        if (labels[i] == Label.TEMP)
            throw new CyclicDependenciesException();    // i depends on itself --- oh, no!
 //       labels[i] = Label.PERM;  // bug
        labels[i] = Label.TEMP; // correct
        for (int j=0; j<labels.length; j++) {    // investigate all nodes that i depends on
            if (DependencyM[i][j] == 1) {
                visitM(j, labels, DependencyM, out);
            }
        }
        labels[i] = Label.PERM;       // all nodes that i depends on have been investigated and output, so we can also output i
//        out.add(0,i);  // bug
        out.add(i);
    }

    public static List<Integer> computeOrderingM(Integer[][] DependencyM) throws CyclicDependenciesException {
        List<Integer> out = new ArrayList<>();
        int numNodes = DependencyM.length;
        Label[] labels = new Label[numNodes];
        for (int i=0; i<numNodes; i++)
            labels[i] = Label.NONE;
        int i = findUnmarked(labels);
        while (i > -1) {  // correct
 //       while (i > 0) {  // bug
            visitM(i, labels, DependencyM, out);
            i = findUnmarked(labels);
        }
        return out;
    }

    static void visitL (int i, Label[] labels, List<List<Integer>> DependencyL, List<Integer> out) throws CyclicDependenciesException {
        if (labels[i] == Label.PERM)
            return;                       // i has already been output, so any dependency that a node under investigation has on i can be discharged
        if (labels[i] == Label.TEMP)
            throw new CyclicDependenciesException();    // i depends on itself --- oh, no!
        //       labels[i] = Label.PERM;  // bug
        labels[i] = Label.TEMP; // correct
        for (int j=0; j<labels.length; j++) {    // investigate all nodes that i depends on
            if (DependencyL.contains(Arrays.asList(i,j))) {
                visitL(j, labels, DependencyL, out);
            }
        }
        labels[i] = Label.PERM;       // all nodes that i depends on have been investigated and output, so we can also output i
//        out.add(0,i);  // bug
        out.add(i);
    }

    public static List<Integer> computeOrderingL(int numNodes, List<List<Integer>> DependencyL) throws CyclicDependenciesException {
        List<Integer> out = new ArrayList<>();
        Label[] labels = new Label[numNodes];
        for (int i=0; i<numNodes; i++)
            labels[i] = Label.NONE;
        int i = findUnmarked(labels);
        while (i > -1) {  // correct
            //       while (i > 0) {  // bug
            visitL(i, labels, DependencyL, out);
            i = findUnmarked(labels);
        }
        return out;
    }

    public static String toStringSorted(List<List<Integer>> depList) {
        List<List<Integer>> depListCopy = new ArrayList<>();
        int len = depList.size();
        List<Integer> depI, depJ;
        for (int i=0; i<len; i++) {
            depI = depList.get(i);
            int j;
            for (j=0; j<depListCopy.size(); j++) {
                depJ = depListCopy.get(j);
                if ((depI.get(0) < depJ.get(0)) || ((depI.get(0) == depJ.get(0)) && (depI.get(1) < depJ.get(1))))
                    break;
            }
            depListCopy.add(j, depI);
        }
        return depListCopy.toString();
    }

    public static Integer[][] addDependencyM(int i, int j, Integer[][] DM) {
        DM[i][j] = 1;
        return DM;
    }

    public static Integer[][] remDependencyM(int i, int j, Integer[][] DM) {
        DM[i][j] = 0;
        return DM;
    }

    public static List<List<Integer>> addDependencyL(int i, int j, List<List<Integer>> DL) {
        DL.add(Arrays.asList(i,j));
        return DL;
    }

    public static List<List<Integer>> remDependencyL(int i, int j, List<List<Integer>> DL) {
        DL.remove(Arrays.asList(i,j));
        return DL;
    }

}
