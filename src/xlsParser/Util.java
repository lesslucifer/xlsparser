/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

/**
 *
 * @author Salm
 */
public class Util {
    private Util() {}
    
    public static boolean isEmpty(String s)
    {
        return s == null || s.length() <= 0 || "".equals(s);
    }
    
    public static int toInt(Object o)
    {
        return toInt(o, 0);
    }
    
    public static int toInt(Object o, int def)
    {
        try
        {
            if (o instanceof Number)
                return ((Number) o).intValue();
            
            return Integer.parseInt(String.valueOf(o));
        }
        catch (Exception ex)
        {
            return def;
        }
    }
    
    public static long toLong(Object o)
    {
        return toLong(o, 0L);
    }
    
    public static long toLong(Object o, long def)
    {
        try
        {
            if (o instanceof Number)
                return ((Number) o).longValue();
            
            return Long.parseLong(String.valueOf(o));
        }
        catch (Exception ex)
        {
            return def;
        }
    }
    
    public static boolean toBoolean(Object o)
    {
        return toBoolean(o, false);
    }
    
    public static boolean toBoolean(Object o, boolean def)
    {
        try
        {
            if (o instanceof Boolean)
                return (Boolean) o;
            
            if (o instanceof Number)
                return ((Number) o).doubleValue() != 0.0;
            
            String s = String.valueOf(o);
            s = s.toLowerCase().trim();
            if (isEmpty(String.valueOf(o)))
                return false;
            
            if ("false".equals(String.valueOf(o)) || "no".equals(String.valueOf(o)) || "0".equals(String.valueOf(o)))
                return false;
            
            if ("true".equals(String.valueOf(o)) || "yes".equals(String.valueOf(o)) || toInt(String.valueOf(o)) != 0)
                return true;
            
            throw new Exception("Invalid boolean string: " + s);
        }
        catch (Exception ex)
        {
            return def;
        }
    }
    
    public static float toFloat(Object o)
    {
        return toFloat(o, 0.f);
    }
    
    public static float toFloat(Object o, float def)
    {
        try
        {
            if (o instanceof Number)
                return ((Number) o).floatValue();
            
            return Float.parseFloat(String.valueOf(o));
        }
        catch (Exception ex)
        {
            return def;
        }
    }
    
    public static double toDouble(Object o)
    {
        return toDouble(o, 0.0);
    }
    
    public static double toDouble(Object o, double def)
    {
        try
        {
            if (o instanceof Number)
                return ((Number) o).doubleValue();
            
            return Double.parseDouble(String.valueOf(o));
        }
        catch (Exception ex)
        {
            return def;
        }
    }
    
    public static Object byPriority(Object... oo)
    {
        for (Object o : oo) {
            if (o != null)
            {
                if (!(o instanceof String) || !isEmpty((String) o))
                {
                    return o;
                }
            }
        }
        
        return null;
    }
}
