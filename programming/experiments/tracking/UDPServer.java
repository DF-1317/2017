import java.io.*;
import java.net.*;
import java.util.*;

import com.fasterxml.jackson.jr.ob.JSON;

/*
 * To build:
 * javac -cp jackson-jr.jar UDPServer.java
 */
class UDPServer {
  public static void main(String args[]) throws Exception {
    DatagramSocket serverSocket = new DatagramSocket(5800);
    byte[] rData = new byte[1024];
    serverSocket.setSoTimeout(1);
    
    while(true) {
      DatagramPacket rPacket = new DatagramPacket(rData,rData.length);
      try {
	      serverSocket.receive(rPacket);
	      String msg = new String( rPacket.getData() );
	      System.out.println("Got: " + msg);
	      Map<String,Object> c = JSON.std.mapFrom(msg);
	      System.out.println(c);
      } catch (SocketTimeoutException e) {}
    }
  }
}
