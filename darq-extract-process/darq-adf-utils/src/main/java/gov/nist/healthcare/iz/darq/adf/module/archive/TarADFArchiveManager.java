package gov.nist.healthcare.iz.darq.adf.module.archive;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class TarADFArchiveManager implements ADFArchiveManager {
	private static final int BUFFER_SIZE = 1024 * 1000 * 5;

	@Override
	public void create(String sourceFile, String targetFile, ADFVersion tag) throws Exception {
		try (OutputStream fOut = Files.newOutputStream(Paths.get(targetFile));
		     BufferedOutputStream buffOut = new BufferedOutputStream(fOut, BUFFER_SIZE);
		     TarArchiveOutputStream tOut = new TarArchiveOutputStream(buffOut)) {

			Path sourcePath = Paths.get(sourceFile);
			TarArchiveEntry adf = new TarArchiveEntry("ADF.data");
			adf.setSize(Files.size(sourcePath));
			tOut.putArchiveEntry(adf);
			Files.copy(sourcePath, tOut);
			tOut.closeArchiveEntry();

			byte[] versionBytes = (tag.name() + '\0').getBytes();
			TarArchiveEntry version = new TarArchiveEntry(".version");
			version.setSize(versionBytes.length);
			tOut.putArchiveEntry(version);
			IOUtils.copy(new ByteArrayInputStream(versionBytes), tOut);
			tOut.closeArchiveEntry();

			tOut.finish();
		}
	}

	@Override
	public void extract(String sourceFile, String targetFile) throws Exception {
		try (InputStream fi = Files.newInputStream(Paths.get(sourceFile));
		     BufferedInputStream bi = new BufferedInputStream(fi, BUFFER_SIZE);
		     TarArchiveInputStream ti = new TarArchiveInputStream(bi)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			ArchiveEntry entry;
			while ((entry = ti.getNextEntry()) != null) {
				if(entry.getName().equals("ADF.data")) {
					BufferedOutputStream fos = new BufferedOutputStream(Files.newOutputStream(Paths.get(targetFile)), BUFFER_SIZE);
					int len;
					while ((len = ti.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					return;
				}
			}
		}
		throw new Exception("ADF.data not found in archive");
	}

	@Override
	public byte[] getFileVersion(String file) throws Exception {
		try (InputStream fi = Files.newInputStream(Paths.get(file));
		     BufferedInputStream bi = new BufferedInputStream(fi, BUFFER_SIZE);
		     TarArchiveInputStream ti = new TarArchiveInputStream(bi)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			ArchiveEntry entry;
			while ((entry = ti.getNextEntry()) != null) {
				if(entry.getName().equals(".version")) {
					int length = ti.read(buffer);
					ti.close();
					if(length <= 0) {
						throw new Exception("Version not found in archive");
					} else {
						return Arrays.copyOf(buffer, length);
					}
				}
			}
		}
		throw new Exception("version not found in archive");
	}
}
