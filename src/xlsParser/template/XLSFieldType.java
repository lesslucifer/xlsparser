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
enum XLSFieldType {
    STRING {
        @Override
        public Object parse(XLSFieldTemplate desc, XLSIterator ite) {
            return getFirst(desc, ite);
        }
    },
    INT {
        @Override
        public Object parse(XLSFieldTemplate desc, XLSIterator ite) {
            String data = getFirst(desc, ite);
            data = data.replaceAll(",", "");
            return Double.valueOf(data).intValue();
        }
    },
    LONG {
        @Override
        public Object parse(XLSFieldTemplate desc, XLSIterator ite) {
            return Double.valueOf(getFirst(desc, ite)).longValue();
        }
    },
    DOUBLE {
        @Override
        public Object parse(XLSFieldTemplate desc, XLSIterator ite) {
            return Double.valueOf(getFirst(desc, ite).replace(',', '.'));
        }
    },
    BOOL {
        @Override
        public Object parse(XLSFieldTemplate desc, XLSIterator ite) {
            return Util.toBoolean(getFirst(desc, ite));
        }
    };
    
    public abstract Object parse(XLSFieldTemplate desc, XLSIterator ite);
    private static String getFirst(XLSFieldTemplate desc, XLSIterator ite)
    {
        String xlsField = desc.getField(ite.newIterator(new XLSParseContext(ite.getContext()) {
            @Override
            protected Object getDataProtected(String field) {
                return null;
            }
        }));
        String value = ite.getRecords().get(0).get(xlsField);
        return value;
    }
    
    public static XLSFieldType parse(String s)
    {
        if (s.startsWith("#"))
        {
            s = s.substring(1);
        }
        
        return valueOf(s);
    }
}
