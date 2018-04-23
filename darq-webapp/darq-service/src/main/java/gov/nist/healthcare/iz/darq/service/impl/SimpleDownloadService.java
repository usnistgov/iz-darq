package gov.nist.healthcare.iz.darq.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gov.nist.healthcare.iz.darq.model.FileDownload;
import gov.nist.healthcare.iz.darq.service.utils.DownloadService;

public class SimpleDownloadService implements DownloadService {

	private Map<String, FileDownload> map;
	
	public SimpleDownloadService(Map<String, FileDownload> map) {
		super();
		this.map = map;
	}

	@Override
	public InputStream getFile(String id) {
		if(map.containsKey(id)){
			return SimpleDownloadService.class.getResourceAsStream("/"+this.map.get(id).getPath());
		}
		return null;
	}

	@Override
	public List<FileDownload> catalog() {
		List<FileDownload> result = new ArrayList<>();
		for(Entry<String, FileDownload> e : this.map.entrySet()){
			e.getValue().setId(e.getKey());
			result.add(e.getValue());
		}
		return result;
	}

	@Override
	public FileDownload getInfo(String id) {
		return this.map.get(id);
	}

}
