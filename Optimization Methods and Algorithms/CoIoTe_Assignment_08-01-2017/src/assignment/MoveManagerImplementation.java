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
class MoveManagerImplementation implements MoveManager {

	/**
	 * 
	 */
	public MoveManagerImplementation() {}

	/* (non-Javadoc)
	 * @see org.coinor.opents.MoveManager#getAllMoves(org.coinor.opents.Solution)
	 */
	@Override
	public Move[] getAllMoves(Solution solution) {
		SolutionAdapterExtension coIoTeSolution = (SolutionAdapterExtension) solution;
		int indexesMaskLength = coIoTeSolution.indexesMask.length;
		Random random = new Random();
		Move[] moves = new ComplexMoveImplementation[indexesMaskLength];
		for (int i = 0; i < indexesMaskLength; i++) {
			int j = random.nextInt(indexesMaskLength);
			if (i == j) {
				j = (j + 1) % indexesMaskLength;
			}
			moves[i] = new ComplexMoveImplementation(i, j);
		}
		return moves;
	}

}
