package gov.nist.healthcare.iz.darq.digest.service.vocabulary;

import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.digest.domain.ExtractFraction;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.digest.service.impl.LineItemizer;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.DataElement;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SimpleRecordValueAnalysisService implements RecordValueAnalysisService{

	@Autowired
	private LineItemizer itemizer;

	@Override
	public RecordValuesAnalysisResult analyseRecordValues(PreProcessRecord record) throws IllegalAccessException {
		Map<String, ExtractFraction> fieldsExtraction = new HashMap<>();
		Map<String, TablePayload> patientCodes = new HashMap<>();
		Map<String, Map<String, Map<String, TablePayload>>> vaccinationCodes = new HashMap<>();
		RecordValuesAnalysisResult result = new RecordValuesAnalysisResult(fieldsExtraction, patientCodes, vaccinationCodes);

		for(DataElement patientDataElement: itemizer.itemizePatient(record.getRecord().patient)) {
			processFieldExtractPercentage(patientDataElement, fieldsExtraction);
			processPatientCode(patientDataElement, patientCodes);
		}

		for(VaccineRecord vaccineRecord: record.getRecord().history) {
			for(DataElement vaccinationDataElement: itemizer.itemizeVax(vaccineRecord)) {
				processFieldExtractPercentage(vaccinationDataElement, fieldsExtraction);
				processVaccinationCode(vaccinationDataElement, vaccineRecord, record.getAgeGroupAtVaccinationByVaccinationId(), vaccinationCodes);
			}
		}
		return result;
	}

	// Provider, Age Group, Year, Gender, Source, Code, Count
	@Override
	public Map<String, Map<String, Map<String, Map<String, Map<String, TablePayload>>>>> getVaccinationEvents(PreProcessRecord record, DetectionContext context) {
		Map<String, Map<String, Map<String, Map<String, Map<String, TablePayload>>>>> events = new HashMap<>();
		for(VaccineRecord vaccineRecord: record.getRecord().history) {
			String provider = vaccineRecord.reporting_group.getValue();
			String ageGroup = record.getProvidersByVaccinationId().get(vaccineRecord.vax_event_id.getValue());
			String year = String.valueOf(vaccineRecord.administration_date.getValue().getYear());
			String gender = record.getRecord().patient.gender.getValue();
			String source = vaccineRecord.event_information_source.getValue();
			String code = context.getVaccineGroupValue(vaccineRecord.vaccine_type_cvx.getValue());

			Map<String, Map<String, Map<String, Map<String, TablePayload>>>> byAgeGroup = events.computeIfAbsent(provider, (k) -> new HashMap<>());
			Map<String, Map<String, Map<String, TablePayload>>> byYear = byAgeGroup.computeIfAbsent(ageGroup, (k) -> new HashMap<>());
			Map<String, Map<String, TablePayload>> byGender = byYear.computeIfAbsent(year, (k) -> new HashMap<>());
			Map<String, TablePayload> bySource = byGender.computeIfAbsent(gender, (k) -> new HashMap<>());

			TablePayload tablePayload = bySource.computeIfAbsent(source, (k) -> new TablePayload());
			if(tablePayload.getCodes() == null) {
				tablePayload.setCodes(new HashMap<>());
			}

			int count = tablePayload.getCodes().computeIfAbsent(code, (k) -> 0);
			tablePayload.getCodes().put(code, count + 1);
			tablePayload.setTotal(tablePayload.getCodes().values().stream().mapToInt(x -> x).sum());
		}

		return events;
	}

	public void processPatientCode(DataElement code, Map<String, TablePayload> patientCodes) {
		if(code.isCoded() && code.getValue().hasValue()) {
			this.processCodeTable(code, patientCodes);
		}
	}

	public void processVaccinationCode(DataElement code, VaccineRecord record, Map<String, String> ageGroupAtVaccinationByVaccinationId, Map<String, Map<String, Map<String, TablePayload>>> vaccinationCodes) {
		if(code.isCoded() && code.getValue().hasValue()) {
			Map<String, Map<String, TablePayload>> byAgeGroup = vaccinationCodes.computeIfAbsent(record.reporting_group.getValue(), (k) -> new HashMap<>());
			Map<String, TablePayload> byTable = byAgeGroup.computeIfAbsent(ageGroupAtVaccinationByVaccinationId.get(record.vax_event_id.getValue()), (k) -> new HashMap<>());
			this.processCodeTable(code, byTable);
		}
	}

	public void processCodeTable(DataElement code, Map<String, TablePayload> byTable) {
		TablePayload tablePayload = byTable.computeIfAbsent(code.getTable(), (k) -> new TablePayload());
		if(tablePayload.getCodes() == null) {
			tablePayload.setCodes(new HashMap<>());
		}

		int count = tablePayload.getCodes().computeIfAbsent(code.getValue().toString(), (k) -> 0);
		tablePayload.getCodes().put(code.getValue().toString(), count + 1);
		tablePayload.setTotal(tablePayload.getCodes().values().stream().mapToInt(x -> x).sum());
	}

	public void processFieldExtractPercentage(DataElement de, Map<String, ExtractFraction> fieldsExtraction){
		ExtractFraction dataElementExtractPercentage = fieldsExtraction.computeIfAbsent(de.getName(), (key) -> new ExtractFraction());

		if(de.getValue().hasValue()) {
			dataElementExtractPercentage.incValued();
		} else {
			if (de.getValue().placeholder() == null) {
				dataElementExtractPercentage.incEmpty();
			} else {
				switch (de.getValue().placeholder().getCode()) {
					case VALUE_PRESENT: dataElementExtractPercentage.incValuePresent(); break;
					case NOT_EXTRACTED: dataElementExtractPercentage.incNotExtracted(); break;
					case NOT_COLLECTED: dataElementExtractPercentage.incNotCollected(); break;
					case VALUE_NOT_PRESENT: dataElementExtractPercentage.incValueNotPresent(); break;
					case VALUE_LENGTH: dataElementExtractPercentage.incValueLength(); break;
					case EXCLUDED: dataElementExtractPercentage.incExcluded(); break;
					default: dataElementExtractPercentage.incEmpty();
				}
			}
		}
	}
}
