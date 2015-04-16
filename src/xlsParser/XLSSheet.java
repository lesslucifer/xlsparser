/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;

/**
 *
 * @author Salm
 */
public class XLSSheet {
    private final String name;
    private final Map<String, Integer> headerMap;
    private final List<XLSRecord> rows;

    public XLSSheet(String name, Sheet sheet) {
        this.name = name;
        int nRows = sheet.getRows();
        if (nRows < 1)
        {
            throw new IllegalStateException(
                    "Sheet row must be greater than 0"
                + " (for header) [" + sheet.getName() + "]");
        }
        
        Cell[] header = sheet.getRow(0);
        headerMap = new HashMap(header.length);
        for (int i = 0; i < header.length; ++i)
        {
            headerMap.put(header[i].getContents(), i);
        }
        
        this.rows = new ArrayList(nRows - 1);
        for (int i = 1; i < nRows; ++i)
        {
            Cell[] row = sheet.getRow(i);
            rows.add(new XLSRecord(headerMap, row));
        }
    }
    
    public List<XLSRecord> getRecords()
    {
        return rows;
    }

    public String getName() {
        return name;
    }
}
