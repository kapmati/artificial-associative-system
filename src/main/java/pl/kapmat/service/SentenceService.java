package pl.kapmat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import pl.kapmat.dao.SentenceDAO;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;
import pl.kapmat.util.FileOperator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Sentence service
 *
 * Created by Kapmat on 2016-09-24.
 */
@Service
@ComponentScan(basePackages = "pl.kapmat.dao")
public class SentenceService {

	private SentenceDAO sentenceDAO;

	public void setDAO(SentenceDAO sentenceDAO) {
		this.sentenceDAO = sentenceDAO;
	}

	private void insertSentencesIntoDatabase(List<Sentence> sentences) {
		sentenceDAO.save(sentences);
	}

	public List<Sentence> getAllSentences() {
		return (List<Sentence>) sentenceDAO.findAll();
	}

	public void insertSentences(String path, Language lang) {
		FileOperator fileOperator = new FileOperator();
		List<Sentence> sentenceList = fileOperator.getSentencesFromTxt(path, lang);

		sentenceList = deleteLastCharacter(sentenceList);
		sentenceList = deleteSentencePartBeforeChar(sentenceList, '\t');

		insertSentencesIntoDatabase(sentenceList);
	}

	public List<Sentence> replaceCharacter(List<Sentence> sentences, char oldChar, char newChar) {
		List<String> sentencesList = sentences.stream()
				.map(s -> s.getText().replace(oldChar, newChar))
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
