package gov.nist.healthcare.iz.darq.patient.matching.service.mismo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlock;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlockHandler;
import org.apache.commons.codec.language.Soundex;
import org.immregistries.mismo.match.StringUtils;
import org.immregistries.mismo.match.model.Patient;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MismoSQLitePatientBlockHandler implements PatientBlockHandler<Patient> {

  public static final String RECORD_ID = "RECORD_ID";
  public static final String CONTENT = "CONTENT";
  public static final String FIRSTNAME_INITIAL = "FNI";
  public static final String LASTNAME_INITIAL = "LNI";
  public static final String BIRTH_YEAR = "BY";
  public static final String PHONE = "PH";
  public static final String LASTNAME_SOUNDEX = "LNS";
  public static final String ZIP = "ZIP";
  public static final String[] BLOCK_FIELDS = {
          FIRSTNAME_INITIAL,
          LASTNAME_INITIAL,
          BIRTH_YEAR,
          PHONE,
          LASTNAME_SOUNDEX,
          ZIP
  };
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
    statement.executeUpdate(
            String.format("CREATE TABLE %s ("+ String.join(",", Collections.nCopies(BLOCK_FIELDS.length + 2, "%s string")) +")",
              TABLE,
              RECORD_ID,
              CONTENT,
              FIRSTNAME_INITIAL,
              LASTNAME_INITIAL,
              BIRTH_YEAR,
              PHONE,
              LASTNAME_SOUNDEX,
              ZIP
            )
    );

    for(String FIELD: BLOCK_FIELDS) {
      statement.executeUpdate(String.format("CREATE INDEX %s ON %s(%s)", FIELD + "_INDEX", TABLE, FIELD));
    }

    this.INSERT_STATEMENT =  connection.prepareStatement(String.format("INSERT INTO %s VALUES("+ String.join(", ", Collections.nCopies(BLOCK_FIELDS.length + 2, "?")) +")", TABLE));
    String INITIALS_BIRTH_YEAR = String.format(" %s= ? AND %s= ? AND %s= ?", FIRSTNAME_INITIAL, LASTNAME_INITIAL, BIRTH_YEAR);
    String PHONE_NUMBER = String.format(" %s= ? ", PHONE);
    String LASTNAME_ZIP = String.format(" %s= ? AND %s= ?", LASTNAME_SOUNDEX, ZIP);
    this.FIND_STATEMENT = connection.prepareStatement(String.format("SELECT * FROM %s WHERE (%s) OR (%s) OR (%s)", TABLE, INITIALS_BIRTH_YEAR, PHONE_NUMBER, LASTNAME_ZIP));
  }

  @Override
  public void close() throws Exception {
    this.connection.close();
  }

  @Override
  public void store(String id, Patient patient) throws IOException, SQLException {
    INSERT_STATEMENT.setString(1, id);
    INSERT_STATEMENT.setString(2, patientRecordToString(patient));
    INSERT_STATEMENT.setString(3, getFirstNameInitial(patient));
    INSERT_STATEMENT.setString(4, getLastNameInitial(patient));
    INSERT_STATEMENT.setString(5, getBirthYear(patient));
    INSERT_STATEMENT.setString(6, getPhoneNumber(patient));
    INSERT_STATEMENT.setString(7, getLastNameSoundex(patient));
    INSERT_STATEMENT.setString(8, getZip(patient));
    INSERT_STATEMENT.executeUpdate();
  }

  String getFirstNameInitial(Patient patient) {
    return (String.valueOf(patient.getNameFirst().charAt(0))).toUpperCase();
  }

  String getLastNameInitial(Patient patient) {
    return (String.valueOf(patient.getNameLast().charAt(0))).toUpperCase();
  }

  String getBirthYear(Patient patient) {
    return patient.getBirthDate().substring(Math.max(0, patient.getBirthDate().length() - 4));
  }

  String getPhoneNumber(Patient patient) {
    return StringUtils.isEmpty(patient.getPhone()) ? random() : patient.getPhone();
  }

  String random() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();

    return random.ints(leftLimit, rightLimit + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
  }

  String getZip(Patient patient) {
    return patient.getAddressZip();
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
    FIND_STATEMENT.setString(1, getFirstNameInitial(patient));
    FIND_STATEMENT.setString(2, getLastNameInitial(patient));
    FIND_STATEMENT.setString(3, getBirthYear(patient));
    FIND_STATEMENT.setString(4, getPhoneNumber(patient));
    FIND_STATEMENT.setString(5, getLastNameSoundex(patient));
    FIND_STATEMENT.setString(6, getZip(patient));
    ResultSet resultSet = FIND_STATEMENT.executeQuery();
    return new MismoSQLiteBlock(resultSet, this.objectMapper);
  }
}
