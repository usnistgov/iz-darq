package gov.nist.healthcare.iz.darq.service.utils;

import java.io.InputStream;
import java.util.List;

import gov.nist.healthcare.iz.darq.model.FileDownload;

public interface DownloadService {
	
	public InputStream getFile(String id);
	public FileDownload getInfo(String id);
	public List<FileDownload> catalog();
	

}
