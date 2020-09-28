package com.wenjiuwang.PiratenKapern;

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class correctionGridTest95 
    extends TestCase
{	
	/*	roll 2 skulls, 3 parrots, 3 coins   put 3 coins in chest
	 *  then rerolls 3 parrots and get 2 diamonds 1 coin    put coin in chest (now 4)
	 *  then reroll 2 diamonds and get 1 skull 1 coin     SC for chest only = 400 + 200 = 600
	 *  also interface reports death & end of turn
	*/ 

	public void test95() throws ClassNotFoundException {
		try {
			int port = 0;
			ServerSocket s;
			s = new ServerSocket(0);
			port = s.getLocalPort();
			System.out.println("Server is now open");
			
			int riggedRolls[][] = 
			    {
			      {6, 6, 4, 4, 4, 2, 2, 2},
			      {1, 1, 2},
			      {6, 2},
			    };
			RiggedGame game = new RiggedGame(s, 1, riggedRolls, Fortune.TREASURECHEST, 0);
			Thread thread = new Thread(game);
			thread.start();
			
			int riggedInput[][] = 
				{
					{ 3 },
					{ 6, 7, 8 },
					{ 1 },
					{ 3, 4, 5 },
					{ 3 },
					{ 5 },
					{ 1 },
					{ 3, 4 },
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
        	
        	assertEquals(600, player1.score);

        } 
        catch (Exception e) { 
            System.out.println(e); 
        } 
  
	}

}
