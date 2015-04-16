/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import xlsParser.Util;

/**
 *
 * @author Salm
 */
abstract class XLSParseContext {
    private final XLSParseContext p;

    public XLSParseContext(XLSParseContext p) {
        this.p = p;
    }
    
    public XLSParseContext parent()
    {
        return p;
    }
    
    public Object getData(String field)
    {
        if (Util.isEmpty(field))
            return null;
        
        return getDataProtected(field);
    }
    
    protected abstract Object getDataProtected(String field);
}
