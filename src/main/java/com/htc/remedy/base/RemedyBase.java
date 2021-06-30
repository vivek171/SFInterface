package com.htc.remedy.base;

import com.bmc.arsys.api.*;
import com.bmc.thirdparty.org.apache.commons.lang.ArrayUtils;
import com.bmc.thirdparty.org.apache.commons.lang.StringEscapeUtils;
import com.htc.remedy.model.*;
import com.htc.remedy.repo.UserRepo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by poovarasanv on 26/7/17.
 * <p>
 * Project : remedy-web-services
 */

@Component
@Service
public class RemedyBase {

    public static Environment environment;

    @Autowired
    public void init(Environment environment) {
        this.environment = environment;
    }


    public static String createTicket(ARServerUser arServerUser, String formName, Entry entry) throws Exception {
        return arServerUser.createEntry(formName, entry);
    }

    public static String customcreateTicket(ARServerUser arServerUser, String formName, Entry entry) throws Exception {
        try {
            return arServerUser.createEntry(formName, entry);
        } catch (Exception e) {
            throw new Exception(e + " " + entry.get(700001039).toString());
        }
    }

    public static List<FormModel> getAllForms(ARServerUser arServerUser) {

        List<FormModel> allFormsModel = new ArrayList<>();
        try {
            List<String> allForms = arServerUser.getListForm();

            allForms
                    .forEach(f -> allFormsModel.add(new FormModel(f, f)));

        } catch (ARException e) {
            return null;
        }
        return allFormsModel;

    }

    public static String formatEpocDate(String date) {
        String format = "MM/dd/yyyy hh:mm:ss aa";
        // timestamp to Date
        long epoch = Long.parseLong(date);
        Date expiry = new Date(epoch * 1000);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        return sdf.format(expiry);
    }

    public static Map<String, Object> getEntry(ARServerUser arServerUser, String formName, String entryId) {
        Map<String, Object> stringStringMap = new HashMap<>();
        try {
            Entry entry = arServerUser.getEntry(formName, entryId, null);
            Set<Integer> fieldIds = entry.keySet();
            Map<Integer, String> allFormFields = getFormFields2(arServerUser, formName);
            for (Integer fieldId : fieldIds) {
                Value val = entry.get(fieldId);
                stringStringMap.put(allFormFields.get(fieldId), val.getValue());
            }
        } catch (ARException e) {
            return null;
        }
        return stringStringMap;
    }

    public static int[] getAllFieldsInt(ARServerUser arServerUser, String formName) throws ARException {
        List<Field> fields = arServerUser.getListFieldObjects(formName);
        int[] f = new int[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            f[i] = fields.get(i).getFieldID();
        }

        return f;
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

                    value = RemedyBase.formatEpocDate(val.toString().substring(val.toString().indexOf("=") + 1, val.toString().indexOf("]")));
                    System.out.println(value);
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

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        //System.out.println(value);
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


    private static byte[] getEntryAttachmentField(Field field, Value val, String entryID, String formName, ARServerUser user) {
        byte[] attach = null;
        try {


            if (val.toString() != null) {
                if (field instanceof AttachmentField) {
                    AttachmentValue aVal = (AttachmentValue) val.getValue();
                    if (aVal != null) {
                        String aName;
                        String[] aDetails = aVal.getValueFileName().split("\\.(?=[^\\.]+$)");
                        if (aDetails.length == 2) {
                            aName = aDetails[0] + "." + aDetails[1];
                        } else {
                            aName = aDetails[0];
                        }
                        int lastPos = aName.lastIndexOf('\\');
                        String aNameShort = (lastPos < 0) ? aName : aName.substring(lastPos + 1);


                        FileOutputStream fos = new FileOutputStream(aNameShort);
                        attach = user.getEntryBlob(formName, entryID, field.getFieldID());

                        fos.write(attach);
                        fos.close();
                    }
                }
            }

        } catch (Exception e) {
            e.getMessage();
            attach = null;
        }
        return attach;
    }

    public static class fileAttachment {
        String attachmentname;
        String attachmenturi;
        long originalsize;
        long compressedsize;
        String filesize;

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public long getOriginalsize() {
            return originalsize;
        }

        public void setOriginalsize(long originalsize) {
            this.originalsize = originalsize;
        }

        public long getCompressedsize() {
            return compressedsize;
        }

        public void setCompressedsize(long compressedsize) {
            this.compressedsize = compressedsize;
        }

        public String getAttachmentname() {
            return attachmentname;
        }

        public void setAttachmentname(String attachmentname) {
            this.attachmentname = attachmentname;
        }

        public String getAttachmenturi() {
            return attachmenturi;
        }

        public void setAttachmenturi(String attachmenturi) {
            this.attachmenturi = attachmenturi;
        }
    }

    public static String filesizeinkb(long size) {
        size = Math.round(size / 1024) + (size % 1024 == 0 ? 0 : 1);
        return size + " KB";
    }


    public static fileAttachment createattatchmenturi(ARServerUser arServerUser, String formName, String entryId, Field field, Value val, HttpServletRequest request, Entry entry) throws Exception {
        fileAttachment fileAttachment = null;

        try {
            if (val != null)
                if (val.toString() != null) {
                    if (field instanceof AttachmentField) {
                        AttachmentValue aVal = (AttachmentValue) val.getValue();
                        if (aVal != null) {
                            byte[] bytes = arServerUser.getEntryBlob(formName, entryId, field.getFieldID());
                            fileAttachment = new fileAttachment();
                            fileAttachment.setAttachmenturi(getCurrentUrl(request) == null ? null : getCurrentUrl(request) + "/openapi/downloadattachment?formName=" + formName + "&ticketId=" + entryId + "&fieldId=" + field.getFieldID());
                            fileAttachment.setAttachmentname(aVal.getName());
                            fileAttachment.setOriginalsize(aVal.getOriginalSize());
                            //   fileAttachment.setFilesize(writeByte1(bytes, aVal.getName()));
                            fileAttachment.setFilesize(filesizeinkb(aVal.getOriginalSize()));
                            fileAttachment.setCompressedsize(aVal.getCompressedSize());
                        }
                    }
                }

        } catch (Exception e) {
            e.getMessage();
        }
        return fileAttachment;
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

                if (val != null && val.toString() != null && val.toString().startsWith("[Timestamp=")) {

                    value = RemedyBase.formatEpocDate(val.toString().substring(val.toString().indexOf("=") + 1, val.toString().indexOf("]")));
                    System.out.println(value);
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

            List<Entry> entryList = RemedyBase.queryEntrysByQual(arServerUser, clientcodename, clientcodefields, "");
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

            Map<Integer, String> subTaskFields = getFormFields2(arServerUser, "SR:Task");
            Map<Integer, String> ptcasefields = getFormFields2(arServerUser, "PT:Case History");
            Map<Integer, String> updateticketfields = getFormFields2(arServerUser, "CMN:TicketUpdate");
            Map<Integer, String> kbhistoryfields = getFormFields2(arServerUser, "CMN:KB_Case_History");
            Map<Integer, String> securepatientinfofields = getFormFields2(arServerUser, "CMN:PatientInfo");
            List<Field> securefields = arServerUser.getListFieldObjects("CMN:PatientInfo");
            List<Field> subtaskfields = arServerUser.getListFieldObjects(srtaskformName);

            List<Entry> subTask = queryEntrysByQual(
                    arServerUser,
                    "SR:Task",
                    srtrequiredfield,
                    "('Parent Request ID' = \"" + entryId + "\")", null, 704000115, Constants.AR_SORT_ASCENDING
            );   //704000115 task sequence
            List<Entry> ptcase = queryEntrysByQual(
                    arServerUser,
                    "PT:Case History",
                    ptreqfield,
                    "('Case ID' = \"" + entryId + "\")"
            );
           /* List<Entry> ticketupdateentries = queryEntrysByQual(
                    arServerUser,
                    "CMN:TicketUpdate",
                    updateticketfield,
                    "('Source ID' = \"" + entryId + "\")"
            ); */
            List<Entry> ticketupdateentries = queryEntrysByQual(
                    arServerUser,
                    "CMN:TicketUpdate",
                    updateticketfield,
                    "('Short Description' = \"" + entryId + "\")"
                    /* "('Source ID' = \"" + entryId + "\")"*/
            );
            List<Entry> kbcasehistoryentries = queryEntrysByQual(
                    arServerUser,
                    "CMN:KB_Case_History",
                    kbupdateticketfield,
                    "('Case ID' = \"" + entryId + "\")"
            );

            List<Entry> securepatientinfoentries = queryEntrysByQual(
                    arServerUser,
                    "CMN:PatientInfo",
                    secureticketfield,
                    "('Source Friendly ID' = \"" + entryId + "\")"
            );

            List<Entry> groupentries = queryEntrysByQualwithmaxrecords(arServerUser, groupformname, grouprequiredfield, "'Status'=\"Current\"");

            Map<String, String> groups = new HashMap<>();
            groupentries.forEach(entry1 -> {
                groups.put(entry1.get(106).toString(), entry1.get(105).toString());
            });

            List<Map<String, Object>> subTaskList = new ArrayList<>();
            List<Map<String, Object>> securepatientinfolist = new ArrayList<>();
            List<Map<String, String>> ptcasehistory = new ArrayList<>();
            List<Map<String, String>> ticketupdate = new ArrayList<>();
            List<Map<String, String>> kbcasehistory = new ArrayList<>();
           /* for (Entry entry1 : subTask) {
                HashMap<String, String> stringHashMap = new HashMap<>();
                for (Integer integer : entry1.keySet()) {
                    String value = "";
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        //System.out.println(value);
                    }else if(entry1.getKey()==){

                    }
                    else {
                        value = entry1.get(integer).toString();
                    }
                    stringHashMap.put(subTaskFields.get(integer), value);
                }

                subTaskList.add(stringHashMap);
            }*/


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
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        stringHashMap.put(subTaskFields.get(integer), value);
                        //System.out.println(value);

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

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        //System.out.println(value);
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

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        //System.out.println(value);
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

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        //System.out.println(value);
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
                    String value = "";
                    Field field = null;
                    for (Field field1 : securefields) {
                        if (integer == field1.getFieldID()) {
                            field = field1;
                        }
                    }
                    if (entry1.get(integer) != null && entry1.get(integer).toString() != null && entry1.get(integer).toString().startsWith("[Timestamp=")) {

                        value = RemedyBase.formatEpocDate(entry1.get(integer).toString().substring(entry1.get(integer).toString().indexOf("=") + 1, entry1.get(integer).toString().indexOf("]")));
                        stringHashMap.put(securepatientinfofields.get(integer), value);
                        //System.out.println(value);

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
                securepatientinfolist.add(stringHashMap);
            }

          /*  Collections.sort(subTaskList, new Comparator<Map<String, Object>>() {
                public int compare(final Map<String, String> o1, final Map<String, String> o2) {
                    if (o1.get("Task Sequence") instanceof String && o2.get("Task Sequence") instanceof String)
                        return String.class.cast(o1.get("Task Sequence")).compareTo(String.class.cast(o2.get("Task Sequence")));
                    else
                        return 0;
                }
            });*/

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
            stringStringMap.put("clientcodename", clientcodemapping.get(stringStringMap.get("Client")) != null ? clientcodemapping.get(stringStringMap.get("Client")) : "");


        } catch (Exception e) {
            return null;
        }
        return stringStringMap;
    }

    public static Set<TempGroupInfo1> getusergroupsfromremedy(ARServerUser arserveruser, String loginid, String userdefaultgroup) throws ARException {
        Set<TempGroupInfo1> tempGroupInfos = new HashSet<>();
        List<GroupInfo> groupInfo = new ArrayList<>();
        try {
            groupInfo = arserveruser.getListGroup(loginid, null);
        } catch (Exception e) {
            groupInfo = new ArrayList<GroupInfo>();
        }
        groupInfo.forEach(groupinfo -> {
            TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(
                    String.valueOf(groupinfo.getName()),
                    String.valueOf(groupinfo.getId()),
                    String.valueOf(groupinfo.getGroupType()),
                    groupinfo.getGroupNames(),
                    String.valueOf(groupinfo.getCategory()),
                    String.valueOf(groupinfo.getGroupParent()),
                    String.valueOf(groupinfo.getGroupOverLay()),
                    "Remedy"
            );
            tempGroupInfos.add(tempGroupInfo);
        });

        // if (!userdefaultgroup.isEmpty() && tempGroupInfos.isEmpty()) {
        if (!userdefaultgroup.isEmpty()) {

            String sQuery = "SELECT Group_ID,\n" +
                    "		Group_Type,\n" +
                    "       Group_Name,\n" +
                    "       Group_Category \n" +
                    "       FROM GROUP_X\n" +
                    "       WITH (NOLOCK) WHERE Group_ID ='" + userdefaultgroup + "'\n";

            SQLResult sqlResult = arserveruser.getListSQL(sQuery.toString(), 0, true);
            for (List<Value> content : sqlResult.getContents()) {
                //  Map<String, String> appEntry = new HashMap<>();
                //  appEntry.put("Request ID", content.get(0).toString());
                List<String> groupnames = new ArrayList<String>();
                groupnames.add(content.get(2).toString() == null ? "0" : content.get(2).toString());
                TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(content.get(2).toString(), content.get(0).toString() == null ? "0" : content.get(0).toString(),
                        content.get(1).toString() == null ? "0" : content.get(1).toString(),
                        groupnames,
                        content.get(3).toString() == null ? "0" : content.get(3).toString(),
                        "0",
                        "0", "Remedy");
                tempGroupInfos.add(tempGroupInfo);

            }
        }

        return tempGroupInfos;
    }

    /* public static Set<TempGroupInfo1> getusergroupsfromremedy(ARServerUser arserveruser, String loginid, String userdefaultgroup) throws ARException {
         Set<TempGroupInfo1> tempGroupInfos = new HashSet<>();
         List<GroupInfo> groupInfo = arserveruser.getListGroup(loginid, null);

         groupInfo.forEach(groupinfo -> {
             TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(
                     String.valueOf(groupinfo.getName()),
                     String.valueOf(groupinfo.getId()),
                     String.valueOf(groupinfo.getGroupType()),
                     groupinfo.getGroupNames(),
                     String.valueOf(groupinfo.getCategory()),
                     String.valueOf(groupinfo.getGroupParent()),
                     String.valueOf(groupinfo.getGroupOverLay()),
                     "Remedy"
             );
             tempGroupInfos.add(tempGroupInfo);
         });

         if (!userdefaultgroup.isEmpty() && tempGroupInfos.isEmpty()) {

             String sQuery = "SELECT Group_ID,\n" +
                     "		Group_Type,\n" +
                     "       Group_Name,\n" +
                     "       Group_Category \n" +
                     "       FROM GROUP_X\n" +
                     "       WITH (NOLOCK) WHERE Group_ID ='" + userdefaultgroup + "'\n";

             SQLResult sqlResult = arserveruser.getListSQL(sQuery.toString(), 0, true);
             for (List<Value> content : sqlResult.getContents()) {
                 Map<String, String> appEntry = new HashMap<>();
                 appEntry.put("Request ID", content.get(0).toString());
                 List<String> groupnames = new ArrayList<String>();
                 groupnames.add(content.get(2).toString() == null ? "0" : content.get(2).toString());
                 TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(content.get(2).toString(), content.get(0).toString() == null ? "0" : content.get(0).toString(),
                         content.get(1).toString() == null ? "0" : content.get(1).toString(),
                         groupnames,
                         content.get(3).toString() == null ? "0" : content.get(3).toString(),
                         "0",
                         "0", "Remedy");
                 tempGroupInfos.add(tempGroupInfo);

             }
         }

         return tempGroupInfos;
     }
 */
    public static String selectionfieldvalue(Field field, String value) {
        String selectionfieldvalue = "";
        SelectionFieldLimit sFieldLimit = (SelectionFieldLimit) field.getFieldLimit();
        if (sFieldLimit != null) {
            List<EnumItem> eItemList = sFieldLimit.getValues();
            for (EnumItem eItem : eItemList) {
                if (eItem.getEnumItemNumber() == Integer.parseInt(value)) {
                    return eItem.getEnumItemName();
                }
            }
        }
        return selectionfieldvalue;
    }

    public static String dateTimefieldvalue(Field field, Value val) {
        String selectionfieldvalue = "";
        Timestamp callDateTimeTS = (Timestamp) val.getValue();
        if (callDateTimeTS != null) {
            return RemedyBase.formatEpocDate(callDateTimeTS.toString().substring(callDateTimeTS.toString().indexOf("=") + 1, callDateTimeTS.toString().indexOf("]"))).toString();
        }
        return selectionfieldvalue;
    }

    public static String dateTime(Timestamp callDateTimeTS) {
        String selectionfieldvalue = "";
        if (callDateTimeTS != null) {
            return RemedyBase.formatEpocDate(callDateTimeTS.toString().substring(callDateTimeTS.toString().indexOf("=") + 1, callDateTimeTS.toString().indexOf("]"))).toString();
        }
        return selectionfieldvalue;
    }

    public static List<String> diaryfieldValue(Field field, Value val) {
        List<String> diaryfieldvalue = new ArrayList<>();
        for (DiaryItem dlv : (DiaryListValue) val.getValue()) {
            diaryfieldvalue.add(dateTime(dlv.getTimestamp()) + " -- " + dlv.getUser() + " \n" + dlv.getText());
        }
        return diaryfieldvalue;
    }


    static class WorkLog {
        String time;
        String user;
        String text;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static List<WorkLog> parseWorkLog(String workLog) {
        List<WorkLog> workLogs = new ArrayList<>();

        if (workLog != null && !workLog.trim().isEmpty()) {
            String[] stxt = workLog.split("], \\[");

            for (String s : stxt) {
                String user = s.substring(s.indexOf("User="), s.indexOf(",Time Val")).replace("User=", "");
                String time = s.substring(s.indexOf("Val="), s.indexOf(",Text=")).replace("Val=", "");
                String text = s.substring(s.indexOf("Text="), s.indexOf("],Appended") < 0 ? s.length() : s.indexOf("],Appended")).replace("Text=", "");

                WorkLog workLog1 = new WorkLog();
                workLog1.setText(text);
                workLog1.setTime(formatEpocDate(time.substring(time.indexOf("=") + 1, time.indexOf("]"))));
                workLog1.setUser(user);
                workLogs.add(workLog1);
            }
        }
        return workLogs;
    }


    public static List<String> parseWorkLog1(String workLog) {
        List<WorkLog> workLogs = new ArrayList<>();
        List<String> worklogs1 = new ArrayList<>();

        if (workLog != null && !workLog.trim().isEmpty()) {
            String[] stxt = workLog.split("], \\[");

            for (String s : stxt) {
                String user = s.substring(s.indexOf("User="), s.indexOf(",Time Val")).replace("User=", "");
                String time = s.substring(s.indexOf("Val="), s.indexOf(",Text=")).replace("Val=", "");
                String text = s.substring(s.indexOf("Text="), s.indexOf("],Appended") < 0 ? s.length() : s.indexOf("],Appended")).replace("Text=", "");

                WorkLog workLog1 = new WorkLog();
                workLog1.setText(text);
                workLog1.setTime(formatEpocDate(time.substring(time.indexOf("=") + 1, time.indexOf("]"))));
                workLog1.setUser(user);
                workLogs.add(workLog1);
                worklogs1.add(formatEpocDate(time.substring(time.indexOf("=") + 1, time.indexOf("]"))) + " -- " + user + "\n" + text);
            }
        }
        return worklogs1;
    }


    public static Map<String, String> excelread(File file) {
        Map<String, String> controlidmap = new HashMap<>();

        Workbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = Workbook.getWorkbook(file);

            sheet = workbook.getSheet(0);


            int noc = sheet.getColumns(); //  Number of Rows
            int nor = sheet.getRows();    // Number Of

            for (int i = 1; i < nor; i++) {
                Cell[] currentRow = sheet.getRow(i);
                HashMap currentRowJson = new HashMap<String, String>();
                String cid = "";
                if (currentRow.length > 3) {
                    cid = currentRow[3].getContents();
                }
                controlidmap.put(currentRow[2].getContents(), cid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return controlidmap;
    }


    public static Map<String, String> updateTicket(ARServerUser arServerUser, Map<String, String> updateParam, String formName, String ticketId) {

        try {

            Entry entry = new Entry();
            entry.setEntryId(ticketId);
            Map<String, Object> formFields = getFormFields(arServerUser, formName);
            for (String s : updateParam.keySet()) {
                if (formFields.containsKey(s.toLowerCase())) {
                    entry.put((Integer) formFields.get(s.toLowerCase()), new Value(updateParam.get(s)));
                }
            }

            for (int i : coreFields()) {
                entry.remove(i);
            }


            arServerUser.setEntry(
                    formName,
                    ticketId,
                    entry,
                    null,
                    0
            );

            return updateParam;
        } catch (ARException e) {
            updateParam.clear();
            updateParam.put("error", e.getMessage());
        }


        return updateParam;

    }


    public static Map<String, String> customupdateTicket(ARServerUser arServerUser, Map<String, String> updateParam, String formName, String ticketId) {

        try {

            Entry entry = new Entry();
            entry.setEntryId(ticketId);
            Map<String, Object> formFields = getFormFields(arServerUser, formName);
            for (String s : updateParam.keySet()) {
                if (formFields.containsKey(s.toLowerCase())) {

                    if (updateParam.get(s).equalsIgnoreCase("NOVALUE")) {
                        entry.put((Integer) formFields.get(s.toLowerCase()), new Value());
                    } else {
                        entry.put((Integer) formFields.get(s.toLowerCase()), new Value(updateParam.get(s)));
                    }

                }
            }

            for (int i : coreFields()) {
                entry.remove(i);
            }


            arServerUser.setEntry(
                    formName,
                    ticketId,
                    entry,
                    null,
                    0
            );

            return updateParam;
        } catch (ARException e) {
            updateParam.clear();
            updateParam.put("error", e.getMessage());
        }
        return updateParam;
    }

    public static Map<String, Object> updateTicketbyidwithnovalue(ARServerUser arServerUser, Map<String, Object> updateParam, String formName, String ticketId) throws ARException {
        Entry entry = new Entry();
        entry.setEntryId(ticketId);
        Map<String, Object> formFields = getFormFields(arServerUser, formName);
        for (String s : updateParam.keySet()) {

            if (formFields.containsValue(Integer.parseInt(s))) {
                if (updateParam.get(s).toString().equalsIgnoreCase("NO_VALUE")) {
                    entry.put(Integer.parseInt(s), new Value());
                } else {
                    entry.put(Integer.parseInt(s), new Value(updateParam.get(s).toString()));
                }
            }
        }

        for (int i : coreFields()) {
            entry.remove(i);
        }

        arServerUser.setEntry(
                formName,
                ticketId,
                entry,
                null,
                0
        );

        return updateParam;

    }

    public static Map<String, Object> updateTicketbyid(ARServerUser arServerUser, Map<String, Object> updateParam, String formName, String ticketId) {

        try {
         /*   Entry entry = arServerUser.getEntry(
                    formName,
                    ticketId,
                    null
            );*/
            Entry entry = new Entry();
            entry.setEntryId(ticketId);
            Map<String, Object> formFields = getFormFields(arServerUser, formName);
            for (String s : updateParam.keySet()) {
                if (formFields.containsValue(Integer.parseInt(s))) {
                    entry.put(Integer.parseInt(s), new Value(updateParam.get(s).toString()));
                }
            }

            for (int i : coreFields()) {
                entry.remove(i);
            }

//            if(entry.containsKey(7)) {
//                entry.put(Constants.AR_CORE_STATUS,)
//            }
            //entry.remo

            arServerUser.setEntry(
                    formName,
                    ticketId,
                    entry,
                    null,
                    0
            );

            return updateParam;
        } catch (ARException e) {
            updateParam.clear();
            updateParam.put("error", e.getMessage());
        }


        return updateParam;

    }

    public static Map<String, Object> getSingleEntryWithFields(ARServerUser arServerUser, String formName, String entryId, int[] requiredFields) {
        Map<String, Object> stringStringMap = new HashMap<>();
        try {
            Entry entry = arServerUser.getEntry(formName, entryId, requiredFields);
            Set<Integer> fieldIds = entry.keySet();
            Map<Integer, String> allFormFields = getFormFields2(arServerUser, formName);
            for (Integer fieldId : fieldIds) {
                if (entry.containsKey(fieldId)) {
                    Value val = entry.get(fieldId);
                    stringStringMap.put(allFormFields.get(fieldId), val.getValue());
                }
            }
        } catch (ARException e) {
            return null;
        }
        return stringStringMap;
    }

    public static ARServerUser loginUser(
            String serverName,
            int port,
            String username,
            String password) throws ARException {
        ARServerUser arServerUser = new ARServerUser();
        arServerUser.setServer(serverName);
        arServerUser.setPort(port);
        arServerUser.setUser(username);
        arServerUser.setPassword(password);


        arServerUser.login();
        return arServerUser;
    }


    public static void logout(
            ARServerUser arServerUser) throws ARException {
        arServerUser.logout();
    }

    public static boolean loginUser1(
            String serverName,
            int port,
            String username,
            String password) {
        ARServerUser arServerUser = new ARServerUser();
        arServerUser.setServer(serverName);
        arServerUser.setPort(port);
        arServerUser.setUser(username);
        arServerUser.setPassword(password);


        try {
            arServerUser.login();
        } catch (ARException e) {
            return false;
        }
        return true;
    }


    public static Map<String, Object> getFormFields(ARServerUser arServerUser, String formName) {
        Map<String, Object> fieldResult = new HashMap<>();
        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                fieldResult.put(field.getName().toLowerCase(), field.getFieldID());
            }

        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldResult;

    }

    public static Map<Integer, Field> getFormFieldsObject(ARServerUser arServerUser, String formName) {
        Map<Integer, Field> fieldResult = new HashMap<>();
        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                fieldResult.put(field.getFieldID(), field);
            }

        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldResult;
    }


    public static Map<Integer, String> getFormFields2(ARServerUser arServerUser, String formName) {
        Map<Integer, String> fieldResult2 = new HashMap<>();
        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                fieldResult2.put(field.getFieldID(), field.getName());
            }

        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldResult2;
    }

    public static List<BaseModel> getFormField(ARServerUser arServerUser, String formName) throws ARException {
        List<BaseModel> fieldsModel = new ArrayList<>();

        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                fieldsModel.add(new FieldModel(Integer.toString(field.getFieldID()), field.getName()));


            }
        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldsModel;
    }

