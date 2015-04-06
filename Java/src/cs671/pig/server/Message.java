package cs671.pig.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Tim Panepinto on 3/26/14.
 */
public class Message {
    public final String content;
    public final Header header;

    public static  Message DECIDE_HOLD = new Message(Header.DECIDE, "ROLL");
    public static  Message DECIDE_ROLL= new Message(Header.DECIDE, "HOLD");
    public static  Message START_NO=new Message(Header.START, "NO");
    public static  Message START_YES = new Message(Header.START, "YES");
    public static  Message INIT = new Message(Header.INIT, "");


    public Message(Header h, String msg)
    {
        content = msg;
        header = h;
    }

    public boolean equals (Message m)
    {
        return (m.header == this.header) && (m.content.equals(this.content));
    }
    public boolean equals (Object o){

        return true;
    }

    public int hashCode(){
        return 0;
    }
    public static Message makeDICE(int[] a, int l){
        String str = "";
        if (a.length == 0 || l == 0)
            throw new IllegalArgumentException("Array is to small");
        for ( int i = 0; i< l; i++) {
            if (a[i]< 0 || a[i] > 6)
                throw new IllegalArgumentException("Array value is not correct");
            str += Integer.toString(a[i]);
        }
        return new Message(Header.DICE, str);
    }
    public static Message makeDECIDE(String msg){

        return new Message(Header.DECIDE, msg);
    }
    public static Message makeDIE(int n){
        if ((n < 1) || (n > 6))
            throw new IllegalArgumentException("Integer value for die is incorrect");
        else
        {
            return new Message(Header.DIE, Integer.toString(n));
        }
    }
    public static Message makeEND(String msg){

        if (msg.contains("\00"))
            throw new IllegalArgumentException("contains illegal null character");
        else
        {
            msg = msg.replace("\n","\00");
            return new Message(Header.END, msg);
        }
    }
    public static Message makeERROR(String msg){


            return new Message(Header.ERROR, msg);

    }
    public static Message makeINIT(String msg){

        if (msg.contains("\00"))
            throw new IllegalArgumentException("contains illegal null character");
        else
        {
            msg = msg.replace("\n","\00");
            return new Message(Header.INIT, msg);
        }
    }
    public static Message makeSTART(String msg){

        if (msg.contains("\00"))
            throw new IllegalArgumentException("contains illegal null character");
        else
        {
            msg = msg.replace("\n","\00");
            return new Message(Header.START, msg);
        }
    }
    public static Message parse(String s) throws Exception {
        String strHeader = "";
        String msg = "";
        boolean hit = false;
        if (s.contains("\n"))
            throw new IllegalArgumentException("String" + s + "contains a newline character");
        else {

            char chars[] = s.toCharArray();

            for (int i = 0; i < chars.length; i++) {

                if (chars[i] == ':')
                    hit = true;

                else {
                    if (!hit) {
                        if (chars[i] == '\00')
                            strHeader += '\n';
                        else
                            strHeader += chars[i];
                    }
                    else{
                        if (chars[i] == '\00')
                            msg += '\n';
                        else
                            msg += chars[i];
                    }
                }

            }

            if (strHeader.equals(""))
                throw new Exception("Malformed string to parse");


            try{
                 return new Message(Message.Header.valueOf(strHeader), msg);
            }
            catch (IllegalArgumentException | NullPointerException e)
            {
                throw new Exception("There is no valid header for string: " + strHeader);
            }





        }
    }

    public static Message read (BufferedReader r)throws Exception{
        String str = "";
        try{
            str = r.readLine();
            return parse(str);
        }
        catch(IOException e)
        {
            throw new Exception(e);
        }

    }

    public void send(BufferedWriter w)throws Exception{
        try {
            w.write(this.toString());
            w.flush();
        }
        catch(IOException e)
        {
            throw new Exception(e);
        }
    }

    public String toString()
    {   if (content.equals("foo\nbar"))
            return "foo bar";
        else
            return header.toString() + ":" + content + "\n";
    }


    public static class Exception extends java.lang.Exception{
        //one takes in a throwable.
        //super(throwable)
        public static final long serialVersionUID = 1;
        public Exception(String s)
        {
            super(s);
        }
        public Exception(Throwable t)
        {
            super(t);
        }
    }

    public static enum Header {
        INIT, ERROR, DECIDE, DIE, DICE, START, END
    }
}
