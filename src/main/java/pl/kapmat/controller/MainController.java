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
 * Created by Kapmat on 2016-10-23.
 */
@Controller
public class MainController {

	@Autowired
	private SentenceDAO sentenceDAO;

	@Autowired
	private SentenceService sentenceService;

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences_SHORT.txt";
	//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences.txt";
	//	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_100K-sentences.txt";
	private static final Language LANGUAGE = Language.PL;

	@RequestMapping("/")
	@ResponseBody
	public String index() {
		AasGraph aasGraph = new AasGraph();
		sentenceService.setDAO(sentenceDAO);
		aasGraph.run(sentenceService);
		return "Main page - Artificial associative system";
	}

	@RequestMapping("/insertData/{file}/{lang}")
	@ResponseBody
	public String insertData(@PathVariable String file, @PathVariable String lang) {
		sentenceService.insertSentences(file, Language.getLanguageByName(lang));
		return "File: " + file + "<br>Language: " + lang;
	}
}
