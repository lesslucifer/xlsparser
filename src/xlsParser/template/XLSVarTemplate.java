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
class XLSVarTemplate extends XLSTemplate {
    private final String[] query;

    XLSVarTemplate(XLSTemplate parent, Map<?, ?> jsObj) {
        super(jsObj, parent);
        String q = (String) jsObj.get("$value");
        this.query = q.split("\\.");
    }

    @Override
    public Object parse(XLSIterator ite) {
        XLSParseContext context = ite.getContext();
        for (String q : query) {
            if ("$p".equals(q))
            {
                context = context.parent();
            }
            else
            {
                return context.getData(q);
            }
        }
        
        return null;
    }
}
