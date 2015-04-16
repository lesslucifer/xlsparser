/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsconfig;

/**
 *
 * @author Salm
 */
public interface XLSConfigurator {
    String getInPath();
    void setInPath(String path);
    String getOutPath();
    void setOutPath(String path);
    void runConfig() throws Exception;
}
