package com.netlab.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public abstract class Rdt {
	protected Sender sender;
	protected Receiver receiver;
	protected DatagramSocket socket;
	protected SocketAddress targetAddress;
	protected boolean started;
	
	protected Rdt(DatagramSocket socket) {
		this.socket = socket;
		this.targetAddress = null;
		started = false;
		init();
	}
	
	protected void init() {
		return;
	}
	
	public boolean connect(SocketAddress socketAddress) {
		this.targetAddress = socketAddress;
		return sender.connect(socketAddress);
	}
	
	public void close(SocketAddress socketAddress) {
		if (targetAddress != null)
			sender.close(targetAddress);
		else
			sender.close(socketAddress);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		socket.close();
	}
	
	public void send(DatagramPacket packet) throws IOException{
		if (null != targetAddress)
			packet.setSocketAddress(targetAddress);
		sender.send(packet);
	}
	
	public DatagramPacket receive() throws InterruptedException {
		DatagramPacket datagramPacket = receiver.receive();
		return datagramPacket;
	}
	
	public void setLost(double ackLost, double dataLost) {
		sender.setLost(ackLost, dataLost);
	}
}
