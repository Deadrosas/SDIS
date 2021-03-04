import java.io.*;
import java.net.*;
    
public class Client{
    public static void main(String [] args) throws IOException{
        String message = createRequest(args);
        int multicastPort = getPort(args[1]);

        MulticastSocket multiClientSocket = new MulticastSocket(multicastPort);
        multiClientSocket.joinGroup(InetAddress.getByName(args[0]));
        DatagramPacket responseAddressPacket = getResponse(multiClientSocket);
        String portName = new String(responseAddressPacket.getData());

        int port = getPort(portName);
        System.out.println("Port received: " + port + ".\n");

        DatagramSocket clientSocket = new DatagramSocket(); 
        InetAddress address = responseAddressPacket.getAddress();

        System.out.println("Address: " + address);

        if(sendRequest(clientSocket, message, address, port) != 0){
            System.out.println("Failed to send Request.");
            System.exit(1);
        }

        DatagramPacket responsePacket = getResponse(clientSocket);

        displayResponse(responsePacket.getData(), args);

        clientSocket.close();
    }

    private static void displayResponse(byte [] reponseData, String[] requestArgs){
        String responseReceived = new String(reponseData);
        String response = new String();

        for(int i = 2; i < requestArgs.length; i++){
            response = response + requestArgs[i] + " ";
        }

        response = response + ": ";
        
        if(responseReceived.contains("-1")){
            response = response + "ERROR\n";
        }
        else{
            response = response + responseReceived;
        }

        System.out.println(response);
    }

    private static int getPort(String portString){
        Integer port = 0;
        try{
            port = Integer.valueOf(portString.trim());

        } catch(NumberFormatException e){
            System.out.println("Port given was not a number.\n");
            System.exit(1);
        }

        return port;
    }

    private static String createRequest(String [] requestArgs){
        String message = new String();

        if(requestArgs[2].equals("REGISTER")){
            message = requestArgs[2] + ' ' + requestArgs[3] + ' ' + requestArgs[4];
        }
        else if(requestArgs[2].equals("LOOKUP")){
            message = requestArgs[2] + ' ' + requestArgs[3];
        }
        else{
            System.out.println("Message in a wrong format");
            return "";
        }

        return message;
    }

    private static DatagramPacket getResponse(DatagramSocket clientSocket) throws IOException {
        byte[] rbuf = new byte[256]; // Request buffer

        // Create Request Packet
        DatagramPacket responsePacket = new DatagramPacket(rbuf, rbuf.length);   
        
        // Read and conver the request to string
        clientSocket.receive(responsePacket);

        return responsePacket;
    }

    private static int sendRequest(DatagramSocket serverSocket, String request, InetAddress address, int port) throws IOException{

        byte[] buf = request.getBytes();
        
        DatagramPacket packet = new DatagramPacket(buf,buf.length, address, port); 
        serverSocket.send(packet);

        return 0;
    }
}