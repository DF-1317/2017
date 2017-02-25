import java.io.*;
import java.net.*;
import java.util.*;

import com.fasterxml.jackson.jr.ob.JSON;

class UDPServer {
	class Coord {
		double x;
		double y;
	}
  public static void main(String args[]) throws Exception {
    DatagramSocket serverSocket = new DatagramSocket(5000);
    byte[] rData = new byte[1024];
    byte[] sData = new byte[1024];
    
    while(true) {
      DatagramPacket rPacket = new DatagramPacket(rData,rData.length);
      serverSocket.receive(rPacket);
      String msg = new String( rPacket.getData() );
      System.out.println("Got: " + msg);
      Map<String,Object> c = JSON.std.mapFrom(msg);
      System.out.println(c);
    }
  }
}
