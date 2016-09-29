/*
As a template, we used the class slides (Sockets-big.pdf) and the textbook website for TCP/IP Sockets in Java
Namely, URL: http://cs.baylor.edu/~donahoo/practical/JavaSockets/textcode.html , Date Accessed: week of 9/25/16
As a base to modify, we renamed the PingServer client to PingClient, then used parts from UDPEchoClientTimeout.java and other examples from this booksite.
Stack Overflow: http://stackoverflow.com/questions/18571223/how-to-convert-java-string-into-byte
 */
package pingclient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.text.SimpleDateFormat;


/*
* Server to process ping requests over UDP.
 */
public class PingClient {

    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 100; // milliseconds

    public static void main(String[] args) throws Exception { 
        
       
// Get command line argument.
        if (args.length != 2) {
            System.out.println("Required arguments: <host> <port> (i.e., the server IP address and its port)");
            return;
        }
        int port = Integer.parseInt(args[1]);
// Create random number generator for use in simulating
// packet loss and network delay.
        Random random = new Random();
// Create a datagram socket for receiving and sending UDP packets
// through the port specified on the command line.
        DatagramSocket socket = new DatagramSocket(port);
// Processing loop.
        while (true) {
// Create a datagram packet to hold incomming UDP packet.
            DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
// Block until the host receives a UDP packet.
            socket.receive(request);

/* Presumed Server Part
// Print the received data.
            printData(request);
*/

InetAddress serverAddress = InetAddress.getByName(args[0]);  // Server address
// Convert input String to bytes using the default character encoding

// Assign server port
int servPort = Integer.parseInt(args[1]);

int sequenceCount = 0;
// How do we format the time in the message string?
String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
String stringToSend = "PING " + sequenceCount + timeStamp + "\r\n";
byte[] bytesToSend = stringToSend.getBytes(StandardCharsets.UTF_8);
DatagramPacket sendPacket = new DatagramPacket(bytesToSend,  // Sending packet
        bytesToSend.length, serverAddress, servPort);
DatagramPacket receivePacket =                              // Receiving packet
        new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);
// int roundTripTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
int roundTripTime = 0;
int min = 0;
int max = 0;
int totalTimes = 0;
int sentTime = 0;
int receivedPacketTime = 0;

// This section is taken and modified from the booksite's UDPEchoClient.java
// For 10 seconds, we'll make 10 Echo attempts

boolean receivedResponse = false;
do {
      socket.send(sendPacket);          // Send the ping string
      sentTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
      
      try {
        socket.receive(receivePacket);  // Attempt ping reply reception
        
        if (!receivePacket.getAddress().equals(serverAddress))  // Check source
          throw new IOException("Received packet from an unknown source");

        receivedResponse = true;
      } catch (InterruptedIOException e) {  // We did not get anything
        System.out.println("Timed out, " + (10-sequenceCount) + " more tries...");
      }
      
     
      
        if (receivedResponse) {
            int initialTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
            receivedPacketTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
            
            roundTripTime = receivedPacketTime - sentTime;
            System.out.println("Ping response received. Latency: " + roundTripTime);
            
            // System.out.println("Received: " + new String(receivePacket.getData()) + );
        }
        else {
            System.out.println("No response -- giving up.");
        }
      
        // "naive method", we set the initial ping result as both min and max
        if (sequenceCount == 0) {
            min = receivedPacketTime - sentTime;
            max = receivedPacketTime - sentTime;
        }
        
        if(roundTripTime > max)
        {
            max = roundTripTime;
        }
        if(roundTripTime < min)
        {
            min = roundTripTime;
        }
        
        totalTimes += roundTripTime; // add RTT to sum of times
        sequenceCount++; // Increment 'til 10 tries, success or fail, are made
      
    } while ((!receivedResponse) && (sequenceCount < 10));


    
    socket.close();
        // Print results
    System.out.println("Max: " + max);
    System.out.println("Max: " + min);
    int avgRTT = totalTimes / 10;
    System.out.println("Average Round Trip Time: " + avgRTT);

  }
 }
}
/*
// Decide whether to reply, or simulate packet loss.
            if (random.nextDouble() < LOSS_RATE) {
                System.out.println(" Reply not sent.");
                continue;
            }
            if (random.nextDouble() < LOSS_RATE) {
                System.out.println(" Reply not sent.");
                continue;
            } else {
*/

/* Presumed server part
// Simulate network delay.
                Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
// Send reply.
                InetAddress clientHost = request.getAddress();
                int clientPort = request.getPort();
                byte[] buf = request.getData();
                DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
                socket.send(reply);
                System.out.println(" Reply sent.");
            }
        }
    }
*/

    /*
* Print ping data to the standard output stream.
     */

/*
    private static void printData(DatagramPacket request) throws Exception {
// Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();
// Wrap the bytes in a byte array input stream,
// so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
// Wrap the byte array output stream in an input stream reader,
// so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);
// Wrap the input stream reader in a bufferred reader,
// so you can read the character data a line at a time.
// (A line is a sequence of chars terminated by any combination of \r and \n.)
        BufferedReader br = new BufferedReader(isr);
// The message data is contained in a single line, so read this line.
        String line = br.readLine();
// Print host address and data received from it.
        System.out.println(
                "Received from "
                + request.getAddress().getHostAddress()
                + ": "
                + new String(line));
    }
}
*/
