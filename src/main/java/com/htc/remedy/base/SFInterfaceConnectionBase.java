package com.htc.remedy.base;

import com.htc.remedy.config.IndexerUtilities;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.core.CustomAnalyzer;
import com.htc.remedy.model.IndexWritersModel;
import com.htc.remedy.model.User;
import com.htc.remedy.model.accountconfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.htc.remedy.constants.SFInterfaceConstants.*;

@Service
@Component
public class SFInterfaceConnectionBase {

    private static
    Map<String, JdbcTemplate> jdbcTemplateMap;

    private static
    List<accountconfig> accountconfigList;

    private static
    SFInterfaceMessages messages;

    private static
    Map<String, Map<String, IndexWritersModel>> indexWriters;


    @Autowired
    public SFInterfaceConnectionBase(@Qualifier(value = "jdbcAccTemplate") Map<String, JdbcTemplate> jdbcTemplateMap,
                                     @Qualifier(value = "IndexerWriters") Map<String, Map<String, IndexWritersModel>> indexWriters,
                                     @Qualifier(value = "jdbcmodel") List<accountconfig> accountconfigList, SFInterfaceMessages messages) {
        this.jdbcTemplateMap = jdbcTemplateMap;
        this.indexWriters = indexWriters;
        this.accountconfigList = accountconfigList;
        this.messages = messages;
    }


    public static User fetchRequestInfo(HttpServletRequest request) throws Exception {
        return getUser(request);
    }

    public static User getUser(HttpServletRequest request) throws Exception {
        User user = new User();
        if (request != NULL && request.getUserPrincipal() != NULL && request.getUserPrincipal().getName() != NULL) {
            String[] userDetail = request.getUserPrincipal().getName().split(TILDE);
            user.setAuthUserName(userDetail[NUM_ZERO]);
            user.setClient(userDetail[NUM_ONE]);
            user.setUserID(userDetail[NUM_TWO]);
            user.setAuthTokenType(AUTH_TOKEN);
            user.setClientInstance(userDetail[NUM_THREE].toLowerCase());
            user.setAuthTokenType(userDetail[NUM_FOUR]);
            user.setRequestID(request.getHeader("XRequestId"));

        } else if (request.getHeader(A_TOKEN) != NULL && StringUtils.isNotBlank(request.getHeader(A_TOKEN))
                && request.getHeader(A_TOKEN).trim().equalsIgnoreCase(SFInterfaceConstants.getaTokenValue())) {
            if (SFInterfaceBase.checkBlankorEmpty(request.getHeader(CLIENT_INSTANCE))) {
                if (!SFInterfaceBase.fetchDedicatedInstances().parallelStream()
                        .anyMatch(s -> s.toLowerCase().equalsIgnoreCase((String) request.getHeader(CLIENT_INSTANCE).toLowerCase()))) {
                    throw new Exception(messages.get("AUTHENTICATION.ERROR.CLIENT_INSTANCE_NOTVALID"));
                }
                user.setClientInstance(request.getHeader(CLIENT_INSTANCE).toLowerCase());
            } else {
                throw new Exception(messages.get("AUTHENTICATION.ERROR.CLIENT_INSTANCE_NOTFOUND"));
            }
            user.setUserID(SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, LOGIN_ID + COLON + Constants.getAdminusername()
                    , null, new String[]{USER_SK}, null, null, TRUE, null, user).get(0).get(USER_SK).toString());
            user.setAuthUserName(Constants.getAdminusername());
            user.setAuthTokenType(ATOKEN);
            user.setRequestID(request.getHeader("XRequestId"));
        } else {
            throw new Exception(messages.get("ERROR.MESSAGE.MISSINGTOKEN"));
        }
        return user;
    }

    public static IndexWritersModel fetchIndexerWriterModal(User user, String indexerName) throws Exception {
        return fetchIndexerWriterModal(user, indexerName, null);
    }

    public static IndexWritersModel fetchIndexerWriterModal(User user, String indexerName, Map<String, Object> ticketFields) throws Exception {
        return indexWriters.get(indexerName).get(user.getClientInstance());
    }


    public static JdbcTemplate fetchJdbcTemplatebyType(User user, String type) {
        return jdbcTemplateMap.get(user.getClientInstance() + UNDERSCORE + type);
    }

    public static Map<String, Map<String, IndexWritersModel>> updateIndexerModel(String indexerName, User user) throws IOException {
        Analyzer analyzer = new CustomAnalyzer();
        Directory index = IndexerUtilities.createIndexerDirectory(user.getClientInstance(), indexerName);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Map<String, IndexWritersModel> modals = indexWriters.get(indexerName);
        if (modals == null) {
        }
        IndexWriter indexWriter = new IndexWriter(index, config);
        IndexWritersModel indexWritersModel = new IndexWritersModel();
        indexWritersModel.setIndexername(indexerName);
        indexWritersModel.setIndexWriter(indexWriter);
        indexWritersModel.setConnectionName(user.getClientInstance());
        indexWritersModel.setAvailable(true);
        modals.put(user.getClientInstance(), indexWritersModel);
        indexWriters.put(indexerName, modals);
        return indexWriters;
    }


}
