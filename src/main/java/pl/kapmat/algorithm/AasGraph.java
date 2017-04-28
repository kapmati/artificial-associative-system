package pl.kapmat.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.CoefficientService;
import pl.kapmat.service.NodeService;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.GraphProgressChecker;
import pl.kapmat.util.MathUtil;
import pl.kapmat.util.TimeCounter;

import java.util.*;
import java.util.stream.Collectors;

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
	private char[] charsToDelete = {'-', ',', '.', ':', ';', '(', ')', '{', '}', '[', ']', '+', '=', '_', '<', '>', '|',
			'/', '\\', '*', '\'', '?', '"', '!', '«', '↑', '*', '.', '$', '@', '%', '&', '–', '§', '»', '„', '“', '¿',
			'–', '—', '†', '•', '…', '\u2028', '▪', '♦'};
	private static final int THRESHOLD = 10;

	public void createGraph(String path, Language lang, String type) {
		//Load sentences from file
		List<Sentence> sentences = sentenceService.getSentencesAfterCorrection(path, lang, type);

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
		nodeService.serializeSetOfNodes(nodeSet, "test.ser");
		timer.endCount();
		timer.showTime("Serialize graph");

//		timer.startCount();
//		Set<Node> newSet = nodeService.deserializeSetOfNodes("wPustyniBook.ser");
//		timer.endCount();
//		timer.showTime("Deserialize graph");
		System.out.println("NodeSet size:" + nodeSet.size());
	}

	public void extendGraph(List<Sentence> sentences) {
		timer.startCount();
		System.out.println("Reading data... ");
		nodeSet = nodeService.deserializeSetOfNodes("knowledgeSource300.ser");
		timer.endCount();
		timer.showTime("Deserialize graph");

		sentences = sentenceService.deleteChars(sentences, charsToDelete);
		sentenceService.changeNumber(sentences);

		//Enable graph progress tracking
		runGraphProgressCheckerThread(sentences.size());

		System.out.println("START EXTENDING:" + nodeSet.size());
		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime("Extend graph [+" + sentences.size() + " sentences]");

		nodeService.serializeSetOfNodes(nodeSet, "knowledgeSource301.ser");
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
			List<Node> neighbourNodes = new ArrayList<>();
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

	private void connectNeighbours(List<Node> neighbourNodes) {
		int firstIndex = 0, secondIndex = 0;
		for (Node mainNode : neighbourNodes) {
			firstIndex++;
			for (Node otherNode : neighbourNodes) {
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
		if (nodeSet != null && nodeSet.isEmpty()) {
			System.out.println("Reading data... " + graphName);
			timer.startCount();
			nodeSet = nodeService.deserializeSetOfNodes(graphName);
			timer.endCount();
			timer.showTime("Deserialize graph");
		}
	}

	public List<Map<String, Object>> textAnalysis(String inputSentence) {
//
//		List<Map<String, Object>> responseList = new ArrayList<>();
//
//		List<Node> inputNodes = new ArrayList<>();
//		String[] sentences = inputSentence.split("\\n");
//		for (String strSentence : sentences) {
//			Map<String, Object> responseMap = new HashMap<>();
//			responseMap.put("input", strSentence);
//			Sentence sentence = new Sentence(strSentence, Language.PL);
//			sentence = sentenceService.deleteChars(sentence, charsToDelete);
//			String[] words = sentence.getText().split(" ");
//			List<String> notFoundList = new ArrayList<>();
//			List<String> bestNextList = new ArrayList<>();
//			List<String> similarWordList = new ArrayList<>();
//			for (int i = 0; i < words.length; i++) {
//				Node node = new Node(words[i].toUpperCase());
//				String similar;
//				boolean invalidWord = true;
//				if (nodeSet.contains(node)) {
//					Node oldNode = nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get();
//					if (oldNode.getLevel() > THRESHOLD) {
//						inputNodes.add(nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get());
//						invalidWord = false;
//					}
//				}
//				if (invalidWord) {
//					List<Node> similarNodes = nodeService.similarWord(node, nodeSet);
//					similar = null;
//					for (Node n : similarNodes) {
//						if (similar != null) {
//							similar = similar + ", " + n.getWord() + "(" + n.getLevel() + ")";
//						} else {
//							similar = n.getWord() + "(" + n.getLevel() + ")";
//						}
//					}
//					similarWordList.add(similar + "|\n\n");
//					if (i > 0) {
//						Node oldNode = new Node(words[i - 1].toUpperCase());
//						Map<Node, Coefficient> bestNextNodes = nodeService.getBestNeighbours(nodeSet.stream().filter(n -> n.getWord().equals(oldNode.getWord())).findFirst().orElse(new Node("!!!")));
//						for (Map.Entry next : bestNextNodes.entrySet()) {
//							bestNextList.add(((Node) next.getKey()).getWord());
//						}
//						notFoundList.add(words[i]);
//					}
//				}
//			}
//			responseMap.put("similarWords", similarWordList);
//			responseMap.put("notFound", notFoundList);
//
//			responseList.add(responseMap);
//		}
//		return responseList;
		return  null;
	}

	public Map<String, Map<String, Double>> findBetterWords(String inputSentence) {
		Map<String, Map<String, Double>> responseMap = new TreeMap<>();

		List<Node> inputNodes = new ArrayList<>();
		String[] sentences = inputSentence.split("\\n");
		for (String strSentence : sentences) {
			Sentence sentence = new Sentence(strSentence, Language.PL);
			sentence = sentenceService.deleteChars(sentence, charsToDelete);
			String[] words = sentence.getText().split(" ");
			for (int i = 0; i < words.length; i++) {
				List<String> similarWordList = new ArrayList<>();
				Map<String, Double> similarMapList = new LinkedHashMap<>();
				Node node = new Node(words[i].toUpperCase());
				String similar;
				boolean invalidWord = true;
				if (nodeSet.contains(node)) {
					Node oldNode = nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get();
					if (oldNode.getLevel() > THRESHOLD) {
						if (inputNodes.size() == 0 || checkIfContextIsPossible(oldNode, inputNodes.get(inputNodes.size()-1))) {
							inputNodes.add(oldNode);
							invalidWord = false;
						}
					}
				}
				if (invalidWord) {
					List<String> otherWords = new ArrayList<>();
					for (int j = i; j <words.length; j++) {
						Node newNode = new Node(words[j].toUpperCase());
						if (nodeSet.contains(newNode)) {
							otherWords.add(newNode.getWord());
						}
					}
					Map<Node, Double> bestSimilarNodes = new LinkedHashMap<>(nodeService.getBestSimilarWord(inputNodes, node, nodeSet));
					Map<Node, Double> nextWords = new LinkedHashMap<>(nodeService.getBestNextWords(inputNodes, node.getWord()));

					//kontekst z kolejnymi poprawnymi wyrazami
					Map<Node, Double> bestSimilarNodesNext = nodeService.checkNextPartOfContext(bestSimilarNodes, otherWords);
					Map<Node, Double> nextWordsNext = nodeService.checkNextPartOfContext(nextWords, otherWords);

					Map<Node, Double> bestsOfTheBests = new LinkedHashMap<>();
					if (nextWords.size() > 0) {
						for (Map.Entry<Node, Double> entry : bestSimilarNodes.entrySet()) {
							if (nextWords.containsKey(entry.getKey())) {
								bestsOfTheBests.put(entry.getKey(), entry.getValue());
							}
						}
					}

					bestSimilarNodes = sortMapByValue(bestSimilarNodes);
					bestSimilarNodesNext = sortMapByValue(bestSimilarNodesNext);
					nextWordsNext = sortMapByValue(nextWordsNext);
					bestsOfTheBests = sortMapByValue(bestsOfTheBests);

					List<Node> addedNodes = new ArrayList<>();
					for (Map.Entry<Node, Double> entry : bestSimilarNodesNext.entrySet()) {
						addedNodes.add(entry.getKey());
						similarMapList.put(entry.getKey().getWord(), MathUtil.roundDouble(entry.getValue(),4));
						similar = entry.getKey().getWord() + "(" + entry.getKey().getLevel() + ")[" + entry.getValue() + "]";
						similarWordList.add(similar);
					}

					for (Map.Entry<Node, Double> entry : bestsOfTheBests.entrySet()) {
						if (!addedNodes.contains(entry.getKey())) {
							addedNodes.add(entry.getKey());
							similarMapList.put(entry.getKey().getWord(), MathUtil.roundDouble(entry.getValue(),4));
							similar = entry.getKey().getWord() + "(" + entry.getKey().getLevel() + ")[" + entry.getValue() + "]";
							similarWordList.add(similar);
						}
					}

					for (Map.Entry<Node, Double> entry : nextWordsNext.entrySet()) {
						if (!addedNodes.contains(entry.getKey())) {
							addedNodes.add(entry.getKey());
							similarMapList.put(entry.getKey().getWord(), MathUtil.roundDouble(entry.getValue(),4));
							similar = entry.getKey().getWord() + "(" + entry.getKey().getLevel() + ")[" + entry.getValue() + "]";
							similarWordList.add(similar);
						}
					}

					for (Map.Entry<Node, Double> entry : bestSimilarNodes.entrySet()) {
						if (!addedNodes.contains(entry.getKey())) {
							addedNodes.add(entry.getKey());
							similarMapList.put(entry.getKey().getWord(), MathUtil.roundDouble(entry.getValue(),4));
							similar = entry.getKey().getWord() + "(" + entry.getKey().getLevel() + ")[" + entry.getValue() + "]";
							similarWordList.add(similar);
						}
					}

					similarMapList = similarMapList.entrySet().stream()
							.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
								throw new AssertionError();
							}, LinkedHashMap::new));


					responseMap.put(words[i], similarMapList);
				}
			}

		}
		responseMap = responseMap.entrySet().stream()
				.sorted(Collections.reverseOrder())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
					throw new AssertionError();
				}, LinkedHashMap::new));
		return responseMap;
	}

	private boolean checkIfContextIsPossible(Node currentNode, Node previousNode) {
		return previousNode.getNeighbourMap().containsKey(currentNode);
	}

	private Map<Node, Double> sortMapByValue(Map<Node, Double> map) {
		return map.entrySet().stream()
				.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
				.limit(20)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
					throw new AssertionError();
				}, LinkedHashMap::new));
	}
