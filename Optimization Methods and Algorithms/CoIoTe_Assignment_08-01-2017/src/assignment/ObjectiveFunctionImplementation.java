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
class ObjectiveFunctionImplementation implements ObjectiveFunction {

	/**
	 * 
	 */
	public ObjectiveFunctionImplementation() {}

	/* (non-Javadoc)
	 * @see org.coinor.opents.ObjectiveFunction#evaluate(org.coinor.opents.Solution, org.coinor.opents.Move)
	 */
	@Override
	public double[] evaluate(Solution solution, Move move) {
		SolutionAdapterExtension coIoTeSolution = (SolutionAdapterExtension) solution;
		double objectiveFunction;
		
		if (move == null) {
			objectiveFunction = 0.0;
			
			// Compute objective function from scratch, iterating on each destination / operational cell.
			coIoTeSolution.hasSolution = true;
			for (int i = 0; i < coIoTeSolution.nCells; i++) {
				objectiveFunction += AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[i]);
			}
			
		} else {
			ComplexMoveImplementation coIoTeMove = (ComplexMoveImplementation) move;
			
			if (coIoTeSolution.hasSolution) {
				objectiveFunction = solution.getObjectiveValue()[0];
				
				//System.out.println("X");
				
				// Unassign tasks from destination / operational cells and assign them back in the reverse order.
				VariableModification[] firstCellVariableModifications = coIoTeSolution.variableModifications[coIoTeSolution.indexesMask[coIoTeMove.index1]];
				VariableModification[] secondCellVariableModifications = coIoTeSolution.variableModifications[coIoTeSolution.indexesMask[coIoTeMove.index2]];
				objectiveFunction += UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index1]);
				objectiveFunction += UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index2]);
				objectiveFunction += AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index2]);
				objectiveFunction += AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index1]);
				
				// Revert changes above.
				UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index1]);
				UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index2]);
				coIoTeSolution.variableModifications[coIoTeSolution.indexesMask[coIoTeMove.index1]] = firstCellVariableModifications;
				coIoTeSolution.variableModifications[coIoTeSolution.indexesMask[coIoTeMove.index2]] = secondCellVariableModifications;
				UndoUnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index1]);
				UndoUnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[coIoTeMove.index2]);
			} else {
				objectiveFunction = 0.0;
				
				// Swap items in the indexes mask
				ComplexMoveImplementation.swapIndexesMask(coIoTeSolution, coIoTeMove.index1, coIoTeMove.index2);
				
				// Revert all changes
				for (int i = 0; i < coIoTeSolution.nCells; i++) {
					UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[i]);
				}
				
				// Compute objective function from scratch, iterating on each destination / operational cell.
				coIoTeSolution.hasSolution = true;
				for (int i = 0; i < coIoTeSolution.nCells; i++) {
					objectiveFunction += AssignTasksToCell(coIoTeSolution, coIoTeSolution.indexesMask[i]);
				}
				
				// Revert swapping
				ComplexMoveImplementation.swapIndexesMask(coIoTeSolution, coIoTeMove.index1, coIoTeMove.index2);
				
				// Revert all changes
				for (int i = 0; i < coIoTeSolution.nCells; i++) {
					UnassignTasksFromCell(coIoTeSolution, coIoTeSolution.indexesMask[i]);
				}
			}
		}
		
		if (coIoTeSolution.hasSolution) {
			return new double[] { objectiveFunction };
		} else {
			return new double[] { Double.POSITIVE_INFINITY };
		}
	}
	
	static double AssignTasksToCell(SolutionAdapterExtension solution, int iDestinationCell) {
		double objectiveFunction = 0.0;
		
		// Cycle until all tasks of the destination cell are assigned.
		List<Integer> forbiddenCustomerTypes = new ArrayList<Integer>();
		List<CostPerTaskRatio> optionIndexesList = new ArrayList<CostPerTaskRatio>();
		
		VariableModification[] currentCellVariableModifications = new VariableModification[0];
		int unassignedActivities = solution.activities[iDestinationCell];
		while (unassignedActivities > 0) {
			
			// Determine position, type and time step of the user who can offer the lowest cost per task.
			int iOriginCell = -1;
			int iCustomerType = -1;
			int iTimeStep = -1;
			
			try {
				double minRatio;
				int minCustType;
				int iRatio = 0;
				do {
					iOriginCell = solution.costPerTaskRatios[iDestinationCell][iRatio].iOriginCell;
					iCustomerType = solution.costPerTaskRatios[iDestinationCell][iRatio].iCustomerType;
					iTimeStep = solution.costPerTaskRatios[iDestinationCell][iRatio].iTimeStep;
					minRatio = solution.costPerTaskRatios[iDestinationCell][iRatio].ratio;
					minCustType = iCustomerType;
					iRatio++;
				} while ((solution.usersCell[iOriginCell][iCustomerType][iTimeStep] <= 0) || forbiddenCustomerTypes.contains(iCustomerType));
				
				// In case more than one user can offer the same cost per task, select the one that performs less tasks
				while (minRatio == solution.costPerTaskRatios[iDestinationCell][iRatio].ratio) {
					int newIOriginCell = solution.costPerTaskRatios[iDestinationCell][iRatio].iOriginCell;
					int newICustomerType = solution.costPerTaskRatios[iDestinationCell][iRatio].iCustomerType;
					int newITimeStep = solution.costPerTaskRatios[iDestinationCell][iRatio].iTimeStep;
					if ((solution.usersCell[newIOriginCell][newICustomerType][newITimeStep] > 0) &&
							(forbiddenCustomerTypes.contains(newICustomerType) == false) &&
							(newICustomerType <= minCustType)) {
						//if (print) System.out.println("ABC");
						iOriginCell = newIOriginCell;
						iCustomerType = newICustomerType;
						iTimeStep = newITimeStep;
						minCustType = newICustomerType;
					}
					iRatio++;
					if (iRatio == solution.costPerTaskRatios[iDestinationCell].length) {
						break;
					}
				}
				
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				
				solution.hasSolution = false;
				solution.variableModifications[iDestinationCell] = currentCellVariableModifications;
				return objectiveFunction;
			}
			
			// Move the necessary or possible number of users from origin to destination / operational cell.
			int assignedUsers;
			int availableUsers = solution.usersCell[iOriginCell][iCustomerType][iTimeStep];
			int activitiesPerUser = solution.n[iCustomerType];
			int availableActivities = availableUsers * activitiesPerUser;
			
			if ((unassignedActivities > availableActivities) && forbiddenCustomerTypes.isEmpty()) {
				assignedUsers = availableUsers;
			} else {
				assignedUsers = 0;
				forbiddenCustomerTypes.add(iCustomerType);
				optionIndexesList.add(new CostPerTaskRatio(iOriginCell, iCustomerType, iTimeStep, 0.0));
			}
			
			if (forbiddenCustomerTypes.size() == solution.nCustomerTypes) {
				
				double minimumCost = Double.MAX_VALUE;
				CostPerTaskRatio minimumOptionIndexes = null;
				for (CostPerTaskRatio optionIndexes : optionIndexesList) {
					
					availableUsers = solution.usersCell[optionIndexes.iOriginCell][optionIndexes.iCustomerType][optionIndexes.iTimeStep];
					activitiesPerUser = solution.n[optionIndexes.iCustomerType];
					availableActivities = availableUsers * activitiesPerUser;
					assignedUsers = (unassignedActivities + activitiesPerUser - 1) / activitiesPerUser;
					double currentCost = assignedUsers * solution.costs[optionIndexes.iOriginCell][iDestinationCell][optionIndexes.iCustomerType][optionIndexes.iTimeStep];
					if ((unassignedActivities <= availableActivities) &&
							(currentCost < minimumCost)) {
						minimumCost = currentCost;
						minimumOptionIndexes = optionIndexes;
					}
					if ((unassignedActivities <= availableActivities) &&
							(currentCost == minimumCost) &&
							(minimumOptionIndexes != null) &&
							(optionIndexes.iCustomerType <= minimumOptionIndexes.iCustomerType)) {
						minimumCost = currentCost;
						minimumOptionIndexes = optionIndexes;
					}
				}
				
				iOriginCell = minimumOptionIndexes.iOriginCell;
				iCustomerType = minimumOptionIndexes.iCustomerType;
				iTimeStep = minimumOptionIndexes.iTimeStep;
				activitiesPerUser = solution.n[iCustomerType];
				assignedUsers = (unassignedActivities + activitiesPerUser - 1) / activitiesPerUser;
			}
			
			if (assignedUsers > 0) {
				solution.usersCell[iOriginCell][iCustomerType][iTimeStep] -= assignedUsers;
				unassignedActivities -= assignedUsers * activitiesPerUser;
				solution.variables[iOriginCell][iDestinationCell][iCustomerType][iTimeStep] += assignedUsers;
				objectiveFunction += assignedUsers * solution.costs[iOriginCell][iDestinationCell][iCustomerType][iTimeStep];
				
				VariableModification[] temp = currentCellVariableModifications;
				currentCellVariableModifications = Arrays.copyOf(temp, temp.length + 1);
				currentCellVariableModifications[temp.length] = new VariableModification(iOriginCell, iCustomerType, iTimeStep, assignedUsers);
			}
		}
		
		// Adjust in case of over-assignment of tasks
		if (unassignedActivities != 0) {
			
			for (int iModification = currentCellVariableModifications.length - 1; iModification >= 0; iModification--) {
				
				int iOriginCell = currentCellVariableModifications[iModification].iOriginCell;
				int iCustomerType = currentCellVariableModifications[iModification].iCustomerType;
				int iTimeStep = currentCellVariableModifications[iModification].iTimeStep;
				
				int overassignedActivities = - unassignedActivities;
				int activitiesPerUser = solution.n[currentCellVariableModifications[iModification].iCustomerType];
				if ((currentCellVariableModifications[iModification].variableValue > 0) && (overassignedActivities >= activitiesPerUser)) {
					
					solution.usersCell[iOriginCell][iCustomerType][iTimeStep] += 1;
					unassignedActivities += 1 * activitiesPerUser;
					solution.variables[iOriginCell][iDestinationCell][iCustomerType][iTimeStep] -= 1;
					objectiveFunction -= 1 * solution.costs[iOriginCell][iDestinationCell][iCustomerType][iTimeStep];
					
					currentCellVariableModifications[iModification].variableValue -= 1;
				}
			}
		}
		
		solution.variableModifications[iDestinationCell] = currentCellVariableModifications;
		return objectiveFunction;
	}
	
	static double UnassignTasksFromCell(SolutionAdapterExtension solution, int iDestinationCell) {
		double objectiveFunction = 0.0;
		for (int iModification = 0; iModification < solution.variableModifications[iDestinationCell].length; iModification++) {
			
			int iOriginCell = solution.variableModifications[iDestinationCell][iModification].iOriginCell;
			int iCustomerType = solution.variableModifications[iDestinationCell][iModification].iCustomerType;
			int iTimeStep = solution.variableModifications[iDestinationCell][iModification].iTimeStep;
			int variableValue = solution.variableModifications[iDestinationCell][iModification].variableValue;
			
			solution.usersCell[iOriginCell][iCustomerType][iTimeStep] += variableValue;
			solution.variables[iOriginCell][iDestinationCell][iCustomerType][iTimeStep] -= variableValue;
			objectiveFunction -= variableValue * solution.costs[iOriginCell][iDestinationCell][iCustomerType][iTimeStep];
		}
		solution.variableModifications[iDestinationCell] = new VariableModification[0];
		return objectiveFunction;
	}
	
	static double UndoUnassignTasksFromCell(SolutionAdapterExtension solution, int iDestinationCell) {
		double objectiveFunction = 0.0;
		for (int iModification = 0; iModification < solution.variableModifications[iDestinationCell].length; iModification++) {
			
			int iOriginCell = solution.variableModifications[iDestinationCell][iModification].iOriginCell;
			int iCustomerType = solution.variableModifications[iDestinationCell][iModification].iCustomerType;
			int iTimeStep = solution.variableModifications[iDestinationCell][iModification].iTimeStep;
			int variableValue = solution.variableModifications[iDestinationCell][iModification].variableValue;
			
			solution.usersCell[iOriginCell][iCustomerType][iTimeStep] -= variableValue;
			solution.variables[iOriginCell][iDestinationCell][iCustomerType][iTimeStep] += variableValue;
			objectiveFunction += variableValue * solution.costs[iOriginCell][iDestinationCell][iCustomerType][iTimeStep];
		}
		return objectiveFunction;
	}
	
}
