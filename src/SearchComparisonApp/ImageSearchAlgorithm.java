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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.*;
/**
 *
* @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public abstract class ImageSearchAlgorithm {
  
  // <editor-fold defaultstate="collapsed" desc="Declarations">
  // Declarations
    private PriorityQueue<SearchNode> _openListPriorityQueue;
    private Deque<SearchNode> _openListStack;
    private Queue<SearchNode> _openListQueue;
    private openListType _openListType;
    
    private HashMap<SearchNode, SearchNode> _closedList;
    
    private int[][] _searchState;
    
    private SearchNode _startNode;
    private SearchNode _endNode;
    
    private GraphicsPanel _graphicsPanel;
    
    private boolean _complete;
    private boolean _goalFound;
    private boolean _stopFlag;
    private boolean _pauseFlag;
    
    private long _elapsedSearchNanoSecs;
    
    private int _searchSpeed;
    
    private LinkedList<SearchAlgorithmListener> _listeners;

    
    /**
     * 
     */
    public enum openListType {

        /**
         * 
         */
        stack,
        /**
         *
         */
        queue,
        /**
         *
         */
        priorityQueue
    };
    
// </editor-fold>
    
  // <editor-fold defaultstate="collapsed" desc="Constructors">
  // Constructors
    /**
     * 
     */
    public ImageSearchAlgorithm(){
        _openListPriorityQueue = new PriorityQueue<SearchNode>();
        _openListStack = new LinkedList<SearchNode>();
        _openListQueue = new LinkedList<SearchNode>();
        _closedList = new HashMap<SearchNode, SearchNode>();
        
        _startNode = null;
        _endNode = null;
        
        _graphicsPanel = null;
        _searchState = null;
        _complete = true;
        _goalFound = false;
        
        _elapsedSearchNanoSecs = 0;
        
        _openListType = getOpenListType();
    }
       
  
// </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Image / Graphics functions">
  // Image / Graphics functions
    
    /**
     * Populates an internal array with the terrain costs of each pixel of the
     * provided image.  
     */
    private void fillSearchState(BufferedImage image){
        _searchState = new int[image.getWidth()][image.getHeight()];
        
        int ARGBval = 0;
        int rVal = 0;
        int gVal = 0;
        int bVal = 0;
        
        int startPointX = _graphicsPanel.getStartPoint().x;
        int startPointY = _graphicsPanel.getStartPoint().y;
        int endPointX = _graphicsPanel.getEndPoint().x;
        int endPointY = _graphicsPanel.getEndPoint().y;
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                
                /*
                 * If the pixel we are examining is the starting point or ending
                 * point then we want to get the value of the image pixel at that
                 * point and not the value of whatever color the starting / ending
                 * pixel is.
                 */
                if (x == startPointX && y == startPointY) {
                  ARGBval = _graphicsPanel.getStartPointImageRGB();
                }
                else if(x == endPointX && y == endPointY){
                  ARGBval = _graphicsPanel.getEndPointImageRGB();
                }
                else{
                  ARGBval = image.getRGB(x, y);  
                }
               
                rVal = getRValue(ARGBval);
                gVal = getGValue(ARGBval);
                bVal = getBValue(ARGBval);
                
                /*
                 * Check to see if the pixel is on the gray scale.  Add one to
                 * whatever value was found becuse there always has to be a some
                 * cost to movement even if the "terrain" has no added cost.
                 * (e.g. the white pixels of the image need a cost of one instead
                 * of having no cost)
                 */
                if (rVal == gVal && rVal == bVal && gVal == bVal) {
                    switch (rVal) {
                        case 255:
                            _searchState[x][y] = 1;
                            break;
                        case 204:
                            _searchState[x][y] = 2;
                            break;
                        case 153:
                            _searchState[x][y] = 3;
                            break;
                        case 102:
                            _searchState[x][y] = 4;
                            break;
                        default: 
                            /*
                             * If the pixels gray scale value is not recogized then
                             * assume it is a wall
                             */
                            _searchState[x][y] = - 1;
                    }
                }
                else{
                    /*
                     * The pixel is not a gray scale value so assume it is a wall
                     */
                    _searchState[x][y] = - 1;
                    // TODO: Add logic to convert color pixels in to grayscale
                }  
            }
        }
        
        
    }
    
    /**
     * Returns the R portion of an RGB integer
     * @param ARGBval
     * @return 
     */
    private int getRValue(int ARGBval){
        return (ARGBval >> 16) & 0xFF;
    }
    /**
     * Returns the G portion of an RGB integer
     * @param ARGBval
     * @return 
     */
    private int getGValue(int ARGBval){
        return (ARGBval >> 8) & 0xFF;
    }
     /**
     * Returns the B portion of an RGB integer
     * @param ARGBval
     * @return 
     */
    private int getBValue(int ARGBval){
        return ARGBval & 0xFF;
    }
    
    /**
     * Paints a point of the image to depict that the corresponding node is in 
     * the closed list
     * 
     * @param point The coordinate point of the image that is to be colored
     */
    
    protected void paintClosedListPoint(Point point){
      _graphicsPanel.paintClosedListPoint(point);
      
    }
    
    /**
     * Paints a point of the image to depict that the corresponding node is in
     * the open list
     * 
     * @param point The coordinate point of the image that is to be colored
     */
    protected void paintOpenListPoint(Point point){
      _graphicsPanel.paintOpenListPoint(point);
     
    }
    
    /**
     * Paints the path from the start node to the current node
     * 
     * @param node The current node being examined
     */
    protected void paintCurrentPath(SearchNode node){
      _graphicsPanel.paintSolutionPath(node);
    }
    
    /**
     * Clears the image of all the painting that has been done by the search
     * algorithm.  This would be useful for algorithms that start over and 
     * search the same nodes more than once (iterative deepening algorithms).
     */
    protected void clearPainting(){
        _graphicsPanel.paintImmediately(0, 0, _graphicsPanel.getWidth(), _graphicsPanel.getHeight());
    }


