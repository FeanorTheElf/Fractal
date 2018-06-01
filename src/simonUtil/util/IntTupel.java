package simonUtil.util;

public final class IntTupel {

	public int a;
	public int b;
	
	public boolean equals(Object that){
		if (that instanceof IntTupel){
			return this.a == ((IntTupel)that).a && this.b == ((IntTupel)that).b;
		}
		return false;
	}
	
	public int hashCode(){
		int c = a + b;
		return a + ((c * (c + 1)) >> 1);
	}
	
	public IntTupel(int a, int b){
		this.a = a;
		this.b = b;
	}
}
