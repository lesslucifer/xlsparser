/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.template;

import java.util.List;
import xlsParser.Util;
import xlsParser.*;

/**
 *
 * @author Salm
 */
class XLSIterator {
    private final XLSWorkbook wb;
    private final String sheet;
    private final List<XLSRecord> records;
    private final XLSParseContext context;

    public XLSIterator(XLSWorkbook wb, String sheet,
            XLSParseContext context) {
        this.wb = wb;
        this.sheet = (!Util.isEmpty(sheet))?sheet:(wb.getSheet(0).getName());
        this.records = getSheet().getRecords();
        this.context = context;
    }
    
    public XLSIterator(XLSIterator ite, XLSParseContext context)
    {
        this(ite, null, context);
    }
    
    public XLSIterator(XLSIterator ite, String sheet,
            XLSParseContext context)
    {
        this(ite, sheet, ite.records, context);
    }
    
    public XLSIterator(XLSIterator ite,
            String sheet, List<XLSRecord> recs, XLSParseContext context)
    {
        this.wb = ite.wb;
        if (Util.isEmpty(sheet) || ite.sheet.equals(sheet))
        {
            this.records = recs;
            this.sheet = ite.sheet;
        }
        else
        {
            this.sheet = sheet;
            this.records = getSheet().getRecords();
        }
        
        this.context = context;
    }

    public XLSWorkbook getWb() {
        return wb;
    }
    
    public final XLSSheet getSheet()
    {
        return wb.getSheet(sheet);
    }

    public List<XLSRecord> getRecords() {
        return records;
    }
    
    public XLSIterator newIterator(XLSParseContext context)
    {
        return new XLSIterator(this, context);
    }
    
    public XLSIterator newIterator(String sheet, XLSParseContext context)
    {
        return new XLSIterator(this, sheet, context);
    }
    
    public XLSIterator newIterator(String sheet, List<XLSRecord> records, XLSParseContext context)
    {
        return new XLSIterator(this, sheet, records, context);
    }
    
    public String getFirst(String field)
    {
        return this.records.get(0).get(field);
    }

    public XLSParseContext getContext() {
        return context;
    }
}