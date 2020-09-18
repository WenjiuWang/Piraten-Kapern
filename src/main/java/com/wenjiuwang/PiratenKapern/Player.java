package com.wenjiuwang.PiratenKapern;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import sun.java2d.windows.GDIBlitLoops;

public class Player {
	int id;
	String name;
	int[] dice;
	int score = 0;
	int turnScore = 0;
	Fortune fortune = Fortune.NONE;
	int fortuneIndicator = 0;
	boolean [] treasureChest = { false, false, false, false, false, false, false, false };
	
	//networking
	Socket socket;
	ObjectInputStream inStream;
	ObjectOutputStream outStream;

	/*
	 * Constructor & Main
	 */
	public Player(String s) {
		this.name = s;
	}
	
	public static void main(String args[]) throws ClassNotFoundException {
		System.out.println("Please enter the player name");
		Scanner scan = new Scanner(System.in);
		String name = scan.next();
		Player player = new Player(name);
		player.connectServer();
		player.playGame();
	}
	
	/*
	 * Game Logic methods
	 */
	public int[] rerollDice(Scanner scan) {
		int [] result;
		System.out.println("Please select which die(dice) to be rerolled: (1, 2, 5, ..)");
		//keep trying until get correct input
		while(true) {
			int[] pos = Arrays.stream((scan.next()).replaceAll("\\s", "").split(",")).mapToInt(Integer::parseInt).toArray();
			if (pos.length < 2) {
				System.out.println("*** At least 2 dice are required to reroll ***");
				System.out.println("Please select which die(dice) to be rerolled: (1, 2, 5, ..)");
				continue;
			}
			//skull check for reroll pos
			ArrayList<Integer> newDice = new ArrayList<Integer>();
    		for (int i = 0; i < pos.length; ++i) {
    			newDice.add(this.dice[pos[i]-1]);
    		}
    		int [] cleanDice = newDice.stream().mapToInt(i -> i).toArray();
			int skullCount = Game.countObject(Object.SKULL, this.fortune, this.fortuneIndicator, cleanDice);
		    if (skullCount == 0 || (skullCount == 1 && this.fortune == Fortune.SORCERESS)) {
			    this.sendRequest(RequestCode.REROLL, pos);
			    result = pos;
		       	break;
		     } else {
				System.out.println("*** Based on your fortune card, you can not reroll these many skulls. ***");
				System.out.println("Please select which die(dice) to be rerolled: (1, 2, 5, ..)");
	         }
		}
		return result;
	}
	
	/*
	 * Networking
	 */
	public void sendRequest(RequestCode code, int[] info) {
		try {
			Gamedata data = new Gamedata(code, this.fortune, this.fortuneIndicator, info, this.turnScore);
        	this.outStream.writeObject(data);
       		this.outStream.flush();
       	} catch (IOException ex) {
			System.out.println("Failed to send request to server");
			ex.printStackTrace();
		}
	}
	
	public void connectServer() {
		System.out.println("Connecting to game server ...");
		try {
			this.socket = new Socket("localhost", 10140);
			this.inStream = new ObjectInputStream(this.socket.getInputStream());
			this.outStream = new ObjectOutputStream(this.socket.getOutputStream());
			System.out.println("Connected.");
		} catch (IOException ex) {
			System.out.println("Fails to connect.");
		}
	}
	
