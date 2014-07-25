package com.lucasdiegorr.graphic;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextArea;

import com.lucasdiegorr.client.ClientService;
import com.lucasdiegorr.communication.ChatMessage;
import com.lucasdiegorr.communication.ChatMessage.Action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ViewChat {

	private JFrame frame;
	private JTextField textFieldNickName;
	private JTextField textToSend;
	private JTextArea textAreaChat;
	private JButton btnConnection;
	private JButton btnDisconnect;
	private JButton btnSend;
	private Socket socket;
	private ChatMessage message;
	private ClientService clientService;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewChat window = new ViewChat();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ViewChat() {
		initialize();
	}

	private class ListenerSocket implements Runnable{

		private ObjectInputStream reader;
		
		public ListenerSocket(Socket socket) {
			try {
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
	
						connected(message);
					
					}else if (action.equals(Action.DISCONNECT)) {
						
						disconnect();
						socket.close();

						
					}else if (action.equals(Action.SEND_ONE)) {
						
						received(message);
						
					}else if (action.equals(Action.ONLINE)) {
						
						refreshOnline(message);
						
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	private void connected(ChatMessage message) {
		
		if (message.getMessage().equals("NO_FROM_SERVER")) {
			this.textFieldNickName.setText("");
			JOptionPane.showMessageDialog(frame, "O nome de usuário já é utilizado.\n Por favor utilize outro nome de usuário.");
			return;
		}else {
			this.message = message;
			this.btnConnection.setEnabled(false);
			this.textFieldNickName.setEditable(false);
			this.btnDisconnect.setEnabled(true);
			this.textToSend.setEditable(true);
			this.btnSend.setEnabled(true);
			
			JOptionPane.showMessageDialog(frame, "Conexão realizada com sucesso.\n Bom jogo.");
		}
	}
	
	private void disconnect() {
		
			this.btnConnection.setEnabled(true);
			this.textFieldNickName.setEditable(true);
			this.btnDisconnect.setEnabled(false);
			this.textToSend.setEditable(false);
			this.btnSend.setEnabled(false);
			
			JOptionPane.showMessageDialog(frame, "Você foi desconectado :(.\n Até uma próxima vez.");
	}
	
	private void received(ChatMessage message) {
		textAreaChat.append(message.getName() + " : " +message.getMessage() + "\n");
	}
	
	private void refreshOnline(ChatMessage message) {
		
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Conectar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(9, 11, 402, 70);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		textFieldNickName = new JTextField();
		textFieldNickName.setBounds(10, 22, 164, 20);
		panel.add(textFieldNickName);
		textFieldNickName.setColumns(10);
		
		btnConnection = new JButton("Conectar");
		btnConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nickName = textFieldNickName.getText();
				
				if (nickName != null) {
					message = new ChatMessage();
					
					message.setAction(Action.CONNECT);
					message.setName(nickName);
					
						clientService = new ClientService();
						socket = clientService.connect();
						
						new Thread(new ListenerSocket(socket)).start();
					
					clientService.sendMessage(message);
				}
			}
		});
		btnConnection.setBounds(184, 21, 115, 23);
		panel.add(btnConnection);
		
		btnDisconnect = new JButton("Sair");
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message.setAction(Action.DISCONNECT);
				clientService.sendMessage(message);
				disconnect();
			}
		});
		btnDisconnect.setBounds(312, 21, 80, 23);
		panel.add(btnDisconnect);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Online", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(421, 11, 153, 340);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JList listOnline = new JList();
		listOnline.setBounds(10, 23, 133, 306);
		panel_1.add(listOnline);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 92, 401, 259);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		textAreaChat = new JTextArea();
		textAreaChat.setEditable(false);
		textAreaChat.setBounds(10, 23, 381, 179);
		panel_2.add(textAreaChat);
		
		textToSend = new JTextField();
		textToSend.setEditable(false);
		textToSend.setBounds(10, 213, 296, 31);
		panel_2.add(textToSend);
		textToSend.setColumns(10);
		
		btnSend = new JButton("Enviar");
		btnSend.setEnabled(false);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSend.setBounds(316, 213, 75, 31);
		panel_2.add(btnSend);
	}
}
