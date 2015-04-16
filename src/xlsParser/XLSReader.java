/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.io.File;
import java.io.IOException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 *
 * @author Salm
 */
public class XLSReader {
    public static XLSWorkbook read(String file)
            throws IOException, BiffException
    {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("Cp1252");
        Workbook workbook = Workbook.getWorkbook(new File(file), settings);
        Sheet[] sheets = workbook.getSheets();
        String[] sheetNames = workbook.getSheetNames();
        
        return new XLSWorkbook(sheetNames, sheets);
    }
}