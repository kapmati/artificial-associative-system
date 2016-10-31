package pl.kapmat.algorithm;

/**
 * One class for two node coefficients:
 * 	1) synapticWeight
 * 	2) synapticEffectiveness
 *
 * @author Mateusz Kaproń
 */
public class Coefficient {

	private double synapticWeight;
	private double synapticEffectiveness;

	public double getSynapticWeight() {
		return synapticWeight;
	}

	public void setSynapticWeight(double synapticWeight) {
		this.synapticWeight = synapticWeight;
	}

	public double getSynapticEffectiveness() {
		return synapticEffectiveness;
	}

	public void setSynapticEffectiveness(double synapticEffectiveness) {
		this.synapticEffectiveness = synapticEffectiveness;
	}
}
