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
	private static final int THRESHOLD = 1;

	public void run(String path, Language lang, String type) {
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
		nodeService.serializeSetOfNodes(nodeSet, "knowledgeSource.ser");
		timer.endCount();
		timer.showTime("Serialize graph");

		timer.startCount();
		Set<Node> newSet = nodeService.deserializeSetOfNodes("knowledgeSource.ser");
		timer.endCount();
		timer.showTime("Deserialize graph");
		System.out.println("NodeSet size:" + nodeSet.size());
	}

	public void extendGraph(List<Sentence> sentences) {
		timer.startCount();
		nodeSet = nodeService.deserializeSetOfNodes("booksNew.ser");
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

		nodeService.serializeSetOfNodes(nodeSet, "booksNew.ser");
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
		if (nodeSet.isEmpty()) {
			System.out.println("Reading data...");
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
			for (int i = 0; i < words.length; i++) {
				Node node = new Node(words[i].toUpperCase());
				String similar;
				boolean invalidWord = true;
				if (nodeSet.contains(node)) {
					Node oldNode = nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get();
					if (oldNode.getLevel() > THRESHOLD) {
						inputNodes.add(nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get());
						invalidWord = false;
					}
				}
				if (invalidWord) {
					List<Node> similarNodes = nodeService.similarWord(node, nodeSet);
					similar = null;
					for (Node n : similarNodes) {
						if (similar != null) {
							similar = similar + ", " + n.getWord() + "(" + n.getLevel() + ")";
						} else {
							similar = n.getWord() + "(" + n.getLevel() + ")";
						}
					}
					similarWordList.add(similar + "|\n\n");
					if (i > 0) {
						Node oldNode = new Node(words[i - 1].toUpperCase());
						Map<Node, Coefficient> bestNextNodes = nodeService.getBestNeighbours(nodeSet.stream().filter(n -> n.getWord().equals(oldNode.getWord())).findFirst().orElse(new Node("!!!")));
						for (Map.Entry next : bestNextNodes.entrySet()) {
							bestNextList.add(((Node) next.getKey()).getWord());
						}
						notFoundList.add(words[i]);
					}
				}
			}
			responseMap.put("similarWords", similarWordList);
			responseMap.put("notFound", notFoundList);

			responseList.add(responseMap);
		}
		return responseList;
	}

	public Map<String, List<String>> findBetterWords(String inputSentence) {
		Map<String, List<String>> responseMap = new HashMap<>();

		List<Node> inputNodes = new ArrayList<>();
		String[] sentences = inputSentence.split("\\n");
		for (String strSentence : sentences) {
			Sentence sentence = new Sentence(strSentence, Language.PL);
			sentence = sentenceService.deleteChars(sentence, charsToDelete);
			String[] words = sentence.getText().split(" ");
			for (int i = 0; i < words.length; i++) {
				List<String> similarWordList = new ArrayList<>();
				Node node = new Node(words[i].toUpperCase());
				String similar;
				boolean invalidWord = true;
				if (nodeSet.contains(node)) {
					Node oldNode = nodeSet.stream().filter(n -> n.getWord().equals(node.getWord())).findFirst().get();
					if (oldNode.getLevel() > THRESHOLD) {
						inputNodes.add(oldNode);
						invalidWord = false;
					}
				}
				if (invalidWord) {
					List<Node> similarNodes = nodeService.similarWord(node, nodeSet);
					Map<Node, Double> nextWords = new LinkedHashMap<>(nodeService.getBestNextWords(inputNodes));
					nextWords = nextWords.entrySet().stream()
							.sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y)-> {throw new AssertionError();}, LinkedHashMap::new));
					if (nextWords.size() > 0) {
						for (Node singleNode : similarNodes) {
							if (nextWords.containsKey(singleNode)) {
								similar = singleNode.getWord() + "(" + singleNode.getLevel() + ")[" + nextWords.get(singleNode) + "]";
								similarWordList.add(similar);
							}
						}
					}
					if (similarWordList.size() < 10) {
						for (int j = 0; j < ((similarNodes.size() < 10) ? similarNodes.size() : 10); j++) {
							similar = similarNodes.get(j).getWord() + "(" + similarNodes.get(j).getLevel() + ")[" + nextWords.get(similarNodes.get(j)) + "]*";
							similarWordList.add(similar);
						}
					}
					responseMap.put(words[i], similarWordList);
				}
			}

		}
		return responseMap;
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
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y)-> {throw new AssertionError();}, LinkedHashMap::new));
			Map<String, Double> resultMap = new LinkedHashMap<>();
			for (Map.Entry<Node, Double> entry : bestWords.entrySet()) {
				resultMap.put(entry.getKey().getWord(), entry.getValue());
			}
			responseMap.put("words", resultMap);
		}
		return responseMap;
	}
}
