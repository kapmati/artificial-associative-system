package pl.kapmat.model;

import javax.persistence.*;

/**
 * Sentence model
 *
 * Created by Kapmat on 2016-09-21.
 */


@Entity
@Table(name = "sentences")
public class Sentence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private int id;

	@Column(name = "text", nullable = false)
	private String text;

	@Column(name = "language", nullable = false)
	private String language;

	public Sentence() {
	}

	public Sentence(String text, String language) {
		this.text = text;
		this.language = language;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "Sentence{" +
				"id=" + id +
				", text='" + text + '\'' +
				", language='" + language + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Sentence sentence = (Sentence) o;

		if (id != sentence.id) {
			return false;
		}
		if (!text.equals(sentence.text)) {
			return false;
		}
		return language.equals(sentence.language);

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + text.hashCode();
		result = 31 * result + language.hashCode();
		return result;
	}
}
