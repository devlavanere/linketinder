package linketinder.dao

import linketinder.dao.interfaces.IDatabaseConnection
import java.sql.Connection
import java.sql.SQLException
import org.postgresql.Driver

class PostgresConnection implements IDatabaseConnection {
    @Override
    Connection conectar() {
        try {
            Driver driver = new Driver()

            Properties props = new Properties()
            props.setProperty("user", DbConfig.USER)
            props.setProperty("password", DbConfig.PASSWORD)

            Connection conn = driver.connect(DbConfig.URL, props)

            if (conn == null) {
                throw new SQLException("O driver do Postgres recusou a URL: " + DbConfig.URL)
            }

            return conn

        } catch (Exception e) {
            throw new RuntimeException("Erro de conexão com o PostgreSQL: " + e.message, e)
        }
    }
}