/**
 * 
 */
package assignment;

import org.coinor.opents.*;
import java.util.*;

/**
 * @author Group 29
 *	
 * Massimo Tumolo (236037)
 * Enrico Balsamo (222340)
 * Angelo Filannino (242140)
 * Davide Gallitelli (251421)
 * Daniele Montisci (230073)
 */
class SolutionAdapterExtension extends SolutionAdapter {

	int[] indexesMask;
	VariableModification[][] variableModifications;
	
	// Input
	int[][][][] costs;
	int[] n;
	int[] activities;
	int[][][] usersCell;
	
	CostPerTaskRatio[][] costPerTaskRatios;
	
	int nCells;
	int nCustomerTypes;
	int nTimeSteps;
	
	// Output
	int[][][][] variables;
	boolean hasSolution;
	
	/**
	 * 
	 */
	public SolutionAdapterExtension() {}
	
	public SolutionAdapterExtension(Random indexesMaskRandom, Heuristic heuristic) {
		
		// Randomly initialize the mask for the indexes
		indexesMask = new int[heuristic.nCells];
		for (int i = 0; i < indexesMask.length; i++) {
			indexesMask[i] = i;
		}
		if (indexesMaskRandom != null) {
			for (int i = indexesMask.length - 1; i > 0; i--) {
				int j = indexesMaskRandom.nextInt(i + 1);
				int temp = indexesMask[j];
				indexesMask[j] = indexesMask[i];
				indexesMask[i] = temp;
		    }
		}
		
		// Empty variable modifications list
		variableModifications = new VariableModification[heuristic.nCells][];
		for (int i = 0; i < variableModifications.length; i++) {
			variableModifications[i] = new VariableModification[0];
		}
		
		// Clone inputs
		this.costs = heuristic.problem.costs;
		this.n = heuristic.problem.n;
		this.activities = heuristic.problem.activities;
		this.usersCell = new int[heuristic.problem.usersCell.length][][];
		for (int j = 0; j < heuristic.problem.usersCell.length; j++) {
			this.usersCell[j] = new int[heuristic.problem.usersCell[j].length][];
			for (int m = 0; m < heuristic.problem.usersCell[j].length; m++) {
				this.usersCell[j][m] = heuristic.problem.usersCell[j][m].clone();
			}
		}
		
		this.nCells = heuristic.nCells;
		this.nCustomerTypes = heuristic.nCustomerTypes;
		this.nTimeSteps = heuristic.nTimeSteps;
		
		this.costPerTaskRatios = heuristic.costPerTaskRatios;
		
		// Initialize outputs
		this.variables = new int[nCells][nCells][nCustomerTypes][nTimeSteps];
		for (int i = 0; i < this.variables.length; i++) {
			for (int j = 0; j < this.variables[i].length; j++) {
				for (int m = 0; m < this.variables[i][j].length; m++) {
					for (int t = 0; t < this.variables[i][j][m].length; t++) {
						this.variables[i][j][m][t] = 0;
					}
				}
			}
		}
		this.hasSolution = false;
	}

	@Override
	public Object clone() {
		SolutionAdapterExtension coIoTeSolution = (SolutionAdapterExtension) super.clone();
		
		coIoTeSolution.indexesMask = this.indexesMask.clone();
		
		coIoTeSolution.variableModifications = new VariableModification[this.variableModifications.length][];
		for (int i = 0; i < this.variableModifications.length; i++) {
			coIoTeSolution.variableModifications[i] = this.variableModifications[i].clone();
		}
		
		// Clone inputs
		coIoTeSolution.costs = this.costs;
		coIoTeSolution.n = this.n;
		coIoTeSolution.activities = this.activities;
		coIoTeSolution.usersCell = new int[this.usersCell.length][][];
		for (int j = 0; j < this.usersCell.length; j++) {
			coIoTeSolution.usersCell[j] = new int[this.usersCell[j].length][];
			for (int m = 0; m < this.usersCell[j].length; m++) {
				coIoTeSolution.usersCell[j][m] = this.usersCell[j][m].clone();
			}
		}
		
		coIoTeSolution.nCells = this.nCells;
		coIoTeSolution.nCustomerTypes = this.nCustomerTypes;
		coIoTeSolution.nTimeSteps = this.nTimeSteps;
		
		coIoTeSolution.costPerTaskRatios = this.costPerTaskRatios;
		
		// Clone outputs
		coIoTeSolution.variables = new int[this.variables.length][][][];
		for (int i = 0; i < this.variables.length; i++) {
			coIoTeSolution.variables[i] = new int[this.variables[i].length][][];
			for (int j = 0; j < this.variables[i].length; j++) {
				coIoTeSolution.variables[i][j] = new int[this.variables[i][j].length][];
				for (int m = 0; m < this.variables[i][j].length; m++) {
					coIoTeSolution.variables[i][j][m] = this.variables[i][j][m].clone();
				}
			}
		}
		coIoTeSolution.hasSolution = this.hasSolution;
		
		return coIoTeSolution;
	}
	
}
