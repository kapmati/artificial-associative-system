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
//		divideBigFile();
		String s1 = "LUBIŁ";
		String s2 = "dUBIŁ";
		System.out.println(levenshteinDistance(s1, s2));

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

	private static int levenshteinDistance(String s1, String s2) {

		int l1 = s1.length();
		int l2 = s2.length();
		int cost;
		int distanceTable[][] = new int[l1 + 1][l2 + 1];

		for (int i = 0; i <= l1; i++) {
			distanceTable[i][0] = i;
		}

		for (int j = 0; j <= l2; j++) {
			distanceTable[0][j] = j;
		}

		for (int i = 1; i <= l1; i++) {
			for (int j = 1; j <= l2; j++) {
				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					cost = 0;
				} else {
					cost = 1;
				}
				distanceTable[i][j] = Math.min(distanceTable[i - 1][j] + 1, Math.min(distanceTable[i][j - 1] + 1, distanceTable[i - 1][j - 1] + cost));
			}
		}
		return distanceTable[l1][l2];
	}
}
