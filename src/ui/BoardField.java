package ui;

import GameStates.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static utils.Constants.Field.FIELD_SIZE;

public class BoardField {

    private int xPos, yPos, fieldIndex, index;
    private int xOffsetCenter = FIELD_SIZE/2;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;
    private Character piece;
    public Color fieldColor;

    private boolean active;
    BoardOverlay overlay;

    public BoardField(int xPos, int yPos, int index, Character piece, BoardOverlay overlay){
        this.xPos = xPos;
        this.yPos = yPos;
        this.fieldIndex = index;
        this.piece = piece;
        this.overlay = overlay;
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(xPos, yPos, FIELD_SIZE, FIELD_SIZE);
    }

    public void drawSquare(Graphics g){


        g.setColor(fieldColor);
        g.fillRect(xPos, yPos, FIELD_SIZE, FIELD_SIZE);


    }

    public void drawPiece(Graphics g){
        int pieceX;
        int pieceY;

        pieceX = mousePressed?overlay.getMouseX()-FIELD_SIZE/2:xPos;
        pieceY = mousePressed?overlay.getMouseY()-FIELD_SIZE/2:yPos;
        if(overlay.getChessPiecesImgs().containsKey(piece))
            g.drawImage(overlay.getChessPiecesImgs().get(piece), pieceX, pieceY, FIELD_SIZE, FIELD_SIZE, null);
    }

    public void mouseMoved(MouseEvent e){

    }

    public boolean isIn(MouseEvent e){
        return bounds.contains(e.getX(), e.getY());
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

    public Character getPiece() {
        return piece;
    }

    public void setPiece(Character piece) {
        this.piece = piece;
    }
}
