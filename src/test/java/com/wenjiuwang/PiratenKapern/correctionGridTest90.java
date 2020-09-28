package com.wenjiuwang.PiratenKapern;

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class correctionGridTest90 
    extends TestCase
{	
	/*	roll 3 parrots, 2 swords, 2 diamonds, 1 coin     put 2 diamonds and 1 coin in chest
	 *  then reroll 2 swords and get 2 parrots put 5 parrots in chest and take out 2 diamonds & coin
	 *  then reroll the 3 dice and get 1 skull, 1 coin and a parrot
	 *  score 6 parrots + 1 coin for 1100 points
	*/ 

	public void test90() throws ClassNotFoundException {
		try {
			int port = 0;
			ServerSocket s;
			s = new ServerSocket(0);
			port = s.getLocalPort();
			System.out.println("Server is now open");
			
			int riggedRolls[][] = 
			    {
			      {4, 4, 4, 5, 5, 1, 1, 2},
			      {4, 4},
			      { 6, 2, 4},
			    };
			RiggedGame game = new RiggedGame(s, 1, riggedRolls, Fortune.TREASURECHEST, 0);
			Thread thread = new Thread(game);
			thread.start();
			
			int riggedInput[][] = 
				{
					{ 3 },
					{ 6, 7, 8 },
					{ 1 },
					{ 4, 5 },
					{ 3 },
					{ 1, 2, 3, 4, 5},
					{ 4 },
					{ 6, 7, 8},
					{ 1 },
					{ 6, 7, 8 },
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
        	
        	assertEquals(1100, player1.score);

        } 
        catch (Exception e) { 
            System.out.println(e); 
        } 
  
	}

}
