package pl.kapmat.model;

/**
 * Sentence language
 *
 * @author Mateusz Kaproń
 */
public enum Language {
	PL, ENG;

	public static Language getLanguageByName(String name) {
		switch (name.toUpperCase()) {
			case "PL":
				return Language.PL;
			case "ENG":
				return Language.ENG;
			default:
				return Language.PL;
		}
	}
}
