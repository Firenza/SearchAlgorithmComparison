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

//
import SearchComparisonApp.*;
import java.awt.geom.Point2D;

/**
 *
* @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public class AStarSearch extends ImageSearchAlgorithm {
    /**
     * Use straight line distance as a heuristic;
     */
    @Override
    public double calculateHCost(SearchNode node){
        return Point2D.distance(node.getMapLoc().x, node.getMapLoc().y, 
                                getEndNode().getMapLoc().x, getEndNode().getMapLoc().y);
    }
    
    /**
     * Utilize both the actual terrain cost and the heuristic cost in the 
     * calculation of the total node cost
     */
    @Override
    public double calculateFCost(SearchNode node){
        return node.getGCost() + node.getHCost();
    }

    /**
     * Use a priority queue for the open list since the nodes need to be kept 
     * in order of their total cost
     */
    @Override
    public openListType getOpenListType() {
        return openListType.priorityQueue;
    }
}
