public final class Success<T> extends Result<T> {
	
	private T value;
	
	public Success(T value) {
		super(true);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
}
