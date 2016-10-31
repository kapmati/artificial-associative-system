package pl.kapmat.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.NodeService;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.TimeCounter;

import java.util.*;

/**
 * Artificial associative system main class
 *
 * @author Mateusz Kaproń
 */
@Service
public class AasGraph {

	@Autowired
	private SentenceService sentenceService;

	@Autowired
	private NodeService nodeService;

	private Set<Node> nodeSet = new HashSet<>();
	private TimeCounter timer = new TimeCounter();

	public void run() {
		//Load sentences from db
		List<Sentence> sentences = sentenceService.getAllSentences();

		//Delete unnecessary characters
		char[] charsToDelete = {'-', ',', '.', ':', ';', '(', ')', '{', '}', '[', ']', '+', '=', '_', '<', '>', '|', '/', '\\', '*'};
		sentences = sentenceService.deleteChars(sentences, charsToDelete);

		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime();

		nodeService.serializeSetOfNodes(nodeSet);

		timer.startCount();
		Set<Node> newSet = nodeService.deserializeSetOfNodes();
		timer.endCount();
		timer.showTime();
	}

	/*TODO
		2) Podstawienie specjalnego słowa za liczbę lub ujednolicenie liczb
		3) Sprawdzić czy wstawiane są jedynie unikatowe węzły i czy ich liczba się zgadza
	*/
	private void buildGraph(List<Sentence> sentences) {
		String[] words;
		for (Sentence sentence : sentences) {
			words = sentence.getText().split(" ");
			Node singleNode;
			Set<Node> neighbourNodes = new LinkedHashSet<>();
			for (String word : words) {
				if (!word.equals("")) {
					singleNode = new Node(word.toUpperCase());
					//Check if word is new
					if (!nodeSet.contains(singleNode)) {
						nodeSet.add(singleNode);
					} else {
						singleNode = nodeService.getNodeFromSet(nodeSet, word);
						singleNode.increaseLevel();
					}
					neighbourNodes.add(singleNode);
				}
			}
			connectNeighbours(neighbourNodes);
		}
	}

	private void connectNeighbours(Set<Node> neighbourNodes) {
		List<Node> nodesList = new ArrayList<>(neighbourNodes);
		for (Node mainNode : nodesList) {
			for (Node otherNode : nodesList) {
				if (!mainNode.equals(otherNode)) {
					mainNode.addNeighbour(otherNode);
				}
			}
		}

	}
}
