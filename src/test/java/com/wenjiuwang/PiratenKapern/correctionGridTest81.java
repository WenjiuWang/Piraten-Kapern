package com.wenjiuwang.PiratenKapern;

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class correctionGridTest81 
    extends TestCase
{	
	//roll no skulls, then next round roll 1 skull and reroll for it, then score 

	public void test81() throws ClassNotFoundException {
		try {
			int port = 0;
			ServerSocket s;
			s = new ServerSocket(0);
			port = s.getLocalPort();
			System.out.println("Server is now open");
			
			int riggedRolls[][] = 
			    {
			      {1, 1, 3, 3, 4, 4, 5, 5},
			      {5, 6},
			      {3, 4},
			    };
			RiggedGame game = new RiggedGame(s, 1, riggedRolls, Fortune.SORCERESS, 0);
			Thread thread = new Thread(game);
			thread.start();
			
			int riggedInput[][] = 
				{
					{ 1 },
					{ 2, 6 },
					{ 1 },
					{ 5, 6 },
					{ 2 },
				};
			
			RiggedPlayer player1 = new RiggedPlayer(port, "A", riggedInput);
			Thread thread1  = new Thread(player1);
			thread1.start();
			
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
			RiggedPlayer player2 = new RiggedPlayer(port, "B", riggedInput);
			Thread thread2 = new Thread(player2);
			thread2.start();
			
			RiggedPlayer player3 = new RiggedPlayer(port, "C", riggedInput);
			Thread thread3 = new Thread(player3);
			thread3.start();
		
        	thread.join(); 
        	thread1.join(); 
        	thread2.join(); 
        	thread3.join(); 
        	
        	assertEquals(0, Game.countObject(Object.SKULL, Fortune.SORCERESS, 0, player1.dice));

        } 
        catch (Exception e) { 
            System.out.println(e); 
        } 
  
	}

}
