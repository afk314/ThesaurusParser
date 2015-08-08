package org.healthwise.quantumleap.parsing.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimball on 8/8/15.
 */
public class Concept {
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
}
