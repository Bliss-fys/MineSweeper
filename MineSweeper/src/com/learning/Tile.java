package com.learning;

import javax.swing.JButton;

public class Tile extends JButton {

	private int xPosition;
	private int yPosition;
	private boolean isBomb;
	private boolean isFlag;
	
	public Tile(int XPosition, int YPosition) {
		this.xPosition = XPosition;
		this.yPosition = YPosition;
	}
	
	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}
	
	public int getXPosition() {
		return this.xPosition;
	}
	
	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	
	public int getYPosition() {
		return this.yPosition;
	}
	
	
	public void setIsBomb(boolean isBomb) {
		this.isBomb = isBomb;
	}
	
	public boolean getIsBomb() {
		return this.isBomb;
	}
	
	public void setIsFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}
	
	public boolean getIsFlag() {
		return this.isFlag;
	}
	
	
	
	
	
}
