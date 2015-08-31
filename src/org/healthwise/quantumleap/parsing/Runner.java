package org.healthwise.quantumleap.parsing;

import org.healthwise.quantumleap.parsing.beans.ConceptBean;
import org.healthwise.quantumleap.parsing.beans.FacetMatchesCsvBean;
import org.healthwise.quantumleap.parsing.readers.*;

import java.io.*;
import java.util.*;

public class Runner {

    // Boilerplate RDF to append at the top
    private final String RDF_HEADER_FILENAME = "rdf_header.txt";
    // Outtput filename
    private final String RDF_OUTPUT_FILENAME = "/Users/akimball/dev/onto-out/HW_Concept.rdf";

    Map<Integer, ConceptBean> idToConcepts;
    Map<String, ConceptBean> conceptIdToConcepts;
    public static List<String> blacklistedLabels = new ArrayList();

    //List<FacetMatchesCsvBean> fmList;

    public static void main(String[] args) throws Exception {
        Runner runner = new Runner();
        ThesaurusBlacklistReader cbr = new ThesaurusBlacklistReader();
        blacklistedLabels = cbr.readWithCsvBeanReader();
        runner.start();
    }

    private void readHeaderIntoOutput(StringBuffer output) {
        List<String> srcFile = readFile(RDF_HEADER_FILENAME);
        Iterator i = srcFile.iterator();
        while(i.hasNext()) {
            output.append(i.next());
        }
    }

    private void start() throws Exception {
        idToConcepts = getConcepts();


        List<FacetMatchesCsvBean> all = buildFacetMapingList();


        this.addFacetIdsToConcepts();
        this.conceptIdToConcepts = buildConceptIdToConceptMap();

        this.merge(all);

        StringBuffer output = new StringBuffer();
        readHeaderIntoOutput(output);
        this.render(output);
        output.append("</rdf:RDF>\n");
        this.writeOutput(output);

    }

    private List<FacetMatchesCsvBean> buildFacetMapingList() throws Exception {
        List<FacetMatchesCsvBean> all = new ArrayList<FacetMatchesCsvBean>();
        all.addAll(getTestsFacetMatches());
        all.addAll(getProceduresFacetMatches());
        all.addAll(getWellnessFacetMatches());
        return all;
    }

    private void render(StringBuffer output) {
        Iterator<Integer> iter = idToConcepts.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            ConceptBean c = idToConcepts.get(id);
            output.append(c.render());
        }
    }

    private void addFacetIdsToConcepts() throws Exception {
        // Iterate over the Facet object and plug in the ids
        Iterator<Integer> iter = idToConcepts.keySet().iterator();
        Map<String, String> facetLabelToIdMap = getFacetLabelToIdMap();
        while (iter.hasNext()) {
            Integer id = iter.next();
            ConceptBean c = idToConcepts.get(id);
            String label = c.getLabel();
            if (Runner.blacklistedLabels.contains(label)) {
                System.out.println("Skipping: "+label);
            } else {
                String fidpId = facetLabelToIdMap.get(label);
                if (fidpId == null || fidpId.isEmpty()) {
                    System.out.println("Can't find an id for label: " + label);
                    fidpId = "MISSING";
                }
                c.setFacetId(fidpId);
            }
        }
    }

    private Map<String, ConceptBean> buildConceptIdToConceptMap() {
        Map<String, ConceptBean> cIdMap = new HashMap();
        Set keys = idToConcepts.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            Integer id = (Integer) iter.next();
            ConceptBean c = idToConcepts.get(id);
            cIdMap.put(c.getFacetId(), c);

        }
        return cIdMap;
    }

    private void merge(List<FacetMatchesCsvBean> all) throws Exception {
        // Iterate over every TFM object.  Find the corresponding concept, and sync changes.
        Iterator<FacetMatchesCsvBean> iter = all.iterator();
        while (iter.hasNext()) {
            FacetMatchesCsvBean currentTFM = iter.next();
            String rdid = currentTFM.getRdId();
            String conceptId = currentTFM.getConceptId();
            ConceptBean c = conceptIdToConcepts.get(conceptId);

            if (c == null) {
                System.out.println("Can't find a concept for id: "+conceptId);
            } else {
                c.setRdId(rdid);

                try {
                    if (currentTFM.getDmaps() != null) {
                        c.addToScopeNotes(currentTFM.getDmaps());
                    }
                } catch (Exception e) {
                    System.out.println("Error");
                }
                if (currentTFM.getRdLabel() != null) {
                    c.setRdLabel(currentTFM.getRdLabel());
                }
                if (currentTFM.getRdType() != null) {
                    c.setRdType(currentTFM.getRdType());
                }
                if (currentTFM.getLifecycleStage() == null) {
                    System.out.println("Whats up? "+currentTFM.getConceptId());
                }
                c.setLifecycleStage(currentTFM.getLifecycleStage());
            }
        }
    }

    public Map<Integer, ConceptBean>  getConcepts() throws Exception {
        ThesaurusParser parser = new ThesaurusParser();
        return parser.buildRDF();

    }

    public List<FacetMatchesCsvBean> getTestsFacetMatches() throws Exception {
        TestsFacetMatchesReader testFacetMap = new TestsFacetMatchesReader();
        return testFacetMap.readWithCsvBeanReader();
    }

    public List<FacetMatchesCsvBean> getProceduresFacetMatches() throws Exception {
        ProceduresFacetMatchesReader proceduresFacetMap = new ProceduresFacetMatchesReader();
        return proceduresFacetMap.readWithCsvBeanReader();
    }
    public Map<String, String> getFacetLabelToIdMap() throws Exception {
        ConceptIdLabelReader conceptReader = new ConceptIdLabelReader();
        return conceptReader.readWithCsvBeanReader();
    }
    public List<FacetMatchesCsvBean> getWellnessFacetMatches() throws Exception {
        WellnessFacetMatchesReader wellnessFacetMap = new WellnessFacetMatchesReader();
        return wellnessFacetMap.readWithCsvBeanReader();
    }

    private List<String> readFile(String name) {
        BufferedReader in = null;
        List<String> myList = new ArrayList<String>();
        try {

            //System.out.println("starting");
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(name);
            in = new BufferedReader(new InputStreamReader(inStream));
            String str;
            while ((str = in.readLine()) != null) {
                myList.add(str+"\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return myList;
    }

    // Write the contents to a file
    private void writeOutput(StringBuffer output) throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(RDF_OUTPUT_FILENAME));

        //write contents of StringBuffer to a file
        bwr.write(output.toString());

        //flush the stream
        bwr.flush();

        //close the stream
        bwr.close();
    }

}
