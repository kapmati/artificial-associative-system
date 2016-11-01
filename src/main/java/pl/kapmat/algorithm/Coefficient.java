package pl.kapmat.algorithm;

import java.io.Serializable;

/**
 * One class for two node coefficients:
 * 	1) synapticWeight
 * 	2) synapticEffectiveness
 *
 * @author Mateusz Kaproń
 */
public class Coefficient implements Serializable {

	private double synapticWeight;
	private double synapticEffectiveness;

	public Coefficient(double synapticWeight, double synapticEffectiveness) {
		this.synapticWeight = synapticWeight;
		this.synapticEffectiveness = synapticEffectiveness;
	}

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

	@Override
	public String toString() {
		return "Coefficient{" +
				"synapticWeight=" + synapticWeight +
				", synapticEffectiveness=" + synapticEffectiveness +
				'}';
	}
}
