package exercices.minandmax;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainMinMax {

    private static final int PARALLELISM = 8;
    private static final int SIZE = 100_000_000;

    public static void main(String[] args) {

        List<Long> numbers = generateRandomNumbers();
//        List<Integer> numbers = List.of(-999, 101, 10, 2, 3, 5, 4, 8, 9, 7, 41, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

        long start = System.currentTimeMillis();
        var calculator = MinMaxConcurrentCalculator.fromLongs(numbers);

        Thread threadGetMin = new Thread(() -> System.out.println("Teste Min -> "
                .concat(calculator.getMin(PARALLELISM).toString())));
        Thread threadGetMax = new Thread(() -> System.out.println("Teste Max -> "
                .concat(calculator.getMax(PARALLELISM).toString())));

        threadGetMax.start();
        threadGetMin.start();

        try {
            threadGetMin.join();
            threadGetMax.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long end = System.currentTimeMillis();

        System.out.println("Total Time Spent(ms) -> " + (end - start));
    }

    private static List<Long> generateRandomNumbers() {
        List<Long> numbers = new ArrayList<>(SIZE);
        Random random = new Random(SIZE);
        for (int i = 0; i < SIZE; i++) {
            numbers.add(random.nextLong());
        }
        return numbers;
    }
}
