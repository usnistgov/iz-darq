package gov.nist.healthcare.iz.darq.digest.domain;
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + day;
			result = prime * result + month;
			result = prime * result + year;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Barket other = (Barket) obj;
			if (day != other.day)
				return false;
			if (month != other.month)
				return false;
			if (year != other.year)
				return false;
			return true;
		}
		
		
		
		
		
}