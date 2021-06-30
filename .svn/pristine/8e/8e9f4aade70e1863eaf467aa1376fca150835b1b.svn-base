package com.htc.remedy.assets;

import com.google.gson.JsonObject;
import com.htc.remedy.base.SFInterfaceMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.htc.remedy.constants.SFInterfaceConstants.*;

@Service
@Component
public class SFInterfaceAssetBase {

    private static final Logger LOGGER = LogManager.getLogger(SFInterfaceAssetBase.class);

    private static SFInterfaceMessages sfInterfaceMessages;

    public static List<Map<String, Object>> runjdbcQueryWithSelectedColumns(JdbcTemplate jdbcTemplate, String query, String[] requiredColumns) {

        Map<String, Object> requiredColumnsResult = new HashMap<>();

        if (requiredColumns != NULL && requiredColumns.length > NUM_ZERO) {
            for (String result : requiredColumns) {
                requiredColumnsResult.put(result, result);
            }

            return jdbcTemplate.query(query, (resultSet, position) -> {
                int cCount = resultSet.getMetaData().getColumnCount();
                Map<String, Object> jsonObject = new HashMap<>();
                for (int i = 1; i <= cCount; i++) {
                    if (requiredColumnsResult.containsKey(resultSet.getMetaData().getColumnLabel(i))) {
                        jsonObject.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getString(i));
                    }
                }
                return jsonObject;
            });
        } else {
            return SFInterfaceAssetBase.runjdbcQuery(jdbcTemplate, query);
        }
    }

    public static List<Map<String, Object>> runjdbcQuery(JdbcTemplate jdbcTemplate, String query) {
        return jdbcTemplate.query(query, (resultSet, position) -> {
            int cCount = resultSet.getMetaData().getColumnCount();
            Map<String, Object> jsonObject = new HashMap<>();
            for (int i = 1; i <= cCount; i++) {
                jsonObject.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getString(i));
            }
            return jsonObject;
        });
    }

    public static String getDBQuery(JsonObject jsonObject, Map<String, String[]> params, String endpointName) {

        String query = "";

        if (jsonObject.has(endpointName)) {
            query = jsonObject.get(endpointName).getAsString();
            query=params.keySet()
                    .stream()
                    .filter(stringEntry -> stringEntry != null)
                    .reduce(query, (tempquery, param) ->
                            tempquery.replaceAll(DOUBLE_OPEN_BRACE_WITH_BACKSLASH + param + DOUBLE_CLOSE_BRACE_WITH_BACKSLASH, params.get(param)[NUM_ZERO])
                    );

            return query.replaceAll("'\\{\\{.*?\\}\\}'","null");
        }

        return query;
    }

}
