package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.kapmat.dao.SentenceDAO;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;

import java.util.List;

/**
 * Sentence controller
 *
 * @author Mateusz Kaproń
 */
@Controller
@RequestMapping("/sentence")
public class SentenceController {

	private SentenceDAO sentenceDAO;

	@Autowired
	public SentenceController(SentenceDAO sentenceDAO) {
		this.sentenceDAO = sentenceDAO;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getSentences() {
		StringBuffer stringBuffer = new StringBuffer();
		sentenceDAO.findAll().forEach(sentence -> stringBuffer.append(sentence.getText()).append("<br>"));
		return new ResponseEntity<>(stringBuffer, HttpStatus.OK);
	}

	@RequestMapping(value = "/language/{language}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getSentencesByLanguage(@PathVariable String language) {
		Language lang = Language.getLanguageByName(language);
		List<Sentence> sentences = sentenceDAO.findByLanguage(lang);
		if (sentences.size() == 0) {
			return new ResponseEntity<>("Sentences with '" + language + "' language not found", HttpStatus.NO_CONTENT);
		}
		StringBuffer stringBuffer = new StringBuffer();
		sentences.forEach(sentence -> stringBuffer.append(sentence.getId()).append(".").append(sentence.getText()).append("<br>"));
		return new ResponseEntity<>(stringBuffer, HttpStatus.OK);
	}
}
