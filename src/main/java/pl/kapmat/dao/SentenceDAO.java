package pl.kapmat.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.kapmat.model.Language;
import pl.kapmat.model.Sentence;

import java.util.List;

/**
 * Sentence DAO interface
 *
 * @author Mateusz Kaproń
 */
@Transactional
public interface SentenceDAO extends CrudRepository<Sentence, Integer> {

	List<Sentence> findByLanguage(Language language);

	void deleteAll();
}
