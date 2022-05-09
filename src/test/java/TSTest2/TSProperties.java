package TSTest2;

import TSImpl.TopSort;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static TSImpl.TopSort.*;
import static TSTest.TSHelpers.*;

public class TSProperties {

/*
    @Property
    @Report(Reporting.GENERATED)
    void propGeneratorCheck (@ForAll("dependencyMatrices") Integer[][] DependencyM) {
//        Assume.that(aCyclic(DependencyM));
        if (aCyclic(DependencyM))
            System.out.println("does NOT have cycle: " + Arrays.deepToString(DependencyM));
        else
            System.out.println("does have cycle: " + Arrays.deepToString(DependencyM));
//       Assertions.assertThat(aCyclic(DependencyM)).isTrue();
    }

    // computed ordering does respect all dependencies
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering1 (@ForAll("dependencyMatrices") Integer[][] DependencyM) {
        List<Integer> ordering=null;
        try {
            ordering = computeOrderingM(DependencyM);
            Assertions.assertThat(checkOrdering(ordering,DependencyM)).isTrue();
            Assertions.assertThat(aCyclic(DependencyM)).isTrue();
        } catch (CyclicDependenciesException e) {
            System.out.println("Cyclic dependencies!");
            Assertions.assertThat(TSHelpers.aCyclic(DependencyM)).isFalse();
        }
    }

    // if i depends on j (i.e., DependencyM[i][j]==1), then j will appear before i in the ordering computed
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering2 (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                                     @ForAll @IntRange(min=0, max=3) Integer i,
                                     @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(aCyclic(DependencyM));
        Assume.that(DependencyM[i][j] == 1);
        List<Integer> ordering=null;
        try {
            ordering = computeOrderingM(DependencyM);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        int indexOfI = ordering.indexOf(i);
        int indexOfJ = ordering.indexOf(j);
        Assertions.assertThat(indexOfJ).isLessThan(indexOfI);
    }

    // the ordering produced contains exactly the numbers 0 to dimension(DependencyM)-1
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering3 (@ForAll("dependencyMatrices") Integer[][] DependencyM) {
        Assume.that(aCyclic(DependencyM));
        List<Integer> ordering=null;
        try {
            ordering = computeOrderingM(DependencyM);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        boolean res;
        for (int i=0; i< DependencyM.length; i++) {
            res = ordering.remove((Object) i);
            Assertions.assertThat(res).isTrue();
        }
        Assertions.assertThat(ordering).isEmpty();
    }

    // use JQWIK's test case generation to 'compute' a solution
    @Property
    @Report(Reporting.GENERATED)
    void propGenerateSolution (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                               @ForAll("orderings") List<Integer> order) {
        Assume.that(aCyclic(DependencyM));
        Assume.that(DependencyM.length == order.size());
        Assume.that(checkOrdering(order,DependencyM) == true);
//        Assertions.assertThat(checkOrdering(order,DependsOn)).isFalse();
        System.out.println(Arrays.deepToString(DependencyM));
        System.out.println("generated solution: " + order);
    }

    // removing a dependency preserves the correctness of an ordering
    @Property
    @Report(Reporting.GENERATED)
    void propRemDependency (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                            @ForAll @IntRange(min=0, max=3) Integer i,
                            @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(aCyclic(DependencyM));
        List<Integer> ordering0=null;
        try {
            ordering0 = computeOrderingM(DependencyM);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        DependencyM = remDependencyM(i,j,DependencyM);
        Assertions.assertThat(checkOrdering(ordering0, DependencyM)).isTrue();
    }

    // after adding (i,j) to dependsOn, j will appear before i in the ordering
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency1 (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                             @ForAll @IntRange(min=0, max=3) Integer i,
                             @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(i != j);
        DependencyM = addDependencyM(i,j,DependencyM);
        Assume.that(aCyclic(DependencyM));
        List<Integer> ordering=null;
        try {
            ordering = computeOrderingM(DependencyM);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        int indexOfI = ordering.indexOf(i);
        int indexOfJ = ordering.indexOf(j);
        Assertions.assertThat(indexOfJ).isLessThan(indexOfI);
    }

    // property: if DM acyclic and DM1=add(i,j,DM) is cyclic, then reachable(j,i,DM)
    // assumes that dimensions of generated matrices are consistent with generated indices
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency2 (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                             @ForAll @IntRange(min=0, max=3) Integer i,
                             @ForAll @IntRange(min=0, max=3) Integer j) {
        // Assume.that(TSHelpers.aCyclic(DependencyM));
        Integer[][] DM0 = TSHelpers.clone(DependencyM);
        DependencyM = addDependencyM(i, j, DependencyM);
        if (!aCyclic(DependencyM)) {
            System.out.println("DM after insert" + Arrays.deepToString(DependencyM));
            Assertions.assertThat(reachable(j, i, DM0));
        }
    }

     // property: if out is ordering of DM and i before j in out, then out is not an ordering of add(i,j,DM)
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency3 (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                             @ForAll @IntRange(min=0, max=3) Integer i,
                             @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(i != j);
        Assume.that(DependencyM[i][j] == 0);
        Assume.that(aCyclic(DependencyM));
        List<Integer> ordering=null;
        try {
            ordering = computeOrderingM(DependencyM);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        int indexOfI = ordering.indexOf(i);
        int indexOfJ = ordering.indexOf(j);
        if (indexOfI < indexOfJ) {
            DependencyM = addDependencyM(i, j, DependencyM);
            Assertions.assertThat(checkOrdering(ordering, DependencyM)).isFalse();
        }
        else {
            DependencyM = addDependencyM(j, i, DependencyM);
            Assertions.assertThat(checkOrdering(ordering, DependencyM)).isFalse();
        }
    }

    // If i and j independent, and o is an ordering, then reversing the order of i and j in o also is an ordering
    // fails
    @Property
    @Report(Reporting.GENERATED)
    void propComputeOrderingAndIndependence (@ForAll("dependencyMatrices") Integer[][] DependencyM,
                                             @ForAll @IntRange(min=0, max=3) Integer i,
                                             @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(i != j);
        Assume.that(independent(i, j, DependencyM));
        Assume.that(aCyclic(DependencyM));
        List<Integer> ordering=null;
        try {
            ordering = computeOrderingM(DependencyM);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        System.out.println("old ordering: " + ordering);
        int indexOfI = ordering.indexOf(i);
        int indexOfJ = ordering.indexOf(j);
        ordering.set(indexOfI, j);
        ordering.set(indexOfJ, i);
        System.out.println("new ordering: " + ordering);
//        Assertions.assertThat(checkOrdering(ordering, DependencyM)).isTrue();  // shuuld fail
        Assertions.assertThat(checkOrdering(ordering, DependencyM)).isFalse();  // should also fail, but produce instance were it works
    }

    @Property
    @Report(Reporting.GENERATED)
    void aProp (@ForAll("dependencyLists") List<List<Integer>> DependencyL) {
        System.out.println(DependencyL);
        Assertions.assertThat(DependencyL.get(0).get(0) == 1).isTrue();
    }

    // generates candidate orderings
    @Provide
    public static Arbitrary<List<Integer>> orderings() {
        int numNodes = 4;
        Arbitrary<Integer> num = Arbitraries.integers().between(0,numNodes-1);
        Arbitrary<List<Integer>> numList = num.list().uniqueElements().ofSize(numNodes);
//        Arbitrary<List<Integer>> numList = num.list().uniqueElements().ofMinSize(2).ofMaxSize(4);
        return numList;
    }

    // generates square adjacency matrices containing 0 or 1 w/o cycles that involve 0 or 1 intermediate nodes
    // dimension of the matrix is between MinNumNodes and MaxNumNodes
    // for dimension=2, generates 16 acyclic matrices (exhaustive)
    // for dimension=3, generates 512 matrices of which 464 are acyclic (exhaustive)
    // for dimension=4, generates 65536 matrices of which 45536 are acyclic (exhaustive at 100K tries)
    @Provide
    public static Arbitrary<Integer[][]> dependencyMatrices() {
        final int MinNumNodes = 4;
        final int MaxNumNodes = 4;
        //       final int NumNodes = 5;  // NumNodes=4 needs 100K tries for exhaustive search
        Arbitrary<Integer> intArb = Arbitraries.integers().between(0,1);
        Arbitrary<Integer[]> intArrayArb = intArb.array(Integer[].class).ofMinSize(MinNumNodes).ofMaxSize(MaxNumNodes);
        Arbitrary<Integer[][]> intMatrixArb =
                intArrayArb.array(Integer[][].class)
                        .ofMinSize(MinNumNodes)
                        .ofMaxSize(MaxNumNodes)
                        .filter(m -> {  // filter out the matrices that are not square
                            int dim = m[0].length;  // use length of first row of the matrix as matrix dimension
                            return Arrays
                                    .stream(m)
                                    .allMatch(row -> m.length==dim && row.length==dim);
                        })
                        .map(m -> {    // break cycles of length 0 and 1; w/o this, get too many cyclic matrices
                            int len = m[0].length;
                            for (int i=0; i<len; i++) {
                                for (int j = 0; j < len; j++) {
                                    if (m[i][j] == 1)
                                        m[j][i] = 0;
                                }
                            }
                            return m;
                        });
         return intMatrixArb;
    }

    @Property
    public void propertyCheckListGenerator (@ForAll("depencencyLists1") List<List<Integer>> DependencyL) {
        System.out.println(TopSort.toStringSorted(DependencyL));
//        Assertions.assertThat(ll.size()).isLessThan(7); // holds
    }

    public boolean aCyclicL (List<List<Integer>> DependencyL) {
        Integer[][] M = new Integer[DependencyL.size()][DependencyL.size()];
        for (int i=0; i<DependencyL.size(); i++) {
            for (int j = 0; j < DependencyL.size(); j++) {
                List<Integer> l = new ArrayList<>(Arrays.asList(i,j));
//                l.add(i);
//                l.add(j);
                if (DependencyL.contains(l))
                    M[i][j] = 1;
                else
                    M[i][j] = 0;
            }
        }
        return TSHelpers.aCyclic(M);
    }

    @Provide
    public Arbitrary<List<List<Integer>>> dependencyLists1() {
        final int numNodes = 4;
        final int maxTuples = 12;
        Arbitrary<Integer> num = Arbitraries.integers().between(0,numNodes-1);
//        Arbitrary<List<Integer>> tuples = num.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        Arbitrary<List<Integer>> tuples = num.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        return tuples.list()
                .ofMinSize(0)
                .ofMaxSize(12)
                .uniqueElements()
                .filter(ll -> aCyclicL(ll));
    }

    @Provide
    public Arbitrary<List<List<Integer>>> dependencyLists2() {
        final int numNodes = 4;
        final int maxTuples = 12;
        Arbitrary<Integer> num = Arbitraries.integers().between(0,numNodes-1);
//        Arbitrary<List<Integer>> tuples = num.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        Arbitrary<List<Integer>> tuples = num.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        return
                tuples.list()
                        .ofMinSize(0)
                        .ofMaxSize(12)
                        .uniqueElements()
                        .filter(ll -> {
                            int i, j;
                            List<Integer> revT = new ArrayList<>();
                            for (int ind = 0; ind < ll.size(); ind++) {
                                i = ll.get(ind).get(0);
                                j = ll.get(ind).get(1);
                                revT = Arrays.asList(j,i);
//                                revT.add(j);
//                                revT.add(i);
                                if (ll.contains(revT))
                                    return false;
                            }
                            return true;
                        });
    }


    @Provide
    public Arbitrary<List<List<Integer>>> dependencyLists3() {
        Arbitrary<Integer> num = Arbitraries.integers().between(0,1);
        //	Arbitrary<List<Integer>> numList = num.list().ofSize(3);
        Arbitrary<List<Integer>> numList = num.list().ofMinSize(1).ofMaxSize(3);
        return numList.list().ofMinSize(1).ofMaxSize(3).filter(ll -> {
            int len = ll.get(0).size();
            return ll.stream().allMatch(l -> ll.size()==len && l.size()==len);});
    }

*/

}
