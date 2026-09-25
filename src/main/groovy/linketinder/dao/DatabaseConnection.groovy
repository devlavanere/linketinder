package linketinder.dao

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/linketinder"

    static Connection getConnection() throws SQLException {
        // Chamando as variáveis diretamente da nossa classe DbConfig
        return DriverManager.getConnection(URL, DbConfig.USER, DbConfig.PASSWORD)
    }
}