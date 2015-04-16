/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.util.Map;
import jxl.Cell;

/**
 *
 * @author Salm
 */
public class XLSRecord
{
    private final Map<String, Integer> headerMap;
    private final String[] contents;

    public XLSRecord(Map<String, Integer> headerMap, Cell[] rows) {
        this.headerMap = headerMap;
        contents = new String[headerMap.size()];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = (rows.length > i)?rows[i].getContents():"";
        }
    }

    public XLSRecord(Map<String, Integer> headerMap, String[] rows) {
        this.headerMap = headerMap;
        contents = new String[rows.length];
        System.arraycopy(rows, 0, contents, 0, rows.length);
    }
    
    public String get(String field)
    {
        Integer index = headerMap.get(field);
        if (index == null)
        {
            throw new IllegalArgumentException("Invalid field [" +
                    field + "] must be in " + headerMap.keySet());
        }
        
        return contents[index];
    }
    
    public String get(int index)
    {
        return contents[index];
    }
    
    public int size()
    {
        return contents.length;
    }
    
    Integer getHeaderIndex(String header)
    {
        return headerMap.get(header);
    }
    
    String[] getContents()
    {
        return this.contents;
    }
}
