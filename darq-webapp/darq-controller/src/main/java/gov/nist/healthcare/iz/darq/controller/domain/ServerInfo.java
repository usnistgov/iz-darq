package gov.nist.healthcare.iz.darq.controller.domain;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

@Component
public class ServerInfo implements ConfigurableService {

	@Value("${app.version}")
	private String version;
	@Value("${app.qualifier}")
	private String qualifier;
	@Value("${app.date}")
	private String date;
	@Autowired
	private Environment environment;
	private String contact;
	private static final String MAILTO_PROPERTY_KEY = "qdar.general.contact.email";

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
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
		return keyValueSet.stream().anyMatch((k) -> k.getKey().equals(MAILTO_PROPERTY_KEY));
	}

	@Override
	public void configure(Properties properties) throws Exception {
		this.contact = properties.getProperty(MAILTO_PROPERTY_KEY);
	}

	@Override
	public Set<ToolConfigurationProperty> initialize() {
		return Collections.singleton(
				new ToolConfigurationProperty(MAILTO_PROPERTY_KEY, this.environment.getProperty(MAILTO_PROPERTY_KEY), true)
		);
	}

	@Override
	public Set<ToolConfigurationKey> getConfigurationKeys() {
		return Collections.singleton(
				new ToolConfigurationKey(MAILTO_PROPERTY_KEY, true)
		);
	}

	@Override
	public OpAck<Void> checkServiceStatus() {
		if(Strings.isNullOrEmpty(this.contact)) {
			return new OpAck<>(OpAck.AckStatus.FAILED, "no contact email set", null, "AART_CONNECT");
		} else if (!this.contact.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")){
			return new OpAck<>(OpAck.AckStatus.FAILED, "contact email not valid", null, "AART_CONNECT");
		}
		return new OpAck<>(OpAck.AckStatus.SUCCESS, "contact email valid", null, "AART_CONNECT");
	}

	@Override
	public String getServiceDisplayName() {
		return "SERVER_INFO";
	}
}
