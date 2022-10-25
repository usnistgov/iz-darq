package gov.nist.healthcare.iz.darq.controller.route;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.ExternalQueryVariableScope;
import gov.nist.healthcare.iz.darq.model.EmailType;
import gov.nist.healthcare.iz.darq.model.ExternalQueryVariable;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.repository.ExternalQueryVariableRepository;
import gov.nist.healthcare.iz.darq.service.exception.VariableImportException;
import gov.nist.healthcare.iz.darq.service.impl.ExternalQueryVariableService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
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


    @RequestMapping(value="/import", method=RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("isAdmin()")
    public OpAck<Void> importCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "scope") ExternalQueryVariableScope scope
    ) throws Exception{
        InputStream stream = file.getInputStream();
        try {
            List<ExternalQueryVariable> variables = this.externalQueryVariableService.importFromCSV(stream, scope);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, variables.size() + " " + scope + " Variable(s) imported", null, "import-variables");
        } catch (VariableImportException e) {
            e.printStackTrace();
            if(e.getImported() != null && e.getImported().size() > 0) {
                return new OpAck<>(OpAck.AckStatus.FAILED, "There were errors while importing from the file : " + e.getMessage() + ". Some variables were imported : " + e.getImported().size() + " " + scope + " Variable(s) imported.", null, "import-variables");
            }
            throw e;
        }
    }

    @RequestMapping(value="/export/{scope}", method=RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("isAdmin()")
    public void exportCSV(
            HttpServletResponse response,
            @PathVariable() ExternalQueryVariableScope scope
    ) throws Exception{
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename="+scope.name().toLowerCase()+"_variables.csv");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.externalQueryVariableService.exportToCSV(baos, scope);
        IOUtils.write(baos.toByteArray(), response.getOutputStream());
    }

}
