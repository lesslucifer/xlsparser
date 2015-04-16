/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.jxlimpl;

import xlsParser.XLSReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import xlsParser.BaseXLSRecord;
import xlsParser.BaseXLSSheet;
import xlsParser.BaseXLSWorkbook;
import xlsParser.XLSRecord;
import xlsParser.XLSSheet;
import xlsParser.XLSWorkbook;

/**
 *
 * @author Salm
 */
public class JXLReader implements XLSReader {
    @Override
    public XLSWorkbook read(String file)
            throws IOException, BiffException
    {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("Cp1252");
        Workbook workbook = Workbook.getWorkbook(new File(file), settings);
        Sheet[] sheets = workbook.getSheets();
        String[] sheetNames = workbook.getSheetNames();
        
        List<XLSSheet> xlsSheets = new ArrayList(sheets.length); 
        for (int i = 0; i < sheets.length; ++i)
        {
            xlsSheets.add(this.createSheet(sheetNames[i], sheets[i]));
        }
        
        return new BaseXLSWorkbook(xlsSheets);
    }
    
    
    
    private XLSSheet createSheet(String name, jxl.Sheet sheet)
    {
        int nRows = sheet.getRows();
        if (nRows < 1)
        {
            throw new IllegalStateException(
                    "Sheet row must be greater than 0"
                + " (for header) [" + sheet.getName() + "]");
        }
        
        Cell[] header = sheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap(header.length);
        for (int i = 0; i < header.length; ++i)
        {
            headerMap.put(header[i].getContents(), i);
        }
        
        List<XLSRecord> rows = new ArrayList(nRows - 1);
        for (int i = 1; i < nRows; ++i)
        {
            Cell[] row = sheet.getRow(i);
            rows.add(this.createRecord(headerMap, row));
        }
        
        return new BaseXLSSheet(name, headerMap, rows);
    }
    
    private XLSRecord createRecord(Map<String, Integer> headerMap, jxl.Cell[] row)
    {
        String[] contents = new String[headerMap.size()];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = (row.length > i)?row[i].getContents():"";
        }
        
        return new BaseXLSRecord(headerMap, contents);
    }
    
    public static final JXLReader READER = new JXLReader();
}