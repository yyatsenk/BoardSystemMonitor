package com.volvo.BoardSystemMonitor;


import java.io.*;
import java.net.*;
import android.widget.Toast;
import android.content.Context;


public class  CClient  extends Thread{
    private Socket socket;
    private String ServerIP = "172.22.55.120";
    private Context MainContext;
    private PrintWriter outToServer;
    private DataInputStream inputFromServer;
    private String SendData = null;
    public  CClient (Context cxt, String data) {
        MainContext = cxt;
        SendData = data;
    }

    public void run() {
        try {
            try {
                InetAddress inetAddress = InetAddress.getByName(ServerIP);
                socket = new Socket(ServerIP, 5005);

                //getPort() method will return the port number of this socket
                System.out.println("Port number: " + socket.getPort());

                try {
                    outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    inputFromServer = new DataInputStream(socket.getInputStream());
                } catch (Exception e) {
                    System.out.print(e.toString());
                }
                Send(SendData);
            } catch (Exception e) {
                System.out.print("Whoops! It didn't work!:");
                System.out.print(e.getLocalizedMessage());
                System.out.print("\n");
            } finally {
                if (socket != null)
                    socket.close();
                if (outToServer != null)
                    outToServer.close();
                if (inputFromServer != null)
                    inputFromServer.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }


    }
    public void Send(String s)
    {
        try
        {
            outToServer.print(s + "\n");
            outToServer.flush();

            StringBuffer inputLine = new StringBuffer();
            String tmp;

            while ((tmp = inputFromServer.readLine()) != null) {
                System.out.println("FROM SERVER: " + tmp);
                inputLine.append(tmp);
            }
            System.out.println(inputLine);
            MainActivity.setRecieved(inputLine.toString());

        }
        catch (UnknownHostException e) {
            System.out.print(e.toString());
        } catch (IOException e) {
            System.out.print(e.toString());
        }catch (Exception e) {
            System.out.print(e.toString());
        }

    }
}
