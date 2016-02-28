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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.*;

/**
 *
* @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public class MainForm extends JFrame implements SearchFrameListener {
    
    public static void main(String args[]) {
      
         /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

       /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainForm().setVisible(true);
            }
        });    
     
    }
    
    // <editor-fold defaultstate="collapsed" desc="Declarations">
    
    // A list of internal frames used to display search maps
    private LinkedList<SearchFrame> _searchFrames = new LinkedList<SearchFrame>();
    // A list of objects that are listening to any events raised by this class
    private LinkedList<MainFormListener> _listeners;
    
    private int _searchSpeed;
    
    private JPanel topJPanel;
    
    private JButton openFileJButton;
    private JButton startSearchJButton;
    private JButton pauseSearchJButton;
    private JButton stopSearchJButton;
    private JButton addFrameJButton;
    private JButton removeFrameJButton;
    
    private JPanel internalFrameContainerJPanel;
    
    private JMenu settingsJMenu;
    private JCheckBoxMenuItem globalStartEndPointsJCheckBoxMenuItem;
    private JMenuItem searchSpeedJMenuItem;
    private JMenuItem aboutJMenuItem;
    private JMenuItem helpContentsJMenuItem;
    
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
     public MainForm() {
 
        _searchSpeed = 1000;
   
        initializeComponents();
        
        setupEventListeners() ;
        
        /* Disable the pause / stop search buttons because there is no search
         * running when the form is created
         */
        this.pauseSearchJButton.setEnabled(false);
        this.stopSearchJButton.setEnabled(false);
        
        addFrame();
        
      
    }
    
    //</editor-fold>
   
    //<editor-fold defaultstate="collapsed" desc="Initialization Methods">
    private void initializeComponents(){
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setTitle("Search Comparison App");
       
        JMenuBar mainMenu = new JMenuBar();
        
        settingsJMenu = new JMenu("Settings");
        globalStartEndPointsJCheckBoxMenuItem = new JCheckBoxMenuItem("Global Start / End Points", true);
        settingsJMenu.add(globalStartEndPointsJCheckBoxMenuItem);
        mainMenu.add(settingsJMenu);
        searchSpeedJMenuItem = new JMenuItem("Search Speed");
        settingsJMenu.add(searchSpeedJMenuItem);
        mainMenu.add(settingsJMenu);
        
        JMenu helpMenu = new JMenu("Help");
        helpContentsJMenuItem = new JMenuItem("Help Contents");
        helpMenu.add(helpContentsJMenuItem);
        helpMenu.addSeparator();
        aboutJMenuItem = new JMenuItem("About");
        helpMenu.add(aboutJMenuItem);
        mainMenu.add(helpMenu);
        
        setJMenuBar(mainMenu);
    
        topJPanel = new JPanel();
        topJPanel.setPreferredSize(new Dimension(200,80));
        topJPanel.setBackground(new java.awt.Color(255, 153, 51));
        topJPanel.setPreferredSize(new java.awt.Dimension(150, 80));
        
        openFileJButton = new JButton(Utils.createImageIcon("/images/icons/OpenFile.png"));
        topJPanel.add(openFileJButton);
        startSearchJButton = new JButton(Utils.createImageIcon("/images/icons/PlayHS.png"));
        topJPanel.add(startSearchJButton);
        pauseSearchJButton = new JButton(Utils.createImageIcon("/images/icons/PauseHS.png"));
        topJPanel.add(pauseSearchJButton);
        stopSearchJButton = new JButton(Utils.createImageIcon("/images/icons/StopHS.png"));
        topJPanel.add(stopSearchJButton);
        addFrameJButton = new JButton("Add Frame");
        topJPanel.add(addFrameJButton);
        removeFrameJButton = new JButton("Remove Frame");
        topJPanel.add(removeFrameJButton);

        internalFrameContainerJPanel = new JPanel();
        internalFrameContainerJPanel.setBackground(new java.awt.Color(255, 255, 255));
        internalFrameContainerJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        getContentPane().add(topJPanel, BorderLayout.NORTH);
        getContentPane().add(internalFrameContainerJPanel, BorderLayout.CENTER);
        
        pack();
    }
        
    private void setupEventListeners(){
         
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mainFormResized();
            }
        });
        
         openFileJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openFileJButtonMousePressed();
            }
        });

        startSearchJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                startSearchJButtonMousePressed();
            }
        });

        pauseSearchJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pauseSearchJButtonMousePressed();
            }
        });
        
        stopSearchJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                stopSearchJButtonMousePressed();
            }
        });
        
        addFrameJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                addFrameJButtonMousePressed();
            }
        });
        
         removeFrameJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                removeFrameJButtonMousePressed();
            }
        });
         
         
         searchSpeedJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searchSpeedJMenuItemAction();
            }
        });
         
         
        aboutJMenuItem.addActionListener(new java.awt.event.ActionListener() {
          @Override
              public void actionPerformed(ActionEvent ae) {
                aboutJMenuItemAction();
           }
         });
        
        helpContentsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
          @Override
            public void actionPerformed(ActionEvent ae) {
                helpContentsJMenuItemAction();
          }
         });
   
        
    }
     //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Handlers for events generated by this object">
    
    private void mainFormResized() {                                      
        resizeSubFrames();
    }                                    

    private void openFileJButtonMousePressed() {                                             
        
        if (this.openFileJButton.isEnabled()) {
             JFileChooser fc = new JFileChooser();
     
            File file = null;
            try {
                // Create get a file object respresenting the current directory
                file = new File(new File(".").getCanonicalPath());

            } 
            catch (IOException e) {
            }

            fc.setCurrentDirectory(file);
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);

            int returnVal = fc.showOpenDialog(this);

            // If the user clicked the OK button
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = fc.getSelectedFile().getAbsolutePath();

                // Let all the subframes know to load this image
                this.fireOpenFileJButtonMousePress(filePath);

                this.resizeSubFrames();
            }
        }
        
       
    }              
     
    private void startSearchJButtonMousePressed(){
        if (this.startSearchJButton.isEnabled()) {
            
            /*
             * Verify that at least one of the serach frames is ready for a 
             * search to be run so we can safely disable controls
             */
            int readyFrames = 0;
            for (SearchFrame searchFrame : _searchFrames) {
                if (searchFrame.isReadyForSearch()) {
                    readyFrames ++;
                }
            }
            
            if (readyFrames > 0) {
                this.openFileJButton.setEnabled(false);
                this.startSearchJButton.setEnabled(false);
                this.pauseSearchJButton.setEnabled(true);
                this.stopSearchJButton.setEnabled(true);
                this.addFrameJButton.setEnabled(false);
                this.removeFrameJButton.setEnabled(false);

                fireStartSearchJButtonMousePress();
            }     
        }
    }                                     

    private void pauseSearchJButtonMousePressed() {
        if (this.pauseSearchJButton.isEnabled()) {
            firePauseSearchJButtonMousePress();
            
            System.out.println("Exiting main form pause event");
        }

        
    }
    
    private void stopSearchJButtonMousePressed() {
        if (this.stopSearchJButton.isEnabled()) {
            fireStopSearchJButtonMousePress();
            
             System.out.println("Exiting from main form stop event");
        }
        
       
    }
    
    private void addFrameJButtonMousePressed(){
        if (this.addFrameJButton.isEnabled()) {
            addFrame();
        }   
    }
    
    private void removeFrameJButtonMousePressed(){
        if (this.removeFrameJButton.isEnabled()) {
             removeFrame();
        } 
    }
    
    private void searchSpeedJMenuItemAction() {
          SearchSpeedDialog d = new SearchSpeedDialog(this);
    }
  
    private void aboutJMenuItemAction() {
        String dialogString;
        dialogString = "Copyright 2012 Chad Oftedahl\noftesoft@gmail.com"; 
        JOptionPane.showMessageDialog(this, dialogString, "About", 
                                      JOptionPane.PLAIN_MESSAGE, null);
    }
    
    private void helpContentsJMenuItemAction() {
        
        try {
		File pdfFile = new File("SearchComparisonAppHelp.pdf");
                
		if (pdfFile.exists()) {
 
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } 
                    else {
                        String dialogString = "AWT Desktop is not supported\n\n" +
                                                "Update your Java installation or"+
                                                "manually open the help file located"+
                                                "in directory where this application is";

                        JOptionPane.showMessageDialog(this, dialogString, "", 
                                    JOptionPane.ERROR_MESSAGE, null);
                    }

		} 
                else {
                    String dialogString = "Help file not found!\n\n" +
                                        "Reinstall the application";

                        JOptionPane.showMessageDialog(this, dialogString, "", 
                                    JOptionPane.ERROR_MESSAGE, null);
		}
 
	  } catch (Exception ex) {
		ex.printStackTrace();
	  }
    }
    // </editor-fold> 
  
    //<editor-fold defaultstate="collapsed" desc="Handlers for events generated by objects being listened to">
    /**
     * When any Search Frames being listened to set a new start point this method is 
     * called.  The new point is then set in each search frame.
     * @param newStartPoint 
     */
    @Override
    public void newStartPointSet(Point newStartPoint){
        if (this.globalStartEndPointsJCheckBoxMenuItem.isSelected()) {
            for(SearchFrame frame : this._searchFrames){
              frame.setStartPoint(newStartPoint);
            }
        }
    }
     /**
     * When any Search Frames being listened to set a new end point this method is 
     * called.  The new point is then set in each search frame.
     * @param newStartPoint 
     */
    @Override
    public void newEndPointSet(Point newEndPoint){
        if (this.globalStartEndPointsJCheckBoxMenuItem.isSelected()) {
           for(SearchFrame frame : this._searchFrames){
              frame.setEndPoint(newEndPoint);
            } 
        }
    }
     /**
     * When any Search Frames being listened to running a search have that 
     * search finish then this method is called.
     */
    @Override
    public void searchComplete(){
        int activeSearches = 0;
        
        /*
         * Add up the number of searches that are ongoing
         */
        for (SearchFrame searchFrame : _searchFrames) {
            if (searchFrame.isSearchRunning()) {
                activeSearches ++;
            }
        }
        
        if (activeSearches == 0) {
            this.openFileJButton.setEnabled(true);
            this.startSearchJButton.setEnabled(true);
            this.pauseSearchJButton.setEnabled(false);
            this.stopSearchJButton.setEnabled(false);
            this.addFrameJButton.setEnabled(true);
            this.removeFrameJButton.setEnabled(true);
    
        }
    }
    
    @Override
    public void searchStartedInFrame() {
        this.openFileJButton.setEnabled(false);
        this.startSearchJButton.setEnabled(false);
        this.pauseSearchJButton.setEnabled(true);
        this.stopSearchJButton.setEnabled(true);
        this.addFrameJButton.setEnabled(false);
        this.removeFrameJButton.setEnabled(false);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Listener notification methods">
    /**
     * Notifies all SearchFrames that have registered with this object
     * that an image was opened in the main form. 
     * @param filePath 
     */ 
    private void fireOpenFileJButtonMousePress(String filePath){
      if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.parentOpenFile(filePath);
            }
        }
    }
    /**
     * Notifies all SearchFrames that have registered with this object
     * that there is space available for them to zoom in.
     */
    private void fireSpaceAvilableForSubframeZoomIn(){
         if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.parentImageZoomIn();
            }
        }
    }
     /**
     * Notifies all SearchFrames that have registered with this object
     * that they need to zoom out in order to fit in the current window
     */
    private void fireSpaceRequiresSubframeZoomOut(){
         if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.parentImageZoomOut();
            }
        }
    }
     /**
     * Notifies all SearchFrames that have registered with this object
     * that the start search button was pressed in the main window.
     */
    private void fireStartSearchJButtonMousePress(){
         if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.parentStartSearch();
            }
        }
    }
     /**
     * Notifies all SearchFrames that have registered with this object
     * that the pause search button was pressed in the main window.
     */
    private void firePauseSearchJButtonMousePress(){
         if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.parentPauseSearch();
            }
        }
    }
     /**
     * Notifies all SearchFramesthat have registered with this object
     * that the stop search button was pressed in the main window.
     */
    private void fireStopSearchJButtonMousePress(){
         if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.parentStopSearch();
            }
        }
    }
     /**
     * Notifies all SearchFrames that the search throttling parameters have been
     * changed
     */
    public void fireSearchThrottlingChange(){
         if (this.hasListener()) {
            for(MainFormListener listener : _listeners){
                listener.searchThrottlingChanged(this.getSearchSpeed());
            }
        }
    }
    
    
    
    //</editor-fold>
    
    /**
     * Adds a new search frame to the form.  By default the frame will inherit
     * any properties of the last frame to be added.
     */
    private void addFrame(){
        SearchFrame newFrame = new SearchFrame();
        internalFrameContainerJPanel.add(newFrame);
        newFrame.pack();
        newFrame.setVisible(true);
        
        if (_searchFrames.size() > 0) {
            // TODO: Implement clone method for SearchFrames so all this code can be removed
            
            SearchFrame lastFrame = _searchFrames.getLast();
            // Set the new frames image to be the same as the last frames image
            if (lastFrame.hasImage()) {
                newFrame.setImage(lastFrame.getImageClone());
                
                int newFrameWidth = newFrame.getSize().width;
                int oldFrameWidth = lastFrame.getSize().width;
                int imageWidth = lastFrame.getImage().getWidth();
                
                // Increase the size of the image to match the last frame
                while(newFrameWidth < oldFrameWidth){
                    newFrame.parentImageZoomIn();
                    newFrameWidth += imageWidth;
                }
       
                /*
                 * If the global start / end points option is selected then
                 * set the start / end points of the new frame to be the same
                 * as the old frame
                 */
                if (this.globalStartEndPointsJCheckBoxMenuItem.isSelected()) {
                    newFrame.setStartPoint(lastFrame.getStartPoint());
                    newFrame.setEndPoint(lastFrame.getEndPoint());
                }
            }
        }
     
        _searchFrames.add(newFrame);
        
        newFrame.addListener(this);
        this.addListener(newFrame);
        
        newFrame.setSearchSpeed(this.getSearchSpeed());
        
        // Check to see if all of the forms need to be resized to fit in the window
        this.resizeSubFrames();
        
    }
    
    /**
     * Removes the the last frame that was added from the form.
     */
    private void removeFrame(){
        if (!_searchFrames.isEmpty()) {
          // Remove the most recently added frame from the frame list
          SearchFrame lastFrame = _searchFrames.removeLast();
          
          /*
           * Remove references between this object and the search frame
           */
          lastFrame.removeListener(this);
          this.removeListener(lastFrame);
          
          // Schedule the frame for garbage collection
          lastFrame.dispose();  
          
          // Resize the remaining frames if there is enough space available
          this.resizeSubFrames();      
        }   
    }
   
    /**
     * Expands / Contracts the search frames contained in this form so that
     * they take up as much space in their container panel as they can while 
     * satisfying the following constraints <p>
     * 
     * 1) The aspect ratios of their corresponding images must be maintained<br>
     * 2) The scaled images height / width must be a multiple of the original 
     * images height / width.
     */
    private void resizeSubFrames(){
        this.setPreferredSize(this.getSize());

        if (_searchFrames.size() > 0) {
            int frameCount = _searchFrames.size();
            SearchFrame firstFrame = _searchFrames.element();

            int frameContainerWidth = this.internalFrameContainerJPanel.getSize().width;
            int frameContainerHeight = this.internalFrameContainerJPanel.getSize().height;

            int frameWidth = firstFrame.getWidth();
            int frameHeight = firstFrame.getHeight();

            int imageWidth = firstFrame.getBaseImageWidth();
            int imageHeight = firstFrame.getBaseImageHeight();

            int cumulativeFrameWidth = frameWidth * frameCount;

            int widthAvailable = frameContainerWidth - (frameWidth * frameCount);
            int heightAvailable = frameContainerHeight - frameHeight;

            int widthNeededForZoomIn = firstFrame.getBaseImageWidth() * frameCount;
            int heightNeededForZoomIn = firstFrame.getBaseImageHeight();

            // If there are images in the search frames
            if (widthNeededForZoomIn > 0 && heightNeededForZoomIn > 0) {
                // If there is free space available in the main frame
                if (widthAvailable > 0 && heightAvailable > 0) {
                    
                    while(widthAvailable > widthNeededForZoomIn &&
                        heightAvailable > heightNeededForZoomIn){

                        this.fireSpaceAvilableForSubframeZoomIn();

                        widthNeededForZoomIn += imageWidth * frameCount;
                        heightNeededForZoomIn += imageHeight;
                    }
                }
                else{
                    /*
                        * Check to see if the frames are already at their minimum
                        * size and if there are do not zoom out regardless of
                        * whether or not the frames fit in the window
                        */
                    if (((frameWidth - imageWidth) >= firstFrame.getMinimumSize().width) &&
                            ((frameHeight - imageHeight) >= firstFrame.getMinimumSize().height )) {

                            while(cumulativeFrameWidth > frameContainerWidth || 
                            frameHeight > frameContainerHeight){

                            this.fireSpaceRequiresSubframeZoomOut();

                            cumulativeFrameWidth -= widthNeededForZoomIn;
                            frameHeight -= heightNeededForZoomIn;
                        }
                    }
                }
            }
        }

    }
    
    /**
     * Adds an object that is implementing the MainFormListener interface to a 
     * list of MainFormListeners that will be used to send events to
     */
    private void addListener(MainFormListener listener){
        if (_listeners == null){
            _listeners = new LinkedList<MainFormListener>();
        }
        _listeners.add(listener);
        
    }
     /**
     * Removes an object that is implementing the MainFormListener interface from
     * the list of listeners that are registered with this object
     * 
     */
    private void removeListener(MainFormListener listener){
        if (this.hasListener()) {
            _listeners.remove(listener);
        }
    }
    /**
     * Returns true if any MainFormListener objects have registered with this object
     * and false if there are no listeners
     */
    private boolean hasListener(){
        if (_listeners != null && !_listeners.isEmpty()) {
            return true;
        }
        else{
            return false;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Getters / Setters">

    /**
     * Returns the current search speed in nodes per second
     */
    public int getSearchSpeed() {
        return _searchSpeed;
    }

    /**
     * Sets the nodes per second speed of all searches
     */
    public void setSearchSpeed(int searchSpeed) {
       this._searchSpeed = searchSpeed;
    }






   
}
