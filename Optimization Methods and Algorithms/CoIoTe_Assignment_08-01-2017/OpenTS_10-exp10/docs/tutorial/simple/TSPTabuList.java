



public class TSPTabuList 
implements net.iharder.opents.TabuList 
{
    int   tenure;
    int[] tabuList;
    int   pos;
    
    public TSPTabuList( int tenure )
    {
        this.tenure = tenure;
        tabuList = new int[ tenure ];
        for( int i = 0; i < tenure; i++ )
            tabuList[i] = -1;
    }   // end constructor
    
    

    public void setTabu(
    net.iharder.opents.Solution fromSoln, 
    net.iharder.opents.Move move)
    {
        TSPSwapMove mv = (TSPSwapMove)move;
        tabuList[ pos++ % tenure ] = 
            mv.pos1 
            * ((TSPSolution)fromSoln).tour.length
            + mv.pos2;
    }   // end setTabu
    
 
    public boolean isTabu(
    net.iharder.opents.Solution fromSoln, 
    net.iharder.opents.Move move)
    {
        TSPSwapMove mv   = (TSPSwapMove)move;
        int         hash = mv.pos1 
                           * ((TSPSolution)fromSoln).tour.length 
                           + mv.pos2;
        for( int i = 0; i < tenure; i++ )
            if( tabuList[i] == hash )
                return true; 
        return false;
    }   // end isTabu
    
}   // end class TSPTabuList
