package com.wenjiuwang.PiratenKapern;

import junit.framework.TestCase;

public class ScoringTest 
    extends TestCase
{

	Game game = new Game();
	
	/*
	 * test winner 
	 */
	public void testWinner() {
		PlayerData p1 = new PlayerData(1);
		PlayerData p2 = new PlayerData(2);
		PlayerData p3 = new PlayerData(3);
		p1.score = 7000;
		p2.score = 8000;
		p3.score = 9000;
		
		game.players[0] = p1;
		game.players[1] = p2;
		game.players[2] = p3;
		int winner = game.getWinner();
		
		assertEquals(2, winner);
	}
	
	/*
	 * test individual Diamond and Gold score
	 */
	public void testDiamondGoldScore() {
		//No diamond nor gold from Fortunes
		game.fortune = Fortune.NONE;
		int[] dice1 = { 1, 1, 1, 3, 3, 4, 5, 6}; // 3 Diamond only
		assertEquals(300, game.DiamondGoldScore(dice1));
		
		int[] dice2 = { 2, 2, 3, 3, 3, 4, 5, 6}; // 2 Gold only
		assertEquals(200, game.DiamondGoldScore(dice2));
		
		int[] dice3 = { 2, 1, 1, 1, 3, 4, 5, 6}; // 3 Diamond + 1 Gold
		assertEquals(400, game.DiamondGoldScore(dice3));
		
		int[] dice4 = { 3, 4, 5, 5, 3, 4, 5, 6}; // no diamond, no gold
		assertEquals(0, game.DiamondGoldScore(dice4));
		
		//Diamond Fortune activated
		game.fortune = Fortune.DIAMOND;
		assertEquals(400, game.DiamondGoldScore(dice1)); //dice1 now 4d
		assertEquals(300, game.DiamondGoldScore(dice2)); //dice2 now 2g + 1d
		
		//Gold Fortune activated
		game.fortune = Fortune.GOLD;
		assertEquals(400, game.DiamondGoldScore(dice1)); //dice1 now 3d + 1g
	}
	
	/*
	 * test full chest
	 */
	public void testFullChest() {
		int[] dice1 = { 1, 2, 3, 3, 3, 4, 4, 6}; //1 skull -> not full
		assertFalse(game.isFullChest(dice1));	
		
		int[] dice2 = { 3, 3, 3, 3, 3, 4, 5, 3}; //1 sword -> not full
		assertFalse(game.isFullChest(dice2));	
		
		int[] dice3 = { 3, 3, 3, 3, 4, 4, 4, 3}; // full
		assertTrue(game.isFullChest(dice3));
	}
	
	/*
	 * test setsReference
	 */
	public void testSetReference() {
		assertEquals(4000, game.setReference(8));
		assertEquals(0, game.setReference(2));
		assertEquals(0, game.setReference(1));
	}
	
	/*
	 * test setsScore (score gained from object sets ONLY)
	 */
	public void testSetsScore() {
		//Monkey Business is NOT activated
		game.fortune = Fortune.NONE;
		int[] dice1 = { 1, 1, 1, 3, 3, 4, 5, 6}; //3 diamond, 2 monkey, 1 parrot, 1 sword, 1 skull
		assertEquals(100, game.SetsScore(dice1));
		
		int[] dice2 = { 3, 3, 3, 3, 3, 4, 5, 2}; //5 monkey, 1 parrot, 1 gold, 1 sword
		assertEquals(500, game.SetsScore(dice2));
		
		//Monkey Business is activated
		game.fortune = Fortune.MONKEYBUSINESS;
		int[] dice3 = { 3, 3, 3, 3, 3, 4, 5, 2}; //6 monkey + parrot, 1 gold, 1 sword
		assertEquals(1000, game.SetsScore(dice3));
		
		int[] dice4 = { 3, 5, 2, 2, 3, 4, 5, 2}; //3 monkey + parrot, 3 gold, 2 sword
		assertEquals(200, game.SetsScore(dice4));
		
		//extraDiamond is activated
		game.fortune = Fortune.DIAMOND;
		assertEquals(200, game.SetsScore(dice1)); //3 +1 diamond, 2 monkey, 1 parrot, 1 sword, 1 skull
		
		//extraGold is activated
		game.fortune = Fortune.GOLD;
		assertEquals(200, game.SetsScore(dice4)); // 2 monkey, 1 parrot, 3+1 gold, 2 sword
	}
	
	/*
	 * test turn total score
	 */
	public void testTurnTotalScore() {
		//No Fortune activated
		game.fortune = Fortune.NONE;

		int[] dice1 = { 1, 1, 1, 3, 3, 4, 5, 6}; //3 diamond, 2 monkey, 1 parrot, 1 sword, 1 skull
		assertEquals(400, game.turnTotalScore(dice1));
		
		int[] dice2 = { 3, 2, 3, 2, 3, 2, 1, 5}; //3 monkey, 3 gold, 1 diamond, 1 parrot
		assertEquals(600, game.turnTotalScore(dice2));
		
		int[] dice3 = { 3, 3, 4, 4, 3, 4, 2, 2}; //3 monkey, 3 parrot, 2 gold (full chest)
		assertEquals(900, game.turnTotalScore(dice3));
		
		int[] dice4 = { 3, 6, 4, 6, 3, 6, 2, 2}; // 3 skulls
		assertEquals(0, game.turnTotalScore(dice4));
	}
	
	public void testTurnDeductScore() {
		int[] dice1 = { 1, 1, 1, 3, 6, 6, 6, 6}; //4 SKULLs

		//No Fortune activated
		game.fortune = Fortune.NONE;
		assertEquals(400, game.turnDeductScore(dice1));
		
		game.fortune = Fortune.SKULLS;
		game.fortuneIndicator = 2;
		assertEquals(600, game.turnDeductScore(dice1));
		
		game.fortune = Fortune.CAPTAIN;
		game.fortuneIndicator = 0;
		assertEquals(800, game.turnDeductScore(dice1));

	}
	

}
