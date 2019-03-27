package com.netlab.lab2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class Receiver extends Thread
{
	protected DatagramSocket socket;
	protected BlockingQueue<DatagramPacket> packets;
	protected Sender sender;
	
	public Receiver(DatagramSocket socket, Sender sender) {
		this.packets = new LinkedBlockingQueue<>();
		this.socket = socket;
		this.sender = sender;
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				DatagramPacket datagramPacket = new DatagramPacket(new byte[1472], 1472);
				try {
					socket.receive(datagramPacket);
				} catch (IOException e) {
					continue;
				}
				Packet packet = Packet.parsePacket(datagramPacket);
				
				if (packet.isSyn()) {
					if (packet.isAck())
						System.out.println("[recv] synack " + packet.getSeq());
					else
						System.out.println("[recv] syn " + packet.getSeq());
					handleSynPacket(datagramPacket);
				} else if (packet.isFin()) {
					System.out.println("[recv] fin " + packet.getSeq());
					handleFinPacket(datagramPacket);
				} else if (packet.isAck()) {
					System.out.println("[recv] ack " + packet.getSeq());
					handleAckPacket(datagramPacket);
				} else {
					System.out.println("[recv] data " + packet.getSeq());
					handleDataPacket(datagramPacket);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DatagramPacket receive() throws InterruptedException {
		DatagramPacket dgPacket = packets.take();
		Packet packet = new Packet(dgPacket);
		byte[] data = packet.getData();
		return new DatagramPacket(data, data.length, dgPacket.getSocketAddress());
	}
	
	protected abstract void handleFinPacket(DatagramPacket dgPacket);

	protected abstract void handleSynPacket(DatagramPacket dgPacket);
	
	protected abstract void handleAckPacket(DatagramPacket dgPacket);
	
	protected abstract void handleDataPacket(DatagramPacket dgPacket) throws IOException;
}
