package pl.kapmat.service;

import org.springframework.stereotype.Service;
import pl.kapmat.algorithm.Coefficient;
import pl.kapmat.algorithm.Node;
import pl.kapmat.util.MathUtil;

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
		System.out.println("Start SERIALIZATION");
		try (FileOutputStream file = new FileOutputStream("src/main/resources/" + graphFileName);
			 ObjectOutputStream output = new ObjectOutputStream(file)
		) {
			output.writeObject(nodeSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<Node> deserializeSetOfNodes(String graphFileName) {
		Set<Node> setOfNodes = null;
		try (FileInputStream file = new FileInputStream("src/main/resources/" + graphFileName);
			 ObjectInputStream input = new ObjectInputStream(file)
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

	public Map<Node, Double> getBestNextWordsUsingPart(List<Node> nodeList, String partOfWord) {
		if (partOfWord.equalsIgnoreCase("nbsp")) {
			return getBestNextWords(nodeList, null).entrySet().stream()
					.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
					.limit(20)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} else {
			return getBestNextWords(nodeList, null).entrySet().stream()
					.filter(n -> n.getKey().getWord().startsWith(partOfWord.toUpperCase()))
					.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
					.limit(20)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	public Map<Node, Double> getBestNextWords(List<Node> nodeList, String invalidWordPart) {
		Node lastNode = nodeList.get(nodeList.size() - 1);
		Map<Node, Coefficient> lastNodeNeighbours = lastNode.getNeighbourMap();
		Map<Node, Double> bestNodesMap = new LinkedHashMap<>();
		char firstLetter;
		if (invalidWordPart != null) {
			firstLetter = Character.toUpperCase(invalidWordPart.charAt(0));
			for (Map.Entry<Node, Coefficient> nodeEntry : lastNodeNeighbours.entrySet()) {
				if (nodeEntry.getValue().isNearWord() && firstLetter == nodeEntry.getKey().getWord().charAt(0)) {
					bestNodesMap.put(nodeEntry.getKey(), nodeEntry.getValue().getSynapticEffectiveness());
				}
			}
		} else {
			for (Map.Entry<Node, Coefficient> nodeEntry : lastNodeNeighbours.entrySet()) {
				if (nodeEntry.getValue().isNearWord()) {
					bestNodesMap.put(nodeEntry.getKey(), nodeEntry.getValue().getSynapticEffectiveness());
				}
			}
		}


		bestNodesMap = bestNodesMap.entrySet().stream()
				.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
				.limit(100)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
					throw new AssertionError();
				}, LinkedHashMap::new));

		bestNodesMap.entrySet().forEach(n -> n.setValue(lastNodeNeighbours.get(n.getKey()).getSynapticWeight()));

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

		return bestNodesMap.entrySet().stream()
				.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
				.limit(50)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
					throw new AssertionError();
				}, LinkedHashMap::new));
	}

	public Map<Node, Double> getBestSimilarWord(List<Node> inputNodes, Node node, Set<Node> candidates) {
//		List<Node> similarWords = similarWord(node, candidates);
		List<Node> similarWords = closestWordsByLevenshtein(node, candidates, 2);
		Map<Node, Double> resultMap = new LinkedHashMap<>();

		for (Node n : inputNodes) {
			for (Node similarWord : similarWords) {
				if (n.getNeighbourMap().containsKey(similarWord)) {
					if (resultMap.containsKey(similarWord)) {
						resultMap.put(similarWord, n.getNeighbourMap().get(similarWord).getSynapticWeight() + resultMap.get(similarWord));
					} else {
						resultMap.put(similarWord, n.getNeighbourMap().get(similarWord).getSynapticWeight());
					}
				}
			}
		}

		Map<Node, Double> filteredResultMap = new LinkedHashMap<>();
		if (inputNodes.size() > 0) {
			for (Map.Entry<Node, Double> entry : resultMap.entrySet()) {
				if (inputNodes.get(inputNodes.size()-1).getNeighbourMap().containsKey(entry.getKey())) {
					filteredResultMap.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			filteredResultMap = resultMap;
		}

		return filteredResultMap;
	}

	public List<Node> similarWord(Node node, Set<Node> candidates) {
		String word = node.getWord();
		char[] wordArray = word.toCharArray();
		int amountOfSameLetter;
		Map<Node, Integer> similarityMap = new HashMap<>();
		for (Node n : candidates) {
			if (!n.getWord().equals(node.getWord()) && (n.getWord().length() <= word.length() + 1) || (n.getWord().length() <= word.length() - 1)) {
				char[] candidateArray = n.getWord().toCharArray();
				amountOfSameLetter = 0;
				for (int i = 0; i < wordArray.length; i++) {
					for (int j = 0; j < candidateArray.length; j++) {
						if (wordArray[i] == candidateArray[j]) {
							amountOfSameLetter++;
							break;
						}
					}
				}
				similarityMap.put(n, amountOfSameLetter);
			}
		}
		int maxValue = similarityMap.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getValue();

		List<Node> similarityList = similarityMap.entrySet().stream()
				.filter(e -> e.getValue().equals(maxValue - 1) || e.getValue().equals(maxValue))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		List<Node> result = new ArrayList<>();
		result.addAll(similarityList);

		return result;
	}

	public List<Node> closestWordsByLevenshtein(Node node, Set<Node> candidates, int maxDistance) {
		int distance;
		List<Node> closestNodes = new ArrayList<>();
		for (Node candidate : candidates) {
			distance = levenshteinDistance(node.getWord(), candidate.getWord());
			if (distance <= maxDistance) {
				closestNodes.add(candidate);
			}
		}
		return closestNodes;
	}

	public Map<Node, Double> checkNextPartOfContext(Map<Node, Double> bestNodes, List<String> otherWords) {
		if (otherWords.size() > 0) {
			Map<Node, Double> bestNewNodes = new LinkedHashMap<>();
			for (Map.Entry<Node, Double> entry : bestNodes.entrySet()) {
				if (entry.getKey().getNeighbourMap().entrySet().stream().anyMatch(e -> otherWords.get(0).equalsIgnoreCase(e.getKey().getWord()))) {
					bestNewNodes.put(entry.getKey(), entry.getValue() +
							entry.getKey().getNeighbourMap().entrySet().stream()
									.filter(e -> otherWords.get(0).equalsIgnoreCase(e.getKey().getWord())).findFirst().get().getValue().getSynapticWeight());
				}
			}
			return bestNewNodes;
		} else {
			return bestNodes;
		}
	}

	private static int levenshteinDistance(String s1, String s2) {

		int l1 = s1.length();
		int l2 = s2.length();
		int cost;
		int distanceTable[][] = new int[l1 + 1][l2 + 1];

		for (int i = 0; i <= l1; i++) {
			distanceTable[i][0] = i;
		}

		for (int j = 0; j <= l2; j++) {
			distanceTable[0][j] = j;
		}

		for (int i = 1; i <= l1; i++) {
			for (int j = 1; j <= l2; j++) {
				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					cost = 0;
				} else {
					cost = 1;
				}
				distanceTable[i][j] = Math.min(distanceTable[i - 1][j] + 1, Math.min(distanceTable[i][j - 1] + 1, distanceTable[i - 1][j - 1] + cost));
			}
		}
		return distanceTable[l1][l2];
	}

}
