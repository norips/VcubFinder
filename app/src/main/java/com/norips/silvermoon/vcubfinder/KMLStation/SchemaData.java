package com.norips.silvermoon.vcubfinder.KMLStation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;

@Root
public class SchemaData{
    @Attribute
    protected String schemaUrl;
    @ElementMap(entry="SimpleData", key="name", attribute=true, inline=true)
    private Map<String, String> simpledata;

    public String getKey() {
        return schemaUrl;
    }
    public Map<String, String> getSimpledata() { return simpledata; }

}

