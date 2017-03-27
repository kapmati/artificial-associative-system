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

	public Map<Node, Double> getBestNextWords(List<Node> nodeList, String partOfWord) {
		Node lastNode = nodeList.get(nodeList.size() - 1);
		Map<Node, Coefficient> lastNodeNeighbours = lastNode.getNeighbourMap();
		Map<Node, Double> bestNodesMap = new HashMap<>();
		for (Map.Entry<Node, Coefficient> nodeEntry : lastNodeNeighbours.entrySet()) {
			bestNodesMap.put(nodeEntry.getKey(), nodeEntry.getValue().getSynapticWeight());
		}
		for (Node node : nodeList) {
			if (!node.equals(lastNode)) {
				for (Map.Entry<Node, Coefficient> nodeEntry : node.getNeighbourMap().entrySet()) {
					if (bestNodesMap.containsKey(nodeEntry.getKey())) {
						for (Map.Entry<Node, Double> bestNode : bestNodesMap.entrySet()) {
							if (bestNode.getKey().equals(nodeEntry.getKey())) {
								bestNodesMap.put(bestNode.getKey(), bestNodesMap.get(bestNode.getKey()) + nodeEntry.getValue().getSynapticWeight());
								break;
							}
						}

					}
				}
			}
		}

		//Filter all neighbour which starts with @partOfWord
		return bestNodesMap.entrySet().stream()
				.filter(n -> n.getKey().getWord().startsWith(partOfWord.toUpperCase()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public List<Node> similarWord(Node node, Set<Node> candidates) {
		String word = node.getWord();
		char[] wordArray = word.toCharArray();
		int amountOfSameLetter;
		int samePosition;
		Map<Node, Integer> similarityMap = new HashMap<>();
		Map<Node, Integer> positionMap = new HashMap<>();
		for (Node n : candidates) {
			if (!n.getWord().equals(node.getWord())) {
				char[] candidateArray = n.getWord().toCharArray();
				amountOfSameLetter = 0;
				samePosition = 0;
				for (int i = 0; i < wordArray.length; i++) {
					for (int j = 0; j < candidateArray.length; j++) {
						if (wordArray[i] == candidateArray[j]) {
							if (i == j) {
								samePosition++;
							}
							amountOfSameLetter++;
							break;
						}
					}
				}
				positionMap.put(n, samePosition);
				similarityMap.put(n, amountOfSameLetter);
			}
		}
		int maxValue = similarityMap.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getValue();

		List<Node> similarityList = similarityMap.entrySet().stream()
				.filter(e -> e.getValue().equals(maxValue) && e.getKey().getWord().length() <= word.length() + 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		int maxPosition = positionMap.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getValue();
		List<Node> positionList = positionMap.entrySet().stream()
				.filter(e -> e.getValue().equals(maxPosition) && e.getKey().getWord().length() <= word.length() + 2)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		Comparator<Node> wordComparator = Comparator.comparing(Node::getWord);
		Collections.sort(similarityList, wordComparator);
		Collections.sort(positionList, wordComparator);

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
		positionList.removeAll(bestPart);
		result.addAll(positionList);
		secondPart.removeAll(result);
		result.addAll(secondPart);
		similarityList.removeAll(result);
		result.addAll(similarityList);

		return result;
	}

}