//	public Map<Node, Double> findNextWords(List<Node> inputNodesPart) {
//		Map<Node, Double> responseMap = new HashMap<>();
//
////		String[] sentences = inputSentence.split("\\n");
////		String lastSentence = sentences[sentences.length - 1];
////		Sentence sentence = new Sentence(lastSentence, Language.PL);
////		sentence = sentenceService.deleteChars(sentence, charsToDelete);
////		String[] words = sentence.getText().split(" ");
////		List<Node> nodeList = new ArrayList<>();
////		for (int i = 0; i < words.length - 1; i++) {
////			Node newNode = new Node(words[i]);
////			//TODO Zakładam tymczasowo że wszystkie słowa są poprawne!!
////			//W przypadku wykrycia niepoprawnego słowa trzeba najpierw znaleźć poprawne słowa!!
////			Node node = nodeSet.stream().filter(n -> n.getWord().equals(newNode.getWord().toUpperCase())).findFirst().get();
////			nodeList.add(node);
////		}
//		Map<Node, Double> nextWords = nodeService.getBestNextWords(inputNodesPart);
//		for (Map.Entry<Node, Double> entry : nextWords.entrySet()) {
//			responseList.add(entry.getKey().getWord() + " <" + entry.getValue() + ">");
//		}
//		return responseMap;
//	}

	public Map<String, Object> finishWord(String inputSentence) {
		Map<String, Object> responseMap = new TreeMap<>();

		String[] sentences = inputSentence.split("\\n");
		String lastSentence = sentences[sentences.length - 1];
		Sentence sentence = new Sentence(lastSentence, Language.PL);
		sentence = sentenceService.deleteChars(sentence, charsToDelete);
		String[] words = sentence.getText().split(" ");
		List<Node> nodeList = new ArrayList<>();
		for (int i = 0; i < words.length - 1; i++) {
			Node newNode = new Node(words[i].toUpperCase());
			if (nodeSet.contains(newNode)) {
				Node node = nodeSet.stream().filter(n -> n.getWord().equals(newNode.getWord().toUpperCase())).findFirst().get();
				nodeList.add(node);
			}
		}
		if (nodeList.size() > 0) {
			Map<Node, Double> bestWords = new LinkedHashMap<>(nodeService.getBestNextWordsUsingPart(nodeList, words[words.length - 1]));
			bestWords = bestWords.entrySet().stream()
					.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
					.limit(7)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
						throw new AssertionError();
					}, LinkedHashMap::new));
			Map<String, Double> resultMap = new LinkedHashMap<>();
			for (Map.Entry<Node, Double> entry : bestWords.entrySet()) {
				resultMap.put(entry.getKey().getWord(), entry.getValue());
			}
			responseMap.put("words", resultMap);
		}
		return responseMap;
	}
}
