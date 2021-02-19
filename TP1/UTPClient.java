import java.io.*;
import java.net.*;
    
public class UTPClient{
    public static void main(String [] args) throws IOException{
        DatagramSocket clientSocket = new DatagramSocket(); 
        InetAddress address = InetAddress.getByName(args[0]);

        String ex = "Hello, World!";
        byte[] buf = ex.getBytes();

        DatagramPacket packet = new DatagramPacket(buf,buf.length, address, 4445); 
        clientSocket.send(packet);
    }
}