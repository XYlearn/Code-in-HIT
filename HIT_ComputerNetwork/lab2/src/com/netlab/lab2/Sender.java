package com.netlab.lab2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Random;

public abstract class Sender {
	protected DatagramSocket socket;
	private double ackLost;
	private double dataLost;
	
	static Random random = new Random();
	
	public Sender(DatagramSocket socket) {
		this.socket = socket;
		this.ackLost = 0;
		this.dataLost = 0;
	}
	
	public abstract void send(DatagramPacket dgPacket) throws IOException;

	public abstract void ack(DatagramPacket dgPacket);
	
	public abstract void fin(DatagramPacket dgPacket);
	
	public abstract void syn(DatagramPacket dgPacket);
	
	public DatagramPacket sendData(DatagramPacket dgPacket) throws IOException {
		Packet packet = new Packet(dgPacket);
		if (random.nextDouble() >= dataLost) {
			this.socket.send(dgPacket);
			System.out.println("[send] data " + packet.getSeq());
		} else {
			System.out.println("[send] data " + packet.getSeq() + " and lost");
		}
		return dgPacket;
	}
	
	public DatagramPacket sendAck(SocketAddress socketAddress, int seq) throws IOException {
		Packet respPacket = Packet.ackPacket(seq);
		byte[] payload = respPacket.getBytes();
		DatagramPacket respDatagramPacket =
			new DatagramPacket(payload, payload.length, socketAddress);
		if (random.nextDouble() >= ackLost) {
			this.socket.send(respDatagramPacket);
			System.out.println("[send] ack " + seq);
		} else {
			System.out.println("[send] ack " + seq + " and lost");
		}
		return respDatagramPacket;
	}
	
	public DatagramPacket sendFin(SocketAddress socketAddress, int seq) throws IOException {
		System.out.println("[send] fin " + seq);
		Packet respPacket = Packet.finPacket(seq);
		byte[] payload = respPacket.getBytes();
		DatagramPacket respDatagramPacket =
			new DatagramPacket(payload, payload.length, socketAddress);
		this.socket.send(respDatagramPacket);
		return respDatagramPacket;
	}
	
	public DatagramPacket sendSyn(SocketAddress socketAddress, int seq) throws IOException {
		System.out.println("[send] syn " + seq);
		Packet respPacket = Packet.synPacket(seq);
		byte[] payload = respPacket.getBytes();
		DatagramPacket respDatagramPacket =
			new DatagramPacket(payload, payload.length, socketAddress);
		this.socket.send(respDatagramPacket);
		return respDatagramPacket;
	}
	
	public DatagramPacket sendSynAck(SocketAddress socketAddress, int seq) throws IOException {
		System.out.println("[send] synack " + seq);
		Packet respPacket = Packet.synAckPacket(seq);
		byte[] payload = respPacket.getBytes();
		DatagramPacket respDatagramPacket =
			new DatagramPacket(payload, payload.length, socketAddress);
		this.socket.send(respDatagramPacket);
		return respDatagramPacket;
	} 
	
	protected abstract boolean connect(SocketAddress socketAddress);
	
	protected abstract boolean close(SocketAddress socketAddress);
	
	public void setLost(double ackLost, double dataLost) {
		this.ackLost = ackLost;
		this.dataLost = dataLost;
	}
}
