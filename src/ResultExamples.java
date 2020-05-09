import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ResultExamples {
	
	public static void main(String[] args) {

		Instant start = Instant.now();

		var integerStream = Stream.iterate(0, i -> i+1).limit(999999).map(Create::success);

		Result<List<Integer>> res = Result.aggregate(integerStream);

		Instant end = Instant.now();
		System.out.println(Duration.between(start, end).getNano());
		System.out.println(Duration.between(start, end).getSeconds());

		res.matchVoid(s-> System.out.println(s.size()), System.out::println);
	}
}