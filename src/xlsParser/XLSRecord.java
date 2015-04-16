/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser;

import java.util.List;

/**
 *
 * @author vinova
 */
public interface XLSRecord {

    String get(String field);
    String get(int index);
    int getHeaderIndex(String header);
    String[] getContents();
    int size();
}
