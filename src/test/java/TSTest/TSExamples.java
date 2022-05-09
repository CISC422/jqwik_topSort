package TSTest;

import net.jqwik.api.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;

import static TSImpl.TopSort.*;

public class TSExamples {

    @Example
    void exampleACyclicTest() {
//        Integer[][] DepL1 = {{0,1,0},{0,0,1},{1,0,0}};
        List<List<Integer>> myDepL = new ArrayList<>(Arrays.asList(
                Arrays.asList(0,2),
                Arrays.asList(0,1),
                Arrays.asList(1,2),
                Arrays.asList(2,0)
        ));
        System.out.println(toStringSorted(myDepL));
        Assertions.assertThat(TSHelpers.aCyclic(myDepL)).isFalse();
    }

    @Example
    void exampleComputeOrderingTest() throws CyclicDependenciesException {
//        Integer[][] myDM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
        List<List<Integer>> myDepL = new ArrayList<>(Arrays.asList(
                Arrays.asList(2,0),
                Arrays.asList(2,1),
                Arrays.asList(2,3),
                Arrays.asList(3,1),
                Arrays.asList(3,2)
        ));
        System.out.println("Dependencies: " + toStringSorted(myDepL));
        List<Integer> ordering = computeOrdering(4, myDepL);
        System.out.println("Ordering: " + ordering);
    }

    @Example
    void exampleCollectAllOrderings() {
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
