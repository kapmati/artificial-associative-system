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

import java.util.HashMap;
import java.util.Map;

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

	@RequestMapping(value = "/extend", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> extend(@RequestParam("extendBook") String extendBook) {
		aasGraph.extendGraph(sentenceService.getSentencesAfterCorrection(System.getProperty("user.dir") + "/src/main/resources/text/Books/" + extendBook, LANGUAGE));
		return new ResponseEntity<>("Main page - Artificial associative system", HttpStatus.OK);
	}

	@RequestMapping(value = "/run", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> run(@RequestParam("book") String book) {
		aasGraph.run(System.getProperty("user.dir") + "/src/main/resources/text/Books/" + book, LANGUAGE);
		return new ResponseEntity<>("Main page - Artificial associative system", HttpStatus.OK);
	}

	@RequestMapping(value = "/insertData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> insertData() {
		sentenceService.insertSentences(PATH, Language.PL);
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
		aasGraph.readGraph("60k.ser");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
