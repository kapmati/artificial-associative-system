package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.kapmat.dao.SentenceDAO;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;

import java.util.List;

/**
 * Sentence controller
 *
 * Created by Kapmat on 2016-10-23.
 */
@Controller
@RequestMapping("/sentence")
public class SentenceController {

	@Autowired
	private SentenceDAO sentenceDAO;

	@RequestMapping("/getSentences")
	@ResponseBody
	public String getSentences() {
		StringBuffer stringBuffer = new StringBuffer();
		sentenceDAO.findAll().forEach(sentence -> stringBuffer.append(sentence.getText()).append("<br>"));
		return stringBuffer.toString();
	}

	@RequestMapping("getSentencesByLanguage/{language}")
	@ResponseBody
	public String getSentencesByLanguage(@PathVariable String language) {
		Language lang = null;
		switch (language.toUpperCase()) {
			case "PL":
				lang = Language.PL;
				break;
			case "ENG":
				lang = Language.ENG;
				break;
		}
		List<Sentence> sentences = sentenceDAO.findByLanguage(lang);
		if (sentences.size() == 0) {
			return "Sentences with '" + language + "' language not found";
		}
		StringBuffer stringBuffer = new StringBuffer();
		sentences.forEach(sentence -> stringBuffer.append(sentence.getText()).append("<br>"));
		return stringBuffer.toString();
	}
}
