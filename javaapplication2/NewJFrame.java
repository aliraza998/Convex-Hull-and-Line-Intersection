/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaapplication2;

import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javaapplication2.Database;
import javaapplication2.Coordinates;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Saadu
 */
public class NewJFrame extends javax.swing.JFrame {

    Database database = new Database();
    private ChoicePage choicepage;
    private String currentAlgorithm = "";
    private String lastAlgorithm = "";

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame(ChoicePage choicepage) {
        this.choicepage = choicepage;
        initComponents();
    }

//    private double crossProduct(Coordinates a, Coordinates b, Coordinates c) {
//        return (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
//    }
    
    private void saveBoundaryPointsToFile() {
    JFileChooser fileChooser = new JFileChooser();
    
    // Set the default file name with the ".txt" extension
    fileChooser.setSelectedFile(new File("boundary_points.txt"));
    
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
    fileChooser.setFileFilter(filter);

    int returnValue = fileChooser.showSaveDialog(this);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        try {
            File file = fileChooser.getSelectedFile();
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            for (Coordinates point : database.boundaryp) {
                bufferedWriter.write(point.getX() + "," + point.getY());
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
            JOptionPane.showMessageDialog(rootPane, "Boundary points saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(rootPane, "Error saving boundary points to file: " + e.getMessage());
        }
    }
}


    private int crossProduct(Coordinates A, Coordinates B, Coordinates C) {
        return (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x);
    }
    

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void computeConvexHull() {
        String selectedAlgorithm = jComboBox1.getSelectedItem().toString();
        

        switch (selectedAlgorithm) {
            case "Graham Scan" -> {
                computeConvexHullGraham();
                jLabel7.setText("Graham Scan");
                currentAlgorithm = "Graham Scan";
            }
            case "Jarvis March" -> {
                computeConvexHullJarvis();
                jLabel7.setText("Jarvis March");
                currentAlgorithm = "Jarvis March";
            }
            case "Quick Elimination" -> {
                computeConvexHullQuick();
                jLabel7.setText("Quick Elimination");
                currentAlgorithm = "Quick Elimination";
            }
            case "Brute Force" -> {
                computeConvexHullBruteForce();
                jLabel7.setText("Brute Force");
                currentAlgorithm = "Brute Force";
            }

            default ->
                System.out.println("Selected algorithm not implemented");
        }

        drawingPanel.repaint();
    }

    
private void computeConvexHullBruteForce() {
    if (database.allpoints.size() < 3)
        {
            return;
       }
    database.sortAllPoints();
    for (int i = 0; i < database.allpoints.size(); i++) {
            for (int j = 0; j < database.allpoints.size(); j++) {
                if (i != j) {
                    boolean isOnRight = true;

                    for (int k = 0; k < database.allpoints.size(); k++) {
                        if (k != i && k != j) {
                            int crossProduct = (int) crossProduct(
                                    database.allpoints.get(i), database.allpoints.get(j), database.allpoints.get(k));

                            if (crossProduct < 0) {
                                isOnRight = false;
                                break;
                            }
                        }
                    }

                    if (isOnRight) {
                        if (!database.boundaryp.contains(database.allpoints.get(i))) {
                            database.boundaryp.add(database.allpoints.get(i));
                        }
                        if (!database.boundaryp.contains(database.allpoints.get(j))) {
                            database.boundaryp.add(database.allpoints.get(j));
                        }
                    }
                }
            }
        }
        orderPoints(database.boundaryp);  
}

private void orderPoints(List<Coordinates> hullPoints) {
    if (hullPoints.size() > 1) {
        // Find the bottom-most point (or left-most point in case of tie)
        Coordinates start = Collections.min(hullPoints, Comparator.comparingInt(Coordinates::getY).thenComparingInt(Coordinates::getX));
        // Sort the other points based on polar angle with respect to 'start'
        hullPoints.sort((p1, p2) -> {
            double dx1 = p1.getX() - start.getX();
            double dy1 = p1.getY() - start.getY();
            double dx2 = p2.getX() - start.getX();
            double dy2 = p2.getY() - start.getY();

            double angle1 = Math.atan2(dy1, dx1);
            double angle2 = Math.atan2(dy2, dx2);

            // If angles are the same, sort by distance to 'start'
            if (angle1 == angle2) {
                double dist1 = dx1 * dx1 + dy1 * dy1;
                double dist2 = dx2 * dx2 + dy2 * dy2;
                return Double.compare(dist1, dist2);
            }
            return Double.compare(angle1, angle2);
        });

        // Remove 'start' from hullPoints if it's not the first element after sorting
        hullPoints.remove(start);
        // Add 'start' at the beginning of the list
        hullPoints.add(0, start);
    }
}
    public void computeConvexHullQuick() {

        if (database.allpoints.size() < 3) {
            return;
        }

        Coordinates minXPoint = Collections.min(database.allpoints, Comparator.comparing(Coordinates::getX).thenComparing(Coordinates::getY));
        Coordinates maxXPoint = Collections.max(database.allpoints, Comparator.comparing(Coordinates::getX).thenComparing(Coordinates::getY));

        database.boundaryp.add(minXPoint);

        List<Coordinates> leftSet = new ArrayList<>();
        List<Coordinates> rightSet = new ArrayList<>();

        for (Coordinates p : database.allpoints) {
            if (p.equals(minXPoint) || p.equals(maxXPoint)) {
                continue;
            }

            if (crossProductQuick(minXPoint, maxXPoint, p) > 0) {
                rightSet.add(p);
            } else {
                leftSet.add(p);
            }
        }

        findHull(minXPoint, maxXPoint, rightSet);
        database.boundaryp.add(maxXPoint);
        findHull(maxXPoint, minXPoint, leftSet);
        Coordinates last = database.boundaryp.getLast();
        Coordinates secondLast = database.boundaryp.get(database.boundaryp.size()-2);                
        if (last.getX() == secondLast.getX() && last.getY() == secondLast.getY()) {
            database.boundaryp.removeLast();
        }
    }

    private void findHull(Coordinates A, Coordinates B, List<Coordinates> points) {
        if (points.isEmpty()) {
            return;
        }

        Coordinates furthest = points.stream()
                .max(Comparator.comparingDouble(p -> distanceFromLine(A, B, p)))
                .orElse(null);

        if (furthest != null && !furthest.equals(B)) {
            // Make sure B is in the list before finding its index
            if (!database.boundaryp.contains(B)) {
                database.boundaryp.add(B);
            }
            int indexB = database.boundaryp.indexOf(B);
            database.boundaryp.add(indexB, furthest);

            List<Coordinates> leftOfAF = points.stream()
                    .filter(p -> crossProduct(A, furthest, p) > 0)
                    .collect(Collectors.toList());

            List<Coordinates> leftOfFB = points.stream()
                    .filter(p -> crossProduct(furthest, B, p) > 0)
                    .collect(Collectors.toList());

            findHull(A, furthest, leftOfAF);
            findHull(furthest, B, leftOfFB);
        }
    }

    private double distanceFromLine(Coordinates A, Coordinates B, Coordinates P) {
        double ABx = B.getX() - A.getX();
        double ABy = B.getY() - A.getY();
        double magnitudeAB = ABx * ABx + ABy * ABy;
        double crossProductQuickValue = crossProductQuick(A, B, P);
        return Math.abs(crossProductQuickValue) / Math.sqrt(magnitudeAB);
    }

    private double crossProductQuick(Coordinates a, Coordinates b, Coordinates c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    private void computeConvexHullAndrew() {
        System.out.println("Computing convex hull for " + database.allpoints.size() + " points.");
        if (database.allpoints.size() < 3)
        {
            return;
        }
        List<Coordinates> lower = new ArrayList<>();
        for (Coordinates point : database.allpoints) {
            while (lower.size() >= 2 && crossProduct(lower.get(lower.size() - 2), lower.get(lower.size() - 1), point) <= 0) {
                Coordinates xy = lower.remove(lower.size() - 1);
            }
            lower.add(point);
        }

        List<Coordinates> upper = new ArrayList<>();
        for (int i = database.allpoints.size() - 1; i >= 0; i--) {
            Coordinates point = database.allpoints.get(i);
            while (upper.size() >= 2 && crossProduct(upper.get(upper.size() - 2), upper.get(upper.size() - 1), point) <= 0) {
                Coordinates xy = upper.remove(upper.size() - 1);
            }
            upper.add(point);
        }

        // Remove the last point of each list to prevent the duplication of the points where the lists meet
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);

        // Concatenate the lower and upper hulls to get the convex hull
        database.boundaryp.addAll(lower);
        database.boundaryp.addAll(upper);

    }
    
    // Graham Scan
    private void computeConvexHullGraham() {
        if (database.allpoints.size() < 3) {
            return;
        }

        // Find the bottom-most point
        Coordinates start = Collections.min(database.allpoints, Comparator.comparing(Coordinates::getY).thenComparing(Coordinates::getX));

        // Sort points by polar angle with start
        database.allpoints.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - start.getY(), p1.getX() - start.getX());
            double angle2 = Math.atan2(p2.getY() - start.getY(), p2.getX() - start.getX());
            return Double.compare(angle1, angle2);
        });

        Stack<Coordinates> stack = new Stack<>();
        stack.push(database.allpoints.get(0));
        stack.push(database.allpoints.get(1));
        stack.push(database.allpoints.get(2));

        for (int i = 3; i < database.allpoints.size(); i++) {
            Coordinates top = stack.peek();
            Coordinates next = database.allpoints.get(i);

            while (stack.size() > 1 && crossProduct(stack.get(stack.size() - 2), top, next) <= 0) {
                stack.pop();
                top = stack.peek();
            }
            stack.push(next);
        }

        database.boundaryp.clear();
        database.boundaryp.addAll(stack);
    }

    private void computeConvexHullJarvis() {
        if (database.allpoints.size() < 3) {
            return;
        }
        // Find the left-most point
        Coordinates start = Collections.min(database.allpoints, Comparator.comparing(Coordinates::getX));

        List<Coordinates> result = new ArrayList<>();
        Coordinates current = start;
        do {
            result.add(current);
            Coordinates nextTarget = database.allpoints.get(0);

            for (int i = 1; i < database.allpoints.size(); i++) {
                if (current == nextTarget || crossProduct(current, nextTarget, database.allpoints.get(i)) < 0) {
                    nextTarget = database.allpoints.get(i);
                }
            }
            current = nextTarget;

        } while (current != start);

        database.boundaryp.clear();
        database.boundaryp.addAll(result);
    }
    
    private void generateRandomPoints() {
        Random random = new Random();
        int numPoints = 5; 

        for (int i = 0; i < numPoints; i++) {
            int x = random.nextInt(20, drawingPanel.getWidth()-20);
            int y = random.nextInt(20, drawingPanel.getHeight()-20);
            database.add(x, y);
        }

        drawingPanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        solveButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        bPointsLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        drawingPanel = new CustomDrawingPanel(database);
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        pointsNumLabel = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(854, 480));
        setMinimumSize(new java.awt.Dimension(854, 480));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(854, 480));
        setResizable(false);
        setSize(new java.awt.Dimension(854, 480));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Graham Scan", "Jarvis March", "Quick Elimination", "Brute Force" }));
        jComboBox1.setAlignmentX(0.0F);
        jComboBox1.setAlignmentY(0.0F);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 220, -1));

        solveButton.setText("Solve");
        solveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solveButtonActionPerformed(evt);
            }
        });
        getContentPane().add(solveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 220, -1));
        //solveButton.addActionListener(e -> computeConvexHull());

        jButton2.setText("Input Points");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 220, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Boundary Points:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 390, 200, 30));

        bPointsLabel.setForeground(new java.awt.Color(255, 255, 255));
        bPointsLabel.setText("No boundary points");
        getContentPane().add(bPointsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 420, 590, 30));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Execution Time Last run:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Algorithm: ");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("No Algo applied");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, -1, -1));

        timeLabel.setForeground(new java.awt.Color(255, 255, 255));
        timeLabel.setText("null");
        getContentPane().add(timeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 260, 170, -1));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Duration");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        drawingPanel.setBackground(new java.awt.Color(204, 204, 204));
        drawingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        drawingPanel.setForeground(new java.awt.Color(102, 153, 255));
        drawingPanel.setMinimumSize(new java.awt.Dimension(350, 350));
        drawingPanel.setName(""); // NOI18N
        drawingPanel.setPreferredSize(new java.awt.Dimension(350, 350));
        drawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drawingPanelMouseClicked(evt);
            }
        });
        getContentPane().add(drawingPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, 570, 350));
        //drawingPanel = new javax.swing.JPanel() {
            //    @Override
            //    protected void paintComponent(java.awt.Graphics g) {
                //        super.paintComponent(g);
                //        // Drawing code here
                //
                //        // Draw points
                //        g.setColor(java.awt.Color.BLACK);
                //        for (Coordinates point : database.getPoints()) {
                    //            g.fillOval((int) point.getX() - 2, (int) point.getY() - 2, 5, 5);
                    //        }
                //
                //        // Draw convex hull
                //        g.setColor(java.awt.Color.RED);
                //        if (database.boundaryp.size() > 1) {
                    //            Coordinates start = database.boundaryp.get(0);
                    //            for (int i = 1; i <= database.boundaryp.size(); i++) {
                        //                Coordinates end = database.boundaryp.get(i % database.boundaryp.size());
                        //                g.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
                        //                start = end;
                        //            }
                    //        }
                //    }
            //};

        //drawingPanel.addMouseListener(new MouseAdapter() {
            //    @Override
            //    public void mouseClicked(MouseEvent e) {
                //        // Add point to database and repaint
                //        database.add(e.getX(), e.getY());
                //        drawingPanel.repaint();
                //        System.out.println("Point added: " + e.getX() + ", " + e.getY());
                //    }
            //});

    jButton1.setText("Back");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });
    getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, 70, -1));

    jButton3.setText("Clear All Points");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton3ActionPerformed(evt);
        }
    });
    getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 430, 140, -1));

    jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
    jLabel4.setForeground(new java.awt.Color(255, 255, 255));
    jLabel4.setText("X");
    jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel4MouseClicked(evt);
        }
    });
    getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 0, 20, -1));

    jButton4.setText("Generate 5 random points");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton4ActionPerformed(evt);
        }
    });
    getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 220, 30));

    jLabel2.setForeground(new java.awt.Color(255, 255, 255));
    jLabel2.setText("Points generated: ");
    getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, -1, -1));

    pointsNumLabel.setForeground(new java.awt.Color(255, 255, 255));
    pointsNumLabel.setText("No Points");
    getContentPane().add(pointsNumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 300, -1, -1));

    jButton6.setText("Andrew's Monotone Chain");
    jButton6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255), 4));
    jButton6.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton6ActionPerformed(evt);
        }
    });
    getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 220, -1));

    jLabel8.setForeground(new java.awt.Color(255, 255, 255));
    jLabel8.setText("(With Reference to Research Paper)");
    getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 210, -1));

    jButton5.setText("Export Boundary Points");
    jButton5.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton5ActionPerformed(evt);
        }
    });
    getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 390, 200, -1));

    jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication2/DAA(1100_480)-transformed.jpeg"))); // NOI18N
    jLabel3.setMaximumSize(new java.awt.Dimension(1100, 480));
    jLabel3.setMinimumSize(new java.awt.Dimension(1100, 480));
    getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 480));

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int num = 0;
        try {
            num = Integer.parseInt(JOptionPane.showInputDialog(rootPane, "How many points do want to enter"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Please enter integer.");
        }
        for (int i = 0; i < num; i++) {
            boolean validInput = false;
            int x = 0, y = 0;
            while (!validInput) {
                try {
                    String input = JOptionPane.showInputDialog(null, "Enter coordinates in format x,y:");

                    if (input == null) {
                        System.out.println("No input provided. Exiting.");
                        return;
                    }

                    String[] parts = input.split(",");

                    x = Integer.parseInt(parts[0].trim());
                    y = Integer.parseInt(parts[1].trim());

                    database.add(x, y);

                    validInput = true;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter coordinates in the format x,y.");
                }
            }
        }
        pointsNumLabel.setText((database.allpoints.size()) + "");
        drawingPanel.repaint();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void drawingPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanelMouseClicked
        // TODO add your handling code here:
//        database.add(evt.getX(), evt.getY());
        database.add(evt.getX(), drawingPanel.getHeight() - evt.getY());
        drawingPanel.repaint();
        pointsNumLabel.setText((database.allpoints.size()) + "");
        System.out.println("Point added: " + evt.getX() + ", " + evt.getY());
    }//GEN-LAST:event_drawingPanelMouseClicked

    private void solveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solveButtonActionPerformed
        // TODO add your handling code here:
        if (database.allpoints.size() < 3)
        {
            JOptionPane.showMessageDialog(rootPane, "Please input atleast 3 points to successfully to exectue convex hull.");
            return;
        }
        database.clearConvexHull();
        long startTime = System.currentTimeMillis();
        computeConvexHull();
        lastAlgorithm = jComboBox1.getSelectedItem().toString();
        ((CustomDrawingPanel)drawingPanel).startDrawing();
        long endTime = System.currentTimeMillis();
        
        double executionTime = (endTime - startTime) / 10000.0;
        timeLabel.setText(String.valueOf(executionTime) + "s");
        String text1 = "", text2 = "";
        for (Coordinates point : database.boundaryp) {
            text1 = text1 + "(" + point.getX() + ", " + point.getY() + ") ";
        }
        bPointsLabel.setText(text1);
    }//GEN-LAST:event_solveButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        choicepage.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        database.clearAllPoints();
        database.clearConvexHull();
        bPointsLabel.setText("No boundary points");
        drawingPanel.repaint();
        pointsNumLabel.setText((database.allpoints.size()) + "");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        generateRandomPoints();
        pointsNumLabel.setText((database.allpoints.size()) + "");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        database.clearConvexHull();
//        currentAlgorithm = "Andrew's Monotone Algorithm";
        long startTime = System.currentTimeMillis();
        computeConvexHullAndrew();
//        lastAlgorithm = "Andrew's Monotone Chain";
        ((CustomDrawingPanel)drawingPanel).startDrawing();
        long endTime = System.currentTimeMillis();
        
        double executionTime = (endTime - startTime) / 10000.0;
        timeLabel.setText(String.valueOf(executionTime) + "s");
        String text1 = "", text2 = "";
        for (Coordinates point : database.boundaryp) {
            text1 = text1 + "(" + point.getX() + ", " + point.getY() + ") ";
        }
        jLabel7.setText("Andrew's Monstone Chain");
        bPointsLabel.setText(text1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        saveBoundaryPointsToFile();
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new NewJFrame().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bPointsLabel;
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel pointsNumLabel;
    private javax.swing.JButton solveButton;
    private javax.swing.JLabel timeLabel;
    // End of variables declaration//GEN-END:variables

}
