/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import xlsParser.XLSParser;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import xlsParser.Util;
import xlsParser.XLSRecord;

/**
 *
 * @author Salm
 */
class XLSDynamicHMapTemplate extends XLSTemplate {
    private final XLSTemplate key;
    private final XLSTemplate value;

    XLSDynamicHMapTemplate(XLSTemplate parent, Map<?, ?> jsObj) {
        super(jsObj, parent);
        Object xls = jsObj.get("$xls");
        Object k = jsObj.get("$key");
        Object v = jsObj.get("$value");
        
        
        this.key = XLSTemplate.parseTemplate(Util.byPriority(k, xls, v), this);
        this.value = XLSTemplate.parseTemplate(Util.byPriority(v, k, xls), this);
    }
    
    @Override
    public Object parse(XLSIterator ite) {
        String keyField = (String) this.key.parse(ite);
        String valField = (String) this.value.parse(ite);
        List<XLSRecord> exportedRecords = XLSParser
                .exportColumnRecords(ite.getRecords(), keyField, valField);
        
        Map<String, Object> out = new LinkedHashMap(exportedRecords.size());
        exportedRecords.forEach((r) -> {
            String k = r.get(keyField);
            String v = r.get(valField);
            XLSParser.putDynamicMap(out, k, v);
        });
        
        return out;
    }
}
