package pl.kapmat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mateusz Kaproń
 *         03.04.17
 */
public class ConvertText {

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/allBooks.txt";
	private static final String PATH_OUTPUT = System.getProperty("user.dir") + "/src/main/resources/text/allBooks";
	private static final String BOOK_DIRECTORY_PATH = System.getProperty("user.dir") + "/src/main/resources/text/Books";
	private static final String ALL_BOOKS = System.getProperty("user.dir") + "/src/main/resources/text/allBooks.txt";

	public static void main(String[] args) throws IOException {
//		Files.write(Paths.get(PATH_OUTPUT), Files.readAllLines(Paths.get(PATH)).stream().filter(ConvertText::containPolishLetter).collect(Collectors.toList()));
//		mergeFiles();
		divideBigFile();
	}

	public static void mergeFiles() throws IOException {
		List<String> inputList = new ArrayList<>();
		try(Stream<Path> paths = Files.walk(Paths.get(BOOK_DIRECTORY_PATH))) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					try {
						inputList.addAll(Files.readAllLines(filePath));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		Files.write(Paths.get(ALL_BOOKS), inputList.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList()));
	}

	public static void divideBigFile() throws IOException {
		List<String> inputList = Files.readAllLines(Paths.get(PATH));
//		List<String> polishSentences = inputList.stream().filter(ConvertText::containPolishLetter).collect(Collectors.toList());
		int parts = 11;
		for (int i = 0; i < parts; i++) {
			if (i == 10) {
				Files.write(Paths.get(PATH_OUTPUT + "_PART_" + (i + 1) + ".txt"), inputList.subList(i * 5000, inputList.size()));
			} else {
				Files.write(Paths.get(PATH_OUTPUT + "_PART_" + (i + 1) + ".txt"), inputList.subList(i * 5000, (i+1)*5000));
			}
		}
	}

	public static boolean containPolishLetter(String sentence) {
		CharSequence ą = "ą";
		CharSequence ć = "ć";
		CharSequence ę = "ę";
		CharSequence ł = "ł";
		CharSequence ó = "ó";
		CharSequence ś = "ś";
		CharSequence ź = "ź";

		return sentence.contains(ą) || sentence.contains(ć) || sentence.contains(ę) || sentence.contains(ł) ||
				sentence.contains(ó) || sentence.contains(ś) || sentence.contains(ź);
	}
}
