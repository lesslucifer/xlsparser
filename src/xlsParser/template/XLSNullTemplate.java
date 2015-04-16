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
class XLSNullTemplate extends XLSTemplate {
    XLSNullTemplate(String sheet, XLSTemplate parent) {
        super(sheet, parent);
    }
    
    public static final XLSNullTemplate INST = new XLSNullTemplate(null, null);

    @Override
    public Object parse(XLSIterator ite) {
        return null;
    }
}
