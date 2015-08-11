package org.healthwise.quantumleap.parsing.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by akimball on 8/8/15.
 */
public class Concept {


    private final String HWNS = "hwcv_sc";
    private final String CONCEPT_ID = "concept_id";
    private final String RD_ID = "rd_id";
    private final String FACET_VALUE_ID = "facet_value_id";

    private Integer id;
    private String label;
    private List broader = new ArrayList();
    private List narrower  = new ArrayList();
    private List related  = new ArrayList();
    private List scopeNotes  = new ArrayList();
    private List<String> altLabel = new ArrayList<String>();
    private String rdId;
    private String facetId;
    private String definition;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Concept(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void addToBroader(String idOfBroader) {
        broader.add(idOfBroader);
    }

    public List getBroader() {
        return broader;
    }



    public void addToNarrower(String idOfNarrower) {
        narrower.add(idOfNarrower);
    }

    public List getNarrower() {
        return narrower;
    }



    public List getRelated() {
        return related;
    }



    public void addToRelated(String idOfRelated) {
        this.related.add(idOfRelated);
    }

    public List getScopeNote() {
        return scopeNotes;
    }

    public void addToScopeNotes(String scopeNote) {
        scopeNotes.add(scopeNote);
    }

    public String getRdId() {
        return rdId;
    }

    public void setRdId(String rdId) {
        this.rdId = rdId;
    }

    public String getFacetId() {
        return facetId;
    }

    public void setFacetId(String facetId) {
        this.facetId = facetId;
    }


    public List<String> getAltLabel() {
        return altLabel;
    }

    public void addToAltLabel(String altLabel) {
        this.altLabel.add(altLabel);
    }

    public String render() {
        Iterator iter;
        StringBuffer out = new StringBuffer();
        out.append("  <skos:Concept rdf:ID=\"HWCV_"+getId()+"\">\n");
        out.append("    <"+HWNS+":"+CONCEPT_ID+">HWCV_"+getId()+"</"+HWNS+":"+CONCEPT_ID+">\n");
        out.append("    <rdfs:label>"+getLabel()+"</rdfs:label>\n");
        if (this.getBroader().size() > 0) {
            iter = this.getBroader().iterator();
            while (iter.hasNext()) {
                String broaderId = (String) iter.next();
                out.append("    <skos:broader rdf:resource=\"#HWCV_" + broaderId + "\"/>\n");
            }
        }
        if (this.getNarrower().size() > 0) {
            iter = this.getNarrower().iterator();
            while (iter.hasNext()) {
                String narrowerId = (String) iter.next();
                out.append("    <skos:narrower rdf:resource=\"#HWCV_"+narrowerId+"\"/>\n");
            }
        }
        if (this.getAltLabel().size() > 0) {
            iter = this.getAltLabel().iterator();
            while (iter.hasNext()) {
                String altLabel = (String) iter.next();
                out.append("    <skos:altLabel>"+altLabel+"</skos:altLabel>\n");
            }
        }
        if (this.getScopeNote().size() > 0) {
            iter = this.getScopeNote().iterator();
            while (iter.hasNext()) {
                String scopeNote = (String) iter.next();
                out.append("    <skos:scopeNote>"+scopeNote+"</skos:scopeNote>\n");
            }
        }
        if (this.getDefinition() != null) {
            out.append("    <skos:definition>"+getDefinition()+"</skos:definition>\n");
        }

        if (this.getRdId() != null) {
            out.append("    <"+HWNS+":"+RD_ID+">"+getRdId()+"</"+HWNS+":"+RD_ID+">\n");
        }

        if (this.getFacetId() != null) {
            out.append("    <"+HWNS+":"+FACET_VALUE_ID+">"+getFacetId()+"</"+HWNS+":"+FACET_VALUE_ID+">\n");
        }


        out.append("  </skos:Concept>\n\n");

        return out.toString();

    }

}
