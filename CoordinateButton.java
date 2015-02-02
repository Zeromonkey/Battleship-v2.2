import javax.swing.JButton;

@SuppressWarnings("serial")
public class CoordinateButton extends JButton{
	
	public int xCor, yCor;
	
	public CoordinateButton(int x, int y){
		this.xCor = x;
		this.yCor = y;
	}
	public int getXCor(){
		return xCor;
	}
	public int getYCor(){
		return yCor;
	}
}
