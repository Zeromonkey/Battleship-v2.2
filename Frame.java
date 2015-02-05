import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class Frame implements ItemListener, ActionListener{
	
	private JPanel placement, ships, comboBoxPane;
	private final static int SIZE = 10;
	static JFrame mainFrame;
	private JTextArea console;
	private String messages = "";
	private ShipButton selected = null;
	public JButton[] vShips, hShips;
	public ButtonGrid btnGrid;
	public JButton fireButton;
	public GridCanvas ownShips;
	public boolean shotsFired;
	  
	public Frame(){
		mainFrame = new JFrame("Battleship");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(825,840);
		mainFrame.setResizable(false);
		mainFrame.setLayout(new GridLayout(2,2));
		mainFrame.setBackground(Color.blue);
		ImageIcon img = new ImageIcon(getClass().getResource("oceanhit.png"));
		mainFrame.setIconImage(img.getImage());
	}
   
   public void init(){
      console = new JTextArea();
      console.setLineWrap(true);
      console.setWrapStyleWord(true);
      DefaultCaret caret = (DefaultCaret)console.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      JScrollPane consoleScroll = new JScrollPane(console);
      consoleScroll.setPreferredSize(new Dimension(350,350));
      console.setBackground(Color.BLACK);
      console.setFont(new Font("STENCIL", Font.PLAIN, 18));
      console.setForeground(Color.GREEN);
      console.setEditable(false);
      
      JPanel enemyShips = new JPanel(new GridLayout(SIZE, SIZE));
      btnGrid = new ButtonGrid(SIZE, SIZE, enemyShips, this);
      
      this.ships = new JPanel();
      this.comboBoxPane = new JPanel();
      this.placement = new JPanel(new CardLayout());
      JPanel hShips = new JPanel(new FlowLayout());
      hShips.setPreferredSize(new Dimension(250,375));
      JPanel vShips = new JPanel(new FlowLayout());
      vShips.setPreferredSize(new Dimension(375,375));
      String[] cbItems = new String[]{"Horizontal Ships", "Vertical Ships"};
      JComboBox<String> cb = new JComboBox<String>(cbItems);
      cb.setPreferredSize(new Dimension(350, 25));
      cb.setEditable(false);
      cb.addItemListener(this);
      comboBoxPane.add(cb);
      ships.add(comboBoxPane, BorderLayout.CENTER);
      ships.add(placement);
      
      createShips(hShips, true);
      createShips(vShips, false);
      
      placement.add(hShips, "Horizontal Ships");
      placement.add(vShips, "Vertical Ships");
      
      ownShips = new GridCanvas(400, SIZE, this);
      
      mainFrame.add(enemyShips);
      mainFrame.add(consoleScroll);
      mainFrame.add(ships);
      mainFrame.add(ownShips);
      mainFrame.setVisible(true);
   }
   
   public void start(){
	   printConsole("All ships have been placed!\n");
	   ships.remove(comboBoxPane);
	   ships.remove(placement);
	   fireButton = new JButton();
	   fireButton.setPreferredSize(new Dimension(256,256));
	   try{
		   Image img = ImageIO.read(getClass().getResource("fireButton.jpg"));
		   fireButton.setIcon(new ImageIcon(img));
	   } catch (IOException e){
		   e.printStackTrace();
	   }
	   ships.add(fireButton);
	   ships.revalidate();
	   ships.repaint();
	   fireButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent fired) {
				shotsFired = true;
			}
       });
	   btnGrid.addActnEvt();
	   printConsole("Let the game begin!\n");
   }
   
   public void createShips(JPanel panel, boolean horizontal){
	   String ori;
	   if(horizontal){
		   ori = "h";
	   } else {
		   ori = "v";
	   }
	   JButton carrier = new ShipButton("carrier", ori);
	   JButton battleship = new ShipButton("battleship", ori);
	   JButton cruiser = new ShipButton("cruiser", ori);
	   JButton submarine = new ShipButton("submarine", ori);
	   JButton patrol = new ShipButton("patrol", ori);
	   try{
		   Image CarrierM = ImageIO.read(getClass().getResource(ori + "carrier.png"));
		   carrier.setIcon(new ImageIcon(CarrierM));
		   Image BattleshipM = ImageIO.read(getClass().getResource(ori + "battleship.png"));
		   battleship.setIcon(new ImageIcon(BattleshipM));
		   Image CruiserM = ImageIO.read(getClass().getResource(ori + "cruiser.png"));
		   cruiser.setIcon(new ImageIcon(CruiserM));
		   Image SubmarineM = ImageIO.read(getClass().getResource(ori + "submarine.png"));
		   submarine.setIcon(new ImageIcon(SubmarineM));
		   Image PatrolM = ImageIO.read(getClass().getResource(ori + "patrol.png"));
		   patrol.setIcon(new ImageIcon(PatrolM));
	   } catch (IOException ex){
		   System.out.println("Cannot find ships!");
	   }
	   if(horizontal){
		   JButton[] hShips = {carrier, battleship, cruiser, submarine, patrol};
		   this.hShips = hShips;
		   for(JButton b : hShips){
			   panel.add(b);
			   b.addActionListener(this);
		   }
	   } else {
		   JButton[] vShips = {carrier, battleship, cruiser, submarine, patrol};
		   this.vShips = vShips;
		   for(JButton b : vShips){
			   panel.add(b);
			   b.addActionListener(this);
		   }
	   }
   }
   
   public void itemStateChanged(ItemEvent evt) {
	   for(int i = 0; i < hShips.length ; i++){
		   if(hShips[i].isVisible() == false){
			   vShips[i].setVisible(false);
		   }
	   }
	   for(int i = 0; i < vShips.length ; i++){
		   if(vShips[i].isVisible() == false){
			   hShips[i].setVisible(false);
		   }
	   }
	    CardLayout cl = (CardLayout)(placement.getLayout());
	    cl.show((JPanel)placement, (String)evt.getItem());
   }
   
   public void actionPerformed(ActionEvent evt){
	   selected = (ShipButton) evt.getSource();	   
	   printConsole(selected.toString() + " has been selected!\n");
   }
   
   public void printConsole(String message){
	   messages += message;
	   console.setText(messages);
   }
   
   public JButton getSelected(){
	   return selected;
   }
   
   public void setSelectedNull(){
	   selected = null;
   }
   
   public JPanel getPlacementPanel(){
	   return placement;
   }
   
   public String fire (){								//Returns firing information in standard form (ATK:x,y)
	   String arg = "ATK:";								//Defaulted for now to a standard attack
	   int xNum = btnGrid.getXSelect();
	   int yNum = btnGrid.getYSelect();
	   arg += xNum + "," + yNum;
	   return arg;
   }
   
}
