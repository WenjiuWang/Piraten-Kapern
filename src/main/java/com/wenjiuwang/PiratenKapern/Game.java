package com.wenjiuwang.PiratenKapern;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
	
	int [] skullIndicator = { 1, 1, 1, 2, 2 };
	int [] battleIndicator = { 2, 2, 3, 3, 4, 4 };
	
	int fortuneCount = 0;
	int skullCount = 0;
	int battleCount = 0;
	
	//networking
	ServerSocket serverSocket;
	Client[] clients = new Client[3];

	/*
	 * Constructor & Main
	 */
	
	public Game() {
	}
	
	public static void main(String args[]) throws Exception {
		Game game = new Game();
		game.startServer();
		game.shuffleFortune();
		game.play();
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
	
	/*
	 * Game Logic - Dice & Fortune Card
	 */
	
	public int[] firstRoll() {
		int[] dice1 = { 0, 0, 0, 0, 0, 0, 0, 0};
		int[] pos = {1,2,3,4,5,6,7,8};
		return reroll(dice1, pos);
	}
	
	public int[] reroll(int[] dice, int[] rollPos) {
	    Random rand = new Random();
        for (int i = 0; i < rollPos.length; i++) { 
        	if (dice[rollPos[i]-1] - 1 != Object.SKULL.ordinal()) {
                dice[rollPos[i]-1] = rand.nextInt(6) + 1;
        	} else if (this.fortune == Fortune.SORCERESS) {
                dice[rollPos[i]-1] = rand.nextInt(6) + 1;
                this.fortune = Fortune.NONE;
        	}
        }
     
		return dice;
	}
	
	public void shuffleFortune() {
		List<Fortune> fortuneList = Arrays.asList(this.fortunePile);
		Collections.shuffle(fortuneList);
		this.fortunePile = fortuneList.toArray(this.fortunePile);
		
		List<Integer> skullList = new ArrayList<>();
		for (int i = 0; i < this.skullIndicator.length; i++) {
			skullList.add(this.skullIndicator[i]);
		}
		Collections.shuffle(skullList);
		this.skullIndicator = skullList.stream().mapToInt(i -> i).toArray();
		
		List<Integer> battleList = new ArrayList<>();
		for (int i = 0; i < this.battleIndicator.length; i++) {
			battleList.add(this.battleIndicator[i]);
		}
		Collections.shuffle(battleList);
		this.battleIndicator = battleList.stream().mapToInt(i -> i).toArray();

	}

	public Fortune drawFortune() {
	    Fortune f = this.fortunePile[this.fortuneCount];
	    if (f == Fortune.SEABATTLE) {
	    	this.fortuneIndicator = this.battleIndicator[this.battleCount];
	    	this.skullCount = (this.skullCount < 4) ? this.skullCount += 1 : 0;
	    } else if (f == Fortune.SKULLS) {
	    	this.fortuneIndicator = this.skullIndicator[this.skullCount];
	    	this.battleCount = (this.battleCount < 5) ? this.battleCount += 1 : 0;
	    } else {
	    	this.fortuneIndicator = 0;
	    }
	    this.fortuneCount = (this.fortuneCount < 34) ? this.fortuneCount += 1 : 0;
		return f;
	}
	
	/*
	 * Networking
	 */
	public void startServer() {
		System.out.println("Server initializing...");
		try {
			this.serverSocket = new ServerSocket(10140);
			System.out.println("Server is now open");

		} catch (IOException ex) {
			System.out.println("Failed to open server");
		}
	}
	
	public class Client implements Runnable {
		private Socket socket;
		private ObjectInputStream inStream;
		private ObjectOutputStream outStream;
		
		@Override
		public void run() {
			System.out.println("Client thread running ...");
			try {
				while (true) {
				}

			} catch (Exception ex) {
				{
					System.out.println("Run failed");
					ex.printStackTrace();
				}
			}
		}
		
		
		public Client(Socket s) {
			this.socket = s;
			try {
				this.outStream = new ObjectOutputStream(this.socket.getOutputStream());
				this.inStream = new ObjectInputStream(this.socket.getInputStream());
			} catch (IOException ex) {
				System.out.println("Client socket stream failed");
			}
		}
		
	}
	
	public void sendRequest(int playernum, RequestCode code, int[] info, int s) {
		try {
			this.clients[playernum].outStream.writeObject(new Gamedata(code, this.fortune, this.fortuneIndicator, info, s));
	        this.clients[playernum].outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Game Loop - Server side
	 */
	private void play() throws ClassNotFoundException {
		// connect players
		try {
			int playerCount = 0;
			System.out.println("Waiting for players to join ...");
			while(playerCount < 3) {
				Socket clientSocket = this.serverSocket.accept();
				Client c = new Client(clientSocket);
				this.clients[playerCount] = c;
				this.players[playerCount] = new PlayerData(playerCount);
				playerCount += 1;
				System.out.println("A new player has joined!");
			}
				
			for (int i = 0; i < 3; i++) {
				Thread thread = new Thread(this.clients[i]);
				thread.start();
			}
		} catch (IOException ex) {
			System.out.println("Could not connect 3 players");
		}
		
		try {
			//play turns until someone reach 6000 points
			int playerNum = 0;
			System.out.println("Game Starting!");
			int finalRounds = 2;
			int[] curDice = {1};

			while(finalRounds >= 0) {
				//draw Fortune card & first roll.
				this.fortune = this.drawFortune();
				curDice = this.firstRoll();				
				//send back fortune & dice info
				int skulls = Game.countObject(Object.SKULL, this.fortune, this.fortuneIndicator, curDice);
				
		        //send back initial info
		        if (skulls >= 4 && this.fortune != Fortune.SEABATTLE) {
			        //SKULL OF ISLAND
					this.sendRequest(playerNum, RequestCode.ISLAND_OF_SKULLS, curDice, this.turnDeductScore(curDice));
		        } else {
		        	//NORMAL TURN
					this.sendRequest(playerNum, RequestCode.NORMAL, curDice, this.turnTotalScore(curDice));
		        }
				
				//Handle client requests until turn ends
		        boolean playTurn = true;
		        while(playTurn) {
					Gamedata gd = (Gamedata) this.clients[playerNum].inStream.readObject();
					switch(gd.code) {
					case REROLL:
						//re roll
						int[] pos = gd.data;
						curDice = this.reroll(curDice, pos);
						this.sendRequest(playerNum, (skulls >= 4 && this.fortune != Fortune.SEABATTLE ? RequestCode.ISLAND_OF_SKULLS : RequestCode.NORMAL), curDice, 
								(skulls >= 4 && this.fortune != Fortune.SEABATTLE ? this.turnDeductScore(curDice) : this.turnTotalScore(curDice)));
						break;
					case PUT_IN:
						//Put dice in the chest 
						for (int i = 0; i < gd.data.length; ++i) {
							if (this.treasureChest[gd.data[i]-1]) {
								System.out.println("Skip die #" + gd.data[i] + " cause it is already in the chest.");
							} else {
								this.treasureChest[gd.data[i]-1] = true;
							}
						}
						this.sendRequest(playerNum, RequestCode.NORMAL, curDice, this.turnTotalScore(curDice));
						break;
					case TAKE_OUT:
						//Take dice out of the chest
						for (int i = 0; i < gd.data.length; ++i) {
							if (this.treasureChest[gd.data[i]-1]) {
								this.treasureChest[gd.data[i]-1] = false;
							} else {
								System.out.println("Skip die #" + gd.data[i] + " cause it not in the chest.");
							}
						}
						this.sendRequest(playerNum, RequestCode.NORMAL, curDice, this.turnTotalScore(curDice));
						break;
						default:
							playTurn = false;
							break;
					}
		        }
				//update player points:
		        if (skulls >= 4 && this.fortune != Fortune.SEABATTLE) {
		        	//island of skulls
					for (int i = 0; i < 3; ++i) {
						 int[] info = { playerNum };
						if (i != playerNum) {
							this.players[i].score -= this.turnDeductScore(curDice);
							this.sendRequest(i, RequestCode.DEDUCT, info, this.turnDeductScore(curDice));
						}
					}
		        } else {
		        	//NORMAL TURN
					this.players[playerNum].score += this.turnTotalScore(curDice);
		        }
		        
				//check if reach 6000 points
				if (players[playerNum].score >= 6000 || finalRounds <= 1) {
					finalRounds -= 1;
				}
				
				//determine next player
				playerNum = (playerNum < 2) ? playerNum + 1 : 0;
				
				//clear treasure chest
				for (int i = 0; i < this.treasureChest.length; ++i) {
					this.treasureChest[i] = false;
				}
				
				//clear fortune num
				this.fortuneIndicator = 0;
			}
		} catch (IOException ex) {
			System.out.println("Disconnected");
		}
		
	}
}

