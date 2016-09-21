/**
 * Created by Kapmat on 2016-09-21.
 */
package pl.kapmat.model;

public class Sentence {

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Sentence{" +
				"text='" + text + '\'' +
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

		return text.equals(sentence.text);
	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}
}
