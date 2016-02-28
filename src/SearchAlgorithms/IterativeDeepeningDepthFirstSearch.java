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
package SearchAlgorithms;

import SearchComparisonApp.ImageSearchAlgorithm;
import SearchComparisonApp.SearchNode;

/**
 *
 *  @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public class IterativeDeepeningDepthFirstSearch extends ImageSearchAlgorithm{
    int _currentDepthLimit;
    
    public IterativeDeepeningDepthFirstSearch(){
        _currentDepthLimit = 1;
    }
    
    /*
     * Depth first search does not utilize a heuristic so just return 0;
     */
    @Override
    public double calculateHCost(SearchNode node){
        return 0;
    }
    /**
     * Return 0 because DFS does not order nodes based on total cost 
     */
    @Override
    public double calculateFCost(SearchNode node){
        return 0;
    }
    
    @Override
    public openListType getOpenListType() {
        return openListType.stack;
    }

    
    @Override
    public void runSearchStep(){
        
        SearchNode curNode = getNextNode();
        
        if (curNode == null){
            
             _currentDepthLimit += 1;
             
             resetOpenList();
             resetClosedList();
             
             clearPainting();
             
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
                 * then continue on to the next adjacent node
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
                 * Set the nodes depth in the tree so we can know when to stop
                 * our current iteration of th depth search
                 */
                adjNode.setTreeDepth(curNode.getTreeDepth() + 1);
                
                
                if (adjNode.getTreeDepth() <= _currentDepthLimit) {
                    /*
                    * If this node is in the open list then update the nodes cost
                    * in the open list if need be and continue on to the next 
                    * adjacent node
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
              
            }
            
            /*
             * Add the current node to the closed list since we are done examining
             * all of its adjacent nodes
             */
            addToClosedList(curNode);
        }
    }



}