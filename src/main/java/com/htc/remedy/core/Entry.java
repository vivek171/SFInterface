package com.htc.remedy.core;

import java.util.ArrayList;
import java.util.List;

public class Entry {
    String entryId;
    List<FieldEntry> fieldEntries;

    public Entry() {
        fieldEntries = new ArrayList<>();
    }

    public Entry(String entryId, List<FieldEntry> fieldEntries) {
        this.entryId = entryId;
        this.fieldEntries = fieldEntries;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public void addFieldEntry(FieldEntry entry) {
        this.fieldEntries.add(entry);
    }

    public List<FieldEntry> getFieldEntries() {
        return fieldEntries;
    }

    public void setFieldEntries(List<FieldEntry> fieldEntries) {
        this.fieldEntries = fieldEntries;
    }

}
