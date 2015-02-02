package com.metasys.ryft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.metasys.ryft.program.RyftPrimitives.Statistics;

/**
 * Bean holding statistics returned from the execution of the Ryft primitives.
 *
 * @author Sylvain Crozon
 *
 */
public class Result implements Serializable {

    private List<Stat> statistics = new ArrayList<>();
    private String outputFile;
    private String indexFile;

    public void copy(Result copy) {
        statistics = copy.getStatistics();
        outputFile = copy.getOutputFile();
        indexFile = copy.getIndexFile();
    }

    public List<Stat> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Stat> statistics) {
        this.statistics = statistics;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getIndexFile() {
        return indexFile;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Result [statistics=");
        builder.append(statistics);
        builder.append(", outputFile=");
        builder.append(outputFile);
        builder.append(", indexFile=");
        builder.append(indexFile);
        builder.append("]");
        return builder.toString();
    }

    public static class Stat implements Serializable {
        Statistics name;
        Object value;

        public Stat(Statistics name, Object value) {
            this.name = name;
            this.value = value;
        }

        public Statistics getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class Line implements Serializable {
        int number;
        String content;

        public Line(int number, String content) {
            super();
            this.number = number;
            this.content = content;
        }

        public int getNumber() {
            return number;
        }

        public String getContent() {
            return content;
        }
    }

}
