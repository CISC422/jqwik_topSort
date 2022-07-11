/* CISC/CMPE 422/835
 * Properties and generators for property-based testing w/ Jqwik of TopSort implementation
 */
package TSTest;

import TSImpl.TopSort;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Positive;
import net.jqwik.api.constraints.UniqueElements;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static TSImpl.TopSort.*;
import static TSTest.TSHelpers.*;

public class TSProperties {

    final int  numNodes = 4;  // set of nodes is {0, ..., numNodes-1}

// TESTING 'topSort' INDIVIDUALLY: Main properties (see notes) ===========================

    // P1: "computed ordering respect all dependencies"
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering1 (@ForAll("dependencyListsCyclesPossible1") List<List<Integer>> deps) {
//        void propCheckComputedOrdering1 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps) {
        try {
            List<Integer> ord = topSort(numNodes, deps);
            System.out.println("dependencies: " + deps);
            System.out.println("order: " + ord);
            Assertions.assertThat(checkOrdering(ord,deps)).isTrue();
        } catch (CyclicDependenciesException e) {
            System.out.println("CyclicDepsException thrown");
        }
    }

    // P2: "the ordering produced contains exactly the numbers 0 to numNodes-1"
    @Property
    @Report(Reporting.GENERATED)
//    void propCheckComputedOrdering3a (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps) {
    void propCheckComputedOrdering3a (@ForAll("dependencyListsCyclesPossible2") List<List<Integer>> deps) {
        try {
            List<Integer> ord = topSort(numNodes, deps);
            System.out.println("dependencies: " + deps);
            System.out.println("ordering: " + ord);
            boolean res=false;
            for (int i=0; i<numNodes; i++) {
                res = ord.remove((Object) i);
                Assertions.assertThat(res).isTrue();
            }
            Assertions.assertThat(ord).isEmpty();
        }
        catch (CyclicDependenciesException e) {
            System.out.println("'CyclicDepsException' thrown");
        }
    }

    // P3a: "If dependencies are acyclic, then topSort does not throw a runtime exception"
    @Property
    @Report(Reporting.GENERATED)
//    void propCheckProperTermination (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps) {
    void propCheckProperTermination (@ForAll("dependencyListsCyclesPossible1") List<List<Integer>> deps) {
        Assume.that(aCyclic(deps));
        System.out.println("dependencies: " + deps);
        topSort(numNodes, deps);
    }

    // P4: "If dependencies are cyclic, then topSort thows 'CyclicDepsException'"
    @Property
    @Report(Reporting.GENERATED)
    void propCheckCyclicDepsException (@ForAll("dependencyListsCyclesPossible1") List<List<Integer>> deps) {
        Assume.that(!aCyclic(deps));
        System.out.println("dependencies: " + deps);
        Assertions.assertThatExceptionOfType(CyclicDependenciesException.class).isThrownBy(() -> {
            topSort(numNodes, deps);
        }).withMessageContaining("Cyclic dependencies");

    }

// TESTING 'topSort' INDIVIDUALLY: Additional properties ===========================

    // property computed ordering does respect all dependencies
    @Property
    @Report(Reporting.GENERATED)
   void propCheckComputedOrdering1a (@ForAll("dependencyListsCyclesPossible2") List<List<Integer>> deps) {
//        void propCheckComputedOrdering1 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps) {
        List<Integer> ord=null;
        try {
            ord = topSort(numNodes, deps);
            System.out.println("dependencies: " + deps);
            System.out.println("order: " + ord);
            Assertions.assertThat(checkOrdering(ord, deps)).isTrue();
          } catch (CyclicDependenciesException e) {
            System.out.println("Cyclic dependencies!!!");
            Assertions.assertThat(TSHelpers.aCyclic(deps)).isFalse();
        }
    }

    // if dependencies acyclic, then computed ordering does respect all dependencies; else 'CyclicDependenciesException' is thrown
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering1c (@ForAll("dependencyListsCyclesPossible2") List<List<Integer>> deps) {
//        void propCheckComputedOrdering1c (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> DependencyL) {
        if (aCyclic(deps)) {
            System.out.println("dependencies (acyclic): " + deps);
            List<Integer> ord = topSort(numNodes, deps);
            System.out.println("order: " + ord);
            Assertions.assertThat(checkOrdering(ord, deps)).isTrue();
        }
        else {
            System.out.println("dependencies (cyclic): " + deps);
            System.out.println("exception thrown");
            Assertions.assertThatExceptionOfType(CyclicDependenciesException.class).isThrownBy(() -> {
                topSort(numNodes, deps);
            }).withMessageContaining("Cyclic dependencies");
        }
    }

