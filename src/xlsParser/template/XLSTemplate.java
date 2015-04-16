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
abstract class XLSTemplate {
    private final XLSTemplate sheet;
    protected final XLSTemplate parent;
    
    protected XLSTemplate(XLSTemplate parent) {
        this.sheet = null;
        this.parent = parent;
    }

    protected XLSTemplate(String sheet, XLSTemplate parent) {
        this.sheet = new XLSRawTemplate(parent, sheet);
        this.parent = parent;
    }
    
    protected XLSTemplate(Map<?, ?> jsObj, XLSTemplate parent)
    {
        this.sheet = XLSTemplate.parseTemplate(jsObj.get("$sheet"), parent);
        this.parent = parent;
    }
    
    public String getSheet(XLSIterator ite)
    {
        return (sheet == null)?ite.getSheet().getName():
                (String) this.sheet.parse(ite);
    }

    public abstract Object parse(XLSIterator ite);
    
    public static XLSTemplate parseTemplate(Object obj, XLSTemplate parent)
    {
        if (obj == null)
        {
            return XLSNullTemplate.INST;
        }
        else if (obj instanceof String)
        {
            String sObj = (String) obj;
            if (XLSFieldTemplate.isShortcutTemplate(sObj))
            {
                return new XLSFieldTemplate(parent, sObj);
            }
        }
        else if (obj instanceof Map<?, ?>)
        {
            Map<?, ?> jsObj = (Map<?, ?>) obj;
            if (XLSFieldTemplate.isTemplateDescription(jsObj))
            {
                return new XLSFieldTemplate(parent, jsObj);
            }
            
            XLSTemplateMode mode = XLSTemplateMode.parse((String) jsObj.get("$mode"));
            switch (mode)
            {
                case OBJECT:
                    return new XLSObjectTemplate(jsObj, parent);
                case MAP:
                    return new XLSMapTemplate(parent, jsObj);
                case LIST_MAP:
                    return new XLSListMapTemplate(parent, jsObj);
                case DYNAMIC_MAP:
                    return new XLSDynamicMapTemplate(parent, jsObj);
                case DYNAMIC_HMAP:
                    return new XLSDynamicHMapTemplate(parent, jsObj);
                case HARRAY:
                    return new XLSHArrayTemplate(parent, jsObj);
                case VAR:
                    return new XLSVarTemplate(parent, jsObj);
                default:
                    throw new IllegalArgumentException("Invalid template mode: " + mode);
            }
        }
        
        return new XLSRawTemplate(parent, obj);
    }
    
    static XLSRawTemplate extractRaw(XLSTemplate... templs)
    {
        for (XLSTemplate templ : templs) {
            if (templ instanceof XLSRawTemplate)
                return (XLSRawTemplate) templ;
            
            if (templ instanceof XLSFieldTemplate)
            {
                XLSFieldTemplate fieldTempl = (XLSFieldTemplate) templ;
                if (fieldTempl.getFieldTemlate() instanceof XLSRawTemplate)
                    return (XLSRawTemplate) fieldTempl.getFieldTemlate();
            }
        }
        
        return null;
    }
}