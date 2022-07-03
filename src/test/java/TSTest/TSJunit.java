package TSTest;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static TSImpl.TopSort.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TSJunit {
    @Test
    public void testAdd() {
       Assertions.assertThat(Integer.sum(19, 23)).isEqualTo(41);
    }

    @Test
//    void exampleComputeOrderingTest() throws CyclicDependenciesException {
    void topSortTest1() {
        // dependencies: [[1,0], [2,1]]
        List<List<Integer>> deps = new ArrayList<>(Arrays.asList(
                Arrays.asList(1,0),
                Arrays.asList(2,1)
        ));
        System.out.println("dependencies: " + toStringSorted(deps));
        List<Integer> ord = computeOrdering(3, deps);
        System.out.println("ordering: " + ord);
        List<Integer> expected = new ArrayList<>(Arrays.asList(0,1,2));
        Assertions.assertThat(ord).isEqualTo(expected);
    }

    @Test
//    void exampleComputeOrderingTest() throws CyclicDependenciesException {
    void topSortTest2() {
        // dependencies: [[1,0], [2,1], [0,2]]
        List<List<Integer>> deps = new ArrayList<>(Arrays.asList(
                Arrays.asList(1,0),
                Arrays.asList(2,1),
                Arrays.asList(0,2)
        ));
        System.out.println("Dependencies: " + toStringSorted(deps));
        Assertions.assertThatExceptionOfType(CyclicDependenciesException.class).isThrownBy(() -> {
                    List<Integer> ordering = computeOrdering(3, deps);}).withMessageContaining("Cyclic dependencies");
    }

    @ParameterizedTest
    @MethodSource("dependenciesAndOrderingProvider")
    void topSortTest3(List<List<Integer>> input, List<Integer> expected) {
        System.out.println("Dependencies: " + toStringSorted(input));
        List<Integer> ordering = computeOrdering(3, input);
        System.out.println("Ordering: " + ordering);
        Assertions.assertThat(ordering).isEqualTo(expected);
    }
    static Stream<Arguments> dependenciesAndOrderingProvider() {
        List<Integer> d10 = Arrays.asList(1,0);
        List<Integer> d21 = Arrays.asList(2,1);
        List<Integer> d20 = Arrays.asList(2,0);
        return Stream.of(
                arguments(Arrays.asList(d10,d21), Arrays.asList(0,1,2)),
                arguments(Arrays.asList(d10,d21,d20), Arrays.asList(0,1,2)),
                arguments(Arrays.asList(d10,d20), Arrays.asList(0,1,2))
        );
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    void topSortTest4() {
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5); // finite stream
        List squares = nums.stream().map(x -> x*x).collect(Collectors.toList());
        System.out.println(squares);

        Stream<Integer> evens = Stream.iterate(0, n -> n+2).limit(20000);  // bounded infinite stream
        evens.forEach(n -> System.out.print(n+" "));
    }
}
