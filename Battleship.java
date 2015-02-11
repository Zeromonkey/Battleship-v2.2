import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class Battleship {
    public static Network gameNetwork = null;
    public static Frame gameFrame = null;
    public static int serVal = 0;
    
    public static void main (String [] args){
        gameFrame = new Frame();
        serVal = JOptionPane.showConfirmDialog(null, "Are you the server? (Y/N)", "Server?", JOptionPane.YES_NO_OPTION);
        if(serVal == JOptionPane.YES_OPTION){	        //The following creates the network and establishes the role of the player.
        	try {
        		gameNetwork = new Network(true, true);
			} catch (IOException e) {
				System.out.println("NETWORK Error\n");
				e.printStackTrace();
			}
        }
        else{
        	try {
        		gameNetwork = new Network(false, false);
			} catch (IOException e) {
				System.out.println("NETWORK Error");
				e.printStackTrace();
			}
        }
        gameFrame.init();
        gameFrame.printConsole("Welcome to Battleship v2.2.\n");
        gameFrame.printConsole("Created by Team Aqua.\n");
        if(serVal == JOptionPane.YES_OPTION){									//Helps identify who is who in the game.
        	gameFrame.printConsole("Ready, Player One?\n");						//Server
        }
        else{
        	gameFrame.printConsole("Ready, Player Two?\n");						//Client
        }
        gameFrame.printConsole("Place your ships on the bottom right grid.\n");
        int ruleVal = JOptionPane.showConfirmDialog(null, "Do you know the rules? (Y/N)", "Rules?", JOptionPane.YES_NO_OPTION);
        if(ruleVal == JOptionPane.NO_OPTION){	        	//This prints the rules for the player if they don't know them.
        	printRules();
        }
        boolean placed = gameFrame.ownShips.isPlaced();
        while(!placed){
        	try {
        		TimeUnit.MILLISECONDS.sleep(250);
	        	placed = gameFrame.ownShips.isPlaced();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        gameNetwork.donePlacing = true;
        try {
			gameNetwork.placeBoats();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        gameFrame.start();
        int turnVal = gameNetwork.getMagicNumber();
        int myShipCount = 1;
        int enemyShipCount = 1;
        while (isAlive(gameNetwork.getServerBool()) == true){
        	if (turnVal == 0){
        		gameFrame.printConsole("Choose a square to launch a missle at.\n");
            	gameFrame.printConsole("Click fire when you are ready.\n");
            	String atkArg = "";
            	gameFrame.shotsFired = false;
            	while(!(gameFrame.shotsFired)){
            		try {
						TimeUnit.MILLISECONDS.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            	}
            	atkArg = gameFrame.fire();
            	gameFrame.shotsFired = false;
            	String response = "";
				try {
					response = gameNetwork.attack(atkArg);
				} catch (IOException e) {
					e.printStackTrace();
				}         	
            	//parse response x,y,B:SID
            	String[] reply = response.split(":");
            	String[] shotInfo = reply[0].split(",");
            	int x = Integer.parseInt(shotInfo[0]);
            	int y = Integer.parseInt(shotInfo[1]);
            	int B = Integer.parseInt(shotInfo[2]);
            	int SID = Integer.parseInt(reply[1]);          	
            	if (B == 1){
            		gameFrame.btnGrid.selectHit(x,y);							//Set (x,y)  enemy square to hit
            		if (SID > 0){
            			gameFrame.printConsole("You sunk their " + getShipName(SID) + "!\n");
            			enemyShipCount--;
            		}
            	}
            	else if (B == 0){
            		gameFrame.btnGrid.selectMiss(x,y);							//Set (x,y) enemy square to miss
            	}
            	turnVal = 1;
        	}
        	else if (turnVal == 1){
        		gameFrame.printConsole("The other player is firing.\n");
            	gameFrame.printConsole("Please Wait...\n");
            	String defArg = null;
            	while (defArg == null){
            		try {
            			TimeUnit.MILLISECONDS.sleep(250);
            			defArg = gameNetwork.defend();
            		} catch (InterruptedException | IOException e) {
            			System.out.println("Defending Error.");
            			e.printStackTrace();
            		}
				}
            	//parses attack ATK:x,y
            	String[] attack = defArg.split(":");			
            	String[] shotInfo = attack[1].split(",");			
            	//String attackCode = attack[0];           				  Not used for special shots
            	int x = Integer.parseInt(shotInfo[0]);
            	int y = Integer.parseInt(shotInfo[1]);
            	int[] hitMiss = gameFrame.ownShips.attacked(x, y);
            	int B = hitMiss[0];
            	if(B == 1){
            		gameFrame.printConsole("Your ship on " + "(" + x + "," + y + ") " + "was hit!\n");
            	}
            	else{
            		gameFrame.printConsole("Your opponent missed your ships, firing at " + "(" + x + "," + y + ").\n");
            	}
            	int SID = hitMiss[1];
            	if (SID > 0){
            		myShipCount--;
            	}
            	//Creates response x,y,B:SID
            	String repArg = x + "," + y + "," + B + ":" + SID;
            	try {
					gameNetwork.defendReply(repArg);
				} catch (IOException e) {
					e.printStackTrace();
				}
            	turnVal = 0;
        	}
        	if (myShipCount == 0){
        		gameFrame.printConsole("\n************GAME OVER**************\n                         You lost :(");
        		break;
        	} else if (enemyShipCount == 0){
        		gameFrame.printConsole("\n************GAME OVER**************\n                       You won!!! :)");
        		break;
        	}
        }
        gameFrame.btnGrid.removeActnEvt();
    }
    
    private static boolean isAlive(boolean serverOrClient){
    	boolean schroodinger = false;
    	if(serverOrClient){
    		schroodinger = Network.serverSocketC.isConnected();
    	}
    	else{
    		schroodinger = Network.clientSocket.isConnected();
    	}
    	return schroodinger;
    }
    
    public static String getShipName(int ID){
    	String name = "";
    	switch (ID){
    	case 1:
    		name = "Patrol Boat";
    		break;
    	case 2:
    		name = "Submarine";
    		break;
    	case 3:
    		name = "Destroyer";
    		break;
    	case 4:
    		name = "Battleship";
    		break;
    	case 5:
    		name = "Aircraft Carrier";
    		break;
    	default:
    		name = "MissingNo.";
    		break;
    	}
    	return name;
    }

	public static void printRules(){
		gameFrame.printConsole("********Rules for BattleShip********\n" +
    			"\n***********Game Objective***********\n" +
    			"The object of Battleship is to try and sink all of the other player's ships (upper left) before they sink all of yours (bottom right). " +
    			"You try and hit them by selecting the coordinate of one of the squares on the board and firing. " +
    			"Neither you nor the other player can see the other board so you must try to guess where they are. " +
    			"\n\n********Starting a New Game********\n"+
    			"Each player places the 5 ships somewhere on their board by clicking on the ship you want to place and then clicking on a coordinate on your grid (bottom right). " +
    			"The ships can only be placed vertically or horizontally. " +
    			"Ships cannot overlap each other or be placed off the board. " +
    			"Once the guessing begins, the players may not move the ships. " +
    			"The 5 ships are:  Carrier (5), Battleship (4), Cruiser (3), Submarine (3), and Patrol Boat (2)." +
    			"\n\n**********Playing the Game**********\n" +
    			"Player's take turns guessing by selecting coordinates and firing. " +
    			"It will return either hit or miss. " +
    			"When all of the squares that one your ships occupies have been hit, the ship will be sunk. " +
    			"All hits and misses will be recorded automatically for you. " +
    			"As soon as all of one player's ships have been sunk, the game ends, and the player who still has ships remaining wins." +
    			"\n**********************************\n");
    }
}
