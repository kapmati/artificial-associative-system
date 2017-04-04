package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.kapmat.algorithm.AasGraph;
import pl.kapmat.model.Language;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.GraphProgressChecker;
import pl.kapmat.util.MathUtil;

import java.util.*;

/**
 * Main controller
 *
 * @author Mateusz Kaproń
 */
@Controller
public class MainController {

	@Autowired
	private SentenceService sentenceService;

	@Autowired
	private AasGraph aasGraph;

	@Autowired
	public MainController(SentenceService sentenceService, AasGraph aasGraph) {
		this.sentenceService = sentenceService;
		this.aasGraph = aasGraph;
	}

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/Books/W_pustyni_i_w_puszczy.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/Books/Opowieść_wigilijna_(1925)-całość.txt";

	//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences_SHORT.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_100K-sentences_SHORT.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_100K-sentences.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/test.txt";
	private static final Language LANGUAGE = Language.PL;

	@RequestMapping(value = "/extendGraph", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> extendGraph(@RequestBody String fileName) {
		String type = "s";
		aasGraph.extendGraph(sentenceService.getSentencesAfterCorrection(System.getProperty("user.dir") + "/src/main/resources/text/" + fileName, LANGUAGE, type));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/createGraph", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> createGraph(@RequestBody String fileName) {
		String textType = "book";
		if (textType.equalsIgnoreCase("book")) {
			aasGraph.createGraph(System.getProperty("user.dir") + "/src/main/resources/text/Books/" + fileName, LANGUAGE, textType);
		} else {
			aasGraph.createGraph(System.getProperty("user.dir") + "/src/main/resources/text/" + fileName, LANGUAGE, textType);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/insertData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> insertData() {
		sentenceService.insertSentences(PATH, Language.PL, "book");
		return new ResponseEntity<>("File: " + PATH + "<br>Language: " + Language.PL, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAllSentences", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> deleteAllSentences() {
		sentenceService.deleteAllSentences();
		return new ResponseEntity<>("Clear", HttpStatus.OK);
	}

	@RequestMapping(value = "/textAnalysis", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> textAnalysis(@RequestBody String inputText) throws InterruptedException {
//		aasGraph.readGraph("60k.ser");
		return new ResponseEntity<>(aasGraph.textAnalysis(inputText), HttpStatus.OK);
	}

	@RequestMapping(value = "/finishWord", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> finishWord(@RequestBody String inputText) throws InterruptedException {
//		aasGraph.readGraph("60k.ser");
		return new ResponseEntity<>(aasGraph.finishWord(inputText), HttpStatus.OK);
	}

	@RequestMapping(value = "/nextWord", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> nextWord(@RequestBody String inputText) throws InterruptedException {
		Map<String, Double> resultMap = (Map<String, Double>) aasGraph.finishWord(inputText).get("words");
		List<Map<String, String>> resultList = new ArrayList<>();
		for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
			Map<String, String> nextWordsMap = new LinkedHashMap<>();
			nextWordsMap.put("name", entry.getKey().toLowerCase());
			nextWordsMap.put("coeff", "[" + MathUtil.roundDouble(entry.getValue(), 4) + "]");
			resultList.add(nextWordsMap);
		}
		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}

	@RequestMapping(value = "/wordsChecking", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> wordsChecking(@RequestBody String inputText) throws InterruptedException {
//		aasGraph.readGraph("60k.ser");
		return new ResponseEntity<>(aasGraph.findBetterWords(inputText), HttpStatus.OK);
	}

	@RequestMapping(value = "/breakExtending", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> breakExtending() throws InterruptedException {
		GraphProgressChecker.breakLoop = true;
		Map<String, String> response = new HashMap<>();
		response.put("message", "Extending interrupted successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//	@CrossOrigin(origins = "http://localhost:63342")
	@RequestMapping(value = "/loadGraph", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> loadGraph(@RequestBody String fileName) throws InterruptedException {
		System.out.println(fileName);
		aasGraph.readGraph(fileName);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
