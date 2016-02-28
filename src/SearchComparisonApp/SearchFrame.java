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
import SearchAlgorithms.*;
import java.awt.*;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.*;
/**
 *
* @author Chad Oftedahl (chadoftedahl@gmail.com)
 */


public class SearchFrame extends JInternalFrame implements SearchAlgorithmListener, MainFormListener {
    
    // <editor-fold defaultstate="collapsed" desc="Declarations">
    JButton openFileButton;
    JButton startSearchButton;
    JButton pauseSearchButton;
    JButton stopSearchButton;
    JComboBox algorithmChooserComboBox;
    JLabel openListCountLabel;
    JLabel closedListCountLabel;
    JLabel totalListCountLabel;
    JLabel elapsedTimeLabel;
    JLabel solutionCostLabel;
    JPanel openListPanel;
    JPanel closedListPanel;
    JPanel totalListPanel;
    JPanel elapsedTimePanel;
    JPanel solutionCostPanel;
    JPanel topPanel;
    JPanel bottomPanel;
    
    GraphicsPanel _graphicsPanel;
    ImageSearchAlgorithm _searchAlg;
    boolean _frameLoaded;
    private int _searchSpeed;
    
    LinkedList<SearchFrameListener> _listeners;
    
// </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
       /*
     * Constructor
     */
    public SearchFrame (){
        /* Set a flag to that control events are not fired during the initialization
         * of the frame
         */
        _frameLoaded = false;
        
        _listeners = new LinkedList<SearchFrameListener>();
        _searchAlg = null;
        
        initializeComponents();
        
        setupEventListeners();
        
        // Disable the pause and stop buttons as no search is running at the start
        this.pauseSearchButton.setEnabled(false);
        this.stopSearchButton.setEnabled(false);
       
        
        /*
         * TODO: Come up with a better way to populate the search chooser combo box.
         * Idealy this would go through all the classes in the SearchAlgorithms package
         * and call a static toString() method on them all and add these values to the 
         * combo box
         */
        algorithmChooserComboBox.addItem("UCS");
        algorithmChooserComboBox.addItem("GBFS");
        algorithmChooserComboBox.addItem("A*");
        algorithmChooserComboBox.addItem("BFS");
        algorithmChooserComboBox.addItem("DFS");
       // algorithmChooserComboBox.addItem("IDDFS");
        // Set the starting value of the combobox to be empty
        algorithmChooserComboBox.setSelectedIndex(-1);
       
        _frameLoaded = true;  
      
    }
    //</editor-fold>
 
