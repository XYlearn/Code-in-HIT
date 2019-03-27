package com.netlab.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Date;

public class Server {
	
	private DatagramSocket socket;
	private Rdt rdt;
	
	public Server(Rdt rdt) {
		this.rdt = rdt;
	}
	
	public static void main(String[] args) throws SocketException {
		
		int port = 9981;
		DatagramSocket socket = new DatagramSocket(port);
		Rdt rdt = null;
		if (args.length == 0 || args[0].toLowerCase().equals("gbn"))
			rdt = new GBNRdt(socket);
		else
			rdt = new SRRdt(socket);
		
		Server server = new Server(rdt);
		
		DatagramPacket recvPacket = new DatagramPacket(new byte[0], 0);
		
		while (true) {
			try {
				String recv = server.recv(recvPacket);
				String resp = null;
				if (recv.equals("-time")) {
					resp = new Date().toString();
				} else if (recv.equals("-quit")) {
					resp = "Good Bye!";
				} else if (recv.equals("-test")) {
					continue;
				} else {
					resp = recv;
				}
				server.send(resp, recvPacket.getSocketAddress());
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void send(String s, SocketAddress socketAddress) {
		byte[] sendbuf = s.getBytes();
		DatagramPacket packet = new DatagramPacket(sendbuf, sendbuf.length, socketAddress);
		try {
			rdt.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String recv(DatagramPacket packet) {
		try {
			DatagramPacket recvPacket = rdt.receive();
			packet.setSocketAddress(recvPacket.getSocketAddress());
			packet.setData(recvPacket.getData());
			packet.setLength(recvPacket.getLength());
			String recv = new String(recvPacket.getData());
			return recv;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "";
		}
		
	}
}
