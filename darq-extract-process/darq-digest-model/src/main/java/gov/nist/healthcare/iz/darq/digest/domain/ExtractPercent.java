package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Objects;

public class ExtractPercent {
	public double valued;
	public double excluded;
	public double notCollected;
	public double notExtracted;
	public double valuePresent;
	public double valueNotPresent;
	public double valueLength;
	public double empty;
	public double total;

	public ExtractPercent(ExtractFraction fraction) {
		this.valued = ((double) fraction.getValued() / fraction.getTotal()) * 100;
		this.excluded = ((double) fraction.getExcluded() / fraction.getTotal()) * 100;
		this.notCollected = ((double) fraction.getNotCollected() / fraction.getTotal()) * 100;
		this.notExtracted = ((double) fraction.getNotExtracted() / fraction.getTotal()) * 100;
		this.valuePresent = ((double) fraction.getValuePresent() / fraction.getTotal()) * 100;
		this.valueNotPresent = ((double) fraction.getValueNotPresent() / fraction.getTotal()) * 100;
		this.valueLength = ((double) fraction.getValueLength() / fraction.getTotal()) * 100;
		this.empty = ((double) fraction.getEmpty() / fraction.getTotal()) * 100;
		this.total = fraction.getTotal();
	}

	public static ExtractPercent merge(ExtractPercent source, ExtractPercent target) {
		ExtractPercent result = new ExtractPercent();
		result.valued = (source.valued + target.valued) / 2.0;
		result.excluded = (source.excluded + target.excluded) / 2.0;
		result.notCollected = (source.notCollected + target.notCollected) / 2.0;
		result.notExtracted = (source.notExtracted + target.notExtracted) / 2.0;
		result.valuePresent = (source.valuePresent + target.valuePresent) / 2.0;
		result.valueNotPresent = (source.valueNotPresent + target.valueNotPresent) / 2.0;
		result.valueLength = (source.valueLength + target.valueLength) / 2.0;
		result.empty = (source.empty + target.empty) / 2.0;
		result.total = source.total + target.total;
		return result;
	}

	public ExtractPercent() {
		super();
	}

	public double getValued() {
		return valued;
	}

	public void setValued(double valued) {
		this.valued = valued;
	}

	public double getExcluded() {
		return excluded;
	}

	public void setExcluded(double excluded) {
		this.excluded = excluded;
	}

	public double getNotCollected() {
		return notCollected;
	}

	public void setNotCollected(double notCollected) {
		this.notCollected = notCollected;
	}

	public double getNotExtracted() {
		return notExtracted;
	}

	public void setNotExtracted(double notExtracted) {
		this.notExtracted = notExtracted;
	}

	public double getValuePresent() {
		return valuePresent;
	}

	public void setValuePresent(double valuePresent) {
		this.valuePresent = valuePresent;
	}

	public double getValueNotPresent() {
		return valueNotPresent;
	}

	public void setValueNotPresent(double valueNotPresent) {
		this.valueNotPresent = valueNotPresent;
	}

	public double getValueLength() {
		return valueLength;
	}

	public void setValueLength(double valueLength) {
		this.valueLength = valueLength;
	}

	public double getEmpty() {
		return empty;
	}

	public void setEmpty(double empty) {
		this.empty = empty;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExtractPercent that = (ExtractPercent) o;
		return Double.compare(that.valued, valued) == 0 && Double.compare(that.excluded, excluded) == 0 && Double.compare(that.notCollected, notCollected) == 0 && Double.compare(that.notExtracted, notExtracted) == 0 && Double.compare(that.valuePresent, valuePresent) == 0 && Double.compare(that.valueNotPresent, valueNotPresent) == 0 && Double.compare(that.valueLength, valueLength) == 0 && Double.compare(that.empty, empty) == 0 && Double.compare(that.total, total) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valued, excluded, notCollected, notExtracted, valuePresent, valueNotPresent, valueLength, empty, total);
	}

	@Override
	public String toString() {
		return "ExtractPercent{" +
				"valued=" + valued +
				", excluded=" + excluded +
				", notCollected=" + notCollected +
				", notExtracted=" + notExtracted +
				", valuePresent=" + valuePresent +
				", valueNotPresent=" + valueNotPresent +
				", valueLength=" + valueLength +
				", empty=" + empty +
				", total=" + total +
				'}';
	}
}
