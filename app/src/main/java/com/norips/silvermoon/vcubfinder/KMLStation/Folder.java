package com.norips.silvermoon.vcubfinder.KMLStation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by silvermoon on 06/11/14.
 */
@Root
public class Folder {
    @Attribute(name="id")
    protected String id;

    @Element(name="name")
    protected String name;

    @ElementList(inline=true, entry="Placemark")
    protected List<Placemark> placemarkList;

    public List getPlacemark(){
        return placemarkList;
    }

}
