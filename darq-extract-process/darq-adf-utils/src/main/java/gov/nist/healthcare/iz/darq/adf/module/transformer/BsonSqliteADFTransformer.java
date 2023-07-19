package gov.nist.healthcare.iz.darq.adf.module.transformer;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.ADFManager;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFTransformer;
import gov.nist.healthcare.iz.darq.adf.module.json.BsonADFReader;
import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class BsonSqliteADFTransformer implements ADFTransformer {

	ADFManager manager;

	public BsonSqliteADFTransformer(ADFManager manager) {
		this.manager = manager;
	}

	@Override
	public ADFVersion getFrom() {
		return ADFVersion.ADFBSON000001;
	}

	@Override
	public ADFVersion getTo() {
		return ADFVersion.ADFSQLITE0001;
	}

	@Override
	public void transform(CryptoKey origin, CryptoKey target, File source, Path destination, boolean replace) throws Exception {
		destination.toFile().getParentFile().mkdirs();
		try(
				SqliteADFWriter writer = (SqliteADFWriter) manager.getWriter(getTo(), target);
				BsonADFReader reader = (BsonADFReader) manager.getADFReader(source.getAbsolutePath())
		) {
			writer.open(destination.toFile().getParentFile().getAbsolutePath());
			reader.read(origin);
			transform(writer, reader);
			writer.exportAndClose(destination.toAbsolutePath().toString());
		}
		if(replace) {
			Files.deleteIfExists(source.toPath());
		}
	}

	@Override
	public void transform(CryptoKey target, ADFReader reader, Path destination, boolean replace) throws Exception {
		destination.toFile().getParentFile().mkdirs();
		try(SqliteADFWriter writer = (SqliteADFWriter) manager.getWriter(getTo(), target)) {
			writer.open(destination.toFile().getParentFile().getAbsolutePath());
			transform(writer, (BsonADFReader) reader);
			writer.exportAndClose(destination.toAbsolutePath().toString());
		}
		if(replace) {
			Files.deleteIfExists(reader.getADFLocation());
		}
	}

	public void transform(SqliteADFWriter writer, BsonADFReader reader) throws Exception {
		ADFile file = reader.getADF();
		writer.write_metadata(reader.getMetadata(), file.getConfiguration());
		writer.write(file.getGeneralPatientPayload(), file.getReportingGroupPayload());
		writer.setSummary(file.getSummary());
	}

}
