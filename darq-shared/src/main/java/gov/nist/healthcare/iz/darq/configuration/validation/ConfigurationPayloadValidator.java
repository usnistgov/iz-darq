package gov.nist.healthcare.iz.darq.configuration.validation;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionDescriptor;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetection;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetectionTarget;
import org.immregistries.mismo.match.PatientCompare;
import org.immregistries.mqe.vxu.VxuObject;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConfigurationPayloadValidator {

    Set<String> allowedDetectionCodes = new HashSet<>();

    // Valid detections codes
    {
        AvailableDetectionEngines.ALL_DETECTION_DESCRIPTORS.forEach((detection) -> {
            allowedDetectionCodes.add(detection.getCode());
        });
    }

    public void validateConfigurationPayload(ConfigurationPayload configurationPayload, boolean validateMismoConfiguration) throws InvalidConfigurationPayload {
        ArrayList<String> errors = new ArrayList<>();
        errors.addAll(validateAgeGroups(configurationPayload.getAgeGroups(), true));
        errors.addAll(validateDetections(configurationPayload.getDetections()));
        errors.addAll(validateAsOfDate(configurationPayload.getAsOf()));
        errors.addAll(validateVaxCodeAbstraction(configurationPayload.getVaxCodeAbstraction()));
        if(validateMismoConfiguration) {
            errors.addAll(validateMismoPatientMatcherConfiguration(configurationPayload.getMismoPatientMatchingConfiguration()));
        }
        errors.addAll(validateComplexDetections(configurationPayload.getComplexDetections()));

        if(!errors.isEmpty()) {
            throw new InvalidConfigurationPayload(errors);
        }
    }

    public List<String> validateMismoPatientMatcherConfiguration(String mismoPatientMatcherConfiguration) {
        List<String> issues = new ArrayList<>();
        if(mismoPatientMatcherConfiguration != null && !mismoPatientMatcherConfiguration.isEmpty()) {
            try {
                new PatientCompare(mismoPatientMatcherConfiguration);
            } catch (Exception e) {
                issues.add("MISMO patient matcher configuration is invalid : " + e.getCause().getMessage());
            }
        }
        return issues;
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

        if(!errors.isEmpty()) {
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
            if(!allowedDetectionCodes.contains(code)) {
                errors.add("Configuration Detections : Code '" + code + "' is not a valid Detection Code");
            }
        }
        return errors;
    }

    public List<String> validateAsOfDate(String asOf) {
        if(asOf != null && !asOf.isEmpty()) {
            ArrayList<String> errors = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            try {
                sdf.parse(asOf);
            } catch (ParseException e) {
                errors.add("Configuration AsOf Date : Invalid AsOf Date '"+ asOf +"', valid format is MM/dd/yyyy");
            } catch (Exception e) {
                errors.add("Configuration AsOf Date : Invalid AsOf Date '"+ e.getMessage() +"'");
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

    public List<String> validateComplexDetections(List<ComplexDetection> complexDetections) {
        ArrayList<String> errors = new ArrayList<>();
        if(complexDetections != null) {
            Set<String> codes = new HashSet<>();
            for(ComplexDetection cd: complexDetections) {
                if(cd.getCode() == null || cd.getCode().isEmpty()) {
                    errors.add("Configuration complex detection code is required");
                } else if(!cd.getCode().matches("^[A-Z0-9]+$")) {
                    errors.add("Configuration complex detection code '"+ cd.getCode() +"' is invalid only A to Z and 0 to 9 characters permitted");
                } else if(cd.getCode().length() > 10) {
                    errors.add("Configuration complex detection code '"+ cd.getCode() +"' is too long, only 10 characters permitted");
                } else if(allowedDetectionCodes.contains(cd.getCode())) {
                    errors.add("Configuration complex detection code '"+ cd.getCode() +"' is already in use");
                } else if(codes.contains(cd.getCode())) {
                    errors.add("Configuration complex detection code '"+ cd.getCode() +"' is duplicate");
                } else if(cd.getTarget() == null) {
                    errors.add("Configuration complex detection target is required");
                } else if(cd.getDescription() == null || cd.getDescription().isEmpty()) {
                    errors.add("Configuration complex detection description is required");
                } else {
                    List<String> detectionErrors = cd.getExpression().validate();
                    errors.addAll(detectionErrors.stream().map((error) -> cd.getCode() +" - "+ error)
                                                 .collect(Collectors.toList()));
                    cd.getExpression().getLeafDetectionCodes().forEach((code) -> {
                        if(!allowedDetectionCodes.contains(code)) {
                            errors.add(cd.getCode() +" - Detection '"+ code +"' is not a valid Detection Code");
                        }

                        DetectionDescriptor detection = getDetectionByCode(code);
                        if(detection != null) {
                            if(cd.getTarget().equals(ComplexDetectionTarget.VACCINATION) && !detection.getTarget().equals(VxuObject.VACCINATION.name())) {
                                errors.add(cd.getCode() +" - Detection '"+ code +"' target is '"+detection.getTarget()+"' which is not compatible with '"+ cd.getTarget() +"'");
                            }
                        } else {
                            errors.add(cd.getCode() +" - Detection '"+ code +"' is not a valid Detection Code");
                        }
                    });
                }
                codes.add(cd.getCode());
            }
        }
        return errors;
    }

    DetectionDescriptor getDetectionByCode(String code) {
        return AvailableDetectionEngines.ALL_DETECTION_DESCRIPTORS.stream()
                                                                  .filter((detection) -> detection.getCode().equals(code))
                                                                  .findFirst()
                                                                  .orElse(null);
    }

}
