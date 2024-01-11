/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication2;

/**
 *
 * @author sameed
 */
import java.awt.BasicStroke;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Timer;

public class CustomDrawingPanel extends JPanel {
    private Database database; 
    private boolean areLinesIntersectingCCW = false;
    private boolean areLinesIntersectingCrossProduct = false;
    private boolean areLinesIntersectingSlope = false;
    
    private int currentLineIndex = 0;
    private Timer drawingTimer;
    int adjustedY;


    public CustomDrawingPanel(Database database) {
        this.database = database; 
        
        drawingTimer = new Timer(500, e -> {
            currentLineIndex++;
            repaint();  

            if (currentLineIndex >= database.boundaryp.size()) {
                drawingTimer.stop();
            }
        });
    }
    public void startDrawing() {
        currentLineIndex = 0;  
        drawingTimer.start();  
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        // Set the desired line width here
        g2d.setStroke(new BasicStroke(2f));  
        // Draw points
        g2d.setColor(java.awt.Color.red);
        for (Coordinates point : database.allpoints) {
            adjustedY = getHeight() - point.getY();
            g2d.fillOval((int) point.getX() - 2, adjustedY - 2, 6, 6);
        }
        
        
        g2d.setColor(java.awt.Color.black);
        for (Coordinates point : database.boundaryp) {
            adjustedY = getHeight() - point.getY();
            g2d.fillOval((int) point.getX() - 3, adjustedY - 3, 9, 9);
        }
        
        if (database.boundaryp.size() > 1) {
        Coordinates point = database.boundaryp.get(0);
        adjustedY = getHeight() - point.getY();
        Font boldFont = new Font(g2d.getFont().getFontName(), Font.BOLD, g2d.getFont().getSize()+2);
        g2d.setFont(boldFont);
        g2d.drawString("(" + point.getX() + ", " + point.getY() + ")", (int) point.getX()-25, adjustedY-5);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        }
        

        // Draw convex hull
        g2d.setColor(java.awt.Color.BLUE);
    if (database.boundaryp.size() > 1) {
        for (int i = 0; i < currentLineIndex; i++) {
            Coordinates start = database.boundaryp.get(i % database.boundaryp.size());
            Coordinates end = database.boundaryp.get((i+1) % database.boundaryp.size());
            int startY = getHeight() - start.getY();
            int endY = getHeight() - end.getY();
            g2d.drawLine((int) start.getX(), startY, (int) end.getX(), endY);
            start = end;
        }
    }
        
         // Draw lines based on intersection parameters
        g2d.setColor(java.awt.Color.BLUE);
        if (database.x1 != null && database.y1 != null && database.x2 != null && database.y2 != null)
        {
        g2d.drawLine((int) database.x1, (int) database.y1, (int) database.x2, (int) database.y2);
        }
        if (database.x3 != null && database.y3 != null && database.x4 != null && database.y4 != null) {
        g2d.drawLine((int) database.x3, (int) database.y3, (int) database.x4, (int) database.y4);
        }
    }
}
