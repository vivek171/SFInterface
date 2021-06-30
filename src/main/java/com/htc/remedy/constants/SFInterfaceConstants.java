package com.htc.remedy.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SFInterfaceConstants {

    public static final String ASSIGNED_TICKET_COUNT = "AssignedTicketCount";
    public static final String RESOLVER = "Resolver";
    public static final String LAST_TICKET_ASSIGNED_ON = "LastTicketAssignedOn";
    public static final String CONTROLSK = "Control_sk";
    public static String fileserverURL;

    public static final String CONTROL_CODE = "ControlCode";
    public static final String CONTROL_TYPE = "ControlType";
    public static final String JSON_PROPERTIES = "JSONProperties";
    public static final String BACK_UP_APPROVER_SK = "BackUpApproverSk";
    public static final String KBPHRASES_SK = "KBPhrasesSk";
    public static final String PHRASES = "Phrases";
    public static final String AGENT_SPECIFIC_PHRASE = "AgentSpecificPhrase";
    public static final String FETCH_OBJECTS_PATH = "/{version}/Objects1";
    public static final String Create_Update_RDYKB_Phrases_PATH = "/{version}/UpdateRDYKBPhrases";
    public static final String Create_Update_RDYKBPhraseDetails_PATH = "/{version}/UpdateRDYKBPhraseDetails";
    public static final String Create_Update_FDNBackUp_Approvers_PATH = "/{version}/UpdateFDNBackUpApprovers";
    public static final String Create_Update_FDN_Controls_PATH = "/{version}/UpdateFDNControls";
    public static final String Create_Update_FDN_Support_Users_PATH = "/{version}/UpdateFDNSupportUsers";
    public static final String Create_Update_RDYKB_Phrases = "UpdateARDYKBPhrases";
    public static final String Create_Update_FDNBackUp_Approvers = "UpdateFDNBackUpApprovers";
    public static final String Create_Update_RDYKBPhraseDetails = "UpdateRDYKBPhraseDetails";
    public static final String Create_Update_FDN_Controls = "UpdateFDNControls";
    public static final String Create_Update_FDN_Support_Users = "UpdateFDNSupportUsers";
    public static final String UPDATE_CMN_CATEGORIZATION = "UpdateDMartCMNCategorization";
    public static final String UPDATE_CMN_CATEGORIZATION_PATH = "/{version}/UpdateDMartCMNCategorization";
    public static final String CREATE_CMN_CATEGORIZATION = "CreateDMartCMNCategorization";
    public static final String CREATE_CMN_CATEGORIZATION_PATH = "/{version}/CreateDMartCMNCategorization";
    public static final String UPDATE_IAM_APPLICATION_PATH = "/{version}/UpdateIAMApplication";
    public static final String UPDATE_WORKNOTES_TYPE_PATH = "/{version}/UpdateWorkNotesType";
    public static final String UPDATE_EMAIL_OPTIONS_PATH = "/{version}/UpdateEMailOptions";
    public static final String UPDATE_CASE_TYPE_PATH = "/{version}/UpdateCaseType";
    public static final String UPDATE_BOOKMARKS_PATH = "/{version}/UpdateBookmarks";
    public static final String UPDATE_APPROVAL_CRITERIA_PATH = "/{version}/UpdateApprovalCriteria";
    public static final String UPDATE_IMPACT_PATH = "/{version}/UpdateImpact";
    public static final String UPDATE_SUPPORT1_PATH = "/{version}/UpdateSupport1";
    public static final String UPDATE_SUPPORT_ATTACHMENTS_PATH = "/{version}/UpdateSupportAttachments";
    public static final String UPDATE_TASK_DETAILS_PATH = "/{version}/UpdateTaskDetails";
    public static final String UPDATE_USER_POSITION_PATH = "/{version}/UpdateUserPosition";
    public static final String UPDATE_RDY_WIZARD_PATH = "/{version}/UpdateRDYWizard";
    public static final String UPDATE_ROUND_ROBIN_RULE_PATH = "/{version}/UpdateRoundRobinRule";
    public static final String UPDATE_AUTHENTICATION_TYPE_PATH = "/{version}/UpdateAuthenticationType";
    public static final String UPDATE_MASTER_TYPE_PATH = "/{version}/UpdateMasterType";
    public static final String UPDATE_ALERT_STATUS_PATH = "/{version}/UpdateAlertStatus";
    public static final String UPDATE_USER_TYPE_PATH = "/{version}/UpdateUserType";
    public static final String UPDATE_SEVERITY_PATH = "/{version}/UpdateSeverity";
    public static final String UPDATE_TASK_ASSIGNMENT_GROUP_PATH = "/{version}/UpdateTaskAssignmentGroup";
    public static final String UPDATE_TITLE_PATH = "/{version}/UpdateTitle";
    public static final String UPDATE_USER_HIERARCHY_PATH = "/{version}/UpdateUserHierarchy";
    public static final String UPDATE_USER_NOTES_VISIBILITY_PATH = "/{version}/UpdateUserNotesVisibility";
    public static final String UPDATE_USER_SKILL_SETS_PATH = "/{version}/UpdateUserSkillsets";
    public static final String UPDATE_INSTRUCTIONS_PATH = "/{version}/UpdateInstructions";
    public static final String UPDATE_EMAIL_DOMAINS_PATH = "/{version}/UpdateEMailDomains";
    public static final String UPDATE_SKILL_SET_PATH = "/{version}/UpdateSkillset";
    public static final String UPDATE_AUTOMATION_SERVICES_PATH = "/{version}/UpdateAutomationServices";
    public static final String UPDATE_ACCOUNT_TYPE_PATH = "/{version}/UpdateAccountType";
    public static final String UPDATE_ESCLATIONS_PATH = "/{version}/UpdateEscalations";
    public static final String UPDATE_UNITS_PATH = "/{version}/UpdateUnits";
    public static final String UPDATE_NOTIFICATIONS_PATH = "/{version}/UpdateNotifications";
    public static final String UPDATE_NOTIFICATION_MAPPING_PATH = "/{version}/UpdateNotificationMapping";
    public static final String REQUESTED_ID = "RequestedId";
    public static final String Z_APPLICATION = "ZApplication";
    public static final String ASSOCIATED_REQUEST = "AssociatedRequest";
    public static final String REMEDY_MESSAGE = "RemedyMessage";
    public static final String QUESTIONS_INFORMATION = "QuestionsInformation";

    public static final String COLOR_R = "ColorR";
    public static final String COLOR_G = "ColorG";
    public static final String COLOR_B = "ColorB";
    public static final String COLOR = "Color";
    public static final String ASSIGNEE_GROUP = "AssigneeGroup";
    public static final String SUB_TYPE = "SubType";
    public static final String DST = "DST";
    public static final String ALIAS = "Alias";
    public static final String URL_KEY = "URLKey";
    public static final String TIME_ZONE_OFFSET = "TimeZoneOffset";
    public static final String GET_METHOD = "GET";
    public static final String BOTH_METHOD = "BOTH";
    public static final String POST_METHOD = "POST";
    public static final String VERSION = "version";
    public static final String V1 = "V1";
    public static final String ENDPOINT_NAME = "endpointname";
    public static final String SLASH_SYMBOL = "/";
    public static final String SINGLE_PIPE = "|";
    public static final String OPEN_BRACE = "{";
    public static final String CLOSE_BRACE = "}";
    public static final String HYPHEN = "-";
    public static final String A_TOKEN = "atoken";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String DEFAULT_JSON = "DefaultJson";
    public static final String AUTH_TOKEN = "AuthToken";
    public static final String ATOKEN = "AToken";
    public static final String ACCOUNT = "Account";
    public static final String ACCOUNTS = "Accounts";
    public static final String QUERY_TYPE = "QueryType";
    public static final String INDEX_NAME = "IndexName";
    public static final String QUERY = "Query";
    public static final String CR_Tasks = "CRTasks";
    public static final String CR_RISKS = "CRRisks";
    public static final String CR_IMPACTED = "CRImpacted";
    public static final String PUBLISHED = "Published";
    public static final String PUBLISH = "Publish";
    public static final String CR_NUMBER = "CRNumber";
    public static final String VOID = "Void";

    public static final String USER_SK = "User_sk";
    public static final String SRNUMBER = "SRNumber";
    public static final String TASKID = "TaskID";
    public static final String LOGIN_ID = "LoginID";
    public static final String FIRSTNAME = "FirstName";
    public static final String LASTNAME = "LastName";
    public static final String NAME = "Name";
    public static final String VIP = "VIP";
    public static final String USERTYPE_SK = "UserType_sk";
    public static final String PROBLEM_MANAGER_FULL_NAME = "ProblemManagerFullName";
    public static final String PROBLEM_MANAGER_LOGIN_ID = "ProblemManagerLoginID";
    public static final String PROBLEM_MANAGER_REFID_3 = "ProblemManagerRefID3";
    public static final String INCIDENT_MANAGER_REFID_1 = "IncidentManagerRefID1";
    public static final String INCIDENT_MANAGER_REFID_2 = "IncidentManagerRefID2";
    public static final String INCIDENT_MANAGER_REFID_3 = "IncidentManagerRefID3";
    public static final String PROBLEM_MANAGER_NETWORK_LOGIN_ID = "ProblemManagerNetworkLoginID";
    public static final String INCIDENT_MANAGER_NAME = "IncidentManagerName";
    public static final String PAGINATION = "Pagination";
    public static final String TOTAL_COUNT = "TotalCount";
    public static final String PAGE_NUM = "PageNum";
    public static final String TOTAL_PAGES = "TotalPages";
    public static final String RECORD_COUNT = "RecordCount";
    public static final String DOCUMENT = "Document";

    public static final String NETWORK_LOGIN = "NetworkLogin";
    public static final String SUBMITTED_BY = "SubmittedBy";
    public static final String SUBMITTER_NAME = "SubmitterName";
    public static final String DATE_TIME_CREATED = "DateTimeCreated";
    public static final String DATE_TIME_ASSIGNED = "DateTimeAssigned";
    public static final String DATE_TIME_WORK_IN_PROGRESS = "DateTimeWorkInProgress";
    public static final String FIRST_ACKNOWLEDGE = "FirstAcknowledge";
    public static final String BUSINESS_ORGANIZATION = "BusinessOrganization";
    public static final String Z_SUBMITTER_GROUP = "zSubmitterGroup";
    public static final String WORK_LOG = "WorkLog";
    public static final String AUDIT_LOG = "AuditLog";
    public static final String REQUESTER_LOGIN = "RequesterLogin";
    public static final String LOCATION_OF_CHANGE = "LocationOfChange";
    public static final String REASON_FOR_CHANGE = "ReasonForChange";
    public static final String REFERENCE = "Reference";
    public static final String CRT_NUMBER = "CRTNumber";
    public static final String PARENT_CRT = "ParentCRT";
    public static final String TASK_DETAIL = "TaskDetail";
    public static final String CLIENT_VENDOR = "ClientVendor";
    public static final String APPROVAL_CRITERIA = "ApprovalCriteria";
    public static final String CLIENT_USER_IMPACT = "ClientUserImpact";
    public static final String EXTERNAL_ACCOUNTCODE = "ExternalAccountcode";
    public static final String EFFECTED_ACCOUNTCODE = "EffectedAccountcode";
    public static final String EFFECTED_LOCATION = "EffectedLocation";
    public static final String REQUESTER_GROUP_NAME = "RequesterGroupName";
    public static final String REQUESTER_NAME = "RequesterName";
    public static final String IS_CAB_MEMBER = "IsCabMember";
    public static final String REVIEWER_COMMENTS = "ReviewerComments";
    public static final String APPROVER_LOGIN = "ApproverLogin";
    public static final String APPROVER_NAME = "ApproverName";
    public static final String APPROVALS_NEEDED = "ApprovalsNeeded";
    public static final String IMPLEMENTATION_RESULTS = "ImplementationResults";
    public static final String RESULT_DETAILS = "ResultDetails";
    public static final String STATUS_OF_CHANGE = "StatusOfChange";
    public static final String ATTACHMENT_FIELD1 = "AttachmentField1";
    public static final String ATTACHMENT_FIELD = "AttachmentField";
    public static final String ATTACHMENT_FIELD2 = "AttachmentField2";
    public static final String ATTACHMENT_FIELD3 = "AttachmentField3";
    public static final String ATTACHMENT_FIELD4 = "AttachmentField4";
    public static final String ATTACHMENT_FIELD5 = "AttachmentField5";
    public static final String CHANGE_PROJECT_NAME = "ChangeProjectName";
    public static final String CLIENT_CHANGE_NAME = "ClientChangeName";
    public static final String CHANGE_OWNER_NAME = "ChangeOwnerName";
    public static final String DOWNTIME = "Downtime";
    public static final String EST_BACKOUT_TIME = "EstBackoutTime";
    public static final String ACTUAL_DOWN_TIME = "ActualDownTime";
    public static final String LEAD_DAYS = "LeadDays";
    public static final String APPROVAL_REQUESTED_ON = "ApprovalRequestedOn";
    public static final String ACTUAL_INSTALL_DATE = "ActualInstallDate";
    public static final String TASK_START_DATE = "TaskStartDate";
    public static final String TASK_END_DATE = "TaskEndDate";
    public static final String EST_CRITICAL_USERDOWNTIME = "EstCriticalUserDownTime";
    public static final String APPROVED_BY = "ApprovedBy";
    public static final String APPROVED_DATE_TIME = "ApprovedDateTime";
    public static final String REQUEST_COUNT = "RequestCount";
    public static final String EMAIL_SUBJECT = "EmailSubject";
    public static final String TYPE_OF_CHANGE = "TypeOfChange";
    public static final String WORKLOG_UPDATE = "WorkLogUpdate";
    public static final String ADEQUATE_DETAILS_NO = "AdequateDetailsNo";
    public static final String ADEQUATE_DETAILS = "AdequateDetails";
    public static final String VERIFIED_INFORMATION = "VerifiedInformation";
    public static final String NO_VERIFY_REASON = "NoVerifyReason";
    public static final String DATE_TIME_PENDING_ON = "DateTimePendingOn";
    public static final String DATE_TIME_PENDING_OFF = "DateTimePendingOff";
    public static final String SOLUTION_DOCUMENT_ID = "SolutionDocumentID";
    public static final String VENDOR_REF = "VendorRef";
    public static final String Z_APPROVAL_PERSON_GROUP = "zApprovalPersonGroup";
    public static final String Z_APPROVAL_STATUS = "zApprovalStatus";
    public static final String PARENT_REQUEST_ID = "ParentRequestID";
    public static final String IMPACTED_COMMUNITY = "ImpactedCommunity";
    public static final String ACTUAL_START_DATE = "ActualStartDate";
    public static final String ACTUAL_END_DATE = "ActualEndDate";
    public static final String CREATOR = "Creator";
    public static final String CREATE_DATE = "CreateDate";
    public static final String TASK = "Task";
    public static final String APPROVAL = "Approval";
    public static final String RECORD_ENTRY_ID = "RecordEntryID";
    public static final String REQ_LOGIN_ID = "ReqLoginID";
    public static final String CST = "CST";
    public static final String APPLICATION = "Application";
    public static final String APPLICATION_sk = "Application_sk";
    public static final String APPLICATION_LOGIN_ID = "ApplicationLoginID";
    public static final String APPLICATION_TEMP_PASSWORD = "ApplicationTempPassword";
    public static final String ACC_APPLICATION_sk = "AccApplication_sk";
    public static final String PARENT_APPLICATION_sk = "ParentApplication_sk";
    public static final String ACCOUNT_GROUP_APP_SK = "AccGroupApp_sk";
    public static final String CREATED_DATE = "CreatedDate";
    public static final String STATUS_DISPLAY = "StatusDisplay";
    public static final String MASTER_ACCOUNT = "MasterAccount";
    public static final String MAPPING_TYPE = "MappingType";
    public static final String MAPPING_TYPE_VALUE_GS = "GS";
    public static final String GROUP_MASTER_ACCOUNT = "GroupMasterAccount";
    public static final String GROUP_ACCOUNT = "GroupAccount";
    public static final String MASTER_ACCOUNT_CODE = "MasterAccountCode";
    public static final String MASTER_ACCOUNT_SK = "MasterAccount_sk";
    public static final String ACCOUNT_SK = "Account_sk";
    public static final String LINK_NAME = "LinkName";
    public static final String ACC_ROLE_USER_SK = "AccRoleUser_sk";
    public static final String ACC_ROLE_USER_CODE = "AccRoleUserCode";
    public static final String GROUP_LEVEL_SK = "GroupLevel_sk";
    public static final String AUTHTYPE_SK = "AuthType_sk";
    public static final String PROBLEM_MANAGER_SK = "ProblemManager_sk";
    public static final String PROBLEM_MANAGER_CODE = "ProblemManagerCode";
    public static final String BUSINESS_FUNCTION_SK = "BusinessFunction_sk";
    public static final String ATTACHMENT_SK = "Attachment_sk";
    public static final String ATTACHMENT_CODE = "AttachmentCode";
    public static final String ATTACHMENT_NAME = "AttachmentName";
    public static final String PROCUREMENT_SK1 = "Procurement_sk";
    public static final String PROCUREMENT_CODE = "ProcurementCode";
    public static final String PROCUREMENT_DETAILS_SK1 = "ProcurementDetails_sk";
    public static final String PROCUREMENT_DETAILS_CODE = "ProcurementDetailsCode";
    public static final String CR_REQUEST_TYPE_SK = "CRRequestType_sk";
    public static final String CR_REQUEST_TYPE_CODE = "CRRRequestTypeCode";
    public static final String CR_REQUEST_TYPE = "CRRequestType";
    public static final String MILESTONE_SK = "Milestone_sk";
    public static final String MILESTONE_CODE = "MilestoneCode";
    public static final String MILESTONE = "Milestone";
    public static final String AGREEMENT_TYPE_SK = "AgreementType_sk";
    public static final String AGREEMENT_TYPE_CODE = "AgreementTypeCode";
    public static final String AGREEMENT_TYPE = "AgreementType";
    public static final String RDY_MAPPING_SK = "RDYMapping_sk";
    public static final String RDY_MAPPING_CODE = "RDYMappingCode";
    public static final String KB_PHRASES_SK = "KBPhrases_sk";
    public static final String ERROR = "Error";
    public static final String MESSAGE = "Message";
    public static final String ERRORCODE = "ErrorCode";
    public static final String INFOCODE = "InfoCode";
    public static final String MOREINFO = "MoreInfo";
    public static final String MOREINFO_URL = "https://sfdocs-s.caretechweb.com/";
    public static final String WF_VALIDATE_API_ERROR = "WF_ValidateAPI_Error";
    public static final String WF_INITIATE_API_ERROR = "WF_InitiateAPI_Error";
    public static final String WF_TICKETINFO_API_ERROR = "WF_TicketInfoAPI_Error";
    public static final String ERROR_MESSAGE = "Error Message";
    public static final String SUCCESS = "Success";
    public static final String RESULT_SET = "Resultset";
    public static final String EXCEPTION = "Exception";
    public static final String CREATE_INCIDENT = "CreateIncident";
    public static final String CREATE_SERVICE_REQUEST = "CreateServiceRequest";
    public static final String CREATE_SRTASK = "CreateServiceRequestTask";
    public static final String CREATE_INCIDENT_DETAILS = "CreateIncidentDetails";
    public static final String CREATE_SR_DETAILS = "CreateServiceRequestDetails";
    public static final String CREATE_SRTASK_DETAILS = "CreateSRTaskDetails";
    public static final String UPDATE_INCIDENT = "UpdateIncident";
    public static final String UPDATE_SERVICE_REQUEST = "UpdateServiceRequest";
    public static final String UPDATE_SRTASK = "UpdateServiceRequestTask";
    public static final String UPDATE_INCIDENT_DETAILS = "UpdateIncidentDetails";
    public static final String UPDATE_SR_DETAILS = "UpdateServiceRequestDetails";
    public static final String UPDATE_SRTASK_DETAILS = "UpdateSRTaskDetails";
    public static final String CREATE_VACATION = "CreateVacation";
    public static final String UPDATE_VACATION = "UpdateVacation";
    public static final String CREATE_DOCUMENT = "CreateDocument";
    public static final String UPDATE_DOCUMENT = "UpdateDocument";
    public static final String CREATE_SUPPORT = "CreateSupport";
    public static final String UPDATE_SUPPORT = "UpdateSupport";
    public static final String UPDATE_PROFILE = "UpdateProfile";
    public static final String UPDATE_INCIDENT_MANAGER = "UpdateIncidentManager";
    public static final String CREATE_INCIDENT_MANAGER = "CreateIncidentManager";
    public static final String CREATE_PROFILE = "CreateProfile";
    public static final String UPDATE_WATCHLIST = "UpdateWatchList";
    public static final String UPDATE_CR_USER_FAVORITIES = "UpdateCRUserFavorities";
    public static final String UPDATE_CR_USER_FAVORITIES_PATH = "/{version}/UpdateCRUserFavorities";
    public static final String CLONE_CR_PATH = "/{version}/CloneCR";
    public static final String UPDATE_BUSINESS_FUNCTION_PATH = "/{version}/UpdateBusinessFunction";
    public static final String UPDATE_SOURCE_PATH = "/{version}/UpdateSource";
    public static final String UPDATE_BUSINESS_TIME_DETAILS_PATH = "/{version}/UpdateBusinessTimeDetails";
    public static final String UPDATE_GROUP_TYPE_PATH = "/{version}/UpdateGroupType";
    public static final String UPDATE_QUEUE_PATH = "/{version}/UpdateQueue";
    public static final String UPDATE_RCL_MAPPING_PATH = "/{version}/UpdateRCLMapping";
    public static final String UPDATE_ROOT_CAUSE_LEVEL_PATH = "/{version}/UpdateRootCauseLevel";
    public static final String UPDATE_GROUP_LEVEL_PATH = "/{version}/UpdateGroupLevel";
    public static final String UPDATE_SEC_ACCOUNT_ROLE_USER_PATH = "/{version}/UpdateSECAccountRoleUser";
    public static final String UPDATE_STATUS_ENTITY_TYPE_PATH = "/{version}/UpdateStatusEntityType";
    public static final String UPDATE_STATUS_ENTITY_PATH = "/{version}/UpdateStatusEntity";
    public static final String UPDATE_TASK_PATH = "/{version}/UpdateTask";
    public static final String UPDATE_TASK_GROUP_PATH = "/{version}/UpdateTaskGroup";
    public static final String UPDATE_AUTO_CLOSE_PATH = "/{version}/UpdateAutoClose";
    public static final String UPDATE_LDAP_DETAILS_PATH = "/{version}/UpdateLDAPDetails";
    public static final String UPDATE_BLD_DEPT_PATH = "/{version}/UpdateBldDept";
    public static final String UPDATE_BLD_SUITE_PATH = "/{version}/UpdateBldSuite";
    public static final String UPDATE_DEPT_FLR_PATH = "/{version}/UpdateDeptFlr";
    public static final String UPDATE_BLD_FLR_PATH = "/{version}/UpdateBldFlr";
    public static final String UPDATE_DEPT_SUITE_PATH = "/{version}/UpdateDeptSuite";
    public static final String UPDATE_BLD_CATEGORY_PATH = "/{version}/UpdateBldCategory";
    public static final String UPDATE_CR_LOCATIONS_PATH = "/{version}/UpdateCRLocations";
    public static final String UPDATE_ALERT_ACCOUNT_REL_PATH = "/{version}/UpdateAlertAccountRel";
    public static final String UPDATE_CONTACT_TYPE_PATH = "/{version}/UpdateContactType";
    public static final String UPDATE_OBJECTS_PATH = "/{version}/UpdateObjects";
    public static final String UPDATE_KB_TYPE_AHEAD_CATEGORIES_PATH = "/{version}/UpdateKBTypeAHeadCategories";
    public static final String UPDATE_GEO_CITY_PATH = "/{version}/UpdateGEOCity";
    public static final String UPDATE_GEO_COUNTRY_PATH = "/{version}/UpdateGEOCountry";
    public static final String UPDATE_GEO_STATE_PATH = "/{version}/UpdateGEOState";
    public static final String UPDATE_ASSET_CATEGORY_PATH = "/{version}/UpdateAssetCategory";
    public static final String UPDATE_ASSET_ATTACHMENT_PATH = "/{version}/UpdateAssetAttachment";
    public static final String UPDATE_ASSET_PROCUREMENT_PATH = "/{version}/UpdateAssetProcurement";
    public static final String UPDATE_ASSET_PROCUREMENT_DETAILS_PATH = "/{version}/UpdateAssetProcurementDetails";
    public static final String UPDATE_CR_CAB_MEMBER_PATH = "/{version}/UpdateCRCABMembers";
    public static final String UPDATE_CR_REQUEST_TYPE_PATH = "/{version}/UpdateCRRequestType";
    public static final String UPDATE_CR_MILESTONE_PATH = "/{version}/UpdateCRMileStone";
    public static final String UPDATE_CR_PROJECT_PATH = "/{version}/UpdateCRProject";
    public static final String UPDATE_SLM_AGREEMENT_TYPE_PATH = "/{version}/UpdateSLMAgreementType";
    public static final String UPDATE_OPSBOT_ALERTS_PATH = "/{version}/UpdateOPSBOTAlerts";
    public static final String UPDATE_SLM_DEFINITION_PATH = "/{version}/UpdateSLMDefinition";
    public static final String UPDATE_SLM_GOAL_TYPE_PATH = "/{version}/UpdateSLMGoalType";
    public static final String UPDATE_RDY_ATTACHMENTS_PATH = "/{version}/UpdateRDYAttachments";
    public static final String UPDATE_RDY_MAPPING_SLA_PATH = "/{version}/UpdateRDYMappingSLA";
    public static final String UPDATE_RDY_QUERIES_PATH = "/{version}/UpdateRDYQueries";
    public static final String UPDATE_RDY_ROOTCAUSE_PATH = "/{version}/UpdateRDYRootCause";

    public static final String UPDATE_RDY_MAPPING_PATH = "/{version}/UpdateRDYMapping";
    public static final String UPDATE_KB_ARTICLE_TYPE_PATH = "/{version}/UpdateKBArticleType";
    public static final String UPDATE_KB_CONTENT_TYPE_PATH = "/{version}/UpdateKBContentType";
    public static final String UPDATE_KB_DOC_CONTENT_TYPE_PATH = "/{version}/UpdateKBDocContentType";
    public static final String UPDATE_KB_DOCUMENT_TYPE_PATH = "/{version}/UpdateKBDocumentType";
    public static final String UPDATE_OBJECT_PERMISSIONS_PATH = "/{version}/UpdateObjectPermissions";
    public static final String UPDATE_ENTITIES_PATH = "/{version}/UpdateEntities";
    public static final String UPDATE_ENTITY_LINKS_PATH = "/{version}/UpdateEntityLinks";
    public static final String FETCH_ENTITY_LINKS_PATH = "/{version}/EntityLinks";
    public static final String UPDATE_ENTITY_LINKS = "UpdateEntityLinks";
    public static final String FETCH_ENTITY_LINKS = "EntityLinks";
    public static final String UPDATETITLE = "UpdateTitle";
    public static final String UPDATESkillSET = "UpdateSkillSet";
    public static final String UPDATE_ENTITY_PATH = "/{version}/UpdateEntity";
    public static final String UPDATE_Title_PATH = "/{version}/UpdateTitle";
    public static final String UPDATE_SkillSet_PATH = "/{version}/UpdateSkillSet";
    public static final String UPDATE_ENTITY_LINK_PATH = "/{version}/UpdateEntityLink";
    public static final String UPDATE_LINKS_PATH = "/{version}/UpdateLinks";
    public static final String ADMIN_SCREEN_MENUS = "/{version}/Menus";
    public static final String CREATE_DMART_ACCOUNT_PATH = "/{version}/CreateDMartAccount";
    public static final String UPDATE_DMART_ACCOUNT_PATH = "/{version}/UpdateDMartAccount";
    public static final String CREATE_FDN_PEOPLE_INFO_PATH = "/{version}/CreateDMartPeopleInfo";
    public static final String UPDATE_FDN_PEOPLE_INFO_PATH = "/{version}/UpdateDMartPeopleInfo";
    public static final String CREATE_DMART_GROUP_PATH = "/{version}/CreateDMartGroup";
    public static final String UPDATE_DMART_GROUP_PATH = "/{version}/UpdateDMartGroup";
    public static final String CREATE_DMART_USER_PATH = "/{version}/CreateDMartUser";
    public static final String UPDATE_DMART_USER_PATH = "/{version}/UpdateDMartUser";
    public static final String CREATE_DMART_GRP_OPTIONS_PATH = "/{version}/CreateDMartGrpOptions";
    public static final String UPDATE_DMART_GRP_OPTIONS_PATH = "/{version}/UpdateDMartGrpOptions";
    public static final String CREATE_DMART_CHANGE_REQUEST_PATH = "/{version}/CreateDMartChangeRequest";
    public static final String CREATE_DMART_CR_CONSOLIDATED_PATH = "/{version}/CreateDMartCRConsolidated";
    public static final String UPDATE_DMART_CR_CONSOLIDATED_PATH = "/{version}/UpdateDMartCRConsolidated";
    public static final String UPDATE_DMART_CHANGE_REQUEST_PATH = "/{version}/UpdateDMartChangeRequest";
    public static final String CREATE_DMART_CRTASK_PATH = "/{version}/CreateDMartCRTask";
    public static final String UPDATE_DMART_CRTASK_PATH = "/{version}/UpdateDMartCRTask";

    public static final String CREATE_WATCHLIST_REPORT = "CreateWatchListReporting";
    public static final String UPDATE_PROFILE_PREFRENCE = "UpdateProfilePreference";
    public static final String UPDATE_USER_PREFRENCE = "UpdateUserPreference";
    public static final String UPDATE_ACCOUNTGROUPUSER = "UpdateAccountGroupUser";
    public static final String UPDATE_SECURE_ANSWERS1 = "UpdateSecureAnswers1";
    public static final String UPDATE_BUILDING = "UpdateBuilding";
    public static final String UPDATE_DEPARTMENT = "UpdateDepartment";
    public static final String UPDATE_FLOOR = "UpdateFloor";
    public static final String UPDATE_SUITE = "UpdateSuite";
    public static final String UPDATE_ACCOUNT = "UpdateAccount";
    public static final String UPDATE_GROUP = "UpdateGroup";
    public static final String UPDATE_ACCOUNT_GROUP = "UpdateAccountGroup";
    public static final String UPDATE_CATEGORY = "UpdateCategory";
    public static final String UPDATE_APPROVERS = "UpdateApprovers";
    public static final String UPDATE_CR_DISCUSSION_BOARD = "UpdateCRDiscussionBoard";
    public static final String CREATE_CHANGE_REQUEST = "CreateChangeRequest";
    public static final String UPDATE_CHANGE_REQUEST = "UpdateChangeRequest";
    public static final String CREATE_CHANGE_REQUEST_TASKS = "CreateChangeRequestTasks";
    public static final String UPDATE_CHANGE_REQUEST_TASKS = "UpdateChangeRequestTasks";
    public static final String CREATE_CHANGE_REQUEST_DETAILS = "CreateChangeRequestDetails";
    public static final String UPDATE_CHANGE_REQUEST_DETAILS = "UpdateChangeRequestDetails";
    public static final String CREATE_CR_SCHEDULES = "CreateCRSchedules";
    public static final String UPDATE_CR_SCHEDULES = "UpdateCRSchedules";
    public static final String CREATE_CR_CLOSURE = "CreateCRClosure";
    public static final String UPDATE_CR_CLOSURE = "UpdateCRClosure";
    public static final String UPDATE_USERNOTES = "UpdateUserNotes";
    public static final String UPDATE_SECURE_INFO_HISTORY = "UpdateSecureInfoViewHistory";
    public static final String UPDATE_SECUREINFO_DOWNLADED_HISTORY = "UpdateSecureInfoDownloadHistory";
    public static final String UPDATE_INC_EMAIL = "UpdateINCEmail";
    public static final String UPDATE_SR_EMAIL = "UpdateSREmail";
    public static final String UPDATE_SRTASK_EMAIL = "UpdateSRTaskEmail";
    public static final String UPDATE_DMART_INC = "UpdateDMartINC";
    public static final String UPDATE_DMART_SR = "UpdateDMartSR";
    public static final String UPDATE_DMART_SRTASK = "UpdateDMartSRTask";
    public static final String CREATE_DMART_INC = "CreateDMartINC";
    public static final String CREATE_DMART_SR = "CreateDMartSR";
    public static final String CREATE_DMART_SRTASK = "CreateDMartSRTask";
    public static final String CREATE_DMART_CONSOLIDATED = "CreateDMartConsolidated";
    public static final String UPDATE_DMART_CONSOLIDATED = "UpdateDMartConsolidated";
    public static final String UPDATE_SECURE_ANSWERS = "UpdateSecureAnswers";
    public static final String CREATE_COM_RESPONSES = "CreateComResponses";
    public static final String UPDATE_COM_RESPONSES = "UpdateComResponses";
    public static final String UPDATE_APPLICATION = "UpdateApplication";
    public static final String UPDATE_ACC_APPLICATION = "UpdateAccApplication";
    public static final String UPDATE_ACTION_ITEMS = "UpdateActionItems";
    public static final String UPDATE_IMPACTED = "UpdateImpacted";
    public static final String UPDATE_CR_RISKS = "UpdateCRRisks";
    public static final String CREATE_CR_APPROVALS = "CreateCRApprovals";
    public static final String UPDATE_CR_APPROVALS = "UpdateCRApprovals";
    public static final String UPDATE_ACC_GROUP_APP = "UpdateAccGroupApp";
    public static final String CREATE_KBARTICLET_LOG = "CreateKBArticleLog";
    public static final String UPDATE_KBARTICLET_LOG = "UpdateKBArticleLog";
    public static final String UPDATE_RDY_QUERIES = "UpdateRDYQueries";
    public static final String UPDATE_IAM_APPLICATION = "UpdateIAMApplication";
    public static final String UPDATE_WORKNOTES_TYPE = "UpdateWorkNotesType";
    public static final String UPDATE_EMAIL_OPTIONS = "UpdateEMailOptions";
    public static final String UPDATE_CASE_TYPE = "UpdateCaseType";
    public static final String UPDATE_BOOKMARKS = "UpdateBookmarks";
    public static final String UPDATE_APPROVAL_CRITERIA = "UpdateApprovalCriteria";
    public static final String UPDATE_IMPACT = "UpdateImpact";
    public static final String UPDATE_SUPPORT1 = "UpdateSupport1";
    public static final String UPDATE_SUPPORT_ATTACHMENTS = "UpdateSupportAttachments";
    public static final String UPDATE_TASK_DETAILS = "UpdateTaskDetails";
    public static final String UPDATE_USER_POSITION = "UpdateUserPosition";
    public static final String UPDATE_RDY_WIZARD = "UpdateRDYWizard";
    public static final String UPDATE_ROUND_ROBIN_RULE = "UpdateRoundRobinRule";
    public static final String UPDATE_AUTHENTICATION_TYPE = "UpdateAuthenticationType";
    public static final String UPDATE_MASTER_TYPE = "UpdateMasterType";
    public static final String UPDATE_ALERT_STATUS = "UpdateAlertStatus";
    public static final String UPDATE_USER_TYPE = "UpdateUserType";
    public static final String UPDATE_SEVERITY = "UpdateSeverity";
    public static final String UPDATE_TASK_ASSIGNMENT_GROUP = "UpdateTaskAssignmentGroup";
    public static final String UPDATE_TITLE = "UpdateTitle";
    public static final String UPDATE_USER_HIERARCHY = "UpdateUserHierarchy";
    public static final String UPDATE_USER_NOTES_VISIBILITY = "UpdateUserNotesVisibility";
    public static final String UPDATE_USER_SKILL_SETS = "UpdateUserSkillsets";
    public static final String UPDATE_INSTRUCTIONS = "UpdateInstructions";
    public static final String UPDATE_EMAIL_DOMAINS = "UpdateEmailDomains";
    public static final String UPDATE_SKILL_SET = "UpdateSkillset";
    public static final String UPDATE_AUTOMATION_SERVICES = "UpdateAutomationServices";
    public static final String UPDATE_ACCOUNT_TYPE = "UpdateAccountType";
    public static final String UPDATE_ESCLATIONS = "UpdateEscalations";
    public static final String UPDATE_UNITS = "UpdateUnits";
    public static final String UPDATE_NOTIFICATIONS = "UpdateNotifications";
    public static final String UPDATE_NOTIFICATION_MAPPING = "UpdateNotificationMapping";
    public static final String UPDATE_RDY_ROOTCAUSE = "UpdateRDYRootCause";
    public static final String UPDATE_KB_TICKET_MAPPING = "UpdateKBTicketMapping";
    public static final String UPDATE_HANGUPS = "UpdateHangUps";
    public static final String SCHEDULE_DETAILS = "ScheduleDetails";
    public static final String RISK_DETAILS = "RiskDetails";
    public static final String KB_PHRASE_DETAILS = "KBPhraseDetails";
    public static final String KBPhraseDetails_sk = "KBPhraseDetails_sk";
    public static final String CREATE_HANGUPS_REPORTING = "CreateHangupsReporting";
    public static final String CREATE_KBARTICLE = "CreateKBArticle";
    public static final String UPDATE_KBARTICLE = "UpdateKBArticle";
    public static final String USER_PREFERENCES = "userpreferences";
    public static final String ACC_GROUP_USER = "accgroupuser";
    public static final String SECURE_ANSWERS = "SecureAnswers";
    public static final String CLOSURE_DETAILS = "ClosureDetails";
    public static final String IS_BACKUP_NEEDED = "IsBackUpNeeded";
    public static final String OPERATOR = "Operator";
    public static final String APPROVER_COUNT = "ApproverCount";
    public static final String CR_DISCUSSION_BOARD = "CRDiscussionBoard";
    public static final String CR_DB_RECIPIENTS = "CRDBRecipients";
    public static final String BUILDING_DEPARTMENT = "BuidingDepartment";
    public static final String BUILDING_SUITE = "BuidingSuite";
    public static final String DEPARTMENT_FLOOR = "DepartmentFloor";
    public static final String BUILDING_FLOOR = "BuildingFloor";
    public static final String DEPARTMENT_SUITE = "DepartmentSuite";
    public static final String BUILDING_CATEGORY = "BuildingCategory";
    public static final String ENTITY = "Entity";
    public static final String ENTITY_LINK = "EntityLink";
    public static final String LINKS = "Links";
    public static final String CLONE_CR = "CloneCR";
    public static final String UPDATE_BUSINESS_FUNCTION = "UpdateBusinessFunction";
    public static final String UPDATE_SOURCE = "UpdateSource";
    public static final String UPDATE_BUSINESS_TIME_DETAILS = "UpdateBusinessTimeDetails";
    public static final String UPDATE_GROUP_TYPE = "UpdateGroupType";
    public static final String UPDATE_QUEUE = "UpdateQueue";
    public static final String UPDATE_RCL_MAPPING = "UpdateRCLMapping";
    public static final String UPDATE_ROOT_CAUSE_LEVEL = "UpdateRootCauseLevel";
    public static final String UPDATE_GROUP_LEVEL = "UpdateGroupLevel";
    public static final String UPDATE_SEC_ACCOUNT_ROLE_USER = "UpdateSECAccountRoleUser";
    public static final String UPDATE_STATUS_ENTITY = "UpdateStatusEntity";
    public static final String UPDATE_STATUS_ENTITY_TYPE = "UpdateStatusEntityType";
    public static final String UPDATE_TASK = "UpdateTask";
    public static final String UPDATE_TASK_GROUP = "UpdateTaskGroup";
    public static final String UPDATE_AUTO_CLOSE = "UpdateAutoClose";
    public static final String UPDATE_LDAP_DETAILS = "UpdateLDAPDetails";
    public static final String UPDATE_BLD_DEPT = "UpdateBldDept";
    public static final String UPDATE_BLD_SUITE = "UpdateBldSuite";
    public static final String UPDATE_DEPT_FLR = "UpdateDeptFlr";
    public static final String UPDATE_BLD_FLR = "UpdateBldFlr";
    public static final String UPDATE_DEPT_SUITE = "UpdateDeptSuite";
    public static final String UPDATE_BLD_CATEGORY = "UpdateBldCategory";
    public static final String UPDATE_CR_LOCATIONS = "UpdateCRLocations";
    public static final String UPDATE_ALERT_ACCOUNT_REL = "UpdateAlertAccountRel";
    public static final String UPDATE_CONTACT_TYPE = "UpdateContactType";
    public static final String UPDATE_OBJECTS = "UpdateObjects";
    public static final String UPDATE_KB_TYPE_AHEAD_CATEGORIES = "UpdateKBTypeAHeadCategories";
    public static final String UPDATE_GEO_CITY = "UpdateGEOCity";
    public static final String UPDATE_GEO_COUNTRY = "UpdateGEOCountry";
    public static final String UPDATE_GEO_STATE = "UpdateGEOState";
    public static final String UPDATE_ENTITY = "UpdateEntity";
    public static final String UPDATE_ENTITY_LINK = "UpdateEntityLink";
    public static final String UPDATE_LINKS = "UpdateLinks";
    public static final String UPDATE_ASSET_CATEGORY = "UpdateAssetCategory";
    public static final String UPDATE_ASSET_ATTACHMENT = "UpdateAssetAttachment";
    public static final String UPDATE_ASSET_PROCUREMENT = "UpdateAssetProcurement";
    public static final String UPDATE_ASSET_PROCUREMENT_DETAILS = "UpdateAssetProcurementDetails";
    public static final String UPDATE_CR_CAB_MEMBER = "UpdateCRCABMembers";
    public static final String UPDATE_CR_REQUEST_TYPE = "UpdateCRRequestType";
    public static final String UPDATE_CR_MILESTONE = "UpdateCRMileStone";
    public static final String UPDATE_CR_PROJECT = "UpdateCRProject";
    public static final String UPDATE_SLM_AGREEMENT_TYPE = "UpdateSLMAgreementType";
    public static final String UPDATE_OPSBOT_ALERTS = "UpdateOPSBOTAlerts";
    public static final String UPDATE_SLM_DEFINTION = "UpdateSLMDefinition";
    public static final String UPDATE_SLM_GOALTYPE = "UpdateSLMGoalType";
    public static final String UPDATE_RDY_ATTACHMENTS = "UpdateRDYAttachments";
    public static final String UPDATE_RDY_MAPPING_SLA = "UpdateRDYMappingSLA";
    public static final String UPDATE_RDY_MAPPING = "UpdateRDYMapping";
    public static final String CREATE_DMART_ACCOUNT = "CreateDMartAccount";
    public static final String CREATE_DMART_CHANGE_REQUEST = "CreateDMartChangeRequest";
    public static final String CREATE_DMART_CR_CONSOLIDATED = "CreateDMartCRConsolidated";
    public static final String UPDATE_DMART_CR_CONSOLIDATED = "UpdateDMartCRConsolidated";
    public static final String UPDATE_DMART_CHANGE_REQUEST = "UpdateDMartChangeRequest";
    public static final String CREATE_DMART_CRTASK = "CreateDMartCRTask";
    public static final String UPDATE_DMART_CRTASK = "UpdateDMartCRTask";
    public static final String UPDATE_DMART_ACCOUNT = "UpdateDMartAccount";
    public static final String CREATE_FDN_PEOPLE_INFO = "CreateDMartPeopleInfo";
    public static final String UPDATE_FDN_PEOPLE_INFO = "UpdateDMartPeopleInfo";
    public static final String CREATE_DMART_GROUP = "CreateDMartGroup";
    public static final String UPDATE_DMART_GROUP = "UpdateDMartGroup";
    public static final String CREATE_DMART_USER = "CreateDMartUser";
    public static final String UPDATE_DMART_USER = "UpdateDMartUser";
    public static final String CREATE_DMART_GRP_OPTIONS = "CreateDMartGrpOptions";
    public static final String UPDATE_DMART_GRP_OPTIONS = "UpdateDMartGrpOptions";
    public static final String UPDATE_KB_ARTICLE_TYPE = "UpdateKBArticleType";
    public static final String UPDATE_KB_CONTENT_TYPE = "UpdateKBContentType";
    public static final String UPDATE_KB_DOC_CONTENT_TYPE = "UpdateKBDocContentType";
    public static final String UPDATE_KB_DOCUMENT_TYPE = "UpdateKBDocumentType";
    public static final String UPDATE_OBJECT_PERMISSIONS = "UpdateObjectPermissions";

    public static final String CAB_MEMBER_SK = "CABMember_sk";
    public static final String BUS_TIME_DETAIL_SK = "BusTimeDetail_sk";
    public static final String BUS_TIME_DETAIL_CODE = "BusTimeDetailCode";
    public static final String START_TIME = "StartTime";
    public static final String END_TIME = "EndTime";
    public static final String AVAILABILITY = "Availability";
    public static final String HOLIDAY_DATE = "HolidayDate";
    public static final String CLONED_CR = "ClonedCR";
    public static final int NUM_ZERO = 0;
    public static final String TAB = "\t";
    public static final int NUM_ONE = 1;
    public static final int NUM_TWO = 2;
    public static final int NUM_THREE = 3;
    public static final int NUM_FOUR = 4;
    public static final int NUM_FIVE = 5;
    public static final int MINUS_ONE = -1;
    public static final Integer ENDPOINT_DEFAULT_PAGENO = 1;
    public static final String OBJECT_TYPE = "ObjectType";
    public static final String OBJECT = "Object";
    public static final String DISPLAYNAME = "DisplayName";
    public static final String PARENT_OBJECT_CODE = "ParentObjectCode";
    public static final String IS_MANDATORY = "IsMandatory";
    public static final String ESTIMATED_EFFORT = "EstimatedEffort";
    public static final String ACTUAL_EFFORT = "ActualEffort";
    public static final String IS_DELETED = "IsDeleted";
    public static final String APPROVAL_NUMBER = "ApprovalNumber";
    public static final String PARENT_OBJECT_SK = "ParentObject_sk";
    public static final String OBJECT_SK = "Object_sk";
    public static final String OBJECT_PERMISSION_SK = "ObjectPermission_sk";
    public static final String OBJECT_URL = "ObjectURL";
    public static final String OBJECT_DESCRIPTION = "ObjectDescription";
    public static final String ACTIONITEM_SK = "ActionItem_sk";
    public static final String IMPACTED_SK = "Impacted_sk";
    public static final String APPROVAL_SK = "Approval_sk";
    public static final String APPROVALSTATUS_SK = "ApprovalStatus_sk";
    public static final String ARTICLELOG_SK = "ArticleLog_sk";
    public static final String KBARTICLE_LOG = "KBArticleLog";


    public static final String CREATE_DMART_KBARTICLE_LOG = "CreateDMartKBArticleLog";

    public static String DB_SSP_CONNECTION_NAME;
    public static String DB_REF_CONNECTION_NAME;
    public static String DB_REF_REPLICA_CONNECTION_NAME;
    public static String DB_REPORTING_CONNECTION_NAME;
    public static String DB_ASSET_CONNECTION_NAME;

    public static final String GET_PATH = "/{version}/{endpointname}";
    public static final String POST_PATH = "/{version}/{endpointname}";
    public static final String ADMIN_GET_PATH = "/{version}/Admin/{endpointname}";
    public static final String GENERATE_TICKET_NUMBER_PATH = "/{version}/GenerateTicketNumber";
    public static final String CREATE_INCIDENT_PATH = "/{version}/CreateIncident";
    public static final String CREATE_INCIDENT_DETAILS_PATH = "/{version}/CreateIncidentDetails";
    public static final String CREATE_SR_PATH = "/{version}/CreateServiceRequest";
    public static final String CREATE_SR_DETAILS_PATH = "/{version}/CreateServiceRequestDetails";
    public static final String CREATE_SRT_PATH = "/{version}/CreateServiceRequestTask";
    public static final String CREATE_SRT_DETAILS_PATH = "/{version}/CreateSRTaskDetails";
    public static final String UPDATE_INCIDENT_PATH = "/{version}/UpdateIncident";
    public static final String UPDATE_INCIDENT_DETAILS_PATH = "/{version}/UpdateIncidentDetails";
    public static final String UPDATE_SR_PATH = "/{version}/UpdateServiceRequest";
    public static final String UPDATE_SR_DETAILS_PATH = "/{version}/UpdateServiceRequestDetails";
    public static final String UPDATE_SRT_PATH = "/{version}/UpdateServiceRequestTask";
    public static final String UPDATE_SRT_DETAILS_PATH = "/{version}/UpdateSRTaskDetails";
    public static final String CREATE_VACATION_PATH = "/{version}/CreateVacation";
    public static final String UPDATE_VACATION_PATH = "/{version}/UpdateVacation";
    public static final String CREATE_DOCUMENT_PATH = "/{version}/CreateDocument";
    public static final String UPDATE_DOCUMENT_PATH = "/{version}/UpdateDocument";
    public static final String SCHEDULE_DETAILS_PATH = "/{version}/ScheduleDetails";
    public static final String RISK_DETAILS_PATH = "/{version}/RiskDetails";
    public static final String KB_PHRASE_DETAILS_PATH = "/{version}/KBPhraseDetails";
    public static final String CLOSURE_DETAILS_PATH = "/{version}/ClosureDetails";
    public static final String CREATE_SUPPORT_PATH = "/{version}/CreateSupport";
    public static final String UPDATE_SUPPORT_PATH = "/{version}/UpdateSupport";
    public static final String UPDATE_PROFILE_PATH = "/{version}/UpdateProfile";
    public static final String CREATE_INCIDENT_MANAGER_PATH = "/{version}/CreateIncidentManager";
    public static final String UPDATE_INCIDENT_MANAGER_PATH = "/{version}/UpdateIncidentManager";
    public static final String CREATE_PROFILE_PATH = "/{version}/CreateProfile";
    public static final String UPDATE_WATCHLIST_PATH = "/{version}/UpdateWatchList";
    public static final String UPDATE_PROFILE_PREFRENCE_PATH = "/{version}/UpdateProfilePreference";
    public static final String UPDATE_USER_PREFRENCE_PATH = "/{version}/UpdateUserPreference";
    public static final String UPDATE_ACCOUNTGROUPUSER_PATH = "/{version}/UpdateAccountGroupUser";
    public static final String UPDATE_USERNOTES_PATH = "/{version}/UpdateUserNotes";
    public static final String UPDATE_SECURE_INFO_VIEW_HISTORY_PATH = "/{version}/UpdateSecureInfoViewHistory";
    public static final String UPDATE_SECUREINFO_DOWNLADED_PATH = "/{version}/UpdateSecureInfoDownloadHistory";
    public static final String QUERY_PATH = "/{version}/Query";
    public static final String PROCUREMENT_PATH = "/{version}/Procurement";
    public static final String SUPPORTGROUPS_COUNT_PATH = "/{version}/SupportGroupsCount";
    public static final String PROFILE_SEARCH4_PATH = "/{version}/ProfileSearch4";
    public static final String CONSOLIDATED_TICKETS_PATH = "/{version}/ConsolidatedTickets";
    public static final String INCIDENT_DETAILS_PATH = "/{version}/IncidentDetails";
    public static final String SERVICE_REQUEST_DETAILS_PATH = "/{version}/ServiceRequestDetails";
    public static final String SRTASK_DETAILS_PATH = "/{version}/SRTaskDetails";
    public static final String CHANGE_REQUEST_DETAILS_PATH = "/{version}/ChangeRequestDetails";
    public static final String CR_TASK_DETAILS_PATH = "/{version}/CRTaskDetails";
    public static final String CR_APPROVAL_DETAILS_PATH = "/{version}/CRApprovalDetails";
    public static final String CR_PRINT_DETAILS_PATH = "/{version}/CRPrintDetails";
    public static final String VACATION1_PATH = "/{version}/Vacation1";
    public static final String OBJECT_PERMISSION1_PATH = "/{version}/ObjectPermissions1";
    public static final String CREATE_USER_FAVORITE_ARTICLE_PATH = "/{version}/CreateKBUserFavoriteArticle";
    public static final String DELETE_USER_FAVORITE_ARTICLE_PATH = "/{version}/DeleteKBUserFavoriteArticle";
    public static final String CREATE_KBARTICLE_PATH = "/{version}/CreateKBArticle";
    public static final String UPDATE_KBARTICLE_PATH = "/{version}/UpdateKBArticle";
    public static final String ACCOUNTS_BY_USER_PATH = "/{version}/AccountsByUser";
    public static final String USER_ACCOUNTS_PATH = "/{version}/UserAccounts";
    public static final String USER_ACCOUNTS_PATH1 = "/{version}/UserAccounts1";
    public static final String ACCOUNT_GROUP1_PATH = "/{version}/AccountGroup1";
    public static final String ACCOUNT_QUEUES_BY_USER_PATH = "/{version}/AccountQueuesByUser";
    public static final String UPDATE_INC_EMAIL_PATH = "/{version}/UpdateINCEmail";
    public static final String UPDATE_SR_EMAIL_PATH = "/{version}/UpdateSREmail";
    public static final String UPDATE_SRTASK_EMAIL_PATH = "/{version}/UpdateSRTaskEmail";
    public static final String CREATE_NOTIFICATION_PATH = "/{version}/CreateNotification";
    public static final String UPDATE_FRONTENDMESSAGES_PATH = "/{version}/UpdateFrontEndMessages";
    public static final String CREATE_FRONTENDMESSAGES_PATH = "/{version}/CreateFrontEndMessages";
    public static final String UPDATE_FRONTENDMESSAGES = "UpdateFrontEndMessages";
    public static final String CREATE_FRONTENDMESSAGES = "CreateFrontEndMessages";
    public static final String CREATE_NOTIFICATION = "CreateNotification";
    public static final String UPDATE_DMART_INC_PATH = "/{version}/UpdateDMartINC";
    public static final String UPDATE_DMART_SR_PATH = "/{version}/UpdateDMartSR";
    public static final String UPDATE_DMART_SRTASK_PATH = "/{version}/UpdateDMartSRTask";
    public static final String FETCH_SUPPORT_DETAILS = "SupportDetails";
    public static final String USERPREFERENCE = "UserPreference";
    public static final String FETCH_SUPPORT_ATTACHMENT = "SupportAttachments";
    public static final String INDEXERNAME_FETCH_SUPPORT_DETAILS = "Support";
    public static final String INDEXERNAME_FETCH_SUPPORT_INDEXER_SK = "Support_sk";
    public static final String SUPPORT_GROUP_PATH = "/{version}/Support2";
    public static final String SUPPORT_OTHER_GROUP_PATH = "/{version}/Support3";
    public static final String USER_ACCOUNTGROUPS_PATH = "/{version}/UserAccountGroups";
    public static final String CREATE_DMART_INC_PATH = "/{version}/CreateDMartINC";
    public static final String CREATE_DMART_SR_PATH = "/{version}/CreateDMartSR";
    public static final String CREATE_DMART_SRTASK_PATH = "/{version}/CreateDMartSRTask";
    public static final String CREATE_DMART_CONSOLIDATED_PATH = "/{version}/CreateDMartConsolidated";
    public static final String UPDATE_DMART_CONSOLIDATED_PATH = "/{version}/UpdateDMartConsolidated";
    public static final String UPDATE_SECURE_ANSWERS_PATH = "/{version}/UpdateSecureAnswers";
    public static final String CREATE_COM_RESPONSES_PATH = "/{version}/CreateComResponses";
    public static final String UPDATE_COM_RESPONSES_PATH = "/{version}/UpdateComResponses";
    public static final String UPDATE_APPLICATION_PATH = "/{version}/UpdateApplication";
    public static final String UPDATE_ACC_APPLICATION_PATH = "/{version}/UpdateAccApplication";
    public static final String UPDATE_ACC_GROUP_APP_PATH = "/{version}/UpdateAccGroupApp";
    public static final String CREATE_KBARTICLET_LOG_PATH = "/{version}/CreateKBArticleLog";
    public static final String UPDATE_KBARTICLET_LOG_PATH = "/{version}/UpdateKBArticleLog";
    public static final String UPDATE_KB_TICKET_MAPPING_PATH = "/{version}/UpdateKBTicketMapping";
    public static final String UPDATE_HANGUPS_PATH = "/{version}/UpdateHangUps";
    public static final String SUPPORT_PERSONS_BY_USER_PATH = "/{version}/SupportPersonsByUser";
    public static final String CUSTOM_FILTER_TICKET_PATH = "/{version}/CustomFilterTicket";
    public static final String FETCH_PRIMARY_AND_SECONDARY_LOGINID_PATH = "/{version}/FetchPrimaryandSecondaryLoginID";
    public static final String TICKET_COUNT_PATH = "/{version}/TicketCount";
    public static final String FETCH_USER_DETAILS_N_GROUPS_PATH = "/{version}/FetchUserDetailsNGroups";
    public static final String WORK_NOTES_UPDATE_TYPES_PATH = "/{version}/WorkNotesUpdateTypes";
    public static final String EQUIVALENT_SK_PATH = "/{version}/EquivalentSK";
    public static final String DOWNLOAD_ATTACHMENTS_PATH = "/{version}/DownloadAttachments";
    public static final String FILE_UPLOAD_PATH = "/{version}/FileUpload";
    public static final String INCIDENT_SECUREINFO_DETAILS_PATH = "/{version}/INCSecureInfoDetails";
    public static final String DOCUMENT_DETAILS_PATH = "/{version}/DocumentDetails";
    public static final String SR_SECUREINFO_DETAILS_PATH = "/{version}/SRSecureInfoDetails";
    public static final String SRTASK_SECUREINFO_DETAILS_PATH = "/{version}/SRTSecureInfoDetails";
    public static final String DMART_INCIDENT_DETAILS_PATH = "/{version}/DMARTIncidentDetails";
    public static final String UPDATE_SECURE_ANSWER1_PATH = "/{version}/UpdateSecureAnswers1";
    public static final String UPDATE_BUILDING_PATH = "/{version}/UpdateBuilding";
    public static final String UPDATE_DEPARTMENT_PATH = "/{version}/UpdateDepartment";
    public static final String UPDATE_FLOOR_PATH = "/{version}/UpdateFloor";
    public static final String UPDATE_SUITE_PATH = "/{version}/UpdateSuite";
    public static final String UPDATE_ACCOUNT_PATH = "/{version}/UpdateAccount";
    public static final String UPDATE_GROUP_PATH = "/{version}/UpdateGroup";
    public static final String UPDATE_ACCOUNT_GROUP_PATH = "/{version}/UpdateAccountGroup";
    public static final String UPDATE_CATEGORY_PATH = "/{version}/UpdateCategory";
    public static final String UPDATE_TYPE_PATH = "/{version}/UpdateType";
    public static final String UPDATE_ITEM_PATH = "/{version}/UpdateItem";
    public static final String UPDATE_CTI_PATH = "/{version}/UpdateCTI";
    public static final String UPDATE_PENDING_REASONS_PATH = "/{version}/UpdatePendingReasons";
    public static final String UPDATE_DESIGNATION_PATH = "/{version}/UpdateDesignation";
    public static final String CREATE_CHANGE_REQUEST_PATH = "/{version}/CreateChangeRequest";
    public static final String UPDATE_CHANGE_REQUEST_PATH = "/{version}/UpdateChangeRequest";
    public static final String CREATE_CHANGE_REQUEST_TASKS_PATH = "/{version}/CreateChangeRequestTasks";
    public static final String UPDATE_CHANGE_REQUEST_TASKS_PATH = "/{version}/UpdateChangeRequestTasks";
    public static final String CREATE_CHANGE_REQUEST_DETAILS_PATH = "/{version}/CreateChangeRequestDetails";
    public static final String UPDATE_CHANGE_REQUEST_DETAILS_PATH = "/{version}/UpdateChangeRequestDetails";
    public static final String UPDATE_CHANGE_TYPE_PATH = "/{version}/UpdateChangeType";
    public static final String UPDATE_PLATFORMS_PATH = "/{version}/UpdatePlatforms";
    public static final String UPDATE_CR_CATEGORY_PATH = "/{version}/UpdateCRCategory";
    public static final String UPDATE_CR_SYSTEMS_PATH = "/{version}/UpdateCRSystems";
    public static final String INCIDENT_MANAGERS_PATH = "/{version}/IncidentManagers";
    public static final String PLATFORM_DETAILS_PATH = "/{version}/PlatFormDetails";
    public static final String CR_CATEGORY_DETAILS_PATH = "/{version}/CRCategoryDetails";
    public static final String CR_SYSTEM_DETAILS_PATH = "/{version}/CRSystemDetails";
    public static final String UPDATE_LOV_PATH = "/{version}/UpdateLOVValues";
    public static final String UPDATE_ACTION_ITEMS_PATH = "/{version}/UpdateActionItems";
    public static final String UPDATE_IMPACTED_PATH = "/{version}/UpdateImpacted";
    public static final String UPDATE_CR_RISKS_PATH = "/{version}/UpdateCRRisks";
    public static final String CREATE_CR_APPROVALS_PATH = "/{version}/CreateCRApprovals";
    public static final String UPDATE_CR_APPROVALS_PATH = "/{version}/UpdateCRApprovals";
    public static final String UPDATE_RISKLEVEL_PATH = "/{version}/UpdateRiskLevel";
    public static final String UPDATE_QUESTIONS_PATH = "/{version}/UpdateQuestions";
    public static final String UPDATE_TASKTYPE_PATH = "/{version}/UpdateTaskType";
    public static final String UPDATE_BUSINESS_ORGANIZATION_PATH = "/{version}/UpdateBusinessOrganization";
    public static final String UPDATE_PRIORITY_PATH = "/{version}/UpdatePriority";
    public static final String UPDATE_ACCOUNT_ROLE_PATH = "/{version}/UpdateAccountRole";
    public static final String UPDATE_APPROVAL_TYPE_PATH = "/{version}/UpdateApprovalType";
    public static final String UPDATE_VENDOR_PATH = "/{version}/UpdateVendor";
    public static final String UPDATE_LOV_NAME_PATH = "/{version}/UpdateLOVName";
    public static final String INCIDENT_MANAGER_HISTORY_PATH = "/{version}/IncidentManagerHistory";
    public static final String AdvancedTicketSearch_PATH = "/{version}/AdvancedTicketSearch";
    public static final String SupportUser1_PATH = "/{version}/SupportUser1";
    public static final String AssignedGroup3_PATH = "/{version}/AssignedGroups3";
    public static final String AssignedGroup4_PATH = "/{version}/AssignedGroups4";
    public static final String SERVICENOW_FETCH_Ticket = "/{version}/SNOWTicketDetails";
    public static final String CURRENT_FEM_PATH = "/{version}/CurrentFEM";
    public static final String CREATE_CR_SCHEDULES_PATH = "/{version}/CreateCRSchedules";
    public static final String UPDATE_CR_SCHEDULES_PATH = "/{version}/UpdateCRSchedules";
    public static final String CREATE_CR_CLOSURE_PATH = "/{version}/CreateCRClosure";
    public static final String UPDATE_CR_CLOSURE_PATH = "/{version}/UpdateCRClosure";
    public static final String UPDATE_APPROVER_PATH = "/{version}/UpdateApprovers";
    public static final String UPDATE_CR_DISCUSSION_BOARD_PATH = "/{version}/UpdateCRDiscussionBoard";
    public static final String CHANGE_REQUEST2 = "/{version}/ChangeRequest2";
    public static final String ADMIN_PATH = "Admin/";
    public static final String MODIFIED_ON = "ModifiedOn";
    public static final String ISP = "ISP";
    public static final String ENDPOINTNAME_ACCOUNTS_BY_STATUS = "AccountsByStatus";
    public static final String ENDPOINTNAME_TICKETS = "Tickets";
    public static final String ENDPOINTNAME_ACCOUNTGROUPS1 = "AccountGroups1";
    public static final String FILE_UPLOAD = "FileUpload";
    public static final String DOWNLOAD_ATTACHMENTS = "DownloadAttachments";
    public static final String GENERATE_TICKET_NUMBER = "GenerateTicketNumber";
    public static final String PROCUREMENT = "Procurement";
    public static final String SUPPORTGROUPS_COUNT = "SupportGroupsCount";
    public static final String PROFILE_SEARCH4 = "ProfileSearch4";
    public static final String CONSOLIDATED_TICKETS = "ConsolidatedTickets";
    public static final String INCIDENT_DETAILS = "IncidentDetails";
    public static final String SERVICE_REQUEST_DETAILS = "ServiceRequestDetails";
    public static final String SRTASK_DETAILS = "SRTaskDetails";
    public static final String ACCOUNTS_BY_USER = "AccountsByUser";
    public static final String USER_ACCOUNTS = "UserAccounts";
    public static final String USER_ACCOUNTS1 = "UserAccounts1";
    public static final String ACCOUNT_GROUP1 = "AccountGroup1";
    public static final String CR_PRINT_DETAILS = "CRPrintDetails";
    public static final String ACCOUNT_QUEUES_BY_USER = "AccountQueuesByUser";
    public static final String SUPPORT2 = "Support2";
    public static final String SUPPORT3 = "Support3";
    public static final String SUPPORT_PERSONS_BY_USER = "SupportPersonsByUser";
    public static final String TICKET_COUNT = "TicketCount";
    public static final String WORK_NOTES_UPDATE_TYPES = "WorkNotesUpdateTypes";
    public static final String EQUIVALENT_SK = "EquivalentSK";
    public static final String INCIDENT_SECUREINFO_DETAILS = "INCSecureInfoDetails";
    public static final String SR_SECUREINFO_DETAILS = "SRSecureInfoDetails";
    public static final String SRTASK_SECUREINFO_DETAILS = "SRTSecureInfoDetails";
    public static final String USERFAVOURITE_SK = "UserFavourite_sk";
    public static final String CREATE_EMAIL_TICKETS = "CreateEMailTickets";
    public static final String DOCUMENT_DETAILS = "DocumentDetails";
    public static final String USER_ACCOUNTGROUPS = "UserAccountGroups";
    public static final String CHANGE_REQUEST_DETAILS = "ChangeRequestDetails";
    public static final String VACATION1 = "Vacation1";
    public static final String OBJECT_PERMISSION1 = "ObjectPermissions1";
    public static final String CREATE_KB_USER_FAVORITE_ARTICLE = "CreateKBUserFavoriteArticle";
    public static final String DELETE_KB_USER_FAVORITE_ARTICLE = "DeleteKBUserFavoriteArticle";
    public static final String INCIDENT_MANAGERS = "IncidentManagers";
    public static final String UPDATE_CHANGE_TYPE = "UpdateChangeType";
    public static final String UPDATE_PLATFORMS = "UpdatePlatforms";
    public static final String UPDATE_CR_CATEGORY = "UpdateCRCategory";
    public static final String UPDATE_CR_SYSTEMS = "UpdateCRSystems";
    public static final String PLATFORM_DETAILS = "PlatFormDetails";
    public static final String CR_CATEGORY_DETAILS = "CRCategoryDetails";
    public static final String CR_SYSTEM_DETAILS = "CRSystemDetails ";
    public static final String PLATFORM = "PlatForm";
    public static final String PLATFORMS = "PlatForms";
    public static final String CR_CATEGORY = "CRCategory";
    public static final String CATEGORY_SYSTEMS = "CategorySystems";
    public static final String FDN_PLATFORM = "FDN_Platforms";
    public static final String FDN_CR_CATEGORY = "FDN_CRCategory";
    public static final String FDN_APPROVERS = "FDN_Approvers";
    public static final String FDN_INSTRUCTIONS = "FDN_Instructions";
    public static final String FDN_CR_SYSTEM = "FDN_CRSystems";
    public static final String FDN_STATUSENTITY = "FDN_StatusEntity";
    public static final String FDN_PENDINGREASON = "FDN_PendingReasons";
    public static final String QUESTIONS1 = "Questions1";
    public static final String QUESTIONS = "Questions";
    public static final String SYSTEM_CODE = "SystemCode";
    public static final String CRSYSTEM = "CRSystem";
    public static final String UPDATE_LOV = "UpdateLOVValues";
    public static final String UPDATE_RISKLEVEL = "UpdateRiskLevel";
    public static final String UPDATE_QUESTIONS = "UpdateQuestions";
    public static final String UPDATE_TASKTYPE = "UpdateTaskType";
    public static final String UPDATE_BUSINESS_ORGANIZATION = "UpdateBusinessOrganization";
    public static final String UPDATE_PRIORITY = "UpdatePriority";
    public static final String UPDATE_ACCOUNT_ROLE = "UpdateAccountRole";
    public static final String UPDATE_APPROVAL_TYPE = "UpdateApprovalType";
    public static final String UPDATE_VENDOR = "UpdateVendor";
    public static final String UpdateAccGroupUser = "UpdateAccGroupUser";
    public static final String UPDATE_LOV_NAME = "UpdateLOVName";
    public static final String INCIDENT_MANAGER_HISTORY = "IncidentManagerHistory";
    public static final String AdvancedTicketSearch = "AdvancedTicketSearch";
    public static final String CURRENT_FEM = "CurrentFEM";
    public static final String Json_AFTER_Manupulation = "JSON_AFTER_MANUPULATION";

    public static final String ENDPOINTNAME_PROFILESEARCH1 = "ProfileSearch1";
    public static final String ENDPOINTNAME_FloorSuite = "FloorSuite";
    public static final String ENDPOINTNAME_Department = "Department";
    public static final String INCIDENTS = "INCIDENTS";
    public static final String INC = "INC";
    public static final String SR = "SR";
    public static final String SRT = "SRT";
    public static final String CR = "CR";
    public static final String CRT = "CRT";
    public static final String CRAPPR = "CRAPPR";
    public static final String CRT_ATTACHMENTS = "CRTAttachments";
    public static final String CR_ATTACHMENTS = "CRAttachments";
    public static final String CRT_WORKNOTES = "CRTWorkNotes";
    public static final String SUP = "SUP";
    public static final String BUSINESS_FUNCTION = "BusinessFunction";
    public static final String OFFLINE_UNIQUE_ID = "OfflineUniqueID";
    public static final String BUSINESS_FUNC = "BusinessFunc";
    public static final String INDEXER_UPDATE = "indexerupdate";
    public static final String INDEXER_SYNC = "syncindexer";
    public static final String INDEXERNAME_FETCH_INCIDENT = "Incident";
    public static final String INDEXERNAME_FETCH_SERVICE_REQUEST = "ServiceRequest";
    public static final String INDEXERNAME_FETCH_SERVICE_REQUEST_TASK = "SRTask";
    //public static final String INDEXERNAME_FETCH_ACCOUNT_BY_STATUS = "AccountsByStatus";
    public static final String INDEXERNAME_FETCH_PROFILE = "ProfileSearch";
    public static final String INDEXERNAME_FETCH_BACKUP_APPROVER = "BackUpApprover";
    public static final String INDEXERNAME_FETCH_ADFS_CONFIGURATION = "ADFSConfig";
    public static final String INDEXERNAME_FETCH_LOCATION = "Location";
    public static final String INDEXERNAME_FETCH_BUILDING = "Building";
    public static final String INDEXERNAME_FETCH_DEPARTMENT = "Department";
    public static final String INDEXERNAME_FETCH_FLOORSUITE = "FloorSuite";
    public static final String INDEXERNAME_FETCH_FLOOR = "Floor";
    public static final String INDEXERNAME_FETCH_SUITE = "Suite";
    public static final String INDEXERNAME_FETCH_BUILDING_INDEXER_SK = "Building_sk";
    public static final String INDEXERNAME_FETCH_CASETYPE = "CaseType";
    public static final String INDEXERNAME_FETCH_SOURCE = "Source";
    public static final String INDEXERNAME_FETCH_PRIORITY = "Priority";
    public static final String INDEXERNAME_FETCH_QUEUE = "Queue";
    public static final String INDEXERNAME_FETCH_CTI = "CTI";
    public static final String INDEXERNAME_FETCH_DESIGNATION = "Designation";
    public static final String INDEXERNAME_FETCH_VENDOR = "Vendor";
    public static final String INDEXERNAME_FETCH_MANAGE_ACCOUNT_GROUP = "ManageAccountGroup";
    public static final String INDEXERNAME_FETCH_BUSINESSFUNCTION = "BusinessFunction";
    public static final String INDEXERNAME_FETCH_SECURE_ANSWERS = "SecureAnswers";
    public static final String INDEXERNAME_FETCH_CR_DISCUSSION_BOARD = "CRDiscussionBoard";
    public static final String INDEXERNAME_FETCH_PENDING_REASON = "PendingReason";
    public static final String INDEXERNAME_FETCH_PROFILE_INDEXER_SK = "People_Info_sk";
    public static final String INDEXERNAME_FETCH_KB_ARTICLEDETAILS = "KBArticleDetails";
    public static final String CREATE_DMART_KB_ARTICLE_METADATA = "CreateDMartKBArticleMetadata";
    public static final String UPDATE_DMART_KB_ARTICLE_METADATA = "UpdateDMartKBArticleMetadata";
    public static final String ONCALLSK = "OnCallsk";
    public static final String INDEXERNAME_FETCH_LDAPDETAILS = "LDAPDetails";
    public static final String USER_SUPPORT_GROUPS_COUNT = "UserSupportGroupsCount";
    public static final String INDEXERNAME_FULL_TEXT_SEARCH = "FullTextSearch";
    public static final String INDEXERNAME_FETCH_WATCHLIST = "WatchList";
    public static final String INDEXERNAME_FETCH_CR_USER_FAVORITIES = "CRUserFavorities";
    public static final String INDEXERNAME_FETCH_WATCHLIST_INDEXER_SK = "WatchList_sk";
    public static final String INDEXERNAME_FETCH_CR_USER_FAVORITIES_INDEXER_SK = "UserFavourite_sk";
    public static final String INDEXERNAME_FETCH_USERNOTES = "UserNotes";
    public static final String INDEXERNAME_FETCH_USERPREFERENCE = "UserPreference";
    public static final String INDEXERNAME_FETCH_USERNOTES_INDEXER_SK = "UserNote_sk";
    public static final String INDEXERNAME_FETCH_USERPREFERENCE_INDEXER_SK = "UserPreference_sk";
    public static final String ACC_GROUP_USER_SK = "AccGroupUser_sk";
    public static final String SECURE_ANSWERS_SK = "SecureAnswers_sk";
    public static final String DISCUSSION_BOARD_SK = "DiscussionBoard_sk";
    public static final String CR_USER_PERMISSIONS = "CRUserPermissions";
    public static final String CR_USER_FAVOURITES = "CRUserFavorities";
    public static final String USER_FAVOURITE = "UserFavorite";
    public static final String USER_FAVORITE_ARTICLES_SK = "UserFavoriteArticles_sk";
    public static final String USER_FAVORITE_SK = "UserFavorite_sk";
    public static final String INDEXERNAME_FETCH_STATUS = "Status";
    public static final String INDEXERNAME_FETCH_WORKNOTES_UPDATE_TYPE = "WorkNotesUpdateType";
    public static final String INDEXERNAME_FETCH_PROCUREMENT = "Procurement";
    public static final String INDEXERNAME_FETCH_PROCUREMENT_DETAILS = "ProcurementDetails";
    public static final String INDEXERNAME_FETCH_ACCOUNT = "Account";
    public static final String INDEXERNAME_FETCH_VACATION = "Vacation";
    public static final String INDEXERNAME_FETCH_USERGROUPS = "AccountGroups";
    public static final String INDEXERNAME_FETCH_COM_PERMISSIONS = "COMPermissions";
    public static final String INDEXERNAME_FETCH_KB_USER_FAVORITE_ARTICLES = "KBUserFavoriteArticles";
    public static final String CREATE_DMART_KB_USER_FAVORITE_ARTICLES = "CreateDmartKBUserFavoriteArticles";
    public static final String UPDATE_DMART_KB_USER_FAVORITE_ARTICLES = "UpdateDmartKBUserFavoriteArticles";
    public static final String APP_SERVICE_DESK = "APP-SERVICE-DESK";
    public static final String CR_ADMIN = "CR_ADMIN";
    public static String CR_ADMIN_GROUPNAME;
    public static final String cr_coordinator = "CR_COORDINATOR";
    public static String cr_coordinator_groupname;
    public static String ssp_approval_url;
    public static final String INDEXERNAME_FETCH_ACTIONITEMS = "ActionItems";
    public static final String INDEXERNAME_FETCH_CR_IMPACTED = "CRImpacted";
    public static final String INDEXERNAME_FETCH_CR_IMPACTED1 = "CRImpacted1";
    public static final String INDEXERNAME_FETCH_CRRISKS = "CRRisks";
    public static final String INDEXERNAME_FETCH_CRREQUESTTYPE = "CRRequestType";
    public static final String INDEXERNAME_FETCH_CR_APPROVALS = "CRApprovals";
    public static final String INDEXERNAME_FETCH_CR_APPROVALSTATUS = "CRApprovalStatus";
    public static final String INDEXERNAME_FETCH_AccountApplication = "AccountApplication";
    public static final String INDEXERNAME_FETCH_MANAGEACCOUNTGROUPS = "ManageAccountGroup";
    public static final String INDEXERNAME_FETCH_ACCOUNT_ROLE_USER = "AccountRoleUser";
    public static final String INDEXERNAME_FETCH_ASSINGED_GROUPS = "AssignedGroups";
    public static final String INDEXERNAME_FETCH_PROBLEM_MANAGER = "ProblemManager";
    public static final String INDEXERNAME_FETCH_PROJECT = "Project";
    public static final String INDEXERNAME_FETCH_OBJECT_PERMISSIONS = "ObjectPermissions";
    public static final String SELECT = "SELECT ";
    public static final String SELECTED_FIELDS = "SelectedFields";
    public static final String FROM_WITH_SPACE = " FROM ";
    public static final String FORM_NAME = "FormName";
    public static final String FILTER_CONDITION = "FilterCondition";
    public static final String EQUAL_WITH_SINGLE_QUOTE = " = '";
    public static final String FILTER_CONDITION_VALUE = "FilterConditionValue";
    public static final String SINGLE_QUOTE = "'";
    public static final String INDEXERNAME_FETCH_INC_SECUREINFO_ADT = "INCSecureInfoADT";
    public static final String INDEXERNAME_FETCH_SR_SECUREINFO_ADT = "SRSecureInfoADT";
    public static final String INDEXERNAME_FETCH_SRT_SECUREINFO_ADT = "SRTSecureInfoADT";
    public static final String FILE = "File";
    public static final String ATTACHMENT_TYPE_U = "U";
    public static final String ATTACHMENT_TYPE = "S";
    public static final String SECURE_ATTACHMENTS = "SecureAttachments";
    public static final String SECURE_INFO = "SecureInformation";
    public static final String INC_SECURE_INFO = "INCSecureInformation";
    public static final String SR_SECURE_INFO = "SRSecureInformation";
    public static final String SRT_SECURE_INFO = "SRTSecureInformation";
    public static final String RAISED_BY_SK = "RaisedBy_sk";
    public static final String RAISED_BY_USERNAME = "RaisedByUserName";
    public static final String SUBMITTER_SK = "Submitter_sk";
    public static final String CTI_SK = "CTI_sk";
    public static final String CTI_CODE = "CTICode";
    public static final String COMMENTS = "Comments";
    public static final String COMMENT = "Comment";
    public static final String PENDING_REASON_SK = "PendingReason_sk";
    public static final String PENDING_REASON_CODE = "PendingReasonCode";

    public static final String PRIORITY_SK = "Priority_sk";
    public static final String SOURCE_SK = "Source_sk";
    public static final String SOURCE_CODE = "SourceCode";
    public static final String BUILDING_SK = "Building_sk";
    public static final String DEPARTMENT_SK = "Department_sk";
    public static final String FLOOR_SK = "Floor_sk";
    public static final String SUITE_SK = "Suite_sk";
    public static final String ALERTSTATUS_SK = "AlertStatus_sk";
    public static final String RKM_REASON = "RKMReason";
    public static final String ASSIGNED_GROUP_SK = "AssignedGroup_sk";
    public static final String LAST_UPDATED_USER_NAME = "LastUpdatedUserName";
    public static final String LAST_UPDATE_USER = "LastUpdateUser";
    public static final String WATCHLIST_SK = "WatchList_sk";
    public static final String WATCHLIST_REPORT = "WatchList Reporting";
    public static final String WATCHLIST_SP = "WatchList2";
    public static final String REPORTING_UPDATE = "ReportingUpdate";
    public static final String CLIENT_INSTANCE = "ClientInstance";
    public static String OFFLINE_TICKET_DEFAULT_SUMMARY;


    //Constants placed in ReferenceBase
    public static final String BUSINESS_FUNCTION_REQUIRED_FIELDS_JSON_PATH = "classpath:config/BusinessFunctionRequiredFields.json";
    public static final String DATASOURCE_JSON_PATH = "classpath:config/DataSource.json";
    public static final String BUSINESS_FUNCTION_TICKETS_REQUIRED_FIELDS_JSON_PATH = "classpath:config/TicketJson.json";
    public static final String INDEXER_JSON_PATH = "classpath:config/IndexerKey.json";
    public static final String BUSINESS_FUNCTION_UserPreferenceDefault_JSON_PATH = "classpath:config/UserPreferenceDefault.json";
    public static final String WHITELISTED_API_JSON_PATH = "classpath:config/WhiteListedAPI.json";
    public static final String AUTH_MAPPING_API_JSON_PATH = "classpath:config/Auth_Mapping.json";
    public static final String EXTERNAL_API_JSON_PATH = "classpath:config/externalURL.json";
    public static final String CR_JSON_PATH = "classpath:config/CR.json";
    public static final String TABLE_MAPPING_JSON_PATH = "classpath:config/tablemapping.json";
    public static final String ASSET_SP_MAPPING_JSON_PATH = "classpath:config/AssetSPMapping.json";
    public static final String ERROR_MAPPING_JSON_PATH = "classpath:config/ErrorMapping.json";
    public static final String ENDPOINT_SK = "Endpoint_sk";
    public static final String UTF_8 = "UTF-8";
    public static final String ASTERISK = "*";
    public static final String DOUBLE_ASTERISK = "*:*";
    public static final String COLON = " : ";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String IDOC = "iDoc";
    public static final String SEMI_COLON = ";";
    public static final String INDEXER_CREATE_ALL = "indexercreateall";
    public static final String INDEXER_CREATE = "indexercreate";
    public static final String EMPTY = " ";
    public static final String COLON_WITH_BACKSLASH = " : \"";
    public static final String BACKSLASH = "\"";
    public static final String AND = " AND ";
    public static final String LEVEL = "Level";
    public static final String NOT = " NOT ";
    public static final String OR = " OR ";
    public static final String OPEN_BRACKET = " ( ";
    public static final String AND_WITH_OPEN_BRACKET = " AND ( ";
    public static final String CLOSE_BRACKET = " ) ";
    public static final String QUESTION_MARK = "?";
    public static final String AND_SYMBOL = "&";
    public static final String AT = "@";
    public static final String COMMA_WITH_AT = ",@";
    public static final String COLON_WITH_EQUAL = ":=";
    public static final String CALL = "Call ";
    public static final Object NULL = null;
    public static final String result = "result";
    public static final Object link = "link";
    public static final String NA = "NA";
    public static final String RESPONSE = "Response";
    public static final String REFERENCE_TICKET_NUMBER = "ReferenceTicketNumber";
    public static final Object sys_id = "sys_id";
    public static final String DOUBLE_OPEN_BRACE = "{{";
    public static final String DOUBLE_OPEN_BRACE_ESCAPE = "\\{\\{";
    public static final String DOUBLE_CLOSE_BRACE = "}}";
    public static final String DOUBLE_CLOSE_BRACE_ESCAPE = "\\}\\}";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String EQUAL_AND_QUESTION_MARK = "=?";
    public static final String COMMA = ",";
    public static final String TILDE = "~";
    public static final String DOUBLE_OPEN_BRACE_WITH_BACKSLASH = "(?i)\\{\\{";
    public static final String DOUBLE_CLOSE_BRACE_WITH_BACKSLASH = "\\}\\}";
    public static final String EQUAL = "=";
    public static final String OPEN_SQUARE_BRACKET = "[";
    public static final String CLOSE_SQUARE_BRACKET = "]";
    public static final String LOGIN = "login";
    public static final String LOGIN_PAGE_URL = "LoginPageURL";
    public static final String SAML_TOKEN_REQUEST_PARAM_NAME = "SAMLTokenRequestParamName";
    public static final String SAML_STATUS = "SAMLStatus";
    public static final String CONSUMER_URL = "ConsumerURL";
    public static final String REDIRECT_URL = "redirect:";
    public static final String ADFS_URL = "/adfs";
    public static final String ADFS_ACCOUNT_URL = "/{Account}";
    public static final String ADFS_ACCOUNT_LOGOUT_URL = "/{Account}/logout";
    public static final String LOGOUT_URL = "LogoutURL";
    public static final String ADFS_CALLBACK_URL = ADFS_ACCOUNT_URL + "/callback";
    public static final String EMAIL = "email";
    public static final String TRUE = "true";
    public static final String ASC = "ASC";
    public static final String DESC = "desc";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String RESULT_SET_1 = "#result-set-1";
    public static final String DIGITS = "Digits";
    public static final String PREFIX = "Prefix";
    public static final String DELIMITER = "Delimiter";
    public static final String ACC_CODE = "AccCode";
    public static final String ENDPOINT_DETAILS = "EndpointDetails";
    public static final String ENDPOINT_FIELDS = "EndpointFields";
    public static final String FETCH_ALL_ACCOUNTS = "FetchAllAccounts";
    public static final String SECURE_PATIENT_ATTACHMENTS = "SecurePatientAttachments";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String SECURE = "S";
    public static final String NORMAL = "N";
    public static final String DUPLICATE = "dup_";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH-mm-ss";
    public static final String DELETED_ATTACHMENTS = "DeletedAttachments";
    public static final String FILE_NAME = "FileName";
    public static final String FILE_SIZE = "FileSize";
    public static final String FILE_TYPE = "FileType";
    public static final String FILE_PATH = "FilePath";
    public static final String PATH = "Path";
    public static final String GROUP_ID = "GroupID";
    public static final String A1st_LEVEL_GROUP = "A1st_Level_Group";
    public static final String GROUP_NAME = "GroupName";
    public static final String BackUpApprover_sk = "BackUpApprover_sk";
    public static final String BackUpApproverName = "BackUpApproverName";
    public static final String LONG_GROUP_NAME = "LongGroupName";
    public static final String IS_NOTIFICATION_NEEDED = "IsNotificationNeeded";
    public static final String NOTIFY_ADDRESS = "NotifyAddress";
    public static final String GROUP_TYPE_SK = "GroupType_sk";
    public static final String GROUP_TYPE_CODE = "GroupTypeCode";
    public static final String GROUP_TYPE = "GroupType";
    public static final String IS_TIME_SPENT_REQUIRED = "IsTimeSpentRequired";
    public static final String NOTIFY_REASSIGN = "NotifyReassign";
    public static final String SIZE = "Size";
    public static final String GROUP_CODE = "GroupCode";
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String COUNT_OF_SUPPORT_GROUP = "CountofSupportGroups";
    public static final String ACCOUNT_WITH_COLON = "Account:\"";
    public static final String ACCOUNT_WITH_OPENBRACKET_AND_COLON = "( Account :\"";
    public static final String ACCOUNT_WITH_COLON_AND_OR = " OR Account :\"";
    public static final String LOGINID_WITH_COLON = "LoginID:\"";
    public static final String PEOPLEINFOSK_WITH_COLON_AND_OR = "\" OR  People_Info_sk : \"";
    public static final String ACCOUNTSK_WITH_COLON_AND_OR = "\" OR Account_sk:\"";
    public static final String MASTERACCOUNT_WITH_COLON_AND_OR = "\" OR MasterAccount:\"";
    public static final String MASTERACCOUNT_SK_WITH_COLON_AND_OR = "\" OR MasterAccount_sk:\"";
    public static final String ENDPOINTNAME_WITH_COLON = "EndpointName : \"";
    public static final String AND_APIVERSION_WITH_COLON = "\" AND ApiVersion : \"";
    public static final String AND_OPEN_BRACKET_APITYPE_WITH_COLON = "\"AND (ApiType :\"";
    public static final String OR_APITYPE_COLON_BOTH_CLOSE_BRACKET = "\" OR ApiType : \"BOTH\")";
    public static final String ENDPOINTSK_WITH_COLON = "Endpoint_sk : \"";
    public static final String ADDRESS_LINE_1 = "AddressLine1";
    public static final String ADDRESS_LINE_2 = "AddressLine2";
    public static final String BUILDING_CODE = "BuildingCode";
    public static final String DEPARTMENT_CODE = "DepartmentCode";
    public static final String FLOOR_CODE = "FloorCode";
    public static final String SUITE_CODE = "SuiteCode";
    public static final String COUNTRY_SK = "Country_sk";
    public static final String COUNTRY = "Country";
    public static final String COUNTRY_CODE = "CountryCode";
    public static final String TELEPHONIC_CODE = "TelephonicCode";
    public static final String COUNTRY_SHORT_CODE = "CountryShortCode";
    public static final String STATE_SK = "State_sk";
    public static final String STATE_POSTAL_CODE = "StatePostalCode";
    public static final String STATE_CODE = "StateCode";
    public static final String STATE = "State";
    public static final String CITY_SK = "City_sk";
    public static final String ZIP_CODE = "ZipCode";
    public static final String PHONE = "Phone";
    public static final String REQUESTED_BY = "RequestedBy";
    public static final String LOCATION_NOTE = "LocationNote";
    public static final String INVALID_DEPT_FLAG = "InvalidDeptFlag";
    public static final String PARENT_ACCOUNT_SK = "ParentAccount_sk";
    public static final String ACCOUNT_NAME = "AccountName";
    public static final String SESSION_EXPIRY = "SessionExpiry";
    public static final String URL_CODE = "URLCode";
    public static final String ACCOUNT_TYPE_SK = "AccountType_sk";
    public static final String VIEW = "View";
    public static final String ADD = "Add";
    public static final String DELETE = "Delete";
    public static final String EDIT = "Edit";
    public static final String ON = "On";
    public static final String ACCOUNT_TYPE = "AccountType";
    public static final String ESCAPE_CHARACTER = "\"";
    public static final String ESCAPE_CHARACTER_NEWLINE = " \n ";
    public static final String ESCAPE_CHARACTER_TAB = " \t ";
    public static final String GETUTC_DATE = " GETUTCDATE() ";
    public static final String CTS = "CTS";
    public static final String ESCAPE_CHARACTER_SINGLEQUOTES = "'";
    public static final String SORTFIELD = "SortField";
    public static final String SORTORDER = "SortOrder";
    public static final String NAVICON = "NavIcon";
    public static final String DISTINCT = "Distinct";
    public static final String DISTINCTFIELD = "DistinctField";
    public static final String COUNT = "RecordCount";
    public static final String PAGE_NUMBER = "PageNo";
    public static final String FIELD_NAME = "FieldName";
    public static final String ALIAS_NAME = "AliasName";
    public static final String FUNCTIONAL_TYPE = "FunctionalType";
    public static final String CLIENT = "Client";
    public static final String MASTER_CLIENT = "Master_Client";
    public static final String SUPPORT = "Support";
    public static final String NOTIFICATION = "Notification Only";
    public static final String PERMISSION = "Permission";
    public static final String ACCOUNT_CODE_NAME = "AccountCodeName";
    public static final String MASTER_ACCOUNT_CODE_NAME = "MasterAccountCodeName";
    public static final String RESULT = "Result";
    public static final String NULL_VALUE = "NULL";
    public static final String NUMBER_THREE = "3";
    public static final String PRIMARY_LOGINID = "PrimaryLoginID";
    public static final String SECONDARY_LOGINID = "SecondaryLoginID";
    public static final String SUPPORT_USER_SK = "SupportUser_sk";
    public static final String PRIMARY_SUPPORT_USER_SK = "PrimarySupportUser_sk";
    public static final String SECONDARY_SUPPORT_USER_SK = "SecondarySupportUser_sk";
    public static final String PRIMARY_SUPPORT_SK = "PrimarySupport_sk";
    public static final String SECONDARY_SUPPORT_SK = "SecondarySupport_sk";
    public static final String PRIMARY_SUPPORT_FULLNAME = "PrimarySupportFullName";
    public static final String SECONDARY_SUPPORT_FULLNAME = "SecondarySupportFullName";
    public static final String PRIMARY_LEVEL = "PrimaryLevel";
    public static final String SECONDARY_LEVEL = "SecondaryLevel";
    public static final String STATUS = "Status";
    public static final String DOCUMENT_TYPE_SK = "DocumentType_sk";
    public static final String DOCUMENT_TYPE = "DocumentType";
    public static final String KB_ARTICLE_LEVEL = "KBArticleLevel";
    public static final String CSS_CLASS = "CssClass";
    public static final String TARGET = "Target";
    public static final String CTI_STATUS = "CTIStatus";
    public static final String STATUS_VALUE_ACTIVE = "Active";
    public static final String STATUS_DESCRIPTION = "StatusDescription";
    public static final String TICKET = "Ticket";
    public static final String TOTAL = "Total";
    public static final String INC_WITH_HYPHEN = "INC-";
    public static final String SR_WITH_HYPHEN = "SR-";
    public static final String SRT_WITH_HYPHEN = "SRT-";
    public static final String TYPE = "Type";
    public static final String ITEM = "Item";
    public static final String CALL_PARAM_IN_PROCEDURE = "{call ";
    public static final String UNDERSCORE = "_";
    public static final String QUESTION_MARKS_IN_GENERATETICKETNUMBER = " (?,?,?)}";
    public static final String RESULTSET_MSG = "Procedure doesn't return #result-set-1";
    public static final String MISSING_REQUIRED_FIELDS = "Missing Required Fields - ";
    public static final String INITIATE_R_FLOW = "InitiateRFLOW";
    public static final String INITIATED = "Initiated";
    public static final String CTS_1ST_LEVEL = "CTS-RES CTR-1ST LEVEL";
    public static final String NOT_INITIATED = "Not Initiated - ";
    public static final String REFERENCE_NUMBER = "ReferenceNumber";
    public static final String OBJECT_CODE = "ObjectCode";
    public static final String ACCOUNT_CODE = "AccountCode";
    public static final String APPLICATION_CODE = "ApplicationCode";
    public static final String APPLICATION_NAME = "ApplicationName";
    public static final String APPLICATION_SUB_URL = "ApplicationSubURL";
    public static final String APPLICATION_URL = "ApplicationURL";
    public static final String THUMBNAIL_URL = "ThumbNailURL";
    public static final String SORT_ORDER = "SortOrder";
    public static final String NAV_ICON = "NavIcon";
    public static final String CSS_CLASS_1 = "CSSClass";
    public static final String ISSSO = "IsSSO";
    public static final String DATA = "Data";
    public static final String PREV_DATA = "PrevData";
    public static final String TICKETSK_LOWERCASE = "ticketsk";
    public static final String OBJECTCODE = "objectCode";
    public static final String EMPTY_RESULTSET = "The statement did not return a result set";
    public static final String INVALID_USER = "Not a Valid User!";
    public static final String CONVERSION_FAILED = "Conversion failed";
    public static final String PEOPLE_INFO = "People_Info";
    public static final String PEOPLE_INFORMATION = "People Information";

    //Constants for ticket create and update
    public static final String TICKET_NUMBER = "TicketNumber";
    public static final String TICKETSK = "TicketSK";
    public static final String DFT = "DFT";
    public static final String TMP = "TMP";
    public static final String SUBMITTER = "Submitter";
    public static final String PARENT = "Parent";
    public static final String ASSIGNED_INDIVIDUAL = "AssignedIndividual";
    public static final String ASSIGNED_GROUP = "AssignedGroup";
    public static final String ASSIGNED_GROUP_CODE = "AssignedGroupCode";
    public static final String ASSIGNED_GROUP_NAME = "AssignedGroupName";
    public static final String BUILDING = "Building";
    public static final String DEPARTMENT = "Department";
    public static final String FLOOR = "Floor";
    public static final String SUITE = "Suite";
    public static final String CTI = "CTI";
    public static final String PRIORITY = "Priority";
    public static final String PROJECT = "Project";
    public static final String SOURCE = "Source";
    public static final String SOURCE_NAME = "SourceName";
    public static final String ALERT_STATUS = "AlertStatus";
    public static final String ALERT_STATUS_SK = "AlertStatus_sk";
    public static final String ALERT_STATUS_CODE = "AlertStatusCode";
    public static final String USER_TYPE_SK = "UserType_sk";
    public static final String USER_TYPE_CODE = "UserTypeCode";
    public static final String USER_TYPE = "UserType";
    public static final String RAISED_BY = "Raisedby";
    public static final String RAISED_BY_User_sk = "RaisedUser_sk";
    public static final String ALTERNATE_User_sk = "AIUser_sk";
    public static final String RAISED_BY_LOGINID = "RaisedLoginID";
    public static final String VENDOR = "Vendor";
    public static final String IMPACT = "Impact";
    public static final String SEVERITY = "Severity";
    public static final String SEVERITY_SK = "Severity_sk";
    public static final String SEVERITY_CODE = "SeverityCode";
    public static final String CONTACT_TYPE_SK = "ContactType_sk";
    public static final String CONTACT_TYPE_CODE = "ContactTypeCode";
    public static final String TRIAGE_GROUP = "TriageGroup";
    public static final String BUS_ORG = "BusOrg";
    public static final String DESIGNATION = "Designation";
    public static final String USERS = "Users";
    public static final String QUEUE = "Queue";
    public static final String QUEUE_sk = "Queue_sk";
    public static final String QUEUE_CODE = "QueueCode";
    public static final String PHONE_LINE = "PhoneLine";
    public static final String PRIMARY_NUMBER = "PrimaryNumber";
    public static final String SECONDARY_NUMBER = "SecondaryNumber";
    public static final String PENDING_REASON = "PendingReason";
    public static final String PROBLEM_MANAGER = "ProblemManager";
    public static final String CASE_TYPE = "CaseType";
    public static final String CASE_TYPE_sk = "CaseType_sk";
    public static final String SUMMARY = "Summary";
    public static final String DESCRIPTION = "Description";
    public static final String RESOLUTION = "Resolution";
    public static final String BUSINESS_PHONE = "BusinessPhone";
    public static final String BUSINESS_PHONE_EXT = "BusinessPhoneExt";
    public static final String MOBILE = "Mobile";
    public static final String PAGER = "Pager";
    public static final String PAGER_PIN = "PagerPin";
    public static final String OFFICE = "Office";
    public static final String REASON = "Reason";
    public static final String ONSITE_CALL = "OnsiteCall";
    public static final String USERPREFERENCE_COMMON = "COMM";
    public static final String TIME_SPENT_IN_MINUTES = "TimeSpentInMinutes";
    public static final String IS_ALTERNATE_USER = "IsAlternateUser";
    public static final String FORM_BUILDER_ID = "FormBuilderID";
    public static final String TOTAL_COST = "TotalCost";
    public static final String EVENT_SCHEDULE = "EventSchedule";
    public static final String ROOT_CAUSE_CATEGORY = "RootCauseCategory";
    public static final String MINUTES = "Minutes";
    public static final String zAssignLogin = "zAssignLogin";
    public static final String COST_CENTER = "CostCenter";
    public static final String SD_PROVIDED_DETAILS = "SDProvidedDetails";
    public static final String SUBMITTER_LOGINID = "SubmitterLoginID";
    public static final String MODIFIED_BY_LOGINID = "ModifiedByLoginID";
    public static final String BUS_EMAILID = "BusEmailID";
    public static final String ASSET_TAG = "AssetTag";
    public static final String COMPUTER_NAME = "ComputerName";
    public static final String TITLE = "Title";
    public static final String TITLE_SK = "Title_sk";
    public static final String TITLE_CODE = "TitleCode";
    public static final String USER_HIERARCHY_SK = "UserHierarchy_sk";
    public static final String USER_NOTES_VISIBILITY_SK = "UserNotesVisibility_sk";
    public static final String USER_SKILL_SET_SK = "UserSkillset_sk";
    public static final String EMAIL_DOMAINS_SK = "EMailDomains_sk";
    public static final String INSTRUCTION_SK = "Instruction_sk";
    public static final String INSTRUCTION_CODE = "InstructionCode";
    public static final String INSTRUCTION_TYPE = "InstructionType";
    public static final String XQUEUE_SK = "XQueue_sk";
    public static final String XSTATUS_SK = "XStatus_sk";
    public static final String EXPIRE_ON = "ExpireOn";
    public static final String SKILL_SET_SK = "Skillset_sk";
    public static final String AUTOMATION_SERVICES_SK = "AutomationServices_sk";
    public static final String ESCALATION_SK = "Escalation_sk";
    public static final String ESCALATION_CODE = "EscalationCode";
    public static final String ESCALATION_NAME = "EscalationName";
    public static final String UNIT_SK = "Unit_sk";
    public static final String UNIT_CODE = "UnitCode";
    public static final String UNIT_NAME = "UnitName";
    public static final String NOTIFICATION_SK = "Notification_sk";
    public static final String NOTIFICATION_MAPPING_SK = "NotificationMapping_sk";
    public static final String NOTIFICATION_CODE = "NotificationCode";
    public static final String NOTIFICATION_NAME = "NotificationName";
    public static final String DOC_ID = "DocID";
    public static final String ARTICLE_KEYWORDS = "Article_Keywords";
    public static final String ARTICLE_OWNER_LOGINID = "ArticleOwnerLoginID";
    public static final String ON_CREATION = "OnCreation";
    public static final String ON_EVERY = "OnEvery";
    public static final String ON_EVERY_UNIT_SK = "OnEveryUnit_sk";
    public static final String NO_OF_TIMES_BEFORE_ETA = "NoOfTimesBeforeETA";
    public static final String FINAL_REMINDER = "FinalReminder";
    public static final String FINAL_REMINDER_UNIT_SK = "FinalReminderUnit_sk";
    public static final String SERVICE_NAME = "ServiceName";
    public static final String SKILL_SET_CODE = "SkillsetCode";
    public static final String SKILL_SET = "Skillset";
    public static final String VISBILITY_USER_SK = "VisbilityUser_sk";
    public static final String VISIBILITY_GROUP_SK = "VisibilityGroup_sk";
    public static final String REPORTING_SK = "Reporting_sk";
    public static final String MENTOR_SK = "Mentor_sk";
    public static final String RDY_MAPPING = "RDYMapping";
    public static final String BUS_HOURS_TO_RESOLVE = "BusHoursToResolve";
    public static final String HOURS_TO_RESOLVE = "HourstoResolve";
    public static final String CONVERTED_TO = "ConvertedTo";
    public static final String IS_AUTO_CLOSE = "IsAutoClose";
    public static final String TICKET_TYPE = "TicketType";
    public static final String LAST_UPDATED_DATE = "LastUpdateDate";
    public static final String NON_SLA_ISSUE_REASON = "NonSLAIssueReason";
    public static final String CONTACT_TYPE = "ContactType";
    public static final String IS_VERIFIED_INFO = "IsVerifiedInfo";
    public static final String NO_ARTICLE_REASON = "NoArticleReason";
    public static final String WRK_DESCRIPTION = "WrkDescription";
    public static final String NOTIFY_OWNER = "NotifyOwner";
    public static final String ARTICLE_METADATA = "ArticleMetadata";
    public static final String TASK_ID = "TaskID";
    public static final String SERVICEREQUEST_SK = "ServiceRequest_sk";
    public static final String TASK_NAME = "TaskName";
    public static final String TASK_DETAILS = "TaskDetails";
    public static final String TASK_SEQUENCE = "TaskSequence";
    public static final String CLONED_SK = "Cloned_sk";
    public static final String PENDING_TIME = "PendingTime";
    public static final String ASSIGNED_ON = "AssignedOn";
    public static final String ACKNOWLEDGED_ON = "AcknowledgedOn";
    public static final String PENDING_ON = "PendingOn";
    public static final String PENDING_OFF = "PendingOff";
    public static final String REOPENED_ON = "ReopenedOn";
    public static final String FLR_ACHIEVED = "FLRAchieved";
    public static final String FLR_POSSIBLE = "FLRPossible";
    public static final String RESOLVED_BY = "ResolvedBy";
    public static final String RESOLVED_ON = "ResolvedOn";
    public static final String CLOSED_ON = "ClosedOn";
    public static final String IS_SD_CAN_RESOLVE = "IsSDCanResolve";
    public static final String SD_RESOLVED_COMMENTS = "SDResolvedComments";
    public static final String IS_SD_ASSIGNED_WRONG_GROUP = "IsSDAssignedWrongGroup";
    public static final String SD_ASSIGNED_WRONG_GROUP_COMMENTS = "SDAssignedWrongGroupComments";
    public static final String IS_SD_PROVIDE_DETAILS = "IsSDProvideDetails";
    public static final String EXTERNALTICKETID = "ExternalTicketID";
    public static final String PREV_ASSIGNED_INDIVIDUAL = "PrevAssignedIndividual";
    public static final String PREV_ASSIGNED_GROUP = "PrevAssignedGroup";
    public static final String FIRST_ACK_ON = "FirstAckOn";
    public static final String FIRST_ASSIGNED_ON = "FirstAssignedOn";
    public static final String VOID_BY = "VoidBy";
    public static final String VOID_ON = "VoidOn";
    public static final String IS_REOPENED = "IsReOpened";
    public static final String CONVERTED_FROM = "ConvertedFrom";
    public static final String GRP_CHG_COUNT = "GrpChgCount";
    public static final String PAUSE_SLA = "PauseSLA";
    public static final String WORK_STARTED_ON = "WorkStartedOn";
    public static final String UPDATE_TYPE = "UpdateType";
    public static final String UPDATE_ITEM = "UpdateItem";
    public static final String UPDATE_CTI = "UpdateCTI";
    public static final String UPDATE_PENDING_REASON = "UpdatePendingReasons";
    public static final String UPDATE_DESIGNATION = "UpdateDesignation";
    public static final String USER = "User";
    public static final String COMPLELTED_ON = "CompletedOn";
    public static final String ALT_USER_SK = "ALT_User_sk";
    public static final String ALT_IS_SYSTEM_USER = "ALT_IsSystemUser";
    public static final String ALT_FIRSTNAME = "ALT_FirstName";
    public static final String ALT_LASTNAME = "ALT_LastName";
    public static final String ALT_EMAIL_ADDRESS = "ALT_EmailAddress";
    public static final String ALT_BUILDING = "ALT_Building";
    public static final String ALT_DEPARTMENT = "ALT_Department";
    public static final String ALT_FLOOR = "ALT_Floor";
    public static final String ALT_SUITE = "ALT_Suite";
    public static final String ALT_OFFICE = "ALT_Office";
    public static final String ALT_PHONE = "ALT_Phone";
    public static final String ALT_PHONEEXT = "ALT_PhoneExt";
    public static final String ALT_STATUS = "ALT_Status";
    public static final String ALT_REFID2 = "ALT_RefID2";
    public static final String ALT_ACCOUNT_SK = "ALT_Account_sk";
    public static final String SECURE_INFORMATION = "SecureInformation";
    public static final String ATTACHMENTS = "Attachments";
    public static final String SECURE_INFO_SK = "SecureInfo_SK";
    public static final String DELETE_ATTACHMENT_SK = "DeleteAttachment_sk";
    public static final String WORKNOTE_TYPE = "WorkNoteType";
    public static final String WORKNOTES_UPDATE_TYPE = "WorkNotesUpdateType";
    public static final String WORKNOTES_QUEUE = "WorkNotesQueue";
    public static final String INC_ROOT_CAUSE_SK = "INCRootCause_sk";
    public static final String RCL_MAPPING_SK = "RCLMapping_sk";
    public static final String ROOT_CAUSE_LEVEL_SK = "RootCauseLevel_sk";
    public static final String PARENT_ROOT_CAUSE_LEVEL_SK = "ParentRootCauseLevel_sk";
    public static final String ROOT_CAUSE_LEVEL_CODE = "RootCauseLevelCode";
    public static final String INC_ROOT_CAUSE_STATUS = "INCRootCauseStatus";
    public static final String SR_ROOT_CAUSE_SK = "SRRootCause_sk";
    public static final String SR_ROOT_CAUSE_STATUS = "SRRootCauseStatus";

    //Constants for create and update request

    public static final String LOGIN_USER_SK = "LoginUser_sk";
    public static final String START_DATE = "StartDate";
    public static final String END_DATE = "EndDate";
    public static final String STATUS_SK = "Status_sk";
    public static final String SkillsetCode = "SkillsetCode";
    public static final String Skillset_sk = "Skillset_sk";
    public static final String STATUS_TYPE_SK = "StatusType_sk";
    public static final String USER_POSITION_SK = "UserPosition_sk";
    public static final String USER_POSITION_CODE = "UserPositionCode";
    public static final String BUS_ORG_SK = "BusOrg_sk";
    public static final String USER_VACATION_SK = "UserVacation_sk";
    public static final String REPOSITORY_NAME = "RepositoryName";
    public static final String REPOSITORY_SK = "Repository_sk";
    public static final String MODIFIED_BY = "ModifiedBy";
    public static final String GROUP_SK = "Group_sk";
    public static final String ACCOUNT_GROUP_SK = "AccountGroup_sk";
    public static final String CATEGORY_SK = "Category_sk";
    public static final String CATEGORY_CODE = "CategoryCode";
    public static final String CATEGORY_NAME = "CategoryName";
    public static final String PARENT_CATEGORY_SK = "ParentCategory_sk";
    public static final String TYPE_SK = "Type_sk";
    public static final String TYPE_CODE = "TypeCode";
    public static final String ITEM_SK = "Item_sk";
    public static final String ITEM_CODE = "ItemCode";
    public static final String SUPPORT_TYPE = "SupportType";
    public static final String NOTES = "Notes";
    public static final String ACTIVE = "Active";
    public static final String EXECUTION_ORDER = "ExecutionOrder";
    public static final String ROUND_ROBIN_RULE_SK = "RoundRobinRule_sk";
    public static final String SUPPORT_ATTACHMENT_SK = "SupportAttachment_sk";
    public static final String GLOBAL = "Global";
    public static final String PRIMARY_USER_SK = "PrimaryUser_sk";
    public static final String SECONDARY_USER_SK = "SecondaryUser_sk";
    public static final String SEQUENCE = "Sequence";
    public static final String SUPPORT_SK = "Support_sk";
    public static final String MASTER_TYPE_CODE = "MasterTypeCode";
    public static final String START = "Start";
    public static final String END = "End";
    public static final String SELECTION_TYPE = "SelectionType";

    public static final String WIZ_ITEM_SK = "WizItem_sk";
    public static final String PARENT_WIZITEM_SK = "ParentWizItem_sk";
    public static final String LEVEL_NO = "LevelNo";
    public static final String SEQ_NO = "SeqNo";
    public static final String BUTTON_TYPE = "ButtonType";
    public static final String SUBMISSION_TYPE = "SubmissionType";
    public static final String ATTRIBUTE = "Attribute";
    public static final String ICON = "Icon";

    public static final String ROUND_ROBIN_RULE_CODE = "RoundRobinRuleCode";
    public static final String ROUND_ROBIN_RULE = "RoundRobinRule";
    public static final String AUTH_TYPE_SK = "AuthType_sk";
    public static final String AUTH_TYPE_CODE = "AuthTypeCode";
    public static final String AUTH_TYPE = "AuthType";

    public static final String PEOPLE_INFO_SK = "PeopleInfo_sk";
    public static final String PEOPLEINFO_SK = "People_Info_sk";
    public static final String EMAIL_ADDRESS = "EmailAddress";
    public static final String PHONE_WORK = "PhoneWork";
    public static final String PHONE_EXTN = "PhoneExtn";
    public static final String FAX = "Fax";
    public static final String PAGER_NUMERIC = "PagerNumeric";
    public static final String PAGER_ALPHA = "PagerAlpha";
    public static final String PHONE_CELL = "PhoneCell";
    public static final String PHONE_HOME = "PhoneHome";
    public static final String NOTIFY_METHOD = "NotifyMethod";
    public static final String CLIENT_NOTES = "ClientNotes";
    public static final String NOTE_EXPIRES = "NoteExpires";
    public static final String SSP_PREFERED_CONTACT_METHOD = "SSPPreferedContactMethod";
    public static final String AD_COMPANY = "ADCompany";
    public static final String AD_DEPARTMENT = "ADDepartment";
    public static final String EMPLOYEE_NUMBER = "EmployeeNumber";
    public static final String MANAGER_LOGIN = "ManagerLogin";
    public static final String MANAGER_NAME = "ManagerName";
    public static final String MANAGER_STRING = "ManagerString";
    public static final String MIDDLE_INIT = "MiddleInit";
    public static final String FULL_NAME = "FullName";
    public static final String TRIAGE_FULL_NAME = "HELP DESK TRIAGE";
    public static final String ROLE_PREFIX = "RolePrefix";
    public static final String ROLE = "Role";
    public static final String PEOPLE_INFO_STATUS_SK = "PeopleInfoStatus_sk";
    public static final String IS_SUPPORT_PERSON = "IsSupportPerson";
    public static final String SUPERVISOR_LOGIN = "SupervisorLogin";
    public static final String USER_POSITION_STATUS_SK = "UserPositionStatus_sk";
    public static final String IS_PRIMARY_LOCATION = "IsPrimaryLocation";
    public static final String LOGIN_NAME = "LoginName";
    public static final String GROUP_LIST = "GroupList";
    public static final String COMPUTED_GRP_LIST = "ComputedGrpList";
    public static final String LOGIN_ALIAS = "LoginAlias";

    public static final String TICKET_SK = "Ticket_sk";
    public static final String FUNCTION_TYPE = "FunctionType";
    public static final String PREFRENCE = "Preference";
    public static final String IS_PUBLIC = "IsPublic";
    public static final String AGENT_USER_SK = "AgentUser_sk";
    public static final String BUSINESS_FUNCTION_CODE = "BusinessFunctionCode";
    public static final String BUSINESS_FUNCTION_CODE_CRAPPR = "CRAPPR";
    public static final String SECURE_INFORMATION_SK = "SecureInformation_sk";
    public static final String ACTION = "Action";
    public static final String ATTACHMENTS_SK = "Attachments_sk";
    public static final String CREATED_BY = "CreatedBy";
    public static final String MY_INC = "My_INC";
    public static final String MY_GROUP_INC = "My_group_INC";
    public static final String RAISED_BY_ME = "Raised_By_Me";
    public static final String MY_SR = "My_SR";
    public static final String MY_GROUP_SR = "My_group_SR";
    public static final String MY_SRT = "My_SRT";
    public static final String MY_GROUP_SRT = "My_group_SRT";
    public static final String MY_CR = "My_CR";
    public static final String MY_GROUP_CR = "My_group_CR";
    public static final String APPROVERS = "Approvers";
    public static final String APPROVER_GROUP = "ApproverGroup";


    public static final String USERNOTE_SK = "UserNote_sk";

    ///SFController constants
    public static final String INDEXER_ALIAS_COLUMN_NAME_ON = "On";
    public static final String DATETIME = "datetime";
    public static final String STATUS_CODE = "StatusCode";
    public static final String INVALID_INDEXER = "Invalid Indexer!";
    public static final String PROCUREMENT_REQ_ACCESSORIES = "requiredAccessories";
    public static final String PROCUREMENT_OPT_ACCESSORIES = "optionalAccessories";
    public static final String PROCUREMENT_SK = "ProcurementCode";
    public static final String IS_REQUIRED = "IsRequired";
    public static final String PROCUREMENT_DETAILS_SK = "ProcurementDetailsCode";
    public static final String CATEGORY = "Category";
    public static final String CHILDEQUIPMENTNAME = "ChildEquipmentName";
    public static final String BREAKTAG = "<br />";
    public static final String ACCESSORYIMGFILE = "<img style='float:left;width:75px;margin-right:5px;' src='Clients/images/procurement/";
    public static final String CLOSETAG = "'/>";
    public static final String PRICE = "Price";
    public static final String EQUIPMENT = "Equipment";
    public static final String PROC_OUTPUT = "{\"aaData\":[";
    public static final String PROC_QUERY = "Status:\"1\"";
    public static final String SR_NUMBER = "SRNumber";
    public static final String WATCHLIST = "WatchList";
    public static final String SELFFLAG = "SelfFlag";
    public static final String WATCHLIST_STATUS = "1";
    public static final String INDEXERNAME_FETCH_INCIDENT_SP = "Incident1";
    public static final String INDEXERNAME_FETCH_INC_HISTORY = "INCHistory";
    public static final String INDEXERNAME_FETCH_INC_WORKNOTES = "INCWorkNotes";
    public static final String INDEXERNAME_FETCH_INC_ATTACHMENTS = "INCAttachments";
    public static final String INDEXERNAME_FETCH_INC_SECUREINFO = "INCSecureInformation";
    public static final String INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP = "MasterTypeMapping1";
    public static final String INDEXERNAME_FETCH_KBTICKET_MAPPING_SP = "KBTicketMapping1";
    public static final String INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP = "KBArticleLog";
    public static final String INDEXERNAME_FETCH_COM_RESPONSES = "ComResponses";
    public static final String INDEXERNAME_FETCH_INC_ROOT_CAUSE = "INCRootCause";
    public static final String INDEXERNAME_FETCH_SR_ROOT_CAUSE = "SRRootCause";
    public static final String INDEXERNAME_FETCH_DOCUMENTS = "Documents";
    public static final String INDEXERNAME_FETCH_DOCUMENTATTACHMENTS = "DocumentAttachments";
    public static final String INCIDENT = "Incident";
    public static final String INDEXERNAME_FETCH_SR_SP = "ServiceRequest1";
    public static final String INDEXERNAME_FETCH_SR_HISTORY = "SRHistory";
    public static final String INDEXERNAME_FETCH_SR_WORKNOTES = "SRWorkNotes";
    public static final String INDEXERNAME_FETCH_SR_ATTACHMENTS = "SRAttachments";
    public static final String INDEXERNAME_FETCH_SR_SECUREINFO = "SRSecureInformation";
    public static final String SERVICEREQUEST = "ServiceRequest";
    public static final String INDEXERNAME_FETCH_SRT_SP = "SRTask1";
    public static final String INDEXERNAME_FETCH_SRT_HISTORY = "SRTHistory";
    public static final String INDEXERNAME_FETCH_SRT_WORKNOTES = "SRTWorkNotes";
    public static final String INDEXERNAME_FETCH_SRT_ATTACHMENTS = "SRTAttachments";
    public static final String INDEXERNAME_FETCH_SRT_SECUREINFO = "SRTSecureInformation";
    public static final String INDEXERNAME_FETCH_CHANGE_REQUEST1 = "ChangeRequest1";
    public static final String INDEXERNAME_FETCH_CHANGE_REQUEST = "ChangeRequest";
    public static final String INDEXERNAME_FETCH_CR_TASKS = "CRTasks";
    public static final String INDEXERNAME_FETCH_CR_WORKNOTES = "CRWorkNotes";
    public static final String INDEXERNAME_FETCH_CR_ATTACHMENTS = "CRAttachments";
    public static final String INDEXERNAME_FETCH_CR_ASSETS = "CRAssets";
    public static final String INDEXERNAME_FETCH_CR_GROUPS = "CRGroups";
    public static final String INDEXERNAME_FETCH_CR_BUILDINGS = "CRBuildings";
    public static final String INDEXERNAME_FETCH_CR_LOCATIONS1 = "CRLocations1";
    public static final String INDEXERNAME_FETCH_CR_RISKS1 = "CRRisks1";
    public static final String INDEXERNAME_FETCH_KB_PHRASES = "KBPhrases";
    public static final String INDEXERNAME_FETCH_QUESTIONS = "Questions";
    public static final String INDEXERNAME_FETCH_READYMAPPING = "ReadyMapping";
    public static final String INDEXERNAME_FETCH_COM_REFERENCES = "COMReferences";
    public static final String INDEXERNAME_FETCH_CR_DISCUSSION_BOARD_SP = "CRDiscussionBoard1";
    public static final String INDEXERNAME_FETCH_CR_APPROVALS1 = "CRApprovals1";
    public static final String INDEXERNAME_FETCH_APPROVALCRITERIA = "ApprovalCriteria";
    public static final String INDEXERNAME_FETCH_APPROVERS = "Approvers";
    public static final String INDEXERNAME_FETCH_CR_TASKS1 = "CRTasks1";
    public static final String INDEXERNAME_FETCH_CR_DESIGNBOARD = "CRDesignBoard";
    public static final String INDEXERNAME_FETCH_CR_APPROVALSTATUS1 = "CRApprovalStatus1";
    public static final String INDEXERNAME_FETCH_CR_STAKEHOLDER = "CRStakeHolder";
    public static final String INDEXERNAME_FETCH_CRT_STAKEHOLDERS = "CRTStakeHolders";
    public static final String INDEXERNAME_FETCH_CRT_ASSIGNMENTS = "CRTAssignments";
    public static final String INDEXERNAME_FETCH_CRT_WORKNOTES = "CRTWorkNotes";
    public static final String INDEXERNAME_FETCH_CRT_ATTACHMENTS = "CRTAttachments";
    public static final String INDEXERNAME_FETCH_CR_SCHEDULES1 = "CRSchedules1";
    public static final String INDEXERNAME_FETCH_CR_SCHEDULES = "CRSchedules";
    public static final String INDEXERNAME_FETCH_CR_DISCUSSIONPOINTS = "CRDiscussionPoints";
    public static final String INDEXERNAME_FETCH_COM_ACTIONITEMS = "ActionItems";
    public static final String INDEXERNAME_FETCH_COM_ACTIONITEMS1 = "ActionItems1";
    public static final String INDEXERNAME_FETCH_CR_MEETINGPARTICIPANTS = "CRMeetingParticipants";
    public static final String SRTASK = "SRTask";
    public static final String CHANGE_REQUEST = "ChangeRequest";
    public static final String CR_SCHEDULES = "CRSchedules";
    public static final String ENTITYCODE = "EntityCode";
    public static final String ENTITYNAME = "EntityName";
    public static final String ENTITY_sk = "Entity_sk";
    public static final String ENTITYTYPE = "EntityType";
    public static final String CR_DESIGNBOARD = "CRDesignBoard";
    public static final String CR_APPROVALS_ENTITYNAME = "CR_Approvals";
    public static final String CR_TASKS_ENTITYNAME = "CR_Tasks";
    public static final String CR_SCHEDULES_ENTITYNAME = "CR_Schedules";
    public static final String TICKET_COUNT_QUERY = " AND (EntityCode : \"INC\" OR EntityCode : \"SR\")";
    public static final String OPEN_SQUARE_BRACKET_WITH_NEWLINE = "\":[\n";
    public static final String CLOSE_SQUARE_BRACKET_WITH_NEWLINE = "\n]\n";
    public static final String CLOSE_BRACKETS_AND_BRACES = "]}";
    public static final String COMMA_WITH_DOUBLE_QUOTES = ",\"";
    public static final String DOUBLE_QUOTES = "\"";
    public static final String MASTERACCOUNT_WITH_COLON = " MasterAccount:\"";
    public static final String MASTERACCOUNTSK_WITH_COLON = " MasterAccount_sk:\"";
    public static final String ASSIGNED_GROUPNAME_WITH_COLON = " AssignedGroupName :\"";
    public static final String SUBMITTERSK_WITH_COLON_AND_OR = "\" OR Submitter_sk :\"";
    public static final String RaisedSK_WITH_COLON_AND_OR = "\" OR RaisedUser_sk :\"";
    public static final String SUBMITTER_LOGINID_WITH_COLON = "SubmitterLoginID :\"";
    public static final String RAISED_LOGINID_WITH_COLON = "RaisedLoginID :\"";
    public static final String ACCOUNT_CODE_WITH_COLON = "AccountCode : \"";
    public static final String USER_SK_WITH_COLON = "User_sk : \"";
    public static final String ACCOUNT_CODE_WITH_COLON_AND_OR = "\" OR AccountCode : \"";
    public static final String BUSINESSFUNCTION_WITH_COLON = "BusinessFunction :\"";
    public static final String BUSINESSFUNCTIONCODE_WITH_COLON = "BusinessFunctionCode :\"";
    public static final String GROUPNAME_WITH_COLON = "GroupName :\"";
    public static final String GROUPCODE_WITH_COLON = "GroupCode :\"";
    public static final String ATTACHMENT_FILENAME = "attachment;filename=";
    public static final String GROUPS = "Groups";
    public static final String FOREIGN_KEY = "FOREIGN KEY";
    public static final String INFO = "Info";
    public static final String COLUMN_DOESNOT_ALLOW_NULLS = "column does not allow nulls";
    public static final String DOCID_SHOULD_BE_GREATER_THAN_ZERO = "docID must be >= 0 and < maxDoc=0 (got docID=0)";
    public static final String NOT_ACCOUNT_WITH_COLON = "NOT Account :\"";
    public static final String NOT_GROUPNAME_WITH_COLON = "NOT GroupName :\"";
    public static final String ASSIGNED_INDIVIDUALSK_WITH_COLON = " AND AssignedIndividual_sk : \"";
    public static final String ASSIGNED_INDIVIDUALSK = "AssignedIndividual_sk";
    public static final String SUBMITTERSK_WITH_COLON = " AND Submitter_SK : \"";
    public static final String CLOSE_BRACKET_WITH_DOUBLEQUOTES = "\")";
    public static final String STATUS_CODE_WITH_COLON = " StatusCode :\"";
    public static final String STATUS_WITH_COLON = " Status:\"";
    public static final String NULL1 = "NULL";
    public static final String SQL_SERVER_EXC = "SQLServerException";
    public static final boolean FALSE = false;
    public static final String JDBC_ACC_TEMPLATE = "jdbcAccTemplate";
    public static final String ENGINE_TYPE = "EngineType";
    public static final String INPUT_JSON = "InputJson";
    public static final String R_FLOW = "RFLOW";
    public static final String K_FLOW = "KFLOW";
    public static final String SUPPORTTYPE_WITH_COLON = "SupportType :\"";
    public static final String RELATED_TYPE = "RelatedType";
    public static final String RELATEDTYPE_WITH_COLON = "RelatedType :\"";
    public static final String ALT_CONTACT_SK = "AltContact_sk";
    public static final String DISPLAY_ACCOUNT = "DisplayAccount";
    public static final String CREATED_ON = "CreatedOn";
    public static final String WORKNOTES_NAME = "WorkNotesName";
    public static final String WORKNOTES_UPDATETYPE = "WorkNotesUpdateType";
    public static final String WN_CREATOR_LOGINID = "WNCreatorLoginID";
    public static final String CREATED_BY_LOGINID = "CreatedByLoginID";
    public static final String WN_CREATOR_FULLNAME = "WNCreatorFullName";

    //Constants for Profile
    public static final String REFID_1 = "RefID1";
    public static final String REFID_2 = "RefID2";
    public static final String REFID_3 = "RefID3";
    public static final String REFID_4 = "RefID4";
    public static final String SUPERVISOR_NAME = "SupervisorName";
    public static final String COST_CODE = "CostCode";
    public static final String ROLE_DESC = "RoleDesc";
    public static final String PER_EMAIL_ID = "PerEmailID";
    public static final String PASSWORD = "Password";
    public static final String GUID = "GUID";
    public static final String CLIENT_NOTE_EXPIRATION_DATE = "ClientNoteExpirationDate";
    public static final String PEOPLE_SOFT_EMP_ID = "PeopleSoftEMPID";
    public static final String COMPANY_CODE = "CompanyCode";

    public static final String CODE = "Code";
    public static final String DESIGNATION_SK = "Designation_sk";
    public static final String DESIGNATION_CODE = "DesignationCode";
    public static final String AD_PASSWORD = "ADPassword";
    public static final String IS_SPECIAL_NOTIFICATION = "IsSpecialNotification";
    public static final String IS_AUTOMATED_USER = "IsAutomatedUser";
    public static final String RETURN_ON = "ReturnOn";
    public static final String CLIENT_EXECUTIVE = "ClientExecutive";

    //Constants for Masterdetails
    public static final String BUSORG_CODE = "BusOrgCode";
    public static final String PRIORITY_CODE = "PriorityCode";
    public static final String ACCOUNT_ROLE_SK = "AccountRole_sk";
    public static final String ACCOUNT_ROLE_CODE = "AccountRoleCode";
    public static final String APPROVAL_TYPE_SK = "ApprovalType_sk";
    public static final String APPROVAL_TYPE_CODE = "ApprovalTypeCode";
    public static final String APPROVAL_TYPE = "ApprovalType";
    public static final String VENDOR_SK = "Vendor_sk";
    public static final String VENDOR_CODE = "VendorCode";

    public static int ENDPOINT_RESULTS_COUNT;


    public static final String FNAME = "FName";
    public static final String LNAME = "LName";
    public static final String EMP_NO = "EmpNo";
    public static final String BUSEMAILID = "BuseMailId";
    public static final String ATTRIBUTE2 = "Attribute2";
    public static final String ATTRIBUTE3 = "Attribute2";
    public static final String ATTRIBUTE4 = "Attribute2";
    public static final String ATTRIBUTE5 = "Attribute2";
    public static final String ATTRIBUTE6 = "Attribute2";
    public static final String ATTRIBUTE7 = "Attribute2";
    public static final String ISACTIVE = "IsActive";
    ;
    public static final String PEREMAILID = "PreEmailId";
    public static final String ASSIGNEDGROUP = "AssignedGroup";
    public static final String EXTSYSID = "ExtSysId";
    public static final String PEOPLESOFT_EMPID = "PeopleSoftEmpId";

    public static String dedicatedInstances;
    public static String INDEXER_SYNC_URL;
    public static String INDEXER_CREATEALL_URL;
    public static String INDEXER_CREATE_URL;
    public static String INDEXER_UPDATE_URL;
    public static List<String> ticketCreateReqFields = new ArrayList<>();
    public static List<String> ticketUpdateReqFields = new ArrayList<>();
    public static List<String> ticketDetailsCreateReqFields = new ArrayList<>();
    public static List<String> ticketDetailsUpdateReqFields = new ArrayList<>();
    public static List<String> createProfileReqFields = new ArrayList<>();
    public static List<String> createChangeRequestReqFields = new ArrayList<>();
    public static List<String> updateChangeRequestReqFields = new ArrayList<>();
    public static List<String> createChangeRequestDetailsReqFields = new ArrayList<>();
    public static List<String> updateChangeRequestDetailsReqFields = new ArrayList<>();


    // Constants for createEmailRequest
    public static final String MAIL_TEMPLATE_SK = "MailTemplate_sk";


    public static int getEndpointResultsCount() {
        return ENDPOINT_RESULTS_COUNT;
    }

    @Value("${luc.record.limit}")
    public void setEndpointResultsCount(int endpointResultsCount) {
        this.ENDPOINT_RESULTS_COUNT = endpointResultsCount;
    }

    public static final String INC_SK = "Inc_sk";
    public static final String INCSK = "INCSK";
    public static final String SRSK = "SRSK";
    public static final String SRTSK = "SRTsk";

    public static String getIndexerSyncUrl() {
        return INDEXER_SYNC_URL;
    }

    @Value("${indexer.sync.ips}")
    public void setIndexerSyncUrl(String indexerSyncUrl) {
        this.INDEXER_SYNC_URL = indexerSyncUrl;
    }

    public static String getIndexerCreateallUrl() {
        return INDEXER_CREATEALL_URL;
    }

    @Value("${indexer.createall.url}")
    public void setIndexerCreateallUrl(String indexerCreateallUrl) {
        this.INDEXER_CREATEALL_URL = indexerCreateallUrl;
    }

    public static String getIndexerCreateUrl() {
        return INDEXER_CREATE_URL;
    }

    @Value("${indexer.create.url}")
    public void setIndexerCreateUrl(String indexerCreateUrl) {
        this.INDEXER_CREATE_URL = indexerCreateUrl;
    }

    public static String getIndexerUpdateUrl() {
        return INDEXER_UPDATE_URL;
    }

    @Value("${indexer.update.url}")
    public void setIndexerUpdateUrl(String indexerUpdateUrl) {
        this.INDEXER_UPDATE_URL = indexerUpdateUrl;
    }

    public static final String SR_SK = "SR_sk";
    public static final String SRT_SK = "SRT_sk";
    public static final String FROM = "From";
    public static final String TO = "To";
    public static final String LUCENE_TO = "TO";
    public static final String CC = "Cc";
    public static final String SUBJECT = "Subject";
    public static final String MAIL_BODY = "MailBody";
    public static final String EMAIL_SK = "Email_sk";
    public static final String SREMAIL_SK = "SREmail_sk";
    public static final String INCEMAIL_SK = "INCEmail_sk";
    public static final String MAILID = "MailID";
    public static final String INC_NUMBER = "INCNumber";
    public static final String SRT_NUMBER = "SRTNumber";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Constants from WhooshController
    public static final String CREATE_ALL_INDEXERS_PATH = "/CreateAllIndexers";
    public static final String CREATE_INDEXER_PATH = "/CreateIndexer";
    public static final String UPDATE_INDEXER_PATH = "/UpdateIndexer";
    public static final String INTERRUPTED = ":Interrupted";
    public static final String CREATED = ":Created";
    public static final String UPDATED = ":Updated";
    public static final String FAILED_BECAUSE = ":Failed because ";
    public static final String WHERE = " WHERE ";

    //Constants for update report

    public static final String CASE = "Case";
    public static final String REQUEST = "Request";
    public static final String PARENT_CASE = "ParentCase";
    public static final String DATE_TIME_RESOLVED = "DateTimeResolved";
    public static final String DATE_TIME_CLOSED = "DateTimeClosed";
    public static final String LAST_MODIFIED_BY = "LastModifiedBy";
    public static final String LAST_MODIFIED_DATE = "LastModifiedDate";
    public static final String ZSUBMITTER_GROUP = "zSubmitterGroup";
    public static final String ZASSIGNED_GROUP = "zAssignedGroup";
    public static final String ZASSIGN_GROUP_ID = "zAssignGroupID";
    public static final String ZASSIGN_LOGIN = "zAssignLogin";
    public static final String ASSIGN_GRP_CHG = "AssignGrpChg";
    public static final String SUPPORT_PERSON = "SupportPerson";
    public static final String GROUPING = "Grouping";
    public static final String GROUP_NOTIFY_METHOD = "GroupNotifyMethod";
    public static final String APPROVAL_EMAIL = "ApprovalEmail";
    public static final String ASSET_TAG_REQUIRED = "AssetTagRequired";
    public static final String SECURITY_ACCESS = "SecurityAccess";
    public static final String AUTO_CLOSE = "AutoClose";
    public static final String GROUP_EMAIL_ADDRESS = "GroupEmailAddress";
    public static final String REQUIRE_TIME_SPENT = "RequireTimeSpent";
    public static final String GROUP = "Group";

    public static final String RE_OPENED = "ReOpened";
    public static final String DATE_TIME_REOPENED = "DateTimeReopened";
    public static final String TOTAL_TIME_MIN = "TotalTimeMin";
    public static final String TIME_SPENT_DATE_TIME = "TimeSpentDateTime";

    public static final String ZASSIGNED_TO_WRONG_GROUP = "zAssignedToWrongGroup";
    public static final String ZASSIGNED_TO_WRONG_GROUP_LOG = "zAssignedToWrongGroupLog";
    public static final String BUT_COULD_HAVE_BEEN_RESOLVED_YES = "ButCouldHaveBeenResolvedYes";
    public static final String ZCOULD_RESOLVED_BY_HELPDESK = "zCouldResolvedByHelpDesk";

    public static final String TICKET_UPDATE_TYPE = "TicketUpdateType";
    public static final String ACD_CALL = "ACDCall";
    public static final String ACD = "ACD";
    public static final String ZAPPROVAL_PERSON_GROUP = "zApprovalPersonGroup";
    public static final String ZAPPROVAL_STATUS = "zApprovalStatus";
    public static final String MODIFIED_DATE = "ModifiedDate";
    public static final String COMPLETE_DATE_TIME = "CompleteDateTime";
    public static final String PARENT_TICKET = "ParentTicket";
    public static final String VOID_USER = "VoidUser";
    public static final String VOID_TIME = "VoidTime";

    //Constants for secureAnswers and response
    public static final String QUESTION_SK = "Question_sk";
    public static final String QUESTION_CODE = "QuestionCode";
    public static final String QUESTION = "Question";
    public static final String PIN = "Pin";
    public static final String DATE_OF_BIRTH = "DateOfBirth";
    public static final String ANSWER = "Answer";
    public static final String ATTRIBUTE_1 = "Attribute1";
    public static final String ATTRIBUTE_2 = "Attribute2";
    public static final String ATTRIBUTE_3 = "Attribute3";
    public static final String ATTRIBUTE_4 = "Attribute4";
    public static final String RDY_QUERIES_SK = "RDYQueries_sk";
    public static final String RDY_QUERY_SK = "RDYQuery_sk";
    public static final String RDY_ROOTCAUSE_SK = "RDYRootCause_sk";
    public static final String JSON_QUESTION = "JSONQuestion";
    public static final String JSON_RESPONSE = "JSONResponse";
    public static final String JSON_ATTRIBUTE = "JSONAttribute";
    public static final String FROM_USER_SK = "FromUser_sk";
    public static final String RESPONSE_SK = "Response_sk";
    public static final String COM_RESPONSES = "ComResponses";
    public static final String CONTROL_SK = "Control_sk";

    // AttributesConstants

    public static final String ATTRIBUTE_5 = "Attribute5";
    public static final String ATTRIBUTE_6 = "Attribute6";
    public static final String ATTRIBUTE_7 = "Attribute7";
    public static final String ATTRIBUTE_8 = "Attribute8";
    public static final String ATTRIBUTE_9 = "Attribute9";
    public static final String ATTRIBUTE_10 = "Attribute10";
    public static final String ATTRIBUTE_11 = "Attribute11";
    public static final String ATTRIBUTE_12 = "Attribute12";
    public static final String ATTRIBUTE_13 = "Attribute13";
    public static final String ATTRIBUTE_14 = "Attribute14";
    public static final String ATTRIBUTE_15 = "Attribute15";
    public static final String ATTRIBUTE_16 = "Attribute16";
    public static final String ATTRIBUTE_17 = "Attribute17";
    public static final String ATTRIBUTE_18 = "Attribute18";
    public static final String ATTRIBUTE_19 = "Attribute19";
    public static final String ATTRIBUTE_20 = "Attribute20";

    public static final String SF_PROCESS_ID = "SFProcessID";
    public static final String SF_FORM_ID = "SFFormID";
    public static final String SF_SUBMISSION_ID = "SFSubmissionID";
    public static final String IAM_ACTION = "IAMAction";
    public static final String AUX_CONTACT = "AuxContact";
    public static final String AUXCONTACT = "AUXContact";


    //Constants for KBArticle

    public static final String ARTICLE_LOG_TYPE = "ArticleLogType";
    public static final String ARTICLE_METADATA_SK = "ArticleMetadata_sk";
    public static final String ARTICLE_SK = "Article_sk";
    public static final String SEARCH_QUERY = "SearchQuery";
    public static final String SEARCH_TIME = "SearchTime";
    public static final String RESULTS_RETURNED = "ResultsReturned";
    public static final String ARTICLE_LOG_SK = "ArticleLog_sk";
    public static final String TICKET_MAPPING_SK = "TicketMapping_sk";
    public static final String HANGUP_SK = "HangUp_sk";
    public static final String KB_TICKET_MAPPING = "KBTicketMapping";

    //Constants for KBArticle_AR

    public static final String LOG_TYPE = "LogType";
    public static final String KB_ARTICLE = "KBArticle";
    public static final String KB_ID_AR = "kb_id";
    public static final String SEARCH_QUERY_AR = "search_query";
    public static final String SEARCH_TIME_AR = "search_time";
    public static final String RESULTS_RETURNED_AR = "results_returned";
    public static final String CLIENT_AR = "client";
    public static final String TICKET_TYPE_AR = "ticket_type";
    public static final String TITLE_AR = "title";
    public static final String CREATE_DATE_AR = "create_date";


    public static final String MASTERTYPE_MAPPING = "MasterTypeMapping";
    public static final String KB_ARTICLE_LOG = "KBArticleLog";
    public static final String ROOT_CAUSE = "RootCause";
    public static final String MASTER_TYPE = "MasterType";
    public static final String MASTER_TYPE_SK = "MasterType_sk";
    public static final String MASTERTYPE_MAPPING_SK = "MasterTypeMapping_sk";
    public static final String SUBMITTER_ID = "Submitter_ID";
    public static final String FrontEndMessage_sk = "FrontEndMessage_sk";
    public static final String Object_sk = "Object_sk";
    public static final String Message = "Message";
    public static final String Start = "Start";
    public static final String End = "End";
    public static final String Approver_sk = "Approver_sk";
    public static final String ApproverComments = "ApproverComments";
    public static final String Status_sk = "Status_sk";
    public static final String IsPublished = "IsPublished";
    public static final String CreatedBy = "CreatedBy";
    public static final String CreatedOn = "CreatedOn";
    public static final String ModifiedBy = "ModifiedBy";
    public static final String ModifiedOn = "ModifiedOn";
    public static final String OnCallUser = "OnCallUser";

    //Worflow Constants
    public static final String WF_INCIDENT = "incident";
    public static final String WF_SR = "servicerequest";
    public static final String WF_SRT = "srtask";
    public static final String WF_VALIDATE = "validate";
    public static final String WF_INITIATE = "initiate";
    public static final String WF_SUBMIT = "submit";
    public static final String TICKETINFOAPI_RESULT = "TicketInfoAPIResult";

    public static final String DATATYPE_INTEGER = "INTEGER";
    public static final String INTEGER_DEFAULT_VALUE = "-999999999";
    public static final String DATATYPE_VARCHAR = "VARCHAR";
    public static final String DATATYPE_CHAR = "CHAR";
    public static final String DATATYPE_DATETIME = "DATETIME";
    public static final String DATETIME_DEFAULT_VALUE = "1900-01-01 00:00:00";
    public static final String DATATYPE_DATE = "DATE";
    public static final String DATE_DEFAULT_VALUE = "1900-01-01";
    public static final String DATATYPE_BIT = "BIT";
    public static final String BIT_DEFAULT_VALUE = "0";
    public static final String DATATYPE_FLOAT = "FLOAT";
    public static final String FLOAT_DEFAULT_VALUE = "-999999999.0";
    public static final String DATATYPE_DECIMAL = "DECIMAL";
    public static final String DECIMAL_DEFAULT_VALUE = "-999999999.0";
    public static final String DATATYPE_BIGINT = "BIGINT";
    public static final String DATATYPE_TIME = "TIME";
    public static final String TIME_DEFAULT_VALUE = "00:00:00";
    public static final String BIGINT_DEFAULT_VALUE = "-999999999";
    public static final String TASK_SK = "Task_sk";
    public static final String TASK_DETAILS_CODE = "TaskDetailsCode";
    public static final String IS_AUTO_ASSIGN = "IsAutoAssign";
    public static final String AUTO_ACK_AFTER_ASSIGNED = "AutoAckAfterAssigned";
    public static final String REMOVE_TASK_CODE = "RemoveTaskCode";
    public static final String IS_IAM = "IsIAM";
    public static final String TASK_CODE = "TaskCode";
    public static final String TASK_GROUP_SK = "TaskGroup_sk";
    public static final String TASK_GROUP_CODE = "TaskGroupCode";
    public static final String TASK_GROUP = "TaskGroup";
    public static final String AUTO_CLOSE_SK = "AutoClose_sk";
    public static final String LDAP_DETAILS_SK = "LDAP_Details_sk";
    public static final String BASE = "Base";
    public static final String CONN_NAME = "ConnName";
    public static final String DOMAIN = "Domain";
    public static final String FILTER = "Filter";
    public static final String HOST = "Host";
    public static final String PORT = "Port";
    public static final String SEARCHDN = "Searchdn";
    public static final String USER_NAME = "UserName";
    public static final String POST_TEXT = "PostText";
    public static final String PRE_TEXT = "PreText";
    public static final String SSO = "SSO";
    public static final String CITY_CODE = "CityCode";
    public static final String CITY = "City";

    //KBArticle

    public static final String KB_ARTICLE_ID = "KBArticleID";
    public static final String ARTICLE_TITLE = "ArticleTitle";
    public static final String ARTILCE_KEYWORDS = "ArticleKeywords";
    public static final String PHRASE_HISTORY = "PhraseHistory";
    public static final String TYPE_AHEAD_CATEGORY = "TypeAheadCategory";
    public static final String TYPE_AHEAD_CATEGORY1 = "TypeAHeadCategory";
    public static final String TYPE_AHEAD_CATEGORIES_SK = "TypeAHeadCategories_sk";
    public static final String ARTICLE_OWNER_SK = "ArticleOwner_sk";
    public static final String EXPIRY_DATE = "ExpiryDate";
    public static final String LOCK_STATUS = "LockStatus";
    public static final String FLR = "FLR";
    public static final String ARTICLE_TYPE_SK = "ArticleType_sk";
    public static final String CONTENT_OWNER = "ContentOwner";
    public static final String AUTHOR = "Author";
    public static final String DOC_CONTENT_TYPE_SK = "DocContentType_sk";
    public static final String CONTENT_TYPE_SK = "ContentType_sk";
    public static final String CONTENT_TYPE = "ContentType";
    public static final String TYPE_AHEAD_PHRASE = "TypeAheadPhrase";
    public static final String TYPE_AHEAD_PHRASES = "TypeAheadPhrases";
    public static final String USER_LOGIN_ID = "UserLoginID";
    public static final String RDY_MAP = "RDYMap";
    public static final String ArchivedPath = "ArchivedPath";


    // ChangeRequest Constants

    public static final String SCHEDULE_SK = "Schedule_sk";
    public static final String REQUESTER_SK = "Requester_sk";
    public static final String REQUESTER_FULL_NAME = "RequesterFullName";
    public static final String PARENT_SK = "Parent_sk";
    public static final String REQUESTERGROUP_SK = "RequesterGroup_sk";
    public static final String REQUESTERGROUPNAME = "RequesterGroupName";
    public static final String REQUEST_TYPE = "RequestType";
    public static final String RISK_LEVEL = "RiskLevel";
    public static final String REQUESTED_ON = "RequestedOn";
    public static final String DURATION = "Duration";
    public static final String CHANGE_REASON_SK = "ChangeReason_sk";
    public static final String CHANGE_REASON = "ChangeReason";
    public static final String PLATFORM_SK = "Platform_sk";
    public static final String PLATFORM_CODE = "PlatformCode";
    public static final String PLATFORM_NAME = "PlatformName";
    public static final String PLATFORM_DESC = "PlatformDescription";
    public static final String CR_CATEGORY_SK = "CRCategory_sk";
    public static final String CR_CATEGORY_CODE = "CRCategoryCode";
    public static final String CR_CATEGORY_NAME = "CRCategoryName";
    public static final String CR_CATEGORY_DESC = "CRCategoryDescription";
    public static final String CR_SYSTEM_SK = "CRSystem_sk";
    public static final String CR_SYSTEM_CODE = "CRSystemCode";
    public static final String SYSTEM = "System";
    public static final String CR_SYSTEMS = "CRSystems";
    public static final String QUESTION_DATA = "QuestionData";
    public static final String SYSTEM_DESC = "SystemDescription";
    public static final String CHANGE_TYPE_SK = "ChangeType_sk";
    public static final String CHANGE_TYPE_CODE = "ChangeTypeCode";
    public static final String CHANGE_TYPE_NAME = "ChangeTypeName";
    public static final String CHANGE_TYPE = "ChangeType";
    public static final String CHANGE_TYPE_DESC = "ChangeTypeDescription";
    public static final String CAB_MEMEBER_SK = "CABMember_sk";
    public static final String SCHEDULED_ON = "ScheduledOn";
    public static final String ESTIMATED_DOWN_TIME = "EstimatedDownTime";
    public static final String ESTIMATED_BACKOUT_TIME = "EstimatedBackoutTime";
    public static final String ACTUAL_DOWNTIME = "ActualDownTime";
    public static final String EXTERNAL_SYS_ID = "ExternalSysID";
    public static final String IS_DEFAULT = "IsDefault";
    public static final String IS_DEFAULT_GROUP = "IsDefaultGroup";
    public static final String IS_NOTIFY_GROUP_TICKET = "IsNotifyGroupTicket";
    public static final String IMPLEMENTATION_STATUS = "ImplementationStatus";
    public static final String IMPLEMENTATION_STATUS_SK = "ImplementationStatus_sk";
    public static final String RESULTS = "Results";
    public static final String CANCELLED_ON = "CancelledOn";
    public static final String WORKNOTES = "WorkNotes";
    public static final String CR_WORKNOTES = "CRWorkNotes";
    public static final String CR_SK = "CR_sk";
    public static final String CRT_SK = "CRT_sk";
    public static final String PARENT_CRT_SK = "ParentCRT_sk";
    public static final String TASK_DETAILS_SK = "TaskDetail_sk";
    public static final String TASK_ASSIGNMENT_GROUP_SK = "TaskAssignmentGroup_sk";
    public static final String TASK_DETAILS_SK1 = "TaskDetails_sk";
    public static final String TASK_DESCRIPTION = "TaskDescription";
    public static final String LOVVALUES_SK = "LOVValues_sk";
    public static final String LOVNAME_SK = "LOVName_sk";
    public static final String LABEL = "Label";
    public static final String REFERENCE_GROUP = "ReferenceGroup";
    public static final String LOVFUNCTION = "LovFunction";
    public static final String RISK_LEVEL_SK = "RiskLevel_sk";
    public static final String RISK_LEVEL_CODE = "RiskLevelCode";
    public static final String RISK_LEVEL_NAME = "RiskLevelName";
    public static final String RISK_LEVEL_DESC = "RiskLevelDescription";
    public static final String ORDER = "Order";
    public static final String RANGE_START = "RangeStart";
    public static final String RANGE_END = "RangeEnd";
    public static final String TASKTYPE_SK = "TaskType_sk";
    public static final String TASKTYPE_CODE = "TaskTypeCode";
    public static final String TASKTYPE_NAME = "TaskTypeName";
    public static final String TASKTYPE_DESC = "TaskTypeDescription";
    public static final String REQUEST_TYPE_SK = "RequestType_sk";
    public static final String ACTUAL_START_TIME = "ActualStartTime";
    public static final String ACTUAL_END_TIME = "ActualEndTime";
    public static final String DESIGN_BOARD_SEQUENCE = "DesignBoardSequence";
    public static final String DESIGN_BOARD_TASK_TYPE_SK = "DesignBoardTaskType_sk";
    public static final String IS_TEMPLATE = "IsTemplate";
    public static final String CHANGE_NAME = "ChangeName";
    public static final String PROJECT_SK = "Project_sk";
    public static final String PROJECT_CODE = "ProjectCode";
    public static final String PROJECT_NAME = "ProjectName";
    public static final String PERIODIC = "Periodic";
    public static final String MAP_PATH = "MapPath";
    public static final String MAP_JSON = "MapJSON";
    public static final String DISCUSSION_BOARD_JSON = "DiscussionBoardJSON";
    public static final String EST_CRITICAL_USER_DOWN_TIME = "EstCriticalUserDownTime";
    public static final String EST_IMPACTED_USERS = "EstImpactedUsers";
    public static final String CALCULATED_RISK_LEVE_SK = "CalculatedRiskLevel_sk";
    public static final String OVERRIDE = "Override";
    public static final String OVERRIDE_REASON = "OverrideReason";
    public static final String CHANGE_OWNER_SK = "ChangeOwner_sk";
    public static final String INSTALL_ON = "InstallOn";
    public static final String TASK_TYPE = "TaskType";
    public static final String TASK_TYPE_SK = "TaskType_sk";
    public static final String CLIENT_VENDOR_SK = "ClientVendor_sk";
    public static final String VENDOR_TICKET_NUMBER = "VendorTicketNumber";
    public static final String COM_REFRENCES = "ComReferences";
    public static final String COM_PERMISSIONS = "ComPermissions";
    public static final String ESTIMATED_TIME = "EstimatedTime";
    public static final String ReadinessRemarks = "ReadinessRemarks";
    public static final String CUSTOM_REFERNCE = "CustomReference";
    public static final String CR_ASSESTS = "CRAssets";
    public static final String CR_GROUPS = "CRGroups";
    public static final String CR_BUILDINGS = "CRBuildings";
    public static final String CR_LOCATIONS = "CRLocations";
    public static final String CR_LOCATION = "CRLocation";
    public static final String CR_TASK_DETAILS = "CRTaskDetails";
    public static final String CR_APPROVAL_DETAILS = "CRApprovalDetails";
    public static final String ASSET_NAME = "AssetName";
    public static final String ASSET_TYPE = "AssetType";
    public static final String BUILDING_TYPE = "BuildingType";
    public static final String CR_LOCATION_SK = "CRLocation_sk";
    public static final String ALERT_ACC_REL_SK = "AlertAccRel_sk";
    public static final String ALERT_SK = "Alert_sk";
    public static final String ALERT_NAME = "AlertName";
    public static final String ALERT_CODE = "AlertCode";
    public static final String SLM_DEFINTION_SK = "SLMDefinition_sk";
    public static final String SLM_DEFINTION_CODE = "SLMDefinitionCode";
    public static final String SLM_DEFINTION = "SLMDefinition";
    public static final String GOAL_TYPE_SK = "GoalType_sk";
    public static final String GOAL_TYPE_CODE = "GoalTypeCode";
    public static final String GOAL_TYPE = "GoalType";
    public static final String EFFECTIVE_FROM = "EffectiveFrom";
    public static final String EFFECTIVE_TO = "EffectiveTo";
    public static final String GOAL_HOURS = "GoalHours";
    public static final String GOAL_MINUTES = "GoalMinutes";
    public static final String WARNING_PERCENTAGE = "WarningPercentage";
    public static final String IS_RESTART_DEFINITION = "IsRestartDefinition";
    public static final String IS_REOPEN_DEFINITION = "IsReopenDefinition";
    public static final String USE_BUSINESS_HOURS = "UseBusinessHours";
    public static final String SLA_NOTES = "SLANotes";
    public static final String TERMS_AND_CONDITIONS = "TermsAndConditions";
    public static final String RUN_WHILE = "RunWhile";
    public static final String PAUSE_WHEN = "PauseWhen";
    public static final String STOP_WHEN = "StopWhen";
    public static final String NOTIFICATION_TEMPLATE = "NotificationTemplate";
    public static final String CR_LOCATION_CODE = "CRLocationCode";
    public static final String CR_LOCATION_TYPE = "CRLocationType";
    public static final String DELETE_CR_ATTACHMENT_SK = "DeleteCRAttachment_sk";
    public static final String DELETE_CR_COMREFRENCES_SK = "DeleteCRComReferences_sk";
    public static final String CR_ASSET_SK = "CRAsset_sk";
    public static final String CR_GROUP_SK = "CRGroup_sk";
    public static final String CR_BUILDING_SK = "CRBuilding_sk";
    public static final String LOCATION_SK = "Location_sk";
    public static final String RISK_SK = "Risk_sk";
    public static final String RISK_FACTOR = "RiskFactor";
    public static final String RISK_ASSESSMENT_SK = "RiskAssessment_sk";
    public static final String MIGRATION_STEPS = "MitigationSteps";
    public static final String RISK_OWNER_SK = "RiskOwner_sk";
    public static final String RISKS = "Risks";
    public static final String IMPACTED = "Impacted";
    public static final String START_ON = "StartOn";
    public static final String END_ON = "EndOn";
    public static final String MEETING_NAME = "MeetingName";
    public static final String IS_START_NODE = "IsStartNode";
    public static final String PURPOSE = "Purpose";
    public static final String LOCATION = "Location";
    public static final String FOLLOW_UP = "FollowUp";
    public static final String FOLLOW_UP_START_DATE = "FollowUpStartDate";
    public static final String FOLLOW_UP_END_DATE = "FollowUpEndDate";
    public static final String DELETE_FOLLOW_UP_SCHEDULE_SK = "DeleteFollowUpSchedule_sk";
    public static final String PREDECESSOR_ENTITY_NAME = "PredecessorEntityName";
    public static final String PREDECESSOR_ENTITY_TYPE = "PredecessorEntityType";
    public static final String PREDECESSOR_ENTITY_SK = "PredecessorEntity_sk";
    public static final String RDY_MAPPING_SLA_SK = "RDYMappingSLA_sk";
    public static final String X_CRITERIA_1 = "XCriteria1";
    public static final String X_CRITERIA_2 = "XCriteria2";
    public static final String X_CRITERIA_3 = "XCriteria3";
    public static final String X_CRITERIA_4 = "XCriteria4";
    public static final String X_CRITERIA_5 = "XCriteria5";
    public static final String X_BUSINESS_FUNCTION_SK = "XBusinessFunction_sk";
    public static final String X_SEVERITY_SK = "XSeverity_sk";
    public static final String X_IMPACT_SK = "XImpact_sk";
    public static final String X_PRIORITY_SK = "XPriority_sk";
    public static final String X_IS_VIP = "XIsVIP";
    public static final String X_TITLE_SK = "XTitle_sk";
    public static final String Y_SLM_DEFINITION_SK = "YSLMDefinition_sk";
    public static final String Y_VALUE1 = "YValue1";
    public static final String Y_VALUE2 = "YValue2";
    public static final String Y_VALUE3 = "YValue3";
    public static final String Y_VALUE4 = "YValue4";
    public static final String Y_VALUE5 = "YValue5";
    public static final String X_MASTER_TYPE1_SK = "XMasterType1_sk";
    public static final String X_MASTER_TYPE2_SK = "XMasterType2_sk";
    public static final String X_MASTER_TYPE3_SK = "XMasterType3_sk";
    public static final String X_BUILDING_SK = "XBuilding_sk";
    public static final String X_DEPARTMENT_SK = "XDepartment_sk";
    public static final String X_CTI_SK = "XCTI_sk";
    public static final String X_PROCESS_CODE = "XProcessCode";
    public static final String X_USER_TYPE_SK = "XUserType_sk";
    public static final String Y_CTI_SK = "YCTI_sk";
    public static final String Y_ACCOUNT_GROUP_SK = "YAccountGroup_sk";
    public static final String Y_ASSIGED_INDIVIDUAL_SK = "YAssignedIndividual_sk";
    public static final String Y_PRIORITY_SK = "YPriority_sk";
    public static final String Y_SEVERITY_SK = "YSeverity_sk";
    public static final String Y_IMPACT_SK = "YImpact_sk";
    public static final String Y_PRIMARY_APPROVER_SK = "YPrimaryApprover_sk";
    public static final String Y_SECONDARY_APPROVER_SK = "YSecondaryApprover_sk";
    public static final String X_PRIMARY_APPROVER_SK = "XPrimaryApprover_sk";
    public static final String X_CATEGORY_SK = "xCategory_sk";
    public static final String X_TYPE_SK = "xType_sk";
    public static final String X_ITEM_SK = "xItem_sk";
    public static final String XCATEGORY_SK = "XCategory_sk";
    public static final String XTYPE_SK = "XType_sk";
    public static final String XITEM_SK = "XItem_sk";
    public static final String Y_CATEGORY_SK = "YCategory_sk";
    public static final String Y_TYPE_SK = "YType_sk";
    public static final String Y_ITEM_SK = "YItem_sk";
    public static final String Y_CASE_TYPE_SK = "YCaseType_sk";
    public static final String YSR_STATUS_SK = "YSRStatus_sk";
    public static final String Y_INC_STATUS_SK = "YIncStatus_sk";
    public static final String X_ACCOUNT_GROUP_SK = "XAccountGroup_sk";
    public static final String X_ASSIGNED_INDIVIDUAL_SK = "XAssignedIndividual_sk";
    public static final String X_REQUESTER_SK = "XRequester_sk";
    public static final String X_BUS_TIME_DETAIL_SK = "xBusTimeDetail_sk";
    public static final String X_TASK_DETAILS_SK = "xTaskDetails_sk";
    public static final String YAD = "YAD";
    public static final String Y_APPROVAL_CRITERIA_SK = "YApprovalCriteria_sk";
    public static final String Y_SIZE = "YSize";
    public static final String X_ARTICLE_METADATA_SK = "xArticleMetadata_sk";
    public static final String Y_SOURCE_SK = "YSource_sk";
    public static final String Y_QUEUE_SK = "YQueue_sk";
    public static final String X_APPROVAL_CRITERIA_SK = "XApprovalCriteria_sk";
    public static final String SET_TO_AUTO_ACK = "SetToAutoAck";
    public static final String DIRECT_TO_CLOSE = "DirectToClose";
    public static final String X_DESIGNATION_SK = "XDesignation_sk";
    public static final String X_SOURCE_SK = "XSource_sk";
    public static final String DOWN_TIME = "DownTime";
    public static final String EST_START_TIME = "EstStartTime";
    public static final String EST_END_TIME = "EstEndTime";
    public static final String MILE_STONE = "Milestone";
    public static final String TOTAL_EFFORT = "TotalEffort";
    public static final String APPROVAL_CRITERIA_SK = "ApprovalCriteria_sk";
    public static final String APPROVAL_CRITERIA_CODE = "ApprovalCriteriaCode";
    public static final String APPROVAL_CRITERIA1 = "ApprovalCriteria1";
    public static final String APPROVAL_CRITERIA2 = "ApprovalCriteria2";
    public static final String BACKUP_APPROVAL1 = "BackupApproval1";
    public static final String BACKUP_APPROVAL2 = "BackupApproval2";
    public static final String BACKUP_APPROVAL3 = "BackupApproval3";
    public static final String CR_APPROVAL_STATUS = "CRApprovalStatus";
    public static final String CR_APPROVAL_STATUS1 = "CRApprovalStatus1";
    public static final String APPROVAL_STATUS = "ApprovalStatus";
    public static final String CRT_STAKE_HOLDERS = "CRTStakeHolders";
    public static final String CRT_ASSIGNMENTS = "CRTAssignments";
    public static final String DESIGN_BOARD = "DesignBoard";
    public static final String REMARKS = "Remarks";
    public static final String CR_DISCUSSION_POINTS = "CRDiscussionPoints";
    public static final String ACTION_ITEMS = "ActionItems";
    public static final String CR_MEETING_PARTICIPANTS = "CRMeetingParticipants";
    public static final String WORKNOTES_UPDATE_SK = "WorkNotesUpdateType_sk";
    public static final String WORKNOTES_UPDATE_CODE = "WorkNotesUpdateTypeCode";
    public static final String SHOW_QUEUE = "ShowQueue";
    public static final String EMAIL_OPTION_SK = "EMailOption_sk";
    public static final String EMAIL_OPTION_CODE = "EMailOptionCode";
    public static final String EMAIL_OPTION = "EMailOption";
    public static final String CASE_TYPE_CODE = "CaseTypeCode";
    public static final String BOOKMARK_SK = "Bookmark_sk";
    public static final String BOOKMARK = "Bookmark";
    public static final String URL = "URL";
    public static final String IMPACT_SK = "Impact_sk";
    public static final String IMPACT_CODE = "ImpactCode";
    public static final String WEIGHTAGE = "Weightage";
    public static String aTokenValue;
    public static String offlineemailid;
    public static String offline_toemailid;
    public static String email_ticket_applicationcode;
    public static String offline_ticket_applicationcode;
    public static String email_status_code;
    public static String email_Source;

    public static String SERVICENOW_FETCH_SYSTEM_ID;
    public static String SERVICENOW_FETCH_INCIDENT;
    public static String SERVICENOW_FETCH_INCIDENT_DESCRIPTION;
    public static String SERVICENOW_AUTH_HEADER_VALUE;
    public static String CR_TICKET_DEFAULT_URL;


    public static String getCrTicketDefaultUrl() {
        return CR_TICKET_DEFAULT_URL;
    }

    @Value("${cr.ticketurl.default}")
    public void setCrTicketDefaultUrl(String crTicketDefaultUrl) {
        this.CR_TICKET_DEFAULT_URL = crTicketDefaultUrl;
    }

    public static String getServicenowFetchSystemId() {
        return SERVICENOW_FETCH_SYSTEM_ID;
    }

    @Value("${SERVICENOW_FETCH_SYSTEM_ID}")
    public void setServicenowFetchSystemId(String servicenowFetchSystemId) {
        this.SERVICENOW_FETCH_SYSTEM_ID = servicenowFetchSystemId;
    }

    public static String getServicenowFetchIncident() {
        return SERVICENOW_FETCH_INCIDENT;
    }

    @Value("${SERVICENOW_FETCH_INCIDENT}")
    public void setServicenowFetchIncident(String servicenowFetchIncident) {
        this.SERVICENOW_FETCH_INCIDENT = servicenowFetchIncident;
    }

    public static String getServicenowFetchIncidentDescription() {
        return SERVICENOW_FETCH_INCIDENT_DESCRIPTION;
    }

    @Value("${SERVICENOW_FETCH_INCIDENT_DESCRIPTION}")
    public void setServicenowFetchIncidentDescription(String servicenowFetchIncidentDescription) {
        this.SERVICENOW_FETCH_INCIDENT_DESCRIPTION = servicenowFetchIncidentDescription;
    }

    public static String getServicenowAuthHeaderValue() {
        return SERVICENOW_AUTH_HEADER_VALUE;
    }

    @Value("${SERVICENOW_AUTH_HEADER_VALUE}")
    public void setServicenowAuthHeaderValue(String servicenowAuthHeaderValue) {
        this.SERVICENOW_AUTH_HEADER_VALUE = servicenowAuthHeaderValue;
    }

    public static String getEmail_Source() {
        return email_Source;
    }

    @Value("${email.source}")
    public void setEmail_Source(String email_Source) {
        this.email_Source = email_Source;
    }

    public static String getaTokenValue() {
        return aTokenValue;
    }

    public static String getEmail_status_code() {
        return email_status_code;
    }

    @Value("${email.statuscode}")
    public void setEmail_status_code(String email_status_code) {
        this.email_status_code = email_status_code;
    }

    public static String getOffline_toemailid() {
        return offline_toemailid;
    }

    @Value("${offline.ticket.toemailid}")
    public void setOffline_toemailid(String offline_toemailid) {
        this.offline_toemailid = offline_toemailid;
    }

    public static String getOfflineemailid() {
        return offlineemailid;
    }

    @Value("${offline.ticket.emailid}")
    public void setOfflineemailid(String offlineemailid) {
        this.offlineemailid = offlineemailid;
    }

    @Value("${ctsspi.hash}")
    public void setaTokenValue(String aTokenValue) {
        this.aTokenValue = aTokenValue;
    }

    public static String getEmail_ticket_applicationcode() {
        return email_ticket_applicationcode;
    }

    @Value("${email.ticket.applicationcode}")
    public void setEmail_ticket_applicationcode(String email_ticket_applicationcode) {
        this.email_ticket_applicationcode = email_ticket_applicationcode;
    }

    public static String getOffline_ticket_applicationcode() {
        return offline_ticket_applicationcode;
    }

    @Value("${offline.ticket.applicationcode}")
    public void setOffline_ticket_applicationcode(String offline_ticket_applicationcode) {
        this.offline_ticket_applicationcode = offline_ticket_applicationcode;
    }

    public static final List<String> getRequiredFieldsToCreateTicket(String busFunction) {
        if (ticketCreateReqFields.size() > 0) {
            ticketCreateReqFields = new ArrayList<>();
        }

        ticketCreateReqFields.add("AssignedGroup");
        ticketCreateReqFields.add("Account");
        ticketCreateReqFields.add("Status");
        ticketCreateReqFields.add("TicketType");
        ticketCreateReqFields.add("Submitter");
        ticketCreateReqFields.add("BusinessFunction");
        ticketCreateReqFields.add("MasterAccount");
        ticketCreateReqFields.add("MasterAccountCode");
        ticketCreateReqFields.add("EngineType");
        ticketCreateReqFields.add("InitiateRFLOW");
        ticketCreateReqFields.add("ApplicationCode");


        if (busFunction.equals("SRT")) {
            ticketCreateReqFields.add("ServiceRequest_sk");
            ticketCreateReqFields.add("TaskName");
            ticketCreateReqFields.add("TaskDetails");
            ticketCreateReqFields.add("TaskSequence");
        } else {
            /* ticketCreateReqFields.add("CTI");*/
            ticketCreateReqFields.add("Summary");
            ticketCreateReqFields.add("Description");
            ticketCreateReqFields.add("Raisedby");
            /*ticketCreateReqFields.add("Building");*/
            ticketCreateReqFields.add("Source");
            ticketCreateReqFields.add("CaseType");
            ticketCreateReqFields.add("IsAlternateUser");
            /*ticketCreateReqFields.add("Department");*/
            /* ticketCreateReqFields.add("Floor");*/
            ticketCreateReqFields.add("Priority");
            ticketCreateReqFields.add("Queue");
        }

        return ticketCreateReqFields;
    }

    public static void setRequiredFieldsToCreateTicket(List<String> ticketCreateReqFields) {
        SFInterfaceConstants.ticketCreateReqFields = ticketCreateReqFields;
    }

    public static final List<String> getRequiredFieldsToCreateTicketDetails(String busFunction) {
        if (ticketDetailsCreateReqFields.size() > 0) {
            ticketDetailsCreateReqFields = new ArrayList<>();
        }

        ticketDetailsCreateReqFields.add("TicketSK");
        ticketDetailsCreateReqFields.add("Submitter");
        ticketDetailsCreateReqFields.add("EngineType");
        ticketDetailsCreateReqFields.add("InitiateRFLOW");
        ticketDetailsCreateReqFields.add("ApplicationCode");
        ticketDetailsCreateReqFields.add("MasterAccountCode");

        if (!busFunction.equals("SRT")) {
            ticketDetailsCreateReqFields.add("IsAlternateUser");
        }
        return ticketDetailsCreateReqFields;
    }

    public static void setRequiredFieldsToCreateTicketDetails(List<String> ticketCreateReqFields) {
        SFInterfaceConstants.ticketCreateReqFields = ticketCreateReqFields;
    }

    public static final List<String> getRequiredFieldsToUpdateTicketDetails(String busFunction) {
        if (ticketDetailsUpdateReqFields.size() > 0) {
            ticketDetailsUpdateReqFields = new ArrayList<>();
        }

        ticketDetailsUpdateReqFields.add("EngineType");
        ticketDetailsUpdateReqFields.add("InitiateRFLOW");
        ticketDetailsUpdateReqFields.add("ApplicationCode");
        ticketDetailsUpdateReqFields.add("TicketSK");
        ticketDetailsUpdateReqFields.add("TicketNumber");
        ticketDetailsUpdateReqFields.add("ModifiedBy");
        ticketDetailsUpdateReqFields.add("MasterAccountCode");

        return ticketDetailsUpdateReqFields;
    }

    public static void setRequiredFieldsToUpdateTicketDetails(List<String> ticketCreateReqFields) {
        SFInterfaceConstants.ticketCreateReqFields = ticketCreateReqFields;
    }

    public static final List<String> getRequiredFieldsToUpdateTicket(String busFunction) {
        if (ticketUpdateReqFields.size() > 0) {
            ticketUpdateReqFields = new ArrayList<>();
        }
        ticketUpdateReqFields.add("EngineType");
        ticketUpdateReqFields.add("InitiateRFLOW");
        ticketUpdateReqFields.add("ApplicationCode");
        ticketUpdateReqFields.add("TicketNumber");
        ticketUpdateReqFields.add("TicketSK");
        ticketUpdateReqFields.add("MasterAccountCode");
        ticketUpdateReqFields.add("ModifiedBy");

        return ticketUpdateReqFields;
    }

    public static void setRequiredFieldsToUpdateTicket(List<String> ticketUpdateReqFields) {
        SFInterfaceConstants.ticketUpdateReqFields = ticketUpdateReqFields;
    }

    public static List<String> getCreateChangeRequestReqFields(String busFunction) {
        if (createChangeRequestReqFields.size() > 0) {
            createChangeRequestReqFields = new ArrayList<>();
        }
        createChangeRequestReqFields.add("BusinessFunction_sk");
        createChangeRequestReqFields.add("MasterAccount_sk");
        createChangeRequestReqFields.add("MasterAccountCode");
        createChangeRequestReqFields.add("Account_sk");
        // createChangeRequestReqFields.add("EstimatedDownTime");
        createChangeRequestReqFields.add("Status_sk");
        createChangeRequestReqFields.add("CreatedBy");

        /*if (busFunction.equals("CR")) {
            createChangeRequestReqFields.add("Requester_sk");
            createChangeRequestReqFields.add("RequesterGroup_sk");
            createChangeRequestReqFields.add("RequestType");
            createChangeRequestReqFields.add("RiskLevel");
            createChangeRequestReqFields.add("RequestedOn");
            createChangeRequestReqFields.add("ChangeReason_sk");
            createChangeRequestReqFields.add("Platform_sk");
            createChangeRequestReqFields.add("CRCategory_sk");
            createChangeRequestReqFields.add("CRSystem_sk");
            createChangeRequestReqFields.add("ChangeType_sk");
            createChangeRequestReqFields.add("Description");
        }*/
        if (busFunction.equals("CRT")) {
            createChangeRequestReqFields.add("CR_sk");
        }


        return createChangeRequestReqFields;
    }

    public static void setCreateChangeRequestReqFields(List<String> changeRequestReqFields) {
        SFInterfaceConstants.createChangeRequestReqFields = changeRequestReqFields;
    }

    public static List<String> getUpdateChangeRequestReqFields() {
        if (updateChangeRequestReqFields.size() > 0) {
            updateChangeRequestReqFields = new ArrayList<>();
        }
        updateChangeRequestReqFields.add("Ticket_sk");
        updateChangeRequestReqFields.add("TicketNumber");
        updateChangeRequestReqFields.add("ModifiedBy");
        return updateChangeRequestReqFields;
    }

    public static void setUpdateChangeRequestReqFields(List<String> updatechangeRequestReqFields) {
        SFInterfaceConstants.updateChangeRequestReqFields = updatechangeRequestReqFields;
    }

    public static List<String> getCreateChangeRequestDetailsReqFields() {
        if (createChangeRequestDetailsReqFields.size() > 0) {
            createChangeRequestDetailsReqFields = new ArrayList<>();
        }
        createChangeRequestDetailsReqFields.add("Ticket_sk");
        createChangeRequestDetailsReqFields.add("CreatedBy");
        return createChangeRequestDetailsReqFields;
    }

    public static void setcreateChangeRequestDetailsReqFields(List<String> createChangeRequestDetailsReqFields) {
        SFInterfaceConstants.createChangeRequestDetailsReqFields = createChangeRequestDetailsReqFields;
    }

    public static List<String> getUpdateChangeRequestDetailsReqFields() {
        if (updateChangeRequestDetailsReqFields.size() > 0) {
            updateChangeRequestDetailsReqFields = new ArrayList<>();
        }
        updateChangeRequestDetailsReqFields.add("Ticket_sk");
        updateChangeRequestDetailsReqFields.add("ModifiedBy");
        return updateChangeRequestDetailsReqFields;
    }

    public static void setUpdateChangeRequestDetailsReqFields(List<String> updateChangeRequestDetailsReqFields) {
        SFInterfaceConstants.updateChangeRequestDetailsReqFields = updateChangeRequestDetailsReqFields;
    }

    public static List<String> getCreateProfileReqFields() {
        if (createProfileReqFields.size() > 0) {
            createProfileReqFields = new ArrayList<>();
        }
        createProfileReqFields.add("FirstName");
        createProfileReqFields.add("LastName");
        createProfileReqFields.add("Account");
        createProfileReqFields.add("FullName");
        return createProfileReqFields;
    }

    public static void setCreateProfileReqFields(List<String> createProfileReqFields) {
        SFInterfaceConstants.createProfileReqFields = createProfileReqFields;
    }

    public static final Map<String, String> getStatusEntityCode() {
        Map<String, String> statuscode = new HashMap<>();
        statuscode.put("INCIDENTS", "INC");
        statuscode.put("SR", "SR");
        statuscode.put("SRT", "SRT");
        return statuscode;
    }

    public static String getOfflineTicketDefaultSummary() {
        return OFFLINE_TICKET_DEFAULT_SUMMARY;
    }

    @Value("${offline.ticket.default.summary}")
    public void setOfflineTicketDefaultSummary(String offlineTicketDefaultSummary) {
        this.OFFLINE_TICKET_DEFAULT_SUMMARY = offlineTicketDefaultSummary;
    }

    public static String getFileserverURL() {
        return fileserverURL;
    }

    @Value("${ctsspi.fileserver.url}")
    public void setFileserverURL(String fileserverURL) {
        this.fileserverURL = fileserverURL;
    }

    public static String getCrAdminGroupname() {
        return CR_ADMIN_GROUPNAME;
    }

    @Value("${cr.admin.groupname}")
    public void setCrAdminGroupname(String crAdminGroupname) {
        this.CR_ADMIN_GROUPNAME = crAdminGroupname;
    }

    public static String getCr_coordinator_groupname() {
        return cr_coordinator_groupname;
    }

    @Value("${cr.coordinator.groupname}")
    public void setCr_coordinator_groupname(String cr_coordinator_groupname) {
        this.cr_coordinator_groupname = cr_coordinator_groupname;
    }

    public static String getSsp_approval_url() {
        return ssp_approval_url;
    }

    @Value("${ssp.approval.url}")
    public void setSsp_approval_url(String ssp_approval_url) {
        this.ssp_approval_url = ssp_approval_url;
    }

    public static String getDbSspConnectionName() {
        return DB_SSP_CONNECTION_NAME;
    }

    @Value("${db.ssp.connectionname}")
    public void setDbSspConnectionName(String dbSspConnectionName) {
        this.DB_SSP_CONNECTION_NAME = dbSspConnectionName;
    }

    public static String getDbRefConnectionName() {
        return DB_REF_CONNECTION_NAME;
    }

    @Value("${db.ref.connectionname}")
    public void setDbRefConnectionName(String dbRefConnectionName) {
        this.DB_REF_CONNECTION_NAME = dbRefConnectionName;
    }

    public static String getDbReportingConnectionName() {
        return DB_REPORTING_CONNECTION_NAME;
    }

    @Value("${db.reporting.connectionname}")
    public void setDbReportingConnectionName(String dbReportingConnectionName) {
        this.DB_REPORTING_CONNECTION_NAME = dbReportingConnectionName;
    }

    public static String getDbAssetConnectionName() {
        return DB_ASSET_CONNECTION_NAME;
    }

    @Value("${db.asset.connectionname}")
    public void setDbAssetConnectionName(String dbAssetConnectionName) {
        this.DB_ASSET_CONNECTION_NAME = dbAssetConnectionName;
    }


    public static String getDbRefReplicaConnectionName() {
        return DB_REF_REPLICA_CONNECTION_NAME;
    }

    @Value("${db.ref_archive.connectionname}")
    public void setDbRefReplicaConnectionName(String dbRefReplicaConnectionName) {
        this.DB_REF_REPLICA_CONNECTION_NAME = dbRefReplicaConnectionName;
    }


    public static String getDedicatedInstances() {
        return dedicatedInstances;
    }

    @Value("${dedicated.instances}")
    public void setDedicatedInstances(String dedicatedInstances) {
        this.dedicatedInstances = dedicatedInstances;
    }
}
