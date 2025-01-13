package gov.nist.healthcare.iz.darq.adf.module.sqlite;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.archive.ADFArchiveManager;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class SqliteADFMerger {

	public void merge(List<SqliteADFReader> files, CryptoKey key, String tmp, ADFVersion version, String target) throws Exception {
		// Start from the largest file (copy) then merge all the other files by adding tables row by row in the largest file
		int largestFileIndex = getLargestFileIndex(files);
		SqliteADFReader largest = files.get(largestFileIndex);
		List<SqliteADFReader> remaining = files.stream().filter((f) -> !f.equals(largest)).collect(Collectors.toList());
		String initial = getInitial(tmp, largest);
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + initial);

		try {
			SqliteADFWriterDAO dao = new SqliteADFWriterDAO(connection);
			Dictionaries dictionaries = largest.getDictionaries();
			SecretKeySpec secret = new SecretKeySpec(largest.readKey(key), "AES");

			dao.getConnection().setAutoCommit(false);
			AtomicInteger commitCounter = new AtomicInteger(0);

			// WRITE DATA
			merge_table("P_DETECTIONS", remaining, (rows, dictionary) -> {
				try {
					this.merge_patient_detections(
							dictionary,
							rows,
							dictionaries,
							dao,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("P_VOCAB", remaining, (rows, dictionary) -> {
				try {
					this.merge_patient_vocab(
							dictionary,
							rows,
							dictionaries,
							dao,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("V_DETECTIONS", remaining, (rows, dictionary) -> {
				try {
					this.merge_provider_detections(
							dictionary,
							rows,
							dictionaries,
							dao,
							false,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("V_VOCAB", remaining, (rows, dictionary) -> {
				try {
					this.merge_provider_vocab(
							dictionary,
							rows,
							dictionaries,
							dao,
							false,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("P_PROVIDER_DETECTIONS", remaining, (rows, dictionary) -> {
				try {
					this.merge_provider_detections(
							dictionary,
							rows,
							dictionaries,
							dao,
							true,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("P_PROVIDER_VOCAB", remaining, (rows, dictionary) -> {
				try {
					this.merge_provider_vocab(
							dictionary,
							rows,
							dictionaries,
							dao,
							true,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("V_EVENTS", remaining, (rows, dictionary) -> {
				try {
					this.merge_vaccination_events(
							dictionary,
							rows,
							dictionaries,
							dao,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});
			merge_table("P_MATCH_SIGNATURE", remaining, (rows, dictionary) -> {
				try {
					this.merge_match_signatures(
							dictionary,
							rows,
							dictionaries,
							dao,
							commitCounter
					);
				} catch (SQLException ignored) {}
			});

			connection.setAutoCommit(true);

			// Merge non-aggregated data
			Summary summary = merge(files.stream().map(ADFReader::getSummary).collect(Collectors.toList()));
			Metadata metadata = merge(files.get(0).getMetadata());
			ConfigurationPayload configuration = files.get(0).getConfigurationPayload();
			summary.getCounts().numberOfProviders = dictionaries.getValues(Field.PROVIDER).size();

			// WRITE METADATA
			connection.createStatement().execute("DELETE FROM METADATA");
			dao.write_metadata(metadata, summary, configuration, version, secret);

			// WRITE DICTIONARIES
			connection.createStatement().execute("DELETE FROM DICTIONARY");
			dao.write_dictionaries(dictionaries, secret);

			// Export file to target location
			connection.createStatement().execute("VACUUM ");
			ADFArchiveManager.getInstance().create(initial, target, version);
		} finally {
			// Cleanup

			if(!connection.isClosed()) {
				connection.close();
			}
			Files.deleteIfExists(Paths.get(initial));
		}
	}

	public int getLargestFileIndex(List<SqliteADFReader> files) throws IOException {
		int largest = 0;
		for(int i = 1; i < files.size(); i++) {
			if(files.get(i).getFileSize() > files.get(largest).getFileSize()) {
				largest = i;
			}
		}
		return largest;
	}

	public String getInitial(String temporaryDirectory, SqliteADFReader largest) throws Exception {
		Path temporary = Paths.get(temporaryDirectory, UUID.randomUUID() + "_merging.db");
		ADFArchiveManager.getInstance().extract(largest.getADFLocation().toString(), temporary.toString());
		return temporary.toAbsolutePath().toString();
	}

	public void commit(Connection connection, AtomicInteger counter) throws SQLException {
		if((counter.get() % 200) == 0) {
			connection.commit();
		}
		counter.incrementAndGet();
	}

	public Summary merge(List<Summary> summaries) {
		Summary summary = summaries.get(0);
		for(int i = 1; i < summaries.size(); i++) {
			summary = Summary.merge(summary, summaries.get(i));
		}
		return summary;
	}

	public Metadata merge(Metadata a) {
		return new Metadata(
				a.getVersion(),
				a.getBuild(),
				a.getMqeVersion(),
				0,
				new Date(),
				a.getInactiveDetections()
		);
	}

	public void merge_table(String table, List<SqliteADFReader> files, BiConsumer<ResultSet, Dictionaries> fn) throws SQLException {
		for(SqliteADFReader file: files) {
			Statement statement  = file.getConnection().createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"';\n");
			if(resultSet.next()) {
				ResultSet tableRows = statement.executeQuery("SELECT * FROM "+ table);
				fn.accept(tableRows, file.getDictionaries());
			}
		}
	}

	public void merge_provider_detections(Dictionaries source, ResultSet rows, Dictionaries target, SqliteADFWriterDAO dao, boolean patient, AtomicInteger counter) throws SQLException {
		while (rows.next()) {
			String provider = source.findValue(Field.PROVIDER, rows.getInt("PROVIDER_ID"));
			String ageGroup = source.findValue(Field.AGE_GROUP, rows.getInt("AGE_GROUP"));
			String code = source.findValue(Field.DETECTION, rows.getInt("DETECTION_CODE"));
			int p = rows.getInt("P");
			int n = rows.getInt("N");
			dao.write_provider_detections(
					target.getId(Field.PROVIDER, provider),
					target.getId(Field.AGE_GROUP, ageGroup),
					target.getId(Field.DETECTION, code),
					p,
					n,
					patient
			);
			commit(dao.getConnection(), counter);
		}
	}

	public void merge_provider_vocab(Dictionaries source, ResultSet rows, Dictionaries target, SqliteADFWriterDAO dao, boolean patient, AtomicInteger counter) throws SQLException {
		while (rows.next()) {
			String provider = source.findValue(Field.PROVIDER, rows.getInt("PROVIDER_ID"));
			String ageGroup = source.findValue(Field.AGE_GROUP, rows.getInt("AGE_GROUP"));
			String code = source.findValue(Field.CODE, rows.getInt("CODE"));
			String table = source.findValue(Field.TABLE, rows.getInt("VS"));
			int nb = rows.getInt("N");
			dao.write_provider_vocab(
					target.getId(Field.PROVIDER, provider),
					target.getId(Field.AGE_GROUP, ageGroup),
					target.getId(Field.TABLE, table),
					target.getId(Field.CODE, code),
					nb,
					patient
			);
			commit(dao.getConnection(), counter);
		}
	}

	private void merge_patient_detections(Dictionaries source, ResultSet rows, Dictionaries target, SqliteADFWriterDAO dao, AtomicInteger counter) throws SQLException {
		while (rows.next()) {
			String ageGroup = source.findValue(Field.AGE_GROUP, rows.getInt("AGE_GROUP"));
			String code = source.findValue(Field.DETECTION, rows.getInt("DETECTION_CODE"));
			int p = rows.getInt("P");
			int n = rows.getInt("N");
			dao.write_p_detections(
					target.getId(Field.AGE_GROUP, ageGroup),
					target.getId(Field.DETECTION, code),
					p,
					n
			);
			commit(dao.getConnection(), counter);
		}
	}

	private void merge_patient_vocab(Dictionaries source, ResultSet rows, Dictionaries target, SqliteADFWriterDAO dao, AtomicInteger counter) throws SQLException {
		while (rows.next()) {
			String ageGroup = source.findValue(Field.AGE_GROUP, rows.getInt("AGE_GROUP"));
			String code = source.findValue(Field.CODE, rows.getInt("CODE"));
			String table = source.findValue(Field.TABLE, rows.getInt("VS"));
			int nb = rows.getInt("N");
			dao.write_p_vocab(
					target.getId(Field.AGE_GROUP, ageGroup),
					target.getId(Field.TABLE, table),
					target.getId(Field.CODE, code),
					nb
			);
			commit(dao.getConnection(), counter);
		}
	}

	private void merge_match_signatures(Dictionaries source, ResultSet rows, Dictionaries target, SqliteADFWriterDAO dao, AtomicInteger counter) throws SQLException {
		while (rows.next()) {
			String signature = source.findValue(Field.MATCH_SIGNATURE, rows.getInt("MATCH_SIGNATURE"));
			int nb = rows.getInt("N");
			dao.write_match_signature(
					target.getId(Field.MATCH_SIGNATURE, signature),
					nb
			);
			commit(dao.getConnection(), counter);
		}
	}

	private void merge_vaccination_events(Dictionaries source, ResultSet rows, Dictionaries target, SqliteADFWriterDAO dao, AtomicInteger counter) throws SQLException {
		while (rows.next()) {
			String provider = source.findValue(Field.PROVIDER, rows.getInt("PROVIDER_ID"));
			String ageGroup = source.findValue(Field.AGE_GROUP, rows.getInt("AGE_GROUP"));
			String code = source.findValue(Field.VACCINE_CODE, rows.getInt("CODE"));
			String year = source.findValue(Field.VACCINATION_YEAR, rows.getInt("YEAR"));
			String gender = source.findValue(Field.GENDER, rows.getInt("GENDER"));
			String event = source.findValue(Field.EVENT, rows.getInt("SOURCE"));
			int nb = rows.getInt("N");
			dao.write_v_events(
					target.getId(Field.PROVIDER, provider),
					target.getId(Field.AGE_GROUP, ageGroup),
					target.getId(Field.VACCINATION_YEAR, year),
					target.getId(Field.GENDER, gender),
					target.getId(Field.EVENT, event),
					target.getId(Field.VACCINE_CODE, code),
					nb
			);
			commit(dao.getConnection(), counter);
		}
	}

}
