package com.netlab.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

public class Client {
	
	DatagramSocket socket;
	Rdt rdt;
	
	public Client(Rdt rdt) {
		this.rdt = rdt;
	}
	
	public static void main(String[] args) throws SocketException {
		DatagramSocket socket = new DatagramSocket(Math.abs(new Random().nextInt()) % 10000 + 10000);
		Rdt rdt = null;
		Client client = null;
		if (args.length == 0 || args[0].toLowerCase().equals("gbn")) {
			rdt = new GBNRdt(socket);
		} else {
			rdt = new SRRdt(socket);
		}
		client = new Client(rdt);
		
		Scanner scanner = new Scanner(System.in);
		SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9981);
		client.rdt.connect(socketAddress);
		
		DatagramPacket recvPacket = new DatagramPacket(new byte[0], 0);
		
		while (true) {
			try {
				String sends = scanner.nextLine();
				if (!sends.startsWith("-test")) {
					client.send(sends, socketAddress);
					String recv = client.recv(recvPacket);
					System.out.println("[client] recv \"" + recv + "\"");
				} else if(sends.startsWith("both")) {
					String[] argv = sends.split("\\s+");
					int num = Integer.valueOf(argv[1]);
					for (int i = 0; i < num; i++) {
						client.send(sends, socketAddress);
						String recv = client.recv(recvPacket);
						System.out.println("[client] recv \"" + recv + "\"");
					}
				} else {
					String[] argv = sends.split("\\s+");
					double ackLost = 0.2;
					double dataLost = 0.2;
					int num = 10;
					try {
						ackLost = Double.valueOf(argv[1]);
					} catch (Exception e) {}
					try {
						dataLost = Double.valueOf(argv[2]);
					} catch (Exception e) {}
					try {
						num = Integer.valueOf(argv[3]);
					} catch (Exception e) {}
					client.rdt.setLost(ackLost, dataLost);
					for (int i = 0; i < num; i++)
						client.send("" + num, socketAddress);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
		scanner.close();
//		client.rdt.close(null);
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
