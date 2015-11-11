package xlsParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Salm
 */
public class XLSParser {
//    public static <E> E parse(Class<E> clazz) throws Exception
//    {
//        XLSObject objAnn = clazz.getAnnotation(XLSObject.class);
//        if (objAnn == null) throw new IllegalArgumentException(
//                clazz + " must have XLSObject annotation");
//        
//        String file = objAnn.file();
//        if (isEmpty(file)) throw new IllegalArgumentException(
//                clazz + " missing field [file] in XLSObject annotation");
//        
//        XLSWorkbook workbook = XLSReader.read(file);
//        return parse(clazz, workbook);
//    }
    
    public static <E> E parse(Class<E> clazz, XLSWorkbook workbook)
            throws Exception
    {
        XLSObject objAnn = clazz.getAnnotation(XLSObject.class);
        String sheetName = (objAnn != null)?objAnn.sheet():"";
        XLSSheet sheet = (!isEmpty(sheetName))?workbook.getSheet(sheetName):
                workbook.getSheet(0);
        
        return parse(clazz, sheet);
    }
    
    public static <E> E parse(Class<E> clazz, XLSSheet sheet) throws Exception
    {
        return parse(clazz, sheet.getRecords());
    }
    
    public static Object parse(Field field, XLSSheet sheet) throws Exception
    {
        return parse(field, sheet.getRecords());
    }
    
    public static Object parse(Field field, List<XLSRecord> records) throws Exception
    {
        XLSField fieldAnn = field.getAnnotation(XLSField.class);
        String xlsName = (fieldAnn == null || isEmpty(fieldAnn.value()))
                ?field.getName():fieldAnn.value();
        return parseField(field.getType(),
                new Annotations(field),
                new Generics(field),
                xlsName, records);
    }
    
    public static <E> E parse(Class<E> clazz, List<XLSRecord> records)
            throws Exception
    {
        E e = clazz.newInstance();
        return parse(e, clazz, records);
    }
    
    public static <E> E parse(E e, Class<E> clazz, List<XLSRecord> records)
            throws Exception
    {
        XLSObject objAnn = clazz.getAnnotation(XLSObject.class);
        if (objAnn != null)
        {
            Method parseMethod;
            try
            {
                parseMethod = clazz.getMethod(objAnn.parse(), List.class);
            }
            catch (NoSuchMethodException ex)
            {
                parseMethod = null;
            }
            
            if (parseMethod != null)
            {
                parseMethod.invoke(e,  records);
                return e;
            }
        }
        boolean annCheck = objAnn == null || !objAnn.auto();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }
            
            XLSField ann = field.getAnnotation(XLSField.class);
            if (annCheck && ann == null)
            {
                continue;
            }
            
