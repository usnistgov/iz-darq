package gov.nist.healthcare.iz.record.generator.vocabulary;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class StateInfo {
    String state;
    ZipCodeRange zip;
    List<String> areaCodes = new ArrayList<>();

    public boolean matchState(String state) {
        return this.state.equals(state);
    }

    public boolean matchZip(int zip) {
        return this.zip.min >= zip && this.zip.max <= zip;
    }

    public boolean matchAreaCode(String phone) {
        return areaCodes.stream().anyMatch((ac) -> phone.startsWith("("+ac+")"));
    }

    public String getRandomZip() {
        int value = RandomUtils.nextInt(zip.min, zip.max + 1);
        return value + "";
    }

    public String getRandomAreaCode() throws Exception {
        if(areaCodes.size() == 0) {
            throw new Exception("No areaCode found for state " + state);
        }
        int at = RandomUtils.nextInt(0, areaCodes.size());
        return areaCodes.get(at);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ZipCodeRange getZip() {
        return zip;
    }

    public void setZip(ZipCodeRange zip) {
        this.zip = zip;
    }

    public List<String> getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodes(List<String> areaCodes) {
        this.areaCodes = areaCodes;
    }
}
