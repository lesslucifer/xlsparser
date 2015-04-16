/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.Map;
import xlsParser.Util;

/**
 *
 * @author Salm
 */
class ObjectParseContext extends XLSParseContext {
    private final Map<?, ?> data;

    public ObjectParseContext(XLSParseContext p, Map<?, ?> data) {
        super(p);
        this.data = data;
    }

    @Override
    protected Object getDataProtected(String field) {
        if (Util.isEmpty(field))
            return null;
        
        return data.get(field);
    }
}
