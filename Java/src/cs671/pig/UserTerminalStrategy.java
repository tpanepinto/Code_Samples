package cs671.pig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by tim on 3/26/14.
 */
public class UserTerminalStrategy extends Object implements Strategy {
    String stratName;
    int myLastScore = 0;
    int curPlayScore = 0;
    int curOpScore = 0;
    int opLastScore = 0;
    Die die = new Die(42);

    boolean rolling = false;
    boolean op = false;
    boolean gameStarted = false;

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public UserTerminalStrategy(String name){
        stratName = name;
    }


    /**
     * Decides on a roll.
     *
     * @param value the value of the die
     * @return true to roll, false to hold
     * @throws IllegalStateException if called at a time that is
     *                               inconsistent with the rules of the game
     */
    @Override
    public boolean roll(int value) {
        if (!gameStarted || op)
            throw new IllegalStateException("Cannot Roll right now!");
        else {

            rolling = true;
            boolean b = false;

            curPlayScore += value;

            if (value == 1) {
                curPlayScore = 0;
                System.out.print("You roll " + value + ". " + "You Lose your turn and points.");

            } else {
                System.out.print("You roll " + value + ". Your Score is " + myLastScore + "+" + curPlayScore + "=" + (myLastScore + curPlayScore));
                System.out.print("     What is your decision (r/h)?");
                try {
                    String str = in.readLine();
                    if (str.equalsIgnoreCase("r"))
                        b = true;
                    else if (str.equalsIgnoreCase("h")) {
                        b = false;
                        myLastScore = myLastScore + curPlayScore;
                        curPlayScore = 0;
                    }
                } catch (IOException e) {
                    System.err.println("Sorry nothing was entered into the terminal");
                }
            }
            System.out.print("\n");

            rolling = false;
            return b;

        }
    }

    /**
     * Analyses the oponent play.
     *
     * @param values an array of dice values, only the first {@code
     *               count} of which are used
     * @param count  the number of times the opponent rolled the die
     * @throws IllegalStateException    if called at a time that is
     *                                  inconsistent with the rules of the game
     * @throws IllegalArgumentException if {@code count < 1}, {@code
     *                                  values.length < count}, values contains numbers smaller than 1 or
     *                                  larger than 6, or values contain 1 in a position other than the
     *                                  last.
     */
    @Override
    public void opponentPlay(int[] values, int count) {

        if (!gameStarted || rolling)
            throw new IllegalStateException("Opponent should not be going right now!");
        else {
            op = true;

            StringBuffer str = new StringBuffer("");
            boolean opLose = false;
            if (count < 1)
                throw new IllegalArgumentException("Count is less than 1!");
            for (int i = 0; i < values.length; i++) {
                if (values[i] < 1 || values[i] > 6)
                    throw new IllegalArgumentException("Value at " + i + " is not a correct value");
                if (i != values.length - 1 && values[i] == 1)
                    throw new IllegalArgumentException("There is a 1 in the non last position");
            }


            for (int i = 0; i < count; i++) {
                curOpScore += values[i];
                if (values[i] == 1) {
                    curOpScore = 0;
                    opLose = true;
                }
                str.append(Integer.toString(values[i]) + " ");
            }
            opLastScore = opLastScore + curOpScore;
            curOpScore = 0;
            if (opLose) {
                System.out.print("Your opponent plays: " + str.toString() + ". His/Her/It's score stays at " + opLastScore + "\n");

            } else
                System.out.print("Your opponent plays: " + str.toString() + ". His/Her/It's score is " + opLastScore + "\n");
            op = false;
        }
    }

    /**
     * Starts a new game.
     *
     * @param iStart is true iff this player plays first (i.e., it is
     *               false for the other player).
     * @return true if this strategy accepts to play another game, false otherwise
     */
    @Override
    public boolean startGame(boolean iStart) {
        boolean start = false;
        if (iStart)
            System.out.print("Do you want to start a game (you will play first)");
        else
            System.out.print("Do you want to start a game (you will not play first)");

        try {
            String str = in.readLine();
            System.out.println("Start Game: " + str);
            if (str.equalsIgnoreCase("y"))
                start = true;
            if (str.equalsIgnoreCase("n"))
                start = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        gameStarted = start;
        return start;
    }

    /**
     * Ends a game.
     *
     * @param info comments on the game from the the authority that ran it
     */
    @Override
    public void endGame(String info) {
        System.out.print(info);
    }

    /**
     * The name of this strategy.
     */
    @Override
    public String getName() {
        return stratName;
    }
}
