package gov.nist.healthcare.iz.darq.digest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Bracket implements Comparable<Bracket>{
	private int year;
	private int month;
	// Days were removed from age group computation;
	private int day;

	public Bracket(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public Bracket() {
		super();
	}
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	@Deprecated
	public int getDay() {
		return day;
	}

	@Override
	public String toString() {
		String x = part(year, " year") + part(month, " month");
		return x.isEmpty() ? "birth" : x;
	}

	@JsonIgnore
	public int getMonthsValue() {
		return year * 12 + month;
	}

	public String part(int i, String type){
		return i != 0 ? i > 1 ? i + type + "s " : i + type + " " : "";
	}

	@Override
	public int compareTo(Bracket o) {
		int current  = this.getMonthsValue();
		int other = o.getMonthsValue();
		return current - other;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Bracket bracket = (Bracket) o;
		return this.compareTo(bracket) == 0;
	}

	@Override
	public int hashCode() {
		return this.getMonthsValue();
	}
}