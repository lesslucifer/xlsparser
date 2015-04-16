/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.util.List;
import java.util.Map;

/**
 *
 * @author vinova
 */
public class BaseXLSSheet implements XLSSheet {
    private final String name;
    private final Map<String, Integer> headerMap;
    private final List<XLSRecord> rows;
    
    public BaseXLSSheet(String name, Map<String, Integer> headerMap, List<XLSRecord> rows)
    {
        this.name = name;
        this.headerMap = headerMap;
        this.rows = rows;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<XLSRecord> getRecords() {
        return rows;
    }
    
}
