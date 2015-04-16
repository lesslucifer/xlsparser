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
public abstract class AbstractXLSConfigurator implements XLSConfigurator {
    private String inPath = "", outPath = "";

    @Override
    public String getInPath() {
        return inPath;
    }

    @Override
    public void setInPath(String inPath) {
        this.inPath = inPath;
    }

    @Override
    public String getOutPath() {
        return outPath;
    }

    @Override
    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }
}
