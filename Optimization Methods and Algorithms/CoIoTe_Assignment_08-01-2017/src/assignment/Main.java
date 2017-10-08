/**
 * 
 */
package assignment;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
class Main {

	/**
	 * 
	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean _test = false;
		String _inPath = new String();
		String _outPath = new String();
		String _solPath = new String();
		
		// Read input parameters
		for (Integer i = 0; i < args.length; i++) {
			if (Objects.equals(args[i], "-i") == true) {
				_inPath = args[++i];
			} else if (Objects.equals(args[i], "-o") == true) {
				_outPath = args[++i];
			} else if (Objects.equals(args[i], "-s") == true) {
				_solPath = args[++i];
			} else if (Objects.equals(args[i], "-test") == true) {
				_test = true;
			}
		}
		
		if (!_test && (_inPath.isEmpty() || _outPath.isEmpty())) {
			System.out.println("------------------------------ ");
			System.out.println("CMD");
			System.out.println("------------------------------ ");
			System.out.println("-i path of the instance file");
			System.out.println("-o path of the output file");
			System.out.println("-s path of the output solution file or of the solution to test(optional)");
			System.out.println("-test enable the feasibility test (optional)");
			return;
		}
		
		if (!_test) {
			
			// Read the instance file
			Heuristic _heuristic = new Heuristic(_inPath);
			
			// Solve the problem
			List<Double> stat = new Vector<Double>();
			
			// Solve with the tabu search algorithm, instead of the fast algorithm.
			//_heuristic.solveFast(stat, -1);
			_heuristic.solveTabuSearch(stat, 5);
			
			_heuristic.getStatSolution(stat);
			
			// Write KPI of solution
			String[] splittedInPath = _inPath.split(Pattern.quote(File.separator));
			String instanceFileName = splittedInPath[splittedInPath.length - 1];
			String[] splittedFileName = instanceFileName.split(Pattern.quote("."));
			String instanceName = splittedFileName[0];
			_heuristic.writeKPI(_outPath, instanceName, stat);
			
			// Write solution
			if (!_solPath.isEmpty()) {
				_heuristic.writeSolution(_solPath);
			}
		} else {
			
			// Read the instance file
			Heuristic _heuristic = new Heuristic(_inPath);
			
			// Read the solution file
			eFeasibleState _feasibility = _heuristic.isFeasible(_solPath);
			switch (_feasibility) {
			case FEASIBLE:
				System.out.println("Solution is feasible");
				break;
			case NOT_FEASIBLE_DEMAND:
				System.out.println("Solution is not feasible: demand not satisfied");
				break;
			case NOT_FEASIBLE_USERS:
				System.out.println("Solution is not feasible: exceeded number of available users");
				break;
			default:
				break;
			}
		}
		
		System.out.println("Quitting...");
		return;
	}

}
