package org.usfirst.frc.team1317.robot;
import java.io.*;
import java.net.*;
import java.util.*;

import com.fasterxml.jackson.jr.ob.JSON;

class PacketReader {
	class Coord {
		double x;
		double y;
	}
	
	DatagramSocket serverSocket;
	byte[] rData;
	
	PacketReader()
	{
		rData = new byte[1024];
	}
	
	public void InitializeSocket()
	{
		try {
			serverSocket = new DatagramSocket(5800);
		} catch (SocketException e) {
			serverSocket = null;
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> getPacket()
	{
		if(serverSocket == null)
		{
			InitializeSocket();
		}
		if(serverSocket != null)
		{
			DatagramPacket rPacket = new DatagramPacket(rData,rData.length);
			serverSocket.receive(rPacket);
			String msg = new String( rPacket.getData() );
			System.out.println("Got: " + msg);
			Map<String,Object> c = JSON.std.mapFrom(msg);
			System.out.println(c);
			return c;
		}
		else return null;
	}
  public static void main(String args[]) throws Exception {
    DatagramSocket serverSocket = new DatagramSocket(5000);
    byte[] rData = new byte[1024];
    
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
