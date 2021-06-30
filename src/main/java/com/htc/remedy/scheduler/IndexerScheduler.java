package com.htc.remedy.scheduler;

import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.base.SFInterfaceMessages;
import com.htc.remedy.config.IndexerUtilities;
import com.htc.remedy.config.JDBCConfiguration;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.domain.IndexerDomain;
import com.htc.remedy.model.IndexWritersModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.htc.remedy.config.IndexerUtilities.fetchIndexers;
import static com.htc.remedy.constants.SFInterfaceConstants.*;

@Service
@Component
public class IndexerScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerScheduler.class);

    private static
    Map<String, JdbcTemplate> jdbcTemplateMap;

    private static SFInterfaceMessages messages;

    private static
    Map<String, Map<String, IndexWritersModel>> indexWriters;

    @Autowired
    public IndexerScheduler(@Qualifier(value = "jdbcAccTemplate") Map<String, JdbcTemplate> jdbcTemplateMap,
                            SFInterfaceMessages messages, @Qualifier(value = "IndexerWriters") Map<String, Map<String, IndexWritersModel>> indexWriters) {
        this.jdbcTemplateMap = jdbcTemplateMap;
        this.messages = messages;
        this.indexWriters = indexWriters;
    }


    @Scheduled(initialDelayString = "${cron.initialdelay}", fixedDelay = Long.MAX_VALUE)
    private void indexercreationOnStartUp() {
        indexerUpdate();
    }

    @Scheduled(cron = "${cron.expression}", zone = "${cron.zoneid}")
    private void indexerupdatecron() {
        indexerUpdate();
    }


    @Async
    @Scheduled(cron = "${cron.jdbc.refresh.delay}", zone = "${cron.zoneid}")
    public void refreshJdbcTemplates() {
        jdbcTemplateMap = JDBCConfiguration.refreshJdbcTemplate(jdbcTemplateMap);
    }

  /*  public static void indexerUpdate() {

        jdbcTemplateMap.entrySet().stream().forEach(
                stringJdbcTemplateEntry ->
                {
                    String path = "";
                    if (stringJdbcTemplateEntry.getKey().endsWith(SFInterfaceConstants.getDbRefConnectionName())) {
                        List<IndexerDomain> indexerDomains = fetchIndexers(stringJdbcTemplateEntry.getValue());

                        LOGGER.info("Indexer Update Started at:" + getcurrentdatetime());
                        for (IndexerDomain indexerDomain : indexerDomains) {
                            try {
                                path = IndexerUtilities.fetchpathbyConnectionName(stringJdbcTemplateEntry.getKey());
                                SFInterfaceBase.createIndexer(indexWriters.get(indexerDomain.getName()).get(
                                        path)
                                        , indexerDomain.getName(), indexerDomain.getQuery(), indexerDomain.getIndexerkeyaliasname(), stringJdbcTemplateEntry.getValue());
                            } catch (IOException e) {
                                LOGGER.error(path + COLON + indexerDomain.getName() + ":failed=>" + e.getMessage(), e);
                            }
                            LOGGER.info(path + COLON + indexerDomain.getName() + ":Created");
                        }
                        LOGGER.info("Indexer Creation Finished at:" + getcurrentdatetime());
                    }
                });
    }*/

    public static void indexerUpdate() {

        jdbcTemplateMap.entrySet().parallelStream()
                .filter(stringJdbcTemplateEntry -> stringJdbcTemplateEntry.getKey().endsWith(SFInterfaceConstants.getDbRefConnectionName()))
                .forEach(
                        stringJdbcTemplateEntry -> {
                            createIndexers(stringJdbcTemplateEntry.getKey(), stringJdbcTemplateEntry.getValue());
                        });
    }


    public static void createIndexers(String key, JdbcTemplate jdbcTemplate) {
        List<IndexerDomain> indexerDomains = fetchIndexers(jdbcTemplate);
        String path = IndexerUtilities.fetchpathbyConnectionName(key);
        createIndexer(indexerDomains, jdbcTemplate, path);
    }

    public static Map<String, Object> createIndexer(List<IndexerDomain> indexerDomains, JdbcTemplate jdbcTemplate, String path) {
        Map<String, Object> hashMaps = new ConcurrentHashMap<>();
        LOGGER.info("Indexer Update Started at:" + getcurrentdatetime());
        indexerDomains.parallelStream().forEach(indexerDomain -> {
            hashMaps.put(indexerDomain.getName(), createIndexer(path, jdbcTemplate, indexerDomain));
        });
        LOGGER.info(path + "\tIndexer Creation Finished at:" + getcurrentdatetime());
        return hashMaps;
    }

    public static Map<String, Object> createIndexer(String path, JdbcTemplate jdbcTemplate, IndexerDomain indexerDomain) {
        Map<String, Object> returnResponse = new ConcurrentHashMap<>();
        Map<String, Object> indexerResult = new HashMap<>();
        try {
            indexerResult = SFInterfaceBase.createIndexer(indexWriters.get(indexerDomain.getName()).get(
                    path)
                    , indexerDomain.getName(), indexerDomain.getQuery(), indexerDomain.getIndexerkeyaliasname(), jdbcTemplate);
            if (indexerResult.size() == NUM_ZERO) {
                returnResponse.put(STATUS, Boolean.TRUE);
                returnResponse.put(MESSAGE, messages.get("INFO.MESSAGE.EMPTYINDEXER"));
            } else {
                if (indexerResult.size() > NUM_ZERO && indexerResult.get(RESPONSE).toString().equalsIgnoreCase(SUCCESS)) {
                    returnResponse.put(STATUS, Boolean.TRUE);
                    returnResponse.put(MESSAGE, SUCCESS);
                } else {
                    returnResponse.put(STATUS, Boolean.FALSE);
                    returnResponse.put(MESSAGE, indexerResult.get(RESPONSE));
                }
            }
        } catch (Exception e) {
            LOGGER.error(path + COLON + indexerDomain.getName() + ":failed=>" + e.getMessage(), e);
            returnResponse.put(STATUS, Boolean.FALSE);
            returnResponse.put(MESSAGE, e.getMessage() != null ? e.getMessage() : NA);
        }
        LOGGER.info(path + COLON + indexerDomain.getName() + ":Created");
        return returnResponse;
    }

    public static String getcurrentdatetime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }


}
