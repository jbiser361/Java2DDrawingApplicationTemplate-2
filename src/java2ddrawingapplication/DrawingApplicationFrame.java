/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;


/**
 *
 * @author James
 */
public class DrawingApplicationFrame extends JFrame
{
    JFrame frame = new JFrame("Java 2D Drawings");
    private DrawPanel dp = new DrawPanel();
    private JPanel jp = new JPanel(new BorderLayout());
    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    JPanel top = new JPanel();
    JPanel bottom = new JPanel();
        // create the widgets for the firstLine Panel.
    private JButton clear = new JButton("Clear");
    private JButton undo = new JButton("Undo");
    private JButton color1 = new JButton("1st Color");
    private JButton color2 = new JButton("2nd Color");
    
    private JComboBox shapes = new JComboBox<>(new String[] {"Rectangle", "Oval", "Line"});
    
    private JColorChooser chooser1 = new JColorChooser();
    private JColorChooser chooser2 = new JColorChooser();
    
    //create the widgets for the secondLine Panel.
    private JCheckBox gradient = new JCheckBox("Use Gradient");
    private JCheckBox filled = new JCheckBox("Filled");
    private JCheckBox dashed = new JCheckBox("Dashed");
   
    private JSpinner dashLength = new JSpinner();
    private JSpinner lineWidth = new JSpinner();
   
    private JLabel dash = new JLabel("Dash Length:");
    private JLabel width = new JLabel("Line Width:");
    private JLabel shape = new JLabel("Shapes:");
    private JLabel options = new JLabel("Options:");
    
    ArrayList<MyShapes> shapesArray = new ArrayList<>();
    private Stroke stroke;
    int startx;
    int starty;
    int endx;
    int endy;
    Point startp;
    Point endp;
    MyRectangle rectangle;
    String selectedShape;
    Paint mainColor = Color.BLACK;
    Boolean isFilled = false;

    

    // Variables for drawPanel.

    // add status label
    private JLabel mousePos = new JLabel("( , )");
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        // add widgets to panels        
        jp.add(top,BorderLayout.NORTH);
        jp.add(bottom, BorderLayout.SOUTH);
        top.setBackground(Color.CYAN);
        bottom.setBackground(Color.CYAN);
        // firstLine widgets
        top.add(shape);
        top.add(shapes);
        top.add(color1);
        top.add(color2);
        top.add(undo);
        top.add(clear);
        // secondLine widgets
        bottom.add(options);
        bottom.add(filled);
        bottom.add(gradient);
        bottom.add(dashed);
        bottom.add(width);
        bottom.add(lineWidth);
        bottom.add(dash);
        bottom.add(dashLength);
        // add top panel of two panels
        add(jp,BorderLayout.NORTH);
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        dp.setBackground(Color.white);
        add(dp, BorderLayout.CENTER);
        add(mousePos, BorderLayout.SOUTH);
        //add listeners and event handlers
        frame.show();
        
        color1.setBackground(Color.BLACK);
        color1.addActionListener(new ActionListener(){
            @Override 
            public void actionPerformed(ActionEvent event){
                color1.setBackground(JColorChooser.showDialog(null, "Pick your color", Color.BLACK));
            }
        });
        color2.setBackground(Color.BLACK);
        color2.addActionListener(new ActionListener(){
            @Override 
            public void actionPerformed(ActionEvent event){
                color2.setBackground(JColorChooser.showDialog(null, "Pick your color", Color.BLACK));
            }
        });
        
