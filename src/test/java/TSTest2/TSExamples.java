package TSTest2;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static TSImpl.TopSort.*;

public class TSExamples {

/*
    @Example
    void exampleACyclicTest() {
//        Integer[][] myDM = {{0,1,0},{0,0,1},{1,0,0}};
        List<List<Integer>> DepL1 = new ArrayList<>(Arrays.asList(
                Arrays.asList(0,1),
                Arrays.asList(1,2),
                Arrays.asList(2,0))
        );
        System.out.println(toStringSorted(DepL1));
      //  Assertions.assertThat(TSHelpers.aCyclic(DepL1)).isFalse();
    }

    @Example
    void exampleComputeOrderingTest() throws CyclicDependenciesException {
        Integer[][] myDM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
        int numNodes = 4;
        List<Integer> out = computeOrdering(numNodes, myDM);
        System.out.println(out);
    }

    @Example
    void exampleCollectAllSolutions() {
        Arbitrary<Integer> nums = Arbitraries.integers().between(0,9);
        List<String> l = new ArrayList<>();
        nums.filter(n -> n%2==0).forEachValue(n -> l.add(n.toString()));
        System.out.println(l);
    }

    @Example
    void exampleCollectAllOrderings() {
        //        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,0,0,0}};
        List<List<Integer>> ll = new ArrayList<>();
        TSProperties.orderings().filter(o -> TSHelpers.checkOrdering(o,DM)).forEachValue(o -> ll.add(o));
        System.out.println(ll.size() + " orderings found for " + Arrays.deepToString(DM) + ":");
        System.out.println(ll);
    }

    @Example
    void exampleComputeOrderingByGuessing() {
        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
        List<Integer> l = new ArrayList<>();
        l = TSProperties.orderings().filter(o -> TSHelpers.checkOrdering(o,DM)).sample();
        System.out.println("Ordering found for " + Arrays.deepToString(DM) + ":");
        System.out.println(l);
    }

    @Example
    void exampleCountNumberOfOrderings() {
        //        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,1,0,0}};
        Integer[][] DM = {{0,0,0,0},{0,0,0,0},{1,1,0,1},{0,0,0,0}};
        List<List<Integer>> ll = new ArrayList<>();
        TSProperties.orderings().filter(o -> TSHelpers.checkOrdering(o,DM)).forEachValue(ll::add);
        System.out.println(Arrays.deepToString(DM) + " has " + ll.size() + " orderings");
    }
*/

}
