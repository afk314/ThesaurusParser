package org.healthwise.quantumleap.parsing.readers;

import org.healthwise.quantumleap.parsing.beans.FacetBean;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akimball on 8/8/15.
 */
public class ConceptIdLabelReader {

    private final String CSV = "conceptIdToLabel.csv";
    private Map<String, String> labelToIdMap = new HashMap<String, String>();

    /**
     * An example of reading using CsvBeanReader.
     */
    public  Map<String, String> readWithCsvBeanReader() throws Exception {

        ICsvBeanReader beanReader = null;
        try {

            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(CSV);
            //beanReader = new CsvBeanReader(new FileReader(CSV), CsvPreference.STANDARD_PREFERENCE);
            beanReader = new CsvBeanReader(new InputStreamReader(inStream), CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            FacetBean facet;
            while( (facet = beanReader.read(FacetBean.class, header, processors)) != null ) {
                labelToIdMap.put(facet.getFacetValueLabel(), facet.getFacetValueId());
            }

        }
        finally {
            if( beanReader != null ) {
                beanReader.close();
            }
        }
        return labelToIdMap;
    }

    /**
     * Sets up the processors used for the examples. There are 10 CSV columns, so 10 processors are defined. Empty
     * columns are read as null (hence the NotNull() for mandatory columns).
     *
     * @return the cell processors
     */
    private static CellProcessor[] getProcessors() {



        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // facetName
                new Unique(), // facetValueId
                new NotNull(), // facetValueLabel
        };

        return processors;
    }


}
