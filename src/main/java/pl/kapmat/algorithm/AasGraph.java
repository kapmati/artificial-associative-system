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
		timer.showTime("Build graph");

		timer.startCount();
		nodeService.serializeSetOfNodes(nodeSet);
		timer.endCount();
		timer.showTime("Serialize graph");

		timer.startCount();
		Set<Node> newSet = nodeService.deserializeSetOfNodes();
		timer.endCount();
		timer.showTime("Deserialize graph");
	}

	public void extendGraph(List<Sentence> sentences) {
		timer.startCount();
		nodeSet = nodeService.deserializeSetOfNodes();
		timer.endCount();
		timer.showTime("Deserialize graph");

		char[] charsToDelete = {'-', ',', '.', ':', ';', '(', ')', '{', '}', '[', ']', '+', '=', '_', '<', '>', '|', '/', '\\', '*'};
		sentences = sentenceService.deleteChars(sentences, charsToDelete);

		//Enable graph progress tracking
		runGraphProgressCheckerThread(sentences.size());

		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime("Extend graph [+" + sentences.size() + " sentences]");

		nodeService.serializeSetOfNodes(nodeSet);
	}

	/*TODO
		2) Podstawienie specjalnego słowa za liczbę lub ujednolicenie liczb
		3) Sprawdzić czy wstawiane są jedynie unikatowe węzły i czy ich liczba się zgadza
	*/
	private void buildGraph(List<Sentence> sentences) {
		String[] words;
		for (Sentence sentence : sentences) {
			//Increment static value used to track building/extending graph progress
			GraphProgressChecker.index++;

			words = sentence.getText().split(" ");
			Node singleNode;
			Set<Node> neighbourNodes = new LinkedHashSet<>();
			for (String word : words) {
				word = word.trim();
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

	private void runGraphProgressCheckerThread(int size) {
		Runnable runChecking = new GraphProgressChecker(size);
		Thread statusThread = new Thread(runChecking);
		statusThread.start();
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