        undo.addActionListener(new ActionListener(){
            @Override 
            public void actionPerformed(ActionEvent event){
                if (!shapesArray.isEmpty()){
                    shapesArray.remove(shapesArray.size()-1);
                    repaint();      
                }

            }
        });
        clear.addActionListener(new ActionListener(){
            @Override 
            public void actionPerformed(ActionEvent event){
                if (!shapesArray.isEmpty()){
                    shapesArray.clear();
                    repaint();      
                }
            }
        });

    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        //Boolean grad = gradient.isEnabled()
        public DrawPanel()
        {
            MouseHandler mouseHandler = new MouseHandler();
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
 
            for (MyShapes i: shapesArray){
                i.draw(g2d);
            }
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {


            public void mousePressed(MouseEvent event){
                
                selectedShape = shapes.getSelectedItem().toString();
                startp = MouseInfo.getPointerInfo().getLocation();
                stroke = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                isFilled = filled.isSelected();
                Float masterLine = Float.parseFloat(""+lineWidth.getValue());
                int masterDash = (int)dashLength.getValue();
                
                if (gradient.isSelected()){
                    mainColor = new GradientPaint(0, 0, color1.getBackground(), 50, 50, color2.getBackground(), true);
                }
                else {
                    mainColor = color1.getBackground();
                }
                
                if (masterLine < 0){
                    masterLine = 1.0f;
                }
                
                if (masterDash < 0){
                    masterDash = 0;
                }

                if (dashed.isSelected()){
                    
                    stroke = new BasicStroke(masterLine, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[] {masterDash+5} , 0);
                    
                } else {
                    stroke = new BasicStroke(masterLine, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                if ("Rectangle".equals(selectedShape)){
                    MyRectangle rec = new MyRectangle(event.getPoint(),event.getPoint(),mainColor, stroke, isFilled);
                    rec.setStartPoint(event.getPoint());
                    shapesArray.add(rec);
                }
                if ("Oval".equals(selectedShape)){
                    MyOval ov = new MyOval(event.getPoint(),event.getPoint(),mainColor, stroke, isFilled);
                    ov.setStartPoint(event.getPoint());
                    shapesArray.add(ov);
                }
                if ("Line".equals(selectedShape)){
                    MyLine line = new MyLine(event.getPoint(),event.getPoint(),mainColor, stroke);
                    line.setStartPoint(event.getPoint());
                    shapesArray.add(line);
                }

                repaint();
            }
            

            public void mouseReleased(MouseEvent event)
            {
                endp = MouseInfo.getPointerInfo().getLocation();
                endx = endp.x;
                endy = endp.y;
                if ("Rectangle".equals(selectedShape)){
                    MyRectangle rec2 = new MyRectangle(shapesArray.get(shapesArray.size()-1).getStartPoint(),event.getPoint(),mainColor, stroke, isFilled);
                    shapesArray.set(shapesArray.size()-1,rec2);
                    repaint();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    String position = "(" + p.x + ", " + p.y + ")";
                    mousePos.setText(position);
                }                
                
                if ("Oval".equals(selectedShape)){
                    MyOval rec2 = new MyOval(shapesArray.get(shapesArray.size()-1).getStartPoint(),event.getPoint(),mainColor, stroke, isFilled);
                    shapesArray.set(shapesArray.size()-1,rec2);
                    repaint();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    String position = "(" + p.x + ", " + p.y + ")";
                    mousePos.setText(position);
                }
                if ("Line".equals(selectedShape)){
                    MyLine rec2 = new MyLine(shapesArray.get(shapesArray.size()-1).getStartPoint(),event.getPoint(),mainColor, stroke);
                    shapesArray.set(shapesArray.size()-1,rec2);
                    repaint();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    String position = "(" + p.x + ", " + p.y + ")";
                    mousePos.setText(position);
                }
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {    

                endp = MouseInfo.getPointerInfo().getLocation();
                endx = endp.x;
                endy = endp.y;
                if ("Rectangle".equals(selectedShape)){
                    MyRectangle rec2 = new MyRectangle(shapesArray.get(shapesArray.size()-1).getStartPoint(),event.getPoint(),mainColor, stroke, isFilled);
                    shapesArray.set(shapesArray.size()-1,rec2);
                    repaint();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    String position = "(" + p.x + ", " + p.y + ")";
                    mousePos.setText(position);
                }                
                
                if ("Oval".equals(selectedShape)){
                    MyOval rec2 = new MyOval(shapesArray.get(shapesArray.size()-1).getStartPoint(),event.getPoint(),mainColor, stroke, isFilled);
                    shapesArray.set(shapesArray.size()-1,rec2);
                    repaint();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    String position = "(" + p.x + ", " + p.y + ")";
                    mousePos.setText(position);
                }
                if ("Line".equals(selectedShape)){
                    MyLine rec2 = new MyLine(shapesArray.get(shapesArray.size()-1).getStartPoint(),event.getPoint(),mainColor, stroke);
                    shapesArray.set(shapesArray.size()-1,rec2);
                    repaint();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    String position = "(" + p.x + ", " + p.y + ")";
                    mousePos.setText(position);
                }            
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                Point p = MouseInfo.getPointerInfo().getLocation();
                String position = "(" + p.x + ", " + p.y + ")";
                mousePos.setText(position);
            }
        }
    }
}

