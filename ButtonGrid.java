import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ButtonGrid extends Component {
   
	private Frame frame;
	private CoordinateButton[][] grid;
	private int xSelect = 0;
	private int ySelect = 0;

	public ButtonGrid(int width, int height, JPanel panel, Frame frame) {
		this.frame = frame;
		grid = new CoordinateButton[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				grid[x][y] = new CoordinateButton(x,y);
				grid[x][y].setOpaque(true);
				grid[x][y].setContentAreaFilled(false);
				grid[x][y].setPreferredSize(new Dimension(40, 40));
				try {
					Image ocean = ImageIO.read(getClass().getResource("ocean.png"));
					grid[x][y].setIcon(new ImageIcon(ocean));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				panel.add(grid[x][y]);
			}
		}
	}
	public void addActnEvt(){
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				grid[x][y].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						xSelect = ((CoordinateButton) evt.getSource()).getXCor();
						ySelect = ((CoordinateButton) evt.getSource()).getYCor();
						frame.printConsole("Button " + xSelect + ", " + ySelect + " selected!\n");
					}
				});
			}
		}
	}
	
	public int getXSelect(){
		return xSelect;
	}
	
	public int getYSelect(){
		return ySelect;
	}
	
	public void selectHit(int x, int y){
		try{
			Image hit = ImageIO.read(getClass().getResource("oceanhit.png"));
			grid[x][y].setIcon(new ImageIcon(hit));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.printConsole("It was a hit! \n");
	}
	
	public void selectMiss(int x, int y){
		try{
			Image miss = ImageIO.read(getClass().getResource("oceanmiss.png"));
			grid[x][y].setIcon(new ImageIcon(miss));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.printConsole("It was a miss! \n");
	}
}
