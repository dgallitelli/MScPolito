




public class Main2
{

    public static void main( String[] args )
    {
        int numCustomers = 50;
        java.util.Random R = new java.util.Random( 1234567890L );
        double[][] customers = createCustomers( numCustomers, R );
        
        net.iharder.opents.MoveManager       moveMgr    = new TSPMoveManager( numCustomers );
        net.iharder.opents.TabuList          tabuList   = new TSPTabuList( (int)(numCustomers*.30) );
        net.iharder.opents.ObjectiveFunction objFunc    = new TSPObjectiveFunction( customers );
        net.iharder.opents.Solution          initSoln   = createInitialSolution( 
                                                          ((TSPObjectiveFunction)objFunc).matrix );
        
        net.iharder.opents.TabuSearch  tabuSearch =
            new net.iharder.opents.SingleThreadedTabuSearch(
                initSoln,
                moveMgr,
                objFunc,
                tabuList,
                null,    // No aspiration criteria here
                false ); // maximizing = false means minimizing
        
        tabuSearch.addTabuSearchListener( (TSPMoveManager)moveMgr );
        tabuSearch.setIterationsToGo( numCustomers * 100 );
        tabuSearch.startSolving();
        
        TSPSolution soln = (TSPSolution) tabuSearch.getBestSolution();
        printSolution( soln );
        showSolutionFrame( soln, customers );
    }   // end main
    
    
    /** Creates <var>num</var> customers. */
    public static double[][] createCustomers( int num, java.util.Random R )
    {
        double[][] customers = new double[ num ][ 2 ];
        for( int i = 0; i < num; i++ )
        {   customers[i][0] = R.nextDouble() * 200;
            customers[i][1] = R.nextDouble() * 200;
        }   // end for: each customer
        return customers;
    }   // end createCustomers
    
    
    /** Create initial solution with a greedy heuristic. */
    public static TSPSolution createInitialSolution( double[][] matrix )
    {
        int len = matrix.length;
        int[] tour = new int[ len ];
        int[] unvisited = new int[ len ];
        for( int i = 0; i < len; i++ )
            unvisited[i] = i;
        
        // Start with customer '0' by default
        tour[0] = 0;
        unvisited[0] = -1;
        
        for( int i = 1; i < len; i++ )
        {   
            int prevCust    = tour[ i-1 ];
            double shortest = Double.MAX_VALUE;   // Shortest distance
            int closest     = -1;   // Closest neighbor
            for( int j = 0; j < len; j++ )
                if( unvisited[j] >= 0 )
                    if( matrix[prevCust][j] < shortest )
                        shortest = matrix[ prevCust ][ closest = j ];
            tour[i] = closest;
            unvisited[closest] = -1;
        }   // end for: each following customer
        
        
        TSPSolution soln = new TSPSolution();
        soln.tour = tour;
        return soln;
    }   // end createInitialSolution
    
    
    
    public static void printSolution( TSPSolution soln)
    {
        int[] tour = soln.tour;
        System.out.println( "Distance: " + soln.getObjectiveValue()[0] );
        for( int i = 0; i < tour.length; i++ )
            System.out.print( tour[i] + ( i == tour.length-1 ? "\n" : " - ") );
    }   // end printSolution
    
    
    
    
    public static void showSolutionFrame( final TSPSolution soln, final double[][] customers )
    {   java.awt.Panel panel = new java.awt.Panel()
        {   public void paint( java.awt.Graphics g )
            {   int[] tour = soln.tour;
                g.drawString( "Cost: " + soln.getObjectiveValue()[0], 2, 210 );
                // Paint trip.
                for( int i = 0; i < customers.length; i++ )
                {   // First?
                    if( i == 0 )
                    {   g.setColor( java.awt.Color.red );
                        g.drawLine( 
                            (int)customers[ tour[0] ][0],
                            (int)customers[ tour[0] ][1],
                            (int)customers[ tour[1] ][0],
                            (int)customers[ tour[1] ][1] );
                        g.drawString( "Start", 
                            (int)customers[ tour[0] ][0] + 1,
                            (int)customers[ tour[0] ][1] + 1 );
                        g.setColor( java.awt.Color.green.darker() );
                    }   // end if: first customer
                    // Last?
                    else if( i == (customers.length-1) )
                    {   g.setColor( java.awt.Color.blue );
                        g.drawLine( 
                            (int)customers[ tour[tour.length-1] ][0],
                            (int)customers[ tour[tour.length-1] ][1],
                            (int)customers[ tour[0] ][0],
                            (int)customers[ tour[0] ][1] );
                    }   // end else if: last
                    // In between
                    else
                    {   g.drawLine(
                            (int)customers[ tour[i] ][0],
                            (int)customers[ tour[i] ][1],
                            (int)customers[ tour[i+1] ][0], 
                            (int)customers[ tour[i+1] ][1] );
                    }   // end else: in between
                }   // end for
            }   // end paint
        }; // end panel
        
        java.awt.Frame frame = new java.awt.Frame();
        frame.add( panel, java.awt.BorderLayout.CENTER );
        java.awt.Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width, height;
        width = height = 250;
        frame.setBounds( (dim.width-width)/2, (dim.height-height)/2, width, height );
        frame.addWindowListener( new java.awt.event.WindowAdapter()
        {   public void windowClosing( java.awt.event.WindowEvent evt )
            {   System.exit(0);
            }   // end windowClosing
        }); // end window adapter
        frame.setVisible( true );
        
    }   // end showSolutionFrame
    
    
    

}   // end class Main
