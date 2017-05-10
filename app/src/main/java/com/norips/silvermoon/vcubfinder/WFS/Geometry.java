package com.norips.silvermoon.vcubfinder.WFS;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

/**
 * Created by silvermoon on 25/11/14.
 */
public class Geometry {
    @Element(name="Point")
    @Namespace(prefix = "gml")
    protected Point point;
    public Point getPoint(){return point;}
}
