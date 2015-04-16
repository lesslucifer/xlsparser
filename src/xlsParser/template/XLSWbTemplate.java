/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.Map;
import xlsParser.*;

/**
 *
 * @author Salm
 */
public class XLSWbTemplate {
    private final XLSTemplate templ;
    
    public XLSWbTemplate(Map<?, ?> jsObj) {
        templ = XLSTemplate.parseTemplate(jsObj, null);
    }
    
    public Object parse(XLSWorkbook wb)
    {
        XLSIterator rootIte = new XLSIterator(wb, null, null);
        return templ.parse(rootIte.newIterator(templ.getSheet(rootIte), null));
    }
}
