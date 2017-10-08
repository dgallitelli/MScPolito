/**
 * 
 */
package assignment;

/**
 * @author Group 29
 *	
 * Massimo Tumolo (236037)
 * Enrico Balsamo (222340)
 * Angelo Filannino (242140)
 * Davide Gallitelli (251421)
 * Daniele Montisci (230073)
 */
public class VariableModification {

	/**
	 * 
	 */
	public VariableModification(int iOriginCell, int iCustomerType, int iTimeStep, int variableValue) {
		this.iOriginCell = iOriginCell;
		this.iCustomerType = iCustomerType;
		this.iTimeStep = iTimeStep;
		this.variableValue = variableValue;
	}

	int iOriginCell;
	int iCustomerType;
	int iTimeStep;
	int variableValue;
	
	@Override
	public String toString() {
		return new String(new Integer(variableValue).toString());
	}
}
