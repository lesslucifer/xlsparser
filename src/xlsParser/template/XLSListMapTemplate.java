/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import xlsParser.Util;
import xlsParser.XLSParser;
import xlsParser.XLSRecord;

/**
 *
 * @author Salm
 */
class XLSListMapTemplate extends XLSTemplate {
    private final XLSTemplate xlsField;
    private final XLSTemplate value;

    XLSListMapTemplate(XLSTemplate parent,
            Map<?, ?> jsObj) {
        super(jsObj, parent);
        
        Object xls = jsObj.get("$xls");
        Object _key = jsObj.get("$key");
        Object _val = jsObj.get("$value");
        
        this.value = XLSTemplate.parseTemplate(Util.byPriority(_val, _key, xls), this);
        XLSTemplate xlsTempl = XLSTemplate.parseTemplate(Util.byPriority(_key, xls), this);
        this.xlsField = (xlsTempl instanceof XLSNullTemplate)?
                XLSTemplate.extractRaw(this.value):xlsTempl;
    }
    
    @Override
    public Object parse(XLSIterator ite) {
        String xls = (String) this.xlsField.parse(ite.newIterator(
            new MapParseContext(ite.getContext(), null, null, Collections.EMPTY_MAP)));
        List<List<XLSRecord>> exportedRecords = XLSParser.exportRecords(ite.getRecords(), xls);
        
        Map<Integer, Object> out = new LinkedHashMap(exportedRecords.size());
        for (int i = 0; i < exportedRecords.size(); ++i)
        {
            List<XLSRecord> rs = exportedRecords.get(i);
            Object _val = value.parse(ite.newIterator(value.getSheet(ite), rs,
                    new MapParseContext(ite.getContext(), xls, String.valueOf(i), out)));
            
            out.put(i, _val);
        }
        
        return out;
    }
}
