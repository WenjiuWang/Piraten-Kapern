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
		
		
		// handle sorceress (on first or subsequent roll, once and only once per turn)
		game.fortune = Fortune.SORCERESS;
		int[] dice2 = { 1, 1, 1, 3, 3, 4, 6, 6}; //3 diamond, 2 monkey, 1 parrot, 2 skull
		int[] rollPos2 = {1, 8, 3} ; 
		dice2 = game.reroll(dice2, rollPos2); //use sorceress to reroll 1 skull
		int[] rollPos3 = {1, 7, 3} ; 
		dice2 = game.reroll(dice2, rollPos3); //use sorceress to reroll 1 skull
		assertEquals(6, dice2[6]); //another skull should not be rerolled, since Sorceress is consumed.

		
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
