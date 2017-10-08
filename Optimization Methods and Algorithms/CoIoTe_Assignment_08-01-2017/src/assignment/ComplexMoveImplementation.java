/**
 * 
 */
package assignment;

import org.coinor.opents.*;

/**
 * @author Group 29
 *	
 * Massimo Tumolo (236037)
 * Enrico Balsamo (222340)
 * Angelo Filannino (242140)
 * Davide Gallitelli (251421)
 * Daniele Montisci (230073)
 */
class ComplexMoveImplementation implements ComplexMove {

	/**
	 * 
	 */
	public ComplexMoveImplementation(int index1, int index2) {
		this.index1 = index1;
		this.index2 = index2;
	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.Move#operateOn(org.coinor.opents.Solution)
	 */
	@Override
	public void operateOn(Solution solution) {
		SolutionAdapterExtension coIoTeSolution = (SolutionAdapterExtension) solution;
		
		if (coIoTeSolution.hasSolution) {
			
			ObjectiveFunctionImplementation.UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[index1]);
			ObjectiveFunctionImplementation.UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[index2]);
			
			swapIndexesMask(coIoTeSolution, index1, index2);
			
			ObjectiveFunctionImplementation.AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[index1]);
			ObjectiveFunctionImplementation.AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[index2]);
		} else {
			
			// Swap items in the indexes mask
			swapIndexesMask(coIoTeSolution, index1, index2);
			
			// Revert all changes
			for (int i = 0; i < coIoTeSolution.nCells; i++) {
				ObjectiveFunctionImplementation.UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[i]);
			}
			
			// Compute objective function from scratch, iterating on each destination / operational cell.
			coIoTeSolution.hasSolution = true;
			for (int i = 0; i < coIoTeSolution.nCells; i++) {
				ObjectiveFunctionImplementation.AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[i]);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.ComplexMove#attributes()
	 */
	@Override
	public int[] attributes() {
		return new int[] { index1, index2 };
	}

	int index1;
	int index2;
	
	static void swapIndexesMask(SolutionAdapterExtension coIoTeSolution, int index1, int index2) {
		int temp = coIoTeSolution.indexesMask[index1];
		coIoTeSolution.indexesMask[index1] = coIoTeSolution.indexesMask[index2];
		coIoTeSolution.indexesMask[index2] = temp;
		return;
	}
	
}
