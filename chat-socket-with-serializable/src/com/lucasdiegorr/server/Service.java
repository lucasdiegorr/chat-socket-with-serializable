package com.lucasdiegorr.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.lucasdiegorr.communication.ChatMessage;
import com.lucasdiegorr.communication.ChatMessage.Action;

public class Service {

	private ServerSocket server;
	private Socket socket;
	private Map<String, ObjectOutputStream> mapOnlineUsers = new HashMap<String, ObjectOutputStream>();


	public Service() {
		try {
			server = new ServerSocket(5000);
			while (true) {
				socket = server.accept();
				new Thread(new ListenerSocket(socket)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private class ListenerSocket implements Runnable{

		private ObjectOutputStream writer;
		private ObjectInputStream reader;

		public ListenerSocket(Socket socket) {
			try {
				this.writer = new ObjectOutputStream(socket.getOutputStream());
				this.reader = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			ChatMessage message = null;

			try {
				while ((message = (ChatMessage) reader.readObject()) != null) {

					Action action = message.getAction();

					if (action.equals(Action.CONNECT)) {

						boolean isConnect = connect(message, writer);

						if (isConnect) {
							mapOnlineUsers.put(message.getName(), writer);
						}

					}else if (action.equals(Action.DISCONNECT)) {

						disconnect(message, writer);
						return;

					} else if (action.equals(Action.SEND_ONE)) {

						sendOne(message, writer);

					}else if (action.equals(Action.SEND_ALL) ){

						sendAll(message);

					}else if (action.equals(Action.ONLINE)){

					}

				}
			} catch (ClassNotFoundException | IOException e) {
				disconnect(message, writer);
				e.printStackTrace();
			}
		}

	}

	private boolean connect(ChatMessage message, ObjectOutputStream writer) {

		if (mapOnlineUsers.size() == 0) {
			message.setMessage("YES_FROM_SERVER");
			sendOne(message, writer);
			return true;
		}else {
			for (Map.Entry<String, ObjectOutputStream> cliente : mapOnlineUsers.entrySet()) {
				if (cliente.getKey().equals(message.getName())) {
					message.setMessage("NO_FROM_SERVER");
					sendOne(message, writer);
					return false;
				}
			}
			message.setMessage("Conectado com sucesso.");
			sendOne(message, writer);
			return true;
		}
	}

	private void disconnect(ChatMessage message, ObjectOutputStream writer) {
		mapOnlineUsers.remove(message.getName());
		message.setMessage("O usuário " + message.getName() + " deixou o chat.");
		message.setAction(Action.SEND_ONE);
		sendAll(message);
	}

	private void sendOne(ChatMessage message, ObjectOutputStream writer) {
		try {
			writer.writeObject(message);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendAll(ChatMessage message) {
		for (Map.Entry<String, ObjectOutputStream> otherUser : mapOnlineUsers.entrySet()) {
			if (!otherUser.getKey().equals(message.getName())) {
				try {
					otherUser.getValue().writeObject(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
