package com.htc.remedy.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.base.SFInterfaceConnectionBase;
import com.htc.remedy.base.SFInterfaceLoggerBase;
import com.htc.remedy.base.SFInterfaceMessages;
import com.htc.remedy.config.IndexerUtilities;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.core.IndexerIntf;
import com.htc.remedy.core.IndexerUtil;
import com.htc.remedy.core.LucenceImpl;
import com.htc.remedy.domain.IndexerDomain;
import com.htc.remedy.model.IndexWritersModel;
import com.htc.remedy.model.LQLSearch;
import com.htc.remedy.model.SFInterfaceIndexerSyncModel;
import com.htc.remedy.model.User;
import com.htc.remedy.scheduler.IndexerScheduler;
import com.htc.remedy.services.SFInterfaceServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.htc.remedy.constants.SFInterfaceConstants.*;
import static com.htc.remedy.services.SFInterfaceServices.isRequestContainToken;
import static com.htc.remedy.services.SFInterfaceServices.trimToken;

@RestController
@RequestMapping("/whoosh")
public class SFInterfaceWhooshController {

    private static final Logger LOGGER = LogManager.getLogger(SFInterfaceWhooshController.class);

    @Autowired
    IndexerUtil indexerUtil;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    SFInterfaceMessages sfInterfaceMessages;

    /**
     * author - Vivek
     * ModifiedBy - Vani
     * Create All Indexers segment file
     *
     * @return - Returns Indexers status
     */


    @PostMapping(path = CREATE_ALL_INDEXERS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createAllIndexer(HttpServletRequest request) throws Exception {
        JSONObject successObj = new JSONObject();
        Map<String, Object> indexerResult = new HashMap<>(), errorMap = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);

            JdbcTemplate jdbcTemplate = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName());

