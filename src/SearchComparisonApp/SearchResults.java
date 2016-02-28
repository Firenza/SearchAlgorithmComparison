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

/**
 *
 * @author Chad Oftedahl (chadoftedahl@gmail.com)
 */
public class SearchResults {
    private int _closedListCount;
    private int _openListCount;
    private double _elapsedTime;
    private double _solutionCost;

    public SearchResults() {
        _closedListCount = 0;
        _openListCount = 0;
        _elapsedTime = 0;
        _solutionCost = 0;
        
    }

    public SearchResults(int closedListCount, int openListCount, double elapsedTime, int solutionCost) {
        _closedListCount = closedListCount;
        _openListCount = openListCount;
        _elapsedTime = elapsedTime;
        _solutionCost = solutionCost;
    }
 
    /**
     * Returns the number of nodes in the closed list for the search that
     * generated this object
     * @return The number of nodes in the closed list
     */
    public int getClosedListCount() {
        return _closedListCount;
    }

    /**
     * Sets the number of nodes in the closed list
     * @param closedListCount The number of nodes in the closed list
     */
    public void setClosedListCount(int closedListCount) {
        this._closedListCount = closedListCount;
    }

    /**
     * Returns the number of nodes in the open list for the search that
     * generated this object
     * @return The number of nodes in the open list
     */
    public int getOpenListCount() {
        return _openListCount;
    }

    /**
     * Sets the number of nodes in the open list
     * @param openListCount The number of nodes in the open list
     */
    public void setOpenListCount(int openListCount) {
        this._openListCount = openListCount;
    }

    /**
     * Returns the combined number of nodes in the open and closed list
     * @param closedListCount The number of nodes in the open and closed list
     */
    public int getTotalListCount() {
        return _openListCount + _closedListCount;
    }

    /**
     * Returns the amount of time that was taken by the search that generated
     * this object
     * @return Time taken by a search
     */
    public double getElapsedTime() {
        return _elapsedTime;
    }

    /**
     * Sets the time taken by a search
     * @param elapsedTime The time taken by a search
     */
    public void setElapsedTime(double elapsedTime) {
        this._elapsedTime = elapsedTime;
    }

    /**
     * Returns the cost of the solution found by the search algorithm that 
     * generated this object
     * @return the _solutionCost
     */
    public double getSolutionCost() {
        return _solutionCost;
    }

    /**
     * Sets the cost of a solution
     * @param solutionCost The cost of a solution
     */
    public void setSolutionCost(double solutionCost) {
        this._solutionCost = solutionCost;
    }
    
    
}
