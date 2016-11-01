package pl.kapmat.algorithm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Small part of graph
 *
 * @author Mateusz Kaproń
 */
public class Node implements Serializable {

	private String word;
	private int level = 0;
	private Map<Node, Coefficient> neighbourMap = new HashMap<>();

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

	public Map<Node, Coefficient> getNeighbourMap() {
		return neighbourMap;
	}

	public void setNeighbourMap(Map<Node, Coefficient> neighbourMap) {
		this.neighbourMap = neighbourMap;
	}

	public void addNeighbour(Node newNeighbour, Coefficient coeff) {
		this.neighbourMap.put(newNeighbour, coeff);
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

		if (word == null && node.word == null) {
			return true;
		} else if (word == null || node.word == null) {
			return false;
		} else {
			return word.equals(node.word);
		}
	}

	@Override
	public int hashCode() {
		if (word == null) {
			return 0;
		} else {
			return word.hashCode();
		}
	}
}
