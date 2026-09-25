package linketinder.dao

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/linketinder"
    private static final String USER = "postgres"
    private static final String PASSWORD = "lavanere"

    static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD)
        } catch(SQLException e) {
            println "Erro ao conectar com o banco de dados: ${e.message}"
            throw e
        }
    }
}
