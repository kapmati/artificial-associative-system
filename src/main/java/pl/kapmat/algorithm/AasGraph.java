package pl.kapmat.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import pl.kapmat.dao.SentenceDAO;
import pl.kapmat.model.Sentence;
import pl.kapmat.service.SentenceService;
import pl.kapmat.util.TimeCounter;

import java.util.*;

/**
 * Artificial associative system main class
 * <p>
 * Created by Kapmat on 2016-09-25.
 */
public class AasGraph {

	private Set<Node> nodeSet = new HashSet<>();
	private TimeCounter timer = new TimeCounter();

	public void run(SentenceService sentenceService) {
		//Load sentences from db
		List<Sentence> sentences = sentenceService.getAllSentences();

		//Delete unnecessary characters
		sentences = sentenceService.replaceCharacter(sentences, ',', ' ');
		sentences = sentenceService.replaceCharacter(sentences, '-', ' ');
		sentences = sentenceService.replaceCharacter(sentences, '.', ' ');

		timer.startCount();
		buildGraph(sentences);
		timer.endCount();
		timer.showTime();
	}

	/*TODO
		2) Podstawienie specjalnego słowa za liczbę lub ujednolicenie liczb
		3) Sprawdzić czy wstawiane są jedynie unikatowe węzły i czy ich liczba się zgadza
	*/
	private void buildGraph(List<Sentence> sentences) {
		String[] words;
		for (Sentence sentence : sentences) {
			words = sentence.getText().split(" ");
			Node singleNode;
			Set<Node> neighbourNodes = new LinkedHashSet<>();
			for (String word : words) {
				if (!word.equals("")) {
					singleNode = new Node(word.toUpperCase());
					//Check if word is new
					if (!nodeSet.contains(singleNode)) {
						nodeSet.add(singleNode);
					} else {
						singleNode = nodeSet.stream()
								.filter(x -> x.getWord().equals(word.toUpperCase()))
								.findFirst()
								.get();
						singleNode.increaseLevel();
					}
					neighbourNodes.add(singleNode);
				}
			}
			connectNeighbours(neighbourNodes);
		}
	}

	private void connectNeighbours(Set<Node> neighbourNodes) {
		List<Node> nodesList = new ArrayList<>(neighbourNodes);
		for (Node mainNode : nodesList) {
			for (Node otherNode : nodesList) {
				if (!mainNode.equals(otherNode)) {
					mainNode.addNeighbour(otherNode);
				}
			}
		}

	}
}