    // if i depends on j (i.e., dependency list contains [i,j]), then j will appear before i in the ordering computed
    // problem: too many rejections (9K out of 10K)
    // fix: grab pair from generated dependency list, instead of generating it randomly
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering2a (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
//    void propCheckComputedOrdering2a (@ForAll("dependencyListsCyclesPossible2") List<List<Integer>> deps,
                                     @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                                     @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
//        int numNodes = 4;
        Assume.that(deps.contains(Arrays.asList(i,j)));
        try {
            List<Integer> ord = topSort(numNodes, deps);
            int indexOfI = ord.indexOf(i);
            int indexOfJ = ord.indexOf(j);
            Assertions.assertThat(indexOfJ).isLessThan(indexOfI);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
    }

    // if i depends on j (i.e., dependency list contains [i,j]), then j will appear before i in the ordering computed
    // works (but still 2800/10000 rejections, i.e., out of 10K lists, 2800 are empty; fix: change generator to avoid generation of empty lists)
    @Property
    @Report(Reporting.GENERATED)
    void propCheckComputedOrdering2b (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps) {
//        int numNodes = 4;
        Assume.that(!deps.isEmpty());
        List<Integer> pair = deps.get(0);
        List<Integer> ord=null;
        try {
            ord = topSort(numNodes, deps);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        int indexOfI = ord.indexOf(pair.get(0));
        int indexOfJ = ord.indexOf(pair.get(1));
        Assertions.assertThat(indexOfJ).isLessThan(indexOfI);
    }


// TESTING 'topSort' USING OTHER OPERATIONS: Main properties ===========================================================

    // P_{add1}: "after adding dependency (i,j), either topSort will throw 'CyclicDepsException' or j will appear before i in the computed ordering
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency1 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
        Assume.that(i != j);
        List<List<Integer>> deps1 = addDependency(i, j, deps);
        if (aCyclic(deps1)) {
            List<Integer> ord = topSort(numNodes, deps1);
            int indexOfI = ord.indexOf(i);
            int indexOfJ = ord.indexOf(j);
            Assertions.assertThat(indexOfJ).isLessThan(indexOfI);
        } else {
            Assertions.assertThatExceptionOfType(CyclicDependenciesException.class).isThrownBy(() -> {
                topSort(numNodes, deps1);
            }).withMessageContaining("Cyclic dependencies");
        }
    }


     // P_{add2}: "if ord is ordering of deps and i before j in ord, then ord is not an ordering of addDep(i,j,deps)
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency3 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
        Assume.that(i != j);
        Assume.that(!deps.contains(Arrays.asList(i,j)));
        Assume.that(aCyclic(deps));
        List<Integer> ord = topSort(numNodes, deps);
        int indexOfI = ord.indexOf(i);
        int indexOfJ = ord.indexOf(j);
        if (indexOfI < indexOfJ) {
            deps = addDependency(i, j, deps);
            Assertions.assertThat(checkOrdering(ord, deps)).isFalse();
        }
        else {
            deps = addDependency(j, i, deps);
            Assertions.assertThat(checkOrdering(ord, deps)).isFalse();
        }
    }

    // P_{add3}: "if ord is ordering of deps and i before j in ord, then ord is not an ordering of addDep(i,j,deps)"
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency4 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer j,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer k) {
        Assume.that(deps.contains(Arrays.asList(i,j)));
        Assume.that(deps.contains(Arrays.asList(j,k)));
        Assume.that(!deps.contains(Arrays.asList(i,k)));
        List<Integer> ord = topSort(numNodes, deps);
        deps = addDependency(i, k, deps);
        Assertions.assertThat(checkOrdering(ord, deps)).isTrue();
    }

    // P_{add4}: "adding and then removing a new dependency (i,j) does not invalidate a previously found ordering"
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency5 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
        Assume.that(!deps.contains(Arrays.asList(i,j)));
        List<Integer> ord = topSort(numNodes, deps);
        deps = addDependency(i, j, deps);
        deps = removeDependency(i, j, deps);
        Assertions.assertThat(checkOrdering(ord, deps)).isTrue();
    }

    // P_{rem1}: "removing a dependency preserves the correctness of an ordering"
    @Property
    @Report(Reporting.GENERATED)
    void propRemDependency (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                            @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                            @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
        List<Integer> ord = topSort(numNodes, deps);
        deps = removeDependency(i, j, deps);
        Assertions.assertThat(checkOrdering(ord, deps)).isTrue();
    }

