package com.norips.silvermoon.vcubfinder.WFS;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(strict=false)
public class CI_VCUB_P {
    @Attribute(name="fid")
    protected String fid;

    @Element(name="GID")
    @Namespace(prefix = "ms")
    protected String GID;

    @Element(name="IDENT")
    @Namespace(prefix = "ms")
    protected String IDENT;

    @Element(name="TYPE")
    @Namespace(prefix = "ms")
    protected String TYPE;

    @Element(name="NOM")
    @Namespace(prefix = "ms")
    protected String NOM;

    @Element(name="ETAT")
    @Namespace(prefix = "ms")
    protected String ETAT;

    @Element(name="NBPLACES")
    @Namespace(prefix = "ms")
    protected String NBPLACES;

    @Element(name="NBVELOS")
    @Namespace(prefix = "ms")
    protected String NBVELOS;

    public String getGID() {return GID;}
    public String getIDENT() {return IDENT;}
    public String getTYPE() {return TYPE;}
    public String getNOM() {return NOM;}
    public String getETAT() {return ETAT;}
    public String getNBPLACES() {return NBPLACES;}
    public String getNBVELOS() {return NBVELOS;}


}
