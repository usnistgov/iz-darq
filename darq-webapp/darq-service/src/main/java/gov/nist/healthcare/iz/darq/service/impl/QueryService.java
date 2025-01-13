package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.model.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class QueryService {

    public void assertQueryIsValid(Query query) throws Exception {
        List<String> issues = new ArrayList<>();

        if(Strings.isNullOrEmpty(query.getName())) {
            issues.add("Query name is required");
        }

        if(query.getConfiguration() == null) {
            issues.add("Query configuration is required");
        }

        if(query.getQuery() == null) {
            issues.add("Query content is required");
        }

        if(!issues.isEmpty()) {
            throw new Exception("Invalid query : " + String.join(", ", issues));
        }
    }

    public List<String> validateQuery(QueryPayload queryPayload, ConfigurationPayload configurationPayload) {
        List<String> issues = new ArrayList<>();

        Set<DataSelector> filters =  queryPayload.getFilterFields();
        for(DataSelector selector: filters) {
            for(ValueContainer container: selector.getValues()) {
                if(!this.validateField(selector.getField(), container.getValue(), configurationPayload)){
                    issues.add(this.getFieldErrorMessage(selector.getField(), container.getValue(), "(Data Selector)"));
                }
            }
        }

        return issues;
    }

    public boolean validateField(Field f, String value, ConfigurationPayload configurationPayload) {
        switch (f) {
            case AGE_GROUP:
                if(!Strings.isNullOrEmpty(value)) {
                    try {
                        int idx = Integer.parseInt(value.replace("g", ""));
                        if(idx >= 0 && idx < configurationPayload.getAgeGroups().size()) {
                            return true;
                        }
                    } catch (Exception ignored) {
                        return false;
                    }
                }
                return false;
            case DETECTION:
                if(!Strings.isNullOrEmpty(value)) {
                    return configurationPayload.getAllDetectionCodes().contains(value);
                }
                return false;
        }
        return true;
    }

    public String getFieldErrorMessage(Field f, String value, String qualifier) {
        return "Field " + f + " has an invalid value " + value + "("+ qualifier +")";
    }

}
