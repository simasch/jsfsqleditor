package ch.simas.jsfsqleditor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 *
 * @author Simon Martinelli
 */
@Named
@Stateless
public class SqlExecutor {

    @Resource(name = "java:jboss/datasources/MysqlDS")
    private DataSource dataSource;
    private String sql;
    private String result;

    public void executeSql() {
        Connection con = null;
        try {
            con = this.dataSource.getConnection();

            if (this.sql.toUpperCase().startsWith("SELECT")) {
                this.select(con);
            } else if (this.sql.toUpperCase().startsWith("INSERT")
                    || this.sql.toUpperCase().startsWith("UPDATE")
                    || this.sql.toUpperCase().startsWith("DELETE")) {
                this.update(con);
            } else {
                this.result = "Query not valid: " + this.sql;
            }
        } catch (SQLException ex) {
            this.result = ex.toString();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                this.result = ex.toString();
            }
        }
    }

    private void select(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(this.sql);
        ResultSet rs = ps.executeQuery();
        this.result = this.results2Array(rs).toString();
    }

    private void update(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(this.sql);
        this.result = new Integer(ps.executeUpdate()).toString();
    }

    private List<List<String>> results2Array(ResultSet rs)
            throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columns = metaData.getColumnCount();

        List<List<String>> list = new ArrayList<List<String>>();

        while (rs.next()) {
            List<String> record = new ArrayList<String>();

            for (int i = 1; i <= columns; i++) {
                record.add(rs.getString(i));
            }
            list.add(record);
        }
        return list;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
