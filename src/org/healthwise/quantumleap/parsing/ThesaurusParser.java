package org.healthwise.quantumleap.parsing;

import org.apache.commons.lang3.StringEscapeUtils;
import org.healthwise.quantumleap.parsing.beans.Concept;

import java.io.*;
import java.util.*;

public class ThesaurusParser {

    private Map facetFileToTypeMap;

    private Map<Integer, Concept> idConceptMap = new HashMap<Integer, Concept>();

    // Thesaurus files - you'll need to add these to the fileSetup method
    private final String TEST_THESAURUS_FILE = "tests.txt";
    private final String PROCEDURE_THESAURUS_FILE = "procedures.txt";
    private final String CONDITIONS_THESAURUS_FILE = "conditions.txt";
    private final String WELLNESS_THESAURUS_FILE = "wellness.txt";

    // Boilerplate RDF to append at the top
    private final String RDF_HEADER_FILENAME = "rdf_header.txt";

    // Outtput filename
    private final String RDF_OUTPUT_FILENAME = "hwcv-generated.owl";


    // ID mgmt
    // Where to start handing out the ids
    private final static int ID_START = 10000;

    // Used to maintain counter state
    private int idCounter = 0;

    // Map where key=FV label and value=HWCV id
    private Map labelToIdMap;

    // Will hold the overall FACET for any idividual, its parent.  IE parent for angiogram is the concept id for TEST
    private String parent = "10003";

