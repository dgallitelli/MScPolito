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
class CostPerTaskRatio implements Comparable<CostPerTaskRatio> {

	int iOriginCell;
	int iCustomerType;
	int iTimeStep;
	double ratio;
	
	/**
	 * 
	 */
	public CostPerTaskRatio(int iOriginCell, int iCustomerType, int iTimeStep, double ratio) {
		this.iOriginCell = iOriginCell;
		this.iCustomerType = iCustomerType;
		this.iTimeStep = iTimeStep;
		this.ratio = ratio;
	}

	@Override
	public int compareTo(CostPerTaskRatio o) {
		return Double.compare(this.ratio, o.ratio);
	}
	
	@Override
	public String toString() {
		return Double.toString(this.ratio);
	}
	
}
