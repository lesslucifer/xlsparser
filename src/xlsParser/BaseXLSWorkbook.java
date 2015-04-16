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

/**
 *
 * @author vinova
 */
public class BaseXLSWorkbook implements XLSWorkbook {
    private final Map<String, XLSSheet> mSheets;
    private final List<XLSSheet> lSheets;

    public BaseXLSWorkbook(List<XLSSheet> sheets) {
        this.lSheets = new ArrayList(sheets.size());
        this.mSheets = new HashMap(sheets.size());
        
        sheets.forEach((sheet) -> {
            lSheets.add(sheet);
            mSheets.put(sheet.getName(), sheet);
        });
    }
    
    @Override
    public XLSSheet getSheet(String sheet)
    {
        return mSheets.get(sheet);
    }
    
    @Override
    public XLSSheet getSheet(int i)
    {
        return lSheets.get(i);
    }
}
