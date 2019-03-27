package com.netlab.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SRReceiver extends Receiver {
	
	private Map<SocketAddress, SRSlideWindow> slideWindows;

	public SRReceiver(DatagramSocket socket, Sender sender) {
		super(socket, sender);
		slideWindows = new HashMap<>();
	}

	@Override
	protected void handleAckPacket(DatagramPacket dgPacket) {
		SocketAddress socketAddress = dgPacket.getSocketAddress();
		if (!slideWindows.containsKey(socketAddress))
			slideWindows.get(socketAddress).window.put(0, true);
		this.sender.ack(dgPacket);
		
	}

	@Override
	protected void handleDataPacket(DatagramPacket dgPacket) throws IOException {
		Packet packet = Packet.parsePacket(dgPacket);
		sender.sendAck(dgPacket.getSocketAddress(), packet.getSeq());
		SRSlideWindow slideWindow = this.slideWindows.get(dgPacket.getSocketAddress());
		// ack missing
		if (packet.getSeq() < slideWindow.pos)
			return;
		try {
			this.packets.put(dgPacket);
			SocketAddress socketAddress = dgPacket.getSocketAddress();
			this.slideWindows.get(socketAddress).window.put(packet.getSeq(), true);
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
		slideWindows.put(dgPacket.getSocketAddress(), new SRSlideWindow(packet.getSeq() + 1));
		sender.syn(dgPacket);
	}
	
	protected boolean shouldIgnore(SocketAddress src, int seq) {
		return false;
	}
	
	class SRSlideWindow {
		public HashMap<Integer, Boolean> window;
		public int pos;
		public int size;
		
		public SRSlideWindow(int pos) {
			this.window = new HashMap<>();
			this.pos = pos;
			this.size = 18;
			for (int i = pos; i < pos + size; i++)
				window.put(pos, false);
		}
		
		void ack(int seq) {
			window.put(seq, true);
			int newPos = pos;
			while (window.containsKey(newPos) && window.get(newPos)) {
				window.remove(newPos);
				window.put(newPos + size, false);
				newPos += 1;
			}
		}
	}
}
