import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public abstract class Result<T> {

	private static final String defaultErrorSeparator = ", ";
	
	private boolean isSuccess;
	
	public Result(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * Matches a result's state (success, failure) without a return value.
	 * @param success Consumer<T> executed on success
	 * @param failure Consumer<T> executed on failure
	 */
	public void matchVoid(Consumer<T> success, Consumer<String> failure) {
		if (this.isSuccess()) {
			success.accept(this.toSuccess().getValue());
		} else {
			failure.accept(this.toFailure().getMessage());
		}
	}


	/**
	 * Matches a result's state (success, failure) and returns a value of type T1.
	 * @param success Function<T, T1> executed on success
	 * @param failure Function<T, T1> executed on failure
	 * @param <T1> return value type
	 * @return T1
	 */
	public <T1> T1 match(Function<T, T1>  success, Function<String, T1> failure) {
		if (this.isSuccess()) {
			return success.apply(this.toSuccess().getValue());
		}
		return failure.apply(this.toFailure().getMessage());
	}

	/**
	 * Binds a result to an existing result.
	 * @param binder
	 * @param <T1>
	 * @return
	 */
	public <T1> Result<T1> bind(Function<T, Result<T1>> binder) {
		if (this.isSuccess()) {
			return binder.apply(toSuccess().getValue());
		}
		return this.toFailure().convert();
	}

	/**
	 * Maps an existing value to a new Result<T1> with value type T1.
	 * @param mapper Function<T, T1> transforms value on success
	 * @param <T1> New value type
	 * @return Result<T1>
	 */
	public <T1> Result<T1> map(Function<T, T1> mapper) {
		return this.bind((value) -> Create.success(mapper.apply(value)));
	}

	/**
	 * Aggregates results from a stream to one result with a list of all values.
	 * @param stream result stream
	 * @param errorSeparator
	 * @param <T>
	 * @return
	 */

	public static <T> Result<List<T>> aggregate(Stream<Result<T>> stream, String errorSeparator) {
		var stringBuffer = new StringBuffer();
		var results = Result.choose(
				stream,
				tResult ->
						stringBuffer
								.append(errorSeparator)
								.append(tResult))
				.map(tResult ->
						Result.getValueOrWrapExceptionAndReturnNull(
								tResult,
								exceptionWrapper -> {
									stringBuffer
											.append(errorSeparator)
											.append(exceptionWrapper);
									return null;}))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		if (stringBuffer.length() > 0) {
			return Create.failure(stringBuffer.toString());
		}
		return Create.success(results);
	}

	public static <T> Result<List<T>> aggregate(Stream<Result<T>> results) {
		return Result.aggregate(results, Result.defaultErrorSeparator);
	}

	public static <T> Result<List<T>> aggregate(Collection<Result<T>> results) {
		return Result.aggregate(results.stream(), Result.defaultErrorSeparator);
	}

	public static <T> Result<List<T>> aggregate(Collection<Result<T>> results, String errorSeparator) {
		return Result.aggregate(results.stream(), errorSeparator);
	}

	private static <T> T getValueOrWrapExceptionAndReturnNull(Result<T> tResult, Function<GetValueOrThrowException, T> exceptionhandler) {
		try {
			return tResult.getValueOrThrow();
		} catch (GetValueOrThrowException e) {
			return exceptionhandler.apply(e);
		}
	}

	public static <T> Stream<Result<T>> choose(Stream<Result<T>> stream, Consumer<Result<T>> errorHandler)  {
		return stream
				.filter(e-> e.match(s -> true, f -> {
					errorHandler.accept(e);
					return false;
				}));
	}

	public static <T> CompletableFuture<Void> matchVoidAsync(CompletableFuture<Result<T>> future, Consumer<T> success, Consumer<String> failure) {
		return future.thenAccept(result-> result.matchVoid(success, failure));
	}

	public static <T, T1> CompletableFuture<T1> matchAsync(CompletableFuture<Result<T>> future, Function<T, T1>  success, Function<String, T1> failure) {
		return future.thenApply(result -> result.match(success, failure));
	}

	public static <T, T1> CompletableFuture<Result<T1>> bindAsync(CompletableFuture<Result<T>> future, Function<T, Result<T1>> binder) {
		return future.thenApply(result -> result.bind(binder));
	}

	public static <T, T1> CompletableFuture<Result<T1>> mapAsync(CompletableFuture<Result<T>> future, Function<T, T1> mapper) {
		return future.thenApply(result -> result.map(mapper));
	}

	public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Stream<CompletableFuture<Result<T>>> completableFutureStream,  String errorSeparator) {
		return CompletableFuture.supplyAsync(() -> Result.aggregate(completableFutureStream.map(CompletableFuture::join), errorSeparator));
	}

	public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Stream<CompletableFuture<Result<T>>> futureStream) {
		return Result.aggregateAsync(futureStream, Result.defaultErrorSeparator);
	}

	public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Collection<CompletableFuture<Result<T>>> completableFutures,  String errorSeparator) {
		return Result.aggregateAsync(completableFutures.stream(), errorSeparator);
	}

	public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Collection<CompletableFuture<Result<T>>> completableFutures) {
		return Result.aggregateAsync(completableFutures, Result.defaultErrorSeparator);
	}

	public static <T> CompletableFuture<Stream<Result<T>>> chooseAsync(Stream<CompletableFuture<Result<T>>> completableFutureStream, Consumer<Result<T>> errorHandler) {
		return CompletableFuture.supplyAsync(()-> Result.choose(completableFutureStream.map(CompletableFuture::join), errorHandler));
	}

	public T getValueOrNull() {
		if (this.isSuccess()) {
			return this.toSuccess().getValue();
		}
		return null;
	}
	
	public T getValueOrThrow() throws GetValueOrThrowException {
		if (this.isSuccess) {
			return this.toSuccess().getValue();
		}
		throw new GetValueOrThrowException("Tried to get a value from a Failure.");
	}

	public boolean isSuccess() {
		return this.isSuccess;
	}

	public boolean isFailure() {
		return !this.isSuccess;
	}

	private Success<T> toSuccess() {
		return (Success<T>) this;
	}
	
	private Failure<T> toFailure() {
		return (Failure<T>) this;
	}
}
