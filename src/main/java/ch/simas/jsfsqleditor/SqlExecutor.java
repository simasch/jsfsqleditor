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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
    private int number;
    private List<List<String>> resultset;

    public void executeSql() {
        Connection con = null;
        try {
            con = this.dataSource.getConnection();

            if (this.sql.toUpperCase().startsWith("SELECT")) {
                this.select(con);
            } else if (this.sql.toUpperCase().startsWith("INSERT")
                    || this.sql.toUpperCase().startsWith("UPDATE")
                    || this.sql.toUpperCase().startsWith("DELETE")
                    || this.sql.toUpperCase().startsWith("CREATE")
                    || this.sql.toUpperCase().startsWith("DROP")) {
                this.update(con);
            } else {
                String error = "Query not valid: " + this.sql;
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(error));
            }
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(ex.toString()));
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(ex.toString()));
            }
        }
    }

    private void select(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(this.sql);
        ResultSet rs = ps.executeQuery();
        this.resultset = this.results2Array(rs);
    }

    private void update(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(this.sql);
        this.number = ps.executeUpdate();
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<List<String>> getResultset() {
        return resultset;
    }

    public void setResultset(List<List<String>> resultset) {
        this.resultset = resultset;
    }
}
