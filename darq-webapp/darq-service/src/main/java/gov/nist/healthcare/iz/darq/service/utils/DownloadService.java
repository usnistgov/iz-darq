package gov.nist.healthcare.iz.darq.service.utils;
import java.io.InputStream;
import java.util.List;

import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.model.FileDescriptor;
import gov.nist.healthcare.iz.darq.model.FileDescriptorWrapper;
import gov.nist.healthcare.iz.darq.model.qDARJarFile;

public interface DownloadService extends ConfigurableService {

	qDARJarFile getJarFileInfo();
	InputStream getFile(String id);
	FileDescriptor getInfo(String id);
	List<FileDescriptorWrapper> catalog();

}
