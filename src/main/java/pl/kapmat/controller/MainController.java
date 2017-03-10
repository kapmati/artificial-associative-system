package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.kapmat.algorithm.AasGraph;
import pl.kapmat.model.Language;
import pl.kapmat.service.SentenceService;

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

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/monkeyTest.txt";
	//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences_SHORT.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_100K-sentences_SHORT.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_100K-sentences.txt";
//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/test.txt";
	private static final Language LANGUAGE = Language.PL;

	@RequestMapping(value = "/extend", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> extend() {
		aasGraph.extendGraph(sentenceService.getSentencesAfterCorrection(PATH, LANGUAGE));
		return new ResponseEntity<>("Main page - Artificial associative system", HttpStatus.OK);
	}

	@RequestMapping(value = "/run", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> run() {
		aasGraph.run();
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

	@RequestMapping(value = "/context", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> correctConext() {
		//TODO przerobić endpointa
		String input = "I have a dog";
		aasGraph.correctContext(input);
		return new ResponseEntity<>("Contex", HttpStatus.OK);
	}
}