            field.setAccessible(true);
            String fXlsField = (ann == null)?null:ann.value();
            fXlsField = (!isEmpty(fXlsField))?fXlsField:field.getName();
            Object fieldValue = parseField(field.getType(),
                    new Annotations(field), new Generics(field),
                    fXlsField, records);
            field.set(e, fieldValue);
        }
        
        return e;
    }
    
    private static Object parseField(Class<?> fieldType,
            Annotations ann, Generics gen,
            String xlsName, List<XLSRecord> records)
            throws Exception
    {
        Object primitive = parsePrimitive(fieldType, xlsName, records);
        if (primitive != null) return primitive;
        
        if (List.class.isAssignableFrom(fieldType))
        {
            Class<?> elemType = gen.nextGeneric();
            List<List<XLSRecord>> exportedRecords = exportRecords(records, xlsName);

            List<Object> list = new ArrayList();
            for (List<XLSRecord> exportedRecord : exportedRecords) {
                list.add(parseField(elemType, ann, gen, xlsName, exportedRecord));
            }
            
            return list;
        }
        else if (Map.class.isAssignableFrom(fieldType))
        {
            Class<?> genericKey = gen.nextGeneric();
            Class<?> genericVal = gen.nextGeneric();

            XLSMap mapAnn = ann.get(XLSMap.class);
            String keyXLSField = xlsName;
            String valXLSField = (mapAnn != null)?mapAnn.mapField():null;
            valXLSField = (!isEmpty(valXLSField))?valXLSField:keyXLSField;
            Class<?> dynClass = (mapAnn != null)?mapAnn.dynamicClass():String.class;

            XLSMapMode mode = (mapAnn != null)?mapAnn.mode():XLSMapMode.DEFAULT;
            if (mode == XLSMapMode.DEFAULT)
            {
                List<List<XLSRecord>> exportedRecords = exportRecords(records, keyXLSField);
                Map<Object, Object> map = new LinkedHashMap();
                for (List<XLSRecord> exportedRecord : exportedRecords) {
                    Object key = parseField(genericKey, ann, gen, keyXLSField, exportedRecord);
                    Object val = parseField(genericVal, ann, gen, valXLSField, exportedRecord);

                    map.put(key, val);
                }
                
                return map;
            }
            else if (mode == XLSMapMode.DYNAMIC_MAP)
            {
                List<List<XLSRecord>> exportedRecords = exportRecords(records, keyXLSField);
                if (!String.class.isAssignableFrom(genericKey) ||
                    !Object.class.isAssignableFrom(genericVal))
                {
                    throw new IllegalStateException("Dynamic map only support"
                            + " for Map<String, Object>");
                }

                Map<String, Object> map = new LinkedHashMap();
                for (List<XLSRecord> exportedRecord : exportedRecords) {
                    String key = (String) parseField(String.class,
                            ann, gen, keyXLSField, exportedRecord);
                    Object val = parseField(dynClass,
                            ann, gen, valXLSField, exportedRecord);

                    putDynamicMap(map, key, val);
                }

                return map;
            }
            else if (mode == XLSMapMode.DYNAMIC_HMAP)
            {
                if (!String.class.isAssignableFrom(genericKey) ||
                    !Object.class.isAssignableFrom(genericVal))
                {
                    throw new IllegalStateException("Dynamic horizontal map only support"
                            + " for Map<String, Object>");
                }

                List<XLSRecord> exportedRecords = exportColumnRecords(records, keyXLSField, valXLSField);
                Map<String, Object> map = new LinkedHashMap();
                for (XLSRecord exportedRecord : exportedRecords) {
                    List<XLSRecord> exported = Arrays.asList(exportedRecord);
                    String key = (String) parseField(String.class,
                            ann, gen, keyXLSField, exported);
                    Object val = parseField(dynClass,
                            ann, gen, valXLSField, exported);

                    putDynamicMap(map, key, val);
                }

                return map;
            }
            else if (mode == XLSMapMode.LIST_MAP)
            {
                List<List<XLSRecord>> exportedRecords = exportRecords(records, keyXLSField);
                if (!Integer.class.isAssignableFrom(genericKey))
                {
                    throw new IllegalStateException("List map only support"
                            + "for Map<Integer, ?>");
                }

                Map<Integer, Object> map = new LinkedHashMap();
                for (List<XLSRecord> exportedRecord : exportedRecords) {
                    Object val = parseField(genericVal,
                            ann, gen, valXLSField, exportedRecord);

                    map.put(map.size(), val);
                }

                return map;
            }
        }
        
        return parse(fieldType, records);
    }
    
    public static void putDynamicMap(Map<String, Object> m, String field, Object val)
    {
        if (!isEmpty(field) && !isEmpty(val.toString()))
        {
            String[] needTree = field.split("\\.");
            Map<String, Object> cNode = m;
            for (int i = 0; i < needTree.length - 1; ++i)
            {
                String needNode = needTree[i];
                cNode = subMap(cNode, needNode);
            }
            String lastNeedField = needTree[needTree.length - 1];
            cNode.put(lastNeedField, val);
        }
    }
    
    private static Map<String, Object> subMap(Map<String, Object> m, String s)
    {
        Object sub = m.get(s);
        if (sub == null)
        {
            Map<String, Object> subMap = new LinkedHashMap();
            m.put(s, subMap);
            return subMap;
        }
        else if (sub instanceof Map)
        {
            return (Map) sub;
        }
        
        throw new IllegalStateException("Cannot parse submap sub exist and not be map " + sub);
    }
    
    private static Object parsePrimitive(Class<?> clazz, String xlsField, List<XLSRecord> records)
    {
        if (primitiveParsers.containsKey(clazz))
        {
            return primitiveParsers.get(clazz).apply(records.get(0).get(xlsField));
        }
        
        return null;
    }
    
    private final static Map<Class<?>, Function<String, Object>> primitiveParsers = new HashMap();
    
    static
    {
        Function<String, Object> doubleParser = (String t) -> {
            String rep = t.replace(',', '.');
            if (rep.endsWith("%"))
                rep = rep.substring(0, rep.length() - 1);
            return Double.valueOf(rep);
        };
        
        Function<String, Object> booleanParser = (String t) -> {
            if (isEmpty(t)) return false;
            t = t.trim().toLowerCase();
            return !("0".equals(t) || "false".equals(t) || "no".equals(t));
        };
        
        primitiveParsers.put(String.class, String::valueOf);
        primitiveParsers.put(int.class, Integer::valueOf);
        primitiveParsers.put(long.class, Long::valueOf);
        primitiveParsers.put(double.class, doubleParser);
        primitiveParsers.put(Integer.class, Integer::valueOf);
        primitiveParsers.put(Long.class, Long::valueOf);
        primitiveParsers.put(Double.class, doubleParser);
        primitiveParsers.put(boolean.class, booleanParser);
        primitiveParsers.put(Boolean.class, booleanParser);
    }
    
    public static List<List<XLSRecord>> exportRecords(List<XLSRecord> records, String field)
    {
        List<List<XLSRecord>> ret = new ArrayList();
        
        List<XLSRecord> current = null;
        for (XLSRecord record : records) {
            String data = record.get(field);
            if (!isEmpty(data))
            {
                if (current != null)
                {
                    ret.add(current);
                }
                current = new ArrayList();
                current.add(record);
            }
            else
            {
                if (current != null)
                {
                    current.add(record);
                }
            }
        }
        
        if (current != null)
        {
            ret.add(current);
        }
        
        return ret;
    }
    
    public static List<XLSRecord> exportColumnRecords(List<XLSRecord> records, String field, String par)
    {
        List<XLSRecord> ret = new ArrayList();
        
        for (int i = 0; i < records.size(); ++i)
        {
            XLSRecord record = records.get(i);
            int h = 1;
            do
            {
                Map<String, Integer> header = new HashMap();
                String hField = field + h;
                String hPar = par + h;
                
                Integer hFieldIndex = record.getHeaderIndex(hField);
                Integer hParIndex = record.getHeaderIndex(hPar);
                
                if (hFieldIndex == null || hParIndex == null)
                    break;
                
                header.put(field, hFieldIndex);
                header.put(par, hParIndex);
                
                ret.add(new BaseXLSRecord(header, record.getContents()));
                ++h;
            } while (true);
        }
        
        return ret;
    }
    
    private static boolean isEmpty(String s)
    {
        return (null == s) || (s.length() == 0);
    }
    
    private static class Annotations
    {
        private final Field field;

        public Annotations(Field field) {
            this.field = field;
        }
        
        public <T extends Annotation> T get(Class<T> annType)
        {
            return field.getAnnotation(annType);
        }
    }
    
    private static class Generics
    {
        private final Type[] generics;
        private int i = 0;

        public Generics(Field field) {
            Type[] gen;
            try
            {
                ParameterizedType genTypes = (ParameterizedType) field.getGenericType();
                gen = genTypes.getActualTypeArguments();
            }
            catch(Exception ex)
            {
                gen = null;
            }
            
            this.generics = gen;
        }
        
        public Class<?> nextGeneric()
        {
            return (Class<?>) generics[i++];
        }
    }
}
