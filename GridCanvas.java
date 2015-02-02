//package GUI;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class GridCanvas extends Canvas implements MouseListener{
	
	private int size, grid;			//size is px, grid is 10x10
	private Frame frame;
	private int[][] gridCheck;
	private int[][] hitMiss;		//0 is nothing, 1 is hit, 2 is miss	
	private ArrayList<ShipButton> placedButtons;
	public int numPlaced = 0;
	public boolean shipsPlaced = false;
	private int[] ships = new int[6];
	
	public GridCanvas(int size, int grid, Frame frame){
		this.size = size;
		setSize(size , size);
		this.grid = grid;
		this.frame = frame;
		gridCheck = new int[grid][grid];
		hitMiss = new int[grid][grid];
		this.placedButtons = new ArrayList<ShipButton>();
		this.addMouseListener(this);
		ships[0] = 0; ships[1] = 2; ships[2] = 3; ships[3] = 3; ships[4] = 4; ships[5] = 5;
	}
	
	public void paint(Graphics g){
		int blockSize = size / grid;
		BufferedImage ocean = null;
		BufferedImage oceanHit = null;
		BufferedImage oceanMiss = null;
		try {
			ocean = ImageIO.read(getClass().getResource("ocean.png"));
			oceanHit = ImageIO.read(getClass().getResource("oceanhit.png"));
			oceanMiss = ImageIO.read(getClass().getResource("oceanmiss.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < grid; i++){
			for(int j = 0; j < grid; j++){
				g.drawImage(ocean, i*blockSize , j*blockSize, null);
			}
		}
		for(int i = 0; i <= grid; i++){
			g.drawLine(0, i*blockSize, size, i*blockSize);
			g.drawLine(i*blockSize, 0, i*blockSize, size);
		}
		for(ShipButton b: placedButtons){
			g.drawImage(b.getImage(), b.xPosition*40, b.yPosition*40, null);
		}
		for(int i = 0; i < grid; i++){
			for(int j = 0; j < grid; j++){
				if(hitMiss[i][j] == 1){
					g.drawImage(oceanHit, i*40, j*40, null);
				} else if (hitMiss[i][j] == 2){
					g.drawImage(oceanMiss, i*40, j*40, null);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (numPlaced != 5) {
			int xSquare = (e.getX() / 40);
			int ySquare = (e.getY() / 40);
			ShipButton selected = (ShipButton) frame.getSelected();
			if(selected.equals(null)){
				frame.printConsole("You have to select a ship first!");
			} else if (checkBounds(xSquare, ySquare, selected)) {
				String name = selected.toString();
				String ori = selected.getOri();
				BufferedImage img = null;
				try {
					img = ImageIO.read(getClass().getResource(ori + name + ".png"));
				} catch (IOException exc) {
					exc.printStackTrace();
				}
				selected.setImage(img);
				selected.setPosition(xSquare, ySquare);
				repaint();
				placedButtons.add(selected);
				selected.setVisible(false);
				frame.setSelectedNull();
				numPlaced++;
				if (numPlaced == 5) {
					shipsPlaced = true;
					frame.printConsole("Please wait while the other player finishes...\n");
				}
			}
		}
	}
	
	public boolean checkBounds(int x, int y, ShipButton placed){
		int size = 0;
		int id = 0;
		if(placed.toString().equals("carrier")){
			size = 5;
			id = 5;
		}else if(placed.toString().equals("battleship")){
			size = 4;
			id = 4;
		}else if (placed.toString().equals("submarine")){
			size = 3;
			id = 3;
		}else if (placed.toString().equals("cruiser")){
			size = 3;
			id = 2;
		}else {
			size = 2;
			id = 1;
		}
		String ori = placed.getOri();
		if((ori.equals("h") && x + size > 10 )||( ori.equals("v") && y + size > 10)){
			frame.printConsole("Invalid position! Try again.\n");
			return false;
		} else {
			for (int check = 0; check < 2; check++) {
				for (int i = 0; i < size; i++) {
					if (ori.equals("h")) {
						if (check == 0) {
							if (gridCheck[i + x][y] > 0) {
								frame.printConsole("There's already a ship there! Try again.\n");
								return false;
							}
						} else {
							gridCheck[i + x][y] = id;
						}
					} else {
						if (check == 0) {
							if (gridCheck[x][y + i] > 0) {
								frame.printConsole("There's already a ship there! Try again.\n");
								return false;
							}
						} else {
							gridCheck[x][y + i] = id;
						}
					}
				}
			}
			return true;
		}
		
	}
	//int[0] is hit or miss, int[1] is ship id if sunk
	public int[] attacked(int x, int y){
		int[] returnArray = new int[2];
		if(gridCheck[x][y] > 0){
			hitMiss[x][y] = 1;
			int id = gridCheck[x][y];
			ships[id] -= 1;
			if(ships[id] == 0){
				returnArray[1] = id;
			}
			returnArray[0] = 1;
		} else {
			hitMiss[x][y] = 2;
		}
		repaint();
		return returnArray;
	}
	
	public boolean isPlaced(){
		return shipsPlaced;
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {	
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
}
