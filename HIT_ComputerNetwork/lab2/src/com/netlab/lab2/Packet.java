package com.netlab.lab2;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Packet {
    private int seq;
    private boolean ack;
    private boolean syn;
    private boolean fin;
    private byte[] data;

    static int MAX_PAYLOAD = 1472;
    
    public static Packet ackPacket(int seq) {
    	Packet packet = new Packet();
    	packet.seq = seq;
    	packet.ack = true;
    	packet.syn = false;
    	packet.fin = false;
    	packet.data = new byte[0];
    	return packet;
    }
    
    public static Packet synPacket(int seq) {
    	Packet packet = new Packet();
    	packet.seq = seq;
    	packet.ack = false;
    	packet.syn = true;
    	packet.fin = false;
    	packet.data = new byte[0];
    	return packet;
    }
    
    public static Packet synAckPacket(int seq) {
    	Packet packet = new Packet();
    	packet.seq = seq;
    	packet.ack = true;
    	packet.syn = true;
    	packet.fin = false;
    	packet.data = new byte[0];
    	return packet;
    }
    
    public static Packet finPacket(int seq) {
    	Packet packet = new Packet();
    	packet.seq = seq;
    	packet.ack = false;
    	packet.syn = false;
    	packet.fin = true;
    	packet.data = new byte[0];
    	return packet;
    }
    
    public static Packet dataPacket(int seq, byte[] data) {
    	Packet packet = new Packet();
    	packet.seq = seq;
    	packet.ack = false;
    	packet.syn = false;
    	packet.fin = false;
    	packet.data = data.clone();
    	return packet;
    }
    
    public static Packet parsePacket(DatagramPacket dgPacket) {
    	return new Packet(dgPacket);
    }
    
    private Packet() {
    	
    }
    
    public Packet(DatagramPacket dgPacket) {
    	byte[] payload = 
    			Arrays.copyOf(dgPacket.getData(), dgPacket.getLength());
    	if (payload.length < 5 || payload.length > MAX_PAYLOAD)
            throw new IllegalArgumentException("Invalid packet format");
        ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
        this.seq = byteBuffer.getInt();
        byte flags = byteBuffer.get();
        setFlags(flags);
        this.data = Arrays.copyOfRange(payload, 5, payload.length);
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[5 + data.length];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.putInt(this.seq);
        byteBuffer.put(getFlags());
        byteBuffer.put(this.data);
        return bytes;
    }
    
    public int getDataLen() { return this.data.length; }
    public int getSeq() { return this.seq; }
    public boolean isAck() { return this.ack; }
    public boolean isSyn() { return this.syn; }
    public boolean isFin() { return this.fin; }
    public byte[] getData() { return this.data; }
    
    private byte getFlags() {
    	byte flags = 0;
    	if (isAck())
    		flags |= 1;
    	if (isSyn())
    		flags |= 2;
    	if (isFin())
    		flags |= 4;
    	return flags;
    }
    
    private void setFlags(byte flags) {
    	this.ack = (flags & 1) != 0;
        this.syn = (flags & 2) != 0;
        this.fin = (flags & 4) != 0;
    }
}
