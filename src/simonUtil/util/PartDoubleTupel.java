package simonUtil.util;

public class PartDoubleTupel<A> {

	public A a;
	public double b;
	
	public PartDoubleTupel(A a, double b){
		this.a = a;
		this.b = b;
	}
	
	public String toString(){
		return "(" + a + " ; " + b + ")";
	}
	
	public PartDoubleTupel(){
		this(null, 0);
	}
}
