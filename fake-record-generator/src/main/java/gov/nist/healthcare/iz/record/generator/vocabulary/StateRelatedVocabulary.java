package gov.nist.healthcare.iz.record.generator.vocabulary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class StateRelatedVocabulary extends Vocabulary {
    List<StateInfo> states = new ArrayList<>();


    public StateRelatedVocabulary() throws IOException {
        super(new HashSet<>(Arrays.asList(
                "ZIPCODE",
                "PHONE",
                "STATE"
        )));
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, ZipCodeRange>> typeRef
                = new TypeReference<HashMap<String, ZipCodeRange>>() {};
        Map<String, ZipCodeRange> values = mapper.readValue(StateRelatedVocabulary.class.getResourceAsStream("/vocabulary/zipcode_by_state.json"), typeRef);
        List<String> areaCodes = IOUtils.readLines(
                NamesVocabulary.class.getResourceAsStream("/vocabulary/area_code_by_state.csv"),
                StandardCharsets.UTF_8);

        Map<String, StateInfo> info = new HashMap<>();
        for(String state: values.keySet()) {
            StateInfo stateInfo = new StateInfo();
            stateInfo.setState(state);
            stateInfo.setZip(values.get(state));
            info.put(state, stateInfo);
        }

        for(String areaCodeLine: areaCodes) {
            String[] split = areaCodeLine.split(",");
            String state = split[1].trim();
            if(info.containsKey(state)) {
                info.get(state).getAreaCodes().add(split[0].trim());
            }
        }

        this.states = new ArrayList<>(info.values());

    }

    @Override
    protected String getRandomFieldValue(String field) throws Exception {
        if(states.size() == 0) {
            throw new Exception("No state info found");
        }
        int at = RandomUtils.nextInt(0, states.size());
        return getValueForField(states.get(at), field);
    }

    @Override
    String getRandomRelatedFieldValue(String field, String relatedTo, String relatedToValue) throws Exception {
        StateInfo match = states.stream().filter((st) -> matchByFieldValue(st, relatedTo, relatedToValue)).findAny().orElse(null);
        if(match != null) {
            return getValueForField(match, field);
        } else {
            throw new Exception("No related value "+ field + " to " + relatedTo + " = " + relatedToValue);
        }
    }

    String getValueForField(StateInfo stateInfo, String field) throws Exception {
        switch (field) {
            case "STATE" :
                return stateInfo.getState();
            case "PHONE" :
                return getPhoneStringValue(stateInfo.getRandomAreaCode());
            case "ZIPCODE" :
                return stateInfo.getRandomZip();
        }
        return "";
    }

    boolean matchByFieldValue(StateInfo stateInfo, String field, String value) {
        switch (field) {
            case "STATE" :
                return stateInfo.matchState(value);
            case "PHONE" :
                return stateInfo.matchAreaCode(value);
            case "ZIPCODE" :
                return stateInfo.matchZip(Integer.parseInt(value));
        }
        return false;
    }

    public String getPhoneStringValue(String areaCode) {
        int numberMiddle = RandomUtils.nextInt(100, 999);
        int numberEnd = RandomUtils.nextInt(1000, 9999);
        return "(" + areaCode + ")" + numberMiddle + numberEnd;
    }
}
