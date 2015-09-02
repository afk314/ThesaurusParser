package org.healthwise.quantumleap.parsing.readers;

import org.healthwise.quantumleap.parsing.beans.FacetMatchesCsvBean;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimball on 8/8/15.
 */
public class ConditionsFacetMatchesReader {

    private final String CSV = "cfm.csv";
    private List<FacetMatchesCsvBean> lines = new ArrayList<FacetMatchesCsvBean>();
    /**
     * An example of reading using CsvBeanReader.
     */
    public  List<FacetMatchesCsvBean> readWithCsvBeanReader() throws Exception {

        ICsvBeanReader beanReader = null;
        try {

            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(CSV);
            //beanReader = new CsvBeanReader(new FileReader(CSV), CsvPreference.STANDARD_PREFERENCE);
            beanReader = new CsvBeanReader(new InputStreamReader(inStream), CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            FacetMatchesCsvBean tfm;
            while( (tfm = beanReader.read(FacetMatchesCsvBean.class, header, processors)) != null ) {
                lines.add(tfm);
                //System.out.println(String.format("conceptId=%s, conceptName=%s", beanReader.getLineNumber(),
                //        beanReader.getRowNumber(), tfm));
            }

        }
        finally {
            if( beanReader != null ) {
                beanReader.close();
            }
        }
        return lines;
    }

    /**
     * Sets up the processors used for the examples. There are 10 CSV columns, so 10 processors are defined. Empty
     * columns are read as null (hence the NotNull() for mandatory columns).
     *
     * @return the cell processors
     */
    private static CellProcessor[] getProcessors() {



        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // comboId (must be unique)
                new NotNull(), // facetId
                new NotNull(), // facetName
                new NotNull(), // conceptId
                new NotNull(), // conceptName
                new Optional(), // rdId
                new Optional(), // rdLabel
                new Optional(), // rdType
                new Optional(), // cuiMap
                new Optional(), // documentToCuiMap
                new Optional(), // scopeNotes
                new NotNull(), // lifecyceStage


        };

        return processors;
    }


}
