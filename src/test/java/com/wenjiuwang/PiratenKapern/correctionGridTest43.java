package com.wenjiuwang.PiratenKapern;

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class correctionGridTest43 
    extends TestCase
{	
	// showing a short game in which all players score something on first turn
	//   then player 2 wins on second turn
	public void test40() throws ClassNotFoundException {
		

		try {
			int port = 0;
			ServerSocket s;
			s = new ServerSocket(0);
			port = s.getLocalPort();
			System.out.println("Server is now open");

			int riggedRolls[][] = 
			    {
			      {6, 6, 5, 4, 4, 5, 5, 5},
			      {3, 3, 3, 4, 4, 5, 5, 6},
			      {2, 2},
			      {1, 1, 1, 3, 3, 5, 5, 6},
			      {1, 1, 1, 1, 2, 5, 5, 6},
			      {1, 1, 2, 2, 1, 2, 2, 2},
			    };
			RiggedGame game = new RiggedGame(s, 5, riggedRolls, Fortune.CAPTAIN, 0);
			Thread thread = new Thread(game);
			thread.start();
			
			int riggedInput1[][] = 
				{
					{ 2 },
					{ 2 },
				};
			
			RiggedPlayer player1 = new RiggedPlayer(port, "A", riggedInput1);
			player1.score = 4800;
			Thread thread1  = new Thread(player1);
			thread1.start();
			
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			int riggedInput2[][] = 
				{
					{ 1 },
					{ 4, 5 },
					{ 2 },
					{ 2 },
				};
			RiggedPlayer player2 = new RiggedPlayer(port, "B", riggedInput2);
			player2.score = 4000;
			Thread thread2 = new Thread(player2);
			thread2.start();
			
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			int riggedInput3[][] = 
				{
					{ 2 },
				};
			RiggedPlayer player3 = new RiggedPlayer(port, "C", riggedInput3);
			player3.score = 5800;
			Thread thread3 = new Thread(player3);
			thread3.start();
			
			thread.join(); 
	        thread1.join(); 
	       	thread2.join(); 
	       	thread3.join();   	
	        
	       	assertEquals(1, game.getWinner());
        } 
        catch (Exception e) { 
            System.out.println(e); 
        } 
  
	}

}
