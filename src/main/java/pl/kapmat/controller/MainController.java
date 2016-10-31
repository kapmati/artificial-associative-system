package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.kapmat.algorithm.AasGraph;
import pl.kapmat.dao.SentenceDAO;
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

//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences_SHORT.txt";
		private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences.txt";
	//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_100K-sentences.txt";
	private static final Language LANGUAGE = Language.PL;

	@RequestMapping("/")
	@ResponseBody
	public String index() {
		aasGraph.run();
		return "Main page - Artificial associative system";
	}

	@RequestMapping("/insertData")
	@ResponseBody
	public String insertData() {
		sentenceService.insertSentences(PATH, Language.PL);
		return "File: " + PATH + "<br>Language: " + Language.PL;
	}
}
