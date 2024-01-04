package gov.nist.healthcare.iz.darq.adf.module.api;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;

import java.io.File;
import java.nio.file.Path;

public interface ADFTransformer {
	ADFVersion getFrom();

	ADFVersion getTo();

	void transform(CryptoKey origin, CryptoKey target, File source, Path destination, boolean replace) throws Exception;

	void transform(CryptoKey target, ADFReader reader, Path destination, boolean replace) throws Exception;
}

