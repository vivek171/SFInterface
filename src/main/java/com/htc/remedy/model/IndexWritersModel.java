package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.index.IndexWriter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexWritersModel implements BaseModel {
    IndexWriter indexWriter;
    String indexername;
    String connectionName;
    boolean available = Boolean.TRUE;
}
