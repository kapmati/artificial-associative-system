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
	private boolean nearWord = true;

	public Coefficient(double synapticWeight, double synapticEffectiveness, boolean nearWord) {
		this.synapticWeight = synapticWeight;
		this.synapticEffectiveness = synapticEffectiveness;
		this.nearWord = nearWord;
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

	public boolean isNearWord() {
		return nearWord;
	}

	public void setNearWord(boolean nearWord) {
		this.nearWord = nearWord;
	}

	@Override
	public String toString() {
		return "Coefficient{" +
				"synapticWeight=" + synapticWeight +
				", synapticEffectiveness=" + synapticEffectiveness +
				", nearWord=" + nearWord +
				'}';
	}
}
