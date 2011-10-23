package ch.simas.jsfsqleditor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            PreparedStatement ps = con.prepareStatement(this.sql);
            ResultSet rs = ps.executeQuery();
            this.result = rs.toString();
        } catch (SQLException ex) {
            Logger.getLogger(SqlExecutor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SqlExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
