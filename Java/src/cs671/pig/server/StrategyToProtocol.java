package cs671.pig.server;


import cs671.pig.Strategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


/**
 * Created by tim on 3/26/14.
 */
public class StrategyToProtocol implements Runnable{
    Strategy strat;
    BufferedReader in;
    BufferedWriter out;
    boolean endIt;
    public StrategyToProtocol(Strategy s, BufferedReader i, BufferedWriter o)
    {
        strat = s;
        in = i;
        out = o;

    }
    @Override
    public void run() {

        endIt = false;
        while( true ) {
            Message m = null;
            String str = "";
            if (endIt)
                break;
            try {
                str = in.readLine();
            } catch (IOException e) {
                e.getCause();
                e.printStackTrace();
            }
            if (str != null) {
                try {
                    m = Message.parse(str);
                } catch (Message.Exception e) {
                    e.printStackTrace();
                }

                if (m.header == Message.Header.START) {
                    boolean stratStart;
                    if (m.content.equals("YES"))
                        stratStart = strat.startGame(true);
                    else if (m.content.equals("NO"))
                        stratStart = strat.startGame(false);
                    else
                        throw new IllegalArgumentException("Incorrect Message content for START");
                    if (stratStart)
                        m = Message.makeSTART("YES");
                    else
                        m = Message.makeSTART("NO");
                    try {
                        m.send(out);
                    } catch (Message.Exception e) {
                        e.printStackTrace();
                    }


                } else if (m.header == Message.Header.DIE) {
                    boolean roll;


                    int die = Integer.parseInt(m.content);
                    //System.out.println(die);
                    roll = strat.roll(die);
                    if (die != 1) {
                        if (roll)
                            m = Message.makeDECIDE("ROLL");
                        else
                            m = Message.makeDECIDE("HOLD");
                        //System.out.println(m.toString());
                        try {
                            m.send(out);
                        } catch (Message.Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (m.header == Message.Header.DICE) {

                    int dice[] = new int[m.content.length()];
                    //int length = 0;
                    for (int i = 0; i < m.content.length(); i++) {
                        String tmpStr = "";
                        tmpStr += m.content.charAt(i);
                        dice[i] = Integer.parseInt(tmpStr);

                    }
                    strat.opponentPlay(dice, dice.length);


                }
                else if (m.header == Message.Header.END)
                {
                    strat.endGame(m.content);
                }

            }
            else
                break;
        }
    }
    public void endPlay(){
        endIt = true;
    }


}
