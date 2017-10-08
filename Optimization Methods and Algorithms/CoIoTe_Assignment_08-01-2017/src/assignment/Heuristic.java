/**
 * 
 */
package assignment;

import java.io.*;
import java.util.*;

/**
 * @author Group 29
 * 
 * Created by:
 * Luca Gobbato
 * 
 * Modified by:
 * Massimo Tumolo (236037)
 * Enrico Balsamo (222340)
 * Angelo Filannino (242140)
 * Davide Gallitelli (251421)
 * Daniele Montisci (230073)
 */
public class Heuristic {

	int nTimeSteps;
	int nCustomerTypes;
	int nCells;
	Data problem;
	
	CostPerTaskRatio[][] costPerTaskRatios;
	
	boolean hasSolution;
	int[][][][] solution;
	
	/**
	 * 
	 */
	public Heuristic(String path) {
		problem = new Data();
		hasSolution = false;
		String line;
		String[] words;
		
	    try (BufferedReader iffN = new BufferedReader(new FileReader(path))) {
	    	
	    	line = iffN.readLine();
		    words = line.split(" ");
		    nCells = Integer.valueOf(words[0]);
		    nTimeSteps = Integer.valueOf(words[1]);
		    nCustomerTypes = Integer.valueOf(words[2]);

		    // Memory allocation
		    solution = new int[nCells][nCells][nCustomerTypes][nTimeSteps];
		    problem.costs = new int[nCells][nCells][nCustomerTypes][nTimeSteps];
		    problem.n = new int[nCustomerTypes];
		    problem.activities = new int[nCells];
		    problem.usersCell = new int[nCells][nCustomerTypes][nTimeSteps];

		    //
		    line = iffN.readLine();
		    line = iffN.readLine();
		    words = line.split(" ");
		    for (int m = 0; m < nCustomerTypes; m++) {
		        problem.n[m] = Integer.valueOf(words[m]);
		    }

		    line = iffN.readLine();
		    for (int m = 0; m < nCustomerTypes; m++) {
		        for (int t = 0; t < nTimeSteps; t++) {
		            line = iffN.readLine();// linea con m e t
		            for (int i = 0; i < nCells; i++) {
		            	line = iffN.readLine();// linea della matrice c_{ij} per t ed m fissati
		        	    words = line.split(" ");
		                for (int j = 0; j < nCells; j++) {
		                	// Equivalent to original code: truncate decimal part.
		                    problem.costs[i][j][m][t] = Double.valueOf(words[j]).intValue();
		                    
		                    // Consider decimal part.
		                    //problem.costs[i][j][m][t] = Double.valueOf(words[j]);
		                }
		            }
		        }
		    }

		    line = iffN.readLine();
		    line = iffN.readLine();
		    words = line.split(" ");
		    for (int i = 0; i < nCells; i++) {
		        problem.activities[i] = Integer.valueOf(words[i]);
		    }

		    line = iffN.readLine();
		    for (int m = 0; m < nCustomerTypes; m++) {
		        for (int t = 0; t < nTimeSteps; t++) {
		    	    line = iffN.readLine();
		    	    line = iffN.readLine();
		    	    words = line.split(" ");
		            for (int i = 0; i < nCells; i++) {
		                problem.usersCell[i][m][t] = Integer.valueOf(words[i]);
		            }
		        }
		    }
		    iffN.close();
	    } catch (IOException ioe) {
	    	
	    	System.out.println("Impossible to open");
	    	ioe.printStackTrace();
	    } catch (Exception ex) {
	    	
	    	ex.printStackTrace();
	    }
	}
	
	public void solveFast(List<Double> stat, int timeLimit) {
		double objFun = 0.0;
		Calendar tStart = Calendar.getInstance();

		for (int i = 0; i < nCells; i++) {
			for (int j = 0; j < nCells; j++) {
				for (int m = 0; m < nCustomerTypes; m++) {
					for (int t = 0; t < nTimeSteps; t++) {
						solution[i][j][m][t] = 0;
					}
				}
			}
		}
		
		for (int i = 0; i < nCells; i++) {
			int demand = problem.activities[i];
			boolean notSatisfied = true;
			
			for (int j = 0; (j < nCells) && (notSatisfied); j++) {
				for (int m = 0; (m < nCustomerTypes) && (notSatisfied); m++) {
					for (int t = 0; (t < nTimeSteps) && (notSatisfied); t++) {
						if (i != j) {
							if (demand > problem.n[m] * problem.usersCell[j][m][t]) {
								solution[j][i][m][t] = problem.usersCell[j][m][t];
	                            problem.usersCell[j][m][t] -= solution[j][i][m][t];
							} else {
								solution[j][i][m][t] += (int) Math.floor(demand / problem.n[m]);
	                            notSatisfied = false;
							}
							if (solution[j][i][m][t] != 0) {
								objFun += solution[j][i][m][t] * problem.costs[j][i][m][t];
							}
	                        demand -= problem.n[m]*solution[j][i][m][t];
						}
					}
				}
			}
		}
		
		stat.add(((double) (Calendar.getInstance().getTimeInMillis() - tStart.getTimeInMillis()) / 1000));
		stat.add(objFun);
		hasSolution = true;
		
		//System.out.println("Start time = " + tStart.getTimeInMillis());
		//System.out.println("End time = " + Calendar.getInstance().getTimeInMillis());
		System.out.println("Elapsed time = " + ((double) (Calendar.getInstance().getTimeInMillis() - tStart.getTimeInMillis()) / 1000));
		System.out.println("Objective Function = " + objFun);
		return;
	}
	
