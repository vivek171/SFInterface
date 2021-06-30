package com.htc.remedy.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CustomRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        Map<String, Object> jsonObject = new HashMap<>();
        for (int columnCount = 1; resultSet.getMetaData().getColumnCount() >= columnCount; columnCount++) {
            jsonObject.put(resultSet.getMetaData().getColumnName(columnCount), resultSet.getString(columnCount));
        }
        return jsonObject;
    }
}
