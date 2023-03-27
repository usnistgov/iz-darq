package gov.nist.healthcare.iz.darq.patient.matching.service.mismo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlock;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlockHandler;
import org.apache.commons.codec.language.Soundex;
import org.immregistries.mismo.match.model.Patient;

import java.io.IOException;
import java.sql.*;

public class MismoSQLitePatientBlockHandler implements PatientBlockHandler<Patient> {

  public static final String RECORD_ID = "RECORD_ID";
  public static final String CONTENT = "CONTENT";
  public static final String FIRSTNAME_SOUNDEX = "FN";
  public static final String LASTNAME_SOUNDEX = "LN";
  private final Soundex soundex = new Soundex();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private String directory;
  private final String TABLE = "PATIENTS";
  private final String DB_FILE = "PM.db";
  Connection connection;
  private PreparedStatement INSERT_STATEMENT;
  private PreparedStatement FIND_STATEMENT;

  public MismoSQLitePatientBlockHandler() {}

  @Override
  public void initialize(String path) throws SQLException {
    this.directory = path;
    connection = DriverManager.getConnection("jdbc:sqlite:"+ directory +"/" + DB_FILE);
    // --- Create table
    Statement statement = connection.createStatement();
    statement.executeQuery("PRAGMA journal_mode=OFF");
    statement.executeUpdate(String.format("DROP TABLE IF EXISTS %s", TABLE));
    statement.executeUpdate(String.format("CREATE TABLE %s (%s string, %s string, %s string, %s string)", TABLE, RECORD_ID, CONTENT, FIRSTNAME_SOUNDEX, LASTNAME_SOUNDEX));
    statement.executeUpdate(String.format("CREATE INDEX FN_INDEX ON %s(%s)", TABLE, FIRSTNAME_SOUNDEX));
    statement.executeUpdate(String.format("CREATE INDEX LN_INDEX ON %s(%s)", TABLE, LASTNAME_SOUNDEX));

    this.INSERT_STATEMENT =  connection.prepareStatement(String.format("INSERT INTO %s VALUES(?, ?, ?, ?)", TABLE));
    this.FIND_STATEMENT = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s= ? AND %s= ?", TABLE, FIRSTNAME_SOUNDEX, LASTNAME_SOUNDEX));
  }

  @Override
  public void close() throws Exception {
    this.connection.close();
  }

  @Override
  public void store(String id, Patient patient) throws IOException, SQLException {
    INSERT_STATEMENT.setString(1, id);
    INSERT_STATEMENT.setString(2, patientRecordToString(patient));
    INSERT_STATEMENT.setString(3, getFirstNameSoundex(patient));
    INSERT_STATEMENT.setString(4, getLastNameSoundex(patient));
    INSERT_STATEMENT.executeUpdate();
  }

  String getFirstNameSoundex(Patient patient) {
    return soundex.soundex(normalize(patient.getNameFirst()));
  }

  String getLastNameSoundex(Patient patient) {
    return soundex.soundex(normalize(patient.getNameLast()));
  }

  public String normalize(String value) {
    return value == null ? "" : value;
  }

  public String patientRecordToString(Patient patient) throws JsonProcessingException {
    return this.objectMapper.writeValueAsString(patient.getValueMap());
  }

  @Override
  public PatientBlock<Patient> getCandidates(Patient patient) throws Exception {
    FIND_STATEMENT.setString(1, getFirstNameSoundex(patient));
    FIND_STATEMENT.setString(2, getLastNameSoundex(patient));
    ResultSet resultSet = FIND_STATEMENT.executeQuery();
    return new MismoSQLiteBlock(resultSet, this.objectMapper);
  }
}
