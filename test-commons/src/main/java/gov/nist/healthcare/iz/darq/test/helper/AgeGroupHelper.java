package gov.nist.healthcare.iz.darq.test.helper;

import gov.nist.healthcare.iz.darq.digest.domain.Bracket;
import gov.nist.healthcare.iz.darq.digest.domain.Range;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgeGroupHelper {
	LocalDate asOf;
	int nbAgeGroups;
	private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


	public AgeGroupHelper(LocalDate asOf, int nbAgeGroups) {
		this.asOf = asOf;
		this.nbAgeGroups = nbAgeGroups;
	}

	public List<Range> getAgeGroups() {
		List<Range> ageGroups = new ArrayList<>();
		Bracket low = new Bracket(0,0,0);
		for(int i = 0; i < nbAgeGroups; i++) {
			Bracket high = new Bracket((low.getMonth() + 1) / 12, (low.getMonth() + 1) % 12, 0);
			Range range = new Range(low, high);
			ageGroups.add(range);
			low = high;
		}
		return ageGroups;
	}

	public String getDateInAgeGroup(int grp) {
		return getDateInAgeGroupFrom(grp, asOf);
	}

	public String getDateInAgeGroupFrom(int grp, LocalDate from) {
		return DATE_FORMATTER.format(from.minusMonths(grp + 1).plusDays(5));
	}

	public String getDateInAgeGroupFrom(int grp, String from) {
		return getDateInAgeGroupFrom(grp, LocalDate.parse(from, DATE_FORMATTER));
	}

	public String getDateInAgeGroupAt(int grp, LocalDate from) {
		return DATE_FORMATTER.format(from.plusMonths(grp + 1).minusDays(5));
	}

	public String getDateInAgeGroupAt(int grp, String from) {
		return getDateInAgeGroupAt(grp, LocalDate.parse(from, DATE_FORMATTER));
	}

	public LocalDate getAsOf() {
		return asOf;
	}

	public Range getAgeGroup(int nb) {
		return getAgeGroups().get(nb);
	}
}
