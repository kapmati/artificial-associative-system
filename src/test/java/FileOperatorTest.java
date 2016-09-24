import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;
import pl.kapmat.util.FileOperator;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * FileOperator test
 *
 * Created by Kapmat on 2016-09-24.
 */
public class FileOperatorTest {

	private final Logger LOGGER = LoggerFactory.getLogger(FileOperatorTest.class);

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_news_2007_10K-sentences.txt";
	private static final Language LANGUAGE = Language.PL;

	@Test
	public void readDataFromTxtTest() {
		FileOperator fileOperator = new FileOperator();
		List<Sentence> sentenceList = fileOperator.getSentencesFromTxt(PATH, LANGUAGE);
		assertTrue(sentenceList.size()>0);
	}
}
