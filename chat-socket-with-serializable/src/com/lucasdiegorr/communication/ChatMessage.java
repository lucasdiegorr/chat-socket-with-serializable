package com.lucasdiegorr.communication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ChatMessage implements Serializable{

	private String name;
	private String message;
	private String nameReserved;
	private Set<String> listOnlineUsers = new HashSet<String>();
	private Action action;
	
	public enum Action{
		CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, ONLINE
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the nameReserved
	 */
	public String getNameReserved() {
		return nameReserved;
	}

	/**
	 * @param nameReserved the nameReserved to set
	 */
	public void setNameReserved(String nameReserved) {
		this.nameReserved = nameReserved;
	}

	/**
	 * @return the listOnlineUsers
	 */
	public Set<String> getListOnlineUsers() {
		return listOnlineUsers;
	}

	/**
	 * @param listOnlineUsers the listOnlineUsers to set
	 */
	public void setListOnlineUsers(Set<String> listOnlineUsers) {
		this.listOnlineUsers = listOnlineUsers;
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}
	
}
