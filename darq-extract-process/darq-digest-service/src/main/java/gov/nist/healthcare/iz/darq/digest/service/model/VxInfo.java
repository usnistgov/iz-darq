package gov.nist.healthcare.iz.darq.digest.service.model;

public class VxInfo {

    private String provider;
    private String age;
    private String year;
    private String code;
    private String gender;
    private String source;

    public VxInfo(String provider, String age, String code, String gender, String year, String source) {
        super();
        this.provider = provider;
        this.age = age;
        this.code = code;
        this.year = year;
        this.gender = gender;
        this.source = source;
    }
    public String getProvider() {
        return provider;
    }
    public String getAgeGroup() {
        return age;
    }
    public String getCode() {
        return code;
    }
    public String getGender() {
        return gender;
    }
    public String getSource() {
        return source;
    }
    public String getYear() {
        return year;
    }

}
