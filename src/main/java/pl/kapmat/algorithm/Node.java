package pl.kapmat.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Small part of graph
 *
 * @author Mateusz Kaproń
 */
public class Node {

	private String word;
	private int level;
	private Map<Node, Integer> neighbourMap = new HashMap<>();

	public Node() {

	}

	public Node(String word) {
		this.word = word;
		level = 0;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void increaseLevel() {
		this.level++;
	}

	public Map<Node, Integer> getNeighbourMap() {
		return neighbourMap;
	}

	public void setNeighbourMap(Map<Node, Integer> neighbourMap) {
		this.neighbourMap = neighbourMap;
	}

	public void addNeighbour(Node newNeighbour) {
		//TODO Change rate
		this.neighbourMap.put(newNeighbour, 0);
	}

	public List<Node> getNeighbourList() {
		return neighbourMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Node node = (Node) o;

		return word.equals(node.word);
	}

	@Override
	public int hashCode() {
		return word.hashCode();
	}
}
