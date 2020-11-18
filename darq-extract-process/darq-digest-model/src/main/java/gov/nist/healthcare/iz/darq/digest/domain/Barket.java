package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Objects;

public class Barket implements Comparable<Barket>{
		public int year;
		public int month;
		public int day;
		
		public Barket() {
			super();
		}
		public Barket(int year, int month, int day) {
			super();
			this.year = year;
			this.month = month;
			this.day = day;
		}
		public int getYear() {
			return year;
		}
		public int getMonth() {
			return month;
		}
		public int getDay() {
			return day;
		}
		
		@Override
		public String toString() {
			String x = part(year, " year") + part(month, " month") + part(day, " day");
			return x.isEmpty() ? "0d" : x;
		}
		
		
		public String part(int i, String type){
			return i != 0 ? i > 1 ? i + type + "s " : i + type + " " : "";
		}
		
		@Override
		public int compareTo(Barket o) {			
			int dec  = year * 10000 + month * 100 + day * 10;
			int odec = o.getYear() * 10000 + o.getMonth() * 100 + o.getDay() * 10;
			return dec - odec;
		}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Barket barket = (Barket) o;
		return year == barket.year &&
				month == barket.month &&
				day == barket.day;
	}

	@Override
	public int hashCode() {
		return Objects.hash(year, month, day);
	}
}