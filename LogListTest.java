import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class LogListTest {

    private int iterationCount = 100_000;

    @Test
    void add() {
        List<String> expected = new ArrayList<>();
        LogList<String> actual = new LogList<>();
        for (int i = 0; i < 100; ++i) {
            expected.add(String.valueOf(i));
            actual.add(String.valueOf(i));
        }
        Assertions.assertEquals(actual.toString(), expected.toString());
    }

    @Test
    void addAtIndex() {
        List<Character> expected = new ArrayList<>();
        LogList<Character> actual = new LogList<>();
        for (char i = 'a'; i <= 'z'; ++i) {
            expected.add(i);
            actual.add(i);
        }
        expected.add(2, '0');
        actual.add(2, '0');
        Assertions.assertEquals(actual.toString(), expected.toString());
    }

    @Test
    void addToIndexOutOfBounds() {
        LogList<String> list = new LogList<>();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.add(42, "Hello"));
    }

    @Test
    void removeAtIndex() {
        LogList<String> list = new LogList<>();
        list.add("Hello");
        list.add("World");
        list.add("42");
        list.remove(1);
        Assertions.assertEquals(list.get(0), "Hello");
        Assertions.assertEquals(list.get(1), "42");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @Test
    void setAtIndex() {
        LogList<Double> list = new LogList<>();
        for (double i = 0; i < 1; i += 0.01) {
            list.add(i);
        }
        list.set(10, 100.0);
        double EPS = 0.001;
        for (int i = 0; i < 100; ++i) {
            double value = i * 0.01;
            if (i != 10) {
                Assertions.assertEquals(value, list.get(i), EPS);
            } else {
                Assertions.assertEquals(100.0, list.get(i), EPS);
            }
        }
    }

    @Test
    void getFromOutOfBounds() {
        LogList<String> list = new LogList<>();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(-10));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(10));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    private long measureTime(Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        long end = System.currentTimeMillis();
        return end - start;
    }

    private void printMeasureResults(String title, long listTime, long arrayTime, long treeTime) {
        System.out.println(title);
        System.out.println("list : " + listTime);
        System.out.println("tree : " + treeTime);
        System.out.println("array: " + arrayTime);
        System.out.println();
    }

    @Test
    void addingToFront() {
        long listTime = measureTime(() -> {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < iterationCount; ++i) {
                list.add(0, String.valueOf(i));
            }
        });

        long arrayTime = measureTime(() -> {
            List<String> array = new ArrayList<>();
            for (int i = 0; i < iterationCount; ++i) {
                array.add(0, String.valueOf(i));
            }
        });

        long treeTime = measureTime(() -> {
            LogList<String> tree = new LogList<>();
            for (int i = 0; i < iterationCount; ++i) {
                tree.add(0, String.valueOf(i));
            }
        });

        printMeasureResults("add to front", listTime, arrayTime, treeTime);
        Assertions.assertTrue(listTime < treeTime && treeTime < arrayTime);
    }

    @Test
    void addingToMiddle() {
        long listTime = measureTime(() -> {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < iterationCount; ++i) {
                list.add(list.size() / 2, String.valueOf(i));
            }
        });

        long arrayTime = measureTime(() -> {
            List<String> array = new ArrayList<>();
            for (int i = 0; i < iterationCount; ++i) {
                array.add(array.size() / 2, String.valueOf(i));
            }
        });

        long treeTime = measureTime(() -> {
            LogList<String> tree = new LogList<>();
            for (int i = 0; i < iterationCount; ++i) {
                tree.add(tree.size() / 2, String.valueOf(i));
            }
        });

        printMeasureResults("add to middle", listTime, arrayTime, treeTime);
        Assertions.assertTrue(treeTime < listTime && treeTime < arrayTime);
    }

    @Test
    void addingToEnd() {
        long listTime = measureTime(() -> {
            List<String> list = new LinkedList<>();
            list.add("");
            for (int i = 0; i < iterationCount; ++i) {
                list.add(list.size() - 1, String.valueOf(i));
            }
        });

        long arrayTime = measureTime(() -> {
            List<String> array = new ArrayList<>();
            array.add("");
            for (int i = 0; i < iterationCount; ++i) {
                array.add(array.size() - 1, String.valueOf(i));
            }
        });

        long treeTime = measureTime(() -> {
            LogList<String> tree = new LogList<>();
            tree.add("");
            for (int i = 0; i < iterationCount; ++i) {
                tree.add(tree.size() - 1, String.valueOf(i));
            }
        });

        printMeasureResults("add to end", listTime, arrayTime, treeTime);
        Assertions.assertTrue(treeTime > listTime && treeTime > arrayTime);
    }

    @Test
    void removingFromFront() {
        List<String> list = new LinkedList<>();
        List<String> array = new ArrayList<>();
        LogList<String> tree = new LogList<>();

        for (int i = 0; i < iterationCount; ++i) {
            list.add(String.valueOf(i));
            array.add(String.valueOf(i));
            tree.add(String.valueOf(i));
        }

        long listTime = measureTime(() -> {
            for (int i = 0; i < iterationCount; ++i) {
                list.remove(0);
            }
        });

        long arrayTime = measureTime(() -> {
            for (int i = 0; i < iterationCount; ++i) {
                array.remove(0);
            }
        });

        long treeTime = measureTime(() -> {
            for (int i = 0; i < iterationCount; ++i) {
                tree.remove(0);
            }
        });

        printMeasureResults("remove from front", listTime, arrayTime, treeTime);
        Assertions.assertTrue(listTime < treeTime && treeTime < arrayTime);
    }

    @Test
    void removingFromMiddle() {
        List<String> list = new LinkedList<>();
        List<String> array = new ArrayList<>();
        LogList<String> tree = new LogList<>();

        for (int i = 0; i < iterationCount; ++i) {
            list.add(String.valueOf(i));
            array.add(String.valueOf(i));
            tree.add(String.valueOf(i));
        }

        long listTime = measureTime(() -> {
            for (int i = 0; i < iterationCount; ++i) {
                list.remove(list.size() / 2);
            }
        });

        long arrayTime = measureTime(() -> {
            for (int i = 0; i < iterationCount; ++i) {
                array.remove(array.size() / 2);
            }
        });

        long treeTime = measureTime(() -> {
            for (int i = 0; i < iterationCount; ++i) {
                tree.remove(tree.size() / 2);
            }
        });

        printMeasureResults("remove from middle", listTime, arrayTime, treeTime);
        Assertions.assertTrue(treeTime < listTime && treeTime < arrayTime);
    }

    @Test
    void removingFromEnd() {
        List<String> list = new LinkedList<>();
        List<String> array = new ArrayList<>();
        LogList<String> tree = new LogList<>();

        for (int i = 0; i < iterationCount; ++i) {
            list.add(String.valueOf(i));
            array.add(String.valueOf(i));
            tree.add(String.valueOf(i));
        }

        long listTime = measureTime(() -> {
            for (int i = 0; i < iterationCount - 1; ++i) {
                list.remove(list.size() - 1);
            }
        });

        long arrayTime = measureTime(() -> {
            for (int i = 0; i < iterationCount - 1; ++i) {
                array.remove(array.size() - 1);
            }
        });

        long treeTime = measureTime(() -> {
            for (int i = 0; i < iterationCount - 1; ++i) {
                tree.remove(tree.size() - 1);
            }
        });

        printMeasureResults("remove from end", listTime, arrayTime, treeTime);
        Assertions.assertTrue(treeTime > listTime && treeTime > arrayTime);
    }

    @Test
    void gettingByIndex() {
        List<String> list = new LinkedList<>();
        List<String> array = new ArrayList<>();
        LogList<String> tree = new LogList<>();

        for (int i = 0; i < iterationCount; ++i) {
            list.add(String.valueOf(i));
            array.add(String.valueOf(i));
            tree.add(String.valueOf(i));
        }

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < iterationCount; ++i) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);

        long listTime = measureTime(() -> {
            for (int index: indexes) {
                String str = list.get(index);
            }
        });

        long arrayTime = measureTime(() -> {
            for (int index: indexes) {
                String str = array.get(index);
            }
        });

        long treeTime = measureTime(() -> {
            for (int index: indexes) {
                String str = tree.get(index);
            }
        });

        printMeasureResults("remove from end", listTime, arrayTime, treeTime);
        Assertions.assertTrue(arrayTime < treeTime && treeTime < listTime);
    }

}