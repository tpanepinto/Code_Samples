package cs671;



import java.util.*;

//import cs671.Liar.Secret;
/** This Liar class has a constructor that makes a liar method
 * @author Tim Panepinto
 *
 */
public class Liar<T> implements Guesser<Liar.Secret<T>> {

	//keeps the name of what the objects are
	public final String name;
	//keeps the maxLies that can be used
	public final int maxLies;
	
	//List of candidates to be choosen from
	@SuppressWarnings("unused")
	private Set<? extends T> candidates;
	//top array where the questions are asked from
	private Stack<Secret<T>>[] top;
	//bottom array where the elements are not shown
	private Stack<Secret<T>>[] bottom;
	//array of the candidates for easier access
	private ArrayList<T> candidateArr;
	//is the program initialized
	private boolean init;
	//is the secret solved
	private boolean solved;
	//has a question been made
	private boolean qMade;
	//used to ask the last question
	private boolean almostDone;
	//will a certain amount of candidates be selected
	private boolean selCand;
	//has the program started but not initialized
	private boolean start;
	
	//The amount of candidates
	private int sizeCand;
	//the size of the array of stacks = to maxLies + 1
	private int arrSize;
	//size of array bottom
	private int bottomCount;
	//size of array top
	private int topCount;
	//number of candidates to be selected
	private int selCandNum;
	//progress made in the game
	private double progress;
	//current guess of the secret
	private Secret<T> guess;
	/** This is a program where the user gives a list of objects then chooses a secret object.
	 * A series of yes/no questions will be asked to determine the secret object
	 * Once the secret object is found the game ends.
	 *@see Liar
	 *
	 */
	public Liar (Set<? extends T> givenCandidates, int lies, String nameGiven)
	{
		if(givenCandidates.isEmpty())
			throw new IllegalArgumentException ();
		else if (lies < 0)
			throw new IllegalArgumentException ();
		else if (nameGiven == null)
			throw new IllegalArgumentException ();
		else
		{
			name = nameGiven;
			maxLies=lies;
			candidates = givenCandidates;
			sizeCand = givenCandidates.size();
			
			candidateArr = new ArrayList<T>();
			
			for(Iterator<? extends T> it = givenCandidates.iterator(); it.hasNext(); )
				candidateArr.add(it.next());
			init = false;
			solved = false;
			qMade = false;
			selCand = false;
		}
		
	}
	 /** Initializes the guessing engine.  This method must be called to
	   * initiate a problem before the rounds of questions and answers begin.
	   *
	   * @return An initialization message that can be displayed to the user
	   */
	@SuppressWarnings({"unchecked", "rawtypes"})

