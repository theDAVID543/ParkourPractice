package the.david.impl;

public class Pair<T, U>{
	public Pair(T Key, U Value){
		this.Key = Key;
		this.Value = Value;
	}

	public T Key;
	public U Value;

	public T getKey(){
		return Key;
	}

	public U getValue(){
		return Value;
	}
}
