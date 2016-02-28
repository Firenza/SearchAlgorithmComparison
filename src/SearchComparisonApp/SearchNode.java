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

/**
 *
* @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public class SearchNode implements Comparable<SearchNode>{
    
    // <editor-fold defaultstate="collapsed" desc="Declarations">
    private double _fCost;
    private double _gCost;
    private double _hCost;
    private Point _mapLoc;
    private int _terrainCost;
    private SearchNode _parent;
    private int _treeDepth;
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    
    /**
     * 
     */
    public SearchNode(){
        _fCost = 0;
        _gCost = 0;
        _hCost = 0;
        _mapLoc = null;
        _terrainCost = 0;
        _parent = null;
        _treeDepth = 0;
    }
    
    /**
     * 
     * @param mapLoc
     * @param terrainCost
     */
    public SearchNode(Point mapLoc, int terrainCost){
        _fCost = 0;
        _gCost = 0;
        _hCost = 0;
        _mapLoc = mapLoc;
        _terrainCost = terrainCost;
        _parent = null;
         _treeDepth = 0;
    }
    
    /**
     * 
     * @param fcost
     * @param mapLoc
     * @param terrainCost
     */
    public SearchNode(int fcost, Point mapLoc, int terrainCost){
        _fCost = fcost;
        _gCost = 0;
        _hCost = 0;
        _mapLoc = mapLoc;
        _terrainCost = terrainCost;
        _parent = null;
         _treeDepth = 0;
    }
    
    /**
     * 
     * @param fcost
     * @param gcost
     * @param hcost
     * @param mapLoc
     * @param terrainCost
     */
    public SearchNode(int fcost,int gcost, int hcost, Point mapLoc,int terrainCost){
        _fCost = fcost;
        _gCost = gcost;
        _hCost = hcost;
        _mapLoc = mapLoc;
        _terrainCost = terrainCost;
        _parent = null;
         _treeDepth = 0;
    }
// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">

    
    /**
     * Returns the location of the search map image that this node is associated 
     * with
     * @return This pixel coordinate of the search map image that his node is
     * associated with
     */
    public Point getMapLoc(){
        return _mapLoc;
    }
    
    /**
     * Sets the location of the search map image that this node is associated 
     * with
     * @param mapLoc A pixel coordinate of the search map image
     */
    public void setMapLoc(Point mapLoc){
        _mapLoc = mapLoc;
    }
    
    /**
     * Returns the total cost of this node
     * @return The total cost of this node
     */
    public double getFCost(){
        return _fCost;
    }
    
    /**
     * Sets the total cost of this node
     * @param fCost The total cost of this node
     */
    public void setFCost(double fCost){
        _fCost = fCost;
    }
    
    /**
     * Returns the cumulative actual path cost of the search up and including
     * this node
     * @return  The cumulative actual path cost of the search up and including
     * this node
     */
    public double getGCost(){
        return _gCost;
    }
    
    /**
     * Sets the cumulative actual path cost of the search up and including
     * this node
     * @param gCost  The cumulative actual path cost of the search up and including
     * this node
     */
    public void setGCost(double gCost){
        _gCost = gCost;
    }
    
    /**
     * Returns the heuristic value of this node
     * @return The heuristic value of this node
     */
    public double getHCost(){
        return _hCost;
    }
      
    /**
     * Sets the heuristic value of this node
     * @param hCost The value to set as the heuristic value of this node
     */
    public void setHCost(double hCost){
        _hCost = hCost;
    }
    
    /**
     * Returns the node that is the parent node in the search tree to the this
     * node
     * @return A SearchNode that was examined when this node was put on the 
     * open list
     */
    public SearchNode getParent(){
        return _parent;
    }
    
    /**
     * Sets the parent node of this node. Used to calculate the solution path.
     * @param parent A SearchNode to store as the parent of this node
     */
    public void setParent(SearchNode parent){
        _parent = parent;
    }
    
  
    /**
     * Returns the terrain cost of the current node.  
     */
    public int getTerrainCost(){
       return _terrainCost;
    }
    
    public void setTerrainCost(int terrainCost){
        _terrainCost = terrainCost;
    }
    
    
     /**
     * Returns the depth of the node in the search tree.  The starting node will
     * have a depth of 0.
     * 
     */
    public int getTreeDepth() {
        return _treeDepth;
    }

    /**
     * Sets the depth of the node in the search tree.  The starting node should
     * be set to a depth of 0.
     *
     */
    public void setTreeDepth(int depth) {
        this._treeDepth = depth;
    }
    
// </editor-fold>

    
    /**
     * Function to add comparable function comparability to the class.  This
     * is necessary for a Search Node priority queue to sort nodes.
     * @param otherNode A SerachNode that will be compared to this node
     * @return 0 if the two nodes have an equal total cost, 1 if this has a
     * higher total cost, -1 if this node has a lower total cost
     */
    @Override
    public int compareTo(SearchNode otherNode){
        double otherNodeCost = otherNode.getFCost();
        double thisNodeCost = this.getFCost();
        
        if (thisNodeCost == otherNodeCost) {
            return 0;
        }
        else if (thisNodeCost > otherNodeCost ){
            return 1;
        }
        else{
            return -1;
        }
    }
    
    /**
     * Function necessary in order to use SearchNodes as key values in the 
     * closed list HashMap.  Uses two prime numbers so that each image map
     * location will have a unique value.
     * @return A number representing the hashed value of this Node
     */
    @Override
    public int hashCode(){
        return this.getMapLoc().x * 3 + this.getMapLoc().y * 5;
    }
   /**
    * 
    *  This also needed by the closed list HashMap in case we actually store
    *  SearchNodes as values.  This will be used to search through a bucket
    *  for the matching node.  
    *
    * @param obj The SearchNode to compare to this SearchNode
    * @return true if the nodes are equal, false if they are not
    */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final SearchNode other = (SearchNode) obj;
      
        if ((this.getMapLoc().x != other.getMapLoc().x) || (this.getMapLoc().y != other.getMapLoc().y)) {
            return false;
        }
        
        return true;
    }


    
    
    
}
