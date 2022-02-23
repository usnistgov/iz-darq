package gov.nist.healthcare.iz.darq.configuration.validation;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import org.immregistries.mqe.validator.detection.MqeCode;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Service
public class ConfigurationPayloadValidator {

    public void validateConfigurationPayload(ConfigurationPayload configurationPayload) throws InvalidConfigurationPayload {
        ArrayList<String> errors = new ArrayList<>();
        errors.addAll(validateAgeGroups(configurationPayload.getAgeGroups(), true));
        errors.addAll(validateDetections(configurationPayload.getDetections()));
        errors.addAll(validateAsOfDate(configurationPayload.getAsOf()));
        errors.addAll(validateVaxCodeAbstraction(configurationPayload.getVaxCodeAbstraction()));

        if(errors.size() > 0) {
            throw new InvalidConfigurationPayload(errors);
        }
    }


    public List<String> validateAgeGroups(List<Range> ageGroups, boolean allowEmptyGroupSameMinAndMax) {
        ArrayList<String> errors = new ArrayList<>();
        // Validate AgeGroups
        if(ageGroups == null) {
            errors.add("Configuration Age Groups is not defined");
            return errors;
        }

        for (int i = 0; i < ageGroups.size(); i++) {
            Range range = ageGroups.get(i);

            if(range.getMin() == null) {
                errors.add("Configuration Age Groups : Min bracket is missing for age group index :" + i);
            }
            if(range.getMax() == null) {
                errors.add("Configuration Age Groups : Max bracket is missing for age group index :" + i);
            }
        }

        if(errors.size() > 0) {
            return errors;
        }

        ageGroups.sort(null);
        Range previous = null;
        for(int i = 0; i < ageGroups.size(); i++) {
            Range range = ageGroups.get(i);

            if(range.getMin().getDay() != 0) {
                errors.add("Configuration Age Groups : Min bracket has a non-zero value for days. Days Value: '"+ range.getMin().getDay() +"' for age group index :" + i);
            }
            if(range.getMax().getDay() != 0) {
                errors.add("Configuration Age Groups : Max bracket has a non-zero value for days. Days Value: '"+ range.getMax().getDay() +"' for age group index :" + i);
            }
            if(range.getMin().getMonthsValue() > range.getMax().getMonthsValue()) {
                errors.add("Configuration Age Groups : Min bracket is greater than Max bracket for range "+ range.toString());
            }
            if(range.getMin().getMonthsValue() == range.getMax().getMonthsValue() && !allowEmptyGroupSameMinAndMax) {
                errors.add("Configuration Age Groups : Min bracket equals Max bracket for range "+ range.toString());
            }
            // Starts with 0years, 0months, 0days
            if(i == 0) {
                if(range.getMin().getMonth() != 0 || range.getMin().getYear() != 0 || range.getMin().getDay() != 0) {
                    errors.add("Configuration Age Groups : Age groups do not start from birth");
                }
            } else {
                if(!range.getMin().equals(previous.getMax())) {
                    errors.add("Configuration Age Groups : Age groups have a gap / overlap between " + previous.getMax() + " and " + range.getMin());
                }
            }

            final int copyI = i;
            if(Stream.iterate(0, j -> j + 1)
                    .limit(ageGroups.size())
                    .anyMatch((j) -> j != copyI && ageGroups.get(j).same(range))) {
                errors.add("Configuration Age Groups : Age groups " + range + " has a duplicate");
            }

            previous = range;
        }

        return errors;
    }

    public List<String> validateDetections(List<String> detections) {
        ArrayList<String> errors = new ArrayList<>();
        if(detections == null) {
            errors.add("Configuration Detections : Detections List is not defined");
            return errors;
        }

        for(String code: detections) {
            try {
                MqeCode.valueOf(code);
            } catch (Exception exception) {
                errors.add("Configuration Detections : Code '" + code + "' is not a valid MQE Code");
            }
        }
        return errors;
    }

    public List<String> validateAsOfDate(String asOf) {
        if(asOf != null && !asOf.isEmpty()) {
            ArrayList<String> errors = new ArrayList<>();
            try {
                (new SimpleDateFormat("MM/dd/yyyy")).parse(asOf);
            } catch (ParseException e) {
                errors.add("Configuration AsOf Date : Invalid AsOf Date '"+ asOf +"', valid format is MM/dd/yyyy");
            }
            return errors;
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> validateVaxCodeAbstraction(Map<String, String> vaxCodeAbstraction) {
        if(vaxCodeAbstraction != null) {
            ArrayList<String> errors = new ArrayList<>();
            boolean containsNullValue = vaxCodeAbstraction.values().stream().anyMatch(Strings::isNullOrEmpty);
            boolean containsNullKey = vaxCodeAbstraction.keySet().stream().anyMatch(Strings::isNullOrEmpty);
            if(containsNullKey) {
                errors.add("Configuration Vaccine Abstraction : Map contains NULL or EMPTY key");
            }
            if(containsNullValue) {
                errors.add("Configuration Vaccine Abstraction : Map contains NULL or EMPTY value");
            }
            return errors;
        } else {
            return Collections.emptyList();
        }
    }
}
