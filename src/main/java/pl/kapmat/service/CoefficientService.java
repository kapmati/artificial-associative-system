package pl.kapmat.service;

import org.springframework.stereotype.Service;
import pl.kapmat.algorithm.Coefficient;
import pl.kapmat.algorithm.Node;
import pl.kapmat.util.MathUtil;

/**
 * Coefficient service
 *
 * @author Mateusz Kaproń
 */
@Service
public class CoefficientService {

	public Coefficient countCoefficients(Node firstNode, int firstIndex, Node secondNode, int secondIndex) {
		double tau = (double) (firstIndex - secondIndex);
		double sE;
		if (secondNode.getNeighbourMap().get(firstNode) != null) {
			sE = secondNode.getNeighbourMap().get(firstNode).getSynapticEffectiveness() + (1 / tau);
		} else {
			sE = 1 / tau;
		}
		double sW = (2 * sE) / (secondNode.getLevel() + sE);

		return new Coefficient(MathUtil.roundDouble(sW,8), MathUtil.roundDouble(sE,8));
	}

	/**
	 * This method should be invoked after increasing node level
	 */
	public void updateCoefficients(Node node) {
		for (Coefficient coeff : node.getNeighbourMap().values()) {
			double sE = coeff.getSynapticEffectiveness();
			double sW = (2 * sE) / (node.getLevel() + sE);
			coeff.setSynapticWeight(MathUtil.roundDouble(sW,10));
		}
	}
}
