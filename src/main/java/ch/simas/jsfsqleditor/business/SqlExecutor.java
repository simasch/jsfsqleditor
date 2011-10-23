package ch.simas.jsfsqleditor.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

/**
 *
 * @author Simon Martinelli
 */
@LocalBean
@Stateless
public class SqlExecutor {

    @Resource(name = "java:jboss/datasources/MysqlDS")
    private DataSource dataSource;

    public Result executeSql(String sql) throws QueryNotValidException, SQLException {
        Connection con = null;
        try {
            con = this.dataSource.getConnection();

            if (sql == null) {
                throw new QueryNotValidException("Query is empty");
            } else if (sql.toUpperCase().startsWith("SELECT")) {
                return this.select(con, sql);
            } else if (sql.toUpperCase().startsWith("INSERT")
                    || sql.toUpperCase().startsWith("UPDATE")
                    || sql.toUpperCase().startsWith("DELETE")
                    || sql.toUpperCase().startsWith("CREATE")
                    || sql.toUpperCase().startsWith("DROP")) {
                return this.update(con, sql);
            } else {
                throw new QueryNotValidException("Query not valid");
            }
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }
    }

    private Result select(Connection con, String sql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return this.results2Array(rs);
    }

    private Result update(Connection con, String sql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        int number = ps.executeUpdate();
        Result result = new Result();
        result.setNumberOfUpdates(number);
        return result;
    }

    private Result results2Array(ResultSet rs)
            throws SQLException {
        Result result = new Result();

        ResultSetMetaData metaData = rs.getMetaData();
        int columns = metaData.getColumnCount();

        List<String> header = new ArrayList<String>();
        for (int i = 1; i <= columns; i++) {
            header.add(metaData.getColumnName(i));
        }
        result.setHeader(header);

        List<List<String>> resultset = new ArrayList<List<String>>();

        while (rs.next()) {
            List<String> record = new ArrayList<String>();

            for (int i = 1; i <= columns; i++) {
                record.add(rs.getString(i));
            }
            resultset.add(record);
        }
        result.setResultset(resultset);
        return result;
    }
}
