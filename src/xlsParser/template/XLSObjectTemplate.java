/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Salm
 */
class XLSObjectTemplate extends XLSTemplate {
    private LinkedHashMap<String, XLSTemplate> data;
    
    XLSObjectTemplate(Map<?, ?> jsObj, XLSTemplate parent) {
        super(jsObj, parent);
        data = new LinkedHashMap(jsObj.size());
        
        jsObj.forEach((k, v) -> {
            String sk = (String) k;
            if (!sk.startsWith("$"))
            {
                data.put(sk, XLSTemplate.parseTemplate(v, this));
            }
        });
    }
    
    @Override
    public Object parse(XLSIterator ite)
    {
        List<Object> rem = new LinkedList();
        Map<Object, Object> out = new LinkedHashMap(data.size());
        XLSParseContext context = new ObjectParseContext(ite.getContext(), out);
        
        data.forEach((f, t) -> {
            Object innerData = t.parse(ite.newIterator(
                    t.getSheet(ite.newIterator(null, context)),
                    context));
            if (innerData != null && f.startsWith("__") && (innerData instanceof Map))
            {
                out.putAll((Map) innerData);
            }
            else if (!f.startsWith("&&"))
            {
                if (f.startsWith("-"))
                {
                    f = f.substring(1);
                    rem.add(f);
                }
                
                out.put(f, innerData);
            }
        });
       
        rem.forEach((r) -> {out.remove(r);});
        
        return out;
    }
}
