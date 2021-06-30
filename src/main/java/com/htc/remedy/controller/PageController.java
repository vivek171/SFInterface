package com.htc.remedy.controller;

import com.bmc.arsys.api.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.htc.remedy.base.BaseModel;
import com.htc.remedy.base.LoggerBase;
import com.htc.remedy.base.RemedyBase;
import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.domain.EndPointDomain;
import com.htc.remedy.domain.FieldsDomain;
import com.htc.remedy.domain.QualificationDomain;
import com.htc.remedy.model.FieldModel;
import com.htc.remedy.model.FormModel;
import com.htc.remedy.model.process1;
import com.htc.remedy.repo.EndPointRepo;
import com.htc.remedy.repo.FieldRepo;
import com.htc.remedy.repo.QualificationRepo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by poovarasanv on 12/9/17.
 * Project : remedy-web-services
 * ctsspi- endpoint controllers
 */
@Controller
@CrossOrigin
@RequestMapping(path = "/page")
public class PageController {

    @Value("${ctsspi.username}")
    String username;

    @Value("${ctsspi.password}")
    String password;

    @Value("${remedy.host}")
    String serverName;

    @Value("${remedy.port}")
    Integer port;


    private final
    EndPointRepo endPointRepo;


    @Autowired
    public PageController(EndPointRepo endPointRepo, QualificationRepo qualificationRepo, FieldRepo fieldRepo) {
        this.endPointRepo = endPointRepo;
        this.qualificationRepo = qualificationRepo;
        this.fieldRepo = fieldRepo;
    }


    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("uid", UUID.randomUUID().toString());
        return SFInterfaceBase.validateAndreturnPage("create_endpoint", request);
    }


    @ResponseBody
    @RequestMapping(path = "/forms", method = RequestMethod.GET)
    public List<FormModel> forms() throws ARException {


        ARServerUser arServerUser = RemedyBase.loginUser(
                serverName,
                port,
                username,
                password
        );
        List<FormModel> formModelsList = new ArrayList<>();
        List<FormModel> filteredformModelsList = new ArrayList<>();
        formModelsList = RemedyBase.getAllForms(arServerUser);
        filteredformModelsList = formModelsList.stream()
                .filter(formModel -> formModel != null)
                .collect(Collectors.toList());
        return filteredformModelsList;
    }


    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @RequestMapping(path = "/allepdetails", method = RequestMethod.GET)
    public String allep(Model model, HttpServletRequest request) {
        model.addAttribute("endpoints", requestMappingHandlerMapping.getHandlerMethods().keySet());
        return SFInterfaceBase.validateAndreturnPage("all_ep", request);
    }


    @ResponseBody
    @RequestMapping(path = "/epdetails", method = RequestMethod.GET)
    public List<EndPointDomain> ep() {
        return endPointRepo.findAll();
    }

    @RequestMapping(path = "/endpointdetails", method = RequestMethod.GET)
    public String endpoint(Model model, HttpServletRequest request) {

        /*try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            List<EndPointDomain> Repo = endPointRepo.findAll();
            model.addAttribute("Repo", Repo);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }*/
        return SFInterfaceBase.validateAndreturnPage("search_endpoint", request);
    }


    @ResponseBody
    @RequestMapping(path = "/endpoints", method = RequestMethod.GET)
    public Map<String, Object> endpoints(
            @RequestParam(value = "draw", defaultValue = "1") int draw,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "length") int length,
            @RequestParam(value = "search[value]", required = false, defaultValue = "") String searchvalue,
            @RequestParam(value = "order[0][column]", defaultValue = "2") String sortcolumn,
            @RequestParam(value = "order[0][dir]", defaultValue = "asc") String sorttype,
            HttpServletRequest request
    ) {
        Map<String, Object> Repo = new HashMap<>();
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            int filteredcount = 0;
            int pageindex = 0;
            if (length != 0) {
                pageindex = start / length;
            }
            List<EndPointDomain> endPointDomains = new ArrayList<>();
            Pageable pageable = new PageRequest(pageindex, length);
            if (sortcolumn.equalsIgnoreCase("5")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByActiveAsc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByActiveAsc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByActiveDesc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByActiveDesc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("4")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByDateAsc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByDateAsc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByDateDesc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByDateDesc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("3")) {
                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByFormNameAsc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByFormNameAsc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByFormNameDesc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByFormNameDesc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                }

            } else {
                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByEndPointNameAsc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByEndPointNameAsc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(endPointRepo.findByEndPointNameContainingOrFormNameContainingOrderByEndPointNameDesc(pageable, searchvalue, searchvalue));
                        filteredcount = endPointRepo.countByEndPointNameContainingOrFormNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(endPointRepo.findAllByOrderByEndPointNameDesc(pageable));
                        filteredcount = (int) endPointRepo.count();
                    }
                }
            }
            Repo.put("draw", draw);
            Repo.put("recordsTotal", endPointRepo.count());
            Repo.put("recordsFiltered", filteredcount);
            Repo.put("data", endPointDomains);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Repo;
    }


    @RequestMapping(path = "/editendpointdetails/{q}", method = RequestMethod.GET)
    public String editendpoint(Model model,
                               @PathVariable(name = "q", required = true) String endpointName
            , HttpServletRequest request) {
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            List<FormModel> formModels = RemedyBase.getAllForms(arServerUser);
            List<FormModel> filteredformModels = new ArrayList<>();
            for (FormModel formModel : formModels) {
                if (formModel != null)
                    filteredformModels.add(formModel);
            }

            EndPointDomain endPointDomain = endPointRepo.findByEndPointName(endpointName);
            Set<FieldsDomain> fieldsDomains = fieldRepo.findByFieldsEndpoint(endPointDomain);
            Set<QualificationDomain> qualificationDomains = qualificationRepo.findByQualificationEndPoint(endPointDomain);

            JsonArray jsonElements = new JsonArray();
            for (QualificationDomain qualificationDomain : qualificationDomains) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("columnName", qualificationDomain.getColumnName());
                jsonObject.addProperty("condition", qualificationDomain.getCondition());

                if (qualificationDomain.getCondition().equals("like")) {
                    jsonObject.addProperty("columnValue", qualificationDomain.getColumnValue());
                } else {
                    jsonObject.addProperty("columnValue", qualificationDomain.getColumnValue());
                }

                jsonObject.addProperty("appendCondition", qualificationDomain.getAppendCondition());
                jsonElements.add(jsonObject);
            }
            model.addAttribute("fields", fieldsDomains);
            model.addAttribute("fieldsJson", new JSONArray(fieldsDomains).toString());
            model.addAttribute("filters", qualificationDomains);
            model.addAttribute("filtersJson", jsonElements.toString());
            model.addAttribute("forms", filteredformModels);
            model.addAttribute("Repo", endPointDomain);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return SFInterfaceBase.validateAndreturnPage("edit_endpoint", request);
    }

    final
    QualificationRepo qualificationRepo;

    final
    FieldRepo fieldRepo;

