package com.learning;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import com.learning.Tile;

public class Board extends MouseAdapter implements ActionListener {

	private JFrame frame;
	private JLabel bombsCounter;
	private JLabel timerLabel;
	private int bombsCount;
	private JPanel fieldPanel;
	private JPanel infoPanel;
	private Tile[][] buttons;
	private int fieldHeight = 0;
	private int fieldWidth = 0;
	private int[] XPositions;
	private int[] YPositions;
	private Timer timer;
	
	
	public Board(int bombsCount, int fieldWidth, int fieldHeight) {
		this.bombsCount = bombsCount;
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		
		//creating board
		frame = new JFrame("MineSweeper");
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		fieldPanel = new JPanel(new GridLayout(fieldHeight, fieldWidth));
		
		//adding tiles to the board
		buttons = new Tile[fieldHeight][fieldWidth];
		for(int i = 0; i < fieldHeight; i++) {
			
			for(int j = 0; j < fieldWidth; j++) {
				buttons[i][j] = new Tile(j, i);
				buttons[i][j].addMouseListener(this);
				buttons[i][j].setFocusable(false);
				buttons[i][j].setMargin(new Insets(0, 0, 0, 0));
				buttons[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				buttons[i][j].setVerticalAlignment(SwingConstants.CENTER);
				fieldPanel.add(buttons[i][j]);
			}
		}
		
		//creating top info panel and adding panels to a frame
		infoPanel = new JPanel(new BorderLayout());
		bombsCounter = new JLabel(" " + this.bombsCount);
		bombsCounter.setFont(new Font( "Arial", Font.BOLD, 25));
		bombsCounter.setOpaque(true);
		timerLabel = new JLabel(Integer.toString(000));
		timerLabel.setFont(new Font( "Arial", Font.BOLD, 25));
		timerLabel.setOpaque(true);
		//timerLabel.setBackground(new Color(16));
		infoPanel.add(bombsCounter, BorderLayout.EAST);
		infoPanel.add(timerLabel, BorderLayout.WEST);
		infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		frame.add(fieldPanel, BorderLayout.CENTER);
		frame.add(infoPanel, BorderLayout.NORTH);
		frame.setVisible(true);
		timer = new Timer(1000, this);
		
		//inflating board with random bombs and checking for overlaps
		generateBombs(bombsCount, fieldWidth, fieldHeight, buttons);
		timer.start();
	}

	private Random random;
	private void generateBombs(int bombsCount, int fieldWidth, int fieldHeight, Tile[][] tiles) {
	    random = new Random();
	    XPositions = new int[bombsCount];
	    YPositions = new int[bombsCount];
	    int count = 0;
	    while (count < bombsCount) {
	        int x = random.nextInt(fieldWidth);
	        int y = random.nextInt(fieldHeight);
	        boolean overlap = false;
	        for (int i = 0; i < count; i++) {
	            if (XPositions[i] == x && YPositions[i] == y) {
	            	overlap = true;
	                break;
	            } 
	        }
	        XPositions[count] = x;
	        YPositions[count] = y;
	        
	        if(!overlap) {
	        	tiles[x][y].setIsBomb(true);
                count++;
	        }
	
	    }
	}
	
	public int bombsAround(Tile[][] tiles, Tile target) {
		int bombsAroundCount = 0;
		for(int i = target.getYPosition() - 1; i <= target.getYPosition() + 1; i++){
			for(int j = target.getXPosition() - 1; j <= target.getXPosition() + 1; j++) {
				if( (i >= 0 && i < tiles[0].length) && (j >= 0 && j < tiles.length)) {
					if(tiles[i][j].getIsBomb()) {
						bombsAroundCount++;
					}
				} 
			}
		}
		
		return bombsAroundCount;
	}
	
	
	public void revealTiles(Tile[][] tiles, Tile target, Queue<Tile> queue) {
		//System.out.println("Initial queue size: " + queue.size());
		int flagsCount = 0;
		//checking every adjacent tile
		for(int i = target.getYPosition() - 1; i <= target.getYPosition() + 1; i++){
			for(int j = target.getXPosition() - 1; j <= target.getXPosition() + 1; j++) {
				if( (i >= 0 && i < tiles[0].length) && (j >= 0 && j < tiles.length)) {
					if(tiles[i][j].getIsFlag()) {
						flagsCount++;
					}
				} 
			}
		}
		
		if(flagsCount == bombsAround(tiles, target)) {
			for(int i = target.getYPosition() - 1; i <= target.getYPosition() + 1; i++){
				for(int j = target.getXPosition() - 1; j <= target.getXPosition() + 1; j++) {
					if( (i >= 0 && i < tiles[0].length) && (j >= 0 && j < tiles.length)) {
						if(tiles[i][j].getIsBomb()) { //bombs tiles
							tiles[i][j].setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
							tiles[i][j].setText("🚩");
						} else if(bombsAround(tiles, tiles[i][j]) == 0) { //empty tiles
							tiles[i][j].setEnabled(false);
							tiles[i][j].setText(" ");
							tiles[i][j].setBackground(Color.LIGHT_GRAY);
							revealEmptyTiles(tiles, tiles[i][j], queue);
						} else {
							tiles[i][j].setMargin(new Insets(0, 0, 0, 7)); //number tiles
							tiles[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
							tiles[i][j].setText(" " + bombsAround(tiles, tiles[i][j]));
							tiles[i][j].setForeground(getColor(bombsAround(buttons, tiles[i][j])));
							tiles[i][j].setBackground(Color.LIGHT_GRAY);
						}
					} 
				}
			}
		}
	}
	
	Queue<Tile> queue = new LinkedList<>();
	public void revealEmptyTiles(Tile[][] tiles, Tile target, Queue<Tile> queue) {
		//System.out.println("Revealing empty tiles for tile at position (" + target.getXPosition() + ", " + target.getYPosition() + ")");
		queue.add(target);
		
		while(!queue.isEmpty()) {
			Tile tile = queue.remove();
			//System.out.println("Removing tile at position (" + tile.getXPosition() + ", " + tile.getYPosition() + ") from queue");
			
			if (tile.getText().equals(" ")) {
			    continue;
			}
			
			tile.setEnabled(false);
			tile.setText(" ");
			for(int i = tile.getYPosition() - 1; i <= tile.getYPosition() + 1; i++){
				for(int j = tile.getXPosition() - 1; j <= tile.getXPosition() + 1; j++) {
					if( (i >= 0 && i < tiles[0].length) && (j >= 0 && j < tiles.length)) {
						Tile adjacentTile = tiles[i][j];
						if(bombsAround(tiles, adjacentTile) == 0) { //empty tiles
							adjacentTile.setBackground(Color.LIGHT_GRAY);
							queue.add(adjacentTile);
							//System.out.println("Adding empty tile at position (" + adjacentTile.getXPosition() + ", " + adjacentTile.getYPosition() + ") to queue");
						} else {
							adjacentTile.setMargin(new Insets(0, 0, 0, 7)); //number tiles
							adjacentTile.setFont(new Font("Arial", Font.PLAIN, 40));
							adjacentTile.setText(" " + bombsAround(tiles, adjacentTile));
							adjacentTile.setForeground(getColor(bombsAround(buttons, adjacentTile)));
							adjacentTile.setBackground(Color.LIGHT_GRAY);
						}
					} 
				}
			}
		}
	}
	
	private boolean pressed = false;
	    @Override
	    public void mousePressed(MouseEvent e) {
	    	
	        if (SwingUtilities.isLeftMouseButton(e)) {
	            pressed = true;
	        }
	    }
		    
	@Override
	public void mouseReleased(MouseEvent e) {
		Tile clicked = (Tile) e.getSource();
		if(SwingUtilities.isLeftMouseButton(e) && pressed) {
			if( clicked.getIsBomb()) { //bombs tiles
				clicked.setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
				clicked.setText("💣");
				gameOverLose(buttons, XPositions, YPositions);
				//Board boarddzx = new Board(this.bombsCount, this.fieldWidth, this.fieldHeight);
			} else if(bombsAround(buttons, clicked) == 0) { //empty tiles
				revealEmptyTiles(buttons, clicked, queue);
				clicked.setEnabled(false);
				clicked.setText(" ");
				clicked.setBackground(Color.LIGHT_GRAY);
			} else {
				clicked.setMargin(new Insets(0, 0, 0, 7)); //number tiles
				clicked.setFont(new Font("Arial", Font.PLAIN, 40));
				clicked.setText(" " + bombsAround(buttons, clicked));
				clicked.setForeground(getColor(bombsAround(buttons, clicked)));
				clicked.setBackground(Color.LIGHT_GRAY);
			}
		} else if(SwingUtilities.isRightMouseButton(e) && !pressed) { //flag tiles
			if(clicked.getText().equals("🚩")) { //right click on flag
				clicked.setIsFlag(false);
				clicked.setFocusable(true);
				clicked.setText(" ");
				++this.bombsCount;
				bombsCounter.setText(Integer.toString(this.bombsCount));
			} else { // right click on tile
				clicked.setIsFlag(true); 
				clicked.setFocusable(false);
				clicked.setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
				clicked.setText("🚩");
				--this.bombsCount;
				bombsCounter.setText(Integer.toString(this.bombsCount));
				gameOver(buttons, XPositions);
			}
		}
		pressed = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Tile clicked = (Tile) e.getSource();
		if(e.getClickCount() == 2) {
			revealTiles(buttons, clicked, queue);
		}
	}
	
	int timeElapsed = 0;	
	@Override
	public void actionPerformed(ActionEvent e) {
		timeElapsed++;
		timerLabel.setText(Integer.toString(timeElapsed));
		
	}

	public void revealBombs(Tile[][] tiles, int[] XPositions, int[] YPositions) {
		for(int i = 0; i < XPositions.length; i++) {
			tiles[XPositions[i]][YPositions[i]].setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
			tiles[XPositions[i]][YPositions[i]].setText("💣");
			//Thread.sleep(1500);
		}
	}
	
	public Color getColor(int num) {
		switch(num) {
			case 1:
				return Color.BLUE;
			case 2:
				return Color.GREEN;
			case 3:
				return Color.RED;
			case 4:
				return Color.BLACK;
			case 5:
				return Color.MAGENTA;
			default:
				return Color.BLACK;
		}
	}
	
	public void gameOverLose(Tile[][] tiles, int[] XPositions, int[] YPositions) {
		revealBombs(tiles, XPositions, YPositions);
		timer.stop();
		infoPanel.removeAll();
		JLabel gameOverLabel = new JLabel("Game over");
		gameOverLabel.setFont(new Font( "Arial", Font.PLAIN, 30));
		gameOverLabel.setOpaque(true);
		infoPanel.add(gameOverLabel, BorderLayout.NORTH);
		/*for(int i = 0; i < XPositions.length; i++) {
			Thread.sleep(1500);
			tiles[XPositions[i]][YPositions[i]].setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
			tiles[XPositions[i]][YPositions[i]].setText("🔥");
		}*/
	}
	
	public void gameOver(Tile[][] tiles, int[] XPositions) {
		int flagsCount = 0;
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[0].length; j++) {
				if(tiles[i][j].getIsFlag() && tiles[i][j].getIsBomb()) {
					flagsCount++;
				}
			}
		}
		if(flagsCount == XPositions.length){ 
			System.out.print("You win");
			infoPanel.removeAll();
			JLabel gameOverLabel = new JLabel("You win");
			gameOverLabel.setFont(new Font( "Arial", Font.PLAIN, 30));
			gameOverLabel.setOpaque(true);
			infoPanel.add(gameOverLabel, BorderLayout.NORTH);
			timer.stop();
		}
	}
	
}
