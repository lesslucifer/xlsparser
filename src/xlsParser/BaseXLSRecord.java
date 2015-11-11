/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.util.Map;

/**
 *
 * @author vinova
 */
public class BaseXLSRecord implements XLSRecord {
    private final Map<String, Integer> headerMap;
    private final String[] contents;

    public BaseXLSRecord(Map<String, Integer> headerMap, String[] rows) {
        this.headerMap = headerMap;
        contents = new String[Math.max(rows.length, headerMap.size())];
        System.arraycopy(rows, 0, contents, 0, rows.length);
        for (int i = rows.length; i < headerMap.size(); ++i)
        {
            contents[i] = "";
        }
    }
    
    @Override
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
    
    @Override
    public String get(int index)
    {
        return contents[index];
    }
    
    @Override
    public int size()
    {
        return contents.length;
    }
    
    @Override
    public Integer getHeaderIndex(String header)
    {
        return headerMap.get(header);
    }
    
    @Override
    public String[] getContents()
    {
        return this.contents;
    }
}
