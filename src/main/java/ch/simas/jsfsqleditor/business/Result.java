package ch.simas.jsfsqleditor.business;

import java.util.List;

/**
 *
 * @author Simon Martinelli
 */
public class Result {

    private List<String> header;
    private List<List<String>> resultset;
    private int numberOfUpdates;

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public int getNumberOfUpdates() {
        return numberOfUpdates;
    }

    public void setNumberOfUpdates(int numberOfUpdates) {
        this.numberOfUpdates = numberOfUpdates;
    }

    public List<List<String>> getResultset() {
        return resultset;
    }

    public void setResultset(List<List<String>> resultset) {
        this.resultset = resultset;
    }
}
