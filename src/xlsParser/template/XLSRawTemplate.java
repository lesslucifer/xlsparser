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
class XLSRawTemplate extends XLSTemplate {
    private final Object raw;

    XLSRawTemplate(XLSTemplate parent, Object raw) {
        super(parent);
        this.raw = raw;
    }

    @Override
    public Object parse(XLSIterator ite) {
        return raw;
    }
}
