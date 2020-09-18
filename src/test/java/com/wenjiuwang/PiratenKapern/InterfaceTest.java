package com.wenjiuwang.PiratenKapern;

import junit.framework.TestCase;

public class InterfaceTest 
    extends TestCase
{
	
	Player p1 = new Player("1");

	int[] dice1 = { 1, 1, 1, 3, 3, 4, 5, 6}; //3 diamond, 2 monkey, 1 parrot, 1 sword, 1 skull

	/*
	 * test print player info
	 */
	
	public void testPrintPlayerInfo() {
		p1.score = 8000;
		p1.dice = dice1;
		p1.fortune = Fortune.MONKEYBUSINESS;
		p1.printInfo();
	}
	
	/*
	 * test print dice
	 */
	public void testPrintDice() {
		p1.dice = dice1;
		p1.printDice();
	}
	
	/*
	 * test print treasure chest
	 */
	public void testPrintChest() {
		p1.fortune = Fortune.TREASURECHEST;
		p1.dice = dice1;
		p1.treasureChest[0] = true;
		p1.treasureChest[1] = true;
		p1.printChest();
	}
}
