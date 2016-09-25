package pl.kapmat.algorithm;

import pl.kapmat.model.Sentence;
import pl.kapmat.service.SentenceService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Artificial associative system main class
 *
 * Created by Kapmat on 2016-09-25.
 */
public class AasGraph {

	private SentenceService sentenceService = new SentenceService();
	private Set<Node> nodeSet = new HashSet<>();

	public void run() {
		//Load sentences from db
		List<Sentence> sentences = sentenceService.getAllSentences();

		//Delete unnecessary characters
		sentences = sentenceService.replaceCharacter(sentences, ',', ' ');
		sentences = sentenceService.replaceCharacter(sentences, '-', ' ');
		sentences = sentenceService.replaceCharacter(sentences, '.', ' ');

		buildGraph(sentences);
	}

	/*TODO
		1) Usunięcie słowa ""
		2) Podstawienie specjalnego słowa za liczbę lub ujednolicenie liczb
		3) Sprawdzić czy wstawiane są jedynie unikatowe węzły i czy ich liczba się zgadza
	*/
	private void buildGraph(List<Sentence> sentences) {
		String[] words;
		for (Sentence sentence: sentences) {
			words = sentence.getText().split(" ");
			Node singleNode;
			for (String word: words) {
				singleNode = new Node(word);

				//Check if word is new
				if (!nodeSet.contains(singleNode)) {
					nodeSet.add(singleNode);
				} else {
					nodeSet.stream()
							.filter(x -> x.getWord().equals(word))
							.findFirst()
							.get()
							.increaseLevel();
				}
			}
		}
	}

}
