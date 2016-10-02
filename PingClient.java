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

/* UDP Ping Client */

public class PingClient {

    public static void main(String[] args) throws Exception { 
        
       
// Get command line argument.
        if (args.length != 2) {
            System.out.println("Required arguments: <host> <port> (i.e., the server IP address and its port)");
            return;
        }
        int port = Integer.parseInt(args[1]);

// Create a datagram socket for receiving and sending UDP packets
// through the port specified on the command line.
        DatagramSocket socket = new DatagramSocket(port);
// Create a datagram packet to hold incomming UDP packet.
            DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
// Block until the host receives a UDP packet.
//          socket.receive(request);


InetAddress serverAddress = InetAddress.getByName(args[0]);  // Server address
// Convert input String to bytes using the default character encoding

// Assign server port
int serverPort = Integer.parseInt(args[1]);

int sequenceCount = 0;

// Initialize the variables we'll need for the ping and results metrics
long sentTime = 0;
long receivedPacketTime = 0;
long roundTripTime = 0;
long min = -1;
long max = -1;
int totalTimes = 0;

// This section is taken and modified from the booksite's UDPEchoClient.java
// For 10 seconds, we'll make 10 Echo attempts
boolean minmaxSet = false;
boolean receivedResponse = false;
do {
      // Store the current date (computer date in milliseconds) to timeStamp
        long timeStamp = new Date().getTime();
        String stringToSend = "PING " + sequenceCount + " " + timeStamp + "\n";
        // Format our string into a byte array for the transmission
        byte[] bytesToSend = stringToSend.getBytes(StandardCharsets.UTF_8);

      receivedResponse = false; // Each ping loop, we initialize this flag false until a response is received.
      DatagramPacket sendPacket = new DatagramPacket(bytesToSend,  // Sending packet
        bytesToSend.length, serverAddress, serverPort);
      socket.send(sendPacket);          // Send the ping string
      // sentTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
      sentTime = new Date().getTime(); // Mark the time we sent the ping
      socket.setSoTimeout(1000); // Set the timeout delay of 1000ms
      
      try {
        DatagramPacket receivePacket =                              // Receiving packet
        new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);
        
        socket.receive(receivePacket);  // Attempt ping reply reception
        
        if (!receivePacket.getAddress().equals(serverAddress))  // Check source
          throw new IOException("Received packet from an unknown source");

        receivedResponse = true;
      } catch (InterruptedIOException e) {  // We did not get anything
        System.out.println("Ping number " + (sequenceCount + 1) + " of 10 timed out.");
      }
      
     
      
        if (receivedResponse) {
            // initialTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
            receivedPacketTime = new Date().getTime();
            // receivedPacketTime = Integer.parseInt(new SimpleDateFormat("S").format(new Date()));
            
            roundTripTime = receivedPacketTime - sentTime;
            System.out.println("Ping response received. Latency: " + roundTripTime);
            
            // System.out.println("Received: " + new String(receivePacket.getData()) + );
        }
        
      
        // "naive method", we set the first ping response timings as both min and max
        if (minmaxSet == false && receivedResponse == true) {
            min = receivedPacketTime - sentTime;
            max = min;
            minmaxSet = true;
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
      
    } while (sequenceCount < 10);


    
    // Print ping statistics
    if (min == -1 || max == -1) {
        System.out.println("No ping responses were received over 10 attempts.");
    }
    else {
        System.out.println("Min latency: " + min);
        System.out.println("Max latency: " + max);
        int avgRTT = totalTimes / 10;
        System.out.println("Average Round Trip Time: " + avgRTT);
    }
  }
}
