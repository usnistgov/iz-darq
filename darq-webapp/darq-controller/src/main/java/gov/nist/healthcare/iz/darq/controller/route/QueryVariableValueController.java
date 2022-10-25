package gov.nist.healthcare.iz.darq.controller.route;


import gov.nist.healthcare.iz.darq.model.QueryVariable;
import gov.nist.healthcare.iz.darq.model.QueryVariableDisplay;
import gov.nist.healthcare.iz.darq.repository.ExternalQueryVariableRepository;
import gov.nist.healthcare.iz.darq.service.impl.ADFQueryVariableService;
import gov.nist.healthcare.iz.darq.service.impl.QueryValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/query-variable")
public class QueryVariableValueController {

    @Autowired
    ExternalQueryVariableRepository repository;
    @Autowired
    ADFQueryVariableService adfQueryVariableService;
    @Autowired
    QueryValueService queryValueService;

    @RequestMapping(value="/", method= RequestMethod.GET)
    @ResponseBody
    public List<QueryVariable> get() throws Exception {
        List<QueryVariable> variables = new ArrayList<>();
        variables.addAll(repository.findAll());
        variables.addAll(adfQueryVariableService.getListOfADFVariable());
        return variables;
    }

    @RequestMapping(value="/display", method= RequestMethod.GET)
    @ResponseBody
    public List<QueryVariableDisplay> getDisplay() {
        return this.queryValueService.getVariablesDisplay();
    }


}
