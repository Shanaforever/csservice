/**
* @author GHUANG
* @version 2018年11月30日 上午10:05:31
*
*/
package com.centricsoftware.commons.utils;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.RowSet;

import com.centricsoftware.config.entity.CsProperties;
import com.sun.rowset.CachedRowSetImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DBUtil implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(DBUtil.class);

    public static String DATABASE_SERVER = "";

    public static String DATABASE_NAME = "";

    public static String VENDOR = "POSTGRES";

    public static String USERNAME = "";

    public static String PASSWD = "";

    private static final String URL_TERMINATOR = ";";

    private static final String DB_NAME_PROP = "databaseName";

    private static final String EQ = "=";

    private static final String EMPTY = "";

    private Connection connection;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new DBUtil().getConnection();
    }
    public static CsProperties CSProperties;
    static {
        CSProperties = NodeUtil.getProperties();
        USERNAME = CSProperties.getValue("cs.pq.dbuser", "POSTGRES");
        PASSWD = CSProperties.getValue("cs.pq.dbpwd", "postgres");
        VENDOR = CSProperties.getValue("cs.pq.dbtype", "POSTGRES");
        DATABASE_SERVER = CSProperties.getValue("cs.pq.dbhost", "localhost");
        DATABASE_NAME = CSProperties.getValue("cs.pq.dbname", "POSTGRES");
    }

    public static String getDatabaseConnectionString() {
        String subProtocol = null;
        String dbServer = DATABASE_SERVER;
        String dbName = DATABASE_NAME;
        if (null == dbServer || null == dbName) {
            throw new RuntimeException(
                    "Both " + DATABASE_SERVER + " and " + DATABASE_NAME + " has to be configured.");
        }
        String connectionString = "";
        switch (VENDOR) {
        case "POSTGRES":
            subProtocol = "jdbc:postgresql://";
            connectionString = subProtocol + dbServer.trim() + "/" + dbName.trim();
            break;
        case "ORACLE":
            subProtocol = "jdbc:oracle:thin:@";
            connectionString = subProtocol + dbServer.trim() + ":" + dbName.trim();
            break;
        case "MSSQL":
            subProtocol = "jdbc:sqlserver://";
            connectionString = subProtocol + dbServer.trim() + URL_TERMINATOR + DB_NAME_PROP + EQ + dbName.trim();
            break;
        }
        return connectionString;
    }

    void executeDdlStatement(String ddlStatement) {
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(ddlStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        if (null == connection) {
            System.out.println("new connection");
            String dbUrl = getDatabaseConnectionString();
            System.out.println("dburl=" + dbUrl);
            String userName = USERNAME;
            String password = PASSWD;
            try {
                connection = DriverManager.getConnection(dbUrl, userName, password);
            } catch (Exception e) {
                e.printStackTrace();
                connection = null;
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("use existing connection");
        }
        return connection;
    }

    public void executeScript(String sqlStatement) {
        try (Statement stmt = getConnection().createStatement()) {
            try {
                stmt.execute(sqlStatement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeScript(String sqlStatement, Statement stmt) {
        try {
            stmt.execute(sqlStatement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeQuery(String sql) {
        ResultSet rs = null;
        log.info("sql={}",sql);
        try {
            PreparedStatement statement = prepareStatement(sql);
            rs = statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    public RowSet query(String sql) {
        CachedRowSetImpl rowset = null;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            rowset = new CachedRowSetImpl();
            statement = prepareStatement(sql);
            rs = statement.executeQuery();
            rowset.populate(rs);
        } catch (Exception e) {
            log.error("DBLogError", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                log.error("DBLogError", e);
            }
        }
        return rowset;
    }

    @Override
    public void close() {
        if (null == connection) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("DBLogError", e);
        } finally {
            connection = null;
        }
    }

    PreparedStatement prepareStatement(String sql) {
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ps;
    }

    /**
     * Returns single integer obtained by sql. For example select count(*) from tableFoo;
     *
     * @param countingSql
     * @return
     */
    public int getCount(String countingSql) {
        try (PreparedStatement ps = prepareStatement(countingSql); ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new RuntimeException("Bad counting query [" + countingSql + "].");
            }
            int count = rs.getInt(1);
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
