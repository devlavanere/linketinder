package linketinder.dao

import linketinder.dao.interfaces.IDatabaseConnection

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class PostgresConnection implements IDatabaseConnection {
    @Override
    Connection conectar() {
        try {
            return DriverManager.getConnection(DbConfig.URL, DbConfig.USER, DbConfig.PASSWORD)
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão com o PostgreSQL: " + e.message, e)
        }
    }
}