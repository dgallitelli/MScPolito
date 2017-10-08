/**
 * 
 */
package assignment;

import org.coinor.opents.*;
import java.util.Arrays;

/**
 * @author Group 29
 *	
 * Massimo Tumolo (236037)
 * Enrico Balsamo (222340)
 * Angelo Filannino (242140)
 * Davide Gallitelli (251421)
 * Daniele Montisci (230073)
 */
class TabuSearchAdapterExtension extends TabuSearchAdapter {

	/**
	 * 
	 */
	public TabuSearchAdapterExtension() {}
	
	@Override
	public void tabuSearchStarted(TabuSearchEvent e) {
		//System.out.println("TabuSearch has started.");
		return;
	}
	
	@Override
	public void tabuSearchStopped(TabuSearchEvent e) {
		//System.out.println("TabuSearch has stopped.");
		return;
	}

	@Override
	public void newBestSolutionFound(TabuSearchEvent e) {
		SolutionAdapterExtension bestSolution = (SolutionAdapterExtension) e.getTabuSearch().getBestSolution();
		double objectiveFunctionValue = bestSolution.getObjectiveValue()[0];
		int[] indexesMask = bestSolution.indexesMask;
		
		System.out.print("New best solution found:");
		System.out.print(" Objective function value = " + objectiveFunctionValue + ";");
		System.out.print(" Indexes mask = " + Arrays.toString(indexesMask) + ";");
		System.out.println();
		return;
	}
	
	@Override
	public void newCurrentSolutionFound(TabuSearchEvent e) {
		
		return;
	}
	
	@Override
	public void unimprovingMoveMade(TabuSearchEvent e) {
		System.out.println("Unimproving move made.");
		return;
	}
	
	@Override
	public void improvingMoveMade(TabuSearchEvent e) {
		System.out.println("Improving move made.");
		return;
	}
	
	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent e) {
		//System.out.println("No-change-in-value move made.");
		return;
	}
	
}
