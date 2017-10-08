




public class TSPMoveManager 
implements net.iharder.opents.MoveManager, net.iharder.opents.TabuSearchListener
{
    private TSPSwapMove[] lotsOfMoves;
    private TSPSwapMove[] fewMoves;
    private boolean diversify = true;
    private int consecBadMoves;
   
    
    public TSPMoveManager( int numCustomers )
    {   createSwapMoves( numCustomers );
    }   // end constructor
    
    
    public net.iharder.opents.Move[] getAllMoves( 
    net.iharder.opents.Solution solution )
    {   
        //if( diversify )
        //    return lotsOfMoves;
        //else
            return fewMoves;
    }   // end getAllMoves
    
    
    private void createSwapMoves( int tourLength )
    {
        int count = 0;
        for( int i = 1; i <= tourLength-2; i++ )
            count += i;
        lotsOfMoves = new TSPSwapMove[ count ];
        int index = 0;
        for( int i = 1; i < tourLength-1; i++ )
            for( int j = i+1; j < tourLength; j++ )
                lotsOfMoves[index++] = new TSPSwapMove( i, j );
        
        // Each customer after the first gets swapped with
        // up to M following customers
        int M = (int) (tourLength * .2);
        count = (tourLength-M-1)*M;
        for( int i = 1; i <= M-1; i++ )
            count += i;
        fewMoves = new TSPSwapMove[ count ];
        index = 0; 
        for( int i = 1; i < tourLength; i++ )
            for( int j = 1; j <= M && i+j < tourLength; j++ )
                fewMoves[index++] = new TSPSwapMove( i, i+j );
         
    }   // end createSwapMoves
    
    public void newBestSolutionFound(net.iharder.opents.TabuSearchEvent e)
    {   consecBadMoves = 0;
        diversify = false;
    }   // end newBestSolutionFound
    
    
    
    public void unimprovingMoveMade(net.iharder.opents.TabuSearchEvent e)
    {   
        if( consecBadMoves++ >= 3 )
            diversify = true;
    }   // end unimprovingMoveMade
    
    
    public void tabuSearchStarted(net.iharder.opents.TabuSearchEvent e){}
    public void tabuSearchStopped(net.iharder.opents.TabuSearchEvent e){}
    public void newCurrentSolutionFound(net.iharder.opents.TabuSearchEvent e){}

}   // end class TSPMoveManager
