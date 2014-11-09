package com.norips.silvermoon.vcubfinder.KMLStation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ExtendedData {
    @Element(name="SchemaData")
    protected SchemaData schemaDataList;

    public SchemaData getSchemaDataList(){ return schemaDataList; }

}
