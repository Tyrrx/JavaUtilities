package polaris;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public class ResultExamples {
	
	public static void main(String[] args) {

		CompletableFuture f = CompletableFuture.supplyAsync(()-> "bla");

		f.thenAccept(System.out::println);

		Instant start = Instant.now();

		Stream integerStream = Stream.iterate(0, i -> i+1).limit(999999).map(Result::success);

		Result<List<Integer>> res = Result.aggregate(integerStream);

		Instant end = Instant.now();
		System.out.println(Duration.between(start, end).getNano());
		System.out.println(Duration.between(start, end).getSeconds());

		res.matchVoid(s-> System.out.println(s.size()), System.out::println);
	}
}