    public static void main(String[] args) throws IOException {
	// write your code here
        ThesaurusParser m = new ThesaurusParser();
        m.buildRDF();
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    public Map getLabelToIdMap() {
        return labelToIdMap;
    }

    public void setLabelToIdMap(Map labelToIdMap) {
        this.labelToIdMap = labelToIdMap;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }


    // Reset the concept id counter, we need to pre-spool all of the ids and labels to make bt and nt connections
    private  void resetCount() {
        setIdCounter(ID_START);
    }

    // Setup which files to run
    private void thesaurusFileSetup() {
        facetFileToTypeMap = new HashMap();
        facetFileToTypeMap.put(TEST_THESAURUS_FILE, "10003");
        //facetFileToTypeMap.put(PROCEDURE_THESAURUS_FILE, "10301");
        //facetFileToTypeMap.put(CONDITIONS_THESAURUS_FILE, "10000");
        //facetFileToTypeMap.put(WELLNESS_THESAURUS_FILE, "10007");
    }

    // Iterate over the thesaurus files and build the corresponding RDF
    private void buildRDF() throws IOException {
        //System.out.println("here we buildRDF!!!!");
        StringBuffer output = new StringBuffer();
        setup(output);

        Map parentIdToFileContent = readFiles();
        Set parentIds = parentIdToFileContent.keySet();
        Iterator iter = parentIds.iterator();
        while (iter.hasNext()) {
            String parentIdForFile = (String) iter.next();
            ArrayList fileContents = (ArrayList) parentIdToFileContent.get(parentIdForFile);
            buildRdfForFile(parentIdForFile, fileContents, output);
        }
        output.append(closeRdf());
        writeOutput(output);
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

    // Basic setup wrapper
    private void setup(StringBuffer output) {
        resetCount();
        readHeaderIntoOutput(output);
        thesaurusFileSetup();
    }


    // Prespool every concept and give it an id.  We'll use this concept->id map
    // later when we manage some of the connections betweeen concepts
    private void buildIds(ArrayList sb) {
        setLabelToIdMap(new HashMap());

        Iterator iter = sb.iterator();
        while (iter.hasNext()) {
            String inputLine = (String) iter.next();
            inputLine = inputLine.replaceAll("\\n", "");
            if (inputLine.startsWith("#")) continue;
            if (inputLine.startsWith("[")) continue;
            if (inputLine.isEmpty()) continue;
            if (!inputLine.startsWith(" ")) {
                // We have a new concept
                String baseTerm = stripIllegals(inputLine);
                String id = getIdCounter()+"";
                Concept currentConcept = new Concept(getIdCounter(), baseTerm);
                idConceptMap.put(getIdCounter(), currentConcept);
                getLabelToIdMap().put(baseTerm, getIdCounter());

                setIdCounter(getIdCounter() + 1);
            }
        }
    }


    // Iterate over the actual file contents and build the RDF statements
    // Because of the file structure, when new concepts are encountered, the
    // base of the triples is created but not closed.  Futher relations like
    // BT, NT will be applied to the concept at the top of the stack until a new concept
    // is found and then the previous triple will be closed.  Not pretty code but it work.
    private void buildRdfForFile(String parentId, ArrayList sb, StringBuffer output) {
        int thisRun = 0;

        // build the ID Map first
        buildIds(sb);

        Concept currentConcept = null;

        Iterator iter = sb.iterator();
        while (iter.hasNext()) {
            String inputLine = (String)iter.next();
            inputLine = inputLine.replaceAll("\\n", "");


            // skip comments
            if (inputLine.startsWith("#") || (inputLine.startsWith("[")) || (inputLine.isEmpty())) continue;

            // The file is space sensitive - we are looking for top level concepts that start at index 0
            if (!inputLine.startsWith(" ")) {
                if (thisRun != 0) {
                    output.append(closeConcept());
                }

                currentConcept = buildBaseTriples(inputLine, output, parentId);
            } else {
                // We are indented, so we are working with properties of a concept that we've already created
                if (inputLine.trim().isEmpty()) {
                    continue;
                }

                // Split the line at :
                String[] line = inputLine.split(":");
                String relationType = line[0].trim();

                //No idea why, but some relations have no object and we'll need to skip them
                if (line.length <= 1 || line[1].isEmpty()) {
                    continue;
                }

                String value = line[1].trim();
                if (relationType.equals("BT")) {
                    String broaderRelation = makeSkosBroaderTerm(value, currentConcept);
                    if (broaderRelation != null) {
                        output.append(broaderRelation);
                    }
                } else if (relationType.equals("NT")) {
                    String narrowerRelation = makeSkosNarrowerTerm(value, currentConcept);
                    if (narrowerRelation != null) {
                        output.append(narrowerRelation);
                    }
                } else if (relationType.equals("RT")) {
                    String relatedRelation = makeSkosRelatedTerm(value, currentConcept);
                    if (relatedRelation != null) {
                        output.append(relatedRelation);
                    }
                } else if (relationType.equals("SN")) {
                        output.append(buildScopeNote(value, currentConcept));
                } else if (relationType.equals("UF")) {
                    output.append(buildUsedFor(value, currentConcept));
                } else if (relationType.equals("DEF")) {
                    output.append(buildDefinition(value, currentConcept));
                } else if (relationType.equals("ABV")) {
                        // ABV is not used not used
                } else if (relationType.equals("CL")) {
                        // CL is not used not used
                }
                thisRun++;

            }
        }
        output.append(closeConcept());
    }




    // Clean up some characters which can cause us problems
    private String stripIllegals(String l) {
//        l = l.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("'", "").replaceAll(",", "");
//        l = l.replaceAll(" ", "_").replaceAll("\\\\", "").replaceAll("/", "").replaceAll("\\n", "");

        return l;
    }

    private Concept buildBaseTriples(String label, StringBuffer output, String parentId) {
        StringBuffer b = new StringBuffer();
        Integer id = getIdForLabel(label);
        Concept c = idConceptMap.get(id);
        if (c == null) {
            throw new RuntimeException("Failed to find: "+label);
        }


        b.append(openConcept(getIdForLabel(label)));
        b.append(makeConceptId(getIdForLabel(label)));
        b.append(makeRdfsLabel(label));
        b.append(makeSkosBroader(parentId, c));
        output.append(b.toString());
        return c;
    }

    private String addPrefixToId(String numericId) {
        return "HWCV_0"+numericId;
    }


    private Integer getIdForLabel(String label) {
        Integer id = (Integer) getLabelToIdMap().get(stripIllegals(label));
        return id;
    }

    private String closeConcept() {
        return "</skos:Concept>\n\n";
    }

    private String closeRdf() {
        return "</rdf:RDF>\n\n";
    }

    private String makeSkosBroaderTerm(String term, Concept c) {
        Integer id = getIdForLabel(term);
        c.addToBroader(id+"");
        if (id != null) {
            return "  <skos:broader rdf:resource=\"#HWCV_0"+id+"\"/>\n" ;
        } else {
            return null;
        }
    }



    private String makeSkosNarrowerTerm(String term, Concept c) {
        Integer id = getIdForLabel(term);
        c.addToNarrower(id+"");
        if (id != null) {
            return "  <skos:narrower rdf:resource=\"#HWCV_0"+id+"\"/>\n" ;
        } else {
            return null;
        }
    }

    private String makeSkosRelatedTerm(String term, Concept c) {
        Integer id = getIdForLabel(term);
        c.addToRelated(id+"");
        if (id != null) {
            return "  <skos:related rdf:resource=\"#HWCV_0"+id+"\"/>\n" ;
        } else {
            return null;
        }
    }

    private String xmlEncode(String someString) {
        return StringEscapeUtils.escapeXml11(someString);
    }

    private String buildScopeNote(String note, Concept c) {
        String out = xmlEncode(note);
        try {
            c.addToScopeNotes(out);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return "  <skos:scopeNote>"+out+"</skos:scopeNote>\n" ;
    }

    private String buildDefinition(String def, Concept currentConcept) {
        String encoded = xmlEncode(def);
        currentConcept.setDefinition(encoded);
        return "  <skos:definition>"+encoded+"</skos:defition>\n" ;
    }

    private String buildUsedFor(String ufTerm, Concept currentConcept) {
        String encoded = xmlEncode(ufTerm);
        currentConcept.addToAltLabel(encoded);
        return "  <skos:altLabel>"+encoded+"</skos:altLabel>\n" ;
    }


    private String makeSkosBroader(String id, Concept c) {
        c.addToBroader(id);
        return "  <skos:broader rdf:resource=\"#"+id+"\"/>\n" ;
    }
    private String openConcept(Integer id) {
        return "<skos:Concept rdf:ID=\""+id+"\">\n";
    }

    private String makeConceptId(Integer id) {
        return "  <hwcv_sc:concept_id>"+id+"</hwcv_sc:concept_id>\n";
    }

    private String makeRdfsLabel(String label) {
        String l2 = xmlEncode(label);
        return "  <rdfs:label>"+l2+"</rdfs:label>\n";
    }







    private Map readFiles() {
        Map parentIdToFileContent = new HashMap();
        Set filesToRead = facetFileToTypeMap.keySet();
        Iterator iter = filesToRead.iterator();
        while (iter.hasNext()) {
            String filename = (String) iter.next();
            String parentId = (String) facetFileToTypeMap.get(filename);
            parentIdToFileContent.put(parentId, readFile(filename));

        }
       return parentIdToFileContent;
    }

    private void readHeaderIntoOutput(StringBuffer output) {
        List<String> srcFile = readFile(RDF_HEADER_FILENAME);
        Iterator i = srcFile.iterator();
        while(i.hasNext()) {
            output.append(i.next());
        }
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



}
