package org.healthwise.quantumleap.parsing;

import org.healthwise.quantumleap.parsing.beans.Concept;
import org.healthwise.quantumleap.parsing.beans.TestsFacetMatches;

import java.io.*;
import java.util.*;

public class Runner {

    // Boilerplate RDF to append at the top
    private final String RDF_HEADER_FILENAME = "rdf_header.txt";
    // Outtput filename
    private final String RDF_OUTPUT_FILENAME = "/Users/akimball/Dev/ontology-out/hwcv-generated-tests.rdf";

    Map<Integer, Concept> idToConcepts;
    Map<String, Concept> conceptIdToConcepts;
    List<TestsFacetMatches> tfmList;

    public static void main(String[] args) throws Exception {
        Runner runner = new Runner();
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
        tfmList = getTestsFacetMatches();
        this.addFacetIdsToConcepts();
        this.conceptIdToConcepts = buildConceptIdToConceptMap();

        this.merge();

        StringBuffer output = new StringBuffer();
        readHeaderIntoOutput(output);
        this.render(output);
        output.append("</rdf:RDF>\n");
        this.writeOutput(output);

    }

    private void render(StringBuffer output) {
        Iterator<Integer> iter = idToConcepts.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            Concept c = idToConcepts.get(id);
            output.append(c.render());
        }
    }

    private void addFacetIdsToConcepts() throws Exception {
        // Iterate over the Facet object and plug in the ids
        Iterator<Integer> iter = idToConcepts.keySet().iterator();
        Map<String, String> facetLabelToIdMap = getFacetLabelToIdMap();
        while (iter.hasNext()) {
            Integer id = iter.next();
            Concept c = idToConcepts.get(id);
            String label = c.getLabel();
            String fidpId = facetLabelToIdMap.get(label);
            if (fidpId == null || fidpId.isEmpty()) {
                System.out.println("Can't find an id for label: "+label);
                fidpId="MISSING";
            }
            c.setFacetId(fidpId);
        }
    }

    private Map<String, Concept> buildConceptIdToConceptMap() {
        Map<String, Concept> cIdMap = new HashMap();
        Set keys = idToConcepts.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            Integer id = (Integer) iter.next();
            Concept c = idToConcepts.get(id);
            cIdMap.put(c.getFacetId(), c);

        }
        return cIdMap;
    }

    private void merge() throws Exception {
        // Iterate over every TFM object.  Find the corresponding concept, and sync changes.
        Iterator<TestsFacetMatches> iter = getTestsFacetMatches().iterator();
        while (iter.hasNext()) {
            TestsFacetMatches currentTFM = iter.next();
            String rdid = currentTFM.getRdId();
            String conceptId = currentTFM.getConceptId();
            Concept c = conceptIdToConcepts.get(conceptId);
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
                    c.addToScopeNotes("RD Label is " + currentTFM.getRdLabel());
                }
                if (currentTFM.getRdType() != null) {
                    c.addToScopeNotes("RD Type is " + currentTFM.getRdType());
                }
            }
        }
    }

    public Map<Integer, Concept>  getConcepts() throws Exception {
        ThesaurusParser parser = new ThesaurusParser();
        return parser.buildRDF();

    }

    public List<TestsFacetMatches> getTestsFacetMatches() throws Exception {
        TestsFacetMatchesReader testFacetMap = new TestsFacetMatchesReader();
        return testFacetMap.readWithCsvBeanReader();
    }

    public Map<String, String> getFacetLabelToIdMap() throws Exception {
        ConceptIdLabelReader conceptReader = new ConceptIdLabelReader();
        return conceptReader.readWithCsvBeanReader();
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