/*    @RequestMapping(path = "/saveendpoint", method = RequestMethod.POST)
    @ResponseBody
    public BaseModel saveendPoint(
            @RequestParam(value = "qualification[]", required = false) List<String> qualification,
            @RequestParam(value = "selectedFields[]", required = false) List<String> requiredFields,
            @RequestParam("formName") String formName,
            @RequestParam("endpointName") String endPoint,
            @RequestParam(value = "endpointDesc", required = false) String description,
            @RequestParam("endpointUrl") String Url,
            @RequestParam("qualificationString") String QualificationString
    ) {
        List<BaseModel> basemodel = new ArrayList<>();
        EndPointDomain endPointDomain = new EndPointDomain();
        Set<FieldsDomain> fieldsDomain = new HashSet<>();
        List<QualificationDomain> qualificationDomain = new ArrayList<>();
        endPointDomain.setEndPointDescription(description.trim());
        endPointDomain.setEndPointName(endPoint.trim().toLowerCase());
        endPointDomain.setEndPointKey(Url.trim());
        endPointDomain.setFormName(formName.trim());
        endPointDomain.setQualificationString(StringUtils.replace(QualificationString, "\"$NULL$\"", "$NULL$"));
        EndPointDomain savedDomain = endPointRepo.save(endPointDomain);


        if (requiredFields != null) {
            for (String fields : requiredFields) {
                String[] fieldsplit = fields.split("\\^");
                fieldRepo.save(new FieldsDomain(Long.parseLong(fieldsplit[0]), fieldsplit[1], savedDomain));
            }
        }
        if (qualification != null) {
            for (String fields : qualification) {
                String[] fieldsplit = fields.split("\\^");
                qualificationRepo.save(new QualificationDomain(fieldsplit[0], fieldsplit[2], fieldsplit[1], fieldsplit[3], savedDomain));
            }
        }

        EndPointDomain endPointDomain1 = endPointRepo.findOne(savedDomain.getId());


        return endPointDomain1;
    }*/

    @RequestMapping(path = "/updateendpoint", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public BaseModel updateendPoint(
            @RequestParam(value = "qualification[]", required = false) List<String> qualification,
            @RequestParam(value = "selectedFields[]", required = false) List<String> requiredFields,
            @RequestParam("formName") String formName,
            @RequestParam("endpointName") String endPoint,
            @RequestParam(value = "endpointDesc", required = false) String description,
            @RequestParam("endpointUrl") String Url,
            @RequestParam("qualificationString") String QualificationString
    ) {
        List<BaseModel> basemodel = new ArrayList<>();
        EndPointDomain endPointDomain = new EndPointDomain();
        Set<FieldsDomain> fieldsDomain = new HashSet<>();
        EndPointDomain endPointDomain1 = new EndPointDomain();
        List<QualificationDomain> qualificationDomain = new ArrayList<>();

        endPointDomain = endPointRepo.findByEndPointName(endPoint);

        if (!(endPointDomain == null)) {
            fieldRepo.deleteInBatch(fieldRepo.findByFieldsEndpoint(endPointDomain));
            qualificationRepo.deleteInBatch(qualificationRepo.findByQualificationEndPoint(endPointDomain));
            endPointRepo.delete(endPointDomain);
        }
        endPointDomain = new EndPointDomain();
        endPointDomain.setEndPointDescription(description.trim());
        endPointDomain.setEndPointName(endPoint.trim());
        endPointDomain.setEndPointKey(Url.trim());
        endPointDomain.setFormName(formName.trim());
        endPointDomain.setQualificationString(StringUtils.replace(QualificationString, "\"$NULL$\"", "$NULL$"));
        endPointDomain.setDate(LocalDateTime.now());
        endPointDomain.setFilter(null);
        endPointDomain.setSelectedFields(null);

        EndPointDomain savedDomain = endPointRepo.save(endPointDomain);

        if (requiredFields != null) {
            List<FieldsDomain> fd = new ArrayList<>();
            for (String fields : requiredFields) {
                String[] fieldsplit = fields.split("\\^");
                fd.add(new FieldsDomain(Long.parseLong(fieldsplit[0]), fieldsplit[1], savedDomain));

            }
            fieldRepo.save(fd);
            fd = null;
        }
        if (qualification != null) {
            List<QualificationDomain> qualificationDomains = new ArrayList<>();
            for (String fields : qualification) {
                String[] fieldsplit = fields.split("\\^");
                qualificationDomains.add(new QualificationDomain(fieldsplit[0], fieldsplit[2], fieldsplit[1], fieldsplit[3], savedDomain));
            }
            qualificationRepo.save(qualificationDomains);
            qualificationDomains = null;
        }

        endPointDomain1 = endPointRepo.findOne(savedDomain.getId());

        return endPointDomain1;
    }

    @RequestMapping(path = "/updateendpointpromote", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public BaseModel updateendPointPromote(@RequestBody String rendPointDomain1) {


        EndPointDomain rendPointDomain = null;

        rendPointDomain = new Gson().fromJson(rendPointDomain1, EndPointDomain.class);


        EndPointDomain endPointDomain = endPointRepo.findByEndPointName(rendPointDomain.getEndPointName());

        if (!(endPointDomain == null)) {
            fieldRepo.deleteInBatch(fieldRepo.findByFieldsEndpoint(endPointDomain));
            qualificationRepo.deleteInBatch(qualificationRepo.findByQualificationEndPoint(endPointDomain));
            endPointRepo.delete(endPointDomain);
        }


        endPointDomain = new EndPointDomain();
        endPointDomain.setEndPointDescription(rendPointDomain.getEndPointDescription());
        endPointDomain.setEndPointName(rendPointDomain.getEndPointName());
        endPointDomain.setEndPointKey(rendPointDomain.getEndPointKey());
        endPointDomain.setFormName(rendPointDomain.getFormName());
        endPointDomain.setQualificationString(rendPointDomain.getQualificationString());
        endPointDomain.setDate(LocalDateTime.now());
        endPointDomain.setFilter(null);
        endPointDomain.setSelectedFields(null);

        EndPointDomain savedDomain = endPointRepo.save(endPointDomain);


        if (savedDomain != null) {

            for (QualificationDomain qualificationDomain : rendPointDomain.getFilter()) {
                QualificationDomain qualificationDomain1 = new QualificationDomain();
                qualificationDomain1.setAppendCondition(qualificationDomain.getAppendCondition());
                qualificationDomain1.setColumnName(qualificationDomain.getColumnName());
                qualificationDomain1.setColumnValue(qualificationDomain.getColumnValue());
                qualificationDomain1.setCondition(qualificationDomain.getCondition());
                qualificationDomain1.setQualificationEndPoint(savedDomain);
                qualificationRepo.save(qualificationDomain1);
            }

            for (FieldsDomain fieldsDomain : rendPointDomain.getSelectedFields()) {
                FieldsDomain fieldsDomain1 = new FieldsDomain();
                fieldsDomain1.setFieldId(fieldsDomain.getFieldId());
                fieldsDomain1.setFieldName(fieldsDomain.getFieldName());
                fieldsDomain1.setRepresentation(fieldsDomain.getRepresentation());
                fieldsDomain1.setFieldsEndpoint(savedDomain);
                fieldRepo.save(fieldsDomain1);
            }
        }

        return savedDomain;
    }


    @ResponseBody
    @RequestMapping(path = "/checkendpointname", method = RequestMethod.GET)
    public String checkendpoint(Model model, @RequestParam("endpointname") String endpointName) {


        EndPointDomain endPointDomain1 = endPointRepo.findByEndPointName(endpointName.toLowerCase());
        if (endPointDomain1 == null) {
            return "true";
        } else {

            return "false";
        }
    }

    @RequestMapping(path = "/previewendpoint", method = RequestMethod.POST)
    @ResponseBody
    public String previewendPoint(
            @RequestParam(value = "qualification[]", required = false) List<String> qualification,
            @RequestParam(value = "selectedFields[]", required = false) List<String> requiredFields,
            @RequestParam("formName") String formName,
            @RequestParam("endpointName") String endPoint,
            @RequestParam(value = "endpointDesc", required = false) String description,
            @RequestParam("endpointUrl") String Url,
            @RequestParam("qualificationString") String QualificationString,
            HttpServletRequest request
    ) {

        JsonArray tArray = new JsonArray();
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            EndPointDomain endPointDomain = new EndPointDomain();
            endPointDomain.setEndPointDescription(description.trim());
            endPointDomain.setEndPointName(endPoint.trim());
            endPointDomain.setEndPointKey(Url.trim());
            endPointDomain.setFormName(formName.trim());
            endPointDomain.setQualificationString(StringUtils.replace(QualificationString, "\"$NULL$\"", "$NULL$"));

            Map<Integer, String> sMap = new HashMap<>();
            int[] rf = new int[requiredFields.size()];
            int c = 0;

            if (requiredFields != null) {
                for (String fields : requiredFields) {
                    String[] fieldsplit = fields.split("\\^");

                    sMap.put(Integer.parseInt(fieldsplit[0]), fieldsplit[1]);
                    rf[c++] = Integer.parseInt(fieldsplit[0]);

                }
            }

            String qstr = endPointDomain.getQualificationString();

            Map<String, String[]> params = request.getParameterMap();

            String rValue = qstr;
            for (String s : params.keySet()) {
                rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                //System.out.println((qstr.replace("{{" + s + "}}", params.get(s)[0])));
            }

            List<Entry> entries = RemedyBase.queryEntrysByQual(
                    arServerUser,
                    formName,
                    rf,
                    qstr
            );
            if (entries == null) {
                throw new Exception("Fields mismatch/query returns huge data which could not be retreived");
            }
            for (Entry entry : entries) {
                JsonObject jsonObject = new JsonObject();
                for (Integer integer : sMap.keySet()) {
                    if (entry.containsKey(integer)) {
                        jsonObject.addProperty(sMap.get(integer), entry.get(integer).toString());
                    }
                }
                tArray.add(jsonObject);
            }

        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            tArray.add(error);
        }
        return tArray.toString();
    }


    public static Map<String, process1> source() throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        Map<String, process1> sF = new HashMap<>();
        File sFile = new File("D:\\CTI IMPORT\\ctidetails.xls");
        org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(sFile);
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            org.apache.poi.ss.usermodel.Row row = rowIterator.next();
            process1 process1 = new process1(
                    row.getCell(4).getStringCellValue().trim(),
                    row.getCell(2).getStringCellValue().trim(),
                    row.getCell(1).getStringCellValue().trim(), "");

            sF.put(row.getCell(5).getStringCellValue().trim(), process1);
        }

        return sF;
    }

    @ResponseBody
    @RequestMapping(path = "/endpointurl/{endpointname}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> endpointurl(Model model, @PathVariable("endpointname") String endpointName,
                                                 @RequestParam(value = "custom", required = false, defaultValue = "false") String custom,
                                                 @RequestParam(value = "sortfield", required = false) String sortfield,
                                                 @RequestParam(value = "toLowerCase", required = false, defaultValue = "false") String toLowerCase,
                                                 @RequestParam(value = "distinct", required = false, defaultValue = "false") String distinct,
                                                 @RequestParam(value = "noofrecords", required = false, defaultValue = "0") Integer noofrecords,
                                                 @RequestParam(value = "sortorder", required = false, defaultValue = "asc") String sortorder,
                                                 @RequestParam(value = "errormessage", required = false, defaultValue = "") String errormessage,
                                                 HttpServletRequest request) {

        JsonArray tArray = new JsonArray();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            EndPointDomain endPointDomain = endPointRepo.findByEndPointNameAndActiveIsTrue(endpointName);

            if (endPointDomain != null) {
                String formName = endPointDomain.getFormName();
                Set<FieldsDomain> selectedFields = fieldRepo.findByFieldsEndpoint(endPointDomain);

                Map<Integer, String> sMap = new HashMap<>();
                int[] rf = new int[selectedFields.size()];
                int c = 0;

                if (toLowerCase.equalsIgnoreCase("true")) {
                    for (FieldsDomain s : selectedFields) {
                        sMap.put(s.getFieldId().intValue(), s.getFieldName().toLowerCase());
                        rf[c++] = s.getFieldId().intValue();
                    }
                } else {
                    for (FieldsDomain s : selectedFields) {
                        sMap.put(s.getFieldId().intValue(), s.getFieldName());
                        rf[c++] = s.getFieldId().intValue();
                    }
                }

                String qstr = endPointDomain.getQualificationString();

                Map<String, String[]> params = request.getParameterMap();

                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                }
                rValue = StringUtils.replace(rValue, "\"$NULL$\"", "$NULL$");
                List<Entry> entries = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        rf,
                        rValue, noofrecords
                );
                LoggerBase.loguserrecords(endpointName, arServerUser, formName, Constants.FETCH, request);

                if (custom.equalsIgnoreCase("true")) {
                    List<Field> fields = arServerUser.getListFieldObjects(formName);
                    for (Entry entry : entries) {
                        Map<String, Object> eachobject = new HashMap<>();
                        for (Integer integer : sMap.keySet()) {
                            Boolean selectionfield = false, datetimefield = false, diaryfield = false, attachmentfield = false;
                            Field field1 = null;
                            if (entry.containsKey(integer)) {
                                sMap.put(integer, sMap.get(integer).replaceAll(" ", "_"));

                                for (Field field : fields) {
                                    if (field.getFieldID() == integer) {
                                        if (field instanceof SelectionField) {
                                            selectionfield = true;
                                            field1 = field;
                                        } else if (field instanceof DateTimeField) {
                                            datetimefield = true;
                                            field1 = field;
                                        } else if (field instanceof DiaryField) {
                                            diaryfield = true;
                                            field1 = field;
                                        } else if (field instanceof AttachmentField) {
                                            attachmentfield = true;
                                            field1 = field;
                                        }

                                    }
                                }
                                if (entry.get(integer).toString() == null) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), "");
                                } else if (entry.get(integer).toString() != null && selectionfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.selectionfieldvalue(field1, entry.get(integer).toString()));
                                } else if (entry.get(integer).toString() != null && datetimefield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.dateTimefieldvalue(field1, entry.get(integer)));
                                } else if (entry.get(integer).toString() != null && diaryfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.diaryfieldValue(field1, entry.get(integer)));
                                } else if (entry.get(integer).toString() != null && attachmentfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.createattatchmenturi(arServerUser, formName, entry.getEntryId(), field1, entry.get(integer), request, entry));
                                }
                               /* else if (entry.get(integer) != null && entry.get(integer).toString() != null && entry.get(integer).toString().startsWith("[Timestamp=")) {
                                            eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.formatEpocDate(entry.get(integer).toString().substring(entry.get(integer).toString().indexOf("=") + 1, entry.get(integer).toString().indexOf("]"))).toString());
                                        }
                                else if (sMap.get(integer).equalsIgnoreCase("Work_Log")) {
                                    String worklog = entry.get(integer).toString();
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.parseWorkLog1(worklog));
                                }*/
                                else {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), entry.get(integer).toString());
                                }
                            }
                        }
                        result.add(eachobject);
                    }
                } else {
                    for (Entry entry : entries) {
                        Map<String, Object> eachobject = new HashMap<>();

                        for (Integer integer : sMap.keySet()) {
                            if (entry.containsKey(integer)) {
                                eachobject.put(sMap.get(integer), entry.get(integer).toString());
                            }
                        }

                        result.add(eachobject);
                    }
                }
                List<Map<String, Object>> distinctresult = new ArrayList<>();
                if (sortfield != null && !sortfield.isEmpty()) {
                    Collections.sort(result, new Comparator<Map<String, Object>>() {
                        public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                            if (o1.get(sortfield) instanceof String && o2.get(sortfield) instanceof String)
                                return String.class.cast(o1.get(sortfield)).compareTo(String.class.cast(o2.get(sortfield)));
                            else
                                return 0;
                        }
                    });

                    if (distinct.equalsIgnoreCase("true")) {
                        result.stream().forEach(stringStringMap -> {
                            List result1 = distinctresult.stream()
                                    .filter(stringStringMap1 ->
                                            stringStringMap1.get(sortfield).toString().equalsIgnoreCase(stringStringMap.get(sortfield).toString())
                                    )
                                    .collect(Collectors.toList());
                            if (result1.isEmpty() || result1 == null) {
                                distinctresult.add(stringStringMap);
                            }
                        });
                        result.clear();
                        result = new ArrayList<>(distinctresult);
                    }
                    if (sortorder.equalsIgnoreCase("desc")) {
                        Collections.reverse(result);
                    }
                }

            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Endpoint Not found");
                result.add(error);
            }
            if (!errormessage.isEmpty() && result.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                result.clear();
                error.put("error", errormessage);
                result.add(error);
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            if (!errormessage.isEmpty()) {
                result.clear();
                error.put("error", errormessage);
            }
            result.add(error);
        }

        return result;
    }


    @ResponseBody
    @RequestMapping(path = "/updatedendpointurl/{endpointname}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> updatedendpointurl(Model model, @PathVariable("endpointname") String endpointName,
                                                        @RequestParam(value = "custom", required = false, defaultValue = "false") String custom,
                                                        @RequestParam(value = "sortfield", required = false) String sortfield,
                                                        @RequestParam(value = "toLowerCase", required = false, defaultValue = "false") String toLowerCase,
                                                        @RequestParam(value = "distinct", required = false, defaultValue = "false") String distinct,
                                                        @RequestParam(value = "noofrecords", required = false, defaultValue = "0") Integer noofrecords,
                                                        @RequestParam(value = "sortorder", required = false, defaultValue = "asc") String sortorder,
                                                        @RequestParam(value = "errormessage", required = false, defaultValue = "") String errormessage,
                                                        HttpServletRequest request) {

        JsonArray tArray = new JsonArray();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            EndPointDomain endPointDomain = endPointRepo.findByEndPointNameAndActiveIsTrue(endpointName);

            if (endPointDomain != null) {
                String formName = endPointDomain.getFormName();
                Set<FieldsDomain> selectedFields = fieldRepo.findByFieldsEndpoint(endPointDomain);

                Map<Integer, String> sMap = new HashMap<>();
                int[] rf = new int[selectedFields.size()];
                int c = 0;

                if (toLowerCase.equalsIgnoreCase("true")) {
                    for (FieldsDomain s : selectedFields) {
                        sMap.put(s.getFieldId().intValue(), s.getFieldName().toLowerCase());
                        rf[c++] = s.getFieldId().intValue();
                    }
                } else {
                    for (FieldsDomain s : selectedFields) {
                        sMap.put(s.getFieldId().intValue(), s.getFieldName());
                        rf[c++] = s.getFieldId().intValue();
                    }
                }

                String qstr = endPointDomain.getQualificationString();

                Map<String, String[]> params = request.getParameterMap();

                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                }
                rValue = StringUtils.replace(rValue, "\"$NULL$\"", "$NULL$");
                List<Entry> entries = RemedyBase.queryEntrysByQualwithmaxrecords(
                        arServerUser,
                        formName,
                        rf,
                        rValue
                );

                if (custom.equalsIgnoreCase("true")) {
                    List<Field> fields = arServerUser.getListFieldObjects(formName);
                    for (Entry entry : entries) {
                        Map<String, Object> eachobject = new HashMap<>();
                        for (Integer integer : sMap.keySet()) {
                            Boolean selectionfield = false, datetimefield = false, diaryfield = false, attachmentfield = false;
                            Field field1 = null;
                            if (entry.containsKey(integer)) {
                                sMap.put(integer, sMap.get(integer).replaceAll(" ", "_"));

                                for (Field field : fields) {
                                    if (field.getFieldID() == integer) {
                                        if (field instanceof SelectionField) {
                                            selectionfield = true;
                                            field1 = field;
                                        } else if (field instanceof DateTimeField) {
                                            datetimefield = true;
                                            field1 = field;
                                        } else if (field instanceof DiaryField) {
                                            diaryfield = true;
                                            field1 = field;
                                        } else if (field instanceof AttachmentField) {
                                            attachmentfield = true;
                                            field1 = field;
                                        }

                                    }
                                }
                                if (entry.get(integer).toString() == null) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), "");
                                } else if (entry.get(integer).toString() != null && selectionfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.selectionfieldvalue(field1, entry.get(integer).toString()));
                                } else if (entry.get(integer).toString() != null && datetimefield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.dateTimefieldvalue(field1, entry.get(integer)));
                                } else if (entry.get(integer).toString() != null && diaryfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.diaryfieldValue(field1, entry.get(integer)));
                                } else if (entry.get(integer).toString() != null && attachmentfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.createattatchmenturi(arServerUser, formName, entry.getEntryId(), field1, entry.get(integer), request, entry));
                                }
                               /* else if (entry.get(integer) != null && entry.get(integer).toString() != null && entry.get(integer).toString().startsWith("[Timestamp=")) {
                                            eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.formatEpocDate(entry.get(integer).toString().substring(entry.get(integer).toString().indexOf("=") + 1, entry.get(integer).toString().indexOf("]"))).toString());
                                        }
                                else if (sMap.get(integer).equalsIgnoreCase("Work_Log")) {
                                    String worklog = entry.get(integer).toString();
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.parseWorkLog1(worklog));
                                }*/
                                else {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), entry.get(integer).toString());
                                }
                            }
                        }
                        result.add(eachobject);
                    }
                } else {
                    for (Entry entry : entries) {
                        Map<String, Object> eachobject = new HashMap<>();

                        for (Integer integer : sMap.keySet()) {
                            if (entry.containsKey(integer)) {
                                eachobject.put(sMap.get(integer), entry.get(integer).toString());
                            }
                        }

                        result.add(eachobject);
                    }
                }
                List<Map<String, Object>> distinctresult = new ArrayList<>();
                if (sortfield != null && !sortfield.isEmpty()) {
                    Collections.sort(result, new Comparator<Map<String, Object>>() {
                        public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                            if (o1.get(sortfield) instanceof String && o2.get(sortfield) instanceof String)
                                return String.class.cast(o1.get(sortfield)).compareTo(String.class.cast(o2.get(sortfield)));
                            else
                                return 0;
                        }
                    });

                    if (distinct.equalsIgnoreCase("true")) {
                        result.stream().forEach(stringStringMap -> {
                            List result1 = distinctresult.stream()
                                    .filter(stringStringMap1 ->
                                            stringStringMap1.get(sortfield).toString().equalsIgnoreCase(stringStringMap.get(sortfield).toString())
                                    )
                                    .collect(Collectors.toList());
                            if (result1.isEmpty() || result1 == null) {
                                distinctresult.add(stringStringMap);
                            }
                        });
                        result.clear();
                        result = new ArrayList<>(distinctresult);
                    }
                    if (sortorder.equalsIgnoreCase("desc")) {
                        Collections.reverse(result);
                    }
                }

            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Endpoint Not found");
                result.add(error);
            }
            if (!errormessage.isEmpty() && result.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                result.clear();
                error.put("error", errormessage);
                result.add(error);
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            if (!errormessage.isEmpty()) {
                result.clear();
                error.put("error", errormessage);
            }
            result.add(error);
        }

        return result;
    }

    @RequestMapping(path = "/activeep/{eid}", method = RequestMethod.GET)
    public String activeEndpoint(@PathVariable("eid") String eid, HttpServletRequest request) {
        EndPointDomain endPointDomain = endPointRepo.findOne(Long.parseLong(eid));
        endPointDomain.setActive(true);
        endPointRepo.save(endPointDomain);
        return SFInterfaceBase.validateAndreturnPage("redirect:/page/endpointdetails", request);
    }

    @RequestMapping(path = "/deactiveep/{eid}", method = RequestMethod.GET)
    public String deActiveEndpoint(@PathVariable("eid") String eid, HttpServletRequest request) {
        EndPointDomain endPointDomain = endPointRepo.findOne(Long.parseLong(eid));
        endPointDomain.setActive(false);
        endPointRepo.save(endPointDomain);
        return SFInterfaceBase.validateAndreturnPage("redirect:/page/endpointdetails", request);

    }

    @ResponseBody
    @RequestMapping(path = "/deactiveep1/{eid}", method = RequestMethod.GET)
    public String deActiveEndpoint1(@PathVariable("eid") String eid) {
        EndPointDomain endPointDomain = endPointRepo.findOne(Long.parseLong(eid));
        endPointDomain.setActive(false);
        endPointRepo.save(endPointDomain);
        return "true";
    }

    @ResponseBody
    @RequestMapping(path = "/activeep1/{eid}", method = RequestMethod.GET)
    public String activeEndpoin1(@PathVariable("eid") String eid) {
        EndPointDomain endPointDomain = endPointRepo.findOne(Long.parseLong(eid));
        endPointDomain.setActive(true);
        endPointRepo.save(endPointDomain);
        return "true";
    }


    @RequestMapping(path = "/internal/{q}", method = RequestMethod.GET)
    @ResponseBody
    public List<FieldModel> internalController(Model model,
                                               @PathVariable(name = "q", required = true) String queryParam,
                                               @RequestParam Map<String, String> allRequestParams
    ) {

        List<FieldModel> fieldModels = new ArrayList<>();
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            if (queryParam.equals("fields")) {
                String formName = allRequestParams.get("formName");

                fieldModels = RemedyBase.getFormField5(
                        arServerUser,
                        formName);
            }
            Collections.sort(fieldModels);

        } catch (Exception e) {

        }
        return fieldModels;
    }

}
