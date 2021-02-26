import java.io.*;
import java.net.*;
import java.util.HashMap;

public class UTPServer {

    public static void main(String[] args) throws IOException {
        HashMap pairs = new HashMap<>();

        DatagramSocket serverSocket = new DatagramSocket(4445);

        byte[] rbuf = new byte[256];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
        serverSocket.receive(packet);
        String response = new String(packet.getData()).toLowerCase();
        String[] arr = response.split(" ");
        String ip = new String(packet.getAddress().toString()).toLowerCase();

        switch (arr[0]) {
            case "register":
                addEntry(arr[1], ip, pairs);
                break;

            case "lookup":
                getEntry(arr[1], pairs);
                break;

            default:
                System.out.println("Response: " + response);
                System.out.println("ip: " + ip);
        }

    }

    private static void addEntry(String dnsName, String ip, HashMap pairs) {
        if (!pairs.containsKey(ip)) {
            pairs.put(ip, dnsName);
            System.out.println("Sucessefully registered ip: " + ip + " under the name " + dnsName + '.');
        }
    }

    private static <K, V> void getEntry(String dnsName, HashMap<K, V> pairs) {
        for (K key : pairs.keySet()) {
            if (dnsName.equals(pairs.get(key))) {
                System.out.println("The ip: " + key + " is associated to the DNS name provided.");
                return;
            }
        }

        System.out.println("There is no ip address associated with the DNS name provided.");
    }
}