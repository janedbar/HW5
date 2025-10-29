/******************************************************************
 *
 *   Jane Dunbar  / Section 002
 *
 *   This java file contains the problem solutions of isSubset, findKthLargest,
 *   and sort2Arrays methods. You should utilize the Java Collection Framework for
 *   these methods.
 *
 ********************************************************************/

import java.util.*;

class ProblemSolutions {

    /**
     * Method: isSubset()
     *
     * Given two arrays of integers, A and B, return whether
     * array B is a subset if array A.
     */
    public boolean isSubset(int list1[], int list2[]) {
        // Put elements of list1 into a HashSet for O(1) membership tests
        HashSet<Integer> set = new HashSet<>();
        for (int v : list1) {
            set.add(v);
        }
        // Check every element of list2 is in the set
        for (int v : list2) {
            if (!set.contains(v)) return false;
        }
        return true;
    }

