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
	@Value("#{new java.text.SimpleDateFormat(\"yyyyMMdd\").parse(\"${app.date}\")}")
	private Date date;
	
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
