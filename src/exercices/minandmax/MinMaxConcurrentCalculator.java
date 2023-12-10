package exercices.minandmax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MinMaxConcurrentCalculator {

    private static final int SLEEP_TIME = 0;
    private final List<Long> numbers;
    private final AtomicLong minValue = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxValue = new AtomicLong(Long.MIN_VALUE);
    private final AtomicInteger callGetMinCount = new AtomicInteger(0);
    private final AtomicInteger callGetMaxCount = new AtomicInteger(0);
    private List<List<Long>> splitedNumbersList = Collections.emptyList();

    public MinMaxConcurrentCalculator(List<Long> numbersLong) {
        this.numbers = numbersLong;
    }

    public static MinMaxConcurrentCalculator fromLongs(List<Long> numbers) {
        return new MinMaxConcurrentCalculator(numbers);
    }

    public Long getMin(int parallelism) {
        if (this.splitedNumbersList.isEmpty()) {
            this.splitedNumbersList = splitList(parallelism);
        }
        List<Thread> threads = new ArrayList<>(parallelism);

        splitedNumbersList.forEach(longs -> {
            Thread t = new Thread(() -> {
                long min = findMin(longs);
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (this.minValue.get() > min) {
                    this.minValue.set(min);
                }
            });
            threads.add(t);
        });

        threads.forEach(t -> {
            t.setName("findMin-" + callGetMinCount.incrementAndGet());
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return minValue.get();
    }

    private int subListSizeCalculator(int numberOfSubLists) {
        return numbers.size() / numberOfSubLists;
    }

    private Long findMin(List<Long> splitedNumbers) {
        AtomicLong min = new AtomicLong(splitedNumbers.getFirst());
        splitedNumbers.forEach(next -> {
            if (next < min.get()) {
                min.set(next);
            }
        });
        System.out.println("Call findMin Thread -> "
                .concat(Thread.currentThread().getName())
                .concat(" | Min Value -> ").concat(min.toString()));
        return min.get();
    }

    public Long getMax(int parallelism) {
        if (this.splitedNumbersList.isEmpty()) {
            this.splitedNumbersList = splitList(parallelism);
        }

        List<Thread> threads = new ArrayList<>(parallelism);

        splitedNumbersList.forEach(longs -> {
            Thread t = new Thread(() -> {
                long max = findMax(longs);
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (this.maxValue.get() < max) {
                    this.maxValue.set(max);
                }
            });
            threads.add(t);
        });

        threads.forEach(t -> {
            t.setName("findMax-" + callGetMaxCount.incrementAndGet());
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return maxValue.get();
    }

    private Long findMax(List<Long> splitedNumbers) {
        AtomicLong max = new AtomicLong(splitedNumbers.getFirst());
        splitedNumbers.forEach(next -> {
            if (next > max.get()) {
                max.set(next);
            }
        });
        System.out.println("Call findMax Thread -> "
                .concat(Thread.currentThread().getName())
                .concat(" | Max Value -> ").concat(max.toString()));
        return max.get();
    }

    private List<List<Long>> splitList(int numberOfSubLists) {

        var size = subListSizeCalculator(numberOfSubLists);

        return IntStream
                .range(0, (numbers.size() + size - 1) / size)
                .mapToObj(i -> numbers.subList(i * size, Math.min((i + 1) * size, numbers.size())))
                .collect(Collectors.toList());
    }
}
