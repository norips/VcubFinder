package com.norips.silvermoon.vcubfinder.WFS;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(strict=false)
public class CI_VCUB_P {


    @Attribute(name="id")
    protected String id;

    @Element(name="GID")
    @Namespace(prefix = "bm")
    protected String GID;

    @Element(name="IDENT")
    @Namespace(prefix = "bm")
    protected String IDENT;

    @Element(name="TYPE")
    @Namespace(prefix = "bm")
    protected String TYPE;

    @Element(name="NOM")
    @Namespace(prefix = "bm")
    protected String NOM;

    @Element(name="ETAT")
    @Namespace(prefix = "bm")
    protected String ETAT;

    @Element(name="NBPLACES")
    @Namespace(prefix = "bm")
    protected String NBPLACES;

    @Element(name="NBVELOS")
    @Namespace(prefix = "bm")
    protected String NBVELOS;

    @Element(name="geometry")
    @Namespace(prefix = "bm")
    protected Geometry geometry;

    public String getGID() {return GID;}
    public String getIDENT() {return IDENT;}
    public String getTYPE() {return TYPE;}
    public String getNOM() {return NOM;}
    public String getETAT() {return ETAT;}
    public String getNBPLACES() {return NBPLACES;}
    public String getNBVELOS() {return NBVELOS;}
    public Geometry getGeometry() {return geometry;}

}
