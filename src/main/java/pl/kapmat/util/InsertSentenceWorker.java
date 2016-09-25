package pl.kapmat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.SentenceService;

import java.util.List;

/**
 *
 * <p>
 * Created by Kapmat on 2016-09-24.
 */
public class InsertSentenceWorker {

	private final Logger LOGGER = LoggerFactory.getLogger(InsertSentenceWorker.class);

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences_SHORT.txt";
	private static final Language LANGUAGE = Language.PL;

	public static void insertSentences() {

		FileOperator fileOperator = new FileOperator();
		List<Sentence> sentenceList = fileOperator.getSentencesFromTxt(PATH, LANGUAGE);

		SentenceService sentenceService = new SentenceService();
		sentenceList = sentenceService.deleteLastCharacter(sentenceList);
		sentenceList = sentenceService.deleteSentencePartBeforeChar(sentenceList, '\t');

		sentenceService.insertSentencesIntoDatabase(sentenceList);
	}
}
