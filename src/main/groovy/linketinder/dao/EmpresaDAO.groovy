package linketinder.dao

import linketinder.model.Empresa

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class EmpresaDAO {

    void inserir(Empresa e) {
        String sql = "INSERT INTO empresas (nome, cnpj, email, senha, pais, cep, descricao) VALUES (?, ?, ?, ?, ?, ?, ?)"
        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, e.nome)
                    stmt.setString(2, e.cnpj)
                    stmt.setString(3, e.email)
                    stmt.setString(4, e.senha)
                    stmt.setString(5, e.pais)
                    stmt.setString(6, e.cep)
                    stmt.setString(7, e.descricao)
                    stmt.executeUpdate()
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao salvar a empresa no banco de dados.", erro)
        }
    }

    List<Empresa> listarTodas() {
        List<Empresa> lista = []
        String sql = "SELECT * FROM empresas"
        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    ResultSet rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(mapearEmpresa(rs))
                    }
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao listar empresas do banco de dados.", erro)
        }
        return lista
    }

    private Empresa mapearEmpresa(ResultSet rs) throws SQLException {
        Empresa e = new Empresa()
        e.id = rs.getInt("id")
        e.nome = rs.getString("nome")
        e.cnpj = rs.getString("cnpj")
        e.email = rs.getString("email")
        e.pais = rs.getString("pais")
        e.cep = rs.getString("cep")
        e.descricao = rs.getString("descricao")
        return e
    }

    boolean deletar(String cnpj) {
        String sql = "DELETE FROM empresas WHERE cnpj = ?"
        try {
            boolean deletado = false
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, cnpj)
                    int linhasAfetadas = stmt.executeUpdate()
                    deletado = (linhasAfetadas > 0)
                }
            }
            return deletado
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao deletar a empresa no banco de dados.", erro)
        }
    }
}