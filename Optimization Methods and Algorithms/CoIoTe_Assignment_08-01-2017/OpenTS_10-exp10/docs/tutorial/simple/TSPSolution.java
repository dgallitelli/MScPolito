

public class TSPSolution 
extends net.iharder.opents.SolutionAdapter 
{
    public int[] tour;
    
    /*
    public TSPSolution(){}
    public TSPSolution( int numCustomers )
    {
        tour = new int[ numCustomers ];
        for( int i = 0; i < numCustomers; i++ )
            tour[i] = i;
    }   // end constructor
    */
    
    public Object clone()
    {   
        TSPSolution copy = (TSPSolution)super.clone();
        copy.tour = (int[])this.tour.clone();
        return copy;
    }   // end clone
    
}   // end class TSPSolution
