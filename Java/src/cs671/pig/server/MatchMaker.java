package cs671.pig.server;

import cs671.pig.Competition;
import cs671.pig.Strategy;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tim on 3/26/14.
 */
public class MatchMaker extends Object {
    static int LIMIT;
    private final int port;
    private ServerSocket servSocket;
    private ArrayList<Thread> connectedClients;
    private Socket socket;
    private final Object lock;
    private Thread listenerThread;
    private boolean servRun;
    private int playCount = 1;
    BufferedReader in = null;
    BufferedWriter out = null;
    private ArrayList<BufferedReader> buffReads;
    private ArrayList<BufferedWriter> buffWrites;

    public MatchMaker(int p)
    {
        port = p;
        servRun = false;
        listenerThread = new Thread(new myServer());
        connectedClients = new ArrayList<>();
        buffReads = new ArrayList<>();
        buffWrites = new ArrayList<>();
        lock = new Object();
    }

    public void start()
    {
        if (servRun)
            throw new IllegalStateException("Server is already running");
        else
        {
            servRun = true;
            listenerThread.start();
        }
    }


    public synchronized void stop()
    {
        servRun = false;
        for (Iterator<Thread> i = connectedClients.iterator(); i.hasNext();) {
            Thread t = i.next();
            t.interrupt();
        }
        listenerThread.interrupt();
    }

    public class myServer implements Runnable{

        Message message;
        String name = "";


        @Override
        public synchronized void run() {
            InetAddress addr = null;

            try{
                addr = InetAddress.getByName("127.0.0.1");
            }
            catch( UnknownHostException e)
            {

            }


               try {
                    System.out.println("listening at: " + addr + " port: " + port);
                    //addr = InetAddress.getLocalHost();
                    servSocket = new ServerSocket(port, 0, addr);
                   while (servRun) {
                       socket = servSocket.accept();
                       socket.setKeepAlive(true);
                   //InetAddress sockInfo = socket.getInetAddress();
                   in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                   out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


                    synchronized (lock) {
                        Thread t = new Thread(new MyClient(socket));
                        t.start();
                    }
                    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

                } catch (IOException e) {
                    System.err.println(e.getCause());
                    e.printStackTrace();
                }
            finally {
                    try {
                        socket.close();

                    }
                    catch(IOException e)
                    {
                        System.err.println(e.getCause());
                        e.printStackTrace();
                    }
                }
            }
    }


    public class MyClient implements Runnable{

        Socket clientSocket;

        BufferedReader clientIn;
        BufferedWriter clientOut;
        Message message;
        String name = "";


        public MyClient(Socket s)
        {

            clientSocket = s;
            //name = n;
            try {
                clientIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            }
            catch (IOException e)
            {
                System.err.println(e.getCause());
            }

        }
        @Override
        public synchronized void run() {
            String str = "";
          //  while (true) {
//------------------------------------------------------------------------------------------------------------------


                boolean cont = true;
                message = Message.makeINIT("Welcome to my pig server");
                try{
                    message.send(out);
                    out.flush();
                    while(true) {
                        str = in.readLine();
                        if (str != null) {
                            message = Message.parse(str);

                            if (message.header == Message.Header.INIT) {
                                if (isValidName(message.content))
                                    name = message.content;
                                else {
                                    cont = false;
                                    socket.close();
                                }
                                break;


                            } else {
                                cont = false;
                                socket.close();
                                break;
                            }
                        }
                    }

                    while(cont) {

                        StringWriter tmpStr = new StringWriter(0);
                        synchronized (lock) {
                            for (int i = 0; i < connectedClients.size(); i++) {
                                tmpStr.append(connectedClients.get(i).getName());
                                tmpStr.append(",");
                            }
                            if (connectedClients.isEmpty())
                                out.write("Empty");
                        }
                        out.flush();
                        message = Message.makeINIT(tmpStr.toString());
                        message.send(out);
                        out.flush();
                        str = null;

                        while( str == null) {
                            str = in.readLine();
                        }

                        message = Message.parse(str);
                        if (message.header == Message.Header.INIT) {
                            if (message.content.equals("\n") || message.content.equals("")) {
                                Thread t = Thread.currentThread();
                                //t.interrupt();
                                t.setName(name);
                                synchronized (lock) {
                                    connectedClients.add(t);
                                    buffReads.add(in);
                                    buffWrites.add(out);
                                }
                                out.write("Added");
                                out.flush();
                                break;
                            } else {
                                boolean there = false;
                                Thread match = null;
                                BufferedReader matchIn =null;
                                BufferedWriter matchOut = null;
                                synchronized (lock) {
                                    for (int i = 0; i < connectedClients.size(); i++) {
                                        if (connectedClients.get(i).getName().equals(message.content)) {
                                            there = true;
                                            match = connectedClients.get(i);
                                            matchIn = buffReads.get(i);
                                            matchOut = buffWrites.get(i);

                                            connectedClients.remove(i);
                                            buffReads.remove(i);
                                            buffWrites.remove(i);
                                            break;
                                        } else
                                            there = false;
                                    }
                                }
                                if (there)
                                {
                                    message = Message.makeINIT(match.toString());
                                    message.send(out);
                                    //match.start();
                                    message = Message.makeINIT(name);
                                    message.send(matchOut);

                                    Strategy strat1 = new ProtocolToStrategy(name, in, out);
                                    Strategy strat2 = new ProtocolToStrategy(match.getName(), matchIn, matchOut);
                                    Competition comp = new Competition(strat1,strat2);
                                    comp.play(playCount);
                                }

                            }


                        }
                    }

                } catch (IOException | Message.Exception e) {
                    e.printStackTrace();
                }
           // }










//------------------------------------------------------------------------------------------------------




            }

        public synchronized String getName()
        {
            return name;
        }
    }

    public static boolean isValidName(String name)
    {
        boolean b = true;

        if (name == null)
            b= false;

        for ( int i = 0; i< name.length(); i++)
        {
            if ( name.charAt(i) == '\n' || name.charAt(i) == ',')
                b = false;
        }
        return b;
    }

    public static void main( String[] args)
    {
//       MatchMaker match = new MatchMaker(56231);
//       match.start();

         int portNum = 0;
        MatchMaker match;
        if (args.length < 1 | args.length > 1)
            System.err.println("Wrong usage of server");
        else
        {
            portNum = Integer.parseInt(args[0]);
            if (portNum < 0 || portNum > 65535)
                System.err.println("Invalid port number [0..65535].");
            else
            {
                match = new MatchMaker(portNum);

                match.start();

            }
        }
//

    }




}
