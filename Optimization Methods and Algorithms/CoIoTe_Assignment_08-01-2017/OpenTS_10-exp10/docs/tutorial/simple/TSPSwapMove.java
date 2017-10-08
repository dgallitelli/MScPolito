




public class TSPSwapMove 
implements net.iharder.opents.Move 
{
    public int pos1;
    public int pos2;
    
    private int   a; // Used repeatedly in swap operation
    private int[] t; // Used repeatedly in swap operation
    
    public TSPSwapMove(){}
    public TSPSwapMove( int pos1, int pos2 )
    {   this.pos1 = pos1;
        this.pos2   = pos2;
    }   // end constructor
    
    /** Swap two customers in this solution. */
    public void operateOn( net.iharder.opents.Solution solution )
    {   
        t       = ((TSPSolution)solution).tour;
        a       = t[pos1];
        t[pos1] = t[pos2];
        t[pos2] = a;
    }   // end operateOn

}   // end class TSPSwapMove
