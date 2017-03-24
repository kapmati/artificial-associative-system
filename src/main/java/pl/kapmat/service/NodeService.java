package pl.kapmat.service;

import org.springframework.stereotype.Service;
import pl.kapmat.algorithm.Coefficient;
import pl.kapmat.algorithm.Node;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Node service
 *
 * @author Mateusz Kaproń
 */
@Service
public class NodeService {

	public Node getNodeFromSet(Set<Node> nodeSet, String word) {
		return nodeSet.stream().filter(node -> node.getWord().equals(word.toUpperCase())).findFirst().get();
	}

	public void serializeSetOfNodes(Set<Node> nodeSet, String graphFileName) {
		try (OutputStream file = new FileOutputStream("src/main/resources/" + graphFileName);
			 OutputStream buffer = new BufferedOutputStream(file);
			 ObjectOutput output = new ObjectOutputStream(buffer);
		) {
			output.writeObject(nodeSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<Node> deserializeSetOfNodes(String graphFileName) {
		Set<Node> setOfNodes = null;
		try (InputStream file = new FileInputStream("src/main/resources/" + graphFileName);
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

	public List<Node> similarWord(Node node, Set<Node> candidates) {
		String word = node.getWord();
		char[] wordArray = word.toCharArray();
		int amountOfSameLetter;
		Map<Node, Integer> similarityMap = new HashMap<>();
		for (Node n : candidates) {
			char[] candidateArray = n.getWord().toCharArray();
			amountOfSameLetter = 0;
			for (char wordChar : wordArray) {
				for (char candidateChar : candidateArray) {
					if (wordChar == candidateChar) {
						amountOfSameLetter++;
						break;
					}
				}
			}
			similarityMap.put(n, amountOfSameLetter);
		}
		int maxValue = similarityMap.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getValue();

		List<Node> similarityList = similarityMap.entrySet().stream()
				.filter(e -> e.getValue().equals(maxValue) && e.getKey().getWord().length() <= word.length() + 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		Comparator<Node> wordComparator = Comparator.comparing(Node::getWord);
		Collections.sort(similarityList, wordComparator);


		//Słowa o takiej samej pierwszej literze
		List<Node> secondPart = similarityList.stream()
				.filter(e -> e.getWord().charAt(0) == node.getWord().charAt(0))
				.collect(Collectors.toList());

		//Słowa o takiej samej długości i pierwszej literze
		List<Node> bestPart = secondPart.stream()
				.filter(e -> e.getWord().length() == node.getWord().length())
				.collect(Collectors.toList());

		List<Node> result = new ArrayList<>();
		result.addAll(bestPart);
		similarityList.removeAll(secondPart);
		secondPart.removeAll(bestPart);
		result.addAll(secondPart);
		result.addAll(similarityList);

		//Sprawdzać pozycję litery, jeśli taka sama to przyznawać punkty i je zliczać

		return result;
	}

}