    //<editor-fold defaultstate="collapsed" desc="Initializtion Methods">
    private void initializeComponents(){
        // Set up the top panel and all the controls in it
        topPanel = new JPanel();
        // The width of this control sets the width of everything
        topPanel.setPreferredSize(new Dimension(200,80));
       
        openFileButton = new JButton(Utils.createImageIcon("/images/icons/OpenFile.png"));
        topPanel.add(openFileButton);
        startSearchButton = new JButton(Utils.createImageIcon("/images/icons/PlayHS.png"));
        topPanel.add(startSearchButton);
        pauseSearchButton = new JButton(Utils.createImageIcon("/images/icons/PauseHS.png"));
        topPanel.add(pauseSearchButton);
        stopSearchButton = new JButton(Utils.createImageIcon("/images/icons/StopHS.png"));
        topPanel.add(stopSearchButton);
        algorithmChooserComboBox = new JComboBox();
        topPanel.add(algorithmChooserComboBox);        
        
        // Set up the bottom panel and all the controls in it
        _graphicsPanel = new GraphicsPanel();
        
        // Set up the bottom panel and all the controls in it
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        bottomPanel.setPreferredSize(new Dimension(0,62));
        
        Dimension subPanelMargins = new Dimension(4,2);
        
        openListPanel = new JPanel();
        openListPanel.setLayout(new FlowLayout(FlowLayout.LEFT,
                                               subPanelMargins.width,
                                               subPanelMargins.height));
        JLabel label = new JLabel("Open:");
        label.setFont(new Font(label.getFont().getName(),Font.BOLD, label.getFont().getSize()));
        openListPanel.add(label);
        openListCountLabel = new JLabel("0");
        openListPanel.add(openListCountLabel);
        bottomPanel.add(openListPanel);
        
        closedListPanel = new JPanel();
        closedListPanel.setLayout(new FlowLayout(FlowLayout.LEFT,
                                               subPanelMargins.width,
                                               subPanelMargins.height));
        label = new JLabel("Closed:");
        label.setFont(new Font(label.getFont().getName(),Font.BOLD, label.getFont().getSize()));
        closedListPanel.add(label);
        closedListCountLabel = new JLabel("0");
        closedListPanel.add(closedListCountLabel);
        bottomPanel.add(closedListPanel);
        
        totalListPanel = new JPanel();
        totalListPanel.setLayout(new FlowLayout(FlowLayout.LEFT,
                                               subPanelMargins.width,
                                               subPanelMargins.height));
        label = new JLabel("Total:");
        label.setFont(new Font(label.getFont().getName(),Font.BOLD, label.getFont().getSize()));
        totalListPanel.add(label);
        totalListCountLabel = new JLabel("0");
        totalListPanel.add(totalListCountLabel);
        bottomPanel.add(totalListPanel);
        
        elapsedTimePanel = new JPanel();
        elapsedTimePanel.setLayout(new FlowLayout(FlowLayout.LEFT,
                                               subPanelMargins.width,
                                               subPanelMargins.height));
        label = new JLabel("Time:");
        label.setFont(new Font(label.getFont().getName(),Font.BOLD, label.getFont().getSize()));
        elapsedTimePanel.add(label);
        elapsedTimeLabel = new JLabel("0");
        elapsedTimePanel.add(elapsedTimeLabel);
        bottomPanel.add(elapsedTimePanel);
        
        solutionCostPanel = new JPanel();
        solutionCostPanel.setLayout(new FlowLayout(FlowLayout.LEFT,
                                               subPanelMargins.width,
                                               subPanelMargins.height));
        label = new JLabel("Solution Cost:");
        label.setFont(new Font(label.getFont().getName(),Font.BOLD, label.getFont().getSize()));
        solutionCostPanel.add(label);
        solutionCostLabel = new JLabel("0");
        solutionCostPanel.add(solutionCostLabel);
        bottomPanel.add(solutionCostPanel);
        
        // Add the top level containers to the internal frame
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(_graphicsPanel,BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        
        // Set the minium size of the frame to be the current size
        Dimension minSize = new Dimension();
        minSize.width = this.getInsets().left + this.getInsets().right + topPanel.getPreferredSize().width;
        minSize.height = this.getInsets().top + this.getInsets().bottom + topPanel.getPreferredSize().height + bottomPanel.getPreferredSize().height + 24;
        this.setMinimumSize(minSize);
    }
   
    private void setupEventListeners(){
         openFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                openFileButtonClick(evt);
            }
        });
         
          startSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                startSearchButtonClick(evt);
            }
        });
          
          pauseSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                pauseSearchButtonClick(evt);
            }
        });
          
          stopSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                stopSearchButtonClick(evt);
            }
        });
          
          algorithmChooserComboBox.addItemListener(new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                algorithmChooserComboBoxItemChanged(e);
            }
        });
     
          _graphicsPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                graphicsPanelMouseDown(evt);
            }
            
            @Override
            public void mouseReleased(MouseEvent evt){
                graphicsPanelMouseUp(evt);
            }
            
           
            
        });
              
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Handlers for events generated by this object">
    private void openFileButtonClick(MouseEvent evt){
        
        if (this.openFileButton.isEnabled()) {
            JFileChooser fc = new JFileChooser();
        
            File file = null;
            try {
                // Create get a file object respresenting the current directory
                file = new File(new File(".").getCanonicalPath());

            } catch (IOException e) {
            }

            fc.setCurrentDirectory(file);
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);

            int returnVal = fc.showOpenDialog(this);

            // If the user clicked the OK button
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = fc.getSelectedFile().getAbsolutePath();
                loadImage(filePath);
            }
        } 
    }
    private void startSearchButtonClick(MouseEvent evt){
        if (this.startSearchButton.isEnabled()) {
              fireSearchStartedInFrame();
              startSearch(); 
        }
     
    }
    private void pauseSearchButtonClick(MouseEvent evt){
       if(this.pauseSearchButton.isEnabled()){
           pauseSearch();
       }
       
       
    }
    private void stopSearchButtonClick(MouseEvent evt){
        if (this.stopSearchButton.isEnabled()) {
                   stopSearch(); 
        } 
    } 
    private void algorithmChooserComboBoxItemChanged(ItemEvent evt){
      
        if (_frameLoaded) {
            
            _graphicsPanel.repaint();
        
            if(evt.getItem().toString().equalsIgnoreCase("UCS")){
                _searchAlg = new UniformCostSearch();
            }
            else if(evt.getItem().toString().equalsIgnoreCase("GBFS")){
                _searchAlg = new GreedyBestFirstSearch();
            }
            else if(evt.getItem().toString().equalsIgnoreCase("A*")){
                _searchAlg = new AStarSearch();
            }
            else if(evt.getItem().toString().equalsIgnoreCase("BFS")){
                _searchAlg = new BreadthFirstSearch();
            }
            else if(evt.getItem().toString().equalsIgnoreCase("DFS")){
                _searchAlg = new DepthFirstSearch();
            }
            else if(evt.getItem().toString().equalsIgnoreCase("IDDFS")){
                _searchAlg = new IterativeDeepeningDepthFirstSearch();
            }
            
            _searchAlg.setSearchSpeed(this.getSearchSpeed());
        }  
    }  
    private void graphicsPanelMouseDown(MouseEvent evt){
        _graphicsPanel.setMouseDownTime(new Date());
    }
    private void graphicsPanelMouseUp(MouseEvent evt){
        if (_graphicsPanel.getImage() != null && this.startSearchButton.isEnabled()) {
            
            Date upTime = new Date();
        
            long downMilliSeconds = _graphicsPanel.getMouseDownTime().getTime();
            long upMilliSeconds = upTime.getTime();

            double mousePressTime = ((double)upMilliSeconds - (double)downMilliSeconds) / 1000.0d;

            int stretchFactor = _graphicsPanel.getSize().width / _graphicsPanel.getImageWidth();

            if (mousePressTime < .2d) {
                Point newStartPoint = new Point(evt.getX() / stretchFactor, evt.getY() / stretchFactor);
                this.setStartPoint(newStartPoint);
                this.fireStartPointChange(newStartPoint);
            }
            else{
                Point newEndPoint = new Point(evt.getX() / stretchFactor, evt.getY() / stretchFactor);
                this.setEndPoint(newEndPoint);
                this.fireEndPointChange(newEndPoint);
            }
        }   
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Handlers for events generated by objects being listened to">
    /**
     * When a search algorithm being listened to has completed its search it
     * calls this method with results of the search
     * @param results 
     */
    @Override
    public void SearchResultsReady(SearchResults results){
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(2);
       openListCountLabel.setText(nf.format(results.getOpenListCount()));
       closedListCountLabel.setText(nf.format(results.getClosedListCount()));
       totalListCountLabel.setText(nf.format(results.getTotalListCount()));
       elapsedTimeLabel.setText(nf.format(results.getElapsedTime()) + " sec");   
       solutionCostLabel.setText(nf.format(results.getSolutionCost()));
       
       /*
        * Enable the controls that were disabled
        */
       this.openFileButton.setEnabled(true);
       this.startSearchButton.setEnabled(true);
       this.stopSearchButton.setEnabled(false);
       this.pauseSearchButton.setEnabled(false);
       this.algorithmChooserComboBox.setEnabled(true);
       /*
        * Inform the main form that the search is done
        */
       this.fireSearchComlete();
    }  
    /**
     * When any MainForms being listened to have a file opened in them then this
     * method is called
     * @param filePath The path of the image that was loaded
     */
    @Override
    public void parentOpenFile(String filePath) {
        this.loadImage(filePath);
    }
    /**
     * When any MainForms being listened to have space in which to zoom in all 
     * their frames this is the method that is called
     */
    @Override
    public void parentImageZoomIn() {
        this.zoomIn();
    }
    /**
     * When any MainForms being listened to need to shrink their frames to fit
     * them this is the method that is called
     */
    @Override
    public void parentImageZoomOut() {
        this.zoomOut();
    }
     /**
     * When any MainForms being listened to get input from a user to pause all 
     * of the searches this is the method that is called
     */
    @Override
    public void parentStartSearch() {
       this.startSearchButtonClick(null);
    }
     /**
     * When any MainForms being listened to get input from a user to pause all 
     * of the searches this is the method that is called
     */
    @Override
    public void parentPauseSearch() {
       this.pauseSearchButtonClick(null);
    }
     /**
     * When any MainForms being listened to get input from a user to stop all 
     * of the searches this is the method that is called
     */
    @Override
    public void parentStopSearch() {
       this.stopSearchButtonClick(null);
    }
    @Override
    public void searchThrottlingChanged(int nodesPerSecond){
        this.setSearchSpeed(nodesPerSecond);
        
        if (_searchAlg != null) {
            _searchAlg.setSearchSpeed(nodesPerSecond);
        }
    }
    

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Listener notificaion methods">
    private void fireStartPointChange(Point newStartPoint){
        if (this.hasListener()) {
            for(SearchFrameListener listener : _listeners){
                listener.newStartPointSet(newStartPoint);
            }
        }
    
    }
    
    private void fireEndPointChange(Point newEndPoint){
        if (this.hasListener()) {
            for(SearchFrameListener listener : _listeners){
                listener.newEndPointSet(newEndPoint);
            }
        }
    
    }
    
    private void fireSearchComlete(){
       if (this.hasListener()) {
            for(SearchFrameListener listener : _listeners){
                listener.searchComplete();
            }
        }
    
    }
    
    private void fireSearchStartedInFrame(){
        if (this.hasListener()) {
            for(SearchFrameListener listener : _listeners){
                listener.searchStartedInFrame();
            }
        }
    }
    
    
    //</editor-fold>
    
    private void zoomIn(){
        if (_graphicsPanel.getImage() != null){
            // Get the current scaling factor
            int scalingFactor = (this.getSize().width - getNonGraphicsWidth()) / 
                                _graphicsPanel.getImageWidth();

            scalingFactor += 1;

            this.setPreferredSize(new Dimension((_graphicsPanel.getImageWidth() * scalingFactor) + getNonGraphicsWidth(),(_graphicsPanel.getImageHeight() * scalingFactor) + getNonGraphicsHeight()));

            /*
            * Scale up the font of the search result labels to match the 
            * relative size of the frame
            */
            for (Component component : bottomPanel.getComponents()) {
                for (Component subComponent : ((JPanel)component).getComponents()) {
                    int newSize = subComponent.getFont().getSize() + 2;
                    int style = subComponent.getFont().getStyle();
                    String fontName = subComponent.getFont().getFontName();

                    subComponent.setFont(new Font(fontName, style, newSize));
                }
            }

            this.pack();
        } 
    }

    private void zoomOut(){
            if (_graphicsPanel.getImage() != null){
                // Get the current scaling factor
            int scalingFactor = (this.getSize().width - getNonGraphicsWidth()) / 
                                _graphicsPanel.getImageWidth();

            if (scalingFactor > 1) {
                scalingFactor -= 1;

                this.setPreferredSize(new Dimension((_graphicsPanel.getImageWidth() * scalingFactor) + getNonGraphicsWidth(),(_graphicsPanel.getImageHeight() * scalingFactor) + getNonGraphicsHeight()));

                /*
                    * Scale down the font of the search result labels to match the 
                    * relative size of the frame
                    */
                for (Component component : bottomPanel.getComponents()) {
                    for (Component subComponent : ((JPanel)component).getComponents()) {
                        int newSize = subComponent.getFont().getSize() - 2;
                        int style = subComponent.getFont().getStyle();
                        String fontName = subComponent.getFont().getFontName();

                        subComponent.setFont(new Font(fontName, style, newSize));
                    }
                }

                this.pack();
            }
        }
    }
     
    /**
     * Runs the currently selected search algorithm on the currently loaded map
     */
    public void startSearch(){
        // If a search algorithm has been chosen and an image has been loaded
        if (this.isReadyForSearch()) {

            this.openFileButton.setEnabled(false);
            this.startSearchButton.setEnabled(false);
            this.pauseSearchButton.setEnabled(true);
            this.stopSearchButton.setEnabled(true);
            this.algorithmChooserComboBox.setEnabled(false);
            
            if(!_searchAlg.isPaused()){
                /*
                * Trigger an immediate re-painting of the graphics panel in order to
                * erase previous search graphics from the base image.  If repaint() is 
                * called instead the system will combine that with all of the following 
                * algorithm paint requests and end up erasing the search results as soon 
                * as the search is finished.
                */
                _graphicsPanel.paintImmediately(0,0,_graphicsPanel.getWidth(),_graphicsPanel.getHeight());
                
                /*
                 * Set the variables the search algorithm needs to work every time
                 * the algorithm is started.  This way we are covered in case the
                 * user changed the image or start / end points between searches
                 */
                initializeSearchAlgorithm();
                
                resetSearchResults();
            }
          
            Thread searchThread = new Thread(){
                @Override
                public void run(){
                    _searchAlg.runSearch(); 
                }
            };
            
            searchThread.setName("SearchThread");
            searchThread.start();
            
        }
    }
    
     /**
     * Sets a flag in the search algorithm that causes it to pause.  If the 
     * search is started after it was paused then the search algorithm picks up
     * right where it left off
     */
    public void pauseSearch(){
        
        
        if (_searchAlg != null) {
            
           _searchAlg.setPauseFlag(true);  
        }
    }
    
    /**
     * Sets a flag in the search algorithm that causes it to stop.  If the search
     * started after it was stopped then the algorithm starts a new search.
     */
    public void stopSearch(){
        if (_searchAlg != null) {
            _searchAlg.setStopFlag(true);
        }  
    }
    
    /**
     * Loads an image into the GraphicPanel
     * @param filePath The complete path of the image that is to be loaded
     */
    public void loadImage(String filePath){
        _graphicsPanel.loadImage(filePath);
        
        // Check the graphics panel aspect ratios with the image's to see if 
        // the image is sized correctly
        double imageAspectRatio = (double)_graphicsPanel.getImageHeight() / (double)_graphicsPanel.getImageWidth();
        double panelAspectRatio = (double)_graphicsPanel.getSize().height / (double)_graphicsPanel.getSize().width;
        
        if (imageAspectRatio != panelAspectRatio) {
           
            int scalingFactor = 1;
            
            while((_graphicsPanel.getImageWidth() * scalingFactor) < _graphicsPanel.getMinimumSize().width){
                scalingFactor ++;
            }

            this.setPreferredSize(new Dimension((_graphicsPanel.getImageWidth() * scalingFactor) + getNonGraphicsWidth(),(_graphicsPanel.getImageHeight() * scalingFactor) + getNonGraphicsHeight() + 24));  
        }   
        
         /*
         * Make the frame resize the graphics panel to conform with the layout
         * of the frame
         */
        this.pack();
    
    }
    
    /**
     * Adds an object that is implementing the SearchFrameListener interface to a 
     * list of SearchFrameListeners that will be used to send events to
     * @param listener 
     */
    public void addListener(SearchFrameListener listener){
        if (_listeners == null){
            _listeners = new LinkedList<SearchFrameListener>();
        }
        _listeners.add(listener);
    }
    
     /**
     * Removes an object that is implementing the SearchFrameListener interface 
     * from the list of SearchFrameListeners
     */
    public void removeListener(SearchFrameListener listener){
        if (this.hasListener()){
            _listeners.remove(listener);
        }
    }
    
    
    /**
     * Returns true if any SearchFrameListener objects have registered with this object
     * and false if there are no listeners
     */
    public boolean hasListener(){
        if (_listeners != null && !_listeners.isEmpty()) {
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * Get the search algorithm into a state where it is ready to be started. 
     */
    public void initializeSearchAlgorithm(){
        _searchAlg.setGraphicsPanel(_graphicsPanel);
        
        if (!_searchAlg.hasListener()) {
            _searchAlg.addListener(this);
        }
    }
    
    /**
     * Resets the JLabels that display the search results
     */
    private void resetSearchResults(){
       openListCountLabel.setText("0");
       closedListCountLabel.setText("0");
       totalListCountLabel.setText("0");
       elapsedTimeLabel.setText("0 sec");
       solutionCostLabel.setText("0");  
    }
    
    /**
     * Returns true if there is a search running within the frame, false if there
     * is not
     */
    public boolean isSearchRunning(){
        if (_searchAlg == null) {
            return false;
        }
        else if (_searchAlg.isComplete() || _searchAlg.isPaused() || _searchAlg.isStopped()) {
            return false;
        }
        else{
            return true;
        }
    
    }
    
    /** 
     * Returns true if the frame has a loaded image and a selected search
     * algorithms and false if it does not
     */
    public boolean isReadyForSearch(){
        if (_graphicsPanel.getImage() != null && _searchAlg != null) {
            return true;
        }
        
        return false;
    }
   
    //<editor-fold defaultstate="collapsed" desc="Getters / Setters">
     /**
     * Returns the height of everything in the frame that is NOT the image
     * @return 
     */
    private int getNonGraphicsHeight(){
        /*
         * TODO: Figure out how to derive this extra 24 pixel size from the 
         * control itself instead of using a hard coded value
         */
        return this.getInsets().top + topPanel.getHeight() + bottomPanel.getHeight()
               + this.getInsets().bottom + 24;
    }
     /**
     * Returns the width of everything in the frame that is NOT the image
     * @return 
     */
    private int getNonGraphicsWidth(){
        return this.getInsets().left + this.getInsets().right;
    }
    
    /**
     * Returns the width of the original image before any scaling was done
     * @return 
     */
    public int getBaseImageWidth(){
        return _graphicsPanel.getImageWidth();
    }
     /**
     * Returns the height of the original image before any scaling was done
     * @return 
     */
    public int getBaseImageHeight(){
        return _graphicsPanel.getImageHeight();
    }
    /**
     * Returns the width of the image as currently displayed.  This will reflect
     * any scaling that was done to blow the image up from its original size.
     * @return 
     */
    public int getCurrentImageWidth(){
       return this.getWidth() - this.getNonGraphicsWidth();
    }
     /**
     * Returns the height of the image as currently displayed.  This will reflect
     * any scaling that was done to blow the image up from its original size.
     * @return 
     */
    public int getCurrentImageHeight(){
        return this.getHeight() - this.getNonGraphicsHeight();
    }
    
    /**
     * Returns a reference to the image that is being displayed in the frame.
     * @return 
     */
    public BufferedImage getImage(){
        if (this.hasImage()) {
            return _graphicsPanel.getImage();
        }
        else{
            return null;
        }
    }
     /**
     * Returns a reference to a copy of the image that is being displayed in the
     * frame.  This will have any starting / ending pixels removed.
     * @return 
     */
    public BufferedImage getImageClone(){
        if (this.hasImage()) {
            return _graphicsPanel.getImageClone();
        }
        else{
            return null;
        }
    }
    
    /**
     * Sets the image used by the search frame
     * @param image 
     */
    public void setImage(BufferedImage image){
       _graphicsPanel.setImage(image);
    }
  
    /**
     * Returns a reference to the search algorithm currently assigned to the 
     * search frame.  
     * @return 
     */
    public ImageSearchAlgorithm getSearchAlg() {
        return _searchAlg;
    }

    /**
     * Returns true if there is currently an image loaded into the search frame
     * and false is there is not
     * @return 
     */
    public boolean hasImage(){
        if (_graphicsPanel.getImage() == null) {
            return false;
        }
        else{
            return true;
        }
    }
    
    /**
     * Sets the coordinates of the image that will be used as the starting 
     * point for the image search algorithm
     * @param 
     */
    public void setStartPoint(Point newStartPoint){
        _graphicsPanel.setStartPoint(newStartPoint); 
    }
    
    /**
     * Returns the coordinates of the image that will be used as the starting 
     * point for the image search algorithm
     * @return 
     */
    public Point getStartPoint(){
        return _graphicsPanel.getStartPoint();
    }
    
    /**
     * Sets the coordinates of the image that will be used as the ending 
     * point for the image search algorithm
     * @param 
     */
    public void setEndPoint(Point newEndPoint){
        _graphicsPanel.setEndPoint(newEndPoint);
    }
    

    /**
     * Gets the coordinates of the image that will be used as the ending 
     * point for the image search algorithm
     * @return 
     */
    public Point getEndPoint(){
       return _graphicsPanel.getEndPoint();
    }
    //</editor-fold>

    /**
     * Returns the search speed in nodes per second
     */
    public int getSearchSpeed() {
        return _searchSpeed;
    }

    /**
     * Sets the number of nodes per second that the search will evaluate
     */
    public void setSearchSpeed(int searchSpeed) {
        this._searchSpeed = searchSpeed;
    }

}
