package overlay;

// Template class to create a Pair. This will be used for keeping track of MessagingNodes.
public class Pair<A, B> {

	private A first;
	private B second;
	
	public Pair() {}; // default constructor.
	
	public Pair(A first, B second)
	{
		this.first = first;
		this.second = second;
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
	
	public String toString()
	{
		return "( " + first + ", " + second + ")"; // prints (first, second)
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
