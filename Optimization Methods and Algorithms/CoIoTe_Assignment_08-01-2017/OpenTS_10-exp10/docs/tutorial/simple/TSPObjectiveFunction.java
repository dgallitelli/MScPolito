

import net.iharder.opents.*;


public class TSPObjectiveFunction implements ObjectiveFunction
{
    public double[][] matrix;
    
    public TSPObjectiveFunction( double[][] customers ) 
    {   matrix = createMatrix( customers );
    }   // end constructor

    public double[] evaluate( Solution solution, Move move )
    {
        int[] tour = ((TSPSolution)solution).tour;
        int len = tour.length;
        
        // If move is null, calculate distance from scratch
        if( move == null )
        {
            double dist = 0;
            for( int i = 0; i < len; i++ )
                dist += matrix[i][ i+1 >= len ? 0 : i+1 ];
            return new double[]{ dist };
        }   // end if: move == null
        
        // Else calculate incremental
        else
        {
            TSPSwapMove mv = (TSPSwapMove)move;
            int pos1 = mv.pos1; // pos1 is always less
            int pos2 = mv.pos2; // than pos2.
            double dist = solution.getObjectiveValue()[0];
            
            // Treat a pair swap move differently
            if( pos1 + 1 == pos2 )
            {   //   | |
                // A-B-C-D: swap B and C, say (works for symmetric matrix only)
                dist -= matrix[ tour[pos1-1] ][ tour[pos1] ];           // -AB
                dist -= matrix[ tour[pos2]   ][ tour[(pos2+1)%len] ];   // -CD
                dist += matrix[ tour[pos1-1] ][ tour[pos2] ];           // +AC
                dist += matrix[ tour[pos1]   ][ tour[(pos2+1)%len] ];   // +BD
                return new double[]{ dist };
            }   // end if: pair swap
            
            // Else the swap is separated by at least one customer
            else
            {   //   |     |
                // A-B-C-D-E-F: swap B and E, say
                dist -= matrix[ tour[pos1-1] ][ tour[pos1] ];           // -AB
                dist -= matrix[ tour[pos1]   ][ tour[pos1+1] ];         // -BC
                dist -= matrix[ tour[pos2-1] ][ tour[pos2] ];           // -DE
                dist -= matrix[ tour[pos2]   ][ tour[(pos2+1)%len] ];   // -EF
                
                dist += matrix[ tour[pos1-1] ][ tour[pos2] ];           // +AE
                dist += matrix[ tour[pos2]   ][ tour[pos1+1] ];         // +EC
                dist += matrix[ tour[pos2-1] ][ tour[pos1] ];           // +DB
                dist += matrix[ tour[pos1]   ][ tour[(pos2+1)%len] ];   // +BF
                return new double[]{ dist };
            }   // end else: not a pair swap
        }   // end else: calculate incremental
    }   // end evaluate
    
    /** Create symmetric matrix. */
    private double[][] createMatrix( double[][] customers )
    {
        int len = customers.length;
        double[][] matrix = new double[len][len];
        
        for( int i = 0; i < len; i++ )
            for( int j = i+1; j < len; j++ )
                matrix[i][j] = matrix[j][i] = norm(
                    customers[i][0], customers[i][1],
                    customers[j][0], customers[j][1] );
        return matrix;
    }   // end createMatrix
    
    /** Calculate distance between two points. */
    private double norm( double x1, double y1, double x2, double y2 )
    {   
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        return Math.sqrt( xDiff*xDiff + yDiff*yDiff );
    }   // end norm
    
    
}   // end class TSPObjectiveFunction
