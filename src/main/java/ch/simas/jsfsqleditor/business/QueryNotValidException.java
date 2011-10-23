package ch.simas.jsfsqleditor.business;

/**
 *
 * @author Simon Martinelli
 */
public class QueryNotValidException extends Exception {

    QueryNotValidException(String string) {
        super(string);
    }
}
