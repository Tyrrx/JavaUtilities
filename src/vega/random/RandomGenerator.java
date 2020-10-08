package vega.random;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 06.10.2020, 22:42
 */

public class RandomGenerator extends Random {

    public int nextInt(int min, int max) {
        return this.nextInt((max - min) + 1) + min;
    }

    public int[] nextIntArray(int min, int max, int length) {
        return nextIntStream(min, max).limit(length).toArray();
    }

    public IntStream nextIntStream(int min, int max) {
        return IntStream.generate(() -> this.nextInt(min, max));
    }

    public Stream<Character> nextCharStream(char asciiStart, char asciiEnd) {
        return nextIntStream(asciiStart, asciiEnd).boxed().map(i -> (char)i.intValue());
    }

    public IntStream nextDistinctIntStream(int min, int max) {
        return nextIntStream(min, max).distinct().limit(max - min +1);
    }

    public char nextChar(char asciiStart, char asciiEnd) {
        return (char) nextInt(asciiStart, asciiEnd);
    }

    public char nextAlphabetChar() {
        if (this.nextBoolean()) {
            return this.nextChar('A', 'Z');
        }
        return this.nextChar('a', 'z');
    }

    public Stream<Character> nextAlphabetStream() {
        return Stream.generate(this::nextAlphabetChar);
    }

    public static void main(String[] args) {
        //System.out.println(nextInt(0,10));
        //nextIntStream(10, 20).limit(1000).forEach(System.out::println);
        new RandomGenerator().nextCharStream('A', 'Z').limit(30).forEach(System.out::println);
        //new RandomGenerator().nextAlphabetStream().limit(30).forEach(System.out::println);
    }

}
