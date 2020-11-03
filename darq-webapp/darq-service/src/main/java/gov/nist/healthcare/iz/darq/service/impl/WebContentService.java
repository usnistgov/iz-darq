package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.model.HomePage;
import gov.nist.healthcare.iz.darq.model.WebContent;
import gov.nist.healthcare.iz.darq.repository.WebContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebContentService {
    @Autowired
    private WebContentRepository webContentRepository;

    public boolean exists() {
        return this.webContentRepository.exists(WebContent.WEB_CONTENT_ID);
    }

    public WebContent getWebContent() {
        WebContent webContent = this.webContentRepository.findOne(WebContent.WEB_CONTENT_ID);
        if(webContent == null) {
            webContent = new WebContent();
            this.save(webContent);
        }
        return webContent;
    }

    public WebContent save(WebContent webContent) {
        webContent.setId(WebContent.WEB_CONTENT_ID);
        return this.webContentRepository.save(webContent);
    }

    public void setHomeSections(HomePage homepage) {
        WebContent webContent = this.getWebContent();
        webContent.setHomePage(homepage);
        this.save(webContent);
    }

}