            List<IndexerDomain> indexerDomains = IndexerUtilities.fetchIndexers(jdbcTemplate);
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERCREATIONSTART"));
            if (request.getParameterMap() != null && (request.getParameterMap().containsKey("Sync") || request.getParameterMap().containsKey("sync"))) {
                jmsTemplate.convertAndSend(INDEXER_SYNC,
                        new SFInterfaceIndexerSyncModel(INDEXER_CREATE_ALL, null, null, user.getClientInstance()));
            }
            indexerResult = IndexerScheduler.createIndexer(indexerDomains, jdbcTemplate, IndexerUtilities.fetchpathbyClientInstance(user.getClientInstance()));
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERCREATIONEND"));
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, null, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, "CreateAllIndexers"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(indexerResult, HttpStatus.OK);
    }
    /* @PostMapping(path = CREATE_ALL_INDEXERS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createAllIndexer(HttpServletRequest request) throws Exception {
        JSONObject successObj = new JSONObject();
        Map<String, Object> indexerResult = new HashMap<>(), errorMap = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);

            JdbcTemplate jdbcTemplate = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName());

            List<IndexerDomain> indexerDomains = IndexerUtilities.fetchIndexers(jdbcTemplate);
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERCREATIONSTART"));
            if (request.getParameterMap() != null && (request.getParameterMap().containsKey("Sync") || request.getParameterMap().containsKey("sync"))) {
                jmsTemplate.convertAndSend(INDEXER_SYNC,
                        new SFInterfaceIndexerSyncModel(INDEXER_CREATE_ALL, null, null, user.getClientInstance()));
            }

            for (IndexerDomain indexerDomain : indexerDomains) {
                try {
                    IndexWritersModel writersModel = SFInterfaceConnectionBase.fetchIndexerWriterModal(user, indexerDomain.getName());
                    if (writersModel == null) {
                        SFInterfaceConnectionBase.updateIndexerModel(indexerDomain.getName(), user);
                        writersModel = SFInterfaceConnectionBase.fetchIndexerWriterModal(user, indexerDomain.getName());
                    }
                    if (writersModel != null) {
                        if (writersModel.isAvailable()) {
                            indexerResult = SFInterfaceBase.createIndexer(writersModel, indexerDomain.getName(), indexerDomain.getQuery(), indexerDomain.getIndexerkeyaliasname()
                                    , SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()));
                            if (indexerResult.size() == NUM_ZERO) {
                                SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + INTERRUPTED);
                                //successObj.addProperty(indexerDomain.getName(), Boolean.FALSE);
                                errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYINDEXER", null, null);
                                successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                                successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                                successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                            } else {
                                if (indexerResult.size() > NUM_ZERO && indexerResult.get(RESPONSE).toString().equalsIgnoreCase(SUCCESS)) {
                                    SFInterfaceLoggerBase.log(LOGGER, Constants.INSERT, indexerDomain.getName(), null, null, null, null, null, "");
                                    SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + CREATED);
                                    successObj.put(indexerDomain.getName(), Boolean.TRUE);
                                } else {
                                    SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + INTERRUPTED);
                                    successObj.put(indexerDomain.getName(), Boolean.FALSE);
                                    successObj.put(ERROR, (String) indexerResult.get(RESPONSE));
                                }
                            }
                        } else {
                            //successObj.put(indexerDomain.getName(), Boolean.FALSE);
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INDEXER.LOCKED", null, null);
                            successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                            successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                            successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                        }
                    } else {
                        //successObj.put(indexerDomain.getName(), Boolean.FALSE);
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INDEXER.BEAN.NOT.AVAILABLE", null, null);
                        successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                        successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                        successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    successObj.put(ERROR, (String) SFInterfaceServices.ISPExceptionHandler(e, null, null).get(ERROR));
                }
            }
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERCREATIONEND"));
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, null, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, "CreateAllIndexers"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(successObj.toMap(), HttpStatus.OK);
    }*/

    /**
     * author - Vivek
     * ModifiedBy - Vani
     * Create Indexer segment file
     *
     * @param indexerName
     * @return - Returns Indexer status
     */

    @PostMapping(path = CREATE_INDEXER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createIndexerByIndexerName(String[] indexerName, HttpServletRequest request) throws IOException {
        JSONObject successObj = new JSONObject();
        Map<String, Object> indexerResult = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();

        int count = NUM_ZERO;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);

            JdbcTemplate jdbcTemplate = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName());

            List<IndexerDomain> indexerDomains = IndexerUtilities.fetchIndexers(jdbcTemplate);
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERCREATIONSTART"));
            if (request.getParameterMap() != null && (request.getParameterMap().containsKey("Sync") || request.getParameterMap().containsKey("sync"))) {
                jmsTemplate.convertAndSend(INDEXER_SYNC,
                        new SFInterfaceIndexerSyncModel(INDEXER_CREATE, indexerName, null, user.getClientInstance()));
            }
            for (IndexerDomain indexerDomain : indexerDomains) {
                if (Arrays.asList(indexerName).contains(indexerDomain.getName())) {
                    count++;
                    try {
                        IndexWritersModel writersModel = SFInterfaceConnectionBase.fetchIndexerWriterModal(user, indexerDomain.getName());
                        if (writersModel == null) {
                            SFInterfaceConnectionBase.updateIndexerModel(indexerDomain.getName(), user);
                            writersModel = SFInterfaceConnectionBase.fetchIndexerWriterModal(user, indexerDomain.getName());
                        }
                        if (writersModel != null) {
                                indexerResult = SFInterfaceBase.createIndexer(writersModel, indexerDomain.getName(), indexerDomain.getQuery(), indexerDomain.getIndexerkeyaliasname()
                                        , SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()));
                                if (indexerResult.size() == NUM_ZERO) {
                                    SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + INTERRUPTED);
                                    //successObj.addProperty(indexerDomain.getName(), Boolean.FALSE);
                                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYINDEXER", null, null);
                                    successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                                    successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                                    successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                                } else {
                                    if (indexerResult.size() > NUM_ZERO && indexerResult.get(RESPONSE).toString().equalsIgnoreCase(SUCCESS)) {
                                        SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + CREATED);
                                        SFInterfaceLoggerBase.log(LOGGER, Constants.INSERT, indexerDomain.getName(), null, null, null, null, null, "");
                                        successObj.put(indexerDomain.getName(), Boolean.TRUE);
                                    } else {
                                        SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + INTERRUPTED);
                                        successObj.put(indexerDomain.getName(), Boolean.FALSE);
                                        successObj.put(ERROR, (String) indexerResult.get(RESPONSE));
                                    }
                                }
                        } else {
                            //successObj.put()(indexerDomain.getName(), Boolean.FALSE);
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INDEXER.BEAN.NOT.AVAILABLE", null, null);
                            successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                            successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                            successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage());
                        successObj.put(ERROR, (String) SFInterfaceServices.ISPExceptionHandler(e, null, null).get(ERROR));
                    }
                }
            }
            if (count == NUM_ZERO) {
                successObj.put(ERROR, indexerName[NUM_ZERO] + " - " + INVALID_INDEXER);
            }
            count = NUM_ZERO;
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERCREATIONEND"));
        } catch (Exception e) {
            SFInterfaceLoggerBase.log(LOGGER, indexerName[NUM_ZERO] + FAILED_BECAUSE + e.getMessage());
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, "CreateIndexer"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(successObj.toMap(), HttpStatus.OK);
    }

    /**
     * @param indexerName
     * @param ticketID
     * @return - Returns Indexer status
     * @author - Vivek
     * @ModifiedBy - Vani
     * Update Indexer segment file
     */


    @PostMapping(path = UPDATE_INDEXER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateIndexerByIndexerName(String indexerName, String[] ticketID, HttpServletRequest request) throws Exception {
        JSONObject successObj = new JSONObject();
        Boolean indexerstatus = Boolean.FALSE;
        StringBuilder ticketIDQuery = new StringBuilder("");
        User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
        Map<String, Object> errorMap = new HashMap<>();
        if (request.getParameterMap() != null && (request.getParameterMap().containsKey("Sync") || request.getParameterMap().containsKey("sync"))) {
            jmsTemplate.convertAndSend(INDEXER_SYNC,
                    new SFInterfaceIndexerSyncModel(INDEXER_UPDATE, new String[]{indexerName}, ticketID, user.getClientInstance()));
        }
        JdbcTemplate jdbcTemplate = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName());
        IndexerDomain indexerDomain = IndexerUtilities.findByNameAndStatussk(jdbcTemplate, indexerName);
        if (indexerDomain != null && !indexerDomain.getQuery().isEmpty()) {
            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERUPDATESTART"));
            for (String ticketid : ticketID) {
                if (ticketIDQuery.length() > NUM_ZERO) {
                    ticketIDQuery.append(",'" + ticketid + "'");
                } else {
                    ticketIDQuery.append("'" + ticketid + "'");
                }
            }
            try {
                IndexWritersModel writersModel = SFInterfaceConnectionBase.fetchIndexerWriterModal(user, indexerDomain.getName());
                if (writersModel != null) {
                    if (writersModel.isAvailable()) {
                        String updateQuery = indexerDomain.getQuery() + WHERE + indexerDomain.getIndexerkey() + " IN ( " + ticketIDQuery + " )";
                        indexerstatus = SFInterfaceBase.updateindexer(writersModel.getIndexWriter(), indexerDomain.getName(), updateQuery, indexerDomain.getIndexerkeyaliasname()
                                , SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), ticketID, user);
                        SFInterfaceLoggerBase.log(LOGGER, Constants.UPDATE, indexerDomain.getName(), null, null, ticketIDQuery.toString(), null, null, "");
                    } else {
                        //successObj.addProperty(indexerDomain.getName(), Boolean.FALSE);
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INDEXER.LOCKED", null, null);
                        successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                        successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                        successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                    }
                } else {
                    //successObj.put(indexerDomain.getName(), Boolean.FALSE);
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INDEXER.BEAN.NOT.AVAILABLE", null, null);
                    successObj.put(MESSAGE, (String) errorMap.get(MESSAGE));
                    successObj.put(INFOCODE, (String) errorMap.get(INFOCODE));
                    successObj.put(MOREINFO, (String) errorMap.get(MOREINFO));
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                successObj.put(ERROR, (String) SFInterfaceServices.ISPExceptionHandler(e, null, null).get(ERROR));
            }
            SFInterfaceLoggerBase.log(LOGGER, indexerDomain.getName() + UPDATED);
            successObj.put(indexerDomain.getName(), indexerstatus);

            SFInterfaceLoggerBase.log(LOGGER, sfInterfaceMessages.get("INFO.MESSAGE.INDEXERUPDATEEND"));
        }
        return new ResponseEntity(successObj.toMap(), HttpStatus.OK);
    }

    /**
     * author - Vivek
     * The Function is used for Querying records from lucene with query framed from the client
     *
     * @return - Returns Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  indexername query requiredfields and alias name as input
     */
    @PostMapping(path = QUERY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity lqlSearch(@PathVariable(VERSION) String version,
                                    @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                    @RequestParam(value = ACCOUNT) String account,
                                    @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                    @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                    @RequestBody LQLSearch lqlSearch, HttpServletRequest request) throws IOException {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";
        String endpointName = QUERY;

        try {

            if (isRequestContainToken(trimToken(aToken), trimToken(refreshToken), trimToken(accessToken))) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
            }

            JsonArray jsonElements = new JsonArray();

            Directory indexDir = indexerUtil.getIndexDirectory(lqlSearch.getIndexerPath(), lqlSearch.getIndexerName());
            IndexerIntf indexIntf = new LucenceImpl(indexDir);
            List<Document> documents = indexIntf.searchDocumentWithLQL(lqlSearch.getQuery(), lqlSearch.getCount() == null ? NUM_ZERO : lqlSearch.getCount());
            for (Document document : documents) {
                JsonObject jsonObject = new JsonObject();
                if (lqlSearch.getRequiredColumns() != null) {
                    for (String requiredColumn : lqlSearch.getRequiredColumns()) {
                        if (lqlSearch.getAlias() != null && lqlSearch.getAlias().containsKey(requiredColumn))
                            jsonObject.addProperty(lqlSearch.getAlias().get(requiredColumn), document.get(requiredColumn));
                        else
                            jsonObject.addProperty(requiredColumn, document.get(requiredColumn));
                    }
                } else {
                    for (IndexableField field : document.getFields()) {
                        if (lqlSearch.getAlias() != null && lqlSearch.getAlias().containsKey(field.name()))
                            jsonObject.addProperty(lqlSearch.getAlias().get(field.name()), document.get(field.name()));
                        else
                            jsonObject.addProperty(field.name(), document.get(field.name()));
                    }
                }
                jsonElements.add(jsonObject);
            }
            return new ResponseEntity(jsonElements.toString(), HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}




