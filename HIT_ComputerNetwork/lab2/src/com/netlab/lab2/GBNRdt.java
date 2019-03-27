package com.netlab.lab2;

import java.net.DatagramSocket;

public class GBNRdt extends Rdt {

	protected GBNRdt(DatagramSocket socket) {
		super(socket);
		System.out.println("GBN rdt");
	}
	
	@Override
	protected void init() {
		super.init();
		this.sender = new GBNSender(socket);
		this.receiver = new GBNReceiver(socket, sender);
		receiver.start();
	}

}
