package com.norips.silvermoon.vcubfinder.KMLStation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class SimpleData{
    @Attribute
    protected String name;
    @Element
    protected String data;


    public String getKey() {
        return name;
    }
    public String getData() {
        return data;
    }

}
