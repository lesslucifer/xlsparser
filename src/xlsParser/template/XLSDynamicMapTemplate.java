/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

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
class XLSDynamicMapTemplate extends XLSTemplate {
    private final XLSTemplate xlsField;
    private final XLSTemplate key;
    private final XLSTemplate value;

    XLSDynamicMapTemplate(XLSTemplate parent, Map<?, ?> jsObj) {
        super(jsObj, parent);
        Object xls = jsObj.get("$xls");
        Object _key = jsObj.get("$key");
        Object _val = jsObj.get("$value");
        
        this.xlsField = XLSTemplate.parseTemplate(Util.byPriority(xls, _key, _val), this);
        this.key = XLSTemplate.parseTemplate(Util.byPriority(_key, xls, _val), this);
        this.value = XLSTemplate.parseTemplate(Util.byPriority(_val, _key, xls), this);
    }
    
    @Override
    public Object parse(XLSIterator ite) {
        String xls = (String) xlsField.parse(ite);
        String keyField = (String) this.key.parse(ite);
        String valField = (String) this.value.parse(ite);
        List<List<XLSRecord>> exportedRecords = XLSParser
                .exportRecords(ite.getRecords(), xls);
        
        Map<String, Object> out = new LinkedHashMap(exportedRecords.size());
        exportedRecords.forEach((rs) -> {
            rs.forEach((r) -> {
                String k = r.get(keyField);
                String v = r.get(valField);
                XLSParser.putDynamicMap(out, k, v);
            });
        });
        
        return out;
    }
}