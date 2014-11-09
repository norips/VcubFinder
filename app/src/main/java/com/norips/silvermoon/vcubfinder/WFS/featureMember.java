package com.norips.silvermoon.vcubfinder.WFS;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class featureMember{
    @Element(name="CI_VCUB_P")
    @Namespace(prefix = "ms")
    protected CI_VCUB_P ci_vcup_p;
    public CI_VCUB_P getCi_vcup_p(){ return ci_vcup_p; }

}
