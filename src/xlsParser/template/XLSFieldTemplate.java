/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Salm
 */
class XLSFieldTemplate extends XLSTemplate {
    private final XLSFieldType type;
    private final XLSTemplate xlsField;
    
    XLSFieldTemplate(XLSTemplate parent, XLSFieldType type, String xlsField) {
        super(parent);
        this.type = type;
        this.xlsField = new XLSRawTemplate(this, xlsField);
    }

    XLSFieldTemplate(XLSTemplate parent, String shortcut) {
        super(parent);
        if (!isShortcutTemplate(shortcut))
            throw new IllegalArgumentException("Invalid shortcut template");
        
        if (shortcut.startsWith("@"))
        {
            this.type = XLSFieldType.STRING;
            this.xlsField = new XLSRawTemplate(this, shortcut.substring(1));
        }
        else
        {
            int splitIndex = shortcut.indexOf("__");
            this.type = XLSFieldType.parse(shortcut.substring(0, splitIndex).trim().toUpperCase());
            this.xlsField = new XLSRawTemplate(this, shortcut.substring(splitIndex + 2));
        }
    }

    XLSFieldTemplate(XLSTemplate parent, Map<?, ?> jsObj) {
        super(jsObj, parent);
        this.type = getType(jsObj);
        this.xlsField = XLSTemplate.parseTemplate(jsObj.get("$xls"), this);
    }
    
    private XLSFieldType getType(Map<?, ?> jsObj)
    {
        try
        {
            String _type = (String) jsObj.get("$type");
            return XLSFieldType.parse(_type);
        }
        catch (Exception ex)
        {
            return XLSFieldType.STRING;
        }
    }
    
    public static boolean isShortcutTemplate(String s)
    {
        return s.matches("^#[^_]*__[^\\$#]*") || s.startsWith("@");
    }
    
    private static final Set<String> TEMPL_DESC_KEYS =
            new HashSet(Arrays.asList(new String[] {"$type", "$xls"}));
    public static boolean isTemplateDescription(Map<?, ?> jsObj)
    {
        return TEMPL_DESC_KEYS.stream().anyMatch((k) -> (jsObj.containsKey(k)));
    }

    public XLSFieldType getType() {
        return type;
    }
    
    public String getField(XLSIterator ite)
    {
        return (String) this.xlsField.parse(ite);
    }
    
    public XLSTemplate getFieldTemlate()
    {
        return this.xlsField;
    }

    @Override
    public Object parse(XLSIterator ite)
    {
        return type.parse(this, ite);
    }
}
