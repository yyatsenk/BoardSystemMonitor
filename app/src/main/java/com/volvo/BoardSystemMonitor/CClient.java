package com.volvo.BoardSystemMonitor;


import java.io.*;
import java.net.*;
import android.widget.Toast;
import android.content.Context;


public class  CClient  extends Thread{
    private Socket socket;
    private String ServerIP = "172.22.55.120";
    private Context MainContext;
    public  CClient (Context cxt) {
        MainContext = cxt;
    }

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
        Send("GET_SYSTEM_INFO");
    }
    public void Send(String s)
    {
        try
        {
            PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());

            outToServer.print(s + "\n");
            outToServer.flush();

            StringBuffer inputLine = new StringBuffer();
            String tmp;

            while ((tmp = inputFromClient.readLine()) != null) {
                inputLine.append(tmp);
                System.out.println(tmp);
            }

            //use inputLine.toString(); here it would have whole source
            inputFromClient.close();
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
