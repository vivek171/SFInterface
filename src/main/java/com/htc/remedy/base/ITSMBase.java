package com.htc.remedy.base;


import com.bmc.arsys.api.*;
import com.bmc.thirdparty.org.apache.commons.lang.ArrayUtils;
import com.bmc.thirdparty.org.apache.commons.lang.StringEscapeUtils;
import com.google.gson.Gson;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.domain.EndPointDomain;
import com.htc.remedy.domain.FieldsDomain;
import com.htc.remedy.model.Token;
import com.htc.remedy.model.UserDetails;
import com.htc.remedy.repo.EndPointRepo;
import com.htc.remedy.repo.FieldRepo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.htc.remedy.base.RemedyBase.*;


@Service
public class ITSMBase {

    public static Map<String, Object> customupdateTicketbyid(ARServerUser arServerUser, Map<String, String> updateParam, String formName, String ticketId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            Entry entry = new Entry();
            entry.setEntryId(ticketId);
            int[] reqfield;
            int j = 0;
            reqfield = new int[updateParam.size()];
            Map<String, Object> formFields = getFormFields(arServerUser, formName);
            for (String s : updateParam.keySet()) {
                if (formFields.containsValue(Integer.parseInt(s))) {
                    if (updateParam.get(s) == null || updateParam.get(s).toString() == null || updateParam.get(s).equalsIgnoreCase("null") || updateParam.get(s).equalsIgnoreCase("NOVALUE") || updateParam.get(s).equalsIgnoreCase("") || updateParam.get(s).equalsIgnoreCase("NO_VALUE")) {
                        entry.put(Integer.parseInt(s), new Value());
                        reqfield[j] = Integer.parseInt(s);
                        j++;
                    } else {
                        reqfield[j] = Integer.parseInt(s);
                        j++;
                        entry.put(Integer.parseInt(s), new Value(updateParam.get(s)));
                    }
                }
            }
            for (int i : coreFields()) {
                entry.remove(i);
            }
            Entry updatedentry = arServerUser.setGetEntry(formName, ticketId, entry, null, 0, reqfield);

            List<Entry> results = new ArrayList<>();
            results.add(updatedentry);
            List<Map<String, Object>> resultset = ITSMBase.remedyresultsetforitsm(arServerUser, formName, results, request, reqfield);

            resultset.forEach(stringObjectMap -> {
                stringObjectMap.forEach((s, o) -> {
                    response.put(s, o);
                });
            });

            response.put("ticketId", ticketId);
            return response;
        } catch (ARException e) {
            response.clear();
            response.put("error", e.getMessage());
        }
        return response;
    }

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Constants.getFileattachmentpath() + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public static List distinctresultset(List<Map<String, Object>> result, String sortfield, String distinct, String sortorder) {

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
        return result;
    }


    public static List remedyresultsetforitsm(ARServerUser user, String formname, List<Entry> entries, HttpServletRequest request, int[] req) {
        List<Map<String, Object>> entriess = new ArrayList<>();
        try {
            List<Field> fields = user.getListFieldObjects(formname);

            entries.stream().forEach(entry -> {
                Map<String, Object> individualentry = new HashMap<>();
                entry.forEach((integer, value) -> {
                    Object entryvalue = "";
                    Field fieldentry = fields.parallelStream().filter(field -> field.getFieldID() == integer).findFirst().orElse(null);
                    if (fieldentry != null) {
                        if (value.toString() != null) {
                            if (fieldentry instanceof DateTimeField) {
                                entryvalue = timestamp(entry.get(integer));
                            } else if (fieldentry instanceof SelectionField) {
                                entryvalue = RemedyBase.selectionfieldvalue(fieldentry, entry.get(integer).toString());
                            } else if (fieldentry instanceof DiaryField) {
                                entryvalue = parseWorkLog(entry.get(integer).toString());
                                //entryvalue = RemedyBase.diaryfieldValue(fieldentry, entry.get(integer));
                            } else if (fieldentry instanceof AttachmentField) {
                                try {
                                    entryvalue = createattatchmenturi(user, formname, entry.getEntryId(), fieldentry, entry.get(integer), request, entry);
                                } catch (Exception e) {
                                }
                            } else {
                                entryvalue = value.toString();
                            }
                        } else {
                            entryvalue = "";
                        }
                        individualentry.put(fieldentry.getName().replaceAll("[^a-zA-Z0-9]", "_"), entryvalue);
                    }
                });
                entriess.add(individualentry);
            });
        } catch (Exception e) {
        }
        return entriess;
    }

    //remedyapi9.1
    public static List remedyresultsetforitsmentrylist(ARServerUser user, String formname, List<EntryValueList> entries, HttpServletRequest request, int[] req) {
        List<Map<String, Object>> entriess = new ArrayList<>();
        try {
            List<Field> fields = user.getListFieldObjects(formname);
            entries.forEach(entry -> {
                Map<String, Object> individualentry = new HashMap<>();

                for (int i : req) {
                    Field fieldentry = fields.parallelStream().filter(field -> field.getFieldID() == i).findFirst().orElse(null);
                    individualentry.put(fieldentry.getName().replaceAll("[^a-zA-Z0-9]", "_"), entry.get(new ArithmeticOrRelationalOperand(i)).getValue().toString());
                }
                entriess.add(individualentry);
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return entriess;
    }

    public static List remedyresultsetforclient(List<Entry> entries) {
        List<Map<String, Object>> entriess = new ArrayList<>();
        try {
            entries.stream().forEach(entry -> {
                Map<String, Object> individualentry = new HashMap<>();
                individualentry.put("clientcodename", entry.get(536871013).toString());
                individualentry.put("group", entry.get(105).toString());
                entriess.add(individualentry);
            });
        } catch (Exception e) {
        }
        return entriess;
    }

    public static String createTicket(ARServerUser arServerUser, String formName, Entry entry) throws Exception {
        entry.forEach((integer, value) -> {
            if (value == null || value.toString() == null || value.toString().equalsIgnoreCase("null") ||
                    value.toString().equalsIgnoreCase("NO_VALUE") || value.toString().equalsIgnoreCase("NOVALUE") || value.getValue().toString().isEmpty()) {
                value = new Value();
            }
        });
        return arServerUser.createEntry(formName, entry);
    }

    public static List<Entry> queryEntrysByQualwithmaxrecords(ARServerUser server,
                                                              String formName,
                                                              int[] requiredFields,
                                                              String qualStr, Integer noOfRecords) {
        List<Entry> entryList = new ArrayList<>();
        List<Entry> entryList1 = new ArrayList<>();
        try {
            List<Field> fields = server.getListFieldObjects(formName);
            QualifierInfo qual = server.parseQualification(qualStr, fields, null, com.bmc.arsys.api.Constants.AR_QUALCONTEXT_DEFAULT);

            OutputInteger nMatches = new OutputInteger();

            int startsize = 0;
            do {
                entryList = server.getListEntryObjects(
                        formName, qual, startsize,
                        noOfRecords,
                        null, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);
                startsize += entryList.size();
                entryList1.addAll(entryList);
            } while (startsize < nMatches.intValue());
        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList1;
    }

    public static List<Entry> queryEntriesbyentryids(ARServerUser server,
                                                     String formName,
                                                     int[] requiredFields,
                                                     List<String> entryids) throws ARException {
        return server.getListEntryObjects(formName,
                entryids,
                requiredFields);
    }

    //remedyapi9.1
    public static List<EntryValueList> queryEntrysByQualwithmaxrecords(ARServerUser server,
                                                                       String formName,
                                                                       int[] requiredFields,
                                                                       String qualStr, Integer noOfRecords, Boolean distinct, int[] sortfields, int[] groupbyfields) {

        List<EntryValueList> entryValueList = new ArrayList<>();
        List<EntryValueList> responseenEntryValueList = new ArrayList<>();

        List<Integer> sortfield = Arrays.stream(sortfields).boxed().collect(Collectors.toList());
        List<Integer> groupbyfield = Arrays.stream(groupbyfields).boxed().collect(Collectors.toList());

        List<ComplexSortInfo> sortList = new ArrayList<>();
        List<ArithmeticOrRelationalOperand> groupbylist = new ArrayList<>();
        try {
            List<Field> fields = server.getListFieldObjects(formName);

            QualifierInfo qual = server.parseQualification(qualStr, fields, null, com.bmc.arsys.api.Constants.AR_QUALCONTEXT_DEFAULT);

            groupbyfield.stream().forEach(integer -> {
                groupbylist.add(new ArithmeticOrRelationalOperand(integer));
            });

            sortfield.stream().forEach(integer -> {
                sortList.add(new ComplexSortInfo(new ArithmeticOrRelationalOperand(integer), com.bmc.arsys.api.Constants.AR_SORT_ASCENDING));
            });


            OutputInteger nMatches = new OutputInteger();

            int startsize = 0;
            do {
                entryValueList = server.getListValuesFromEntries(formName,
                        qual,
                        startsize,
                        com.bmc.arsys.api.Constants.AR_NO_MAX_LIST_RETRIEVE,
                        sortList,
                        groupbylist,
                        groupbylist,
                        null,
                        distinct,
                        true,
                        nMatches);
                startsize += entryValueList.size();
                responseenEntryValueList.addAll(entryValueList);
            } while (startsize < nMatches.intValue());
        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryValueList;
    }

    public static List<Map<String, Object>> endpointurlfunctionality(EndPointRepo endPointRepo, FieldRepo fieldRepo, String endpointName, String toLowerCase, HttpServletRequest request, ARServerUser loggedinuser, Integer noofrecords, String custom, String sortfield, String sortorder, String distinct) {
        List<Map<String, Object>> result = new ArrayList();
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
            List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(
                    loggedinuser,
                    formName,
                    rf,
                    rValue, noofrecords
            );

            LoggerBase.loguserrecords(endpointName, loggedinuser, formName, Constants.FETCH, request);


            if (custom.equalsIgnoreCase("true")) {
                result = ITSMBase.remedyresultsetforitsm(loggedinuser, formName, entries, request, rf);
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
                            return String.class.cast(o1.get(sortfield)).toLowerCase().compareTo(String.class.cast(o2.get(sortfield)).toLowerCase());
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

        return result;
    }

    public static String timestamp(Value val) {
        if (val != null && val.toString() != null) {
            Timestamp callDateTimeTS = (Timestamp) val.getValue();
            return callDateTimeTS.toString().substring(callDateTimeTS.toString().indexOf("=") + 1, callDateTimeTS.toString().indexOf("]"));
        } else {
            return "";
        }
    }


    public static Map<String, Object> getTicketEntrywithfileattachmentandhistory(ARServerUser arServerUser, String formName, String entryId, HttpServletRequest request) {
        Map<String, Object> stringStringMap = new HashMap<>();
        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            Entry entry = arServerUser.getEntry(formName, entryId, null);

            Set<Integer> fieldIds = entry.keySet();
            Map<Integer, String> allFormFields = getFormFields2(arServerUser, formName);

            String srtaskformName = "SR:Task";
            String securepatientformName = "CMN:PatientInfo";
            String ptcasehistoryformName = "PT:Case History";
            String ticketupdateformName = "CMN:TicketUpdate";
            String kbcasehistoryformName = "CMN:KB_Case_History";
            String groupformname = "Group";
            String clientcodename = "CMN:ClientInfo";


            fields.forEach(field -> {
                Value val = entry.get(field.getFieldID());
                String value = "";

                if (val != null && val.toString() != null && field instanceof DateTimeField) {
                    value = timestamp(val);
                } else {
                    value = val == null ? null : val.toString();
                }

                if (field instanceof AttachmentField) {
                    try {
                        stringStringMap.put(allFormFields.get(field.getFieldID()), createattatchmenturi(arServerUser, formName, entryId, field, val, request, entry));
                    } catch (Exception e) {
                        stringStringMap.put(allFormFields.get(field.getFieldID()), null);
                    }
                } else {
                    stringStringMap.put(allFormFields.get(field.getFieldID()), value);
                }
            });
            int[] clientcodefields = new int[]{
                    536870913,//	Client
                    536871013,//	Client Code Name
                    536870918,//	Client Name
            };

            List<Entry> entryList = queryEntrysByQual(arServerUser, clientcodename, clientcodefields, "");
            Map<String, String> clientcodemapping = new HashMap<>();

            entryList.stream().forEach(entry1 -> clientcodemapping.put(entry1.get(536870913).toString(), entry1.get(536871013).toString()));

            int[] grouprequiredfield = new int[]{
                    7,//	Status
                    8,//	Long Group Name
                    9,//	Comments
                    15,//	Status History
                    15,//Status History
                    105,//	Group Name
                    106,//	Group ID
            };


            int[] srtrequiredfield = new int[]{
                    1,//	Task ID
                    2,//	Submitter
                    3,//	Create Date
                    4,//	SR Assignee
                    5,//	Last Modified By
                    6,//	Modified Date
                    7,//	Status
                    8,//	Task
                    15,//	Status History
                    112,//	zAssigned Group
                    704000142,// Parent Request ID
                    704000085,//  Request Type
                    704000086,//  Task Details
                    704000115,//   Task Sequence
                    536870934,//  Pre-defined Task ID
                    536870943,//Originating Task ID
                    700001052,//	Assigned Group
                    700001059,//	Assigned Individual
                    700001126,//	zCurrentAssign
                    700001128,//	zAssignGroupID
                    536871008,//	Last Update Date
                    200000006,//	Department
                    200000007,//	Building
                    200000012,//	Client
                    536870942,//  Complete Date/Time
                    536870944,//  Actual Task Time (min)
                    700001012, //Email Address
                    700001053,//Service Request Summary
                    536870945,//Assigned Date/Time
                    536870966,//	IAM Application
                    536870967,//	IAM Client
                    536870968,//	User GUID
                    700001054,//	Work Log
                    700001058,//	Audit Trail
                    702170968,//	IAM Application Login ID
                    702270969,//	IAM Temp Password
                    702370970,//	IAM Notes
                    704000152,//	Attachment 1
                    704000153,//	Attachment 2
                    910000146,//	Attachment 3
                    910000147,//	Attachment 4
                    910000148,//	Attachment 5

            };

            int[] ptreqfield = new int[]{
                    3,//	Create Date
                    2,//	Submitter
                    4,//	Assignee Login
                    536870914,//	Case ID
                    536870942,//	Case Status
                    7,//	Record Status
                    700001528,//	Priority
                    700001544,//	Pending Reason
                    536870917,//	Assigned Group
                    536870918,//	Assigned Individual
                    536870923,//	Time Spent in Minutes
                    536870929,//	Time Spent Date/Time
                    536870943,//	Reprioritization Authorizer
                    700001528,//	Priority
            };

            int[] updateticketfield = new int[]{
                    536870914,//	Type
                    536870913,//	ACD Call
                    3,//	Create Date
                    2,//	Submitter Old
                    536870919,//	Original Submitter
            };

            int[] kbupdateticketfield = new int[]{
                    3,//	Create Date
                    703303859,//	KB Article
                    536870921,//	KB Title
                    2,//	Submitter
            };

            int[] secureticketfield = new int[]{
                    1,//	Request ID
                    2,//	Submitter
                    3,//	Create Date
                    4,//	Assigned To
                    5,//	Last Modified By
                    6,//	Modified Date
                    7,//	Status
                    8,//	Source Instance ID,
                    15,//	Status History,
                    112,//	zAssigned Group,
                    536870930,//	Confidential Information
                    536870913,//	Secure/Patient Information
                    536870920,//Source Friendly ID
                    536870924,//Attachment1
                    536870925,//Attachment2
                    536870926,//Attachment3
                    536870927,//Attachment4
                    536870928,//Attachment5
                    700001120,//zD_CernerRequestID
                    700003813,//Traffic Flow Control
                    700003821,//Integration Ticket #
                    734613842// Integration Update Flag
            };

            Map<Integer, String> subTaskFields = getFormFields2(arServerUser, srtaskformName);
            Map<Integer, String> ptcasefields = getFormFields2(arServerUser, ptcasehistoryformName);
            Map<Integer, String> updateticketfields = getFormFields2(arServerUser, ticketupdateformName);
            Map<Integer, String> kbhistoryfields = getFormFields2(arServerUser, kbcasehistoryformName);
            Map<Integer, String> securepatientinfofields = getFormFields2(arServerUser, securepatientformName);
            List<Field> securefields = arServerUser.getListFieldObjects(securepatientformName);
            List<Field> subtaskfields = arServerUser.getListFieldObjects(srtaskformName);


            List<Entry> subTask = queryEntrysByQual(
                    arServerUser,
                    srtaskformName,
                    srtrequiredfield,
                    "('Parent Request ID' = \"" + entryId + "\")", null, 704000115, com.bmc.arsys.api.Constants.AR_SORT_ASCENDING
            );   //704000115 task sequence
            List<Entry> ptcase = queryEntrysByQual(
                    arServerUser,
                    ptcasehistoryformName,
                    ptreqfield,
                    "('Case ID' = \"" + entryId + "\")"
            );

            List<Entry> ticketupdateentries = queryEntrysByQual(
                    arServerUser,
                    ticketupdateformName,
                    updateticketfield,
                    "('Short Description' = \"" + entryId + "\")"
                    /* "('Source ID' = \"" + entryId + "\")"*/
            );
            List<Entry> kbcasehistoryentries = queryEntrysByQual(
                    arServerUser,
                    kbcasehistoryformName,
                    kbupdateticketfield,
                    "('Case ID' = \"" + entryId + "\")"
            );

            List<Entry> securepatientinfoentries = queryEntrysByQual(
                    arServerUser,
                    securepatientformName,
                    secureticketfield,
                    "('Source Friendly ID' = \"" + entryId + "\")"
            );

            List<Entry> groupentries = queryEntrysByQualwithmaxrecords(arServerUser, groupformname, grouprequiredfield, "'Status'=\"Current\"", 0);

            Map<String, String> groups = new HashMap<>();
            groupentries.forEach(entry1 -> {
                groups.put(entry1.get(106).toString(), entry1.get(105).toString());
            });

            List<Map<String, Object>> subTaskList = new ArrayList<>();
            List<Map<String, Object>> securepatientinfolist = new ArrayList<>();
            List<Map<String, String>> ptcasehistory = new ArrayList<>();
            List<Map<String, String>> ticketupdate = new ArrayList<>();
            List<Map<String, String>> kbcasehistory = new ArrayList<>();

            for (Entry entry1 : subTask) {
                HashMap<String, Object> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    Field field = null;
                    for (Field field1 : subtaskfields) {
                        if (integer == field1.getFieldID()) {
                            field = field1;
                        }
                    }
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && field instanceof DateTimeField) {
                        value = timestamp(entry1.get(integer));
                        stringHashMap.put(subTaskFields.get(integer), value);
                    } else if (field instanceof AttachmentField && entry1.get(integer) != null && entry1.get(integer).toString() != null) {
                        try {
                            stringHashMap.put(subTaskFields.get(integer), createattatchmenturi(arServerUser, srtaskformName, entry1.get(1).toString(), field, entry1.get(integer), request, entry1));
                        } catch (Exception e) {
                            stringStringMap.put(subTaskFields.get(integer), null);
                        }
                    } else if ((integer == 700001054 || integer == 700001058) && entry1.get(integer) != null) {
                        stringHashMap.put(subTaskFields.get(integer), parseWorkLog(entry1.get(integer).toString()));
                    } else {
                        stringHashMap.put(subTaskFields.get(integer), entry1.get(integer).toString());
                    }
                }
                subTaskList.add(stringHashMap);
            }


            for (Entry entry1 : ptcase) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {
                        value = timestamp(entry1.get(integer));
                    } else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(ptcasefields.get(integer), value);
                }

                ptcasehistory.add(stringHashMap);
            }

            for (Entry entry1 : ticketupdateentries) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {
                        value = timestamp(entry1.get(integer));
                    } else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(updateticketfields.get(integer), value);
                }

                ticketupdate.add(stringHashMap);
            }
            for (Entry entry1 : kbcasehistoryentries) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {
                        value = timestamp(entry1.get(integer));
                    } else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(kbhistoryfields.get(integer), value);
                }
                kbcasehistory.add(stringHashMap);
            }

            for (Entry entry1 : securepatientinfoentries) {
                HashMap<String, Object> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    if (securepatientinfofields.get(integer) != null) {
                        String value = "";
                        Field field = null;
                        for (Field field1 : securefields) {
                            if (integer == field1.getFieldID()) {
                                field = field1;
                            }
                        }
                        if (field instanceof DateTimeField) {
                            stringHashMap.put(securepatientinfofields.get(integer), timestamp(entry1.get(integer)));
                        } else if (field instanceof AttachmentField && entry1.get(integer) != null && entry1.get(integer).toString() != null) {
                            try {
                                stringHashMap.put(securepatientinfofields.get(integer), createattatchmenturi(arServerUser, securepatientformName, entry1.get(1).toString(), field, entry1.get(integer), request, entry1));
                            } catch (Exception e) {
                                stringStringMap.put(securepatientinfofields.get(integer), null);
                            }
                        } else if ((integer == 536870913 || integer == 536870930) && entry1.get(integer) != null) {
                            stringHashMap.put(securepatientinfofields.get(integer), parseWorkLog(entry1.get(integer).toString()));
                        } else if (integer == 112 && entry1.get(integer) != null) {
                            stringHashMap.put(securepatientinfofields.get(integer), groups.get(entry1.get(integer).toString().replaceAll(";", "")));
                        } else {
                            stringHashMap.put(securepatientinfofields.get(integer), entry1.get(integer).toString());
                        }
                    }
                }
                securepatientinfolist.add(stringHashMap);
            }

            stringStringMap.put("sub_task", subTaskList);
            stringStringMap.put("securepatientinfo", securepatientinfolist);
            stringStringMap.put("pt_casehistory", ptcasehistory);
            stringStringMap.put("ticketupdatehistory", ticketupdate);
            stringStringMap.put("kb_history", kbcasehistory);
            if (stringStringMap.containsKey("Work Log") && stringStringMap.get("Work Log") != null) {
                String wl = stringStringMap.get("Work Log").toString();
                stringStringMap.put("Work Log", parseWorkLog(wl));
            }
            if (stringStringMap.containsKey("Customer Work Log") && stringStringMap.get("Customer Work Log") != null) {
                String wl = stringStringMap.get("Customer Work Log").toString();
                stringStringMap.put("Customer Work Log", parseWorkLog(wl));
            }
            if (stringStringMap.containsKey("Audit Trail") && stringStringMap.get("Audit Trail") != null) {
                String wl = stringStringMap.get("Audit Trail").toString();
                stringStringMap.put("Audit Trail", parseWorkLog(wl));
            }
            if (stringStringMap.containsKey("zAssigned to wrong group log ") && stringStringMap.get("zAssigned to wrong group log ") != null) {
                String wl = stringStringMap.get("zAssigned to wrong group log ").toString();
                stringStringMap.put("zAssigned to wrong group log ", parseWorkLog(wl));
            }
            if (stringStringMap.containsKey("Adequate Details") && stringStringMap.get("Adequate Details") != null) {
                String wl = stringStringMap.get("Adequate Details").toString();
                stringStringMap.put("Adequate Details", parseWorkLog(wl));
            }
            stringStringMap.put("clientcodename", clientcodemapping.get(stringStringMap.get("Client")) != null ? clientcodemapping.get(stringStringMap.get("Client")) : "");

            if (formName.equalsIgnoreCase("sr:task")) {
                List<Map<String, Object>> tempresultset = ITSMBase.srtwithsrfields(arServerUser, stringStringMap.get("Parent Request ID").toString(), request);
                if (!tempresultset.get(0).isEmpty()) {
                    stringStringMap.put("zD_SR Status", tempresultset.get(0).get("Status"));
                    stringStringMap.put("zD_SR Description", tempresultset.get(0).get("Description"));
                }
            }


        } catch (Exception e) {
            stringStringMap.clear();
            stringStringMap.put("Error", e.getMessage());
        }
        return stringStringMap;
    }


    public static List<RemedyBase.WorkLog> parseWorkLog(String workLog) {
        List<RemedyBase.WorkLog> workLogs = new ArrayList<>();

        if (workLog != null && !workLog.trim().isEmpty()) {
            String[] stxt = workLog.split("], \\[");

            for (String s : stxt) {
                String user = s.substring(s.indexOf("User="), s.indexOf(",Time Val")).replace("User=", "");
                String time = s.substring(s.indexOf("Val="), s.indexOf(",Text=")).replace("Val=", "");
                String text = s.substring(s.indexOf("Text="), s.indexOf("],Appended") < 0 ? s.length() : s.indexOf("],Appended")).replace("Text=", "");

                RemedyBase.WorkLog workLog1 = new RemedyBase.WorkLog();
                workLog1.setText(text);
                workLog1.setTime(time.substring(time.indexOf("=") + 1, time.indexOf("]")));
                workLog1.setUser(user);
                workLogs.add(workLog1);
            }
        }
        return workLogs;
    }


    public static Map<String, Object> getTicketEntry(ARServerUser arServerUser, String formName, String entryId) {
        Map<String, Object> stringStringMap = new HashMap<>();
        try {
            Entry entry = arServerUser.getEntry(formName, entryId, null);
            Set<Integer> fieldIds = entry.keySet();
            Map<Integer, String> allFormFields = getFormFields2(arServerUser, formName);


            for (Integer fieldId : fieldIds) {
                Value val = entry.get(fieldId);
                String value = "";
                if (val != null && val.toString() != null && val.toString().startsWith("[Timestamp=")) {
                    value = timestamp(val);
                } else {
                    value = val == null ? null : val.toString();
                }
                stringStringMap.put(allFormFields.get(fieldId), value);
            }

            int[] srtrequiredfield = new int[]{
                    1,//	Task ID
                    2,//	Submitter
                    3,//	Create Date
                    4,//	SR Assignee
                    5,//	Last Modified By
                    6,//	Modified Date
                    7,//	Status
                    8,//	Task
                    15,//	Status History
                    112,//	zAssigned Group
                    704000142,// Parent Request ID
                    704000085,//  Request Type
                    704000086,//  Task Details
                    704000115,//   Task Sequence
                    536870934,//  Pre-defined Task ID
                    536870943,//Originating Task ID
                    700001052,//	Assigned Group
                    700001059,//	Assigned Individual
                    700001126,//	zCurrentAssign
                    700001128,//	zAssignGroupID
                    536871008,//	Last Update Date
                    200000006,//	Department
                    200000007,//	Building
                    200000012,//	Client
                    536870942,//  Complete Date/Time
                    536870944,//  Actual Task Time (min)
                    700001012, //Email Address
                    700001053,//Service Request Summary
                    536870945//Assigned Date/Time

            };

            Map<Integer, String> subTaskFields = getFormFields2(arServerUser, "SR:Task");

            List<Entry> subTask = queryEntrysByQual(
                    arServerUser,
                    "SR:Task",
                    srtrequiredfield,
                    "('Parent Request ID' = \"" + entryId + "\")"
            );

            List<Map<String, String>> subTaskList = new ArrayList<>();
            for (Entry entry1 : subTask) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {
                        value = timestamp(entry1.get(integer));
                    } else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(subTaskFields.get(integer), value);
                }

                subTaskList.add(stringHashMap);
            }

            Collections.sort(subTaskList, new Comparator<Map<String, String>>() {
                public int compare(final Map<String, String> o1, final Map<String, String> o2) {
                    if (o1.get("Task Sequence") instanceof String && o2.get("Task Sequence") instanceof String)
                        return String.class.cast(o1.get("Task Sequence")).compareTo(String.class.cast(o2.get("Task Sequence")));
                    else
                        return 0;
                }
            });

            stringStringMap.put("sub_task", subTaskList);
            if (stringStringMap.containsKey("Work Log") && stringStringMap.get("Work Log") != null) {
                String wl = stringStringMap.get("Work Log").toString();
                stringStringMap.put("Work Log", parseWorkLog(wl));
            }
            if (stringStringMap.containsKey("Customer Work Log") && stringStringMap.get("Customer Work Log") != null) {
                String wl = stringStringMap.get("Customer Work Log").toString();
                stringStringMap.put("Customer Work Log", parseWorkLog(wl));
            }


        } catch (ARException e) {
            return null;
        }
        return stringStringMap;
    }

    public static String usergroups(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");

        String query = "select group_name from CMN_Notification_Assignments with (nolock) where (Functional_Type = 1) AND (Status = 2) AND login_id =\'" + loginid + "\' order by group_name ";

        SQLResult sqlResult = user.getListSQL(query, 0, true);

        if (sqlResult.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("\'" + content.get(0).toString() + "\',");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        return builder.toString();
    }


    public static String usersallgroups(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");

        String query = "select distinct group_name from group_x where group_id in (SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';')) ";

        SQLResult sqlResult = user.getListSQL(query, 0, true);

        if (sqlResult.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("" + content.get(0).toString() + ",");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        return builder.toString();
    }

    public static String usersallgroupsid(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");

        String query = "select distinct group_id from group_x where group_id in (SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';')) ";

        SQLResult sqlResult = user.getListSQL(query, 0, true);

        if (sqlResult.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("" + content.get(0).toString() + ",");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        return builder.toString();
    }

    public static String getallclients(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");

        String query = "select client from CMN_Notification_Assignments with (nolock) where (Functional_Type = 1) AND (Status = 2) AND login_id =\'" + loginid + "\' order by group_name ";

        SQLResult sqlResult = user.getListSQL(query, 0, true);

        if (sqlResult.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("\'" + content.get(0).toString() + "\',");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        return builder.toString();
    }

    public static List<String> convertregexseperatedstringtoList(String regexseparatedstring, String regexpattern) {
        return Arrays.asList(regexseparatedstring.split(regexpattern));
    }


    public static String fetchuserclients(String loginid, ARServerUser user) throws Exception {
        List<GroupInfo> groupInfo = new ArrayList<>();
        StringBuilder builder = new StringBuilder("");
        try {
            try {
                groupInfo = user.getListGroup(loginid, null);
            } catch (ARException e) {
                System.out.println(e.getMessage());
            }


            Boolean admin = false;
            if (groupInfo != null && !groupInfo.isEmpty()) {
                for (int i = 0; i < groupInfo.size(); i++) {
                    if (Constants.getAdminsupportgroupsname().contains(groupInfo.get(i).getName())) {
                        admin = true;
                        break;
                    }
                    if (i == groupInfo.size() - 1)
                        builder.append("'" + groupInfo.get(i).getName() + "'");
                    else
                        builder.append("'" + groupInfo.get(i).getName() + "',");
                }
            } else {
                SQLResult result = user.getListSQL("select client from cmn_people_information with (nolock) where login_id='" + loginid + "' and status in (" + Constants.getActivegroupsids() + ") order by client", 0, true);
                builder.delete(0, builder.length());
                for (int i = 0; i < result.getContents().size(); i++) {

                    if (i == result.getContents().size() - 1)
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "'");
                    else
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "',");
                }
            }
            if (admin) {
                SQLResult result = user.getListSQL("select group_name from group_x with (nolock) where group_type=2 and group_category=0 order by group_name", 0, true);
                builder.delete(0, builder.length());
                for (int i = 0; i < result.getContents().size(); i++) {

                    if (i == result.getContents().size() - 1)
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "'");
                    else
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "',");

                }
            }
        } catch (Exception e) {
            throw new Exception("User not associated with any groups!");
        }
        return builder.toString();
    }


    public static Map<String, List<String>> fetchclientmasterclient(ARServerUser adminuser) {

        List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(adminuser, "CMN:ClientInfo", new int[]{536870914, 536870913, 536871013}, "'Status'=\"Active\" OR 'Status'=\"Onboarding\" OR 'Status'=\"Container\" OR 'Status'=\"Bundle\"", 0);

        Map<String, List<String>> clientmaster = new HashMap<>();
        Map<String, List<String>> clientcodemaster = new HashMap<>();
        Map<String, String> clientcodename = new HashMap<>();
        List<String> subclients = new ArrayList<>();

        for (Entry entry : entries) {
            subclients = new ArrayList<>();
            if (clientmaster.containsKey(entry.get(536870914).toString())) {
                subclients = clientmaster.get(entry.get(536870914).toString());
                subclients.add(entry.get(536870913).toString());
            } else {
                subclients.add(entry.get(536870913).toString());
            }
            clientmaster.put(entry.get(536870914).toString(), subclients);
            clientcodename.put(entry.get(536870913).toString(), entry.get(536871013).toString());
        }

        clientmaster.forEach((s, strings) -> {
            List<String> clientcodenames = new ArrayList<>();
            for (String string : strings) {
                clientcodenames.add(clientcodename.get(string));
            }
            clientcodemaster.put(clientcodename.get(s), clientcodenames);
        });

        return clientcodemaster;
    }

    public static String fetchuserclientsforoncall(String loginid, ARServerUser user) throws Exception {
        List<GroupInfo> groupInfo = new ArrayList<>();
        StringBuilder builder = new StringBuilder("");
        try {
            try {
                groupInfo = user.getListGroup(loginid, null);
            } catch (ARException e) {
                System.out.println(e.getMessage());
            }


            Boolean admin = false;
            if (groupInfo != null && !groupInfo.isEmpty()) {
                for (int i = 0; i < groupInfo.size(); i++) {
                    if (Constants.getAdmingroupsname().contains(groupInfo.get(i).getName())) {
                        admin = true;
                        break;
                    }
                    if (i == groupInfo.size() - 1)
                        builder.append("'" + groupInfo.get(i).getName() + "'");
                    else
                        builder.append("'" + groupInfo.get(i).getName() + "',");
                }
            } else {
                SQLResult result = user.getListSQL("select client from cmn_people_information with (nolock) where login_id='" + loginid + "' and status in (" + Constants.getActivegroupsids() + ") order by client", 0, true);
                builder.delete(0, builder.length());
                for (int i = 0; i < result.getContents().size(); i++) {

                    if (i == result.getContents().size() - 1)
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "'");
                    else
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "',");
                }
            }
            if (admin) {
                SQLResult result = user.getListSQL("select group_name from group_x with (nolock) where group_type=2 and group_category=0 order by group_name", 0, true);
                builder.delete(0, builder.length());
                for (int i = 0; i < result.getContents().size(); i++) {

                    if (i == result.getContents().size() - 1)
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "'");
                    else
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "',");

                }
            }
        } catch (Exception e) {
            throw new Exception("User not associated with any groups!");
        }
        return builder.toString();
    }

    public static String fetchuserclientsconsideringctsasadmin(String loginid, ARServerUser user) throws Exception {
        List<GroupInfo> groupInfo = new ArrayList<>();
        StringBuilder builder = new StringBuilder("");
        try {
            try {
                groupInfo = user.getListGroup(loginid, null);
            } catch (ARException e) {
                System.out.println(e.getMessage());
            }


            Boolean admin = false;

            SQLResult result = user.getListSQL("select client from cmn_people_information with (nolock) where login_id='" + loginid + "' and status in (" + Constants.getActivegroupsids() + ") order by client", 0, true);
            builder.delete(0, builder.length());
            for (int i = 0; i < result.getContents().size(); i++) {
                if (result.getContents().get(i).get(0).toString().equalsIgnoreCase("CTS")) {
                    admin = true;
                    break;
                }
            }
            if (groupInfo != null && !groupInfo.isEmpty() && !admin) {
                for (int i = 0; i < groupInfo.size(); i++) {
                    if (Constants.getAdminsupportgroupsname().contains(groupInfo.get(i).getName())) {
                        admin = true;
                        break;
                    }
                    if (i == groupInfo.size() - 1)
                        builder.append("'" + groupInfo.get(i).getName() + "'");
                    else
                        builder.append("'" + groupInfo.get(i).getName() + "',");
                }
            } else {
                if (!admin) {
                    result = user.getListSQL("select client from cmn_people_information with (nolock) where login_id='" + loginid + "' and status in (" + Constants.getActivegroupsids() + ") order by client", 0, true);
                    builder.delete(0, builder.length());
                    for (int i = 0; i < result.getContents().size(); i++) {

                        if (result.getContents().get(i).get(0).toString().equalsIgnoreCase("CTS")) {
                            admin = true;
                        }

                        if (i == result.getContents().size() - 1)
                            builder.append("'" + result.getContents().get(i).get(0).toString() + "'");
                        else
                            builder.append("'" + result.getContents().get(i).get(0).toString() + "',");
                    }
                }
            }
            if (admin) {
                result = user.getListSQL("select group_name from group_x with (nolock) where group_type=2 and group_category=0 order by group_name", 0, true);
                builder.delete(0, builder.length());
                for (int i = 0; i < result.getContents().size(); i++) {

                    if (i == result.getContents().size() - 1)
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "'");
                    else
                        builder.append("'" + result.getContents().get(i).get(0).toString() + "',");

                }
            }
        } catch (Exception e) {
            throw new Exception("User not associated with any groups!");
        }
        return builder.toString();
    }

    public static Boolean adminuserornot(String loginid, ARServerUser user) throws ARException {
        SQLResult sqlResult;
        String adminquery = "SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';') WHERE  value IN (" + com.htc.remedy.constants.Constants.getAdminsupportgroupsid() + ")";

        sqlResult = user.getListSQL(adminquery, 0, true);
        if (sqlResult.getContents().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean adminandappvacationuserornot(String loginid, ARServerUser user) throws ARException {
        SQLResult sqlResult;
        String adminquery = "SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';') WHERE  value IN (" + com.htc.remedy.constants.Constants.getAdminsupportgroupsid() + ")";

        sqlResult = user.getListSQL(adminquery, 0, true);
        if (sqlResult.getContents().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean adminandsupportuserornot(String loginid, ARServerUser user) throws ARException {
        SQLResult sqlResult;
        String adminquery = "SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';') WHERE  value IN (" + com.htc.remedy.constants.Constants.getAdminsupportgroupsid() + ")";

        sqlResult = user.getListSQL(adminquery, 0, true);
        if (sqlResult.getContents().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean adminctsuserornotand(String loginid, ARServerUser user) throws ARException {
        SQLResult sqlResult;
        String adminquery = "SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';') WHERE  value IN (" + com.htc.remedy.constants.Constants.getAdminsupportgroupsid() + ",8000)";

        sqlResult = user.getListSQL(adminquery, 0, true);
        if (sqlResult.getContents().size() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static ARServerUser impersonateuser(ARServerUser adminuser, String loginid) throws ARException {
        ARServerUser user = new ARServerUser();
        user.setUser(adminuser.getUser());
        user.setPassword(adminuser.getPassword());
        user.setServer(adminuser.getServer());
        user.setPort(adminuser.getPort());
        user.login();
        user.impersonateUser(loginid);
        return user;
    }

    public static void revokeuserandlogout(ARServerUser user) {
        try {
            user.impersonateUser(null);
            user.logout();
        } catch (ARException e) {
            e.printStackTrace();
        }
    }


    public static String validatesupportuserfromtoken(Token token) throws Exception {
        String userloginid = "";

        try {
            UserDetails userDetails = SSOBase.ssologindetails(token);
            userloginid = userDetails.getITSM_Login() != null ? userDetails.getITSM_Login() : "";
            return userloginid;
        } catch (Exception e) {

        }
        Token token1 = SSOBase.refreshtoken(token.getRefresh_token());
        UserDetails userDetails = SSOBase.ssologindetails(token1);
        userloginid = userDetails.getITSM_Login() != null ? userDetails.getITSM_Login() : "";
        return userloginid;
    }

    public static List securepatientinforecords(String ticketId, ARServerUser adminuser, HttpServletRequest request) throws Exception {
        String securepatientformName = "CMN:PatientInfo";
        String securepatientformNameaudittrial = "CMN:TrackPatientInfoViewers";
        List<Map<String, Object>> entriess = new ArrayList<>();
        int[] secureticketfield = new int[]{
                1,//	Request ID
                2,//	Submitter
                3,//	Create Date
                4,//	Assigned To
                5,//	Last Modified By
                6,//	Modified Date
                7,//	Status
                8,//	Source Instance ID,
                15,//	Status History,
                112,//	zAssigned Group,
                536870930,//	Confidential Information
                536870913,//	Secure/Patient Information
                536870920,//Source Friendly ID
                536870924,//Attachment1
                536870925,//Attachment2
                536870926,//Attachment3
                536870927,//Attachment4
                536870928,//Attachment5
                700001120,//zD_CernerRequestID
                700003813,//Traffic Flow Control
                700003821,//Integration Ticket #
                734613842// Integration Update Flag
        };


        List<Entry> securepatientinfoentries = queryEntrysByQual(
                adminuser,
                securepatientformName,
                secureticketfield,
                "('Source Friendly ID' = \"" + ticketId + "\")"
        );

        return ITSMBase.remedyresultsetforitsm(adminuser, securepatientformName, securepatientinfoentries, request, secureticketfield);
    }


    public static void smefetchrecords(String formname, String ticketId, ARServerUser adminuser, HttpServletRequest request) throws Exception {

        //  String query = "('Status' = \"Closed\") AND ('zSubmitter Group' != $NULL$) AND (('zAssigned to wrong group' = \"YES\") OR ('But_CouldHaveBeenResolved_yes' = \"Yes\") OR ('AdequateDetailsNo' = \"No\"))";
        int[] ticketmasterclient = new int[]{
                910000161,//	masterclient Client
                7,//	Status
                536871033,//	zSubmitter Group
                536871156,//	zAssigned to wrong group
                820000081,//	AdequateDetailsNo
                910000223,//	but_CouldHaveBeenResolved_yes
        };

        Entry entrysme = adminuser.getEntry(formname, ticketId, ticketmasterclient);


        String formnamesubject = "CMN:SubjectMatterEvaluation";
        Entry entry = new Entry();

        if (entrysme != null && !entrysme.isEmpty()) {
            if (entrysme.get(7).toString().equalsIgnoreCase("5") &&
                    entrysme.get(536871033) != null && entrysme.get(536871033).toString() != null &&
                    entrysme.get(536871156).toString().equalsIgnoreCase("1") &&
                    entrysme.get(820000081).toString().equalsIgnoreCase("0") &&
                    entrysme.get(910000223).toString().equalsIgnoreCase("0")) {
                entry.put(200000012, entrysme.get(910000161));
                entry.put(8, new Value(entrysme.getEntryId()));
                ITSMBase.createTicket(adminuser, formnamesubject, entry);
            }
        }
    }

    public static List srptticketupdate(String formname, String ticketId, Map<String, String> updateParam, ARServerUser adminuser, HttpServletRequest request) {
        int[] updateticketfield = new int[]{
                536870914,//	Type
                536870913,//	ACD Call
                3,//	Create Date
                2,//	Submitter Old
                536870919,//	Original Submitter
        };
        List<Map<String, String>> ticketupdate = new ArrayList<>();
        try {

            String ticketupdateformName = "CMN:TicketUpdate";
            Entry entryticket = adminuser.getEntry(formname, ticketId, new int[]{200000012, 700001052, 700001059, 536871033});
            Map<Integer, String> updateticketfields = getFormFields2(adminuser, ticketupdateformName);

            Entry entry = new Entry();
            entry.put(8, new Value(ticketId));
            entry.put(536870914, new Value(updateParam.get("tickettype").toString()));
            entry.put(536871087, entryticket.get(200000012));
            if (updateParam.get("queue") != null && updateParam.get("queue").toString() != null)
                entry.put(700003747, new Value(updateParam.get("queue").toString()));//queue
            if (updateParam.get("typeofupdate_articleid") != null && updateParam.get("typeofupdate_articleid").toString() != null)
                entry.put(586871271, new Value(updateParam.get("typeofupdate_articleid").toString()));//RoutingArticleID

            if (updateParam.get("700001054") != null && updateParam.get("700001054").toString() != null)
                entry.put(586871270, new Value(updateParam.get("700001054").toString()));//WorklogUpdate

            if (updateParam.get("link_used") != null && updateParam.get("link_used").toString() != null)
                entry.put(586871272, new Value(updateParam.get("link_used").toString()));//LinkUsed

            if (updateParam.get("no_Published_article") != null && updateParam.get("no_Published_article").toString() != null)
                entry.put(586871273, new Value(updateParam.get("no_Published_article").toString()));//NoPublishedArticle

            if (entryticket.get(700001052) != null && entryticket.get(700001052).toString() != null)
                entry.put(700001052, entryticket.get(700001052));//Assigned Group
            if (entryticket.get(700001059) != null && entryticket.get(700001059).toString() != null)
                entry.put(536870916, entryticket.get(700001059));//Assigned Individual
            if (entryticket.get(536871033) != null && entryticket.get(536871033).toString() != null)
                entry.put(536871033, entryticket.get(536871033));//Triage Group


            adminuser.createEntry(ticketupdateformName, entry);
            List<Entry> entries = queryEntrysByQual(
                    adminuser,
                    ticketupdateformName,
                    updateticketfield,
                    "('Short Description' = \"" + ticketId + "\")"
                    /* "('Source ID' = \"" + entryId + "\")"*/
            );

            for (Entry entry1 : entries) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {
                        value = timestamp(entry1.get(integer));
                    } else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(updateticketfields.get(integer), value);
                }

                ticketupdate.add(stringHashMap);
            }
            return ticketupdate;

        } catch (Exception e) {
            e.getMessage();
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put("error", e.getMessage());
            ticketupdate.add(stringHashMap);
            return ticketupdate;
        }
    }


    public static List pt_casehistory(String ticketId, ARServerUser adminuser) {
        int[] ptreqfield = new int[]{
                3,//	Create Date
                2,//	Submitter
                4,//	Assignee Login
                536870914,//	Case ID
                536870942,//	Case Status
                7,//	Record Status
                700001528,//	Priority
                700001544,//	Pending Reason
                536870917,//	Assigned Group
                536870918,//	Assigned Individual
                536870923,//	Time Spent in Minutes
                536870929,//	Time Spent Date/Time
                536870943,//	Reprioritization Authorizer
                700001528,//	Priority
        };
        try {
            List<Map<String, String>> ptcasehistory = new ArrayList<>();
            String ptcasehistoryformName = "PT:Case History";

            Map<Integer, String> ptcasefields = getFormFields2(adminuser, ptcasehistoryformName);

            List<Entry> ptcase = queryEntrysByQual(
                    adminuser,
                    ptcasehistoryformName,
                    ptreqfield,
                    "('Case ID' = \"" + ticketId + "\")"
            );

            for (Entry entry1 : ptcase) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {
                        value = timestamp(entry1.get(integer));
                    } else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(ptcasefields.get(integer), value);
                }

                ptcasehistory.add(stringHashMap);
            }

            return ptcasehistory;

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    public static List securepatientinfo(String ticketId, ARServerUser adminuser, HttpServletRequest request) throws Exception {
        String securepatientformName = "CMN:PatientInfo";
        String securepatientformNameaudittrial = "CMN:TrackPatientInfoViewers";
        List<Map<String, Object>> entriess = new ArrayList<>();
        int[] secureticketfield = new int[]{
                1,//	Request ID
                2,//	Submitter
                3,//	Create Date
                4,//	Assigned To
                5,//	Last Modified By
                6,//	Modified Date
                7,//	Status
                8,//	Source Instance ID,
                15,//	Status History,
                112,//	zAssigned Group,
                536870930,//	Confidential Information
                536870913,//	Secure/Patient Information
                536870920,//Source Friendly ID
                536870924,//Attachment1
                536870925,//Attachment2
                536870926,//Attachment3
                536870927,//Attachment4
                536870928,//Attachment5
                700001120,//zD_CernerRequestID
                700003813,//Traffic Flow Control
                700003821,//Integration Ticket #
                734613842// Integration Update Flag
        };

        int[] req = new int[]{

                1,//	Request ID
                2,//	Submitter
                3,//	Create Date
                4,//	Assigned To
                5,//	Last Modified By
                6,//	Modified Date
                7,//	Status
                8,//	IP Address
                15,//	Status History
                536870913,//	Source Instance ID
                536870914,//	Panel Holder
                536870915,//	pnl_Admin Only
                600000002,//	Source Friendly ID
        };

        List<Entry> securepatientinfoentries = queryEntrysByQual(
                adminuser,
                securepatientformName,
                secureticketfield,
                "('Source Friendly ID' = \"" + ticketId + "\")"
        );

        List<Entry> audittrial = ITSMBase.queryEntrysByQualwithmaxrecords(adminuser, securepatientformNameaudittrial, req, "'Source Friendly ID'=\"" + ticketId + "\"", 0);

        entriess = ITSMBase.remedyresultsetforitsm(adminuser, securepatientformName, securepatientinfoentries, request, secureticketfield);
        entriess.get(0).put("audit_trial", ITSMBase.remedyresultsetforitsm(adminuser, securepatientformNameaudittrial, audittrial, request, req));
        return entriess;
    }

    public static Object audittrialforsecurepatientinfo(String ticketId, ARServerUser adminuser, HttpServletRequest request) throws Exception {
        String securepatientformNameaudittrial = "CMN:TrackPatientInfoViewers";
        Map<String, Object> audittrialrecords = new HashMap<>();
        int[] req = new int[]{

                1,//	Request ID
                2,//	Submitter
                3,//	Create Date
                4,//	Assigned To
                5,//	Last Modified By
                6,//	Modified Date
                7,//	Status
                8,//	IP Address
                15,//	Status History
                536870913,//	Source Instance ID
                536870914,//	Panel Holder
                536870915,//	pnl_Admin Only
                600000002,//	Source Friendly ID
        };
        List<Entry> audittrial = ITSMBase.queryEntrysByQualwithmaxrecords(adminuser, securepatientformNameaudittrial, req, "'Source Friendly ID'=\"" + ticketId + "\"", 0);
        return ITSMBase.remedyresultsetforitsm(adminuser, securepatientformNameaudittrial, audittrial, request, req);
    }


    public static Map<String, Object> validatesupportscheduleinsert(ARServerUser adminuser, Map<String, Object> ticketFields, String loginid, String group) throws Exception {

        Boolean loginidfound = false, groupfound = false;
        for (String s : ticketFields.keySet()) {
            if (loginid.isEmpty()) {
                if (s.toLowerCase().equalsIgnoreCase("loginid")) {
                    loginid = ticketFields.get(s).toString();
                    loginidfound = true;
                }
            } else {
                loginidfound = true;
            }
            if (s.toLowerCase().equalsIgnoreCase("group")) {
                group = ticketFields.get(s).toString();
                groupfound = true;
            }
        }
        if (groupfound && loginidfound) {
            ticketFields.remove("loginid");
            ticketFields.remove("group");
        } else {
            throw new Exception("Group or Login ID not Found for inserting CMN:PrimarySupportSchedule!");
        }

        String groups = ITSMBase.usersallgroups(loginid, adminuser);

        List<String> groupslist = ITSMBase.convertregexseperatedstringtoList(groups, ",");
        if (group.startsWith("CTS-") && !groupslist.contains("CSS")) {   //!groupslist.contains(group) &&
            throw new Exception("You must be designated as CareTech Staff or a member of this group to create/modify this entry.");
        } else {
        }
        return ticketFields;
    }

    public static String validateformnameexist(Map<String, String> ticketFields) throws Exception {
        String formname = "";
        try {
            formname = ticketFields.get("formName");
        } catch (Exception e) {
            throw new Exception("Formname or Ticket id not available");
        }
        return formname;
    }

    public static String validateticketidexist(Map<String, String> ticketFields) throws Exception {
        String ticketId = "";
        try {
            ticketId = ticketFields.get("ticketId");
        } catch (Exception e) {
            throw new Exception("Formname or Ticket id not available");
        }
        return ticketId;
    }

    public static Map<String, String> removeformnameticketidparams(Map<String, String> updateParam) throws Exception {
        updateParam.remove("formName");
        updateParam.remove("ticketId");
        return updateParam;
    }


    public static Map<String, String> validatesupportscheduleupdate(ARServerUser adminuser, Map<String, String> updateParam, String loginid, String group, String groupid) throws Exception {

        boolean groupfound = false, groupidfound = false, loginidfound = false;
        for (String s : updateParam.keySet()) {
            if (s.equals("group")) {
                group = updateParam.get(s);
                groupfound = true;
            }
            if (s.equals("groupid")) {
                groupid = updateParam.get(s);
                groupidfound = true;
            }
            if (loginid.isEmpty()) {
                if (s.equals("loginid")) {
                    loginid = updateParam.get(s);
                    loginidfound = true;
                }
            } else {
                loginidfound = true;
            }
        }
        if (groupfound && loginidfound && groupidfound) {
            updateParam.remove("loginid");
            updateParam.remove("group");
            updateParam.remove("groupid");
        } else {
            throw new Exception("Login Id or group not found");
        }

        String groups = ITSMBase.usersallgroups(loginid, adminuser);        //admin user's accessible query
        String groupsid = ITSMBase.usersallgroupsid(loginid, adminuser);        //admin user's accessible query
        List<String> groupslist = ITSMBase.convertregexseperatedstringtoList(groups, ",");
        List<String> groupslistid = ITSMBase.convertregexseperatedstringtoList(groupsid, ",");
        if (!groupslistid.contains(groupid) && group.startsWith("CTS-") && !groupslist.contains("CSS")) {  //!groupslist.contains(group) &&
            throw new Exception("You must be designated as CareTech Staff or a member of this group to create/modify this entry.");
        } else {
            return updateParam;
        }
    }


    public static Entry parseentryvalueforinsert(Map<String, Object> ticketFields) {
        Entry entryData = new Entry();
        for (Map.Entry<String, Object> entry : ticketFields.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString() == null || entry.getValue().toString().equalsIgnoreCase("null") || entry.getValue().toString().equalsIgnoreCase("NO_VALUE") || entry.getValue().toString().equalsIgnoreCase("NOVALUE") || entry.getValue().toString().isEmpty()) {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value());
            } else {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
            }
        }
        return entryData;
    }

    public static Boolean validateparam(String param) {
        if (param == null) {
            return true;
        }
        String validateparam = StringEscapeUtils.escapeSql(param);
        String regexpattern = Constants.getSpecialcharactersregexpatternforclientsandkbnumber();
        if (Pattern.matches(regexpattern, validateparam)) {
            return true;
        }
        return false;
    }

    public static String escapesql(String param) {
        if (param == null) {
            return null;
        }
        return StringEscapeUtils.escapeSql(param);
    }


    public static Boolean validatefields(String param) {
        if (param == null) {
            return true;
        }

        if (validateparam(param)) {
            return true;
        } else if (Pattern.matches(Constants.getSPECIALREXPATTERN(), param)) {
            return true;
        } else {
            String[] sqlregex = Constants.getSQLREGEXPATTERN().split(",");
            List<String> sqlregexrecords = Arrays.asList(sqlregex);
            return !sqlregexrecords.parallelStream().anyMatch(s -> param.toUpperCase().contains(s));
        }
    }

    public static Entry putAttachmentwithnovalue(ARServerUser arServerUser, String formName, String entryId, Map<String, File> attach, Map<String, String> fields) throws Exception {


        Map<String, String> previostempvaluefromname = new HashMap<>();
        previostempvaluefromname.put("Attachment 1", "586871221");
        previostempvaluefromname.put("Attachment 2", "586871222");
        previostempvaluefromname.put("Attachment 3", "586871223");
        previostempvaluefromname.put("Attachment 4", "586871224");
        previostempvaluefromname.put("Attachment 5", "586871225");

        Map<String, String> previostempvaluefromid = new HashMap<>();
        previostempvaluefromid.put("700001626", "586871221");
        previostempvaluefromid.put("700001627", "586871222");
        previostempvaluefromid.put("700001628", "586871223");
        previostempvaluefromid.put("700001629", "586871224");
        previostempvaluefromid.put("700001630", "586871225");


        Entry entry = new Entry();
        Entry entry1 = new Entry();

        entry1 = arServerUser.getEntry(formName, entryId, null);

        entry.setEntryId(entryId);
        Map<Integer, String> integerStringMap = RemedyBase.getFormFields2(arServerUser, formName);

        Map<Integer, File> atat = new HashMap<>();
        for (Map.Entry<String, File> attachment1 : attach.entrySet()) {
            for (Integer integer : integerStringMap.keySet()) {
                if (attachment1.getKey().equalsIgnoreCase(integerStringMap.get(integer))) {
                    atat.put(integer, attachment1.getValue());
                }
            }
        }

        for (Map.Entry<String, String> field : fields.entrySet()) {
            for (Integer integer : integerStringMap.keySet()) {
                if (field.getKey().equalsIgnoreCase(integerStringMap.get(integer).toString()) && field.getValue().equalsIgnoreCase("NO_VALUE")) {
                    if (entry1.get(integer).getValue() == null) {
                        entry.put(integer, new Value());
                    } else {
                        if (formName.equalsIgnoreCase("sr:servicerequest") || formName.equalsIgnoreCase("pt:problemticket")) {
                            AttachmentValue attachmentValue = (AttachmentValue) entry1.get(integer).getValue();
                            entry.put(integer, new Value());
                            entry.put(Integer.parseInt(previostempvaluefromid.get(integer.toString())), new Value(attachmentValue.getName()));  //temp field key with previous value while delete
                        } else {
                            entry.put(integer, new Value());
                        }
                    }
                    //    entry.put(integer, new Value());
                }
            }
        }
        if (!atat.isEmpty()) {
            List<AttachmentValue> attachmentValueNew = new ArrayList<>();
            for (Map.Entry<Integer, File> attachment : atat.entrySet()) {
                if (entry1.get(attachment.getKey()).getValue() == null) {
                    AttachmentValue attachmentValue = new AttachmentValue();
                    attachmentValue.setFilePath(attachment.getValue().getAbsolutePath());
                    attachmentValue.setName(attachment.getValue().getName());
                    attachmentValue.setOriginalSize(attachment.getValue().getTotalSpace());
                    attachmentValue.setValue(FileUtils.readFileToByteArray(attachment.getValue()));
                    entry.put(attachment.getKey(), new Value(attachmentValue));
                } else {
                    if (formName.equalsIgnoreCase("sr:servicerequest") || formName.equalsIgnoreCase("pt:problemticket")) {
                        AttachmentValue attachmentValue = (AttachmentValue) entry1.get(attachment.getKey()).getValue();

                        AttachmentValue attachmentValue1 = new AttachmentValue();
                        attachmentValue1.setFilePath(attachment.getValue().getAbsolutePath());
                        attachmentValue1.setName(attachment.getValue().getName());
                        attachmentValue1.setOriginalSize(attachment.getValue().getTotalSpace());
                        attachmentValue1.setValue(FileUtils.readFileToByteArray(attachment.getValue()));
                        entry.put(attachment.getKey(), new Value(attachmentValue1));
                        entry.put(Integer.parseInt(previostempvaluefromid.get(attachment.getKey().toString())), new Value(attachmentValue.getName()));  //temp field key with previous value
                    } else {
                        AttachmentValue attachmentValue = (AttachmentValue) entry1.get(attachment.getKey()).getValue();
                        attachmentValue.setFilePath(attachment.getValue().getAbsolutePath());
                        attachmentValue.setName(attachment.getValue().getName());
                        attachmentValue.setOriginalSize(attachment.getValue().getTotalSpace());
                        attachmentValue.setValue(FileUtils.readFileToByteArray(attachment.getValue()));
                        entry.put(attachment.getKey(), new Value(attachmentValue));
                    }
                }
            }
        }
        arServerUser.setEntry(formName, entryId, entry, null, 0);


        return entry;
    }

    public static Timestamp getcurrenttimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }


    public static List srtwithsrfields(ARServerUser arServerUser, String ticketid, HttpServletRequest request) {
        Map<String, String> srrecords = new HashMap<>();
        List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(arServerUser, "SR:ServiceRequest", new int[]{7, 700001053}, "'1'=\"" + ticketid + "\"", 0);
        return ITSMBase.remedyresultsetforitsm(arServerUser, "SR:ServiceRequest", entries, request, new int[]{7, 700001053});
    }

    public static String getloggedinuser(ARServerUser loggedinuser) {
        if (loggedinuser.getImpersonatedUser() != null && !loggedinuser.getImpersonatedUser().isEmpty()) {
            return loggedinuser.getImpersonatedUser();
        } else {
            return loggedinuser.getUser();
        }
    }

    public static Entry mapinsertrecords(HttpServletRequest ticketFields, Map<String, String> inputmap, ARServerUser loggedinuser, String formName, MultipartHttpServletRequest request) throws Exception {
        Entry entryData = new Entry();
        Map<String, File[]> attach = new TreeMap<>();
        if (ticketFields.getContentType() != null && ticketFields.getContentType().startsWith("multipart/form-data")) {

            MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();
            for (Map.Entry<String, List<MultipartFile>> stringListEntry : attachments.entrySet()) {
                File[] files = new File[stringListEntry.getValue().size()];
                int i = 0;
                for (MultipartFile multipartFile : stringListEntry.getValue()) {
                    files[i] = ITSMBase.convertMultiPartToFile(multipartFile);
                    i++;
                }
                attach.put(stringListEntry.getKey(), files);
            }
            entryData = RemedyBase.putAttachmentswithitsm(loggedinuser, formName, attach);
        }

        for (Map.Entry<String, String> entry : inputmap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString() == null || entry.getValue().toString().equalsIgnoreCase("null") || entry.getValue().toString().equalsIgnoreCase("NO_VALUE") || entry.getValue().toString().equalsIgnoreCase("NOVALUE") || entry.getValue().toString().isEmpty()) {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value());
            } else {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
            }
        }
        return entryData;
    }

    public static Entry mapinsertrecordsforcorrespondence(HttpServletRequest ticketFields, Map<String, String> inputmap, ARServerUser loggedinuser, String formName, MultipartHttpServletRequest request) throws Exception {
        Entry entryData = new Entry();
        Map<String, File[]> attach = new TreeMap<>();

        for (Map.Entry<String, String> entry : inputmap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString() == null || entry.getValue().toString().equalsIgnoreCase("null") || entry.getValue().toString().equalsIgnoreCase("NO_VALUE") || entry.getValue().toString().equalsIgnoreCase("NOVALUE") || entry.getValue().toString().isEmpty()) {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value());
            } else {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
            }
        }
        return entryData;
    }

    public static String gsonstojson(Object records) {
        return new Gson().toJson(records);
    }

    public static void insertattachmentforemailcorrespondence(HttpServletRequest ticketFields, MultipartHttpServletRequest request, ARServerUser loggedinuser, String sourceguid) throws Exception {
        String attachmentformName = "CMN:Attachments";
        if (ticketFields.getContentType() != null && ticketFields.getContentType().startsWith("multipart/form-data")) {
            Map<String, File> attach = new TreeMap<>();
            MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();
            for (Map.Entry<String, List<MultipartFile>> stringListEntry : attachments.entrySet()) {
                for (MultipartFile multipartFile : stringListEntry.getValue()) {
                    Entry emaildata = new Entry();
                    attach.put("Attachment Field", ITSMBase.convertMultiPartToFile(multipartFile));
                    emaildata = RemedyBase.putAttachmentwithitsm(loggedinuser, attachmentformName, attach, new HashMap<>());
                    emaildata.put(18134, new com.bmc.arsys.api.Value(sourceguid));
                    ITSMBase.createTicket(loggedinuser, attachmentformName, emaildata);
                }
            }
        }
    }

}


