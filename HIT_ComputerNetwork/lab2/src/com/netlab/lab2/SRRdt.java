package com.netlab.lab2;

import java.net.DatagramSocket;

public class SRRdt extends Rdt {

	protected SRRdt(DatagramSocket socket) {
		super(socket);
		System.out.println("SR rdt");
	}
	
	protected void init() {
		sender = new SRSender(socket);
		receiver = new SRReceiver(socket, sender);
		receiver.start();
	}

}
