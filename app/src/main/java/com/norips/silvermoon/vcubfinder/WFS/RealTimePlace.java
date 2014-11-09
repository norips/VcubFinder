package com.norips.silvermoon.vcubfinder.WFS;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by silvermoon on 07/11/14.
 */
@Root(strict=false)
public class RealTimePlace {
    @ElementList(inline=true)
    protected List<featureMember> featureMemberList;
public List<featureMember> getFeaturemember(){ return featureMemberList; }

}

