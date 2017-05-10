package com.norips.silvermoon.vcubfinder.WFS;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

/**
 * Created by silvermoon on 25/11/14.
 */
public class Point {
    @Attribute(name="srsName")
    protected String srsName;

    @Element(name="pos")
    @Namespace(prefix = "gml")
    protected String pos;
    public String getPos(){return pos;}
}
