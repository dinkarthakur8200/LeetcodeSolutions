class Solution {

    static class State {
        long score;
        int[] indices;

        State(long score, int[] indices) {
            this.score = score;
            this.indices = indices;
        }
    }

    public int[] maximumWeight(List<List<Integer>> intervals) {

        int n = intervals.size();

        // [left, right, weight, originalIndex]
        int[][] arr = new int[n][4];

        for (int i = 0; i < n; i++) {
            arr[i][0] = intervals.get(i).get(0);
            arr[i][1] = intervals.get(i).get(1);
            arr[i][2] = intervals.get(i).get(2);
            arr[i][3] = i;
        }

        // Sort by ending point
        Arrays.sort(arr, (a, b) -> {
            if (a[1] != b[1]) {
                return Integer.compare(a[1], b[1]);
            }

            return Integer.compare(a[0], b[0]);
        });

        /*
         * previous[i] = index of the last interval
         * whose ending point is strictly smaller than
         * arr[i]'s starting point.
         */
        int[] previous = new int[n];

        for (int i = 0; i < n; i++) {

            int low = 0;
            int high = i - 1;
            int answer = -1;

            while (low <= high) {

                int mid = low + (high - low) / 2;

                if (arr[mid][1] < arr[i][0]) {
                    answer = mid;
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            previous[i] = answer;
        }

        /*
         * dp[i][k] =
         * best answer using first i intervals
         * and selecting at most k intervals.
         */
        State[][] dp = new State[n + 1][5];

        // Base case: selecting 0 intervals gives score 0.
        for (int i = 0; i <= n; i++) {
            for (int k = 0; k <= 4; k++) {
                dp[i][k] = new State(0, new int[0]);
            }
        }

        // Fill DP table
        for (int i = 1; i <= n; i++) {

            int current = i - 1;

            for (int k = 1; k <= 4; k++) {

                // Option 1: Don't take current interval
                State skip = dp[i - 1][k];

                // Option 2: Take current interval
                int prev = previous[current];

                State before;

                if (prev == -1) {
                    before = dp[0][k - 1];
                } else {
                    before = dp[prev + 1][k - 1];
                }

                int[] selected = Arrays.copyOf(
                        before.indices,
                        before.indices.length + 1);

                selected[selected.length - 1] = arr[current][3];

                /*
                 * IMPORTANT:
                 * The answer must be lexicographically smallest
                 * according to original indices.
                 *
                 * So keep indices sorted.
                 */
                Arrays.sort(selected);

                State take = new State(
                        before.score + arr[current][2],
                        selected);

                // Choose the better state
                dp[i][k] = better(skip, take);
            }
        }

        return dp[n][4].indices;
    }

    /*
     * Compare two states.
     *
     * 1. Larger score is better.
     * 2. If scores are equal, lexicographically smaller
     * index array is better.
     */
    private State better(State a, State b) {

        if (a.score > b.score) {
            return a;
        }

        if (b.score > a.score) {
            return b;
        }

        if (lexicographicallySmaller(a.indices, b.indices)) {
            return a;
        }

        return b;
    }

    /*
     * Returns true if a is lexicographically smaller than b.
     */
    private boolean lexicographicallySmaller(int[] a, int[] b) {

        int length = Math.min(a.length, b.length);

        for (int i = 0; i < length; i++) {

            if (a[i] != b[i]) {
                return a[i] < b[i];
            }
        }

        // If one is a prefix of the other,
        // the shorter array is lexicographically smaller.
        return a.length < b.length;
    }
}