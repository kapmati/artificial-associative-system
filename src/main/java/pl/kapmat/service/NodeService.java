package pl.kapmat.service;

import org.springframework.stereotype.Service;
import pl.kapmat.algorithm.Coefficient;
import pl.kapmat.algorithm.Node;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Node service
 *
 * @author Mateusz Kaproń
 */
@Service
public class NodeService {

	private static String SERIALIZE_DATA_FILE_NAME = "setOfNodes.ser";

	public Node getNodeFromSet(Set<Node> nodeSet, String word) {
		return nodeSet.stream().filter(node -> node.getWord().equals(word.toUpperCase())).findFirst().get();
	}

	public void serializeSetOfNodes(Set<Node> nodeSet) {
		try (OutputStream file = new FileOutputStream(SERIALIZE_DATA_FILE_NAME);
			 OutputStream buffer = new BufferedOutputStream(file);
			 ObjectOutput output = new ObjectOutputStream(buffer);
		) {
			output.writeObject(nodeSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<Node> deserializeSetOfNodes() {
		Set<Node> setOfNodes = null;
		try (InputStream file = new FileInputStream(SERIALIZE_DATA_FILE_NAME);
			 InputStream buffer = new BufferedInputStream(file);
			 ObjectInput input = new ObjectInputStream(buffer);
		) {
			setOfNodes = (Set<Node>) input.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return setOfNodes;
	}

	public Map<Node, Coefficient> getBestNeighbours(Node node) {
		double bestWeight = 0;
		for (Map.Entry entry : node.getNeighbourMap().entrySet()) {
			if (bestWeight < ((Coefficient) entry.getValue()).getSynapticWeight()) {
				bestWeight = ((Coefficient) entry.getValue()).getSynapticWeight();
			}
		}
		final double finalWeight = bestWeight;
		return node.getNeighbourMap().entrySet().stream()
				.filter(n -> n.getValue().getSynapticWeight() == finalWeight)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Map<Node, Double> getBestJointNeighbours(List<Node> nodes) {
		Map<Node, Double> bestNodes = new HashMap<>();
		for (Node node : nodes) {
			for (Map.Entry entry : node.getNeighbourMap().entrySet()) {
				if (!bestNodes.containsKey(entry.getKey())) {
					bestNodes.put((Node) entry.getKey(), ((Coefficient) entry.getValue()).getSynapticWeight());
				} else {
					bestNodes.put((Node) entry.getKey(), bestNodes.get(entry.getKey()) + ((Coefficient) entry.getValue()).getSynapticWeight());
				}
			}
		}
		return bestNodes;
	}

}
