package com.htc.remedy.core;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Sort;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IndexerIntf {

    public void removeDocument(Entry entry);

    public void removeDocument(String docid);

    public String formQuery(String term) throws IOException;

    public List<Document> searchDocument(String queryTerm) throws IOException;

    public List<Document> searchDocument(String[] fields, String queryTerm) throws IOException;

    public Map<String,Object> searchDocumentWithLQL(String lql, int count, Sort sort, Boolean simplesearch, Boolean pagination) throws IOException;

    public Map<String,Object> searchDocumentWithLQL(String lql, int count, Sort sort, Boolean simplesearch, String sortField, String[] requiredColumns, Boolean sortorder, Integer pageno, Boolean pagination) throws IOException;

    public List<Document> searchDocumentWithLQL(String lql, int count, Sort sort) throws IOException;

    public List<Document> searchDocumentWithLQL(String lql, int count) throws IOException;

    public List<Document> searchDocumentWithLQL(String lql, String[] requiredColumns) throws IOException;

}
