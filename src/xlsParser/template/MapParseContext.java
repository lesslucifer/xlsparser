/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.Map;

/**
 *
 * @author Salm
 */
class MapParseContext extends XLSParseContext {
    private final String xls;
    private final Object key;
    private final Map<?, ?> value;

    MapParseContext(XLSParseContext p, String xls,
            Object key, Map<?, ?> val) {
        super(p);
        this.xls = xls;
        this.key = key;
        this.value = val;
    }

    @Override
    protected Object getDataProtected(String field) {
        if ("key".equals(field))
            return key;
        
        if ("xls".equals(field))
            return xls;
        
        if (field.startsWith("value"))
        {
            int subIndex = field.indexOf("->");
            if (subIndex >= 0)
            {
                String valField = field.substring(subIndex + 2);
                return value.get(valField);
            }
        }
        
        return null;
    }
}