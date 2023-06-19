# TopSort
Sample code for property-based testing with Jqwik. See CISC 422 course notes for details.

Notes:
- example tests provide pretty good coverage 
- e.g., 'exampleTopSortTest2': 'topSort' and ''findUnmarked' full coverage; only leaves 1 line (of 10) and 1 branch (of 8) in 'visit' unexecuted; having to do w/ cycles
- 'exampleTopSortTest2' and 'exampleTopSortTest3': cover all lines and all branches
BUG 2:
- despite full coverage, example-based tests do not find bug 2; this is because the input does not trigger the bug; in terms of the RIPR model, there is reachability, but no infection
- properties P1 and P3 never fail, because the bug does not cause the property it checks for to be violated (reachability, infection, propagation, but no reveal)
- property P2 fails (even for #tries = 10) and property P4 fails (for, e.g., #tries = 100): reachabity, infection, propagation, reveal

- PBT helps here in two ways:
-- support for testing large number of different inputs, increasing the likelihood of infection
- emphasis properties makes it more likely that developer/tester comes up with properties that are sufficiently comprehensive to ensure reveal
- reachability not so much