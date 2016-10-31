package pl.kapmat.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * TimeCounter
 *
 * @author Mateusz Kaproń
 */
public class TimeCounter {

	private LocalDateTime start;
	private LocalDateTime end;

	public LocalDateTime startCount() {
		this.end = LocalDateTime.now();
		return this.start = LocalDateTime.now();
	}

	public LocalDateTime endCount() {
		return this.end = LocalDateTime.now();
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void showTime() {

		LocalDateTime tempDateTime = LocalDateTime.from(start);

		long years = tempDateTime.until(end, ChronoUnit.YEARS);
		tempDateTime = tempDateTime.plusYears(years);

		long months = tempDateTime.until(end, ChronoUnit.MONTHS);
		tempDateTime = tempDateTime.plusMonths(months);

		long days = tempDateTime.until(end, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays(days);

		long hours = tempDateTime.until(end, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours(hours);

		long minutes = tempDateTime.until(end, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes(minutes);

		long seconds = tempDateTime.until(end, ChronoUnit.SECONDS);
		tempDateTime = tempDateTime.plusSeconds(seconds);

		long miliSeconds = tempDateTime.until(end, ChronoUnit.MILLIS);

		System.out.println( years + " years " +
				months + " months " +
				days + " days " +
				hours + " hours " +
				minutes + " minutes " +
				seconds + " seconds " +
				miliSeconds + " milisecond.");
	}
}
