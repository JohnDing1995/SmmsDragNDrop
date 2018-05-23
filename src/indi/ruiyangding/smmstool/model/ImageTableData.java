package indi.ruiyangding.smmstool.model;

import javafx.beans.property.SimpleStringProperty;

public class ImageTableData {
    private final SimpleStringProperty fileName;
    private final SimpleStringProperty url;
    private final SimpleStringProperty success;
    private final SimpleStringProperty path;
    private final int index;

    public ImageTableData(String fName, String u, String s, String p, int index){
        this.fileName = new SimpleStringProperty(fName);
        this.url = new SimpleStringProperty(u);
        this.success = new SimpleStringProperty(s);
        this.path = new SimpleStringProperty(p);
        this.index = index;
    }

    public String getFileName() {
        return fileName.get();
    }


    public String getUrl() {
        return url.get();
    }


    public String getSuccess() {
        return success.get();
    }

    public void setFileName(String fName)
    {
        fileName.set(fName);
    }

    public void setUrl(String u)
    {
        url.set(u);
    }

    public void setSuccess(String s)
    {
        success.set(s);
    }


    public String getPath() {
        return path.get();
    }

    public void setPath(String p) {
        path.set(p);
    }

    public int getIndex() {
        return index;
    }
}
