package overlay;

public class Trio<A, B, C> {

	private A first;
	private B second;
	private C third;
	
	public Trio() {}; // default constructor
	
	public Trio(A first, B second, C third)
	{
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public A getFirst()
	{
		return first;
	}
	
	public void setFirst(A first)
	{
		this.first = first;
	}
	
	public B getSecond()
	{
		return second;
	}
	
	public void setSecond(B second)
	{
		this.second = second;
	}
	
	public C getThird()
	{
		return third;
	}
	
	public void setThird(C third)
	{
		this.third = third;
	}	
	
	public String toString()
	{
		return "( " + first + ", " + second + ", " + third + ")"; // prints (first, second, third)
	}
}
