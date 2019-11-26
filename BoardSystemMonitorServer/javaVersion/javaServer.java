import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class javaServer {

    private static Socket clientSocket1; //сокет для общения
    private static ServerSocket server1;

    public static void main(String[] args) {
            try  {
                server1 = new ServerSocket(5007);
                System.out.println("Server: started");
                clientSocket1 = server1.accept();
            } catch (Exception e) {
                
            }
            finally {
                //System.out.println("Server: conection closed");
                    //server1.close();
            }
        }
};