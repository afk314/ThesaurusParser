package org.healthwise.quantumleap.parsing;

import org.healthwise.quantumleap.parsing.beans.TestsFacetMatches;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimball on 8/8/15.
 */
public class TestsFacetMatchesReader {

    private final String CSV = "tfm.csv";
    private List<TestsFacetMatches> lines = new ArrayList<TestsFacetMatches>();
    /**
     * An example of reading using CsvBeanReader.
     */
    public  List<TestsFacetMatches> readWithCsvBeanReader() throws Exception {

        ICsvBeanReader beanReader = null;
        try {

            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(CSV);
            //beanReader = new CsvBeanReader(new FileReader(CSV), CsvPreference.STANDARD_PREFERENCE);
            beanReader = new CsvBeanReader(new InputStreamReader(inStream), CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            TestsFacetMatches tfm;
            while( (tfm = beanReader.read(TestsFacetMatches.class, header, processors)) != null ) {
                lines.add(tfm);
                System.out.println(String.format("conceptId=%s, conceptName=%s", beanReader.getLineNumber(),
                        beanReader.getRowNumber(), tfm));
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
                new Optional(), // dmaps
                new Optional(), // rdLabel
                new Optional(), // rdType

        };

        return processors;
    }


}
