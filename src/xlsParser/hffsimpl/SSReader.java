/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.hffsimpl;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xlsParser.*;

/**
 *
 * @author vinova
 */
public class SSReader implements XLSReader {
@Override
    public XLSWorkbook read(String file) throws Exception
    {
        Workbook workbook = null;
        if (file.endsWith(".xls"))
        {
            workbook = new HSSFWorkbook(new FileInputStream(file));
        }
        else if (file.endsWith(".xlsx"))
        {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        }
        
        if (workbook == null)
            return null;
        
        Sheet[] sheets = new Sheet[workbook.getNumberOfSheets()];
        String[] sheetNames = new String[workbook.getNumberOfSheets()];
        
        for (int i = 0; i < sheets.length; ++i)
        {
            sheets[i] = workbook.getSheetAt(i);
            sheetNames[i] = workbook.getSheetName(i);
        }
        
        List<XLSSheet> xlsSheets = new ArrayList(sheets.length); 
        for (int i = 0; i < sheets.length; ++i)
        {
            xlsSheets.add(this.createSheet(sheetNames[i], sheets[i]));
        }
        
        return new BaseXLSWorkbook(xlsSheets);
    }
    
    
    
    private XLSSheet createSheet(String name, Sheet sheet)
    {
        List<Row> rows = new ArrayList(sheet.getPhysicalNumberOfRows());
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); ++i)
        {
            rows.add(sheet.getRow(i));
        }
        
        if (rows.size() < 1)
        {
            throw new IllegalStateException(
                    "Sheet row must be greater than 0"
                + " (for header) [" + name + "]");
        }
        
        String[] header = parseRow(sheet.getRow(0));
        Map<String, Integer> headerMap = new HashMap(header.length);
        for (int i = 0; i < header.length; ++i)
        {
            headerMap.put(header[i], i);
        }
        
        List<XLSRecord> contents = new ArrayList(rows.size() - 1);
        for (int i = 1; i < rows.size(); ++i)
        {
            contents.add(this.createRecord(headerMap, rows.get(i)));
        }
        
        return new BaseXLSSheet(name, headerMap, contents);
    }
    
    private String cellToString(Cell c)
    {
        if (c== null)
            return "";
        
        switch (c.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                return Double.toString(c.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return c.getStringCellValue();
            case Cell.CELL_TYPE_FORMULA:
                switch(c.getCachedFormulaResultType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        return Double.toString(c.getNumericCellValue());
                    case Cell.CELL_TYPE_STRING:
                        return c.getRichStringCellValue().getString();
                }
                break;
        }

        return "";
    }
    
    private String[] parseRow(Row row)
    {
        String[] data = new String[row.getPhysicalNumberOfCells()];
        for (int i = 0; i < data.length; ++i)
        {
            Cell c = row.getCell(i);
            data[i] = cellToString(c);
            
        }
        
        return data;
    }
    
    private XLSRecord createRecord(Map<String, Integer> headerMap, Row row)
    {
        return new BaseXLSRecord(headerMap, parseRow(row));
    }
    
    public static final SSReader READER = new SSReader();
}