    public static List<FieldModel> getFormField5(ARServerUser arServerUser, String formName) throws ARException {
        List<FieldModel> fieldsModel = new ArrayList<>();

        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                if (!field.getName().isEmpty())
                    fieldsModel.add(new FieldModel(Integer.toString(field.getFieldID()), field.getName()));


            }
        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldsModel;

    }


    public static List<Map<String, Object>> getFormFields1(ARServerUser arServerUser, String formName) {
        List<Map<String, Object>> fieldResultFull = new ArrayList<>();
        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                Map<String, Object> fieldResult = new HashMap<>();
                fieldResult.put("FieldID", field.getFieldID());
                fieldResult.put("DefaultValue", field.getDefaultValue().toString());
                Map<String, Object> assignedGroups = new HashMap<>();
                for (PermissionInfo assignedGroup : field.getAssignedGroup()) {

                    assignedGroups.put("GroupID", assignedGroup.getGroupID());
                    assignedGroups.put("Permission", assignedGroup.getPermissionValue());
                }
                fieldResult.put("AssignedGroups", assignedGroups);
                fieldResult.put("FieldName", field.getName());
                fieldResult.put("FieldOption", field.getFieldOption());

                fieldResultFull.add(fieldResult);
            }

        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldResultFull;

    }

    /*query records from remedy */
    public static List<Entry> queryEntrysByQual(ARServerUser server,
                                                String formName,
                                                int[] requiredFields,
                                                String qualStr) {
        List<Entry> entryList = null;
        try {

            // Retrieve the detail info of all fields from the form.
            List<Field> fields = server.getListFieldObjects(formName);
            // Create the search qualifier.
            QualifierInfo qual = server.parseQualification(qualStr, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);

            OutputInteger nMatches = new OutputInteger();
            //  List<SortInfo> sortOrder = new ArrayList<SortInfo>();
            //  sortOrder.add(new SortInfo(2, Constants.AR_SORT_DESCENDING));
            // Retrieve entries from the form using the given
            // qualification.

            entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    null, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);


        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList;
    }


    public static List<Entry> queryEntrysByQualWithNoOfRecords(ARServerUser server,
                                                               String formName,
                                                               int[] requiredFields,
                                                               String qualStr, Integer noOfRecords) {
        List<Entry> entryList = null;
        try {

            // Retrieve the detail info of all fields from the form.
            List<Field> fields = server.getListFieldObjects(formName);
            // Create the search qualifier.
            QualifierInfo qual = server.parseQualification(qualStr, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);

            OutputInteger nMatches = new OutputInteger();
            //  List<SortInfo> sortOrder = new ArrayList<SortInfo>();
            //  sortOrder.add(new SortInfo(2, Constants.AR_SORT_DESCENDING));
            // Retrieve entries from the form using the given
            // qualification.

            if (requiredFields == null) {
                requiredFields = new int[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    requiredFields[i] = fields.get(i).getFieldID();
                }
            }

            entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    noOfRecords,
                    null, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);


        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList;
    }

    public static List<Entry> queryEntrysByQualWithNoOfRecordswithsort(ARServerUser server,
                                                                       String formName,
                                                                       int[] requiredFields,
                                                                       String qualStr, Integer noOfRecords, int req) {
        List<Entry> entryList = null;
        try {

            // Retrieve the detail info of all fields from the form.
            List<Field> fields = server.getListFieldObjects(formName);
            // Create the search qualifier.
            QualifierInfo qual = server.parseQualification(qualStr, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);

            OutputInteger nMatches = new OutputInteger();
            //  List<SortInfo> sortOrder = new ArrayList<SortInfo>();
            //  sortOrder.add(new SortInfo(2, Constants.AR_SORT_DESCENDING));
            // Retrieve entries from the form using the given
            // qualification.

            if (requiredFields == null) {
                requiredFields = new int[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    requiredFields[i] = fields.get(i).getFieldID();
                }
            }

            /*entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    noOfRecords,
                    null, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);*/

            List<SortInfo> infos = new ArrayList<>();
            SortInfo sortInfo = new SortInfo(req, Constants.AR_SORT_DESCENDING);
            infos.add(sortInfo);
            entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    infos, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);
        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList;
    }

    public static List<Entry> queryEntrysByQual(ARServerUser server,
                                                String formName,
                                                int[] requiredFields,
                                                String qualStr, Integer noOfRecords, int fieldid, int sortorder) {
        List<Entry> entryList = null;
        try {

            // Retrieve the detail info of all fields from the form.
            List<Field> fields = server.getListFieldObjects(formName);
            // Create the search qualifier.
            QualifierInfo qual = server.parseQualification(qualStr, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);

            OutputInteger nMatches = new OutputInteger();
            //  List<SortInfo> sortOrder = new ArrayList<SortInfo>();
            //  sortOrder.add(new SortInfo(2, Constants.AR_SORT_DESCENDING));
            // Retrieve entries from the form using the given
            // qualification.

            if (requiredFields == null) {
                requiredFields = new int[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    requiredFields[i] = fields.get(i).getFieldID();
                }
            }

            /*entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    noOfRecords,
                    null, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);*/

            List<SortInfo> infos = new ArrayList<>();
            SortInfo sortInfo = new SortInfo(fieldid, sortorder);
            infos.add(sortInfo);
            entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    infos, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);

        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList;
    }

    public static Boolean findadminornot(String usergroups) {
        String[] usergroup = usergroups.split(",");
        return (Arrays.asList(usergroup).contains("'Administrator'"));
    }

    public static String useraccessibleclient(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");
        SQLResult sqlResult;
        String adminquery = "SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';') WHERE  value IN (" + com.htc.remedy.constants.Constants.getAdmingroupsid() + ")";

        sqlResult = user.getListSQL(adminquery, 0, true);
        if (sqlResult.getContents().size() > 0) {

        } else {

            String query = " SELECT  DISTINCT Client\n" +
                    " FROM CMN_CLIENTINFO WITH (NOLOCK) \n" +
                    " WHERE Status in (0,1,2,3) AND MASTER_CLIENT IN (\n" +
                    "SELECT  MASTER_CLIENT\n" +
                    "FROM Group_GRP_OPT_CMN_MSTR_Join WITH (NOLOCK)\n" +
                    "WHERE  Status__C=0 AND Group_ID IN (\n" +
                    "SELECT value FROM STRING_SPLIT((\n" +
                    "SELECT Group_List\n" +
                    "FROM User_x WITH (NOLOCK)\n" +
                    "WHERE Login_Name='" + loginid + "'),';'\n" +
                    ")\n" +
                    ")\n" +
                    "\tAND MASTER_CLIENT IS NOT NULL )";

            sqlResult = user.getListSQL(query, 0, true);

            if (sqlResult.getContents().size() > 0) {
                for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                    builder.append("\'" + content.get(0).toString() + "\',");
                }
                builder.deleteCharAt(builder.toString().length() - 1);
            }
        }
        return builder.toString();
    }


    public static String useraccessiblemasterclientcode(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");
        SQLResult sqlResult;
        String adminquery = "SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';') WHERE  value in (" + com.htc.remedy.constants.Constants.getAdmingroupsid() + ")";

        sqlResult = user.getListSQL(adminquery, 0, true);
        if (sqlResult.getContents().size() > 0) {
            return "select distinct master_client_code_name  From Group_GRP_OPT_CMN_MSTR_Join where Functional_Type IN (1) AND Status__C=0 AND master_client_code_name is not null order by 1";
        } else {

            return "SELECT  DISTINCT  Master_Client_Code_Name\n" +
                    "\tFROM Group_GRP_OPT_CMN_MSTR_Join\n" +
                    "\tWHERE  Master_Client_Code_Name IN (\n" +
                    "\t\tSELECT DISTINCT Master_Client_Code_Name \n" +
                    "\t\t\tFROM Group_GRP_OPT_CMN_MSTR_Join \n" +
                    "\t\t\tWHERE Status__c=0 \n" +
                    "\t\t\tAND Functional_Type=1\n" +
                    "\t\t)\n" +
                    "\t\tAND Group_ID IN (\n" +
                    "\t\t\tSELECT value \n" +
                    "\t\t\t\tFROM STRING_SPLIT(\n" +
                    "\t\t\t\t\t(SELECT Group_List \n" +
                    "\t\t\t\t\t\tFROM User_x \n" +
                    "\t\t\t\t\t\tWHERE Login_Name='" + loginid + "'),';'\n" +
                    "\t\t\t\t\t))\n" +
                    "\t\tAND Master_Client_Code_Name is not null \n" +
                    "\tORDER by Master_Client_Code_Name desc";
        }
    }


    public static Map<String, String> returnSuccess() {
        Map m = new HashMap<String, String>();
        m.put("success", "ok");
        return m;
    }

    public static Map<String, String> returnError(String msg) {
        Map m = new HashMap<String, String>();
        m.put("error", msg);
        return m;
    }

    public static Map<String, String> returnSuccess(String msg) {
        Map m = new HashMap<String, String>();
        m.put("success", msg);
        return m;
    }

    public static Map<Object, Object> returnErrorobjectobject(String msg) {
        Map m = new HashMap<Object, Object>();
        m.put("error", msg);
        return m;
    }

    public static Map<String, Object> returnErrorobject(String msg) {
        Map m = new HashMap<String, String>();
        m.put("error", msg);
        return m;
    }

    public static Map<Integer, String> returnError1(String msg) {
        Map m = new HashMap<Integer, String>();
        m.put(Integer.parseInt("error"), msg);
        return m;
    }

    public static int[] coreFields() {
        return new int[]{
                1, 3, 5, 6, 15
        };
    }

    public static ARServerUser getUserWithAuth(String serverName, Integer port, UserRepo userRepo) throws ARException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ARServerUser arServerUser = null;
        if (auth != null) {

            UserModel userModel = userRepo.findByUserName(auth.getName());
            if (userModel != null) {
//                arServerUser = RemedyBase.loginUser(
//                        serverName,
//                        port,
//                        auth.getName(),
//                        userModel.getPassword()
//                );
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        } else {
            throw new InvalidTokenException("Invalid token");
        }

        return arServerUser;
    }


    public static ARServerUser getUserWithAuth1(UserRepo userRepo, ARServerUser arServerUser) throws ARException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {

            UserModel userModel = userRepo.findByUserName(auth.getName());
            if (userModel != null) {
              /*arServerUser=  RemedyBase.loginUser(
                        serverName,
                        port,
                        username,
                        password
                );
*/
            } else {
                throw new UsernameNotFoundException("User not Found");
            }
        } else {
            throw new InvalidTokenException("Invalid token");
        }

        return arServerUser;
    }

    public static String decrypt(String encString) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String key = environment.getProperty("ctsspi.encryptkey");
        return decrypt(key, encString);
    }

    public static String encrypt(String encString) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String key = environment.getProperty("ctsspi.encryptkey");
        return encrypt(key, encString);
    }

    public static String decrypt(String key1, String encData) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String key = key1;
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher
                .doFinal(Base64.getDecoder().decode(encData.getBytes()));
        return new String(original).trim();
    }

    public static String encrypt(String key1, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String key = key1;
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] original = Base64.getEncoder().encode(cipher.doFinal(data.getBytes()));
        return new String(original).trim();
    }


    public static String getUserWithAuthname(String serverName, Integer port, UserRepo userRepo) throws ARException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    }

    public static List<CatalogModel> getAllCatalog(ARServerUser arServerUser) {

        int[] requiredFields = {
                1, // servey Id
                600000500,//category
                702020006}; //description

        List<Entry> clients = RemedyBase.queryEntrysByQual(
                arServerUser,
                "KS_RQT_ServiceCatalog_base",
                requiredFields,
                null
        );

        List<CatalogModel> clientModels = new ArrayList<>();

        for (Entry e : clients) {
            System.out.println(e);
            CatalogModel clientModel = new CatalogModel(
                    e.get(requiredFields[0]).toString(),
                    e.get(requiredFields[1]).toString(),
                    e.get(requiredFields[2]).toString()
            );

            clientModels.add(clientModel);
        }

        return clientModels;
    }

    public static List<BaseModel> getCatalogForms(ARServerUser arServerUser, String catalog) {
        int[] requiredFields = {
                1,      //survey ID
                600000500,  //catalog
                600000600,  //type
                700001000,  //survey template name
                700001010};   //survey description

        System.out.println(catalog);
        List<Entry> clientForms = RemedyBase.queryEntrysByQual(
                arServerUser,
                "KS_SRV_SurveyTemplate",
                requiredFields,
                "('Category' = \"" + catalog + "\")"
        );


        List<BaseModel> clientModels = new ArrayList<>();

        for (Entry e : clientForms) {
            System.out.println(e);
            CatalogFormModel clientModel = new CatalogFormModel(
                    e.get(requiredFields[0]).toString(),
                    e.get(requiredFields[1]).toString(),
                    e.get(requiredFields[2]).toString(),
                    e.get(requiredFields[3]).toString(),
                    e.get(requiredFields[4]).toString());

            clientModels.add(clientModel);
        }

        return clientModels;

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    public static Map<String, String> getticketstatus(
            ARServerUser arServerUser,
            String formname,
            String ticketfieldid,
            String statusfieldid,
            List<String> tickets

    ) throws ARException {

        Map<String, String> results = new HashMap<>();
        StringBuilder peopleQuery = new StringBuilder("(");
        Map<Integer, String> formfields = getFormFields2(arServerUser, formname);
        String ticketfieldname = formfields.get(Integer.parseInt(ticketfieldid));
        String statusfieldname = formfields.get(Integer.parseInt(statusfieldid));
        int i = 0;
        int[] reqfield = new int[]{1, 7};

        for (String eachticket : tickets) {
            if (i == 0) {
                peopleQuery.append("('" + ticketfieldname + "' = \"" + eachticket + "\")");
            } else {
                peopleQuery.append(" OR ('" + ticketfieldname + "' = \"" + eachticket + "\")");
            }
            ++i;
        }
        peopleQuery.append(")");

        List<Entry> entries = queryEntrysByQual(arServerUser, formname, reqfield, peopleQuery.toString());

        entries.parallelStream().forEach(entry -> {
            results.put(entry.get(reqfield[0]).toString(), entry.get(reqfield[1]).toString());
        });
        return results;
    }


    public static List<Map<Object, Object>> userSearchsqlquerywithgrouplist(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) throws ARException {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        List<Map<Object, Object>> results1 = new ArrayList<>();
        results = RemedyBase.userSearchsqlquery(
                arServerUser,
                client,
                creteria,
                mailid1,
                networklogin1,
                fullname1,
                partialname1,
                firstname1,
                lastname1,
                loginid1,
                empnumber1
        );
        results.forEach(stringMap -> {
            Map<Object, Object> mapresult = new HashMap<>();
            List<GroupInfo> groupInfo = new ArrayList<>();
            try {
                groupInfo = arServerUser.getListGroup(stringMap.get("login_id").toString(), null);
            } catch (Exception e) {
                groupInfo = new ArrayList<GroupInfo>();
            }
            mapresult.putAll(stringMap);
            mapresult.put("usergroups", groupInfo);

            results1.add(mapresult);
        });
        return results1;
    }

    public static Map<String, String> customupdateTicketbyid(ARServerUser arServerUser, Map<String, String> updateParam, String formName, String ticketId) {

        try {

            Entry entry = new Entry();
            entry.setEntryId(ticketId);
            Map<String, Object> formFields = getFormFields(arServerUser, formName);
            for (String s : updateParam.keySet()) {
                if (formFields.containsValue(Integer.parseInt(s))) {

                    if (updateParam.get(s).equalsIgnoreCase("NOVALUE")) {
                        entry.put(Integer.parseInt(s), new Value());
                    } else {
                        entry.put(Integer.parseInt(s), new Value(updateParam.get(s)));
                    }

                }
            }

            for (int i : coreFields()) {
                entry.remove(i);
            }


            arServerUser.setEntry(
                    formName,
                    ticketId,
                    entry,
                    null,
                    0
            );
            updateParam.put("ticketId", ticketId);

            return updateParam;
        } catch (ARException e) {
            updateParam.clear();
            updateParam.put("error", e.getMessage());
        }
        return updateParam;
    }


    public static List<Map<String, ? extends Object>> userSearchsqlquery(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) throws ARException {

        List<Map<String, ? extends Object>> results = new ArrayList<>();


        creteria = StringEscapeUtils.escapeSql(creteria);
        String c1 = creteria;
        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();


        String sQuery = "SELECT Full_Name,\n" +
                "       Department,\n" +
                "       Building,\n" +
                "       Email_Address,\n" +
                "       Employee_Number,\n" +
                "       Floor,\n" +
                "       Suite,\n" +
                "       Office,\n" +
                "       Business_Organization,\n" +
                "       Phone_Work,\n" +
                "       First_Name,\n" +
                "       Last_Name,\n" +
                "       Login_ID,\n" +
                "       Phone_Ext,\n" +
                "       GUID,\n" +
                "       Designation,\n" +
                "       Fax,\n" +
                "       VIP,\n" +
                "       Title,\n" +
                "       Network_Login,\n" +
                "       Queue,\n" +
                "       Designation,\n" +
                "       Client,\n" +
                "       Middle_Init,\n" +
                "       Cost_Code,\n" +
                "       Role_x,\n" +
                "       Role_Prefix,\n" +
                "       Support_Person_,\n" +
                "       Pager_Numeric,\n" +
                "       Pager_Pin,\n" +
                "       Pager_Alpha,\n" +
                "       Company_Code,\n" +
                "       SSPPrefContactMethod\n" +
                "FROM CMN_People_Information\n" +
                " WITH (NOLOCK) WHERE Client IN (SELECT Client FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "')\n" +
                "AND ((First_Name is NOT NULL AND Last_Name is NOT NULL  AND Full_Name is not NULL AND Login_ID is NOT NULL) \n";

        StringBuilder peopleQuery = new StringBuilder(sQuery);

        String clientquery = "SELECT Client,Client_Name FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "' ";

        SQLResult clientsqlResult = arServerUser.getListSQL(clientquery.toString(), 0, true);
        for (List<Value> content : clientsqlResult.getContents()) {

            buisnessorganization.put(content.get(0).toString(), content.get(1).toString());
        }

        peopleQuery.append(" AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch2("Email_Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Employee_Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("First_Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch2("Last_Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Login_ID", splittedCreteria, "login"));
        }


        peopleQuery.append("(Full_Name = '" + creteria + "')");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");
        peopleQuery.append(") ");
        String status = "2";
        peopleQuery.append("AND Status = '" + status + "'");


        SQLResult sqlResult = arServerUser.getListSQL(peopleQuery.toString(), 0, true);

        List<Map<String, ? extends Object>> fQuery = new ArrayList<>();
        for (List<Value> content : sqlResult.getContents()) {
            Map<String, String> appEntry = new HashMap<>();
            appEntry.put("full_name", content.get(0).toString());
            appEntry.put("department", content.get(1).toString());
            appEntry.put("building", content.get(2).toString());
            appEntry.put("email_address", content.get(3).toString());
            appEntry.put("employee_number", content.get(4).toString());
            appEntry.put("floor", content.get(5).toString());
            appEntry.put("suite", content.get(6).toString());
            appEntry.put("office", content.get(7).toString());
            appEntry.put("business_organization", content.get(8).toString());
            if (buisnessorganization != null) {
                if (buisnessorganization.get(content.get(8).toString()) != null) {
                    appEntry.put("business_organization_name", buisnessorganization.get(content.get(8).toString()));
                } else {
                    appEntry.put("business_organization_name", content.get(8).toString());
                }
            } else {
                appEntry.put("business_organization_name", "");
            }
            appEntry.put("phone_work", content.get(9).toString());
            appEntry.put("first_name", content.get(10).toString());
            appEntry.put("last_name", content.get(11).toString());
            appEntry.put("login_id", content.get(12).toString());
            appEntry.put("Phone_Ext", content.get(13).toString());
            appEntry.put("GUID", content.get(14).toString());
            appEntry.put("Designation", content.get(15).toString());
            appEntry.put("Fax", content.get(16).toString());
            appEntry.put("VIP", content.get(17).toString());
            appEntry.put("Title", content.get(18).toString());
            appEntry.put("Network_Login", content.get(19).toString());
            appEntry.put("Queue", content.get(20).toString());
            appEntry.put("Designation", content.get(21).toString());
            appEntry.put("Client", content.get(22).toString());
            if (content.get(22).toString() != null && content.get(22).toString().equalsIgnoreCase("NMC")) {
                appEntry.put("business_organization_name", "Nebraska Medicine");
                appEntry.put("business_organization", "NMC");
            }

            appEntry.put("Middle_Initial", content.get(23).toString());
            appEntry.put("Cost_Code", content.get(24).toString());
            appEntry.put("Role", content.get(25).toString());
            appEntry.put("Role_Prefix", content.get(26).toString());
            appEntry.put("support_person", content.get(27).toString());
            appEntry.put("pager_numeric", content.get(28).toString());
            appEntry.put("pager_pin", content.get(29).toString());
            appEntry.put("pager_alpha", content.get(30).toString());
            appEntry.put("company_code", content.get(31).toString());
            appEntry.put("ssppreferedcontactmethod", content.get(32).toString());
            fQuery.add(appEntry);
        }

        if (fQuery != null && fQuery.size() > 0) {


            if (!creteria.contains("'")) {
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);

                org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                    String fname = stringHashMap.get("first_name").toString();
                    String lname = stringHashMap.get("last_name").toString();
                    String fullname = stringHashMap.get("full_name").toString();

                    boolean passed = false;

                    if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                        if (fullname.equalsIgnoreCase(c1)) {
                            passed = true;
                        }
                    }

                    if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                            passed = true;
                        }
                    }
                    if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                            passed = true;
                        }
                    }

                    if (!passed) {
                        if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                            String tempfname = stringHashMap.get("first_name").toString();
                            String[] tempCreteria = tempfname.split(" ");
                            if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }

                        if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                            String templname = stringHashMap.get("last_name").toString();
                            String[] tempCreteria = templname.split(" ");
                            if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }
                    }

                    boolean emailPassed = false;
                    if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                        String email = stringHashMap.get("email_address").toString();
                        if (email != null && !email.isEmpty()) {
                            if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                    emailPassed = true;
                                }
                            }
                        }
                    }

                    if (!emailPassed && !passed) {

                        boolean networkloginPassed = false;
                        if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                            String login = stringHashMap.get("Network_Login").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                        passed = true;
                                        networkloginPassed = true;
                                    }
                                }
                            }
                        }
                        if (!networkloginPassed) {
                            boolean loginPassed = false;
                            if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                                String login = stringHashMap.get("login_id").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            passed = true;
                                            loginPassed = true;
                                        }
                                    }
                                }
                            }

                            if (!loginPassed) {
                                if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                    String empno = stringHashMap.get("employee_number").toString();
                                    if (empno != null && !empno.isEmpty()) {
                                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                            if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                                passed = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return passed;
                };


                Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(fQuery, predicate);
                fQuery.clear();


                canVote.parallelStream().forEach(fQuery::add);

                if (fQuery.isEmpty() && !(fQuery.size() > 0))
                    fQuery.add(returnError("No data found for the given criteria"));
            }
        } else {
            fQuery.add(returnError("No data found for the given criteria"));
        }


        return fQuery;
    }

    public static List<Entry> queryEntrysByQualwithmaxrecords(ARServerUser server,
                                                              String formName,
                                                              int[] requiredFields,
                                                              String qualStr) {
        List<Entry> entryList = new ArrayList<>();
        List<Entry> entryList1 = new ArrayList<>();
        try {
            List<Field> fields = server.getListFieldObjects(formName);
            QualifierInfo qual = server.parseQualification(qualStr, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);

            OutputInteger nMatches = new OutputInteger();

            int startsize = 0;
            do {
                entryList = server.getListEntryObjects(
                        formName, qual, startsize,
                        Constants.AR_NO_MAX_LIST_RETRIEVE,
                        null, ArrayUtils.removeElement(requiredFields, 15), true, nMatches);
                startsize += entryList.size();
                entryList1.addAll(entryList);
            } while (startsize < nMatches.intValue());
        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList1;
    }


    public static List<Map<String, ? extends Object>> userSearchsqlquerywithcssweb(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) throws ARException {

        List<Map<String, ? extends Object>> results = new ArrayList<>();


        creteria = StringEscapeUtils.escapeSql(creteria);
        String c1 = creteria;
        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();


        String sQuery = "SELECT Full_Name,\n" +
                "       Department,\n" +
                "       Building,\n" +
                "       Email_Address,\n" +
                "       Employee_Number,\n" +
                "       Floor,\n" +
                "       Suite,\n" +
                "       Office,\n" +
                "       Business_Organization,\n" +
                "       Phone_Work,\n" +
                "       First_Name,\n" +
                "       Last_Name,\n" +
                "       Login_ID,\n" +
                "       Phone_Ext,\n" +
                "       GUID,\n" +
                "       Designation,\n" +
                "       Fax,\n" +
                "       VIP,\n" +
                "       Title,\n" +
                "       Network_Login,\n" +
                "       Queue,\n" +
                "       Designation,\n" +
                "       Client,\n" +
                "       Middle_Init,\n" +
                "       Cost_Code,\n" +
                "       Role_x,\n" +
                "       Role_Prefix,\n" +
                "       Support_Person_,\n" +
                "       Pager_Numeric,\n" +
                "       Pager_Pin,\n" +
                "       Pager_Alpha,\n" +
                "       Company_Code\n" +
                "FROM CMN_People_Information\n" +
                " WITH (NOLOCK) WHERE Client IN (SELECT Client FROM CMN_ClientInfo WITH (NOLOCK) WHERE (CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "') OR ( CMN_ClientInfo.Queue = \'Web Services\' AND Status = 0 ) )\n" +
                "AND ((First_Name is NOT NULL AND Last_Name is NOT NULL  AND Full_Name is not NULL AND Login_ID is NOT NULL) \n";

        StringBuilder peopleQuery = new StringBuilder(sQuery);

        String clientquery = "SELECT Client,Client_Name FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "' ";

        SQLResult clientsqlResult = arServerUser.getListSQL(clientquery.toString(), 0, true);
        for (List<Value> content : clientsqlResult.getContents()) {

            buisnessorganization.put(content.get(0).toString(), content.get(1).toString());
        }

        peopleQuery.append(" AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch2("Email_Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Employee_Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("First_Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch2("Last_Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Login_ID", splittedCreteria, "login"));
        }


        peopleQuery.append("(Full_Name = '" + creteria + "')");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");
        peopleQuery.append(") ");
        String status = "2";
        peopleQuery.append("AND Status = '" + status + "'");


        SQLResult sqlResult = arServerUser.getListSQL(peopleQuery.toString(), 0, true);

        List<Map<String, ? extends Object>> fQuery = new ArrayList<>();
        for (List<Value> content : sqlResult.getContents()) {
            Map<String, String> appEntry = new HashMap<>();
            appEntry.put("full_name", content.get(0).toString());
            appEntry.put("department", content.get(1).toString());
            appEntry.put("building", content.get(2).toString());
            appEntry.put("email_address", content.get(3).toString());
            appEntry.put("employee_number", content.get(4).toString());
            appEntry.put("floor", content.get(5).toString());
            appEntry.put("suite", content.get(6).toString());
            appEntry.put("office", content.get(7).toString());
            appEntry.put("business_organization", content.get(8).toString());
            if (buisnessorganization != null) {
                if (buisnessorganization.get(content.get(8).toString()) != null) {
                    appEntry.put("business_organization_name", buisnessorganization.get(content.get(8).toString()));
                } else {
                    appEntry.put("business_organization_name", content.get(8).toString());
                }
            } else {
                appEntry.put("business_organization_name", "");
            }
            appEntry.put("phone_work", content.get(9).toString());
            appEntry.put("first_name", content.get(10).toString());
            appEntry.put("last_name", content.get(11).toString());
            appEntry.put("login_id", content.get(12).toString());
            appEntry.put("Phone_Ext", content.get(13).toString());
            appEntry.put("GUID", content.get(14).toString());
            appEntry.put("Designation", content.get(15).toString());
            appEntry.put("Fax", content.get(16).toString());
            appEntry.put("VIP", content.get(17).toString());
            appEntry.put("Title", content.get(18).toString());
            appEntry.put("Network_Login", content.get(19).toString());
            appEntry.put("Queue", content.get(20).toString());
            appEntry.put("Designation", content.get(21).toString());
            appEntry.put("Client", content.get(22).toString());
            if (content.get(22).toString() != null && content.get(22).toString().equalsIgnoreCase("NMC")) {
                appEntry.put("business_organization_name", "Nebraska Medicine");
                appEntry.put("business_organization", "NMC");
            }

            appEntry.put("Middle_Initial", content.get(23).toString());
            appEntry.put("Cost_Code", content.get(24).toString());
            appEntry.put("Role", content.get(25).toString());
            appEntry.put("Role_Prefix", content.get(26).toString());
            appEntry.put("support_person", content.get(27).toString());
            appEntry.put("pager_numeric", content.get(28).toString());
            appEntry.put("pager_pin", content.get(29).toString());
            appEntry.put("pager_alpha", content.get(30).toString());
            appEntry.put("company_code", content.get(31).toString());
            fQuery.add(appEntry);
        }

        if (fQuery != null && fQuery.size() > 0) {


            if (!creteria.contains("'")) {
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);

                org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                    String fname = stringHashMap.get("first_name").toString();
                    String lname = stringHashMap.get("last_name").toString();
                    String fullname = stringHashMap.get("full_name").toString();

                    boolean passed = false;

                    if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                        if (fullname.equalsIgnoreCase(c1)) {
                            passed = true;
                        }
                    }

                    if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                            passed = true;
                        }
                    }
                    if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                            passed = true;
                        }
                    }

                    if (!passed) {
                        if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                            String tempfname = stringHashMap.get("first_name").toString();
                            String[] tempCreteria = tempfname.split(" ");
                            if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }

                        if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                            String templname = stringHashMap.get("last_name").toString();
                            String[] tempCreteria = templname.split(" ");
                            if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }
                    }

                    boolean emailPassed = false;
                    if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                        String email = stringHashMap.get("email_address").toString();
                        if (email != null && !email.isEmpty()) {
                            if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                    emailPassed = true;
                                }
                            }
                        }
                    }

                    if (!emailPassed && !passed) {

                        boolean networkloginPassed = false;
                        if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                            String login = stringHashMap.get("Network_Login").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                        passed = true;
                                        networkloginPassed = true;
                                    }
                                }
                            }
                        }
                        if (!networkloginPassed) {
                            boolean loginPassed = false;
                            if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                                String login = stringHashMap.get("login_id").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            passed = true;
                                            loginPassed = true;
                                        }
                                    }
                                }
                            }

                            if (!loginPassed) {
                                if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                    String empno = stringHashMap.get("employee_number").toString();
                                    if (empno != null && !empno.isEmpty()) {
                                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                            if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                                passed = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return passed;
                };


                Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(fQuery, predicate);
                fQuery.clear();


                canVote.parallelStream().forEach(fQuery::add);

                if (fQuery.isEmpty() && !(fQuery.size() > 0))
                    fQuery.add(returnError("No data found for the given criteria"));
            }
        } else {
            fQuery.add(returnError("No data found for the given criteria"));
        }


        return fQuery;
    }


    public static List<Map<String, ? extends Object>> userSearchwithstatussqlquery(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1,
            String status

    ) throws ARException {

        List<Map<String, ? extends Object>> results = new ArrayList<>();
        creteria = StringEscapeUtils.escapeSql(creteria);

        String c1 = creteria;
        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();

        String sQuery = "SELECT Full_Name,\n" +
                "       Department,\n" +
                "       Building,\n" +
                "       Email_Address,\n" +
                "       Employee_Number,\n" +
                "       Floor,\n" +
                "       Suite,\n" +
                "       Office,\n" +
                "       Business_Organization,\n" +
                "       Phone_Work,\n" +
                "       First_Name,\n" +
                "       Last_Name,\n" +
                "       Login_ID,\n" +
                "       Phone_Ext,\n" +
                "       GUID,\n" +
                "       Designation,\n" +
                "       Fax,\n" +
                "       VIP,\n" +
                "       Title,\n" +
                "       Network_Login,\n" +
                "       Queue,\n" +
                "       Designation,\n" +
                "       Client,\n" +
                "       Middle_Init,\n" +
                "       Cost_Code,\n" +
                "       Role_x,\n" +
                "       Role_Prefix,\n" +
                "       Support_Person_,\n" +
                "       Pager_Numeric,\n" +
                "       Pager_Pin,\n" +
                "       Pager_Alpha,\n" +
                "       Company_Code\n" +
                "FROM CMN_People_Information\n" +
                " WITH (NOLOCK) WHERE Client IN (SELECT Client FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "')\n" +
                "AND ((First_Name is NOT NULL AND Last_Name is NOT NULL  AND Full_Name is not NULL AND Login_ID is NOT NULL ) \n";

        StringBuilder peopleQuery = new StringBuilder(sQuery);

        String clientquery = "SELECT Client,Client_Name FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "' ";

        SQLResult clientsqlResult = arServerUser.getListSQL(clientquery.toString(), 0, true);
        for (List<Value> content : clientsqlResult.getContents()) {

            buisnessorganization.put(content.get(0).toString(), content.get(1).toString());
        }

        peopleQuery.append(" AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch2("Email_Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Employee_Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("First_Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch2("Last_Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Login_ID", splittedCreteria, "login"));
        }


        peopleQuery.append("(Full_Name = '" + creteria + "')");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");
        peopleQuery.append(") ");

        if (status.equalsIgnoreCase("ALL")) {

        } else {
            status = "2";
            peopleQuery.append("AND Status = '" + status + "'");
        }

        SQLResult sqlResult = arServerUser.getListSQL(peopleQuery.toString(), 0, true);

        List<Map<String, ? extends Object>> fQuery = new ArrayList<>();
        for (List<Value> content : sqlResult.getContents()) {
            Map<String, String> appEntry = new HashMap<>();
            appEntry.put("full_name", content.get(0).toString());
            appEntry.put("department", content.get(1).toString());
            appEntry.put("building", content.get(2).toString());
            appEntry.put("email_address", content.get(3).toString());
            appEntry.put("employee_number", content.get(4).toString());
            appEntry.put("floor", content.get(5).toString());
            appEntry.put("suite", content.get(6).toString());
            appEntry.put("office", content.get(7).toString());
            appEntry.put("business_organization", content.get(8).toString());
            if (buisnessorganization != null) {
                if (buisnessorganization.get(content.get(8).toString()) != null) {
                    appEntry.put("business_organization_name", buisnessorganization.get(content.get(8).toString()));
                } else {
                    appEntry.put("business_organization_name", content.get(8).toString());
                }
            } else {
                appEntry.put("business_organization_name", "");
            }
            appEntry.put("phone_work", content.get(9).toString());
            appEntry.put("first_name", content.get(10).toString());
            appEntry.put("last_name", content.get(11).toString());
            appEntry.put("login_id", content.get(12).toString());
            appEntry.put("Phone_Ext", content.get(13).toString());
            appEntry.put("GUID", content.get(14).toString());
            appEntry.put("Designation", content.get(15).toString());
            appEntry.put("Fax", content.get(16).toString());
            appEntry.put("VIP", content.get(17).toString());
            appEntry.put("Title", content.get(18).toString());
            appEntry.put("Network_Login", content.get(19).toString());
            appEntry.put("Queue", content.get(20).toString());
            appEntry.put("Designation", content.get(21).toString());
            appEntry.put("Client", content.get(22).toString());
            if (content.get(22).toString() != null && content.get(22).toString().equalsIgnoreCase("NMC")) {
                appEntry.put("business_organization_name", "Nebraska Medicine");
                appEntry.put("business_organization", "NMC");
            }

            appEntry.put("Middle_Initial", content.get(23).toString());
            appEntry.put("Cost_Code", content.get(24).toString());
            appEntry.put("Role", content.get(25).toString());
            appEntry.put("Role_Prefix", content.get(26).toString());
            appEntry.put("support_person", content.get(27).toString());
            appEntry.put("pager_numeric", content.get(28).toString());
            appEntry.put("pager_pin", content.get(29).toString());
            appEntry.put("pager_alpha", content.get(30).toString());
            appEntry.put("company_code", content.get(31).toString());
            fQuery.add(appEntry);
        }

        if (fQuery != null && fQuery.size() > 0) {

            if (!creteria.contains("'")) {
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);

                org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                    String fname = stringHashMap.get("first_name").toString();
                    String lname = stringHashMap.get("last_name").toString();
                    String fullname = stringHashMap.get("full_name").toString();

                    boolean passed = false;

                    if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                        if (fullname.equalsIgnoreCase(c1)) {
                            passed = true;
                        }
                    }

                    if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                            passed = true;
                        }
                    }
                    if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                            passed = true;
                        }
                    }

                    if (!passed) {
                        if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                            String tempfname = stringHashMap.get("first_name").toString();
                            String[] tempCreteria = tempfname.split(" ");
                            if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }

                        if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                            String templname = stringHashMap.get("last_name").toString();
                            String[] tempCreteria = templname.split(" ");
                            if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }
                    }

                    boolean emailPassed = false;
                    if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                        String email = stringHashMap.get("email_address").toString();
                        if (email != null && !email.isEmpty()) {
                            if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                    emailPassed = true;
                                }
                            }
                        }
                    }

                    if (!emailPassed && !passed) {

                        boolean networkloginPassed = false;
                        if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                            String login = stringHashMap.get("Network_Login").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                        passed = true;
                                        networkloginPassed = true;
                                    }
                                }
                            }
                        }
                        if (!networkloginPassed) {
                            boolean loginPassed = false;
                            if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                                String login = stringHashMap.get("login_id").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            passed = true;
                                            loginPassed = true;
                                        }
                                    }
                                }
                            }

                            if (!loginPassed) {
                                if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                    String empno = stringHashMap.get("employee_number").toString();
                                    if (empno != null && !empno.isEmpty()) {
                                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                            if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                                passed = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return passed;
                };


                Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(fQuery, predicate);
                fQuery.clear();


                canVote.parallelStream().forEach(fQuery::add);

                if (fQuery.isEmpty() && !(fQuery.size() > 0))
                    fQuery.add(returnError("No data found for the given criteria"));
            }
        } else {
            fQuery.add(returnError("No data found for the given criteria"));
        }


        return fQuery;
    }


    public static List<Map<String, ? extends Object>> userSearch(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) throws ARException {

        List<Map<String, ? extends Object>> results = new ArrayList<>();

        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();

        int i = 0;
        String query = null;
        int[] clientfieldid = new int[]{536870913, 536870918, 536870914};//536870918	Client Name
        int[] masterclientfieldid = new int[]{536870914};
        String masterclient = new String();
        masterclient = "";
        List<Entry> entrie = queryEntrysByQual(
                arServerUser,
                "CMN:ClientInfo",
                clientfieldid,
                "('Master Client' = \"" + client + "\" OR 'Client'= \"" + client + "\")"
        );

        StringBuilder peopleQuery = new StringBuilder("(");

        if (!(entrie.size() == 0 || entrie == null)) {
            for (Entry entry : entrie) {

                if (i == 0) {
                    peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                } else {
                    peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                }
                ++i;
                masterclient = entry.get(536870914).toString();
                buisnessorganization.put(entry.get(clientfieldid[0]).toString(), entry.get(clientfieldid[1]).toString());
            }
        } else {
            peopleQuery.append("('Client' = \"" + client + "\")");
        }


//        if (!masterclient.isEmpty()) {
//            List<Entry> entries = queryEntrysByQual(
//                    arServerUser,
//                    "CMN:ClientInfo",
//                    clientfieldid,
//                    "('Master Client' = \"" + masterclient + "\")"
//            );
//
//            for (Entry entry : entries) {
//                buisnessorganization.put(entry.get(clientfieldid[0]).toString(), entry.get(clientfieldid[1]).toString());
//
//            }
//        }

        peopleQuery.append(")");


        peopleQuery.append(" AND (  ");


        peopleQuery.append("('First Name' != $NULL$) AND ");
        peopleQuery.append("('Last Name' != $NULL$) AND ");
        peopleQuery.append("('Full Name' != $NULL$) AND ");
        peopleQuery.append("('Login ID' != $NULL$) AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch("Email Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch("Employee Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch("First Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch("Last Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch("Login ID", splittedCreteria, "login"));
        }


        peopleQuery.append("('Full Name' = \"" + creteria + "\")");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");

        peopleQuery.append(" )");
        String status = "Active";
        peopleQuery.append("AND 'Status' =\"" + status + "\"");

        int[] rf = new int[]{
                8, //fullname 0
                200000006, //dept 1
                200000007, //building 2
                700001012, //email 3
                700001038, //empno 4
                910000100, //floor 5
                910000101, //suite 6
                910000102, //office 7
                910000103, //busorg 8
                910000104, //ph work 9
                700001084, //fname 10
                700001082, //lname 11
                700001039, //loginid 12
                910000105,//	Phone Ext 13
                179,//	GUID 14
                700001132,//	Designation 15
                910000106,//	Fax 16
                700001022,//	VIP 17
                620000180,//	Title 18
                536870949,//	Network Login 19
                910000350,//	Queue 20
                700001132,//	Designation 21
                200000012,//	Client 22
                700001083,//middle Initial 23
                910000416,//cost code
                910000415,//Role
                910000414,//Role Prefix
                704000051,//support person
                910000107,//	Pager-Numeric
                910000108,//	Pager Pin
                910000109,//	Pager-Alpha
                734000428,//	Company Code
                700001011, //SSPPrefContactMethod
        };
        List<Entry> entries1 = queryEntrysByQual(
                arServerUser,
                "CMN:People Information",
                rf,
                peopleQuery.toString()
        );

        if (entries1 != null && entries1.size() > 0) {

            entries1.stream().forEach(entry -> {

                Map<String, String> sEntry = new HashMap<>();
                sEntry.put("full_name", entry.get(rf[0]).toString());
                sEntry.put("department", entry.get(rf[1]).toString());
                sEntry.put("building", entry.get(rf[2]).toString());
                sEntry.put("email_address", entry.get(rf[3]).toString());
                sEntry.put("employee_number", entry.get(rf[4]).toString());
                sEntry.put("floor", entry.get(rf[5]).toString());
                sEntry.put("suite", entry.get(rf[6]).toString());
                sEntry.put("office", entry.get(rf[7]).toString());
                sEntry.put("business_organization", entry.get(rf[8]).toString());
                if (buisnessorganization != null) {
                    if (buisnessorganization.get(entry.get(rf[8]).toString()) != null) {
                        sEntry.put("business_organization_name", buisnessorganization.get(entry.get(rf[8]).toString()));
                    } else {
                        sEntry.put("business_organization_name", entry.get(rf[8]).toString());
                    }
                } else {
                    sEntry.put("business_organization_name", "");
                }
                sEntry.put("phone_work", entry.get(rf[9]).toString());
                sEntry.put("first_name", entry.get(rf[10]).toString());
                sEntry.put("last_name", entry.get(rf[11]).toString());
                sEntry.put("login_id", entry.get(rf[12]).toString());
                sEntry.put("Phone_Ext", entry.get(rf[13]).toString());
                sEntry.put("GUID", entry.get(rf[14]).toString());
                sEntry.put("Designation", entry.get(rf[15]).toString());
                sEntry.put("Fax", entry.get(rf[16]).toString());
                sEntry.put("VIP", entry.get(rf[17]).toString());
                sEntry.put("Title", entry.get(rf[18]).toString());
                sEntry.put("Network_Login", entry.get(rf[19]).toString());
                sEntry.put("Queue", entry.get(rf[20]).toString());
                sEntry.put("Designation", entry.get(rf[21]).toString());
                sEntry.put("Client", entry.get(rf[22]).toString());
                if (entry.get(rf[22]).toString() != null && entry.get(rf[22]).toString().equalsIgnoreCase("NMC")) {
                    sEntry.put("business_organization_name", "Nebraska Medicine");
                    sEntry.put("business_organization", "NMC");
                }

                sEntry.put("Middle_Initial", entry.get(rf[23]).toString());
                sEntry.put("Cost_Code", entry.get(rf[24]).toString());
                sEntry.put("Role", entry.get(rf[25]).toString());
                sEntry.put("Role_Prefix", entry.get(rf[26]).toString());
                sEntry.put("support_person", entry.get(rf[27]).toString());
                sEntry.put("pager_numeric", entry.get(rf[28]).toString());
                sEntry.put("pager_pin", entry.get(rf[29]).toString());
                sEntry.put("pager_alpha", entry.get(rf[30]).toString());
                sEntry.put("company_code", entry.get(rf[31]).toString());
                sEntry.put("ssppreferedcontactmethod", entry.get(rf[32]).toString());
                results.add(sEntry);
            });
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);


            org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                String fname = stringHashMap.get("first_name").toString();
                String lname = stringHashMap.get("last_name").toString();
                String fullname = stringHashMap.get("full_name").toString();

                boolean passed = false;

                if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                    if (fullname.equalsIgnoreCase(creteria)) {
                        passed = true;
                    }
                }

                if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                    if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                        passed = true;
                    }
                }
                if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                    if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                        passed = true;
                    }
                }

                if (!passed) {
                    if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                        String tempfname = stringHashMap.get("first_name").toString();
                        String[] tempCreteria = tempfname.split(" ");
                        if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                            if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                }
                            }
                        }
                    }

                    if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                        String templname = stringHashMap.get("last_name").toString();
                        String[] tempCreteria = templname.split(" ");
                        if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                            if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                }
                            }
                        }
                    }
                }

                boolean emailPassed = false;
                if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                    String email = stringHashMap.get("email_address").toString();
                    if (email != null && !email.isEmpty()) {
                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                            if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                passed = true;
                                emailPassed = true;
                            }
                        }
                    }
                }

                if (!emailPassed && !passed) {

                    boolean networkloginPassed = false;
                    if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                        String login = stringHashMap.get("Network_Login").toString();
                        if (login != null && !login.isEmpty()) {
                            if (splittedCreteria.length > 0) {
                                if (login.toLowerCase().equalsIgnoreCase(creteria)) {
                                    passed = true;
                                    networkloginPassed = true;
                                }
                            }
                        }
                    }
                    if (!networkloginPassed) {
                        boolean loginPassed = false;
                        if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                            String login = stringHashMap.get("login_id").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(creteria)) {
                                        passed = true;
                                        loginPassed = true;
                                    }
                                }
                            }
                        }

                        if (!loginPassed) {
                            if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                String empno = stringHashMap.get("employee_number").toString();
                                if (empno != null && !empno.isEmpty()) {
                                    if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                        if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                            passed = true;
                                        }
                                    }

                                    /*for (String s : splittedCreteria) {
                                        if ((levenshteinDistance.apply(empno, s) <= 5) && empno.toLowerCase().startsWith(s.toLowerCase())) {
                                            passed = true;
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                }

                return passed;
            };


            Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(results, predicate);
            results.clear();


            canVote.parallelStream().forEach(stringMap1 -> {
                results.add(stringMap1);
            });

            if (results.isEmpty() && !(results.size() > 0)) {
                results.add(returnError("No data found for the given criteria"));
            }


        } else {
            results.add(returnError("No data found for the given criteria"));
        }


        return results;
    }


    public static List<Map<String, ? extends Object>> userSearchwithstatus(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1,
            String status

    ) throws ARException {

        List<Map<String, ? extends Object>> results = new ArrayList<>();

        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();

        int i = 0;
        String query = null;
        int[] clientfieldid = new int[]{536870913, 536870918, 536870914};//536870918	Client Name
        int[] masterclientfieldid = new int[]{536870914};
        String masterclient = new String();
        masterclient = "";
        List<Entry> entrie = queryEntrysByQual(
                arServerUser,
                "CMN:ClientInfo",
                clientfieldid,
                "('Master Client' = \"" + client + "\" OR 'Client'= \"" + client + "\")"
        );
//        List<Entry> entrie1 = queryEntrysByQual(
//                arServerUser,
//                "CMN:ClientInfo",
//                clientfieldid,
//                "('Client' = \"" + client + "\")"
//        );
//        if (!(entrie1.size() == 0 || entrie1 == null)) {
//            masterclient = entrie1.get(0).get(536870914).toString();
//        }
        StringBuilder peopleQuery = new StringBuilder("(");

        if (!(entrie.size() == 0 || entrie == null)) {

            for (Entry entry : entrie) {

                if (i == 0) {
                    peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                } else {
                    peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                }
                ++i;
                masterclient = entrie.get(0).get(536870914).toString();
                buisnessorganization.put(entry.get(clientfieldid[0]).toString(), entry.get(clientfieldid[1]).toString());

            }
        } else {
            peopleQuery.append("('Client' = \"" + client + "\")");
        }


//        if (!masterclient.isEmpty()) {
//            List<Entry> entries = queryEntrysByQual(
//                    arServerUser,
//                    "CMN:ClientInfo",
//                    clientfieldid,
//                    "('Master Client' = \"" + masterclient + "\")"
//            );
//
//            for (Entry entry : entries) {
//                buisnessorganization.put(entry.get(clientfieldid[0]).toString(), entry.get(clientfieldid[1]).toString());
//            }
//        }

        peopleQuery.append(")");


        peopleQuery.append(" AND (  ");


        peopleQuery.append("('First Name' != $NULL$) AND ");
        peopleQuery.append("('Last Name' != $NULL$) AND ");
        peopleQuery.append("('Full Name' != $NULL$) AND ");
        peopleQuery.append("('Login ID' != $NULL$) AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch("Email Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch("Employee Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch("First Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch("Last Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch("Login ID", splittedCreteria, "login"));
        }


        peopleQuery.append("('Full Name' = \"" + creteria + "\")");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");

        peopleQuery.append(" )");

        if (status.equalsIgnoreCase("ALL")) {

        } else {
            peopleQuery.append(" AND 'Status' =\"" + status + "\"");
        }

        int[] rf = new int[]{
                8, //fullname 0
                200000006, //dept 1
                200000007, //building 2
                700001012, //email 3
                700001038, //empno 4
                910000100, //floor 5
                910000101, //suite 6
                910000102, //office 7
                910000103, //busorg 8
                910000104, //ph work 9
                700001084, //fname 10
                700001082, //lname 11
                700001039, //loginid 12
                910000105,//	Phone Ext 13
                179,//	GUID 14
                700001132,//	Designation 15
                910000106,//	Fax 16
                700001022,//	VIP 17
                620000180,//	Title 18
                536870949,//	Network Login 19
                910000350,//	Queue 20
                700001132,//	Designation 21
                200000012,//	Client 22
                700001083,//middle Initial 23
                910000416,//cost code
                910000415,//Role
                910000414,//Role Prefix
                704000051//support person
        };
        List<Entry> entries1 = queryEntrysByQual(
                arServerUser,
                "CMN:People Information",
                rf,
                peopleQuery.toString()
        );

        if (entries1 != null && entries1.size() > 0) {

            entries1.stream().forEach(entry -> {

                Map<String, String> sEntry = new HashMap<>();
                sEntry.put("full_name", entry.get(rf[0]).toString());
                sEntry.put("department", entry.get(rf[1]).toString());
                sEntry.put("building", entry.get(rf[2]).toString());
                sEntry.put("email_address", entry.get(rf[3]).toString());
                sEntry.put("employee_number", entry.get(rf[4]).toString());
                sEntry.put("floor", entry.get(rf[5]).toString());
                sEntry.put("suite", entry.get(rf[6]).toString());
                sEntry.put("office", entry.get(rf[7]).toString());
                sEntry.put("business_organization", entry.get(rf[8]).toString());
                if (buisnessorganization != null) {
                    if (buisnessorganization.get(entry.get(rf[8]).toString()) != null) {
                        sEntry.put("business_organization_name", buisnessorganization.get(entry.get(rf[8]).toString()));
                    } else {
                        sEntry.put("business_organization_name", entry.get(rf[8]).toString());
                    }
                } else {
                    sEntry.put("business_organization_name", "");
                }
                sEntry.put("phone_work", entry.get(rf[9]).toString());
                sEntry.put("first_name", entry.get(rf[10]).toString());
                sEntry.put("last_name", entry.get(rf[11]).toString());
                sEntry.put("login_id", entry.get(rf[12]).toString());
                sEntry.put("Phone_Ext", entry.get(rf[13]).toString());
                sEntry.put("GUID", entry.get(rf[14]).toString());
                sEntry.put("Designation", entry.get(rf[15]).toString());
                sEntry.put("Fax", entry.get(rf[16]).toString());
                sEntry.put("VIP", entry.get(rf[17]).toString());
                sEntry.put("Title", entry.get(rf[18]).toString());
                sEntry.put("Network_Login", entry.get(rf[19]).toString());
                sEntry.put("Queue", entry.get(rf[20]).toString());
                sEntry.put("Designation", entry.get(rf[21]).toString());
                sEntry.put("Client", entry.get(rf[22]).toString());
                sEntry.put("Middle_Initial", entry.get(rf[23]).toString());
                sEntry.put("Cost_Code", entry.get(rf[24]).toString());
                sEntry.put("Role", entry.get(rf[25]).toString());
                sEntry.put("Role_Prefix", entry.get(rf[26]).toString());
                sEntry.put("support_person", entry.get(rf[27]).toString());

                results.add(sEntry);
            });
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);


            org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                String fname = stringHashMap.get("first_name").toString();
                String lname = stringHashMap.get("last_name").toString();
                String fullname = stringHashMap.get("full_name").toString();

                boolean passed = false;

                if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                    if (fullname.equalsIgnoreCase(creteria)) {
                        passed = true;
                    }
                }

                if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                    if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                        passed = true;
                    }
                }
                if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                    if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                        passed = true;
                    }
                }

                if (!passed) {
                    if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                        String tempfname = stringHashMap.get("first_name").toString();
                        String[] tempCreteria = tempfname.split(" ");
                        if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                            if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                }
                            }
                        }
                    }

                    if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                        String templname = stringHashMap.get("last_name").toString();
                        String[] tempCreteria = templname.split(" ");
                        if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                            if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                }
                            }
                        }
                    }
                }

                boolean emailPassed = false;
                if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                    String email = stringHashMap.get("email_address").toString();
                    if (email != null && !email.isEmpty()) {
                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                            if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                passed = true;
                                emailPassed = true;
                            }
                        }
                    }
                }

                if (!emailPassed && !passed) {

                    boolean networkloginPassed = false;
                    if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                        String login = stringHashMap.get("Network_Login").toString();
                        if (login != null && !login.isEmpty()) {
                            if (splittedCreteria.length > 0) {
                                if (login.toLowerCase().equalsIgnoreCase(creteria)) {
                                    passed = true;
                                    networkloginPassed = true;
                                }
                            }
                        }
                    }
                    if (!networkloginPassed) {
                        boolean loginPassed = false;
                        if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                            String login = stringHashMap.get("login_id").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(creteria)) {
                                        passed = true;
                                        loginPassed = true;
                                    }
                                }
                            }
                        }

                        if (!loginPassed) {
                            if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                String empno = stringHashMap.get("employee_number").toString();
                                if (empno != null && !empno.isEmpty()) {
                                    if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                        if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                            passed = true;
                                        }
                                    }

                                    /*for (String s : splittedCreteria) {
                                        if ((levenshteinDistance.apply(empno, s) <= 5) && empno.toLowerCase().startsWith(s.toLowerCase())) {
                                            passed = true;
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                }

                return passed;
            };


            Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(results, predicate);
            results.clear();


            canVote.parallelStream().forEach(stringMap1 -> {
                results.add(stringMap1);
            });

            if (results.isEmpty() && !(results.size() > 0)) {
                results.add(returnError("No data found for the given criteria"));
            }


        } else {
            results.add(returnError("No data found for the given criteria"));
        }


        return results;
    }

    public static String resultcount(ARServerUser user, String formname, String queryString) {
        OutputInteger totalCountResult = new OutputInteger(); // This will store the result of the count
        QualifierInfo qualification = null;
        try {
            qualification = user.parseQualification(formname, queryString);
            user.getListEntry(formname, qualification, 0, 1, null, null, false, totalCountResult);
        } catch (ARException e) {
            e.printStackTrace();
        }
        return totalCountResult.toString();
    }


    public static List remedyresultset(ARServerUser user, String formname, List<Entry> entries, HttpServletRequest request) {
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
                                entryvalue = RemedyBase.dateTimefieldvalue(fieldentry, entry.get(integer));
                            } else if (fieldentry instanceof SelectionField) {
                                entryvalue = RemedyBase.selectionfieldvalue(fieldentry, entry.get(integer).toString());
                            } else if (fieldentry instanceof DiaryField) {
                                entryvalue = RemedyBase.diaryfieldValue(fieldentry, entry.get(integer));
                            } else if (fieldentry instanceof AttachmentField) {
                                try {
                                    entryvalue = RemedyBase.createattatchmenturi(user, formname, entry.getEntryId(), fieldentry, entry.get(integer), request, entry);
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


    public static List<Map<String, ? extends Object>> ctcauserSearch(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) throws ARException {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        creteria = StringEscapeUtils.escapeSql(creteria);
        String c1 = creteria;
        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();

        String sQuery = "SELECT Full_Name,\n" +
                "       Department,\n" +
                "       Building,\n" +
                "       Email_Address,\n" +
                "       Employee_Number,\n" +
                "       Floor,\n" +
                "       Suite,\n" +
                "       Office,\n" +
                "       Business_Organization,\n" +
                "       Phone_Work,\n" +
                "       First_Name,\n" +
                "       Last_Name,\n" +
                "       Login_ID,\n" +
                "       Phone_Ext,\n" +
                "       GUID,\n" +
                "       Designation,\n" +
                "       Fax,\n" +
                "       VIP,\n" +
                "       Title,\n" +
                "       Network_Login,\n" +
                "       Queue,\n" +
                "       Designation,\n" +
                "       Client,\n" +
                "       Middle_Init,\n" +
                "       Cost_Code,\n" +
                "       Role_x,\n" +
                "       Role_Prefix,\n" +
                "       Support_Person_,\n" +
                "       Pager_Numeric,\n" +
                "       Pager_Pin,\n" +
                "       Pager_Alpha,\n" +
                "       Company_Code\n" +
                "FROM CMN_People_Information\n" +
                " WITH (NOLOCK) WHERE Client IN (SELECT Client FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "')\n" +
                "AND ((First_Name is NOT NULL AND Last_Name is NOT NULL  AND Full_Name is not NULL AND Login_ID is NOT NULL) \n";

        StringBuilder peopleQuery = new StringBuilder(sQuery);

        buisnessorganization.put("CTAZ", "CCRC-Phoenix");
        buisnessorganization.put("CTCC", "CTCA - CORP Schaumburg");
        buisnessorganization.put("CTFL", "CTCA - CORP BOCA");
        buisnessorganization.put("CTGA", "CCRC-Atlanta");
        buisnessorganization.put("CTIL", "CCRC-Chicago");
        buisnessorganization.put("CTOK", "CCRC-Tulsa");
        buisnessorganization.put("CTPA", "CCRC-Philadelphia");
        buisnessorganization.put("OCCC", "Outpatient Care Centers");

        peopleQuery.append(" AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch2("Email_Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Employee_Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("First_Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch2("Last_Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Login_ID", splittedCreteria, "login"));
        }


        peopleQuery.append("(Full_Name = '" + creteria + "')");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");
        peopleQuery.append(") ");
        String status = "2";
        peopleQuery.append("AND Status = '" + status + "'");


        SQLResult sqlResult = arServerUser.getListSQL(peopleQuery.toString(), 0, true);

        List<Map<String, ? extends Object>> fQuery = new ArrayList<>();
        for (List<Value> content : sqlResult.getContents()) {
            Map<String, String> appEntry = new HashMap<>();
            appEntry.put("full_name", content.get(0).toString());
            appEntry.put("department", content.get(1).toString());
            appEntry.put("building", content.get(2).toString());
            appEntry.put("email_address", content.get(3).toString());
            appEntry.put("employee_number", content.get(4).toString());
            appEntry.put("floor", content.get(5).toString());
            appEntry.put("suite", content.get(6).toString());
            appEntry.put("office", content.get(7).toString());
            appEntry.put("business_organization", content.get(8).toString());
            if (buisnessorganization != null) {
                if (buisnessorganization.get(content.get(8).toString()) != null) {
                    appEntry.put("business_organization_name", buisnessorganization.get(content.get(8).toString()));
                } else {
                    appEntry.put("business_organization_name", content.get(8).toString());
                }
            } else {
                appEntry.put("business_organization_name", "");
            }
            appEntry.put("phone_work", content.get(9).toString());
            appEntry.put("first_name", content.get(10).toString());
            appEntry.put("last_name", content.get(11).toString());
            appEntry.put("login_id", content.get(12).toString());
            appEntry.put("Phone_Ext", content.get(13).toString());
            appEntry.put("GUID", content.get(14).toString());
            appEntry.put("Designation", content.get(15).toString());
            appEntry.put("Fax", content.get(16).toString());
            appEntry.put("VIP", content.get(17).toString());
            appEntry.put("Title", content.get(18).toString());
            appEntry.put("Network_Login", content.get(19).toString());
            appEntry.put("Queue", content.get(20).toString());
            appEntry.put("Designation", content.get(21).toString());
            appEntry.put("Client", content.get(22).toString());
            if (content.get(22).toString() != null && content.get(22).toString().equalsIgnoreCase("NMC")) {
                appEntry.put("business_organization_name", "Nebraska Medicine");
                appEntry.put("business_organization", "NMC");
            }

            appEntry.put("Middle_Initial", content.get(23).toString());
            appEntry.put("Cost_Code", content.get(24).toString());
            appEntry.put("Role", content.get(25).toString());
            appEntry.put("Role_Prefix", content.get(26).toString());
            appEntry.put("support_person", content.get(27).toString());
            appEntry.put("pager_numeric", content.get(28).toString());
            appEntry.put("pager_pin", content.get(29).toString());
            appEntry.put("pager_alpha", content.get(30).toString());
            appEntry.put("company_code", content.get(31).toString());
            fQuery.add(appEntry);
        }

        if (fQuery != null && fQuery.size() > 0) {

            if (!creteria.contains("'")) {
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);

                org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                    String fname = stringHashMap.get("first_name").toString();
                    String lname = stringHashMap.get("last_name").toString();
                    String fullname = stringHashMap.get("full_name").toString();

                    boolean passed = false;

                    if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                        if (fullname.equalsIgnoreCase(c1)) {
                            passed = true;
                        }
                    }

                    if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                            passed = true;
                        }
                    }
                    if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                            passed = true;
                        }
                    }

                    if (!passed) {
                        if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                            String tempfname = stringHashMap.get("first_name").toString();
                            String[] tempCreteria = tempfname.split(" ");
                            if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }

                        if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                            String templname = stringHashMap.get("last_name").toString();
                            String[] tempCreteria = templname.split(" ");
                            if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }
                    }

                    boolean emailPassed = false;
                    if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                        String email = stringHashMap.get("email_address").toString();
                        if (email != null && !email.isEmpty()) {
                            if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                    emailPassed = true;
                                }
                            }
                        }
                    }

                    if (!emailPassed && !passed) {

                        boolean networkloginPassed = false;
                        if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                            String login = stringHashMap.get("Network_Login").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                        passed = true;
                                        networkloginPassed = true;
                                    }
                                }
                            }
                        }
                        if (!networkloginPassed) {
                            boolean loginPassed = false;
                            if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                                String login = stringHashMap.get("login_id").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            passed = true;
                                            loginPassed = true;
                                        }
                                    }
                                }
                            }

                            if (!loginPassed) {
                                if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                    String empno = stringHashMap.get("employee_number").toString();
                                    if (empno != null && !empno.isEmpty()) {
                                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                            if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                                passed = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return passed;
                };


                Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(fQuery, predicate);
                fQuery.clear();


                canVote.parallelStream().forEach(fQuery::add);

                if (fQuery.isEmpty() && !(fQuery.size() > 0))
                    fQuery.add(returnError("No data found for the given criteria"));
            }
        } else {
            fQuery.add(returnError("No data found for the given criteria"));
        }


        return fQuery;
    }

    public static String validatequery(String query, List<String> status, String application) {

        if (application.isEmpty()) {
            application = "0,1,2,4";
        }
        if (query.isEmpty() && status.isEmpty()) {
            query += " WHERE (CL.Assigned_To_Group_ IS NOT NULL ) \n AND \n (Application IN (" + application + ")) AND \n (Status_Display IN ('Assigned','Pending','Acknowledged','Requested','Scheduled','Reviewing')) AND ";
        } else if (query.isEmpty() && !status.isEmpty()) {
            String statusquery = "";
            for (String s : status) {
                statusquery += "'" + s + "',";
            }
            if (!statusquery.isEmpty()) {
                statusquery = statusquery.substring(0, statusquery.length() - 1);
            }
            query += " WHERE (CL.Assigned_To_Group_ IS NOT NULL ) \n AND \n (Application IN (" + application + ")) AND (Status_Display IN (" + statusquery + ")) AND ";
        } else if (query.isEmpty()) {
            query += " WHERE (CL.Assigned_To_Group_ IS NOT NULL ) \n AND \n (Application IN (" + application + ")) AND ";
        } else {
            query += " AND ";
        }
        return query;
    }


    public static String fetchusergroups(String loginid, ARServerUser user) {
        List<GroupInfo> groupInfo = new ArrayList<>();
        StringBuilder builder = new StringBuilder("");
        try {
            groupInfo = user.getListGroup(loginid, null);
            Boolean admin = false;
            if (groupInfo != null) {
                for (int i = 0; i < groupInfo.size(); i++) {
                    if (groupInfo.get(i).getId() == 55555555 || groupInfo.get(i).getId() == 1) {
                        admin = true;
                    }
                    if (i == groupInfo.size() - 1)
                        builder.append(groupInfo.get(i).getId());
                    else
                        builder.append(groupInfo.get(i).getId() + ",");
                }
            }
            if (admin) {
                SQLResult result = user.getListSQL("select group_id from group_x where group_type=2 and group_category=0", 0, true);
                builder.delete(0, builder.length());
                for (int i = 0; i < result.getContents().size(); i++) {

                    if (i == result.getContents().size() - 1)
                        builder.append(result.getContents().get(i).get(0).toString());
                    else
                        builder.append(result.getContents().get(i).get(0).toString() + ",");

                }
            }
            /*   user.get*/
        } catch (ARException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String Md5hashing(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String escapeQuotes(String s) {
        if (s == null) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                switch (s.charAt(i)) {
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '\n':
                        sb.append("<br />");
                        break;
                    case '\t':
                        sb.append(" ");
                        break;
                    default:
                        sb.append(s.charAt(i));
                }
            }
            return sb.toString().replace("<br>", "");
        }
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                switch (s.charAt(i)) {

                    case '\'':
                        sb.append("%27");
                        break;
                    case '%':
                        sb.append("%25");
                        break;
                    case '$':
                        sb.append("%24");
                        break;
                    case '&':
                        sb.append("%26");
                        break;
                    case '+':
                        sb.append("%2B");
                        break;
                    case '/':
                        sb.append("%2F");
                        break;
                    case ':':
                        sb.append("%3A");
                        break;
                    case ';':
                        sb.append("%3B");
                        break;
                    case '=':
                        sb.append("%3D");
                        break;
                    case '?':
                        sb.append("%3F");
                        break;
                    case '@':
                        sb.append("%40");
                        break;
                    case ' ':
                        sb.append("%20");
                        break;
                    case '"':
                        sb.append("%22");
                        break;
                    case '<':
                        sb.append("%3C");
                        break;
                    case '>':
                        sb.append("%3E");
                        break;
                    case '#':
                        sb.append("%23");
                        break;
                    case '{':
                        sb.append("%7B");
                        break;
                    case '}':
                        sb.append("%7D");
                        break;
                    case '|':
                        sb.append("%7C");
                        break;
                    case '\\':
                        sb.append("%5C");
                        break;
                    case '^':
                        sb.append("%5E");
                        break;
                    case '~':
                        sb.append("%7E");
                        break;
                    case '[':
                        sb.append("%5B");
                        break;
                    case ']':
                        sb.append("%5D");
                        break;
                    case '`':
                        sb.append("%60");
                        break;
                    default:
                        sb.append(s.charAt(i));
                }
            }
            return sb.toString();
        }
    }


    public static List<Map<String, ? extends Object>> jmhsuserSearch(
            ARServerUser arServerUser,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) throws ARException {

        List<Map<String, ? extends Object>> results = new ArrayList<>();
        creteria = StringEscapeUtils.escapeSql(creteria);
        String c1 = creteria;
        String[] splittedCreteria = creteria.split(" ");
        Map<String, String> buisnessorganization = new HashMap<>();

        String sQuery = "SELECT Full_Name,\n" +
                "       Department,\n" +
                "       Building,\n" +
                "       Email_Address,\n" +
                "       Employee_Number,\n" +
                "       Floor,\n" +
                "       Suite,\n" +
                "       Office,\n" +
                "       Business_Organization,\n" +
                "       Phone_Work,\n" +
                "       First_Name,\n" +
                "       Last_Name,\n" +
                "       Login_ID,\n" +
                "       Phone_Ext,\n" +
                "       GUID,\n" +
                "       Designation,\n" +
                "       Fax,\n" +
                "       VIP,\n" +
                "       Title,\n" +
                "       Network_Login,\n" +
                "       Queue,\n" +
                "       Designation,\n" +
                "       Client,\n" +
                "       Middle_Init,\n" +
                "       Cost_Code,\n" +
                "       Role_x,\n" +
                "       Role_Prefix,\n" +
                "       Support_Person_,\n" +
                "       Pager_Numeric,\n" +
                "       Pager_Pin,\n" +
                "       Pager_Alpha,\n" +
                "       Company_Code\n" +
                "FROM CMN_People_Information\n" +
                " WITH (NOLOCK) WHERE Client IN (SELECT Client FROM CMN_ClientInfo WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "')\n" +
                "AND ((First_Name is NOT NULL AND Last_Name is NOT NULL  AND Full_Name is not NULL AND Login_ID is NOT NULL) \n";

        StringBuilder peopleQuery = new StringBuilder(sQuery);

        String clientquery = "SELECT Client,Client_Name FROM CMN_ClientInfo WITH (NOLOCK) WHERE CMN_ClientInfo.Master_Client = '" + client + "' OR CMN_ClientInfo.Client = '" + client + "'";

        SQLResult clientsqlResult = arServerUser.getListSQL(clientquery.toString(), 0, true);
        for (List<Value> content : clientsqlResult.getContents()) {

            buisnessorganization.put(content.get(0).toString(), content.get(1).toString());
        }

        peopleQuery.append(" AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch2("Email_Address", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Employee_Number", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("First_Name", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch2("Last_Name", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Login_ID", splittedCreteria, "login"));
        }


        peopleQuery.append("(Full_Name = '" + creteria + "')");
        // peopleQuery.append("('Email Address'!= $NULL$) AND ");
        // peopleQuery.append("('Email Address' != $NULL$) AND ");
        //peopleQuery.append("('Employee Number' != $NULL$) AND ");
        peopleQuery.append(") ");

        peopleQuery.append("AND (Network_Login LIKE 'P%')");


        String status = "2";
        peopleQuery.append("AND Status = '" + status + "'");


        SQLResult sqlResult = arServerUser.getListSQL(peopleQuery.toString(), 0, true);

        List<Map<String, ? extends Object>> fQuery = new ArrayList<>();
        for (List<Value> content : sqlResult.getContents()) {
            Map<String, String> appEntry = new HashMap<>();
            appEntry.put("full_name", content.get(0).toString());
            appEntry.put("department", content.get(1).toString());
            appEntry.put("building", content.get(2).toString());
            appEntry.put("email_address", content.get(3).toString());
            appEntry.put("employee_number", content.get(4).toString());
            appEntry.put("floor", content.get(5).toString());
            appEntry.put("suite", content.get(6).toString());
            appEntry.put("office", content.get(7).toString());
            appEntry.put("business_organization", content.get(8).toString());
            if (buisnessorganization != null) {
                if (buisnessorganization.get(content.get(8).toString()) != null) {
                    appEntry.put("business_organization_name", buisnessorganization.get(content.get(8).toString()));
                } else {
                    appEntry.put("business_organization_name", content.get(8).toString());
                }
            } else {
                appEntry.put("business_organization_name", "");
            }
            appEntry.put("phone_work", content.get(9).toString());
            appEntry.put("first_name", content.get(10).toString());
            appEntry.put("last_name", content.get(11).toString());
            appEntry.put("login_id", content.get(12).toString());
            appEntry.put("Phone_Ext", content.get(13).toString());
            appEntry.put("GUID", content.get(14).toString());
            appEntry.put("Designation", content.get(15).toString());
            appEntry.put("Fax", content.get(16).toString());
            appEntry.put("VIP", content.get(17).toString());
            appEntry.put("Title", content.get(18).toString());
            appEntry.put("Network_Login", content.get(19).toString());
            appEntry.put("Queue", content.get(20).toString());
            appEntry.put("Designation", content.get(21).toString());
            appEntry.put("Client", content.get(22).toString());
            if (content.get(22).toString() != null && content.get(22).toString().equalsIgnoreCase("NMC")) {
                appEntry.put("business_organization_name", "Nebraska Medicine");
                appEntry.put("business_organization", "NMC");
            }

            appEntry.put("Middle_Initial", content.get(23).toString());
            appEntry.put("Cost_Code", content.get(24).toString());
            appEntry.put("Role", content.get(25).toString());
            appEntry.put("Role_Prefix", content.get(26).toString());
            appEntry.put("support_person", content.get(27).toString());
            appEntry.put("pager_numeric", content.get(28).toString());
            appEntry.put("pager_pin", content.get(29).toString());
            appEntry.put("pager_alpha", content.get(30).toString());
            appEntry.put("company_code", content.get(31).toString());
            fQuery.add(appEntry);
        }

        if (fQuery != null && fQuery.size() > 0) {
            if (!creteria.contains("'")) {
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);

                org.apache.commons.collections4.Predicate predicate = (org.apache.commons.collections4.Predicate<HashMap<String, ? extends Object>>) stringHashMap -> {
                    String fname = stringHashMap.get("first_name").toString();
                    String lname = stringHashMap.get("last_name").toString();
                    String fullname = stringHashMap.get("full_name").toString();

                    boolean passed = false;

                    if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                        if (fullname.equalsIgnoreCase(c1)) {
                            passed = true;
                        }
                    }

                    if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                            passed = true;
                        }
                    }
                    if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                        if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                            passed = true;
                        }
                    }

                    if (!passed) {
                        if (stringHashMap.get("first_name") != null && firstname1.equalsIgnoreCase("true")) {     //firstname filter
                            String tempfname = stringHashMap.get("first_name").toString();
                            String[] tempCreteria = tempfname.split(" ");
                            if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }

                        if (stringHashMap.get("last_name") != null && lastname1.equalsIgnoreCase("true")) {     //lastname filter
                            String templname = stringHashMap.get("last_name").toString();
                            String[] tempCreteria = templname.split(" ");
                            if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                    if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                        passed = true;
                                    }
                                }
                            }
                        }
                    }

                    boolean emailPassed = false;
                    if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                        String email = stringHashMap.get("email_address").toString();
                        if (email != null && !email.isEmpty()) {
                            if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                    passed = true;
                                    emailPassed = true;
                                }
                            }
                        }
                    }

                    if (!emailPassed && !passed) {

                        boolean networkloginPassed = false;
                        if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                            String login = stringHashMap.get("Network_Login").toString();
                            if (login != null && !login.isEmpty()) {
                                if (splittedCreteria.length > 0) {
                                    if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                        passed = true;
                                        networkloginPassed = true;
                                    }
                                }
                            }
                        }
                        if (!networkloginPassed) {
                            boolean loginPassed = false;
                            if (stringHashMap.get("login_id") != null && loginid1.equalsIgnoreCase("true")) {         //loginid filter
                                String login = stringHashMap.get("login_id").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            passed = true;
                                            loginPassed = true;
                                        }
                                    }
                                }
                            }

                            if (!loginPassed) {
                                if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                    String empno = stringHashMap.get("employee_number").toString();
                                    if (empno != null && !empno.isEmpty()) {
                                        if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                            if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                                passed = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return passed;
                };


                Collection<Map<String, ? extends Object>> canVote = CollectionUtils.select(fQuery, predicate);
                fQuery.clear();


                canVote.parallelStream().forEach(fQuery::add);

                if (fQuery.isEmpty() && !(fQuery.size() > 0))
                    fQuery.add(returnError("No data found for the given criteria"));
            }
        } else {
            fQuery.add(returnError("No data found for the given criteria"));
        }
        return fQuery;
    }

    private static String buildCreateriaForUserSearch(String columnName, String[] splittedCreteria, String type) {

        StringBuilder peopleQuery = new StringBuilder("");


        if (type.equalsIgnoreCase("name") && splittedCreteria.length == 1 && splittedCreteria[0].length() >= 2) {

            peopleQuery.append(" ('").append("First Name").append("' LIKE \"").append(splittedCreteria[0]).append("%\"");
            peopleQuery.append(" OR ");
            peopleQuery.append("'").append("Last Name").append("' LIKE \"").append(splittedCreteria[0]).append("%\" ) OR ");

        } else if (type.equalsIgnoreCase("email") && splittedCreteria.length == 1) {

            peopleQuery.append(" ('").append("Email Address").append("' = \"").append(splittedCreteria[0]).append("\" ) OR ");

        } else if (type.equalsIgnoreCase("login")) {
            peopleQuery.append(" ('").append("Network Login").append("' = \"").append(String.join(" ", splittedCreteria)).append("\"");
            peopleQuery.append(" OR ");
            peopleQuery.append("'").append("Login ID").append("' = \"").append(String.join(" ", splittedCreteria)).append("\") OR ");

        } else if (type.equalsIgnoreCase("empno") && splittedCreteria.length == 1) {

            peopleQuery.append(" ('").append("Employee Number").append("' = \"").append(splittedCreteria[0]).append("\" ) OR ");

        } else if (type.equalsIgnoreCase("name") && splittedCreteria.length >= 2 && splittedCreteria.length <= 3) {
            peopleQuery.append("((");
            for (int i = 0; i < splittedCreteria.length; i++) {
                peopleQuery.append(" '").append(columnName).append("' LIKE \"").append(splittedCreteria[i]).append("%\"");
                if (i < splittedCreteria.length - 1) {
                    peopleQuery.append(" OR ");
                }
                if (i == splittedCreteria.length - 1) {
                    peopleQuery.append(" )");
                    peopleQuery.append(" AND ");
                }
            }
            peopleQuery.append(" ('").append("Full Name").append("' LIKE \"%").append(String.join("%", splittedCreteria)).append("%\" )) OR ");
        } else if (type.equalsIgnoreCase("name") && splittedCreteria.length > 3) {
            peopleQuery.append(" (('").append("First Name").append("' LIKE \"").append(splittedCreteria[0]).append("%\"");
            peopleQuery.append(" OR ");
            peopleQuery.append("'").append("Last Name").append("' LIKE \"").append(splittedCreteria[1]).append("%\" ) AND ");
            peopleQuery.append(" ('").append("Full Name").append("' LIKE \"%").append(String.join("%", splittedCreteria)).append("%\" )) OR ");
        }
        return peopleQuery.toString();
    }


    private static String buildCreateriaForUserSearch2(String columnName, String[] splittedCreteria, String type) {

        StringBuilder peopleQuery = new StringBuilder("");


        if (type.equalsIgnoreCase("name") && splittedCreteria.length == 1 && splittedCreteria[0].length() >= 2) {

            peopleQuery.append(" (").append("First_Name").append(" LIKE '").append(splittedCreteria[0]).append("%'");
            peopleQuery.append(" OR ");
            peopleQuery.append("Last_Name").append(" LIKE '").append(splittedCreteria[0]).append("%' ) OR ");

        } else if (type.equalsIgnoreCase("email") && splittedCreteria.length == 1) {

            peopleQuery.append(" (").append("Email_Address").append(" = '").append(splittedCreteria[0]).append("' ) OR ");

        } else if (type.equalsIgnoreCase("login")) {
            peopleQuery.append(" (").append("Network_Login").append(" = '").append(String.join(" ", splittedCreteria)).append("'");
            peopleQuery.append(" OR ");
            peopleQuery.append("Login_ID").append(" = '").append(String.join(" ", splittedCreteria)).append("') OR ");

        } else if (type.equalsIgnoreCase("empno") && splittedCreteria.length == 1) {

            peopleQuery.append(" (").append("Employee_Number").append(" = '").append(splittedCreteria[0]).append("' ) OR ");

        } else if (type.equalsIgnoreCase("name") && splittedCreteria.length >= 2 && splittedCreteria.length <= 3) {
            peopleQuery.append("((");
            for (int i = 0; i < splittedCreteria.length; i++) {
                peopleQuery.append(columnName).append(" LIKE '").append(splittedCreteria[i]).append("%'");
                if (i < splittedCreteria.length - 1) {
                    peopleQuery.append(" OR ");
                }
                if (i == splittedCreteria.length - 1) {
                    peopleQuery.append(" )");
                    peopleQuery.append(" AND ");
                }
            }
            peopleQuery.append(" ((").append("Full_Name").append(" LIKE '").append(String.join("%", splittedCreteria)).append("%') OR (");
            peopleQuery.append(columnName).append(" = '").append(String.join(" ", splittedCreteria)).append("'))) OR ");
        } else if (type.equalsIgnoreCase("name") && splittedCreteria.length > 3) {
            peopleQuery.append(" ((").append("First_Name").append(" LIKE '").append(splittedCreteria[0]).append("%'");
            peopleQuery.append(" OR ");
            peopleQuery.append("Last_Name").append(" LIKE '").append(splittedCreteria[1]).append("%' ) AND ");
            peopleQuery.append(" (").append("Full_Name").append(" LIKE '").append(String.join("%", splittedCreteria)).append("%' )) OR ");
        }
        return peopleQuery.toString();
    }


    public static Entry putAttachement(ARServerUser arServerUser, String formName, String entryId, String fieldName, File file) throws Exception {

        Entry entry = arServerUser.getEntry(formName, entryId, null);
        Map<Integer, String> integerStringMap = RemedyBase.getFormFields2(arServerUser, formName);

        Integer fieldId = 0;
        for (Integer integer : integerStringMap.keySet()) {
            if (fieldName.equalsIgnoreCase(integerStringMap.get(integer))) {
                fieldId = integer;
            }
        }

        if (integerStringMap.containsValue(fieldName) && fieldId != 0) {

            AttachmentValue attachmentValueNew = new AttachmentValue();
            attachmentValueNew.setFilePath(file.getAbsolutePath());
            attachmentValueNew.setName(file.getName());
            attachmentValueNew.setOriginalSize(file.getTotalSpace());
            attachmentValueNew.setValue(FileUtils.readFileToByteArray(file));


            entry.put(fieldId, new Value(attachmentValueNew));


            arServerUser.setEntry(formName, entryId, entry, null, 0);

        } else {
            throw new Exception("Cannot find field id");
        }

        return entry;
    }

    public static Entry putAttachement1(ARServerUser arServerUser, String formName, String entryId, Map<String, File> attach) throws Exception {

        // Entry entry = arServerUser.getEntry(formName, entryId, null);
        Entry entry = new Entry();
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


        if (!atat.isEmpty()) {


            List<AttachmentValue> attachmentValueNew = new ArrayList<>();
            int j = 0;
            //  AttachmentValue attachmentValue = new AttachmentValue();
            for (Map.Entry<Integer, File> attachment : atat.entrySet()) {
                AttachmentValue attachmentValue = new AttachmentValue();
                //   if (integerStringMap.containsValue() && fieldId != 0) {

                // attachmentValueNew.add(setFilePat(attachment.getValue().getAbsolutePath());
                attachmentValue.setFilePath(attachment.getValue().getAbsolutePath());
                attachmentValue.setName(attachment.getValue().getName());
                attachmentValue.setOriginalSize(attachment.getValue().getTotalSpace());
                attachmentValue.setValue(FileUtils.readFileToByteArray(attachment.getValue()));
                attachmentValueNew.add(attachmentValue);


                entry.put(attachment.getKey(), new Value(attachmentValueNew.get(j++)));

            }
            arServerUser.setEntry(formName, entryId, entry, null, 0);
            // arServerUser.setEntry();


        } else {
            throw new Exception("Cannot find field id");
        }

        return entry;
    }


    public static Entry putAttachmentwithnovalue(ARServerUser arServerUser, String formName, String entryId, Map<String, File> attach, Map<String, String> fields) throws Exception {

        // Entry entry = arServerUser.getEntry(formName, entryId, null);
        Entry entry = new Entry();

        //    entry = arServerUser.getEntry(formName, entryId, null);

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
                    //entry.put(integer, new Value(new AttachmentValue()));
                    entry.put(integer, new Value());
                }
            }
        }
        if (!atat.isEmpty()) {
            List<AttachmentValue> attachmentValueNew = new ArrayList<>();
            int j = 0;
            //  AttachmentValue attachmentValue = new AttachmentValue();
            for (Map.Entry<Integer, File> attachment : atat.entrySet()) {
                AttachmentValue attachmentValue = new AttachmentValue();
                //   if (integerStringMap.containsValue() && fieldId != 0) {

                // attachmentValueNew.add(setFilePat(attachment.getValue().getAbsolutePath());
                attachmentValue.setFilePath(attachment.getValue().getAbsolutePath());
                attachmentValue.setName(attachment.getValue().getName());
                attachmentValue.setOriginalSize(attachment.getValue().getTotalSpace());
                attachmentValue.setValue(FileUtils.readFileToByteArray(attachment.getValue()));
                attachmentValueNew.add(attachmentValue);

                entry.put(attachment.getKey(), new Value(attachmentValueNew.get(j++)));
            }
            // arServerUser.setEntry();
        }

        arServerUser.setEntry(formName, entryId, entry, null, 0);


        return entry;
    }

    public static Entry putAttachmentwithitsm(ARServerUser arServerUser, String formName, Map<String, File> attach, Map<String, String> fields) throws Exception {

        Entry entry = new Entry();
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
                    entry.put(integer, new Value());
                }
            }
        }
        if (!atat.isEmpty()) {
            List<AttachmentValue> attachmentValueNew = new ArrayList<>();
            int j = 0;
            //  AttachmentValue attachmentValue = new AttachmentValue();
            for (Map.Entry<Integer, File> attachment : atat.entrySet()) {
                AttachmentValue attachmentValue = new AttachmentValue();
                attachmentValue.setFilePath(attachment.getValue().getAbsolutePath());
                attachmentValue.setName(attachment.getValue().getName());
                attachmentValue.setOriginalSize(attachment.getValue().getTotalSpace());
                attachmentValue.setValue(FileUtils.readFileToByteArray(attachment.getValue()));
                attachmentValueNew.add(attachmentValue);

                entry.put(attachment.getKey(), new Value(attachmentValueNew.get(j++)));

            }
        }
        return entry;
    }

    public static Entry putAttachmentswithitsm(ARServerUser arServerUser, String formName, Map<String, File[]> attach) throws Exception {

        Entry entry = new Entry();
        Map<Integer, String> integerStringMap = RemedyBase.getFormFields2(arServerUser, formName);

        Map<Integer, File[]> atat = new HashMap<>();
        for (Map.Entry<String, File[]> attachment1 : attach.entrySet()) {
            for (Integer integer : integerStringMap.keySet()) {
                if (attachment1.getKey().equalsIgnoreCase(integerStringMap.get(integer)) || attachment1.getKey().equalsIgnoreCase(integer.toString())) {
                    atat.put(integer, attachment1.getValue());
                }
            }
        }
        if (!atat.isEmpty()) {
            for (Map.Entry<Integer, File[]> attachment : atat.entrySet()) {
                for (File file : attachment.getValue()) {
                    AttachmentValue attachmentValue = new AttachmentValue();
                    attachmentValue.setFilePath(file.getAbsolutePath());
                    attachmentValue.setName(file.getName());
                    attachmentValue.setOriginalSize(file.getTotalSpace());
                    attachmentValue.setValue(FileUtils.readFileToByteArray(file));
                    entry.put(attachment.getKey(), new Value(attachmentValue));
                }
            }
        }
        return entry;
    }


    public static byte[] getAttachement(ARServerUser arServerUser, String formName, String entryId, String fieldName) throws Exception {
        Entry entry = arServerUser.getEntry(formName, entryId, null);

        Map<Integer, String> integerStringMap = RemedyBase.getFormFields2(arServerUser, formName);

        Integer fieldId = 0;
        for (Integer integer : integerStringMap.keySet()) {
            if (fieldName.equalsIgnoreCase(integerStringMap.get(integer))) {
                fieldId = integer;
            }
        }

        if (fieldId != 0 && entry.containsKey(fieldId)) {
            if (entry.get(fieldId).getValue() instanceof AttachmentValue) {
                return arServerUser.getEntryBlob(formName, entryId, fieldId);
            } else {
                throw new Exception("This field is not a file");
            }
        }

        return IOUtils.toByteArray("No Response");
    }

    public static Map<String, Object> getdownloadattachment(ARServerUser arServerUser, String formName, String entryId, String fieldid) throws Exception {
        Entry entry = arServerUser.getEntry(formName, entryId, null);
        String filename = "";
        Integer fieldId = Integer.parseInt(fieldid);
        File file;
        if (fieldId != 0 && entry.containsKey(fieldId)) {
            if (entry.get(fieldId).getValue() instanceof AttachmentValue) {
                filename = ((AttachmentValue) entry.get(fieldId).getValue()).getName();
                byte[] bytes = arServerUser.getEntryBlob(formName, entryId, fieldId);
                Map res = new HashMap<String, Object>();
                res.put("file", writeByte(bytes, filename));
                res.put("name", filename);
                return res;
            }
        }
        return null;
    }


    static File writeByte(byte[] bytes, String filename) throws Exception {
        String filepath = environment.getProperty("ctsspi.fileattachment");
        File file = new File(filepath + filename);
        OutputStream os = new FileOutputStream(file);
        os.write(bytes);
        os.close();
        return file;
    }

    private static String getFileSizeKiloBytes(File file) {
        long size = Math.round((double) file.length() / 1024) + file.length() % 1024 == 0 ? 0 : 1;
        return size + " KB";
    }

    static String writeByte1(byte[] bytes, String filename) throws Exception {
        String filepath = environment.getProperty("ctsspi.fileattachment");
        File file = new File(filepath + filename);
        OutputStream os = new FileOutputStream(file);
        os.write(bytes);
        os.close();
        return getFileSizeKiloBytes(file);
    }

    public static String getCurrentUrl(HttpServletRequest request) throws Exception {
        URL url = new URL(request.getRequestURL().toString());
        String host = url.getHost();
        String scheme = url.getProtocol();

        scheme = environment.getProperty("ctsspi.server.scheme");
        int port = url.getPort();
        String userInfo = url.getUserInfo();
        String contextpath = request.getContextPath();
        URI uri = new URI(scheme, null, host, port, null, null, null);
        return uri.toString() + contextpath;
    }


    public static List<Map<String, ? extends Object>> griddata(
            ARServerUser arServerUser,
            String requesttype, String roleuniqueidentifier


    ) throws Exception {

        List<Map<String, ? extends Object>> results = new ArrayList<>();

        int[] rf = new int[]{
                1,    //Task ID
                8,  //Task
                536870931,    //IAM
                536870967,    //IAM Client
                702270969,    //IAM Temp Password
                702170968,  //IAM Application Login ID
                700001052,    //Assigned Group
                700001059,    //Assigned Individual
                704000159,    //Approval
                704000115,  //Task Sequence
                704000098  //Task Details
        };

        int[] rf1 = new int[]{      //CTS:KS_RQT_Role_Tasks+SR:PDT-Tasks
                536870913,    //Task ID
                8,          //Task
                536870931,    //IAM
                536870967,    //IAM Client
                702270969,    //IAM Temp Password
                702170968,  //IAM Application Login ID
                700001052,    //Assigned Group
                700001059,    //Assigned Individual
                704000159,    //Approval
                704000115, //Task Sequence
                704000098   //Task Details

        };
        String query1 = "('Role Unique Identifier' = \"" + roleuniqueidentifier + "\")";
        String query2 = "('Request Type' = \"" + requesttype + "\")";


        List<Entry> entries1 = RemedyBase.queryEntrysByQual(
                arServerUser,
                "SR:PDT-Tasks",
                rf,
                query2
        );
        List<Entry> entries2 = RemedyBase.queryEntrysByQual(
                arServerUser,
                "CTS:KS_RQT_Role_Tasks+SR:PDT-Tasks",
                rf1,
                query1
        );
        Map<Integer, String> formFields1 = getFormFields2(arServerUser, "SR:PDT-Tasks");
        Map<Integer, String> formFields2 = getFormFields2(arServerUser, "CTS:KS_RQT_Role_Tasks+SR:PDT-Tasks");

        if (entries1 != null || entries2 != null) {
            if (entries1 != null) {
                entries1.forEach(entry1 -> {
                    Map<String, String> sEntry = new HashMap<>();
                    sEntry.put("task_id", entry1.get(rf[0]).toString());
                    sEntry.put("task", entry1.get(rf[1]).toString());
                    sEntry.put("iam", entry1.get(rf[2]).toString());
                    sEntry.put("iam_client", entry1.get(rf[3]).toString());
                    sEntry.put("iam_temp_password", entry1.get(rf[4]).toString());
                    sEntry.put("iam_application_loginid", entry1.get(rf[5]).toString());
                    sEntry.put("assigned_group", entry1.get(rf[6]).toString());
                    sEntry.put("assigned_individual", entry1.get(rf[7]).toString());
                    sEntry.put("approval", entry1.get(rf[8]).toString());
                    sEntry.put("task_sequence", entry1.get(rf[9]).toString());
                    sEntry.put("task_details", entry1.get(rf[10]).toString());
                    results.add(sEntry);
                });
            }
            if (entries2 != null) {
                entries2.forEach(entry1 -> {
                    Map<String, String> sEntry = new HashMap<>();
                    sEntry.put("task_id", entry1.get(rf1[0]).toString());
                    sEntry.put("task", entry1.get(rf1[1]).toString());
                    sEntry.put("iam", entry1.get(rf1[2]).toString());
                    sEntry.put("iam_client", entry1.get(rf1[3]).toString());
                    sEntry.put("iam_temp_password", entry1.get(rf1[4]).toString());
                    sEntry.put("iam_application_loginid", entry1.get(rf1[5]).toString());
                    sEntry.put("assigned_group", entry1.get(rf1[6]).toString());
                    sEntry.put("assigned_individual", entry1.get(rf1[7]).toString());
                    sEntry.put("approval", entry1.get(rf1[8]).toString());
                    sEntry.put("task_sequence", entry1.get(rf1[9]).toString());
                    sEntry.put("task_details", entry1.get(rf1[10]).toString());
                    results.add(sEntry);
                });
            }

        } else {
            results.add(returnError("No data found for the given criteria "));
        }


        return results;

    }

    public static List<Map<String, ? extends Object>> iamstaging(
            ARServerUser arServerUser,
            String userguid,
            String client


    ) throws Exception {

        List<Map<String, ? extends Object>> results = new ArrayList<>();

        Map<String, String> status = new HashMap<>();
        status.put("0", "New");
        status.put("1", "Cancelled");
        status.put("2", "Error");
        status.put("3", "Complete");
        Map<String, String> action = new HashMap<>();
        action.put("0", "New");
        action.put("1", "Remove");
        action.put("2", "Remove All");


        int[] rf = new int[]{
                536870914,//Application Login ID
                200000012,//Client
                536870932,//IAM Action
                536870933,//IAM Application
                6,//Modified Date
                536870918,//Notes
                7,//Status
                536870919, //Status Results
                1    //Request ID
        };


        String query1 = "('User GUID' = \"" + userguid + "\" )";


        List<Entry> entries1 = RemedyBase.queryEntrysByQual(
                arServerUser,
                "IAM:StagingTable",
                rf,
                query1
        );

        if (entries1 != null) {
            entries1.forEach(entry1 -> {
                Map<String, String> sEntry = new HashMap<>();
                sEntry.put("Application_Login_ID", entry1.get(rf[0]).toString());
                sEntry.put("Client", entry1.get(rf[1]).toString());
                sEntry.put("IAM_Action", action.get(entry1.get(rf[2]).toString()));
                sEntry.put("IAM_Application", entry1.get(rf[3]).toString());
                if (entry1.get(rf[4]) != null && entry1.get(rf[4]).toString() != null && entry1.get(rf[4]).toString().startsWith("[Timestamp=")) {
                    sEntry.put("Modified_Date", RemedyBase.formatEpocDate(entry1.get(rf[4]).toString().substring(entry1.get(rf[4]).toString().indexOf("=") + 1, entry1.get(rf[4]).toString().indexOf("]"))).toString());
                } else {
                    sEntry.put("Modified_Date", entry1.get(rf[4]).toString());
                }
                sEntry.put("Notes", entry1.get(rf[5]).toString());
                sEntry.put("Status", status.get(entry1.get(rf[6]).toString()));
                sEntry.put("Status_Results", entry1.get(rf[7]).toString());
                sEntry.put("Request_Id", entry1.get(rf[8]).toString());
                results.add(sEntry);
            });
        } else {
            results.add(returnError("No data found for the given criteria "));
        }


        return results;

    }


}