	public void solveTabuSearch(List<Double> stat, int timeLimit) {
		
		// Start timer
		long tStart = System.currentTimeMillis();
				
		calculateCostPerTaskMatrix();
		
		// Launch thread(s)
		Vector<TabuSearchRunnable> tabuSearchRunnableList = new Vector<TabuSearchRunnable>();
		Vector<Thread> tabuSearchThreadList = new Vector<Thread>();
		int availableThreads = Runtime.getRuntime().availableProcessors();
		//int availableThreads = 1;
		for (int i = 0; i < availableThreads; i++) {
			tabuSearchRunnableList.add(new TabuSearchRunnable(this, i, tStart, timeLimit));
			tabuSearchThreadList.add(new Thread(tabuSearchRunnableList.lastElement()));
			tabuSearchThreadList.lastElement().start();
		}
		
		// Wait thread(s)
		for (Thread tabuSearchThread : tabuSearchThreadList) {
			try {
				tabuSearchThread.join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				solution = null;
				hasSolution = false;
				return;
			}
		}

		// Get solution
		double objectiveFunctionValue = Double.MAX_VALUE;
		int iterations = 0;
		for (TabuSearchRunnable tabuSearchRunnable : tabuSearchRunnableList) {
			if (tabuSearchRunnable.objectiveFunctionValue < objectiveFunctionValue) {
				solution = tabuSearchRunnable.solution;
				hasSolution = tabuSearchRunnable.hasSolution;
				objectiveFunctionValue = tabuSearchRunnable.objectiveFunctionValue;
			}
			iterations += tabuSearchRunnable.iterations;
		}
		
		
		// Stop timer
		long tStop = System.currentTimeMillis();
		stat.add(((double) (tStop - tStart)) / 1000.0);
		stat.add(objectiveFunctionValue);
		
		//System.out.println("Start time = " + tStart.getTimeInMillis());
		//System.out.println("End time = " + Calendar.getInstance().getTimeInMillis());
		System.out.println("Elapsed time = " + (((double) (tStop - tStart)) / 1000.0));
		System.out.println("Best objective function value = " + objectiveFunctionValue);
		System.out.println("Sum of iterations = " + iterations);
		return;
	}
		
	public void calculateCostPerTaskMatrix() {
		long tStartRatiosMatrix = System.currentTimeMillis();
		
		costPerTaskRatios = new CostPerTaskRatio[nCells][];
		CostPerTaskRatio[] currentCellCostPerUserRatio = new CostPerTaskRatio[nCells * nCustomerTypes * nTimeSteps];
		for (int i = 0; i < nCells; i++) {
			int jmt = 0;
			
			for (int j = 0; j < nCells; j++) {
				if (i == j) { continue; }
				
				for (int m = 0; m < nCustomerTypes; m++) {
					double activitiesPerUser = (double) problem.n[m];
					
					for (int t = 0; t < nTimeSteps; t++) {
						if (problem.usersCell[j][m][t] <= 0) { continue; }
						
						double ratio = ((double) problem.costs[j][i][m][t]) / activitiesPerUser;
						currentCellCostPerUserRatio[jmt] = new CostPerTaskRatio(j, m, t, ratio);
						jmt++;
					}
				}
			}
			costPerTaskRatios[i] = Arrays.copyOf(currentCellCostPerUserRatio, jmt);
			Arrays.parallelSort(costPerTaskRatios[i]);
		}
		
		long tStopRatiosMatrix = System.currentTimeMillis();
		System.out.println((tStopRatiosMatrix - tStartRatiosMatrix) + " msecs Ratios Matrix");
		return;
	}
	
