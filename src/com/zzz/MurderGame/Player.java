package com.zzz.MurderGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
	static public final int ROLE_POLICE = 0;
	static public final int ROLE_KILLER = 1;
	static public final int ROLE_COMMON = 2;
	
	static public final int TOTAL_PLAYERS = 8;
	static public final int POLICES_COUNT = 2;
	static public final int KILLER_COUNT = 2;
	
	static public final String MSG_ENTER = "enter";
	static public final String MSG_ENTERED = "entered";
	static public final String MSG_EXIT = "exit";
	static public final String MSG_EXITED = "exited";
	static public final String MSG_NAME_PRE = "name:";
	
	private String mName = null;
	private int mRole = 0;
	private boolean mIsAlive = true;
	private Socket mSocket = null;// 存放客户端socket  
	
	public Player(Socket socket)
	{
		mSocket = socket;
	}
	
	public Player(Socket socket, String name, int role)
	{
		mSocket = socket;
		mName = name;
		mRole = role;
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public void setRole(int role)
	{
		mRole = role;
	}
	
	public int getRole()
	{
		return mRole;
	}
	
	public void kill()
	{
		mIsAlive = false;
	}
	
	public boolean isKilled()
	{
		return !mIsAlive;
	}
	
	public void sendmsg(String msg) {   
		if(mSocket == null)
			return;
        PrintWriter pout = null;   
        try {   
             pout = new PrintWriter(new BufferedWriter(   
                     new OutputStreamWriter(mSocket.getOutputStream())),   
                    true);   
             pout.println(msg);   
        } catch (IOException e) {   
             e.printStackTrace();   
        }      
    }  
	
	public String waitMsg() {  
		if(mSocket == null)
			return null;
		 try {   
			 BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));   
			 return in.readLine();           
         } catch (Exception ex) { 
             System.out.println("server 读取数据异常");   
             ex.printStackTrace();   
         }   
		 
		 return null;
    }  
}
