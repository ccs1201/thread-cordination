package exercices.minandmax.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MinMaxTest {
    private static final int QTD = 51;
    public static List<Long> numbers;

    public static void main(String[] args) {
        MinMaxTest test = new MinMaxTest();

        Random random = new Random(QTD);

        numbers = new ArrayList<>(QTD);
        for (int i = 0; i < QTD; i++) {
            numbers.add(random.nextLong());
        }

        var acutal = test.splitList(5);

        if (acutal.size() != 6) {
            throw new RuntimeException("error");
        }
    }

    private List<List<Long>> splitList(int numberOfSubLists) {

        var size = subListSizeCalculator(numberOfSubLists);

        return IntStream
                .range(0, (numbers.size() + size - 1) / size)
                .mapToObj(i ->
                        numbers.subList(i * size, Math.min((i + 1) * size, numbers.size())))
                .collect(Collectors.toList());
    }

    private int subListSizeCalculator(int numberOfSubLists) {
        return numbers.size() / numberOfSubLists;
    }
}
