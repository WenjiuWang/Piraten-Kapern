package com.wenjiuwang.PiratenKapern;

import java.util.ArrayList;

public class Game {

	public Fortune fortune = Fortune.NONE;
	public int fortuneIndicator = 0;
	PlayerData[] players = new PlayerData[3];

	boolean [] treasureChest = { false, false, false, false, false, false, false, false };
	
	Fortune[] fortunePile = { Fortune.GOLD, Fortune.GOLD, Fortune.GOLD, Fortune.GOLD, 
			Fortune.DIAMOND, Fortune.DIAMOND, Fortune.DIAMOND, Fortune.DIAMOND,
			Fortune.SORCERESS, Fortune.SORCERESS, Fortune.SORCERESS, Fortune.SORCERESS,
			Fortune.CAPTAIN, Fortune.CAPTAIN, Fortune.CAPTAIN, Fortune.CAPTAIN, 
			Fortune.TREASURECHEST, Fortune.TREASURECHEST, Fortune.TREASURECHEST, Fortune.TREASURECHEST,
			Fortune.MONKEYBUSINESS, Fortune.MONKEYBUSINESS, Fortune.MONKEYBUSINESS, Fortune.MONKEYBUSINESS,
			Fortune.SKULLS, Fortune.SKULLS, Fortune.SKULLS, Fortune.SKULLS, Fortune.SKULLS,
			Fortune.SEABATTLE, Fortune.SEABATTLE, Fortune.SEABATTLE, Fortune.SEABATTLE, Fortune.SEABATTLE, Fortune.SEABATTLE};
	
	/*
	 * Constructor
	 */
	
	public Game() {
	}
	
	/*
	 * Game Logic methods
	 */
	public static int countObject(Object obj, Fortune fortune, int fortuneIndicator, int[] dice) {
		int count = 0;
		if (obj == Object.SKULL) {
			count = (fortune == Fortune.SKULLS) ? fortuneIndicator : 0;
		} else if (obj == Object.GOLD) {
			count = (fortune == Fortune.GOLD) ? 1 : 0;
		} else if (obj == Object.DIAMOND) {
			count = (fortune == Fortune.DIAMOND) ? 1 : 0;
		} else if (obj == Object.GOLD) {
			count = (fortune == Fortune.GOLD) ? 1 : 0;
		}
		
        for (int i = 0; i < dice.length; i++) { 
        	if (Object.values()[dice[i] - 1] == obj) {
        		count += 1;
	        }
	    }
		return count;
	}
	
	/*
	 * Game Logic - Scoring
	 */
	
	public int getWinner() {
		int winner = 0; 	
		if (this.players[1].score > this.players[winner].score) winner = 1;
		if (this.players[2].score > this.players[winner].score) winner = 2;
		return winner;
	}
	
	public int DiamondGoldScore(int[] dice) {
		int diamondCount = Game.countObject(Object.DIAMOND, this.fortune, this.fortuneIndicator, dice);
		int goldCount = Game.countObject(Object.GOLD, this.fortune, this.fortuneIndicator, dice);
		return 100 * (diamondCount + goldCount);
	}

	public boolean isFullChest(int[] dice) {
		int [] count = {0, 0, 0, 0, 0, 0};
        for (int i = 0; i < dice.length; i++) { 
            count[dice[i]-1] += 1;
        }
        
        //Skull
        if (count[5] > 0) return false;
        
        //Monkey and Parrot
        if (count[2] < 3 && this.fortune != Fortune.MONKEYBUSINESS) return false;
        if (count[3] < 3 && this.fortune != Fortune.MONKEYBUSINESS) return false;
        if (count[2] + count[3] < 3 && this.fortune == Fortune.MONKEYBUSINESS) return false;

        //Sword 
        if (count[3] < 3) return false;
        
        //it's full otherwise.
		return true;
	}
	
	public int setReference(int val) {
		int score = 0;
		switch (val){
			case 3:
				score = 100;
				break;
			case 4:
				score = 200;
				break;
			case 5:
				score = 500;
				break;
			case 6:
				score = 1000;
				break;
			case 7:
				score = 2000;
				break;
			case 8:
				score = 4000;
				break;
			default:
				score = 0;
				break;
		}
		return score;
	}

	public int SetsScore(int[] dice) {
		int score = 0;
		int [] count = {0, 0, 0, 0, 0, 0};
		
        for (int i = 0; i < dice.length; i++) { 
            count[dice[i]-1] += 1;
        }
        
        //Diamond sets
        count[0] = (this.fortune == Fortune.DIAMOND) ? count[0] + 1 : count[0];
        score += setReference(count[0]);

        //Gold sets
        count[1] = (this.fortune == Fortune.GOLD) ? count[1] + 1 : count[1];
        score += setReference(count[1]);

        //Monkey and Parrot sets
        count[2] = (this.fortune == Fortune.MONKEYBUSINESS) ? count[2] + count[3] : count[2];
        count[3] = (this.fortune == Fortune.MONKEYBUSINESS) ? 0 : count[3];
        score += setReference(count[2]) + setReference(count[3]) ;

        //Sword sets
        score += setReference(count[4]);
        
		return score;
	}
	
	public int turnTotalScore(int[] dice) {
		int skulls = Game.countObject(Object.SKULL, this.fortune, this.fortuneIndicator, dice);
		int swords = Game.countObject(Object.SWORD, this.fortune, this.fortuneIndicator, dice);
        
        //no score for 3 skulls!
        if (skulls >= 3) {
        	// if treasure chest
        	if (this.fortune == Fortune.TREASURECHEST) {
        		ArrayList<Integer> newDice = new ArrayList<Integer>();
        		 for (int i = 0; i < this.treasureChest.length; ++i) {
        			 if (this.treasureChest[i]) {
        				 newDice.add(dice[i]);
        			 }
        		 }
        		 int [] cleanDice = newDice.stream().mapToInt(i -> i).toArray();
        		 return this.DiamondGoldScore(cleanDice) + this.SetsScore(cleanDice);
        	} else {
        		return 0;
        	}
        }
        //Diamond and Gold score + set score
        int score = DiamondGoldScore(dice) + SetsScore(dice);

        //Full chest bonus
		score += isFullChest(dice) ? 500 : 0;
		
		//Sea battle bonus
		if (this.fortune == Fortune.SEABATTLE && swords >= this.fortuneIndicator) {
			if (this.fortuneIndicator == 2) {
				score += 300;
			} else if (this.fortuneIndicator == 3) {
				score += 500;
			} else {
				score += 1000;
			}
		}
		
		//Captain bonus
		score = (this.fortune == Fortune.CAPTAIN) ? score *2 : score;
		return score;
	}
	
	public int turnDeductScore(int[] dice) {
		int result = Game.countObject(Object.SKULL, this.fortune, this.fortuneIndicator, dice);
		if (this.fortune == Fortune.CAPTAIN) result *= 2;
		return result * 100;
	}
	
}
