// $Id: GuesserTextUI.java 856 2013-11-06 15:38:08Z charpov $

package cs671;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

/** Stub of the text-based user interface for guessing games. */
public class GuesserTextUI {
	private static Guesser<?> currentGuesser;
	
	private java.io.Reader guessInput;
	private java.io.Writer guessOutput;
  /** Builds a user interface for the given guesser.  Questions are
   *  displayed on <code>output</code> and user input is read from
   *  <code>input</code>.
   */
   public GuesserTextUI
    (Guesser<?> g, java.io.Reader input, java.io.Writer output) {
     currentGuesser = g;
     guessInput = input;
     guessOutput = output;
     
     
  }

  /** Builds a user interface for the given guesser.  Questions are
   *  displayed on <code>System.out</code> and user input is read from
   *  <code>System.in</code>.
   */
   public GuesserTextUI (Guesser<?> g) {
     currentGuesser = g;
    guessOutput = new PrintWriter(System.out, true);
    guessInput = new InputStreamReader(System.in);
    
     
	   
  }

   /** Plays the current game/guesser
    */
  public int play () {
    int numPlayed = 0;
    PrintWriter printer= new PrintWriter(guessOutput, true);
    Scanner sc = new Scanner(guessInput);
    
    while(true)
    {

    	numPlayed++;
    	try{
    		
    		printer.println(currentGuesser.initialize());
    		while(currentGuesser.hasSolved() == false)
    		{
    			String q = currentGuesser.makeQuestion();
    			printer.println(q);
    			
    			boolean hasAnswered = false;
    			while (hasAnswered == false)
    			{
    				String yOrN = sc.next();
                    //System.out.println(yOrN);
	    			if("y".equalsIgnoreCase(yOrN))
	    			{
	    				currentGuesser.yes();
	    				hasAnswered = true;
	    			}
	    			else if("n".equalsIgnoreCase(yOrN))
	    			{
	    				currentGuesser.no();
	    				hasAnswered = true;
	    			}

    			}
    			hasAnswered = false;
    		}
    		printer.println("The secret is: " + currentGuesser.getSecret());
    		printer.println("Play again?");
            String yOrN = sc.next();

            while( ("n".equalsIgnoreCase(yOrN)) == false && ("y".equalsIgnoreCase(yOrN) == false)) {
                yOrN = sc.next();
            }


            if("n".equalsIgnoreCase(yOrN))
            {
                sc.close();
                break;
            }


    		
    		
    		
    	}
    	catch (Exception e)
    	{ System.out.println(e);}
    }
    
	  return numPlayed;
  }

  private static void usage () {
    System.err.println
      ("Usage: TextUI -hilo min max\n"+
       "   or: TextUI -liar #lies name1 [name2 ...]\n"+
       "   or: TextUI -liar #lies -file filename #names");
  }

  /** Starts a command-line program.  This program can be started in 3
   * different ways:
   *<pre>
   * Usage: TextUI -hilo min max
   *    or: TextUI -liar #lies name1 [name2 ...]
   *    or: TextUI -liar #lies -file filename #names
   *</pre>
   *
   * The last form takes the names of the secret objects from a file
   * and the last parameter specifies how many of these are actually used in
   * the game.
   *
   * @param args command-line parameters
   * @see Liar#selectCandidates
   * @throws java.io.IOException if the file of names cannot be opened and read
   */
  public static void main (String[] args) throws java.io.IOException {
    if (args.length < 3) {
      usage();
      return;
    }
    Guesser<?> guesser = null;
    if (args[0].equals("-hilo"))
      guesser = makeHiLoGuesser(args);
    else if (args[0].equals("-liar"))
      guesser = makeLiarGuesser(args); // args.length > 2
    if (guesser == null) {
      usage();
      return;
    }
    int n = new GuesserTextUI(guesser).play();
    System.out.printf("(%d game", n);
    if (n > 1)
      System.out.printf("s");
    System.out.println(" played)");
  }

  /** Builds a HiLo guesser from the arguments passed in
   * @return a new HiLo guesser
   * @see HiLo
   */
  private static Guesser<Integer> makeHiLoGuesser (String[] args) {
	  int min;
	  int max;
	  if(args.length > 3)
		  throw new IllegalArgumentException();
	  else
	  {
		  min = Integer.parseInt(args[2]);
		  max = Integer.parseInt(args[3]);
		  Guesser<Integer> hiLoGuesser = new HiLo(min,max);
		  return hiLoGuesser;
		  
	  }
	  
  }

  /** Builds a Liar guesser from the arguments passed in
   * @return a new Liar guesser
   * @see Liar
   */
  private static Guesser<Liar.Secret<String>> makeLiarGuesser (String[] args) {
	  int lies = Integer.parseInt(args[1]);
	  if ("-file".equals(args[2]))
	  {
		  String fileName = args[3];
		  int selectCand = Integer.parseInt(args[4]);
		  HashSet<String> candidates = new HashSet<>();
		  
		  Scanner sc = new Scanner(fileName);
		  while(sc.hasNext())
			  candidates.add(sc.next());
		  sc.close();
		  Liar<String> liarGuesser= new Liar<>(candidates, lies, "string");
		  liarGuesser.selectCandidates(selectCand);
		  
		  return liarGuesser;
	  }
	  else
	  {
		  HashSet<String> candidates = new HashSet<>();
		  for ( int i = 2; i< args.length - 1; i++)
		  {
			  candidates.add(args[i]);
		  }
		  Liar<String> liarGuesser= new Liar<>(candidates, lies, "string");
		  return liarGuesser;
	  }
  }
  
  
}
