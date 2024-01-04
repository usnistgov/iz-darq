package gov.nist.healthcare.iz.darq.digest.common;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class SQLiteADFTestUtils {

	public void printTable(String table, SqliteADFReader reader) throws SQLException {
		ResultSet resultSet = reader.getConnection().createStatement().executeQuery("SELECT * from " + table);
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (resultSet.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1) System.out.print(",  ");
				String columnValue = resultSet.getString(i);
				System.out.print(columnValue + " " + rsmd.getColumnName(i));
			}
			System.out.println();
		}
	}

	public void checkTableContent(PreparedStatement statement, PreparedStatement count, List<List<Integer>> rows) throws Exception {
		for (List<Integer> row : rows) {
			for (int j = 0; j < row.size(); j++) {
				statement.setInt(j + 1, row.get(j));
			}
			if (!exists(statement.executeQuery())) {
				throw new Exception("Row " + row.toString() + " does not exist");
			}
		}
		if(size(count.executeQuery()) != rows.size()) {
			throw new Exception("Table size is different than "+ rows.size());
		}
	}

	public boolean exists(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return rs.getBoolean(1);
		}
		return false;
	}

	public int size(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public SqliteADFReader readADF(CryptoKey cryptoKey, TemporaryFolder folder) throws Exception {
		String ADF = Paths.get(folder.getRoot().getAbsolutePath(), "darq-analysis", "ADF.data").toAbsolutePath().toString();
		String root = folder.getRoot().getAbsolutePath();
		SqliteADFReader reader = new SqliteADFReader(ADF, root);
		reader.read(cryptoKey);
		return reader;
	}
}
