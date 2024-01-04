package gov.nist.healthcare.iz.darq.adf.module.archive;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipADFArchiveManager implements ADFArchiveManager {
	private static final int BUFFER_SIZE = 1024 * 1000 * 5;

	@Override
	public void create(String sourceFile, String targetFile, ADFVersion tag) throws Exception {
		FileOutputStream fos = new FileOutputStream(targetFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		File fileToZip = new File(sourceFile);
		FileInputStream fis = new FileInputStream(fileToZip);
		BufferedInputStream bis = new BufferedInputStream(fis, BUFFER_SIZE);

		byte[] bytes = new byte[BUFFER_SIZE];
		int length;

		// WRITE ADF
		ZipEntry adf = new ZipEntry("ADF.data");
		zipOut.putNextEntry(adf);
		while((length = bis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		zipOut.closeEntry();

		// WRITE VERSION
		ZipEntry version = new ZipEntry(".version");
		zipOut.putNextEntry(version);
		byte[] tagBytes = (tag.name() + '\0').getBytes();
		zipOut.write(tagBytes, 0, tagBytes.length);
		zipOut.closeEntry();

		zipOut.close();
		bis.close();
		fis.close();
		fos.close();
	}

	@Override
	public void extract(String sourceFile, String targetFile) throws Exception {
		InputStream fis = new BufferedInputStream(Files.newInputStream(Paths.get(sourceFile)), BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		ZipInputStream zis = new ZipInputStream(fis);

		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			if(zipEntry.getName().equals("ADF.data")) {
				BufferedOutputStream fos = new BufferedOutputStream(Files.newOutputStream(Paths.get(targetFile)));
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				zis.closeEntry();
				zis.close();
				fos.close();
				return;
			}
			zipEntry = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		throw new Exception("ADF.data not found in archive");
	}

	@Override
	public byte[] getFileVersion(String file) throws Exception {
		InputStream fis = new BufferedInputStream(Files.newInputStream(Paths.get(file)), BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		ZipInputStream zis = new ZipInputStream(fis);

		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			if(zipEntry.getName().equals(".version")) {
				int length = zis.read(buffer);
				zis.closeEntry();
				zis.close();

				if(length <= 0) {
					throw new Exception("Version not found in archive");
				} else {
					return Arrays.copyOf(buffer, length);
				}
			}
			zipEntry = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		throw new Exception("Version not found in archive");
	}
}
