package pl.kapmat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * FileOperator is responsible for reading data from txt file
 *
 * @author Mateusz Kaproń
 */
public class FileOperator {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileOperator.class);

	public List<Sentence> getSentencesFromTxt(String filePath, Language language) {
		LOGGER.info("Read data from: " + filePath);
		try (Stream<String> sentencesStream = Files.lines(Paths.get(filePath))) {
			List<String> sentencesString = sentencesStream.collect(Collectors.toList());
			return IntStream.range(0, sentencesString.size())
					.mapToObj(i -> new Sentence(sentencesString.get(i), language))
					.collect(Collectors.toList());
		} catch (IOException e) {
			LOGGER.error("Error during reading data from: " + filePath);
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
}
