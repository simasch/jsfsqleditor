package ch.simas.jsfsqleditor.web;

import ch.simas.jsfsqleditor.business.QueryNotValidException;
import ch.simas.jsfsqleditor.business.Result;
import ch.simas.jsfsqleditor.business.SqlExecutor;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.el.ValueExpression;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Simon Martinelli
 */
@Named
@RequestScoped
public class EditorBean implements Serializable {

    @EJB
    private SqlExecutor sqlExecutor;
    private String sql;
    private List<List<String>> resultset = new ArrayList<List<String>>();
    private List<String> header = new ArrayList<String>();
    private String table;

    public void executeSql() {
        this.table = "";
        try {
            Result result = this.sqlExecutor.executeSql(this.sql);
            if (result.getResultset() == null) {
                FacesContext.getCurrentInstance().addMessage(
                        "", new FacesMessage(result.getNumberOfUpdates() + " row(s) updated."));
            } else {
                this.resultset = result.getResultset();
                this.header = result.getHeader();
                this.populateDataTable();
            }
        } catch (QueryNotValidException qnve) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(qnve.getMessage()));
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(ex.getMessage()));
        }
    }

    private void populateDataTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table><thead><tr>");

        for (String headerValue : this.header) {
            sb.append("<th>");
            sb.append(headerValue);
            sb.append("</th>");
        }
        sb.append("</tr></thead>");
        sb.append("<tbody>");

        for (List<String> row : this.resultset) {
            sb.append("<tr>");
            for (String value : row) {
                sb.append("<td>");
                sb.append(value);
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</tbody></table");
        this.table = sb.toString();
    }

    private ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getExpressionFactory().createValueExpression(
                facesContext.getELContext(), valueExpression, valueType);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
