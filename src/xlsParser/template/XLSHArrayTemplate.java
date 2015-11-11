/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import xlsParser.BaseXLSRecord;
import xlsParser.XLSRecord;

/**
 *
 * @author vinova
 */
class XLSHArrayTemplate extends XLSTemplate {
    private final Map<String, String> export;
    private final XLSTemplate value;

    XLSHArrayTemplate(XLSTemplate parent, Map<?, ?> jsObj) {
        super(jsObj, parent);
        Object e = jsObj.get("$export");
        Object v = jsObj.get("$value");
        
        this.export = (Map) e;
        this.value = XLSTemplate.parseTemplate(v, this);
    }
    
    @Override
    public Object parse(XLSIterator ite) {
        List<XLSRecord> exportedRecords = this.exportRecord(ite.getRecords());
        
        List<Object> out = new LinkedList();
        exportedRecords.forEach((r) -> {
            List<XLSRecord> rList = Arrays.asList(r);
            out.add(value.parse(new XLSIterator(ite, null, rList, null)));
        });
        
        return out;
    }
    
    private Map<String, Integer> headerMap = null;
    public List<XLSRecord> exportRecord(List<XLSRecord> recs)
    {
        int i = 0;
        if (headerMap == null)
        {
            headerMap = new HashMap();
            for (Map.Entry<String, String> entrySet : export.entrySet()) {
                String k = entrySet.getKey();
                headerMap.put(k, i++);
            }
        }
        
        XLSRecord r = recs.get(0);
        List<XLSRecord> rs = new LinkedList();
        boolean hasField = true;
        String[] data = new String[headerMap.size()];

        while (true)
        {
            int j = 0;
            for (Map.Entry<String, String> entry : this.export.entrySet()) {
                String field = String.format(entry.getValue(), rs.size() + 1);

                Integer index = r.getHeaderIndex(field);
                if (index == null)
                {
                    hasField = false;
                    break;
                }
                
                String f = r.get(index);
                if (f == null || f.isEmpty())
                {
                    hasField = false;
                    break;
                }

                data[j] = f;
                ++j;
            }

            if (!hasField)
            {
                break;
            }

            rs.add(new BaseXLSRecord(headerMap, data));
        }

        return rs;
    }
}
