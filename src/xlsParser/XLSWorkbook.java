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
import jxl.Sheet;

/**
 *
 * @author Salm
 */
public class XLSWorkbook {
    private final Map<String, XLSSheet> mSheets;
    private final List<XLSSheet> lSheets;

    public XLSWorkbook(String[] names, Sheet[] sheets) {
        this.lSheets = new ArrayList(sheets.length);
        this.mSheets = new HashMap(sheets.length);
        
        for (int i = 0; i < sheets.length; i++) {
            XLSSheet sheet = new XLSSheet(names[i], sheets[i]);
            lSheets.add(sheet);
            mSheets.put(names[i], sheet);
        }
    }
    
    public XLSSheet getSheet(String sheet)
    {
        return mSheets.get(sheet);
    }
    
    public XLSSheet getSheet(int i)
    {
        return lSheets.get(i);
    }
}
