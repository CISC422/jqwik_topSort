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
//      dependencies are: ((0,2), (0,1), (1,2), (2,0))
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
//      dependencies are: ((0,1), (1,2), (2,3), (0,3))
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
//    void exampleComputeOrderingTest() throws CyclicDependenciesException {
    void exampleComputeOrderingTest1() {
        // dependencies: ((2,0), (2,1), (2,3), (3,1), (3,2))
        List<List<Integer>> depL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1),
                Arrays.asList(3,2)
        ));
        System.out.println("Dependencies: " + toStringSorted(depL));
        List<Integer> ordering = computeOrdering(4, depL);
        System.out.println("Ordering: " + ordering);
    }

    @Example
//    void exampleComputeOrderingTest() throws CyclicDependenciesException {
    void exampleComputeOrderingTest2() {
        // dependencies: ((2,0), (2,1), (2,3), (3,1))
        List<List<Integer>> depL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        System.out.println("Dependencies: " + toStringSorted(depL));
        List<Integer> ordering = computeOrdering(4, depL);
        System.out.println("Ordering: " + ordering);
        List<Integer> expected = new ArrayList<>(Arrays.asList(0,1,3,2));
        Assertions.assertThat(ordering).isEqualTo(expected);
    }


    @Example
//    void exampleComputeOrderingTest() throws CyclicDependenciesException {
    void exampleComputeOrderingTest3() {
        // dependencies: ((2,0), (2,1), (2,3), (3,1), (3,2))
        List<List<Integer>> depL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1),
                Arrays.asList(3,0)
        ));
        System.out.println("Dependencies: " + toStringSorted(depL));
        List<Integer> ordering=null;
        try {
            ordering = computeOrdering(4, depL);
        }
        catch (CyclicDependenciesException c) {
            System.out.println("Cyclic");
            return;
        }
        System.out.println("Ordering: " + ordering);
        TSHelpers.checkOrdering(ordering, depL);
    }

    @Example
    void exampleCollectAllOrderings1() {
//        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,0,0,0}};
        List<List<Integer>> myDepL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3)
        ));
        List<List<Integer>> orderings = new ArrayList<>();
        (new TSProperties()).orderings().filter(o -> TSHelpers.checkOrdering(o,myDepL)).forEachValue(o -> orderings.add(o));
        System.out.println(orderings.size() + " orderings found for " + toStringSorted(myDepL) + ":");
        System.out.println(orderings);
    }

    @Example
    void exampleCollectAllOrderings2() {
//  dependencies: ((2,0), (2,1), (2,3), (3,1))
        List<List<Integer>> myDepL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        List<List<Integer>> orderings = new ArrayList<>();
        (new TSProperties()).orderings().filter(o -> TSHelpers.checkOrdering(o,myDepL)).forEachValue(o -> orderings.add(o));
        System.out.println(orderings.size() + " orderings found for " + toStringSorted(myDepL) + ":");
        System.out.println(orderings);
    }

    @Example
    void exampleComputeOrderingByGuessing() {
//        Integer[][] DependencyM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
        List<List<Integer>> DependencyL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        List<Integer> orderings;
        orderings = (new TSProperties()).orderings().filter(ordering -> TSHelpers.checkOrdering(ordering,DependencyL)).sample();
        System.out.println("Ordering found for " + toStringSorted(DependencyL) + ":");
        System.out.println(orderings);
    }

    @Example
    void exampleCountNumberOfOrderings() {
        //        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
//        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,0,0,0}};
        List<List<Integer>> DependencyL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1)
        ));
        List<List<Integer>> orderings = new ArrayList<>();
        (new TSProperties()).orderings().filter(o -> TSHelpers.checkOrdering(o,DependencyL)).forEachValue(orderings::add);
        System.out.println(toStringSorted(DependencyL) + " has " + orderings.size() + " orderings");
    }

    // === Extra ====================================================
    @Example
    void exampleCollectAllSolutions() {
        Arbitrary<Integer> nums = Arbitraries.integers().between(0,9);
        List<String> l = new ArrayList<>();
        nums.filter(n -> n%2==0).forEachValue(n -> l.add(n.toString()));
        System.out.println(l);
    }

}
