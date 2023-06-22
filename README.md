# CISC/CMPE 422/835: Formal Methods in Software Engineering
## Property-based testing with Jqwik: Topological sort

Sample code for property-based testing with Jqwik. See CISC/CMPE 422/835 course 
notes for details.

### Example-based tests
The tests in [`TSExamples.java`](src/test/java/TSTest/TSExamples.java) provide 
pretty good coverage.
E.g., [`exampleTopSortTest2`](src/test/java/TSTest/TSExamples.java#L57-L72) 
provides 100% line and branch coverage of methods `topSort` and `findUnmarked`, and
only leaves 1 line (of 10) and 1 branch (of 8) in method `visit` unexecuted.
Together, `exampleTopSortTest2` and `exampleTopSortTest3` cover all lines and 
branches of `topSort`, `findUnmarked`, and `visit`. 

### Example-based tests and Bug 2
Despite full coverage, [`Bug 2`](src/main/java/TSImpl/TopSort.java#L47) does 
not cause any of the example-based tests to fail. 
This is because the inputs do not trigger the bug. In terms of the RIPR model, 
there is reachability, 
but no infection. 

### Property-based tests and Bug 2
Properties [`P1`](src/test/java/TSTest/TSProperties.java#L26-L39) and 
[`P3`](src/test/java/TSTest/TSProperties.java#L62-L70) never fail for Bug 2, 
because the bug does not cause the property they check 
for to be violated (reachability, infection, propagation, but no reveal). 
However, property [`P2`](src/test/java/TSTest/TSProperties.java#L41-L60) 
fails (even for 
just 10 tries) and property [`P4`](src/test/java/TSTest/TSProperties.java#L72-L81) 
fails 
(for, e.g., 100 tries).

So, in this example, PBT helps in two ways:
- support for testing large number of different inputs, increasing the likelihood of reachability, infection, and propagation
- emphasis properties makes it more likely that tester comes up with properties that are sufficiently comprehensive to ensure reveal
