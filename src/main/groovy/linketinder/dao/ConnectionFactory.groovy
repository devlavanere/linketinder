package linketinder.dao

import java.sql.Connection

@Singleton
class ConnectionFactory {

    Connection criarConexao(String tipoBanco = "POSTGRES") {
        if (tipoBanco.equalsIgnoreCase("POSTGRES")) {
            return new PostgresConnection().conectar()
        }

        throw new IllegalArgumentException("Banco de dados não suportado: " + tipoBanco)
    }
}