package pl.kapmat.service;

import org.springframework.stereotype.Service;
import pl.kapmat.algorithm.Coefficient;
import pl.kapmat.algorithm.Node;

/**
 * DESCRIPTION
 *
 * @author Mateusz Kaproń
 */
@Service
public class CoefficientService {

	public static Coefficient countCoefficients(Node firstNode, int firstIndex, Node secondNode, int secondIndex) {
		double tau = (double) (firstIndex - secondIndex);

		//Synaptic effectiveness
		// TODO: 31.10.16
//		double sE = secondNode.getNeighCoefficient().get(firstNode).getSynapticEffectiveness() + (1 / tau);
		double sE = 0;
		//Synaptic weight
		double sW = (2 * sE) / (secondNode.getLevel() + sE);

		return new Coefficient(sW, sE);
	}
}
