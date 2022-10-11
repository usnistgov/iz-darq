package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.security.CustomSecurityExpressionRoot;
import gov.nist.healthcare.iz.darq.controller.domain.QuerySaveRequest;
import gov.nist.healthcare.iz.darq.controller.service.DescriptorService;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.model.Query;
import gov.nist.healthcare.iz.darq.model.QueryDescriptor;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.repository.QueryRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.impl.QueryService;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    @Autowired
    private ConfigurationService configService;
    @Autowired
    private QueryRepository queryRepository;
    @Autowired
    private DigestConfigurationRepository confRepo;
    @Autowired
    private DescriptorService descriptorService;
    @Autowired
    private QueryService queryService;

    List<QueryDescriptor> getDescriptors(List<Query> queries, String userId) {
        List<QueryDescriptor> result = new ArrayList<>();
        List<DigestConfiguration> configurations = this.confRepo.findAccessibleTo(userId);
        for(Query q : queries){
            result.add(this.descriptorService.getQueryDescriptor(q, this.configService.compatibilities(q.getConfiguration(), configurations)));
        }
        return result;
    }

    // Get All Accessible Queries
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<QueryDescriptor> all(
            @AuthenticationPrincipal User user) {
        List<Query> queries = queryRepository.findByOwnerId(user.getId());
        return this.getDescriptors(queries, user.getId());
    }

    // Get All Accessible Queries Compatible with a Configuration
    @RequestMapping(value = "/for-configuration", method = RequestMethod.POST)
    @ResponseBody
    public List<QueryDescriptor> getQueriesForConfiguration(
            @AuthenticationPrincipal User user,
            @RequestBody ConfigurationPayload configurationPayload) {
        List<Query> queries = queryRepository.findByOwnerId(user.getId()).stream()
                .filter(q -> this.configService.compatible(configurationPayload, q.getConfiguration()))
                .collect(Collectors.toList());
        return this.getDescriptors(queries, user.getId());
    }

    //  Save Query
    @RequestMapping(value="/", method=RequestMethod.POST)
    @ResponseBody
    @PreAuthorize(
            "#query.id != null ? " +
            "AccessResource(#request, QUERY, EDIT, #query.id) : " +
            "AccessOperation(QUERY, CREATE, GLOBAL)"
    )
    public OpAck<Query> save(
            HttpServletRequest request,
            @AuthenticationPrincipal User user,
            @RequestBody QuerySaveRequest query) throws Exception {
        Query q = new Query();
        if(query.getId() == null || query.getId().isEmpty()) {
            q.setId(null);
            q.setOwner(user.getUsername());
            q.setOwnerId(user.getId());
            q.setLastUpdated(new Date());
            q.setDateCreated(new Date());
            q.setName(query.getName());
        } else {
            Query existing = (Query) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
            // Non Overridable fields
            q.setId(existing.getId());
            q.setOwnerId(existing.getOwnerId());
            q.setOwner(existing.getOwner());
            q.setDateCreated(existing.getDateCreated());
            q.setLastUpdated(new Date());
            q.setName(existing.getName());
        }
        q.setQuery(query.getQuery());
        q.setConfiguration(query.getConfiguration());

        this.queryService.assertQueryIsValid(q);
        Query saved = this.queryRepository.save(q);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Query Successfully Saved", saved, "query-save");
    }

    //  Get Query by Id (Owned)
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, QUERY, VIEW, #id)")
    public Query get(
            HttpServletRequest request,
            @PathVariable("id") String id) throws NotFoundException {
        return (Query) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
    }

    //  Delete Query by Id (Owned or Published [viewOnly])
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, QUERY, DELETE, #id)")
    public OpAck<Query> delete(
            HttpServletRequest request,
            @PathVariable("id") String id) throws NotFoundException {
        Query x =  (Query) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
        this.queryRepository.delete(x);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Query Successfully Deleted", null,"query-delete");
    }

}
