package com.volvo.BoardSystemMonitor;


import java.io.*;
import java.net.*;

public class CClient  extends Thread{
    private Socket socket;
    private String ServerIP = "192.168.0.102";
    public void run() {
        try
        {
            InetAddress inetAddress=InetAddress.getByName(ServerIP);
            socket = new Socket(ServerIP, 5005);

            //getPort() method will return the port number of this socket
            System.out.println("Port number: "+socket.getPort());
        }
        catch(Exception e)
        {
            System.out.print("Whoops! It didn't work!:");
            System.out.print(e.getLocalizedMessage());
            System.out.print("\n");
        }
        Send("Hello from Android");
    }
    public void Send(String s)
    {
        try
        {
            PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            outToServer.print(s + "\n");
            outToServer.flush();

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
