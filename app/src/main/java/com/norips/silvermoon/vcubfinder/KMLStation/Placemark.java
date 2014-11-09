package com.norips.silvermoon.vcubfinder.KMLStation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root
public class Placemark {
    @Attribute(name="id")
    protected String id;

    @Element(name="ExtendedData")
    protected ExtendedData ExtendedDataList;

    @Element(name="Point")
    protected Point point;

    @Element
    protected String name;

    @Element
    protected String snippet;

    @Element
    protected String description;


    public String getName() {
        return name;
    }

    public Point getPoint(){
      return point;
    }
    public ExtendedData getExtendedData(){ return ExtendedDataList; }
}


