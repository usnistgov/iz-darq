package gov.nist.healthcare.iz.darq.digest.service.model;

public class Issue {
    private String code;
    private boolean vaccine;
    private String provider;
    private String age;
    private boolean positive;

    public Issue(String code, boolean vaccine, String provider, String age, boolean positive) {
        super();
        this.code = code;
        this.vaccine = vaccine;
        this.provider = provider;
        this.age = age;
        this.positive = positive;
    }
    public String getCode() {
        return code;
    }
    public boolean isVaccine() {
        return vaccine;
    }
    public String getProvider() {
        return provider;
    }
    public String getAgeGroup() {
        return age;
    }
    public boolean isPositive() {
        return positive;
    }
    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Issue other = (Issue) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }
}
