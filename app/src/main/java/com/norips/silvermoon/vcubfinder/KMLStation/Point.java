package com.norips.silvermoon.vcubfinder.KMLStation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Point{
    @Element(name="coordinates")
    protected String coordinates;

    public String getCoordinates() {
        return coordinates;
    }
}
