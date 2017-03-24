package pl.kapmat.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.CoefficientService;
import pl.kapmat.service.NodeService;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.GraphProgressChecker;
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

	@Autowired
	private CoefficientService coefficientService;

	private Set<Node> nodeSet = new HashSet<>();
	private TimeCounter timer = new TimeCounter();
	private char[] charsToDelete = {'-', ',', '.', ':', ';', '(', ')', '{', '}', '[', ']', '+', '=', '_', '<', '>', '|', '/', '\\', '*', '\'', '?', '"', '!','«','↑','*','.'};

	public void run(String path, Language lang) {
		//Load sentences from file
		List<Sentence> sentences = sentenceService.getSentencesAfterCorrection(path, lang);

		//Delete unnecessary characters
		sentences = sentenceService.deleteChars(sentences, charsToDelete);
		sentenceService.changeNumber(sentences);

		//Enable graph progress tracking
		runGraphProgressCheckerThread(sentences.size());

		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime("Build graph");

		timer.startCount();
		nodeService.serializeSetOfNodes(nodeSet, "books.ser");
		timer.endCount();
		timer.showTime("Serialize graph");

		timer.startCount();
		Set<Node> newSet = nodeService.deserializeSetOfNodes("books.ser");
		timer.endCount();
		timer.showTime("Deserialize graph");
		System.out.println("NodeSet size:" + nodeSet.size());
	}

	public void extendGraph(List<Sentence> sentences) {
		timer.startCount();
		nodeSet = nodeService.deserializeSetOfNodes("books.ser");
		timer.endCount();
		timer.showTime("Deserialize graph");

		sentences = sentenceService.deleteChars(sentences, charsToDelete);
		sentenceService.changeNumber(sentences);

		//Enable graph progress tracking
		runGraphProgressCheckerThread(sentences.size());

		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime("Extend graph [+" + sentences.size() + " sentences]");

		nodeService.serializeSetOfNodes(nodeSet, "books.ser");
		System.out.println("NodeSet size:" + nodeSet.size());
	}

	private void buildGraph(List<Sentence> sentences) {
		String[] words;
		for (Sentence sentence : sentences) {
			if (GraphProgressChecker.breakLoop) {
				break;
			}
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
		GraphProgressChecker.breakLoop = false;
	}

	public int readTest() {
		timer.startCount();
		Set<Node> newSet = nodeService.deserializeSetOfNodes("graph.ser");
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

	public void readGraph(String graphName) {
		if (nodeSet.isEmpty()) {
			timer.startCount();
			nodeSet = nodeService.deserializeSetOfNodes(graphName);
			timer.endCount();
			timer.showTime("Deserialize graph");
		}
	}

	public List<Map<String, Object>> textAnalysis(String inputSentence) {

		List<Map<String, Object>> responseList = new ArrayList<>();

		List<Node> inputNodes = new ArrayList<>();
		String[] sentences = inputSentence.split("\\n");
		for (String strSentence : sentences) {
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("input", strSentence);
			Sentence sentence = new Sentence(strSentence, Language.PL);
			sentence = sentenceService.deleteChars(sentence, charsToDelete);
			String[] words = sentence.getText().split(" ");
			List<String> notFoundList = new ArrayList<>();
			List<String> bestNextList = new ArrayList<>();
			List<String> similarWordList = new ArrayList<>();
			for (int i=0; i<words.length; i++) {
				Node node = new Node(words[i].toUpperCase());

				if (nodeSet.contains(node)) {
					inputNodes.add(nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get());
				} else {
					List<Node> similarNodes = nodeService.similarWord(node, nodeSet);
					String similar = null;
					for (Node n : similarNodes) {
						if (similar != null) {
							similar = similar + ", " + n.getWord();
						} else {
							similar = n.getWord();
						}
					}
					similarWordList.add(similar + "|\n\n");
					if (i > 0) {
						Node oldNode = new Node(words[i-1].toUpperCase());
						Map<Node, Coefficient> bestNextNodes = nodeService.getBestNeighbours(nodeSet.stream().filter(n -> n.getWord().equals(oldNode.getWord())).findFirst().orElse(new Node("!!!")));
						for (Map.Entry next : bestNextNodes.entrySet()) {
							bestNextList.add(((Node)next.getKey()).getWord());
						}
						notFoundList.add(words[i]);
					}
				}
			}
			responseMap.put("similarWords", similarWordList);
			responseMap.put("notFound", notFoundList);
			responseMap.put("bestNeighbour", bestNextList);

			responseList.add(responseMap);
		}
		if (inputNodes.size() > 0) {
			//TODO Tylko testowanie metod
			Map<Node, Coefficient> bestNextNodes = nodeService.getBestNeighbours(inputNodes.get(0));
			Map<Node, Double> bestJointNodes = nodeService.getBestJointNeighbours(inputNodes);
		}

		return responseList;
	}
}
