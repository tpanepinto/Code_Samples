package cs671;

/** This is a program where a user chooses a secret number between a max and a min value.
 * Once selected the user is asked a series of questions until the secret number is
 * found by the engine
 *@see HiLo
 * @author Tim Panepinto
 */
public class HiLo extends Object implements Guesser<Integer>{
	//these double values hold the current hi and lo values and total contains
	//the amount of total number of numbers there are in the range originally given
	private double lo, hi, total;
	//This tells the engine if the game is solved yet
	private boolean solved;
	
	//This is the current guess that the user will be presented with
	private int currGuess = 0;
	//boolean telling the engine if the game has been initialized
	private boolean init;
	//boolean telling engine if there has been a question made
	private boolean qMade;
	//private boolean guessing;
	//boolean telling engine if the game has started
	private boolean start;
	private double lastProg;
	/** Constructor that makes the new game engine
	 *
	 * @param min the minimum value for the game
	 * @param max the maximum value for the game
	 */
	public HiLo(int min, int max)
	{
		if (min>max)
			throw new IllegalArgumentException();
		else{
			hi = max;
			lo = min;
			total = max-min;
			solved = false;
			init = false;
			qMade = false;
		}
		
		//guessing = false;		
		
	}
	 /** Initializes the guessing engine.  This method must be called to
	   * initiate a problem before the rounds of questions and answers begin.
	   *
	   * @return An initialization message that can be displayed to the user
	   */
	@Override
	public String initialize() {
		//Initialize all the values the are in the class
		init = true;
		qMade = false;
		start = true;
        lastProg = 0;
	    //if the user gives no range assume the secret is solved
		if ( lo == hi)
		{ 
			solved = true;
			currGuess = (int)lo;
			return "The secret is: " + currGuess;
		}
		else
			return "Pick a number between "+ (int)lo +" and " + (int)hi;
		
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
        {
            if (hi == lo)
            {
                currGuess = (int) hi;
                solved = true;
                return true;
            }
            else if ( (hi - lo) == 1)
            {
                if (currGuess == lo)
                {
                    solved = true;
                    return true;
                }
                else
                {
                    solved = true;
                    return true;
                }
            }
            else
                return false;
        }

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
	public Integer getSecret() {
		
		if (solved && init)
			return currGuess;
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
		
		if ( init && qMade )
		{	
			lo = currGuess;
			currGuess = (int)Math.ceil((hi-lo)/2) + currGuess;


			
		//progress = progress();
		}
		else
			throw new IllegalStateException();
		qMade = false;
		start = false;
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
		
		
		if ( init && qMade)
		{
			
			hi = currGuess;
			currGuess = (int)Math.ceil((hi/2));
			

		}
		else
			throw new IllegalStateException();
		qMade = false;
		start = false;
	
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
		
		if(qMade)
            throw new IllegalStateException();
        else if ( !init )
            throw new IllegalStateException();
        else if( solved )
            throw new IllegalStateException();
        else
		{
			qMade = true;
			if (solved)
				return "The secret is: " + currGuess;
			else
				return "Is your number larger than " + currGuess + "?";
		
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
		if ( init )
		{
			if ( solved )
				return 1.0;
			else if ( start )
				return 0.0;
			else
            {
                lastProg = lastProg + 0.1;
                return lastProg;

            }
		}
		else
			throw new IllegalStateException();
	}
	
}