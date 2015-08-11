package org.healthwise.quantumleap.parsing.beans;

/**
 * Created by akimball on 8/8/15.
 */
public class Facet {
    private String facetName;
    private String facetValueId;
    private String facetValueLabel;

    public String getFacetName() {
        return facetName;
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public String getFacetValueId() {
        return facetValueId;
    }

    public void setFacetValueId(String facetValueId) {
        this.facetValueId = facetValueId;
    }

    public String getFacetValueLabel() {
        return facetValueLabel;
    }

    public void setFacetValueLabel(String facetValueLabel) {
        this.facetValueLabel = facetValueLabel;
    }
}