	public void getStatSolution(List<Double> stat) {
		if (!hasSolution) {
			return;
		}
		int[] tipi = new int[nCustomerTypes];
		for (int m = 0; m < nCustomerTypes; m++) {
			tipi[m] = 0;
		}
		for (int i = 0; i < nCells; i++) {
			for (int j = 0; j < nCells; j++) {
				for (int t = 0; t < nTimeSteps; t++) {
					for (int m = 0; m < nCustomerTypes; m++) {
						if (solution[i][j][m][t] > 0) {
							tipi[m] += solution[i][j][m][t];
						}
					}
 				}
			}
		}
		for (int m = 0; m < nCustomerTypes; m++) {
			stat.add((double) tipi[m]);
		}
		return;
	}
	
	public void writeKPI(String path, String instanceName, List<Double> stat) {
		try (BufferedWriter iffN = new BufferedWriter(new FileWriter(path, true))) {
			
			if (hasSolution) {
				iffN.write(instanceName + ";" + stat.get(0) + ";" + stat.get(1));
				for (Integer i = 2; i < stat.size(); i++) {
					iffN.write(";" + stat.get(i));
				}
			} else {
				iffN.write(instanceName + ";" + "NO_SOL");
			}
			iffN.newLine();
			iffN.flush();
		} catch (IOException ioe) {
			
			System.out.println("Impossible to open");
			ioe.printStackTrace();
		}
		return;
	}
	
	public void writeSolution(String path) {
		if (!hasSolution) {
			return;
		}
		try (BufferedWriter iffN = new BufferedWriter(new FileWriter(path, true))) {
			
			iffN.write(nCells + ";" + nTimeSteps + ";" + nCustomerTypes);
			iffN.newLine();
			for (int m = 0; m < nCustomerTypes; m++) {
				for (int t = 0; t < nTimeSteps; t++) {
					for (int i = 0; i < nCells; i++) {
						for (int j = 0; j < nCells; j++) {
							if (solution[i][j][m][t] > 0) {
								iffN.write(i + ";" + j + ";" + m + ";" + t + ";" + solution[i][j][m][t]);
								iffN.newLine();
							}
						}
					}
				}
			}
			iffN.flush();
		} catch (IOException ioe) {
			
			System.out.println("Impossible to open");
			ioe.printStackTrace();
		}
		return;
	}
	
	eFeasibleState isFeasible(String path) {
		
		String line;
		String[] words;
		int nCellsN;
		int nTimeStepsN;
		int nCustomerTypesN;
		int i, j, m, t;
		
	    try (BufferedReader iffN = new BufferedReader(new FileReader(path))) {
	    	line=iffN.readLine();
		    words = line.split(";");
		    
		    nCellsN = Integer.valueOf(words[0]);
		    nTimeStepsN = Integer.valueOf(words[1]);
		    nCustomerTypesN = Integer.valueOf(words[2]);			
		
		    int[][][][] solutionN= new int[nCellsN][nCellsN][nCustomerTypesN][nTimeStepsN];
		    
		    line=iffN.readLine();
		    while(line != null){
			    words = line.split(";");
			    i=Integer.valueOf(words[0]);
			    j=Integer.valueOf(words[1]); 
			    m=Integer.valueOf(words[2]); 
			    t=Integer.valueOf(words[3]); 

			    solutionN[i][j][m][t] = Integer.valueOf(words[4]);	    	
			    line=iffN.readLine();
		    }
		    
		    //Demand
		    Boolean feasible = true;
		    int expr = 0;
		    
		    for (i = 0; i < nCells; i++) {
		        for (j = 0; j < nCells; j++)
		            for (m = 0; m < nCustomerTypes; m++)
		                for (t = 0; t < nTimeSteps; t++)
		                    expr += problem.n[m] * solutionN[j][i][m][t];
		        if (expr < problem.activities[i])
		            feasible = false;
		    }
		    
		    if (!feasible)
		    	return eFeasibleState.NOT_FEASIBLE_DEMAND;
		    
		    // Max Number of users
		    for (i = 0; i < nCells; i++)
		        for (m = 0; m < nCustomerTypes; m++)
		            for (t=0; t < nTimeSteps; t++) {
		                expr = 0;
		                for (j = 0; j < nCells; j++)
		                    expr += solutionN[i][j][m][t];
		                if (expr > problem.usersCell[i][m][t])
		                    feasible = false;
		            }

		    if(!feasible)
		        return eFeasibleState.NOT_FEASIBLE_USERS;		    

		return eFeasibleState.FEASIBLE;  
		    
	    } catch (IOException ioe) {
			
			System.out.println("Impossible to open");
			ioe.printStackTrace();
		}
		

		return eFeasibleState.FEASIBLE;
	}

}
