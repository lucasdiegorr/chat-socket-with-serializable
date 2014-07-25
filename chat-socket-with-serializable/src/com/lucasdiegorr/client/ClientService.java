package com.lucasdiegorr.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.lucasdiegorr.communication.ChatMessage;

public class ClientService {

	private Socket socket;
	private ObjectOutputStream writer;
	
	public Socket connect() {
		try {
			this.socket = new Socket("127.0.0.1", 5000);
			this.writer = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return socket;
	}
	
	public void sendMessage(ChatMessage message) {
		try {
			writer.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
