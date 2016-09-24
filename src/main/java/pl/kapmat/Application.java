package pl.kapmat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.HibernateUtil;
import pl.kapmat.util.InsertSentenceWorker;

import java.util.List;

/**
 * Main class
 *
 * Created by Kapmat on 2016-09-21.
 */

public class Application {

	private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences.txt";
	private static final Language LANGUAGE = Language.PL;

	public static void main(String[] args) {

		InsertSentenceWorker.insertSentences();

		SentenceService sentenceService = new SentenceService();
		List<Sentence> sentences = sentenceService.getAllSentences();

		//Probably there is a bug in a Hibernate lib 4.x and it's necessary to close session at the end of main method
		HibernateUtil.getSessionFactory().close();
	}
}
