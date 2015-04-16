/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsconfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import xlsParser.*;

/**
 *
 * @author Salm
 * @param <E>
 */
public class BaseXLSConfigurator<E> extends AbstractXLSConfigurator {
    protected final Class<E> clazz;
    protected final String file;

    public BaseXLSConfigurator(Class<E> clazz, String file) {
        this.clazz = clazz;
        this.file = file;
    }

    public BaseXLSConfigurator(Class<E> clazz) {
        XLSWorkbookObject annWB = clazz.getAnnotation(XLSWorkbookObject.class);
        if (annWB == null) throw new IllegalArgumentException(
                "Require XLSWorkbookObject annotation");
        
        String workbook = annWB.value();
        if (workbook == null || workbook.length() <= 0)
            throw new IllegalArgumentException(
                "Invalid workbook " + workbook);
        
        this.clazz = clazz;
        this.file = workbook;
    }
    
    @Override
    public void runConfig() throws Exception {
        XLSWorkbook workbook = XLSReader.read(getInPath() + "//" + file);

        XLSWorkbookObject annWB = clazz.getAnnotation(XLSWorkbookObject.class);
        E e;
        if (annWB == null || !annWB.sheetField())
        {
            e = XLSParser.parse(clazz, workbook);
        }
        else
        {
            e = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                XLSSheetField sheetAnn = field.getAnnotation(XLSSheetField.class);
                if (sheetAnn == null) continue;
                
                XLSSheet sheet = workbook.getSheet(sheetAnn.value());
                Object fieldVal = XLSParser.parse(field, sheet);

                field.setAccessible(true);
                field.set(e, fieldVal);
            }
        }
        
        postConfig(e);
    }
    
    protected void postConfig(E e) throws Exception {}
}
