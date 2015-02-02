import java.awt.image.BufferedImage;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class ShipButton extends JButton{
	private String name;
	private String ori;
	public int xPosition, yPosition;
	private BufferedImage img;
	
	public ShipButton(String name, String ori){
		super();
		this.name = name;
		this.ori = ori;
	}
	public String toString(){
		return name;
	}
	public String getOri(){
		return ori;
	}
	public void setPosition(int xPosition, int yPosition){
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	public void setImage(BufferedImage img){
		this.img = img;
	}
	public BufferedImage getImage(){
		return img;
	}
}
