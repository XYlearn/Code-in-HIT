package com.netlab.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class GBNReceiver extends Receiver {
	
	private Map<SocketAddress, Integer> ackSeqs;

	public GBNReceiver(DatagramSocket socket, Sender sender) {
		super(socket, sender);
		ackSeqs = new HashMap<>();
	}

	@Override
	protected void handleAckPacket(DatagramPacket dgPacket) {
		SocketAddress socketAddress = dgPacket.getSocketAddress();
		if (!ackSeqs.containsKey(socketAddress))
			ackSeqs.put(socketAddress, 0);
		this.sender.ack(dgPacket);
		
	}

	@Override
	protected void handleDataPacket(DatagramPacket dgPacket) throws IOException {
		Packet packet = Packet.parsePacket(dgPacket);
		if (shouldIgnore(dgPacket.getSocketAddress(), packet.getSeq())) {
			System.out.println("[ign] " + packet.getSeq());
			return;
		}
		sender.sendAck(dgPacket.getSocketAddress(), packet.getSeq());
		// ack packet missing
		if (ackSeqs.get(dgPacket.getSocketAddress()) > packet.getSeq())
			return;
		try {
			this.packets.put(dgPacket);
			SocketAddress socketAddress = dgPacket.getSocketAddress();
			// seq increase by one each time
			this.ackSeqs.put(socketAddress, this.ackSeqs.get(socketAddress) + 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleFinPacket(DatagramPacket dgPacket) {
		sender.fin(dgPacket);
	}

	@Override
	protected void handleSynPacket(DatagramPacket dgPacket) {
		Packet packet = Packet.parsePacket(dgPacket);
		ackSeqs.put(dgPacket.getSocketAddress(), packet.getSeq() + 1);
		sender.syn(dgPacket);
	}
	
	protected boolean shouldIgnore(SocketAddress src, int seq) {
		if (!ackSeqs.containsKey(src))
			ackSeqs.put(src, 0);
		return ackSeqs.get(src) < seq;
	}
}
