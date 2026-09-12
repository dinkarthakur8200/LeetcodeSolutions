# Maximum Weight of Non-Overlapping Intervals

## Problem

Given a list of intervals where each interval contains:

```text
[left, right, weight]
```

We have to choose **at most 4 non-overlapping intervals** such that:

1. The total weight is maximum.
2. Two intervals are non-overlapping only when:

```text
previous.right < current.left
```

3. If multiple selections have the same maximum weight, return the **lexicographically smallest array of original indices**.

---

## Pattern

**Weighted Interval Scheduling + Binary Search + Dynamic Programming + Lexicographical Tie-Breaking**

The overall pattern is:

```text
Sort by Ending Time
        ↓
Binary Search
        ↓
Find Previous Compatible Interval
        ↓
DP with at most 4 selections
        ↓
Maximum Weight
        ↓
Lexicographically Smallest Indices
```

---

## Key Observation

After sorting intervals by their ending point, for every interval we can find the last interval that can be selected before it.

For an interval:

```text
[current.left, current.right]
```

the previous interval must satisfy:

```text
previous.right < current.left
```

The strict `<` is important because intervals sharing an endpoint are considered overlapping.

For example:

```text
[1,5]
[5,8]
```

are overlapping because they share point `5`.

---

## Binary Search

For every interval `i`, we calculate:

```text
previous[i]
```

which represents the last interval whose ending point is strictly smaller than the current interval's starting point.

Because the intervals are sorted by ending point, we can find this using binary search in:

```text
O(log n)
```

instead of checking every previous interval.

---

## Dynamic Programming

Since we can select at most `4` intervals, we use:

```text
dp[i][k]
```

where:

- `i` = first `i` sorted intervals
- `k` = maximum number of intervals we can select

So:

```text
dp[i][k]
```

stores the best possible answer using the first `i` intervals and at most `k` selections.

---

## Take or Skip

For every interval, we have two choices.

### 1. Skip

Don't select the current interval:

```text
skip = dp[i - 1][k]
```

### 2. Take

Select the current interval.

If `previous[i]` is the last compatible interval:

```text
take = currentWeight + dp[previous[i] + 1][k - 1]
```

We use `k - 1` because selecting the current interval consumes one available selection.

Therefore:

```text
dp[i][k] = better(skip, take)
```

---

## Lexicographical Tie-Breaking

The problem has an additional condition.

If two selections have the same total weight, we must return the lexicographically smaller array of original indices.

For example:

```text
[1,3,5,6]
[2,3,5,6]
```

Both have the same weight, so:

```text
[1,3,5,6]
```

is the correct answer because:

```text
1 < 2
```

Therefore, every selected-index array is kept sorted.

---

## Failure We Faced

Initially, the solution produced:

```text
[6,1,3,5]
```

while the expected answer was:

```text
[1,3,5,6]
```

The important thing was that the **same intervals were selected**.

The problem was their order.

Because the DP processes intervals according to their ending time, original indices were being added in that processing order.

So we could get:

```text
[6,1,3,5]
```

instead of:

```text
[1,3,5,6]
```

This caused the lexicographical comparison to be incorrect.

### Fix

After adding an original index:

```java
selected[selected.length - 1] = arr[current][3];
```

we sort the selected indices:

```java
Arrays.sort(selected);
```

Since we can select at most 4 intervals, sorting these arrays is very cheap.

---

## Why Original Indices Are Stored

The intervals are sorted before running DP, so their positions change.

Therefore, every interval is stored as:

```text
[left, right, weight, originalIndex]
```

The `originalIndex` allows us to return the indices according to the original input.

---

## Algorithm

1. Store each interval along with its original index.
2. Sort intervals by ending point.
3. For every interval, find the previous compatible interval using binary search.
4. Create `dp[i][k]` for at most 4 selections.
5. For every interval:
   - Skip it.
   - Take it and combine it with the best compatible solution.

6. Choose the state with:
   - Higher total weight.
   - Lexicographically smaller indices if weights are equal.

7. Keep selected indices sorted.
8. Return the final indices.

---

## Complexity

Let `n` be the number of intervals.

### Time Complexity

Sorting:

```text
O(n log n)
```

Binary search for every interval:

```text
O(n log n)
```

DP:

```text
O(4n) = O(n)
```

Therefore, the overall complexity is:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

for the intervals, previous-index array, and DP states.

---

## Pattern to Remember

When a problem contains:

```text
Non-overlapping intervals
+
Weight/Value
+
Maximum total value
```

think:

> **Weighted Interval Scheduling**

When the number of selected intervals is limited:

> **Weighted Interval Scheduling + K-Selection DP**

When a tie requires the smallest indices:

> **Weighted Interval Scheduling + Binary Search + DP + Lexicographical Tie-Breaking**

---

## What I Learned

- How to identify Weighted Interval Scheduling.
- How sorting by ending time helps.
- How to use binary search to find compatible intervals.
- How `Take / Skip` DP works.
- How to add a selection limit using `k`.
- How to handle lexicographical tie-breaking.
- Why original indices must be preserved after sorting.
- How a solution can select the correct elements but still get **Wrong Answer because of output ordering**.

### Final Pattern

```text
Sorting
   ↓
Binary Search
   ↓
Previous Compatible Interval
   ↓
DP(i, k)
   ↓
Take / Skip
   ↓
Maximum Weight
   ↓
Lexicographically Smallest Indices
```
