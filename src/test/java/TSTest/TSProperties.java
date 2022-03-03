package TSTest;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.List;

import static TSImpl.TopSort.*;
import static TSTest.TSHelpers.*;

public class TSProperties {

    @Property
    @Report(Reporting.GENERATED)
    void propGeneratorCheck (@ForAll("nodeDependenciesAsMatrix") Integer[][] DependsOn) {
//        Assume.that(aCyclic(DependsOn));
        if (aCyclic(DependsOn))
            System.out.println("does NOT have cycle: " + Arrays.deepToString(DependsOn));
        else
            System.out.println("does have cycle: " + Arrays.deepToString(DependsOn));
        Assertions.assertThat(aCyclic(DependsOn)).isTrue();
    }

    @Property
    @Report(Reporting.GENERATED)
    void propComputeOrdering (@ForAll("nodeDependenciesAsMatrix") Integer[][] DependsOn) {
        List<Integer> out=null;
        try {
            out = computeOrdering(DependsOn);
            Assertions.assertThat(checkOrdering(out,DependsOn)).isTrue();
            Assertions.assertThat(aCyclic(DependsOn)).isTrue();
        } catch (CyclicDependenciesException e) {
            System.out.println("Cyclic dependencies!");
            Assertions.assertThat(TSHelpers.aCyclic(DependsOn)).isFalse();
        }
    }

    @Property
    @Report(Reporting.GENERATED)
    void propGenerateSolution (@ForAll("nodeDependenciesAsMatrix") Integer[][] DependsOn,
                               @ForAll("orders") List<Integer> order) {
        Assume.that(aCyclic(DependsOn));
        Assume.that(DependsOn.length == order.size());
        Assume.that(checkOrdering(order,DependsOn) == true);
//        Assertions.assertThat(checkOrdering(order,DependsOn)).isFalse();
        System.out.println(Arrays.deepToString(DependsOn));
        System.out.println("generated solution: " + order);
    }

    @Property
    @Report(Reporting.GENERATED)
    void propRemDependency (@ForAll("nodeDependenciesAsMatrix") Integer[][] DependsOn,
                            @ForAll @IntRange(min=0, max=3) Integer i,
                            @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(aCyclic(DependsOn));
        List<Integer> out0=null;
        try {
            out0 = computeOrdering(DependsOn);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        DependsOn = remDependency(i,j,DependsOn);
        Assertions.assertThat(checkOrdering(out0, DependsOn)).isTrue();
    }

    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency1 (@ForAll("nodeDependenciesAsMatrix") Integer[][] DependsOn,
                             @ForAll @IntRange(min=0, max=3) Integer i,
                             @ForAll @IntRange(min=0, max=3) Integer j) {
        Assume.that(i != j);
        DependsOn = addDependency(i,j,DependsOn);
        Assume.that(aCyclic(DependsOn));
        List<Integer> out=null;
        try {
            out = computeOrdering(DependsOn);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        int indexOfI = out.indexOf(i);
        int indexOfJ = out.indexOf(j);
        Assertions.assertThat(indexOfJ).isLessThan(indexOfI);
    }

    // property: if DM acyclic and DM1=add(i,j,DM) is cyclic, then reachable(j,i,DM)
    // check that dimensions of generated matrices are consistent with generated indices
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency2 (@ForAll("nodeDependenciesAsMatrix") Integer[][] DependsOn,
                             @ForAll @IntRange(min=0, max=3) Integer i,
                             @ForAll @IntRange(min=0, max=3) Integer j) {
        // Assume.that(TSHelpers.aCyclic(DependsOn));
        Integer[][] DM0 = TSHelpers.clone(DependsOn);
        DependsOn = addDependency(i, j, DependsOn);
        if (!aCyclic(DependsOn)) {
            System.out.println("DM after insert" + Arrays.deepToString(DependsOn));
            Assertions.assertThat(reachable(j, i, DM0));
        }
    }


     /* property: if out is ordering of DM and i before j in out, then out is not an ordering of add(i,j,DM)
     */

    @Property
    @Report(Reporting.GENERATED)
    void aProp (@ForAll("nodeDependenciesAsListOfLists") List<List<Integer>> DL) {
        System.out.println(DL);
        Assertions.assertThat(DL.get(0).get(0) == 1).isTrue();
    }


    // generates candidate orderings
    @Provide
    public static Arbitrary<List<Integer>> orders() {
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
    public static Arbitrary<Integer[][]> nodeDependenciesAsMatrix() {
        final int MinNumNodes = 3;
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

    @Provide
    public Arbitrary<List<List<Integer>>> nodeDependenciesAsListOfLists() {
        Arbitrary<Integer> num = Arbitraries.integers().between(0,1);
        //	Arbitrary<List<Integer>> numList = num.list().ofSize(3);
        Arbitrary<List<Integer>> numList = num.list().ofMinSize(1).ofMaxSize(3);
        return numList.list().ofMinSize(1).ofMaxSize(3).filter(ll -> {
            int len = ll.get(0).size();
            return ll.stream().allMatch(l -> ll.size()==len && l.size()==len);});
    }


}
