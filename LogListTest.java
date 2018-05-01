import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class LogListTest {
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
        int iterations = 100_000;

        long listTime = measureTime(() -> {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < iterations; ++i) {
                list.add(0, String.valueOf(i));
            }
        });

        long arrayTime = measureTime(() -> {
            List<String> array = new ArrayList<>();
            for (int i = 0; i < iterations; ++i) {
                array.add(0, String.valueOf(i));
            }
        });

        long treeTime = measureTime(() -> {
            LogList<String> tree = new LogList<>();
            for (int i = 0; i < iterations; ++i) {
                tree.add(0, String.valueOf(i));
            }
        });

        printMeasureResults("add to front", listTime, arrayTime, treeTime);
    }

    @Test
    void addingToMiddle() {
        int iterations = 100_000;

        long listTime = measureTime(() -> {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < iterations; ++i) {
                list.add(list.size() / 2, String.valueOf(i));
            }
        });

        long arrayTime = measureTime(() -> {
            List<String> array = new ArrayList<>();
            for (int i = 0; i < iterations; ++i) {
                array.add(array.size() / 2, String.valueOf(i));
            }
        });

        long treeTime = measureTime(() -> {
            LogList<String> tree = new LogList<>();
            for (int i = 0; i < iterations; ++i) {
                tree.add(tree.size() / 2, String.valueOf(i));
            }
        });

        printMeasureResults("add to middle", listTime, arrayTime, treeTime);
    }

    @Test
    void addingToEnd() {
        int iterations = 100_000;

        long listTime = measureTime(() -> {
            List<String> list = new LinkedList<>();
            list.add("");
            for (int i = 0; i < iterations; ++i) {
                list.add(list.size() - 1, String.valueOf(i));
            }
        });

        long arrayTime = measureTime(() -> {
            List<String> array = new ArrayList<>();
            array.add("");
            for (int i = 0; i < iterations; ++i) {
                array.add(array.size() - 1, String.valueOf(i));
            }
        });

        long treeTime = measureTime(() -> {
            LogList<String> tree = new LogList<>();
            tree.add("");
            for (int i = 0; i < iterations; ++i) {
                tree.add(tree.size() - 1, String.valueOf(i));
            }
        });

        printMeasureResults("add to end", listTime, arrayTime, treeTime);
    }
}