package gov.nist.healthcare.iz.darq.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class WebContent {
    public static final String WEB_CONTENT_ID = "WEB_CONTENT";

    @Id
    private String id;
    private HomePage homePage;
    private String registerTermsAndConditions;
    private String uploadTermsAndConditions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HomePage getHomePage() {
        return homePage;
    }

    public void setHomePage(HomePage homePage) {
        this.homePage = homePage;
    }

    public String getRegisterTermsAndConditions() {
        return registerTermsAndConditions;
    }

    public void setRegisterTermsAndConditions(String registerTermsAndConditions) {
        this.registerTermsAndConditions = registerTermsAndConditions;
    }

    public String getUploadTermsAndConditions() {
        return uploadTermsAndConditions;
    }

    public void setUploadTermsAndConditions(String uploadTermsAndConditions) {
        this.uploadTermsAndConditions = uploadTermsAndConditions;
    }

}
