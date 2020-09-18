package com.wenjiuwang.PiratenKapern;

import java.util.Arrays;

import junit.framework.TestCase;

public class rollAndDrawTest 
	extends TestCase
{
	Game game = new Game();

	/*
	 * test firstRoll 
	 */
	public void testFirstRoll() {
		int[] dice1 = { 0, 0, 0, 0, 0, 0, 0, 0}; // A dice set that is invalid.
		dice1 = game.firstRoll();
		assertFalse(Arrays.asList(dice1).contains(0)); // new dice doesnt contain '0'
	}
	
	/*
	 * test reroll
	 */
	public void testReroll() {
		//No Fortune activated
		game.fortune = Fortune.NONE;
		
		int[] dice1 = { 1, 1, 1, 3, 3, 4, 6, 6}; //3 diamond, 2 monkey, 1 parrot, 2 skull
		int[] rollPos1 = {1, 7, 3} ; 
		dice1 = game.reroll(dice1, rollPos1);
		assertEquals(6, dice1[6]); //skull should not be rerolled.
	}
	
	/*
	 * test shuffle fortune card  
	 */
	public void testShuffleFortune() {
		Fortune[] fortunes = game.fortunePile.clone();
		game.shuffleFortune();
		assertFalse(Arrays.equals(fortunes, game.fortunePile));
	}
	
	/*
	 * test draw fortune card  
	 */
	public void testDrawFortuneCard() {
		game.fortune = Fortune.NONE; // no fortune before drawing
		game.fortune = game.drawFortune();
		assertTrue(game.fortune != Fortune.NONE); // a fortune card is drawn
	}
	


}
