/* CISC/CMPE 422/835
 * Example-based testing of TopSort with Jqwik using '@Example'
 */
package TSTest;

import net.jqwik.api.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;

import static TSImpl.TopSort.*;

public class TSExamples {

    @Example
    void exampleACyclicTest1() {
        // dependencies are: ((0,2), (0,1), (1,2), (2,0))
        List<List<Integer>> depsL1 = new ArrayList<>(Arrays.asList(
                Arrays.asList(0,2),
                Arrays.asList(0,1),
                Arrays.asList(1,2),
                Arrays.asList(2,0)
        ));
        System.out.println(toStringSorted(depsL1));
        Assertions.assertThat(TSHelpers.aCyclic(depsL1)).isFalse();
    }

    @Example
    void exampleACyclicTest2() {
        // dependencies are: ((0,1), (1,2), (2,3), (0,3))
        List<List<Integer>> depsL2 = new ArrayList<>(Arrays.asList(
                Arrays.asList(0,1),
                Arrays.asList(1,2),
                Arrays.asList(2,3),
                Arrays.asList(0,3)
        ));
        System.out.println(toStringSorted(depsL2));
        Assertions.assertThat(TSHelpers.aCyclic(depsL2)).isTrue();
    }

    @Example
    void exampleTopSortTest1() {
        // dependencies: ((2,0), (2,1), (2,3), (3,1), (3,2))
        List<List<Integer>> depL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1),
                Arrays.asList(3,2)
        ));
        System.out.println("Dependencies: " + toStringSorted(depL));
        List<Integer> ordering = topSort(4, depL);
        System.out.println("Ordering: " + ordering);
    }

    @Example
    void exampleTopSortTest2() {
        // dependencies: ((2,0), (2,1), (2,3), (3,1))
        List<List<Integer>> depL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        System.out.println("Dependencies: " + toStringSorted(depL));
        List<Integer> ordering = topSort(4, depL);
        System.out.println("Ordering: " + ordering);
        List<Integer> expected = new ArrayList<>(Arrays.asList(0,1,3,2));
        Assertions.assertThat(ordering).isEqualTo(expected);
    }


    @Example
    void exampleTopSortTest3() {
        // dependencies: ((2,0), (2,1), (2,3), (3,1), (3,2))
        List<List<Integer>> depL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1),
                Arrays.asList(3,0)
        ));
        System.out.println("Dependencies: " + toStringSorted(depL));
        List<Integer> ordering;
        try {
            ordering = topSort(4, depL);
        }
        catch (CyclicDependenciesException c) {
            System.out.println("Cyclic");
            return;
        }
        System.out.println("Ordering: " + ordering);
        TSHelpers.checkOrdering(ordering, depL);
    }

    // Examples involving the generator for orderings ====================================================
    // Use generator to implement "generate & check" paradigm (cf, Prolog)

    // Use ordering generator to 'guess' topological sort of: [[2,0],[2,1],[2,3],[3,1]]
    @Example
    void exampleTopSortByGuessing() {
        List<List<Integer>> deps = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        List<Integer> ords;
        ords = (new TSProperties()).orderings().filter(ordering -> TSHelpers.checkOrdering(ordering,deps)).sample();
        System.out.println("Ordering found for " + toStringSorted(deps) + ":");
        System.out.println(ords);
    }

    // Use ordering generator to compute all correct orderings of: [[2,0],[2,1],[2,3]]
    @Example
    void exampleCollectAllOrderings1() {
        List<List<Integer>> deps = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3)
        ));
        List<List<Integer>> ords = new ArrayList<>();
        (new TSProperties()).orderings().filter(o -> TSHelpers.checkOrdering(o,deps)).forEachValue(o -> ords.add(o));
        System.out.println(ords.size() + " orderings found for " + toStringSorted(deps) + ":");
        System.out.println(ords);
    }

    // Use ordering generator to compute all correct orderings of: [[2,0],[2,1],[2,3],[3,1]]
    @Example
    void exampleCollectAllOrderings2() {
        List<List<Integer>> deps = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        List<List<Integer>> ords = new ArrayList<>();
        (new TSProperties()).orderings().filter(o -> TSHelpers.checkOrdering(o,deps)).forEachValue(o -> ords.add(o));
        System.out.println(ords.size() + " orderings found for " + toStringSorted(deps) + ":");
        System.out.println(ords);
    }

    // Use ordering generator to compute number of correct orderings of: [[2,0],[2,1],[2,3],[3,1]]
    @Example
    void exampleCountNumberOfOrderings() {
        List<List<Integer>> deps = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        List<List<Integer>> ords = new ArrayList<>();
        (new TSProperties()).orderings().filter(o -> TSHelpers.checkOrdering(o,deps)).forEachValue(ords::add);
        System.out.println(toStringSorted(deps) + " has " + ords.size() + " orderings");
    }

    // Example showing how to collect all generated values satisfying a certain property
    @Example
    void exampleCollectAllSolutions() {
        Arbitrary<Integer> nums = Arbitraries.integers().between(0,9);
        List<String> l = new ArrayList<>();
        nums.filter(n -> n%2==0).forEachValue(n -> l.add(n.toString()));
        System.out.println(l);
    }

}
