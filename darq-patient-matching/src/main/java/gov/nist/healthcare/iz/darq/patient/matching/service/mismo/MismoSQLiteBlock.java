package gov.nist.healthcare.iz.darq.patient.matching.service.mismo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.darq.patient.matching.model.EndOfBlockException;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientRecord;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlock;
import org.immregistries.mismo.match.model.Patient;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MismoSQLiteBlock implements PatientBlock<Patient> {

  private final ObjectMapper objectMapper;
  private final ResultSet resultSet;

  public MismoSQLiteBlock(ResultSet resultSet, ObjectMapper objectMapper) {
	this.objectMapper = objectMapper;
	this.resultSet = resultSet;
  }

  public Patient patientRecordFromString(String patient) throws JsonProcessingException {
	TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
	return new Patient(this.objectMapper.readValue(patient, typeRef));
  }

  @Override
  public PatientRecord<Patient> next() throws EndOfBlockException {
	try {
	  if(resultSet.next()) {
		String content = resultSet.getString(MismoSQLitePatientBlockHandler.CONTENT);
		String id = resultSet.getString(MismoSQLitePatientBlockHandler.RECORD_ID);
		return new MismoPatientRecord(id, patientRecordFromString(content));
	  } else {
		throw new EndOfBlockException();
	  }
	} catch (SQLException | JsonProcessingException e) {
	  throw new RuntimeException(e);
	}
  }
}
