package org.healthwise.quantumleap.parsing.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by akimball on 8/8/15.
 */
public class ConceptBean {



    private final String HWNS = "hwcv_sc";
    private final String CONCEPT_ID = "concept_id";
    private final String LIFECYCLE = "lifecycle_stage";
    private final String SYNONYM = "legacy_synonym";
    private final String ABBREVIATION = "abbreviation";
    private final String CLINICAL_LABEL = "clinical_label";



    private Integer id;
    private String label;
    private List<String> broader = new ArrayList();
    private List<String> narrower  = new ArrayList();
    private List<String> related  = new ArrayList();
    private List<String> scopeNotes  = new ArrayList();
    private List<String> notes  = new ArrayList();
    private String topConcept = null;
    private List<String> abbreviations = new ArrayList();
    private List<String> clinicalLabels = new ArrayList();
    private List<String> legacySynonym = new ArrayList<String>();
    private List<String> cuis = new ArrayList<String>();
    private List<String> docs = new ArrayList<String>();
    private List<String> legacyCanadianSynonym = new ArrayList<String>();
    //private List<String> relatedLinks = new ArrayList<String>();
    private String rdId;
    private String rdLabel;
    private String rdType;
    private String facetId;
    private String definition;
    private String lifecycleStage;

    public ConceptBean(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public List<String> getCuis() {
        return cuis;
    }

    public void addCui(String cui) {
        if (!(cui == null && cui.equals("null"))) {
            this.cuis.add(cui);
        }
    }

    public List<String> getDocs() {
        return docs;
    }

    public void addDoc(String doc) {
        this.docs.add(doc);
    }

    public List<String> getNotes() {
        return notes;
    }

    public void addToNotes(String note) {
        this.notes.add(note);
    }

    public List<String> getLegacyCanadianSynonym() {
        return legacyCanadianSynonym;
    }

    public void addLegacyCanadianSynonym(String lcg) {
        this.legacyCanadianSynonym.add(lcg);
    }

    public List getClinicalLabels() {
        return clinicalLabels;
    }

    public void addClinicalLabel(String clinicalLabel) {
        clinicalLabels.add(clinicalLabel);
    }

    public List getAbbreviations() {
        return abbreviations;
    }

    public void addAbbreviations(String abbreviation) {
        this.abbreviations.add(abbreviation);
    }

    public String getTopConcept() {
        return topConcept;
    }

    public void setTopConcept(String topConcept) {
        this.topConcept = topConcept;
    }

    public String getLifecycleStage() {
        return lifecycleStage;
    }

    public void setLifecycleStage(String lifecycleStage) {
        this.lifecycleStage = lifecycleStage;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
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

    public void addTopConcept(String idOfTopConcept) {
        topConcept = idOfTopConcept;
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
        if (facetId != null && !facetId.equals("null")) {
            this.facetId = facetId;
        }

    }


    public List<String> getLegacySynonym() {
        return legacySynonym;
    }

    public String getRdType() {
        return rdType;
    }

    public void setRdType(String rdType) {
        this.rdType = rdType;
    }

    public String getRdLabel() {
        return rdLabel;
    }

    public void setRdLabel(String rdLabel) {
        this.rdLabel = rdLabel;
    }

    public void addToAltLabel(String altLabel, String locale) {
        if (locale.equals("en-us")) {
            this.legacySynonym.add(altLabel);
        } else {
            this.legacyCanadianSynonym.add(altLabel);
        }
    }

    public String render() {

        if (this.getLabel().startsWith("Endoscopic Retrograde")) {
            System.out.println("me");
        }

        Iterator iter;
        StringBuffer out = new StringBuffer();
        out.append("  <skos:Concept rdf:ID=\"HWCV_"+getId()+"\">\n");
        out.append("    <"+HWNS+":"+CONCEPT_ID+">HWCV_"+getId()+"</"+HWNS+":"+CONCEPT_ID+">\n");
        out.append("    <rdfs:label>"+getLabel()+"</rdfs:label>\n");

//        if (getTopConcept() != null) {
//            out.append("    <skos:hasTopConcept rdf:resource=\"#HWCV_" + getTopConcept() + "\"/>\n");
//        }
//        if (getTopConcepts().size() > 0) {
//            iter = this.getTopConcepts().iterator();
//            while (iter.hasNext()) {
//                String topConceptId = (String) iter.next();
//                out.append("    <skos:hasTopConcept rdf:resource=\"#HWCV_" + topConceptId + "\"/>\n");
//            }
//        }

        if (getRelated().size() > 0) {
            iter = this.getRelated().iterator();
            while (iter.hasNext()) {
                String someLink = (String) iter.next();
                out.append("    <skos:related rdf:resource=\"#HWCV_" + someLink + "\"/>\n");
            }
        }


        if (this.getBroader().size() > 0) {
            // if we only have one broader
            if (this.getBroader().size() == 1) {
                out.append("    <skos:broader rdf:resource=\"#HWCV_" + this.getBroader().get(0) + "\"/>\n");
                //out.append("    <skos:hasTopConcept rdf:resource=\"#HWCV_" + this.getBroader().get(0) + "\"/>\n");
            } else {
                // we have more than one
                iter = this.getBroader().iterator();
                while (iter.hasNext()) {
                    String broaderId = (String) iter.next();
                    Integer broaderAsInt = Integer.parseInt(broaderId);
                    if (broaderAsInt > 19999) {
                        out.append("    <skos:broader rdf:resource=\"#HWCV_" + broaderId + "\"/>\n");
                    } else {
                        //out.append("    <skos:hasTopConcept rdf:resource=\"#HWCV_" + broaderId + "\"/>\n");
                    }
                }
            }

        }
//        if (this.getBroader().size() > 0) {
//            iter = this.getBroader().iterator();
//            while (iter.hasNext()) {
//                String broaderId = (String) iter.next();
//                Integer broaderAsInt = Integer.parseInt(broaderId);
//                if (broaderAsInt > 19999) {
//                    out.append("    <skos:broader rdf:resource=\"#HWCV_" + broaderId + "\"/>\n");
//                } else {
//                    out.append("    <skos:hasTopConcept rdf:resource=\"#HWCV_" + broaderId + "\"/>\n");
//                }
//            }
//        } else {
//            if (getTopConcept() != null) {
//                out.append("    <skos:broader rdf:resource=\"#HWCV_" + getTopConcept() + "\"/>\n");
//            }
//        }
        if (this.getNarrower().size() > 0) {
            iter = this.getNarrower().iterator();
            while (iter.hasNext()) {
                String narrowerId = (String) iter.next();
                out.append("    <skos:narrower rdf:resource=\"#HWCV_"+narrowerId+"\"/>\n");
            }
        }
        if (this.getLegacySynonym().size() > 0) {
            iter = this.getLegacySynonym().iterator();
            while (iter.hasNext()) {
                String someSynonym = (String) iter.next();
                out.append("    <skos:altLabel>" + someSynonym + "</skos:altLabel>\n");
            }
        }

        if (this.getLegacyCanadianSynonym().size() > 0) {
            iter = this.getLegacyCanadianSynonym().iterator();
            while (iter.hasNext()) {
                String someSynonym = (String) iter.next();
                out.append("    <skos:altLabel xml:lang=\"en-ca\">" + someSynonym + "</skos:altLabel>\n");
            }
        }


        if (this.getAbbreviations().size() > 0) {
            iter = this.getAbbreviations().iterator();
            while (iter.hasNext()) {
                String abv = (String) iter.next();
                out.append("    <hwcv_sc:"+ABBREVIATION+">"+abv+"</hwcv_sc:"+ABBREVIATION+">\n");
            }
        }

        if (this.getClinicalLabels().size() > 0) {
            iter = this.getClinicalLabels().iterator();
            while (iter.hasNext()) {
                String clLabel = (String) iter.next();
                out.append("    <hwcv_sc:"+CLINICAL_LABEL+">"+clLabel+"</hwcv_sc:"+CLINICAL_LABEL+">\n");
            }
        }

        if (this.getCuis().size() > 0) {
            iter = this.getCuis().iterator();
            while (iter.hasNext()) {
                String cui = (String) iter.next();
                out.append("    <hw_legacy:has_cui rdf:resource=\"http://www.healthwise.org/legacy/concept_data#" + cui + "\"/>\n");
            }
        }

        if (this.getDocs().size() > 0) {
            iter = this.getDocs().iterator();
            while (iter.hasNext()) {
                String doc = (String) iter.next();
                out.append("    <hw_legacy:has_hwid rdf:resource=\"http://www.healthwise.org/legacy/concept_data#" + doc + "\"/>\n");
            }
        }

        if (this.getRdId() != null) {
            out.append("    <hw_legacy:has_rd rdf:resource=\"http://www.healthwise.org/legacy/concept_data#" + getRdId() + "\"/>\n");
        }


        if (this.getScopeNote().size() > 0) {
            iter = this.getScopeNote().iterator();
            while (iter.hasNext()) {
                String scopeNote = (String) iter.next();
                out.append("    <skos:scopeNote>"+scopeNote+"</skos:scopeNote>\n");
            }
        }

        if (this.getNotes().size() > 0) {
            iter = this.getNotes().iterator();
            while (iter.hasNext()) {
                String note = (String) iter.next();
                out.append("    <skos:note>"+note+"</skos:note>\n");
            }
        }

        if (this.getDefinition() != null) {
            out.append("    <skos:definition>"+getDefinition()+"</skos:definition>\n");
        }


        if (this.getFacetId() != null && !this.getFacetId().equals("null")) {

            out.append("    <hw_legacy:has_facet rdf:resource=\"http://www.healthwise.org/legacy/concept_data#" + getFacetId() + "\"/>\n");
        }

        if (getLifecycleStage() == null || getLifecycleStage().equals("null")) {
            out.append("    <" + HWNS + ":" + LIFECYCLE + ">" + "unkown" + "</" + HWNS + ":" + LIFECYCLE + ">\n");
        } else {
            out.append("    <" + HWNS + ":" + LIFECYCLE + ">" + getLifecycleStage() + "</" + HWNS + ":" + LIFECYCLE + ">\n");
        }



        out.append("  </skos:Concept>\n\n");

        return out.toString();

    }

}
