package gov.nist.healthcare.iz.darq.controller.route;

import com.google.common.base.Strings;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.auth.aart.JWTAuthenticationAARTFilter;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aart")
public class AARTConnectErrorController implements ConfigurableService {
    
    String maito;
    @Autowired
    Environment environment;
    private static final String MAILTO_PROPERTY_KEY = "qdar.general.contact.email";

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String error(@RequestParam("token") String token, HttpServletRequest request) {
        try {
            Exception failure = (Exception) request.getAttribute(JWTAuthenticationAARTFilter.AART_CONNECT_EXCEPTION);
            String body = getResourceFileAsString("/templates/aart-error.html");
            Map<String, String> params = new HashMap<>();
            params.put("token", token);
            params.put("message", failure != null ? failure.getMessage() : "");
            params.put("mailto", maito);
            Template template = new Template("name", new StringReader(body), new Configuration());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Reads given resource file as a string.
     *
     * @param fileName path to the resource file
     * @return the file's contents
     * @throws IOException if read fails for any reason
     */
    static String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream is = AARTConnectErrorController.class.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    @Override
    public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
        return keyValueSet.stream().anyMatch((k) -> k.getKey().equals(MAILTO_PROPERTY_KEY));
    }

    @Override
    public void configure(Properties properties) throws Exception {
        this.maito = properties.getProperty(MAILTO_PROPERTY_KEY);
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
        if(Strings.isNullOrEmpty(maito)) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "no contact email set", null, "AART_CONNECT");
        } else if (!maito.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")){
            return new OpAck<>(OpAck.AckStatus.FAILED, "contact email not valid", null, "AART_CONNECT");
        }
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "contact email valid", null, "AART_CONNECT");
    }

    @Override
    public String getServiceDisplayName() {
        return "AART_CONNECT_ERROR";
    }
}
