package pl.kapmat.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.CoefficientService;
import pl.kapmat.service.NodeService;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.GraphProgressChecker;
import pl.kapmat.util.TimeCounter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

	@Autowired
	private CoefficientService coefficientService;

	private Set<Node> nodeSet = new HashSet<>();
	private TimeCounter timer = new TimeCounter();

	public void run() {
		//Load sentences from db
		List<Sentence> sentences = sentenceService.getAllSentences();

		//Delete unnecessary characters
		char[] charsToDelete = {'-', ',', '.', ':', ';', '(', ')', '{', '}', '[', ']', '+', '=', '_', '<', '>', '|', '/', '\\', '*', '\'', '?', '"', '!'};
		sentences = sentenceService.deleteChars(sentences, charsToDelete);
		sentenceService.changeNumber(sentences);

		//Enable graph progress tracking
		runGraphProgressCheckerThread(sentences.size());

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

		char[] charsToDelete = {'-', ',', '.', ':', ';', '(', ')', '{', '}', '[', ']', '+', '=', '_', '<', '>', '|', '/', '\\', '*', '\'', '?', '"', '!'};
		sentences = sentenceService.deleteChars(sentences, charsToDelete);
		sentenceService.changeNumber(sentences);

		//Enable graph progress tracking
		runGraphProgressCheckerThread(sentences.size());

		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime("Extend graph [+" + sentences.size() + " sentences]");

		nodeService.serializeSetOfNodes(nodeSet);
	}

	private void buildGraph(List<Sentence> sentences) {
		String[] words;
		nodeSet.clear();
		for (Sentence sentence : sentences) {
			//Increment static value used to track building/extending graph progress
			GraphProgressChecker.index++;

			words = sentence.getText().split(" ");
			Node singleNode;
			LinkedHashSet<Node> neighbourNodes = new LinkedHashSet<>();
			for (String word : words) {
				word = word.trim();
				if (!word.equals("")) {
					singleNode = new Node(word.toUpperCase());
					//Check if word is new
					if (!nodeSet.contains(singleNode)) {
						nodeSet.add(singleNode);
						singleNode.increaseLevel();
					} else {
						singleNode = nodeService.getNodeFromSet(nodeSet, word);
						singleNode.increaseLevel();
						coefficientService.updateCoefficients(singleNode);
					}
					neighbourNodes.add(singleNode);
				}
			}
			connectNeighbours(neighbourNodes);
		}
	}

	public int readTest() {
		timer.startCount();
		Set<Node> newSet = nodeService.deserializeSetOfNodes();
		timer.endCount();
		timer.showTime("Deserialize graph");
		return newSet.size();
	}

	private void connectNeighbours(LinkedHashSet<Node> neighbourNodes) {
		int firstIndex = 0, secondIndex = 0;
		for (Node mainNode: neighbourNodes) {
			firstIndex++;
			for (Node otherNode: neighbourNodes) {
				secondIndex++;
				if (!mainNode.equals(otherNode) && firstIndex - secondIndex > 0) {
					//Neighbours are added only with one direction
					otherNode.addNeighbour(mainNode, coefficientService.countCoefficients(mainNode, firstIndex, otherNode, secondIndex));
				}
			}
			secondIndex = 0;
		}
	}

	private void runGraphProgressCheckerThread(int size) {
		Runnable runChecking = new GraphProgressChecker(size);
		Thread statusThread = new Thread(runChecking);
		statusThread.start();
	}
}
