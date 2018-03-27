package gov.nist.healthcare.iz.darq.digest.domain;
public class Range implements Comparable<Range> {
		public Barket min;
		public Barket max;
		
		public Range() {
			super();
		}
		public Range(Barket min, Barket max) {
			super();
			this.min = min;
			this.max = max;
		}
		public Barket getMin() {
			return min;
		}
		public void setMin(Barket min) {
			this.min = min;
		}
		public Barket getMax() {
			return max;
		}
		public void setMax(Barket max) {
			this.max = max;
		}
		@Override
		public String toString() {
			return  min + " -> " + max;
		}
		
		@Override
		public int compareTo(Range o) {
			return min.compareTo(o.min);
		}
		
		public boolean same(Range r){
			return min.compareTo(r.min) == 0 && max.compareTo(r.max) == 0;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((max == null) ? 0 : max.hashCode());
			result = prime * result + ((min == null) ? 0 : min.hashCode());
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
			Range other = (Range) obj;
			if (max == null) {
				if (other.max != null)
					return false;
			} else if (!max.equals(other.max))
				return false;
			if (min == null) {
				if (other.min != null)
					return false;
			} else if (!min.equals(other.min))
				return false;
			return true;
		}
		
	}