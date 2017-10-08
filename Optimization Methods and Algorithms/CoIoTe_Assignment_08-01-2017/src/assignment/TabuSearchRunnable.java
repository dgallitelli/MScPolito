/**
 * 
 */
package assignment;

import org.coinor.opents.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Group 29
 *	
 * Massimo Tumolo (236037)
 * Enrico Balsamo (222340)
 * Angelo Filannino (242140)
 * Davide Gallitelli (251421)
 * Daniele Montisci (230073)
 */
public class TabuSearchRunnable implements Runnable {
	
	Random random;
	
	// Input
	Heuristic heuristic;
	
	int threadNum;
	long timeStart;
	int timeLimit;
	
	// Output
	public int[][][][] solution;
	public boolean hasSolution;
	public double objectiveFunctionValue;

	public int iterations;
	
	/**
	 * 
	 */
	public TabuSearchRunnable(
			Heuristic heuristic,
			int threadNum,
			long timeStart,
			int timeLimit) {
		this.heuristic = heuristic;
		this.threadNum = threadNum;
		this.timeStart = timeStart;
		this.timeLimit = timeLimit;
		iterations = 0;
		random = (threadNum == 0) ? null : ThreadLocalRandom.current();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		// Initialize framework's objects
		Solution initialSolution = new SolutionAdapterExtension(random, heuristic);
		MoveManager moveManager = new MoveManagerImplementation();
		ObjectiveFunction objectiveFunction = new ObjectiveFunctionImplementation();
		TabuList tabuList = new ComplexTabuList(heuristic.nCells * 10, 2);
		AspirationCriteria aspirationCriteria = new BestEverAspirationCriteria();
		TabuSearchListener tabuSearchListener = new TabuSearchAdapterExtension();
		//TabuSearchListener tabuSearchListener = new TabuSearchAdapter();
		
		TabuSearch tabuSearch = new SingleThreadedTabuSearch(
				initialSolution,
				moveManager,
				objectiveFunction,
				tabuList,
				aspirationCriteria,
				false);
		tabuSearch.addTabuSearchListener(tabuSearchListener);
		
		// Solve normally
		long tIterationStart = System.currentTimeMillis();
		tabuSearch.setIterationsToGo(1);
		tabuSearch.startSolving();
		iterations++;
		long tIterationStop = System.currentTimeMillis();
		
		while ((System.currentTimeMillis() - timeStart) < ((timeLimit * 980L) - tIterationStop + tIterationStart)) {
			tabuSearch.setIterationsToGo(1);
			tabuSearch.startSolving();
			iterations++;
		}
		
		// Solve with framework's multi-threading (NOT WORKING)
		/*
		tabuSearch.setIterationsToGo(100);
		tabuSearch.setThreads(Runtime.getRuntime().availableProcessors());
		tabuSearch.setThreadPriority(Thread.MAX_PRIORITY);
		try {
			tabuSearch.startSolving();
			Thread.sleep(1000);
			tabuSearch.stopSolving();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		*/
		
		// Get solution
		SolutionAdapterExtension bestSolution = (SolutionAdapterExtension) tabuSearch.getBestSolution();
		solution = bestSolution.variables;
		hasSolution = bestSolution.hasSolution;
		objectiveFunctionValue = bestSolution.getObjectiveValue()[0];
		
		// Print results
		System.out.println("Thread #" + threadNum + ": Objective function value = " + objectiveFunctionValue);
		System.out.println("Thread #" + threadNum + ": Iterations = " + iterations);
		
		return;
	}
	
}
