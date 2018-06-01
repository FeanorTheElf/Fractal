package simonUtil.util;

public class Tupel<A, B> {

	public A a;
	public B b;
	
	public Tupel(A a, B b){
		this.a = a;
		this.b = b;
	}
	
	public Tupel(){
		this(null, null);
	}
}