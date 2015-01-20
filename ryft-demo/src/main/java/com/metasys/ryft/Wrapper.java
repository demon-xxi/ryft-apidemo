package com.metasys.ryft;

public class Wrapper {

    static {
        System.loadLibrary("libryft-mock");
        System.loadLibrary("wrapper");
    }

    public native void init();

    public native int openFile(String[] files);

    public native int writeIndex(String file);

    public native int writeData(String file);

    public native int search(String searchString, int width);

    public native int fuzzySearch(String searchString, int width, int fuzziness);

    public native int sortAsc(String field);

    public native int sortDesc(String field);

    public native int termFrequency(String format);

    public native int execute(int nodes);

    public static void main(String[] args) {
        Wrapper w = new Wrapper();
        w.init();
        w.openFile(new String[] { "abc", "def" });
        w.search("search", 2);
        w.writeData("data");
        w.execute(2);
        w.openFile(new String[] { "ghi", "jkl" });
        w.search("search2", 3);
        w.writeData("data2");
        w.execute(4);
        // fuzzySearch("fuzzy", 3, 2);
        // sortAsc("asc");
        // sortDesc("desc");
        // termFrequency("field");
        // writeIndex("index");
    }
}
