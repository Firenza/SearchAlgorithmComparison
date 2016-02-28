/*
 * Copyright (c) 2012, Chad Oftedahl. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of Chad Oftedahl may NOT be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package SearchComparisonApp;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.lang.String;
import java.awt.*;
import java.util.Date;

/**
 *
 * @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public class GraphicsPanel extends javax.swing.JPanel{
    // TODO: Finish commenting the methods in this class
    
    
    //<editor-fold defaultstate="collapsed" desc="Declarations">
    private BufferedImage _image;
    private float _nodeDrawTransparency;
    private Color _openListColor;
    private Color _closedListColor;
    private Color _solutionPathColor;
    private Color _startNodeColor;
    private Color _endNodeColor;
    private int _startPointImageRGB;
    private int _endPointImageRGB;
    private Point _startPoint;
    private Point _endPoint;
    private Date _mouseDownTime;
    private Graphics2D _openListG2D;
    private Graphics2D _closedListG2D;
    private Graphics2D _solutionPathG2D;
    //</editor-fold>
  
    //<editor-fold defaultstate="collapsed" desc="Constructors">
        public GraphicsPanel(){
        // Set a default preferred size so the component takes up space in the layout
        super.setPreferredSize(new Dimension(0,200));
  
        _image = null;
        
        _nodeDrawTransparency = .2f;
        _openListColor = new Color(255,0,0);
        _closedListColor = new Color(0,255,0);
        _solutionPathColor = new Color(0,0,255);
        _startNodeColor = new Color(0,255,0);
        _endNodeColor = new Color(255,0,0);
        
        _startPoint = null;
        _endPoint =  null;
        
        _startPointImageRGB = 0;
        _endPointImageRGB = 0;
        
        _openListG2D = null;
        _closedListG2D = null;
        _solutionPathG2D = null;
    }
    //</editor-fold>

    /**
     * Sets the starting / ending points to some default values and repaints
     * the panel so these points are shown on the image
     */
    private void imageChangedInitialization(){
        // Reset the starting and ending points as default values
        _startPoint = new Point(-1,-1);
        _endPoint = new Point(-1,-1);
        /* 
        * Start at the top left corner of the image search outward until we find
        * a non-black pixel to use for the start point
        */
        int i = 0;
        while((_image.getRGB(i,i) & 0xFF) == 0){
            i++;
        }
        
        setStartPoint(new Point (i,i));
        
         /* 
        * Start at the bottom right corner of the image search outward until we find
        * a non-black pixel to use for the end point
        */
        i = _image.getWidth() - 1;
        while((_image.getRGB(i,i) & 0xFF) == 0){
            i--;
        }
   
        setEndPoint(new Point (i,i));
        
        // Redraw this control so the image is shown
        this.repaint();       
    }
    
    /**
     * Loads an image located at the given location in the file system into the
     * graphics panel
     * @param imagePath 
     */
    public void loadImage(String imagePath){
        try {
            _image = ImageIO.read(new File(imagePath));
            _image = toCompatibleImage(_image);

            this.imageChangedInitialization();
            this.updateGraphicsContexts();
        }           
        catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }
    
    /**
     * Creates a new image that is optimized for faster redraw speed based on 
     * the current system graphics specifics
     * 
     * @param image
     * @return 
     */
    private BufferedImage toCompatibleImage(BufferedImage image){
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfx_config.getColorModel()))
                return image;

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                        image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image; 
}

    /**
     * Updates the graphics context private variables used for drawing open list, 
     * closed list, and solution path points.  This will need to be called any 
     * time the image is scaled in the graphics panel.
     */
    public void updateGraphicsContexts(){
         _openListG2D = (Graphics2D) this.getGraphics();
         _openListG2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                          getNodeDrawTransparency()));
         _openListG2D.setPaint(getOpenListColor());
         
         _closedListG2D = (Graphics2D) this.getGraphics();
         _closedListG2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                          getNodeDrawTransparency()));
         _closedListG2D.setPaint(getClosedListColor());
         
         _solutionPathG2D = (Graphics2D) this.getGraphics();
         _solutionPathG2D.setPaint(getSolutionPathColor()); 
    }
    
    /**
     * 
     * @param newStartPoint 
     */
    private void updateImageStartPixel(Point newStartPoint){
          // If a new point is being set
         if (!newStartPoint.equals(_startPoint)) {
             // === Restore the old point ===/
             // If we are setting the start point for the first time then there
             // is nothing to restore
             if (!_startPoint.equals(new Point(-1,-1))){
                 _image.setRGB(_startPoint.x, _startPoint.y, getStartPointImageRGB());
             }
             // === Set the new point ===/
             // Save the RGB value of the image at this point
             _startPointImageRGB = _image.getRGB(newStartPoint.x, newStartPoint.y);
             // Paint the image pixel at the start point to be the specified color
             _image.setRGB(newStartPoint.x, newStartPoint.y, getStartNodeColor().getRGB());
             
             
         }
     }
    
    private void updateImageEndPixel(Point newEndPoint){
             if (!newEndPoint.equals(_endPoint)) {
                 
              // === Restore the old point ===/
             // If we are setting the end point for the first time then there
             // is nothing to restore
             if (!_endPoint.equals(new Point(-1,-1))){
                 _image.setRGB(_endPoint.x, _endPoint.y, getEndPointImageRGB());
             }
             
             // === Set the new point ===/
             // Save the RGB value of the image at this point
             _endPointImageRGB = _image.getRGB(newEndPoint.x, newEndPoint.y);
             // Paint the image pixel at the end point to be the specified color
             _image.setRGB(newEndPoint.x, newEndPoint.y, getEndNodeColor().getRGB());
             
         }
     }
      
     public void paintOpenListPoint(Point point){
         paintPoint(point, _openListG2D);
      }
     
     public void paintClosedListPoint(Point point){         
         paintPoint(point, _closedListG2D);
     }
     
     private void paintPoint(Point point, Graphics2D g2d){
         
         float horizStretch = (this.getWidth() / getImageWidth()) ;
         float vertStretch = (this.getHeight() / getImageHeight());
         
         g2d.fillRect((int)(point.x * horizStretch), (int)(point.y * vertStretch), 
                      (int)horizStretch,(int) vertStretch);
     }
     
     public void paintSolutionPath(SearchNode node){         
         while(node != null){
             Point point = node.getMapLoc();
             
             if (!point.equals(_startPoint) && !point.equals(_endPoint)) {
                 paintPoint(point, _solutionPathG2D);
             }
             
             node = node.getParent();
         } 
     }
     
    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">
    /**
     * @return the _nodeDrawTransparency
     */
    public float getNodeDrawTransparency() {
        return _nodeDrawTransparency;
    }

    /**
     * @param nodeDrawTransparency the _nodeDrawTransparency to set
     */
    public void setNodeDrawTransparency(float nodeDrawTransparency) {
        _nodeDrawTransparency = nodeDrawTransparency;
    }

    /**
     * @return the _openListColor
     */
    public Color getOpenListColor() {
        return _openListColor;
    }

    /**
     * @param openListColor the _openListColor to set
     */
    public void setOpenListColor(Color openListColor) {
        _openListColor = openListColor;
        updateGraphicsContexts();
    }

    /**
     * @return the _closedListColor
     */
    public Color getClosedListColor() {
        return _closedListColor;
    }

    /**
     * @param closedListColor the _closedListColor to set
     */
    public void setClosedListColor(Color closedListColor) {
        _closedListColor = closedListColor;
        updateGraphicsContexts();
    }

    /**
     * @return the _solutionPathColor
     */
    public Color getSolutionPathColor() {
        return _solutionPathColor;
    }

    /**
     * @param solutionPathColor the _solutionPathColor to set
     */
    public void setSolutionPathColor(Color solutionPathColor) {
        _solutionPathColor = solutionPathColor;
        updateGraphicsContexts();
    }

    /**
     * @return the _startNodeColor
     */
    public Color getStartNodeColor() {
        return _startNodeColor;
    }

    /**
     * @param startNodeColor the _startNodeColor to set
     */
    public void setStartNodeColor(Color startNodeColor) {
        this._startNodeColor = startNodeColor;
    }

    /**
     * @return the _endNodeColor
     */
    public Color getEndNodeColor() {
        return _endNodeColor;
    }

    /**
     * @param endNodeColor the _endNodeColor to set
     */
    public void setEndNodeColor(Color endNodeColor) {
        this._endNodeColor = endNodeColor;
    }

    /**
     * @return the _startPoint
     */
    public Point getStartPoint() {
        return _startPoint;
    }

    /**
     * @param startPoint the _startPoint to set
     */
    public void setStartPoint(Point newStartPoint) {
         this.updateImageStartPixel(newStartPoint);
      
         // Update the start point as the new point
         this._startPoint = newStartPoint;
         this.repaint();
         
    }

    /**
     * @return the _endPoint
     */
    public Point getEndPoint() {
        return _endPoint;
    }

    /**
     * @param endPoint the _endPoint to set
     */
    public void setEndPoint(Point newEndPoint) {
             this.updateImageEndPixel(newEndPoint);
         
             // Update the end point as the new point
             this._endPoint = newEndPoint;
             this.repaint();
    }

    public int getImageWidth(){
        if (_image == null) {
            return 0;
        }
        else{
           return _image.getWidth(); 
        }   
    }
    
     public int getImageHeight(){
         if (_image == null) {
             return 0;
         }
         else{
            return _image.getHeight(); 
         }
        
    }
    
     public BufferedImage getImage(){
         return _image;
     }
     
     public BufferedImage getImageClone(){
        // Clone the image
        ColorModel cm = _image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = _image.copyData(null);
        BufferedImage imageClone  = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        // Remove the start / end point pixels and restore the orig values
        imageClone.setRGB(this.getStartPoint().x, this.getStartPoint().y, this.getStartPointImageRGB());
        imageClone.setRGB(this.getEndPoint().x,this.getEndPoint().y,this.getEndPointImageRGB());
         
        return imageClone;
     }
     
     public void setImage(BufferedImage image){
         _image = image;
         
         this.imageChangedInitialization();
     }

    /**
     * @return the _mouseDownTime
     */
    public Date getMouseDownTime() {
        return _mouseDownTime;
    }

    /**
     * @param mouseDownTime the _mouseDownTime to set
     */
    public void setMouseDownTime(Date mouseDownTime) {
        this._mouseDownTime = mouseDownTime;
    }

    /**
     * @return the _startPointImageRGB
     */
    public int getStartPointImageRGB() {
        return _startPointImageRGB;
    }

    /**
     * Returns the value
     * @return the _endPointImageRGB
     */
    public int getEndPointImageRGB() {
        return _endPointImageRGB;
    }
    // </editor-fold>
    
    /**
     * Overrides the base class (JPanel) default paint method to draw a loaded
     * image as the background of the graphics panel.
     * @param g 
     */
    @Override
    public void paint(java.awt.Graphics g){
        if (_image != null) {
               g.drawImage(_image,0,0,this.getSize().width,
                           this.getSize().height,0,0,_image.getWidth(),
                           _image.getHeight(), null);  
        }
    }
}
