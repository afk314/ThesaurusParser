package org.healthwise.quantumleap.parsing.readers;

import org.healthwise.quantumleap.parsing.beans.BlacklistLabelBean;
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
public class thesaurusBlacklistReader {

    private final String CSV = "thesaurusBlacklist.csv";
    private List<String> blacklistedLabels = new ArrayList<String>();

    /**
     * An example of reading using CsvBeanReader.
     */
    public List<String> readWithCsvBeanReader() throws Exception {

        ICsvBeanReader beanReader = null;
        try {

            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(CSV);
            //beanReader = new CsvBeanReader(new FileReader(CSV), CsvPreference.STANDARD_PREFERENCE);
            beanReader = new CsvBeanReader(new InputStreamReader(inStream), CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            BlacklistLabelBean blackBean;
            while( (blackBean = beanReader.read(BlacklistLabelBean.class, header, processors)) != null ) {
                blacklistedLabels.add(blackBean.getBlacklistedLabel());
            }

        }
        finally {
            if( beanReader != null ) {
                beanReader.close();
            }
        }
        return blacklistedLabels;
    }

    /**
     * Sets up the processors used for the examples. There are 10 CSV columns, so 10 processors are defined. Empty
     * columns are read as null (hence the NotNull() for mandatory columns).
     *
     * @return the cell processors
     */
    private static CellProcessor[] getProcessors() {



        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // blacklistedLabel
        };

        return processors;
    }


}
