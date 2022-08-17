import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (prerequisites.length <= 1) return true;

        Map<Integer, Set<Integer>> courseAndNeedList = new HashMap<>();

        for (int[] ints : prerequisites) {

                if (courseAndNeedList.containsKey(ints[0])) {
                    courseAndNeedList.get(ints[0]).add(ints[1]);
                } else {
                    Set<Integer> needSet = new HashSet<>();
                    needSet.add(ints[1]);
                    courseAndNeedList.put(ints[0], needSet);
                }
        }
        for (int[] prerequisite : prerequisites) {

                boolean checkResult = checkCourse(prerequisites, new HashSet<>(), new HashSet<>(), courseAndNeedList, prerequisite[0]);
                if (checkResult == false) {
                    return false;
                }
        }

        return true;
    }

    public boolean checkCourse(int[][] prerequisites, Set<Integer> canReach, Set<Integer> checking, Map<Integer, Set<Integer>> courseAndNeedList, int course) {
        if (canReach.contains(course) || !courseAndNeedList.containsKey(course)) return true;
        if (checking.contains(course)) return false;

        Set<Integer> needCourse = courseAndNeedList.get(course);
        checking.add(course);
        for (int nextCheck : needCourse) {
            boolean checkResult = checkCourse(prerequisites, canReach, checking, courseAndNeedList, nextCheck);
            if (checkResult == false) {
                if (checking.contains(course))
                    System.out.println(checking);
                checking.remove(course);
                return false;
            }
        }

        canReach.add(course);
        checking.remove(course);
        return true;
    }
}