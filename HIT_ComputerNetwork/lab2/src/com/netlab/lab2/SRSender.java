package com.netlab.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SRSender extends Sender {
	
	private static Random random = new Random();

	private Map<SocketAddress, SRSlideWindow> slideWindows;
	private long timeout = 1000;
	
	public SRSender(DatagramSocket socket) {
		super(socket);
		slideWindows = new HashMap<>();
	}
	
	
	@Override
	public void send(DatagramPacket dgPacket) throws IOException {
		SRSlideWindow slideWindow = slideWindows.get(dgPacket.getSocketAddress());
		// wrap the packet
		byte[] data = Arrays.copyOf(dgPacket.getData(), dgPacket.getLength());
		Packet packet = Packet.dataPacket(slideWindow.getSeq(), data);
		byte[] payload = packet.getBytes();
		dgPacket.setData(payload);
		dgPacket.setLength(payload.length);
		sendData(dgPacket);
		slideWindow.put(dgPacket);
	}

	public void resend(DatagramPacket dgPacket) throws IOException {
		SRSlideWindow slideWindow = slideWindows.get(dgPacket.getSocketAddress());
		sendData(dgPacket);
		slideWindow.update(dgPacket);
	}
	
	@Override
	public DatagramPacket sendSyn(SocketAddress socketAddress, int seq) throws IOException {
		this.slideWindows.put(socketAddress, new SRSlideWindow(this, seq + 1));
		return super.sendSyn(socketAddress, seq);
	}
	
	@Override
	public void ack(DatagramPacket dgPacket) {
		SRSlideWindow slideWindow = slideWindows.get(dgPacket.getSocketAddress());
		Packet packet = new Packet(dgPacket);
		slideWindow.ack(packet.getSeq());
		System.out.println("[finish] handle ack " + packet.getSeq());
	}
	
	@Override
	public void fin(DatagramPacket dgPacket) {
		SocketAddress socketAddress = dgPacket.getSocketAddress();
		// avoid forever fin
		if (slideWindows.containsKey(socketAddress)) {
			SRSlideWindow slideWindow = slideWindows.get(socketAddress);
			slideWindow.timer.cancel();
			slideWindows.remove(socketAddress);
			try {
				sendFin(socketAddress, slideWindow.seq);
			} catch (IOException e) {}
			System.out.println("[finish] handle fin" + socketAddress);
		}
	}

	@Override
	public void syn(DatagramPacket dgPacket) {
		Packet packet = Packet.parsePacket(dgPacket);
		SocketAddress socketAddress = dgPacket.getSocketAddress();
		try {
			// receive syn ack
			if (packet.isAck()) {
				sendAck(socketAddress, packet.getSeq());
				System.out.println("[finish] handle synack " + socketAddress + " " + packet.getSeq());
				
			}
			// receive syn
			else {
				int seq = randSeq();
				SRSlideWindow slideWindow = new SRSlideWindow(this, seq);
				this.slideWindows.put(socketAddress, slideWindow);
				DatagramPacket respPacket = sendSynAck(socketAddress, seq);
				slideWindow.put(respPacket);
				System.out.println("[finish] handle syn " + socketAddress + " " + packet.getSeq());
			}
		} catch (IOException e) {
			this.slideWindows.remove(socketAddress);
			return;
		}
	}
	
	@Override
	protected boolean connect(SocketAddress socketAddress) {
		try {
			sendSyn(socketAddress, randSeq());
			Thread.sleep(100);
			return true;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
	}


	@Override
	protected boolean close(SocketAddress socketAddress) {
		if (!slideWindows.containsKey(socketAddress))
			return false;
		try {
			sendFin(socketAddress, slideWindows.get(socketAddress).seq);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private int randSeq() {
		return Math.abs(random.nextInt()) % 2048;
	}

	class SRSlideWindow {
		private Map<Integer, DatagramPacket> packets;
		private Map<Integer, TimerTask> timers;
		private Map<Integer, Boolean> acks;
		private Map<Integer, Integer> sendTimes;
		private static final int MAX_SEND_TIME = 15;
		private int seq;
		private int pos;
		private int size = 24;
		private Timer timer;
		private SRSender sender;
		boolean connected;
		
		public SRSlideWindow(SRSender sender, int seq) {
			this.packets = new HashMap<>();
			this.timers = new HashMap<>();
			this.acks = new HashMap<>();
			this.sendTimes = new HashMap<>();
			this.seq = seq;
			this.pos = seq;
			this.timer = new Timer();
			this.sender = sender;
		}
		
		public boolean put(DatagramPacket dgPacket) {
			// control size
			int currSize = this.seq - this.pos;
			if (currSize >= this.size)
				return false;
			
			// start timer
			SRTimeoutTask timeoutTask = new SRTimeoutTask(this.seq);
			this.timer.schedule(timeoutTask, timeout);
			timers.put(this.seq, timeoutTask);
			packets.put(this.seq, dgPacket);
			acks.put(this.seq, false);
			sendTimes.put(this.seq, 1);
				
			this.seq += 1;
			
			return true;
		}
		
		public void update(DatagramPacket dgPacket) {
			Packet packet = new Packet(dgPacket);
			int seq = packet.getSeq();
			SRTimeoutTask timeoutTask = new SRTimeoutTask(seq);
			this.timer.schedule(timeoutTask, timeout);
			timers.put(seq, timeoutTask);
			packets.put(seq, dgPacket);
			acks.put(seq, false);
			sendTimes.put(seq, sendTimes.get(seq) + 1);
		}
		
		public boolean ack(int seq) {
			if (!(seq < pos + size && seq >= pos))
				return false;
			if (acks.containsKey(seq)) {
				timers.get(seq).cancel();
				acks.put(seq, true);
			} else {
				acks.put(seq, true);
			}	
			// shift window
			int newPos = pos;
			while (acks.containsKey(newPos) && acks.get(newPos)) {
				acks.remove(newPos);
				timers.remove(newPos);
				packets.remove(newPos);
				sendTimes.remove(seq);
				newPos += 1;
				System.out.println("[shift] 1");
			}
			this.pos = newPos;
			return true;
		}
		
		public int getSeq() { return seq; }
		public int getPos() { return pos; }
		
		public void setSeq(int seq) { this.seq = seq; }
		public void setPos(int pos) { this.pos = pos; }
		public void setSize(int size) { this.size = size; }
		
		
		class SRTimeoutTask extends TimerTask {
			private int seq;
			
			SRTimeoutTask(int seq) {
				this.seq = seq;
			}
			
			@Override
			public void run() {
				try {
					// no response for a long time, disconnect
					if (sendTimes.get(this.seq) >= MAX_SEND_TIME) {
						SocketAddress socketAddress = packets.get(this.seq).getSocketAddress();
						sender.slideWindows.remove(socketAddress);
						this.cancel();
						return;
					}
					synchronized (this) {
						sender.resend(packets.get(seq));
					}
				} catch (IOException e) {}
				this.cancel();
			}
		}
		
	}
	
}
