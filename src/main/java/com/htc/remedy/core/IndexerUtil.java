package com.htc.remedy.core;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class IndexerUtil {

    private static
    Environment environment;

    @Autowired
    public IndexerUtil(Environment environment) {
        this.environment = environment;
    }

    public static Directory getIndexDirectory(String indexerpath, String indexdirName) throws IOException {
        Path indexFile = Paths.get(environment.getProperty("app.caretech.search.index-base-path"), indexerpath, indexdirName);
        if (!isValidIndexFile(indexFile.toFile()))
            throw new IOException("INDEXER NOT FOUND OR LOCKED :" + indexdirName);
        Directory index = new SimpleFSDirectory(indexFile);
        return index;
    }


    public static Boolean isValidIndexFile(File file) {
        if (
                !file.isHidden()
                        && file.exists()
                        && file.canRead()
        ) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
