package com.wenjiuwang.PiratenKapern;

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class correctionGridTest115 
    extends TestCase
{	
	//show deduction received cannot make your score negative

	public void test115() throws ClassNotFoundException {
		try {
			int port = 0;
			ServerSocket s;
			s = new ServerSocket(0);
			port = s.getLocalPort();
			System.out.println("Server is now open");
			
			int riggedRolls[][] = 
			    {
			      {6, 6, 6, 2, 2, 1, 1, 1},
			      {6, 5},
			      {},
			      {3, 3}
			    };
			RiggedGame game = new RiggedGame(s, 1, riggedRolls, Fortune.SKULLS, 1);
			Thread thread = new Thread(game);
			thread.start();
			
			int riggedInput[][] = 
				{
					{ 1 },
					{ 6, 8},
					{ 1},
					{4, 5},
					{2},
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
			player2.score = 100;
			Thread thread2 = new Thread(player2);
			thread2.start();
			
			RiggedPlayer player3 = new RiggedPlayer(port, "C", riggedInput);
			player3.score = 100;

			Thread thread3 = new Thread(player3);
			thread3.start();
		
        	thread.join(); 
        	thread1.join(); 
        	thread2.join(); 
        	thread3.join(); 
        	
        	assertEquals(0, player1.score);
        	assertEquals(0, player2.score);
        	assertEquals(0, player3.score);

        } 
        catch (Exception e) { 
            System.out.println(e); 
        } 
  
	}

}
