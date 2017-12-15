package gov.nist.healthcare.iz.darq.analysis.stats;

import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.service.StatisticKind;
import gov.nist.healthcare.iz.darq.parser.type.DataUnit;
import gov.nist.healthcare.iz.darq.parser.type.DescriptorType;

public class NotExtractedKind extends StatisticKind {

	@Override
	public String kindId() {
		return "not-extracted";
	}

	@Override
	protected <T> boolean consider(DataUnit<T> dataUnit) {
		return dataUnit.placeholder() != null && dataUnit.placeholder().getCode().equals(DescriptorType.NOT_EXTRACTED);
	}

	@Override
	public List<String> only() {
		return null;
	}

}