	/*
	 * Game Loop - Player side
	 */
	private void playGame() throws ClassNotFoundException {
		boolean playing = true;
		while (playing) {
			RequestCode code = RequestCode.NORMAL;
			try {
				Gamedata gd = (Gamedata) this.inStream.readObject();
				this.dice = gd.data.clone();
				this.fortune = gd.fortune;
				this.turnScore = gd.score;
				this.fortuneIndicator = gd.fortuneIndicator;
	    	    code = gd.code;
				this.printInfo();
			} catch (IOException ex) {
				System.out.println("Lost server connection.");
				ex.printStackTrace();
			}
			Scanner scan = new Scanner(System.in);
			int selection;
			switch(code) {
				case LOSE:
					System.out.println("You Lose!");
					playing = false;
					break;
				case WIN:
					System.out.println("You Win!");
					playing = false;
					break;
					
				case ISLAND_OF_SKULLS:
					//Island of Skulls
					break;
				
				case DEDUCT:
					break;
					
				default: //normal turn
					//play this turn until end by 3 skull, or by player.
					this.printDice();
					//check if there are more than 3 skulls.
					int skulls = Game.countObject(Object.SKULL, this.fortune, this.fortuneIndicator, this.dice);
			        
			        if (skulls >= 3) {
				        this.sendRequest(RequestCode.END, this.dice);
				       	this.score += this.turnScore;
						//clear treasure chest
						for (int i = 0; i < this.treasureChest.length; ++i) {
							this.treasureChest[i] = false;
						}
						System.out.println("******* You got 3 skulls! Your turn is terminated. *******");
						System.out.println("Waiting for your turn.");
				      	break;
			        }
					
					System.out.println("Please enter your selection: ");
					System.out.println("1. Keep Rerolling");
					System.out.println("2. End this turn and score");
					if (this.fortune == Fortune.TREASURECHEST) {
						System.out.println("3. Put dice in the Treasure chest");
						System.out.println("4. Take dice out of the Treasure chest");
					}
					selection = scan.nextInt();
					
					if (selection == 1) {
						this.rerollDice(scan);	
					} else if (selection == 2) {
						this.sendRequest(RequestCode.END, this.dice);
				       	this.score += this.turnScore;
						//clear treasure chest
						for (int i = 0; i < this.treasureChest.length; ++i) {
							this.treasureChest[i] = false;
						}
						System.out.println("Waiting for your turn.");
					} else if (selection == 3 && this.fortune == Fortune.TREASURECHEST){
						//Put dice in the chest 
						System.out.println("Please select which die(dice) to be put in the chest: (1, 2, 5, ..)");
						int[] pos = Arrays.stream((scan.next()).replaceAll("\\s", "").split(",")).mapToInt(Integer::parseInt).toArray();
						for (int i = 0; i < pos.length; ++i) {
							if (this.treasureChest[pos[i]-1]) {
								System.out.println("Skip die #" + pos[i] + " cause it is already in the chest.");
							} else {
								this.treasureChest[pos[i]-1] = true;
							}
						}
						this.sendRequest(RequestCode.PUT_IN, pos);
					} else if (selection == 4 && this.fortune == Fortune.TREASURECHEST)  {
						//Take dice out of the chest
						System.out.println("Please select which die(dice) to be taken out of the chest: (1, 2, 5, ..)");
						int[] pos = Arrays.stream((scan.next()).replaceAll("\\s", "").split(",")).mapToInt(Integer::parseInt).toArray();
						for (int i = 0; i < pos.length; ++i) {
							if (this.treasureChest[pos[i]-1]) {
								this.treasureChest[pos[i]-1] = false;
							} else {
								System.out.println("Skip die #" + (pos[i]) + " cause it not in the chest.");
							}
						}
						this.sendRequest(RequestCode.TAKE_OUT, pos);
					} else {
						System.out.println("Please enter a valid selection");
					}
					break;
			}
		}
	}
	
	
	/*
	 * Interface
	 */
	public void printInfo() {
		System.out.println("|------------------------------------------------------------|");
		System.out.println("|                                                            |");
		System.out.println("| PLAYER: " + this.name + " | Total Score: " + this.score);
		System.out.println("|                                                            |");
		System.out.println("|------------------------------------------------------------|");
		System.out.println("|                                                            |");
		System.out.println("| Fortune Card Active: " + this.fortune.name() + (this.fortuneIndicator == 0 ? "" : " - " + this.fortuneIndicator));
		System.out.println("|                                                            |");
		System.out.println("|------------------------------------------------------------|");
	}

	public void printDice() {
		System.out.println("|------------------------------------------------------------|");
		System.out.println("| Current Dice Rolled:                                       |");
		System.out.println("|                                                            |");
		System.out.println("| 1: " + Object.values()[this.dice[0]-1].name() + " | 2: " + Object.values()[this.dice[1]-1].name() + " | 3: " + Object.values()[this.dice[2]-1].name() + " | 4:  " + Object.values()[this.dice[3]-1].name());
		System.out.println("|                                                            |");
		System.out.println("| 5: " + Object.values()[this.dice[4]-1].name() + " | 6: " + Object.values()[this.dice[5]-1].name() + " | 7: " + Object.values()[this.dice[6]-1].name() + " | 8:  " + Object.values()[this.dice[7]-1].name());
		System.out.println("|                                                            |");
		System.out.println("| Current Rolled Score: " + this.turnScore);
		System.out.println("|------------------------------------------------------------|");
		if (this.fortune == Fortune.TREASURECHEST) printChest();
	}
	
	public void printChest() {
		System.out.println("|------------------------------------------------------------|");
		System.out.println("| Current Dice in Treasure Chest:                            |");
		System.out.println("|                                                            |");
		for (int i = 0; i < this.treasureChest.length; ++i) {
			if (this.treasureChest[i]) {
				System.out.print("| " + (i+1) + ": " + Object.values()[this.dice[i]-1].name() + " ");
			}
		}
		System.out.println("|                                                            |");
		System.out.println("|------------------------------------------------------------|");

	}
	


}