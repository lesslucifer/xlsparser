/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

/**
 *
 * @author vinova
 */
public interface XLSReader {

    XLSWorkbook read(String file) throws Exception;
    
}
