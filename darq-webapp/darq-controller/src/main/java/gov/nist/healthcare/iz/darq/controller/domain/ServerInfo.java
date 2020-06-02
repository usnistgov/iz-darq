package gov.nist.healthcare.iz.darq.controller.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerInfo {

	@Value("${app.version}")
	private String version;
	@Value("${app.qualifier}")
	private String qualifier;
	@Value("${app.date}")
	private String date;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
