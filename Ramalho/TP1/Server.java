import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.lang.StringBuilder;

public class Server{


    public static void main(String [] args) throws IOException{
        
        // Initialize variables
        HashMap<String, Integer> dnsDatabase = new HashMap<String, Integer>();

        int port = getPort(args[0]);
  
        DatagramSocket serverSocket = new DatagramSocket(null);
        serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"),Integer.valueOf(port)));

        // Receive and Respond Loop
        while(true){
            StringBuilder responseBuilder = new StringBuilder(); // Used to generate the response

            DatagramPacket requestPacket = getRequest(serverSocket);

            if(resolveRequest(requestPacket.getData(), dnsDatabase, responseBuilder) != 0){
                System.out.println("Failed to resolve request.\n");
            }

            sendResponse(serverSocket, responseBuilder.toString(), requestPacket.getAddress(), requestPacket.getPort());
        }
        
    }

    private static int getPort(String portString){
        Integer port = 0;
        try{
            port = Integer.valueOf(portString);

        } catch(NumberFormatException e){
            System.out.println("Port given was not a number.\n");
            System.exit(1);
        }

        return port;
    }

    private static DatagramPacket getRequest(DatagramSocket serverSocket) throws IOException {
        byte[] rbuf = new byte[256]; // Request buffer

        // Create Request Packet
        DatagramPacket requestPacket = new DatagramPacket(rbuf, rbuf.length);   
        
        // Read and conver the request to string
        serverSocket.receive(requestPacket);

        return requestPacket;
    }

    private static int sendResponse(DatagramSocket serverSocket, String response, InetAddress address, int port) throws IOException{
        System.out.println("response : " + response + ".\n");
        byte[] buf = response.getBytes();
        
        DatagramPacket packet = new DatagramPacket(buf,buf.length, address, port); 
        serverSocket.send(packet);

        return 0;
    }

    private static int resolveRequest(byte[] requestData, HashMap<String, Integer> dnsDatabase, StringBuilder responseBuilder){
        String [] requestArr = (new String(requestData)).split(" ");
        int responseVal = 0;
        
        switch(requestArr[0]){
            case "REGISTER":
                responseVal = addEntry(requestArr[1], requestArr[2].trim(), dnsDatabase);

                responseBuilder.append(Integer.toString(responseVal));
                break;
            case "LOOKUP":

                responseVal = getEntry(requestArr[1].trim(), dnsDatabase);

                responseBuilder.append(requestArr[1].trim() + " " + responseVal);
                break;
            default:
                System.out.println("Wrong request format.\n");
                return -1;
        }

        return 0;
    }

    private static int addEntry(String dnsName, String dnsIP, HashMap<String, Integer> dnsDatabase){

        if(dnsDatabase.containsKey(dnsName)){
            return -1;
        }

        try{
            Integer ip = Integer.valueOf(dnsIP.trim());

            dnsDatabase.put(dnsName, ip);

            System.out.println("Server: Register " + dnsName + " " + ip + "\n");
        } catch(NumberFormatException e){
            System.out.println("IP addres given was not a number.\n");

            return -1;
        }
        

        return dnsDatabase.size();
    }

    private static int getEntry(String dnsName, HashMap<String, Integer> dnsDatabase){
        System.out.println("DNSname: " + dnsName + "\n.");
        if(dnsDatabase.containsKey(dnsName)){
            System.out.println("Server: Lookup " + dnsName + "\n");
            return dnsDatabase.get(dnsName);
        }
        else{
            System.out.println("The DNS name given doens't exist in the database.\n");
            return -1;
        }
    
    }
}