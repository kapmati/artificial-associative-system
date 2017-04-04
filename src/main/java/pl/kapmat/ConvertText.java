package pl.kapmat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mateusz Kaproń
 *         03.04.17
 */
public class ConvertText {

	private static final String PATH = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_1M-sentences.txt";
	private static final String PATH_OUTPUT = System.getProperty("user.dir") + "/src/main/resources/text/pol_newscrawl_2011_1M-sentences";

	public static void main(String[] args) throws IOException {
//		Files.write(Paths.get(PATH_OUTPUT), Files.readAllLines(Paths.get(PATH)).stream().filter(ConvertText::containPolishLetter).collect(Collectors.toList()));

		List<String> inputList = Files.readAllLines(Paths.get(PATH));
		List<String> polishSentences = inputList.stream().filter(ConvertText::containPolishLetter).collect(Collectors.toList());
		int parts = 10;
		for (int i = 0; i < parts; i++) {
			if (i == 9) {
				Files.write(Paths.get(PATH_OUTPUT + "_PART_" + (i + 1) + ".txt"), polishSentences.subList(i * 100000, polishSentences.size()));
			} else {
				Files.write(Paths.get(PATH_OUTPUT + "_PART_" + (i + 1) + ".txt"), polishSentences.subList(i * 100000, (i+1)*100000));
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