// </editor-fold>
  
  //<editor-fold defaultstate="collapsed" desc="Listener notification methods">
     /**
     * Notifies any listening SearchFrames that the search algorithm has some 
     * results that should be shown on the SearchFrame
     */
    protected void fireResultsReady(){
        if(hasListener()){
            for(final SearchAlgorithmListener listener: _listeners){
                
                Runnable x = new Runnable(){
                    @Override
                    public void run(){
                        listener.SearchResultsReady(getSearchResults());
                    }
                };
                
                javax.swing.SwingUtilities.invokeLater(x);
                
            }
        
        }
            
    }
    //</editor-fold>


    /**
     * Adds a SearchFrame that is implementing the SearchAlgorithmListener interface
     * to a list of SearchAlgorithmListeners that will be used to send events to
     * @param listener
     */
    public void addListener(SearchAlgorithmListener listener){
        if (_listeners == null){
            _listeners = new LinkedList<SearchAlgorithmListener>();
        }
        _listeners.add(listener);
    }
    /**
     * Returns true if any  SearchFrame objects have registered with this object
     * @return
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
     * Tests to see if a node is in the closed list
     * 
     * @param node 
     * @return true if the node is in the closed list, false if it is not
     */
    protected boolean inClosedList(SearchNode node){
       return  _closedList.containsKey(node);
    }
    
    /**
     * Adds a node to a different open list depending on the open list type 
     * specified by the algorithm.  If using a priority queue and a node for this
     * map location is already in the open list, and its total cost is less than 
     * the total cost of the supplied node, then the node in the open list is 
     * removed and the supplied node is added.
     * 
     * @param node
     * @return True of if either the node was added to the open list or if it
     * was already in the open list and it's total cost value was decreased (for
     * a priority queue)
     */
    protected boolean addToOpenList(SearchNode node){
        
        switch(_openListType){
            
            case stack:
                
                /*
                * Search through the open list to see if this node is already in it so
                * we don't have duplicates in the open list.
                */
                for(SearchNode openListNode: _openListStack){
                    if (openListNode.equals(node)) {
                        return false;
                    }
                 }
                
                _openListStack.push(node);
                _graphicsPanel.paintOpenListPoint(node.getMapLoc());
                return true;
                
            case queue:
                
                /*
                * Search through the open list to see if this node is already in it so
                * we don't have duplicates in the open list.
                */
                for(SearchNode openListNode: _openListQueue){
                    if (openListNode.equals(node)) {
                        return false;
                    }
                 }
                
                _openListQueue.offer(node);
                _graphicsPanel.paintOpenListPoint(node.getMapLoc());
                return true;
                
            case priorityQueue:
                

                boolean nodeUpdated = false;
                boolean nodeInOpenList = false;

                /*
                * Search through the open list to see if this node is already in it so
                * we don't have duplicates in the open list.
                */
                for(SearchNode openListNode: _openListPriorityQueue){
                    if (openListNode.equals(node)) {

                        /*
                        * If the node in the open list has a higher total cost than
                        * the node we are trying to add then replace the node in 
                        * the list with the lower cost node.
                        */
                        if (node.getFCost() < openListNode.getFCost()) {
                            _openListPriorityQueue.remove(openListNode);
                            _openListPriorityQueue.add(node);
                            nodeUpdated = true;
                        }

                        nodeInOpenList= true;
                        break;
                    }
                }

                // If the node was not already the open list, add it
                if (!nodeInOpenList) {
                    _openListPriorityQueue.add(node); 
                    _graphicsPanel.paintOpenListPoint(node.getMapLoc());
                }

                /*
                * If some changes were made to the priority queue (a node was added or 
                * updated) then return true so we can update optimal paths and also
                * do any repainting of the graphics panel
                */
                if (nodeUpdated || !nodeInOpenList)
                {
                    return true;
                }
                else{
                    return false;
                }

        }
        
        return false;
           
    }
    
    
    /**
     * Adds a node to the closet list
     * @param node 
     */
    protected void addToClosedList(SearchNode node){
        
        _closedList.put(node, node);
        _graphicsPanel.paintClosedListPoint(node.getMapLoc());
        
    }
     
    /**
     * Clears all the items out of the open list and adds the starting node
     */
    protected void resetOpenList(){
        switch(_openListType){
            
            case stack:
                
                _openListStack.clear();
                _openListStack.push(getStartNode());
                break;
                
            case queue:
                
                _openListQueue.clear();
                _openListQueue.offer(getStartNode());
                break;
                
            case priorityQueue:
                
                _openListPriorityQueue.clear();
                _openListPriorityQueue.add(getStartNode());
                break;
        }
    }
    
    /**
     * Clears the closed list
     */
    protected void resetClosedList(){
         _closedList.clear();
    }
    
    /**
     * Clear the open list, closed list, add the starting node to the open list,
     * and reset all the flags to a state such that the search algorithm can be
     * run again
     */
    private void resetSearch(){
        resetClosedList();
        
        // Set the start  and end nodes based on what the user picked in the graphics panel
        Point startPoint = _graphicsPanel.getStartPoint();
        setStartNode(new SearchNode(startPoint, _searchState[startPoint.x][startPoint.y]));
        Point endPoint = _graphicsPanel.getEndPoint();
        setEndNode(new SearchNode(endPoint, _searchState[endPoint.x][endPoint.y]));
        
        resetOpenList();
        
        _elapsedSearchNanoSecs = 0;
        
        setComplete(false);
        setGoalFound(false);
        setPauseFlag(false);
        setStopFlag(false);
        
    }
    
    /**
     * Runs one step of the search which is comprised of the following individual
     * steps<p>
     * 
     * 1) Get the first node off the open list<br>
     * 2) Check to see if this node is the goal node<br>
     * 3) Get all of the nodes adjacent to the node we are expanding<br>
     * 4) Check to see if each adjacent node is in the closed list<br> 
     * 5) Set the costs of all of the adjacent nodes<br>
     * 6) 
     * 
     */
    public void runSearchStep(){
        SearchNode curNode = getNextNode();
        
        if (curNode == null){
             setComplete(true);
        }
        else if (curNode.equals(getEndNode())) {
            setComplete(true);
            setGoalFound(true);
            
             /*
             * Store a reference to the actual end node with all the data in it
             * in the super classes variable
             */
            setEndNode(curNode);
            
        }
        else{
            
            /*
             * Iterate through all of the nodes adjacent to the one we are
             * looking at
             */
            for(SearchNode adjNode: getAdjacentNodes(curNode)){

                /*
                 * If the adjacent node we are looking at is in the closed list
                 * then continue on
                 */
                if (inClosedList(adjNode)) {
                    continue;
                }
                
                /*
                 * Set the nodes cost values and add it to the open list
                 */
                adjNode.setHCost(calculateHCost(adjNode));
                adjNode.setGCost(calculateGCost(curNode, adjNode));
                adjNode.setFCost(calculateFCost(adjNode));
                
                /*
                 * If this node is in the open list then update the nodes cost
                 * in the open list if need be and continue on to the next node
                 */
                if(addToOpenList(adjNode)){
                    /*
                    * If the node was added to the open list or it was already 
                    * there and it's fcost was updated, set it's parent node so
                    * we can find the solution path
                    */
                    adjNode.setParent(curNode); 
                }

            }
            
            /*
             * Add the current node to the closed list
             */
            addToClosedList(curNode);
            
        }
    }

    
    /**
     * Carries out all the steps needed to run an entire search.  
     */
    public void runSearch(){
        
         /*
         * Update the graphics contexts of the graphics panel in case the image
         * has been resized since the last time the algorithm was run.
         */
        _graphicsPanel.updateGraphicsContexts();
        /*
         * If the search is not resuming from a paused state then reset the 
         * all the search related variables and start from scratch
         */
        if (!isPaused()) {
            resetSearch();
        }
        else{
            // Rest the pause flag so the search can be continued
            setPauseFlag(false);
        }
        
        /*
         * Figure out how many nanoseconds each search step should take so we
         * know how long to wait (if at all) in order to run the search at the
         * users desired speed.
         */
        double secsPerStep = 1 / (double) _searchSpeed;
        int nanoSecsPerStep = (int)(secsPerStep * 1000000000);
  
        long searchStartNanoTime = System.nanoTime();
        /*
         * Run the algorithm until the search has finished or until someone
         * stops or pauses it.
         */
        while(!isComplete() && !isPaused() && !isStopped()){
           
            // Run one search step and time how long it takes
            long stepStartNanoTime = System.nanoTime();
            runSearchStep();
            int stepElapsedNanoSecs = (int)(System.nanoTime() - stepStartNanoTime);
            /*
             * Figure out how long we need to wait in order to throttle the search
             * to run at the speed specified by the user
             */
            int nanoSecsToWait = nanoSecsPerStep - stepElapsedNanoSecs;
            /*
             * Spin wait until the desired amount of time has elapsed.  This works
             * a lot better than putting the thread to sleep because the Thread.Sleep() 
             * method tends to be 1-2 millliseconds off the time you send in.
             */
            long nanoSecondsWaited = 0;
            long waitStartNanoTime = System.nanoTime();
            while(nanoSecondsWaited < nanoSecsToWait)
            {
                nanoSecondsWaited = (System.nanoTime() - waitStartNanoTime);
            }
        }
        
        _elapsedSearchNanoSecs += System.nanoTime()- searchStartNanoTime;
     
        _graphicsPanel.paintSolutionPath(getEndNode());
        
        fireResultsReady();
    }
    
   
    /**
     * Calculates the cumulative actual path cost of a node.  Takes the current 
     * nodes actual path cost and adds it to the terrain cost of the new node to 
     * get the total path cost of the new node.  Since by default the distance
     * between non-diagonal nodes is considered 1, the terrain cost of a diagonal
     * node is multiplied by 1.412 (square root of 2) since we have to travel
     * farther to get this node.
     * 
     * Since the operation of this method will be the same for all searches it
     * can not be overridden in sub classes
     */
    final public double calculateGCost(SearchNode currentNode, SearchNode newNode){
        
        // If the new node is diagnoal from the current node
        if ((currentNode.getMapLoc().x != newNode.getMapLoc().x) && (
             currentNode.getMapLoc().y != newNode.getMapLoc().y)) {
            
            return currentNode.getGCost() + newNode.getTerrainCost() * 1.412;
        }
        else{
            return currentNode.getGCost() + newNode.getTerrainCost();
        }
    }
    
    /**
     * Estimates the cost of the solution path from the current node to the goal
     * node.  
     * @param node
     * @return
     */
    public abstract double calculateHCost(SearchNode node);
    
    /**
     * Calculates the total overall cost that will be assigned to the node
     * @param newNode
     * @return
     */
    public abstract double calculateFCost(SearchNode newNode);
    
    /**
     * Used by the search algorithm to determine which type of open list to use.
     * @return 
     */
    public abstract openListType getOpenListType();

    /**
     * Takes a node and returns a list of all bordering nodes except those that 
     * correspond to a black pixel on the image being searched.
     * 
     * @param node
     * @return A list of the nodes that are bordering the supplied node
     */
    protected LinkedList<SearchNode> getAdjacentNodes(SearchNode node){
        LinkedList<SearchNode> adjNodeList  = new LinkedList<SearchNode>();
        
        int thisNodeXLoc = node.getMapLoc().x;
        int thisNodeYLoc = node.getMapLoc().y;
        int imageHeight = _graphicsPanel.getImageHeight();
        int imageWidth = _graphicsPanel.getImageWidth();
        
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;
        
        // Set the X bounds
        if (thisNodeXLoc == 0) {
            xStart = 0;
            xEnd = 1;
        }
        else if (thisNodeXLoc == imageWidth - 1){
            xStart = thisNodeXLoc - 1;
            xEnd = thisNodeXLoc;
        }
        else{
            xStart = thisNodeXLoc - 1;
            xEnd = thisNodeXLoc + 1;
        }
        
        // Set the Y bounds
        if (thisNodeYLoc == 0) {
            yStart = 0;
            yEnd = 1;
        }
        else if(thisNodeYLoc == imageHeight - 1){
            yStart = thisNodeYLoc - 1;
            yEnd = thisNodeYLoc;
        }
        else{
            yStart = thisNodeYLoc - 1;
            yEnd = thisNodeYLoc + 1;
        }
        
        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y <= yEnd; y++) {
               
                /*
                * Skip the current node if it is the one we are searching around 
                * or if it is a wall
                */
                if ((x == thisNodeXLoc && y == thisNodeYLoc) || _searchState[x][y] == -1) {
                    continue;
                }
                else{
                    adjNodeList.add(new SearchNode(new Point(x,y), _searchState[x][y]));
                } 
              
            }
        }
        
        return adjNodeList;
        
    }
    
    /**
     * Returns a LinkedList of SearchNodes that lie on the path from the 
     * endNode to the start node.  This can be used for partial solutions 
     * or for the final solution.
     * @param endNode
     * @return A list of nodes that represent the solution path
     */
    protected LinkedList<SearchNode> getSolutionPath(SearchNode endNode){
        Deque<SearchNode> solutionStack = new LinkedList<SearchNode>();
        
        SearchNode node = endNode;
        while(node != null){
            solutionStack.push(node);
            node = node.getParent();
        }
        
        LinkedList<SearchNode> solutionList = new LinkedList<SearchNode>();
        
        while(solutionStack.size() > 0){
            solutionList.add(solutionStack.pop());
        }
        
        return solutionList;  
    }
    
    
 // <editor-fold defaultstate="collapsed" desc="Getters / Setters">
   
    /**
     * Returns the node that represents the starting point of the search
     * @return
     */
    public SearchNode getStartNode(){
        return _startNode;
    }
    
    /**
     * Sets the node that represents the starting point of the search and 
     * initializes the nodes actual cost to 0
     * @param startNode
     */
    public void setStartNode(SearchNode startNode){
        _startNode = startNode;
        _startNode.setGCost(0);
    }
 
    /**
     * Sets the node that represents the starting point of the search and 
     * initializes the nodes actual cost to 0 
     * @param startPoint
     */
    public void setStartNode(Point startPoint){
        _startNode = new SearchNode(startPoint, _searchState[startPoint.x][startPoint.y]);
    }
    
    /**
     * Returns the node that represents the ending point of the search
     * @return
     */
    public SearchNode getEndNode(){
        return _endNode;
    }
    
    /**
     * Sets the node that represents the ending point of the search 
     * @param endNode
     */
    public void setEndNode(SearchNode endNode){
        _endNode = endNode;
    }
    
    /**
     * Sets the node that represents the ending point of the search 
     * @param endPoint
     */
    public void setEndNode(Point endPoint){
        _endNode = new SearchNode(endPoint, 0);
    }
    
    /**
     * Returns true if the search is complete and false if it is not
     * @return
     */
    public boolean isComplete(){
        return _complete;
    }
    
    /**
     * Sets a flag which represents the completed status of the algorithm
     * @param complete
     */
    protected void setComplete(boolean complete){
        _complete = complete;
    }
    
    /**
     * Returns true of the goal node has been found and false it if has not
     * @return
     */
    public boolean goalFound(){
        return _goalFound;
    }
    
    /**
     * Sets a flag which represents whether or not the goal node has been
     * found by the algorithm on its current run
     * @param goalFound
     */
    protected void setGoalFound(boolean goalFound){
        _goalFound = goalFound;
    }
    
    /**
     * Gets the first node from the open list type currently in use
     * @return
     */
    protected SearchNode getNextNode(){
        
        switch(_openListType){
            
            case stack:
                
                if (_openListStack.isEmpty()) {
                    return null;
                }
                else{
                    return _openListStack.pop();
                }
                
            case queue:
                
                if (_openListQueue.isEmpty()) {
                    return null;
                }
                else{
                    return _openListQueue.remove();
                }
                
                
            case priorityQueue:
                
                if (_openListPriorityQueue.isEmpty()) {
                    return null;
                }
                else{
                    return _openListPriorityQueue.remove();
                } 
        }
        
        return null;
    }
    
    /**
     * Sets an internal reference to a GraphicsPanel.  This is needed for the 
     * algorithm to paint its progress to the screen
     * @param gp
     */
    public void setGraphicsPanel(GraphicsPanel gp){
        _graphicsPanel = gp;
        fillSearchState(_graphicsPanel.getImage());
    }
    
    /**
     * Returns the number of nodes currently in the open list
     * @return
     */
    public int getOpenListSize(){
         switch(_openListType){
            
            case stack:
                return this._openListStack.size();
  
            case queue:
                return this._openListQueue.size();
   
            case priorityQueue:
                return this._openListPriorityQueue.size();
        }
        
        return 0;
        
    }
    
    /**
     * Returns the number of nodes currently in the closed list
     * @return
     */
    public int getClosedListSize(){
        return _closedList.size();
    }
    
    /**
     * Returns the total path cost from the start node to the end nodes along
     * the solution path
     * @return
     */
    public double getSolutionCost(){
        return getEndNode().getGCost();       
    }
    
    /**
     * Returns the amount of time (in seconds) that have transpired while the
     * search algorithm was running.
     */
    public double getSearchTime(){
  
        return (double)_elapsedSearchNanoSecs / 1000000000.0;
    }
    
    /**
     * Returns a SearchResults object which holds all of the information needed
     * to update the controls on the SearchFrame
     * @return
     */
    public SearchResults getSearchResults(){
        SearchResults results = new SearchResults();
        
        results.setClosedListCount(getClosedListSize());
        results.setOpenListCount(getOpenListSize());
        results.setSolutionCost(getSolutionCost());
        results.setElapsedTime(getSearchTime());
        
        return results;
    }
    
     /**
     * Returns true of the search algorithm was stopped and false if it was not
     * @return the _stopFlag
     */
    public synchronized boolean isStopped() {
        return _stopFlag;
    }

    /**
     * Set a flag representing whether or not the search algorithm should be 
     * stopped.  
     * @param stopFlag the _stopFlag to set
     */
    public synchronized void setStopFlag(boolean stopFlag) {
        _stopFlag = stopFlag;
    }

    /**
     * Returns true if the search algorithm was paused and false if it was not
     * @return the _pauseFlag
     */
    public synchronized boolean isPaused() {
        return _pauseFlag;
    }

    /**
     * Set a flag representing whether or not the search algorithm should be
     * paused
     * @param pauseFlag the _pauseFlag to set
     */
    public synchronized void setPauseFlag(boolean pauseFlag) {

        _pauseFlag = pauseFlag;
    }

    /**
     * Sets the number of nodes that will be evaluated per second.
     */
    public void setSearchSpeed(int nodesPerSecond) {
        this._searchSpeed = nodesPerSecond;
    }

// </editor-fold>

  
    
    
    
 
   
   
    
   
    
    
    
}