// TESTING 'topSort' USING OTHER OPERATIONS: Additional properties ===========================================================

    // property: if dependencies acyclic and adding (i,j) makes them cyclic, then i is reachable from j in DL
    // assumes that dimensions of generated matrices are consistent with generated indices
    @Property
    @Report(Reporting.GENERATED)
    void propAddDependency2 (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                             @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
        // Assume.that(TSHelpers.aCyclic(DependencyM));
//        int numNodes = 4;
        List<List<Integer>> deps0 = TSHelpers.cloneL(deps);
        deps = addDependency(i, j, deps);
        if (!aCyclic(deps)) {
            System.out.println("Dependencies after add" + toStringSorted(deps));
            Assertions.assertThat(reachable(j, i, numNodes, deps0));
        }
    }

    // If i and j are independent and o is an ordering, then reversing the order of i and j in o also is an ordering
    // fails
    @Property
    @Report(Reporting.GENERATED)
    void propComputeOrderingAndIndependence (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> depsL,
                                             @ForAll @IntRange(min=0, max=numNodes-1) Integer i,
                                             @ForAll @IntRange(min=0, max=numNodes-1) Integer j) {
        Assume.that(i != j);
        Assume.that(independent(i, j, numNodes, depsL));
        Assume.that(aCyclic(depsL));
        List<Integer> ord=null;
        try {
            ord = topSort(numNodes, depsL);
        }
        catch (CyclicDependenciesException e) {
            System.out.println("Cyclic!");
        }
        System.out.println("Old ordering: " + ord);
        int indexOfI = ord.indexOf(i);
        int indexOfJ = ord.indexOf(j);
        ord.set(indexOfI, j);
        ord.set(indexOfJ, i);
        System.out.println("New ordering: " + ord);
        Assertions.assertThat(checkOrdering(ord, depsL)).isFalse();  // should also fail, but produce instance were it works
    }

// USE GENERATOR TO 'COMPUTE' SOLUTION ===========================================================

    // use Jqwik's test case generation to 'compute' a solution
    // out of 10K tries, about 5K rejected
    @Property
    @Report(Reporting.GENERATED)
    void propGenerateSolution (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps,
                               @ForAll("orderings") List<Integer> ord) {
//        Assume.that(aCyclic(deps));
//        Assume.that(numNodes == ord.size());
        Assume.that(checkOrdering(ord,deps) == true);
        System.out.println("Dependencies: " + toStringSorted(deps));
        System.out.println("Generated solution: " + ord);
    }

// GENERATORS ===========================================================

    // As 'dependencyListsCyclesPossible3', but also generated lists will not contain:
    // - circles of any length
    // (w/ 4 nodes, 0 of 10K lists contain a cycle)
    @Provide
    public Arbitrary<List<List<Integer>>> dependencyListsWithoutCycles() {
        final int maxPairs = numNodes*numNodes;
        final int minPairs = 0;
        Arbitrary<Integer> num = Arbitraries.integers().between(0,numNodes-1);
//        Arbitrary<List<Integer>> tuples = num.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        Arbitrary<List<Integer>> tuples = num.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        return tuples.list()
                .ofMinSize(minPairs)
                .ofMaxSize(maxPairs)
                .uniqueElements()
                .filter(ll -> aCyclic(ll));
    }

    // As 'dependencyListsCyclesPossible2', but also generated lists will not contain:
    // - circles of length 2, i.e., dependencies [i,j] and [j,i]
    // (w/ 4 nodes, 700 of 10K lists contain a cycle)
    @Provide
    public Arbitrary<List<List<Integer>>> dependencyListsCyclesPossible3() {
        final int maxPairs = numNodes*numNodes;
        Arbitrary<Integer> nodes = Arbitraries.integers().between(0,numNodes-1);
        Arbitrary<List<Integer>> pairs = nodes.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        return
                pairs.list()
                        .ofMinSize(0)
                        .ofMaxSize(maxPairs)
                        .uniqueElements()
                        .filter(depL -> {
                            int i, j;
                            List<Integer> revPair = new ArrayList<>();
                            for (int ind = 0; ind < depL.size(); ind++) {
                                i = depL.get(ind).get(0);
                                j = depL.get(ind).get(1);
                                revPair = Arrays.asList(j,i);
                                if (depL.contains(revPair))
                                    return false;
                            }
                            return true;
                        });
    }

    // As 'dependencyListsCyclesPossible1', but also generated lists will not contain:
    // - circles of length 1, i.e., dependencies [i,i]
    // (w/ 4 nodes, 6600 of 10K lists contain a cycle)
    @Provide
    public Arbitrary<List<List<Integer>>> dependencyListsCyclesPossible2() {
//        final int numNodes = 4;
//        final int maxPairs = numNodes*numNodes;
        final int maxPairs = numNodes;
        Arbitrary<Integer> nodes = Arbitraries.integers().between(0,numNodes-1);
        Arbitrary<List<Integer>> pairs = nodes.list().ofSize(2).uniqueElements().filter(t -> t.get(0)!=t.get(1));
        return pairs.list()
                .ofMinSize(0)
                .ofMaxSize(maxPairs)
                .uniqueElements();
    }

    // Generates lists of pairs [..., [i,j], ...] where i, j are natural number between 0 and numNodes-1 representing nodes
    // Pair [i,j] means that node i depends on node j.
    // Generated lists will not contain: duplicate pairs.
    // (w/ 4 nodes, 6500 of 10K lists contain a cycle)
    @Provide
    public Arbitrary<List<List<Integer>>> dependencyListsCyclesPossible1() {
//        final int numNodes = 4;
//        final int maxPairs = numNodes*numNodes;
        final int maxPairs = numNodes;
        Arbitrary<Integer> nodes = Arbitraries.integers().between(0,numNodes-1);
//        Arbitrary<List<Integer>> pairs = nodes.list().ofSize(2).uniqueElements();
        Arbitrary<List<Integer>> pairs = nodes.list().ofSize(2);
        return pairs.list()
                .ofMinSize(0)
                .ofMaxSize(maxPairs)
                .uniqueElements();
    }

    // generates candidate orderings, i.e., lists containing the numbers 0 to numNodes-1 in some random order
    @Provide
    public Arbitrary<List<Integer>> orderings() {
//        int numNodes = 4;
        Arbitrary<Integer> nodes = Arbitraries.integers().between(0,numNodes-1);
        Arbitrary<List<Integer>> nodeList = nodes.list().uniqueElements().ofSize(numNodes);
        return nodeList;
    }

