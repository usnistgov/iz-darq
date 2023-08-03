package gov.nist.healthcare.iz.darq.test.helper;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtractBuilder {
	List<RecordBuilder> records = new ArrayList<>();
	AgeGroupHelper ageGroupHelper;

	public ExtractBuilder(AgeGroupHelper ageGroupHelper) {
		this.ageGroupHelper = ageGroupHelper;
	}

	public RecordBuilder withRecord() {
		RecordBuilder builder = new RecordBuilder(this, ageGroupHelper);
		records.add(builder);
		return builder;
	}

	public void writeTo(Writer patients, Writer vaccinations) throws IOException {
		for(RecordBuilder builder: records) {
			Record record = builder.get();
			patients.write(record.patient);
			patients.write("\n");
			for(String vaccination: record.vaccinations) {
				vaccinations.write(vaccination);
				vaccinations.write("\n");
			}
		}
		patients.close();
		vaccinations.close();
	}

	public Set<Record> get() {
		return this.records.stream().map(RecordBuilder::get).collect(Collectors.toSet());
	}
}
