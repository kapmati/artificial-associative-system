package pl.kapmat.service;

import pl.kapmat.dao.Dao;
import pl.kapmat.model.Sentence;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Sentence service
 *
 * Created by Kapmat on 2016-09-24.
 */
public class SentenceService {

	private final Dao dao = new Dao();

	public void insertSentencesIntoDatabase(List<Sentence> sentences) {
		dao.saveList(sentences);
	}

	public List<Sentence> getAllSentences() {
		return dao.getALL(Sentence.class);
	}

	public List<Sentence> replaceCharacter(List<Sentence> sentences, char newChar, char oldChar) {
		List<String> sentencesList = sentences.stream()
				.map(s -> s.getText().replace(newChar, oldChar))
				.collect(Collectors.toList());
		return IntStream.range(0, sentences.size())
				.mapToObj(i -> new Sentence(sentencesList.get(i), sentences.get(0).getLanguage()))
				.collect(Collectors.toList());
	}

	public List<Sentence> deleteSentencePartBeforeChar(List<Sentence> sentences, char startChar) {
		List<String> sentencesList = sentences.stream()
				.map(s -> s.getText().substring(s.getText().indexOf(startChar)+1))
				.collect(Collectors.toList());
		return IntStream.range(0, sentences.size())
				.mapToObj(i -> new Sentence(sentencesList.get(i), sentences.get(0).getLanguage()))
				.collect(Collectors.toList());
	}

	public List<Sentence> deleteLastCharacter(List<Sentence> sentences) {
		List<String> sentencesList = sentences.stream()
				.map(s -> s.getText().substring(0, s.getText().length()-1))
				.collect(Collectors.toList());
		return IntStream.range(0, sentences.size())
				.mapToObj(i -> new Sentence(sentencesList.get(i), sentences.get(0).getLanguage()))
				.collect(Collectors.toList());
	}
}
