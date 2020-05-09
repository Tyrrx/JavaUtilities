public abstract class Create {
	public static <T> Result<T> success(T value) {
		return new Success<>(value);
	}
	
	public static <T> Result<T> failure(String message) {
		return new Failure<>(message);
	}
}
