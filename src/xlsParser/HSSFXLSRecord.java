/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.util.Map;
import jxl.Cell;

/**
 *
 * @author vinova
 */
public class HSSFXLSRecord extends XLSRecord {

    public HSSFXLSRecord(Map<String, Integer> headerMap, String[] rows) {
        super(headerMap, rows);
    }
}
