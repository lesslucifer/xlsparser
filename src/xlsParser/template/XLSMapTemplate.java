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
import xlsParser.XLSField;
import xlsParser.XLSParser;
import xlsParser.XLSRecord;

/**
 *
 * @author Salm
 */
class XLSMapTemplate extends XLSTemplate {
    private final XLSTemplate xlsField;
    private final XLSTemplate key;
    private final XLSTemplate value;

    XLSMapTemplate(XLSTemplate parent,
            Map<?, ?> jsObj) {
        super(jsObj, parent);
        
        Object xls = jsObj.get("$xls");
        Object _key = jsObj.get("$key");
        Object _val = jsObj.get("$value");
        
        this.value = XLSTemplate.parseTemplate(Util.byPriority(_val, _key, xls), this);
        XLSTemplate keyTempl = XLSTemplate.parseTemplate(Util.byPriority(_key, xls), this);
        this.key = (keyTempl instanceof XLSNullTemplate)?this.value:keyTempl;
        
        XLSTemplate xlsTempl = XLSTemplate.parseTemplate(Util.byPriority(xls), this);
        this.xlsField = XLSTemplate.extractRaw(xlsTempl, this.key, this.value);
    }
    
    @Override
    public Object parse(XLSIterator ite) {
        String xls = (String) this.xlsField.parse(ite
                .newIterator(new MapParseContext(ite.getContext(), null, null, Collections.EMPTY_MAP)));
        List<List<XLSRecord>> exportedRecords = XLSParser.exportRecords(ite.getRecords(), xls);
        
        Map<Object, Object> out = new LinkedHashMap(exportedRecords.size());
        exportedRecords.forEach((rs) -> {
            Object _key = key.parse(ite.newIterator(key.getSheet(ite), rs,
                    new MapParseContext(ite.getContext(), xls, key, out)));
            Object _val = value.parse(ite.newIterator(value.getSheet(ite), rs,
                    new MapParseContext(ite.getContext(), xls, _key, out)));
            
            out.put(_key, _val);
        });
        
        return out;
    }
}