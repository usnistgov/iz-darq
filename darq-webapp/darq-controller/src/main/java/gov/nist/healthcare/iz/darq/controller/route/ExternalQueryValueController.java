package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.model.ExternalQueryVariable;
import gov.nist.healthcare.iz.darq.repository.ExternalQueryVariableRepository;
import gov.nist.healthcare.iz.darq.service.impl.ExternalQueryVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external-variable")
public class ExternalQueryValueController {

    @Autowired
    ExternalQueryVariableService externalQueryVariableService;
    @Autowired
    ExternalQueryVariableRepository repository;

    @RequestMapping(value="/create", method= RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("isAdmin()")
    public OpAck<ExternalQueryVariable> create(
            @RequestBody ExternalQueryVariable variable) throws Exception {
        ExternalQueryVariable v = this.externalQueryVariableService.createVariable(variable);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Variable Successfully Saved", v, "variable-create");
    }

    @RequestMapping(value="/update", method= RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("isAdmin()")
    public OpAck<ExternalQueryVariable> update(
            @RequestBody ExternalQueryVariable variable) throws Exception {
        ExternalQueryVariable v = this.externalQueryVariableService.updateVariable(variable);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Variable Successfully Updated", v, "variable-update");
    }

    @RequestMapping(value="/delete/{id}", method= RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("isAdmin()")
    public OpAck<ExternalQueryVariable> delete(@PathVariable("id") String id) throws Exception {
        ExternalQueryVariable e = this.repository.findOne(id);
        if(e == null) {
            throw new Exception("Variable " + id + " not found");
        } else {
            this.repository.delete(e);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Variable Successfully Deleted", null, "variable-delete");
        }
    }

    @RequestMapping(value="/", method= RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("isAdmin()")
    public List<ExternalQueryVariable> getList() {
        return this.repository.findAll();
    }

}