// GENERATORS: Properties to test the generators ===========================
    @Property
    @Report(Reporting.GENERATED)
//    void propGeneratorCheck (@ForAll("dependencyListsCyclesPossible1") List<List<Integer>> deps) {
//    void propGeneratorCheck (@ForAll("dependencyListsCyclesPossible2") List<List<Integer>> deps) {
//    void propGeneratorCheck (@ForAll("dependencyListsCyclesPossible3") List<List<Integer>> deps) {
    void propGeneratorSanityCheck (@ForAll("dependencyListsWithoutCycles") List<List<Integer>> deps) {
//        Assume.that(aCyclic(deps));
//        Assertions.assertThat(aCyclic(deps));
        if (aCyclic(deps))
            System.out.println("does NOT have cycle: " + toStringSorted(deps));
        else
            System.out.println("does have cycle: " + toStringSorted(deps));
    }

// BONUS ==================================================================

    // generates square adjacency matrices containing 0 or 1 w/o cycles that involve 0 or 1 intermediate nodes
    // dimension of the matrix is between MinNumNodes and MaxNumNodes
    // for dimension=2, generates 16 acyclic matrices (exhaustive)
    // for dimension=3, generates 512 matrices of which 464 are acyclic (exhaustive)
    // for dimension=4, generates 65536 matrices of which 45536 are acyclic (exhaustive at 100K tries)
    @Provide
    public static Arbitrary<Integer[][]> dependencyMatrices() {
        final int MinNumNodes = 4;
        final int MaxNumNodes = 4;
        //       final int NumNodes = 5;  // NhumNodes=4 needs 100K tries for exhaustive search
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
    public Arbitrary<List<List<Integer>>> dependencyLists3() {
        Arbitrary<Integer> nodes= Arbitraries.integers().between(0,1);
        //	Arbitrary<List<Integer>> numList = num.list().ofSize(3);
        Arbitrary<List<Integer>> nodeList = nodes.list().ofMinSize(1).ofMaxSize(3);
        return nodeList.list().ofMinSize(1).ofMaxSize(3).filter(ll -> {
            int len = ll.get(0).size();
            return ll.stream().allMatch(l -> ll.size()==len && l.size()==len);});
    }

// =============== Tests ==================================================================
    @Property
    public void propertyCheckListGenerator (@ForAll("depencencyLists1") List<List<Integer>> depsL) {
        System.out.println(TopSort.toStringSorted(depsL));
//        Assertions.assertThat(ll.size()).isLessThan(7); // holds
    }
    @Property
    @Report(Reporting.GENERATED)
    public void aProp1(@ForAll @Positive @UniqueElements Integer i) {
        Assertions.assertThat(i).isLessThan(100000);
    }
    @Property
    @Report(Reporting.GENERATED)
    public void aProp2(@ForAll("fourMultiples") Integer i) {
        Assertions.assertThat(i%2==0 && (i<91 || i>95)).isTrue();
    }
    @Provide
    public Arbitrary<Integer> fourMultiples() {
        return Arbitraries.integers().between(0,Integer.MAX_VALUE).filter(n -> n%4 == 0);
    }
}
