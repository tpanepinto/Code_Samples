package cs671.pig.server;

import cs671.pig.Strategy;
import cs671.pig.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tim on 3/26/14.
 */
public class ProtocolToStrategy implements Strategy {

    String name;
    BufferedReader reader;
    BufferedWriter writer;
    Strategy strat;

    boolean rolling = false;
    boolean op = false;
    boolean gameStarted = false;

    public ProtocolToStrategy(String n, BufferedReader r, BufferedWriter w)
    {
        name = n;
        reader = r;
        writer = w;
        //strat = Utils.createStrategy("UserTerminalStrategy");

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
        if(!gameStarted || op)
            throw new IllegalStateException("Cannot roll right now");
        else {
            rolling = true;
            Message m;
            boolean b;

            if (value == 1) {
                m = Message.makeDIE(value);
                try {
                    m.send(writer);
                } catch (Exception e) {
                    e.getCause();
                }
                rolling = false;
                return true;
            }

            m = Message.makeDIE(value);
            try {
                m.send(writer);
            } catch (Exception e) {
                e.getCause();
            }
            try {
                m = Message.parse(reader.readLine());
            } catch (IOException | Message.Exception e) {
                e.getCause();
                e.printStackTrace();
            }
            if (m.header == Message.Header.DECIDE) {
                if (m.content.equals("HOLD")) {
                    rolling = false;
                    return false;
                }
                else if (m.content.equals("ROLL")) {
                    rolling = false;
                    return true;
                }
                else {
                    rolling = false;
                    throw new IllegalArgumentException("not defined in protocol");
                }

            } else{
                rolling = false;
                throw new IllegalArgumentException("not defined in protocol");
            }


        }

        //return true;
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
        if(!gameStarted || rolling)
            throw new IllegalStateException("Opponent cannot go right now");
        else {
            op = true;
            String str;
            Message m;
            if (count < 1)
                throw new IllegalArgumentException("Count is less than 1!");
            for (int i = 0; i < values.length; i++) {
                if (values[i] < 1 || values[i] > 6)
                    throw new IllegalArgumentException("Value at " + i + " is not a correct value");
                if (i != values.length - 1 && values[i] == 1)
                    throw new IllegalArgumentException("There is a 1 in the non last position");
            }

            m = Message.makeDICE(values, count);
            try {
                m.send(writer);
            } catch (Message.Exception e) {
                e.getCause();
                e.printStackTrace();

            }
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
        Message m;
        String str = "";
        try {
                str = reader.readLine();
        }
        catch(IOException e)
        {
            e.getCause();
            e.printStackTrace();
        }
        if (iStart)
            m = Message.makeSTART("YES");
        else
            m = Message.makeSTART("NO");

        try {
            m.send(writer);
            m = Message.parse(str);
        }
        catch(Message.Exception e)
        {
            e.getCause();
            e.printStackTrace();

        }



        if (m.header.equals(Message.Header.START))
        {
            if(m.content.equals("YES")){
                gameStarted = true;
                return true;
            }

            else if(m.content.equals("NO"))
                return false;
            else
                throw new IllegalArgumentException(m.content + " is not defined in protocol");
        }
        else
            throw new IllegalArgumentException(m.content + " is not defined in protocol");

//        String str;
//        Message m;
//        boolean start = false;
//        try {
//            str = reader.readLine();
//            m = Message.parse(str);
//            if (m.header == Message.Header.START)
//            {
//                if (m.content == "YES" || m.content == "yes")
//                    start = true;
//                else
//                    start = false;
//                start = strat.startGame(iStart);
//                m = Message.makeINIT(m.content);
//                m.send(writer);
//            }
//        }
//        catch ( IOException | Message.Exception e)
//        {
//            e.getCause();
//            e.printStackTrace();
//        }


        //take in start yes.
        //convert string to bool
        //boolean b strat.startGame(bool);
       // message m init
         //       m.send();




    }

    /**
     * Ends a game.
     *
     * @param info comments on the game from the the authority that ran it
     */
    @Override
    public void endGame(String info) {
        Message m;

        m = Message.makeEND(info);
        try {
            m.send(writer);

        }
        catch(Message.Exception e)
        {
            e.getCause();
            e.printStackTrace();

        }
    }

    /**
     * The name of this strategy.
     */
    @Override
    public String getName() {
        return name;
    }
}