	@Override
	public String initialize() {
		int half;
		int other;
		String startOff = "Pick one among ";
		half = sizeCand/2;
		other = sizeCand - half;
		arrSize = maxLies + 1;
        top = new Stack[arrSize];

		bottom = new Stack[arrSize];
		init = true;
		qMade = false;
		bottomCount = other;
		topCount = half;
		almostDone = false;
		start = true;
		progress = 0.0;
		
		//Stack<Secret<T>> stackTop = new Stack<>();
		if (selCand)
		{

            Collections.shuffle(candidateArr);
			sizeCand = selCandNum;
			half = sizeCand/2;
			other = sizeCand - half;
		}

		if ( sizeCand == 1)
		{
			solved = true;
			
			Secret<T> newSecret = new Secret<>();
			newSecret.lies = 0;
			newSecret.secret = candidateArr.get(0);
			guess = newSecret;
			return "The secret is: " + candidateArr.get(0) + " (with 0 lie(s))";
		}
		if (!solved)
		{
			for (int k = 0; k < arrSize; k++)
			{
				Stack<Secret<T>> newTopStack = new Stack<>();
				Stack<Secret<T>> newBtmStack = new Stack<>();
				top[k]=newTopStack;
				bottom[k] = newBtmStack;
				
			}
			
				for (int i = 0; i<half; i++)
				{
					Secret<T> newSecret = new Secret<>();
					newSecret.lies = 0;
					newSecret.secret = candidateArr.get(i);
					top[0].push(newSecret);
					startOff = startOff + newSecret.secret + " ";
										
				}
				
				//Stack<Secret<T>> stackBtm = new Stack<>();
				for (int j = 0; j < other; j++)
				{
					Secret<T> newSecret = new Secret<>();
					newSecret.lies = 0;
					newSecret.secret = candidateArr.get(j + half);
					bottom[0].push(newSecret);
					startOff = startOff + newSecret.secret + " ";
										
				}
		}
		
		
		
		
		
		return startOff;
	}
	/** Whether the problem has been solved.  While this method returns
	   * <code>false</code>, users should continue to call methods
	   * <code>makeQuestion</code>, <code>yes</code> and <code>no</code>.
	   * Once it returns <code>true</code>, the method
	   * <code>getSecret</code> can safely be called.
	   *
	   * @return <code>true</code> iff the problem has been solved
	   * @throws IllegalStateException if the engine has not been initialized
	   * @see #makeQuestion
	   * @see #yes
	   * @see #no
	   * @see #getSecret
	   */
	@Override
	public boolean hasSolved() {
		if (init)
            return solved;
		else
			throw new IllegalStateException();
	}
	/** The answer to the problem.  This method should be called after
	   * <code>hasSolved</code> returns <code>true</code> to retreive the
	   * solution to the problem.
	   *
	   * @return the answer to the problem, if it is known
	   * @throws IllegalStateException if the problem has not yet been solved
	   * or the guesser has not been initialized
	   * @see #hasSolved
	   */
	@Override
	public Secret<T> getSecret() {
		if (init && solved)
            return guess;
		else
			throw new IllegalStateException();
	}
	/** Used to reply YES to the previous question.  Method
	   * <code>makeQuestion</code> must be called to generate a question
	   * before calling <code>yes</code> or <code>no</code>.
	   *
	   * @throws IllegalStateException if <code>makeQuestion</code> has
	   * not been called first or the guesser has not been initialized
	   * @see #makeQuestion
	   * @see #no
	   */
	@Override
	public void yes() {
		start = false;
		if ( !init )
			throw new IllegalStateException();
		else if ( !qMade )
			throw new IllegalStateException();
		else
		{
			if(almostDone)
			{
				solved = true;
				qMade = false;
			}
				
			else
			{
				for( int i = (arrSize -1); i > 0; i--)
				{
					if ( i == maxLies)
					{
						bottomCount = bottomCount - bottom[i].size();
						bottom[i].clear();
					}
					if ( i > 0)
					{
						int btmI = bottom[i -1].size();
						for (int j = 0; j< btmI; j++)
						{
							Secret<T> popSecret = bottom[i-1].pop();
							popSecret.lies = popSecret.lies++;
							bottom[i].push(popSecret);
						}
					}
					qMade = false;
				}
				sortItOut();

				
			}
		}
		
	}
	/** Used to reply YES to the previous question.  Method
	   * <code>yes</code> or <code>no</code>  must be called and then the arrays are sorted
	   * in order for the elements to be better distributed.
	   * @see yes
	   * @see no
	   */
	private void sortItOut()
	{
	
		for ( int i = 0; i < arrSize; i++)
		{
			
			
			if (top[i].isEmpty() && !bottom[i].isEmpty())
			{
                int btmSize = (Math.round(bottom[i].size()/2));
				for( int j = 0; j < btmSize; j++)
				{
					top[i].push(bottom[i].pop());
					topCount++;
					bottomCount--;
				}
				
			}
			else if (bottom[i].isEmpty() && !top[i].isEmpty())
			{
                int topSize = (Math.round(top[i].size()/2));
				for( int j = 0; j < topSize; j++)
					{
						bottom[i].push(top[i].pop());
						bottomCount++;
						topCount--;
					}
				
			}
						
		
			
		}
		//System.out.println(DEBUGTAKEMEOUTTOP + " This is size: " + topCount);
		//System.out.println(DEBUGTAKEMEOUTBTM + " This is size: " + bottomCount);
		int loop = (arrSize -1);
		while (bottomCount == 0)
		{
			
			
				 if (bottom[loop].isEmpty() && !top[loop].isEmpty())
					{
                        int btmLoop = top[loop].size();
						for( int j = 0; j < btmLoop; j++)
							{
								bottom[loop].push(top[loop].pop());
								bottomCount++;
								topCount--;
							}
						
					}
				 loop--;	
				// System.out.println("StuckBottom");
			
		}
        loop = (arrSize -1);
		while (topCount == 0)
		{

			if (top[loop].isEmpty() && !bottom[loop].isEmpty())
					{
                        int topLoop = bottom[loop].size();
						for( int j = 0; j < topLoop; j++)
							{
								top[loop].push(bottom[loop].pop());
								bottomCount--;
								topCount++;
							}
						
					}
				
			loop--;
			// System.out.println("StuckTop");
		}
//        String topStr = "Top: " ;
//        String bottomStr = "Bottom: ";
//		for ( int l = 0; l < arrSize - 1; l++)
//        {
//            topStr += (top[l].toString());
//            bottomStr += (bottom[l].toString());
//
//        }
//        System.out.println(topStr);
//        System.out.println(bottomStr);
	}
	/** Used to find if the game is done by checking the amount of elements in play
	 * in both arrays.
	   *
	   * @return the amount of elements in both arrays combined
	   */
	private int checkSolve()
	{	
		int elements = 0;
		for ( int i = 0; i < arrSize; i++)
			elements = elements + top[i].size() + bottom[i].size();
		return elements;
					
	}
	/** Used to reply NO to the previous question.  Method
	   * <code>makeQuestion</code> must be called to generate a question
	   * before calling <code>yes</code> or <code>no</code>.
	   *
	   * @throws IllegalStateException if <code>makeQuestion</code> has
	   * not been called first or the guesser has not been initialized
	   * @see #makeQuestion
	   * @see #yes
	   */
	@Override
	public void no() {
		start = false;
		if ( !init )
			throw new IllegalStateException();
		else if ( !qMade )
			throw new IllegalStateException();
		else
		{
			if( almostDone )
			{
				solved = true;
				for ( int i = 0; i < arrSize - 1; i++)
				{
					for( int j = 0; j < top[i].size(); j++)
					{
						guess = top[i].get(j);
					}
					
				}
				qMade = false;
			}
			else
			{
				for( int i = (arrSize - 1); i > 0; i--)
				{
					if ( i == maxLies)
					{
						topCount = topCount - top[i].size();
						top[i].clear();
					}
					if ( i > 0)
					{
						int topI = top[i -1].size();
						for (int j = 0; j < topI; j++)
						{
							Secret<T> popSecret = top[i-1].pop();
							popSecret.lies = popSecret.lies++;
							top[i].push(popSecret);
						}
					}
					qMade = false;
				}
				sortItOut();

			}
		}
		
	}
	/** Generates a new question.  The previous question must be
	   * answered (using <code>yes</code> or <code>no</code>) before a new
	   * question is generated.
	   *
	   * @return the question, as a string to be displayed to the user
	   * @throws IllegalStateException if the guesser is not initialized,
	   * the problem is already solved or the previous question has not
	   * been answered
	   * @see #initialize
	   * @see #hasSolved
	   * @see #yes
	   * @see #no
	   */
	@Override
	public String makeQuestion() {
		if ( !init || solved )
			throw new IllegalStateException();
		else if ( qMade )
			throw new IllegalStateException();
		else
		{
			
				if (topCount == 1 && bottomCount == 1)
					{
						String doneStr = "Is the secret " + name + " ";
						for ( int i = 0; i < arrSize - 1; i++)
						{
							for( int j = 0; j < top[i].size(); j++)
							{
								doneStr = doneStr + top[i].get(j).secret;
								guess = top[i].get(j);
							}
							almostDone = true;
						}
						qMade = true;
						return doneStr;
					}
				else{
					String candStr = "Is the secret " + name + " among ";
					for ( int i = 0; i < arrSize - 1; i++)
					{
						for( int j = 0; j < top[i].size(); j++)
							candStr = candStr + top[i].get(j).secret;
					}
					//System.out.println(candStr);
					qMade = true;
					return candStr + " ?";
				}
			
		}
		
	}
	 /** Indicates progress towards solving the problem.  After
	   * initialization, the value returned is 0 (unless the problem is
	   * immediately solved, in which case it is 1).  It always increases
	   * as the guessing process progresses.  It is exactly 1 after the
	   * problem is solved.
	   *
	   * @return a value between 0 and 1 that is a measure of how much
	   * progress has been made towards solving the problem
	   * @throws IllegalStateException if the engine has not been initialized
	   */
	@Override
	public double progress() {
		if (init)
		{	//double prevProg;
			if ( solved )
				return 1.0;
			else
			{
				if( start )
					return progress;
				else
				{
					progress = progress + 0.001;

					return progress;
				}
			}
			
		}
		else
			throw new IllegalStateException();
	}
	 /** Gives the amount of candidates to be picked
	   *
	   * @return the amount of candidates to be picked at random
	   * @throws IllegalStateException if the engine has not been initialized
	   * @throws IllegalArgumentException if the amount of candidates is less than 1
	   */
	public int selectCandidates(int n)
	{
		if ( init )
			throw new IllegalStateException();
		else if (solved)
			throw new IllegalStateException();
		else if( n < 1)
			throw new IllegalArgumentException();
		else{
			selCand = true;
			if (n > sizeCand)
				selCandNum = sizeCand;
			else
				selCandNum = n;
			return selCandNum;
		}
		
	}
	
	
	public static class Secret<T> extends Object {
		private int lies;
		private T secret;
		
		public int getLies()
		{
			return lies;
		}
		public java.lang.String toString(){
			return "The secret is: " + secret + "(with " + lies + " lie(s))";
		}
		
		
		public T getSecret()
		{
			return secret;
		}
	}
	
}
