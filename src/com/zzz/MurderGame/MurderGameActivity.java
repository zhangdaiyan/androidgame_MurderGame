package com.zzz.MurderGame;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;


public class MurderGameActivity extends Activity {
   private static final int PORT = 9999;// 端口监听   
   private List<Player> mPlayerList = new ArrayList<Player>();// 存放客户端socket   
   private ServerSocket server = null;   
   private ExecutorService mExecutorService = null;// 线程池   
   TextView mTextView = null;

   static public final String TAG = "MurderGameActivity";
   
   static public final int MSG_UPDATEUI = 0;

   Handler mHandler = new Handler() {  
       public void handleMessage(Message msg) {   
            switch (msg.what) {   
                 case MSG_UPDATEUI:
                	 String str = "";
                	 for(Player player : mPlayerList){
                		 str += player.getName() + "\n";
                	 }
                	 mTextView.setText(str);
                     break;   
            }   
            super.handleMessage(msg);   
       }   
  };
  
    /** Called when the activity is first created. */
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   
        
        mTextView = (TextView)findViewById(R.id.textview);
        new ConnectionThread().start();
        
        synchronized (mPlayerList){   
        	for(int i = 0; i < Player.POLICES_COUNT; i++){
        		Player player = new Player(null, "police" + i, Player.ROLE_POLICE);
        		mPlayerList.add(player);
        	}
        	
        	for(int i = 0; i < Player.KILLER_COUNT; i++){
        		Player player = new Player(null, "killer" + i, Player.ROLE_KILLER);
        		mPlayerList.add(player);
        	}
        	
        	for(int i = 0; i < Player.TOTAL_PLAYERS - Player.POLICES_COUNT - Player.KILLER_COUNT; i++){
        		Player player = new Player(null, "common" + i, Player.ROLE_COMMON);
        		mPlayerList.add(player);
        	}
 	   	}         
        
        synchronized (mHandler){
     	   mHandler.sendEmptyMessage(MSG_UPDATEUI);
        }
    }
    
    class ConnectionThread extends Thread{//刷帧线程
    	public ConnectionThread() {//构造器
        	try {   
	        	server = new ServerSocket(PORT);   
	            mExecutorService = Executors.newCachedThreadPool();// 创建一个线程池   
	            System.out.println("Server Start...");   
        	} catch (Exception ex) {   
        		ex.printStackTrace();   
        	} 
    	}

		public void run() {//重写的run方法
            while (true) {//循环
            	try {                  
                    Socket client = null;   
                    while (true) {   
                       client = server.accept();  
                       Player player = new Player(client);
                       playerEnter(player);   
                       mExecutorService.execute(new Service(player));// 开启一个客户端线程. 
                       Log.d(TAG, Player.MSG_ENTERED);
                    }   
                } catch (Exception ex) {   
                    ex.printStackTrace();   
               }   
            }
		}
	}
    
    public class Service implements Runnable {     	  
        private Player mPlayer = null;    
        private String msg = null;   
  
        public Service(Player player) {   
        	mPlayer = player;   
        }   
  
        public void run() {   
            // TODO Auto-generated method stub   
           try {   
                while (true) {   
                    if ((msg = mPlayer.waitMsg()) != null) {   
                    	Log.d(TAG, "get msg:" + msg);
                        if (msg.equals(Player.MSG_EXIT)) {    
                        	playerExit(mPlayer);
                            break;   
                        }
                        else if (msg.startsWith(Player.MSG_NAME_PRE)) {    
                        	mPlayer.setName(msg.replaceFirst(Player.MSG_NAME_PRE, ""));
                            break;   
                        }else {   
   
                        }   
                   }else{
                	   break;
                   }   
                }   
            } catch (Exception ex) { 
                Log.d(TAG, "server 读取数据异常");   
                ex.printStackTrace();   
            }   
        }    
   }       
    
    void playerExit(Player player)
    {
    	synchronized (mPlayerList){
    		mPlayerList.remove(player);    
    	}
    	
    	player.sendmsg(Player.MSG_EXITED);  
        
        synchronized (mHandler){
        	mHandler.sendEmptyMessage(MSG_UPDATEUI);
        }
    }
    
    void playerEnter(Player player)
    {
        synchronized (mPlayerList){
     	   mPlayerList.add(player); 
 	   	}         
        
        player.sendmsg(Player.MSG_ENTERED);
        
        synchronized (mHandler){
     	   mHandler.sendEmptyMessage(MSG_UPDATEUI);
        }
    }
    
    void startGame()
    {
    	synchronized (mPlayerList){   
        	for(int i = 0; i < Player.POLICES_COUNT; i++){
        		Player player = new Player(null, "police" + i, Player.ROLE_POLICE);
        		mPlayerList.add(player);
        	}
        	
        	for(int i = 0; i < Player.KILLER_COUNT; i++){
        		Player player = new Player(null, "killer" + i, Player.ROLE_KILLER);
        		mPlayerList.add(player);
        	}
        	
        	for(int i = 0; i < Player.TOTAL_PLAYERS - Player.POLICES_COUNT - Player.KILLER_COUNT; i++){
        		Player player = new Player(null, "common" + i, Player.ROLE_COMMON);
        		mPlayerList.add(player);
        	}
 	   	}       
    }
}