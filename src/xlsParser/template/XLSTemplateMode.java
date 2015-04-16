/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

/**
 *
 * @author Salm
 */
enum XLSTemplateMode {
    OBJECT,
    MAP,
    LIST_MAP,
    DYNAMIC_MAP,
    DYNAMIC_HMAP,
    HARRAY,
    VAR;
    
    public static XLSTemplateMode parse(String s)
    {
        try
        {
            s = s.trim().toUpperCase();
            return valueOf(s);
        }
        catch (Exception ex)
        {
            return OBJECT;
        }
    }
}
