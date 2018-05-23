package indi.ruiyangding.smmstool;

public class ImageData {
    public String filename;
    public String storename;
    public int size;
    public int width;
    public int height;
    public String hash;
    public String delete;
    public String url;
    public String msg;
    public String path;
    public String markdown;
    public String html;

    public void generateImageCode() {
        this.markdown = "![" + filename + "](" + url + ")";
        this.html = "<img src=\"" + url + "\" alt=\"" + filename + "\" title=\"" + filename + "\" />";
    }
}
