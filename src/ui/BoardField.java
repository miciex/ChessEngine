package ui;

import GameStates.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static utils.Constants.Field.FIELD_SIZE;

public class BoardField {

    private int xPos, yPos, Fieldindex, index;
    private int xOffsetCenter = FIELD_SIZE/2;
    private GameState state;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;
    private Character piece;
    private int mouseX;
    private int mouseY;
    private boolean active;

    public BoardField(int xPos, int yPos, int index, Character piece){
        this.xPos = xPos;
        this.yPos = yPos;
        this.Fieldindex = index;
        this.piece = piece;
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(xPos-xOffsetCenter, yPos, FIELD_SIZE, FIELD_SIZE);
    }

    public void draw(Graphics g){

        int pieceX;
        int pieceY;

        if (Fieldindex % 2 == 0) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.white);
        }
        g.drawRect(xPos, yPos, FIELD_SIZE, FIELD_SIZE);
        if(Character.isUpperCase(piece)){
            g.setColor(Color.LIGHT_GRAY);
        }else{
            g.setColor(Color.DARK_GRAY);
        }
        if(mousePressed){
            pieceX = mouseX;
            pieceY = mouseY;
        }else{
            pieceX = xPos+xOffsetCenter;
            pieceY = yPos+xOffsetCenter;
        }

        g.drawString(String.valueOf(piece), pieceX, pieceY);
    }

    public void mouesMoved(MouseEvent e){

    }

    public void update(){
        index = 0;
        if(mouseOver){
            index = 1;
        }
        if(mousePressed){
            index = 2;
        }
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void resetBools(){
        mouseOver = false;
        mousePressed = false;
    }

    public GameState getState() {
        return state;
    }
}
