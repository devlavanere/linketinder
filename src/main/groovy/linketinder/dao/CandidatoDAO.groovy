package linketinder.dao

import linketinder.model.Candidato
import linketinder.model.Competencia

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CandidatoDAO {

    void inserir(Candidato c) {
        String sql = "INSERT INTO candidatos (nome, sobrenome, data_nascimento, email, cpf, pais, cep, descricao, senha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, c.nome)
                    stmt.setString(2, c.sobrenome)
                    stmt.setObject(3, c.dataNascimento)
                    stmt.setString(4, c.email)
                    stmt.setString(5, c.cpf)
                    stmt.setString(6, c.pais)
                    stmt.setString(7, c.cep)
                    stmt.setString(8, c.descricao)
                    stmt.setString(9, c.senha)
                    stmt.executeUpdate()
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao salvar o candidato no banco de dados.", erro)
        }
    }

    List<Candidato> listarTodos() {
        List<Candidato> lista = []
        String sql = "SELECT * FROM candidatos"
        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    ResultSet rs = stmt.executeQuery()
                    while (rs.next()) {
                        Candidato c = mapearCandidato(rs)
                        c.competencias = buscarCompetenciasDoCandidato(conn, c.id)
                        lista.add(c)
                    }
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao listar candidatos do banco de dados.", erro)
        }
        return lista
    }

    private Candidato mapearCandidato(ResultSet rs) throws SQLException {
        Candidato c = new Candidato()
        c.id = rs.getInt("id")
        c.nome = rs.getString("nome")
        c.sobrenome = rs.getString("sobrenome")
        java.sql.Date dbDate = rs.getDate("data_nascimento")
        c.dataNascimento = dbDate != null ? dbDate.toLocalDate() : null
        c.email = rs.getString("email")
        c.cpf = rs.getString("cpf")
        c.pais = rs.getString("pais")
        c.cep = rs.getString("cep")
        c.descricao = rs.getString("descricao")
        return c
    }

    private List<Competencia> buscarCompetenciasDoCandidato(Connection conn, int idCandidato) throws SQLException {
        List<Competencia> competencias = []
        String sql = """
            SELECT comp.id, comp.nome 
            FROM competencias comp
            JOIN candidato_competencia cc ON comp.id = cc.id_competencia
            WHERE cc.id_candidato = ?
        """
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idCandidato)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                competencias.add(new Competencia(
                        id: rs.getInt("id"),
                        nome: rs.getString("nome")
                ))
            }
        }
        return competencias
    }

    boolean deletar(String cpf) {
        String sql = "DELETE FROM candidatos WHERE cpf = ?"
        try {
            boolean deletado = false
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, cpf)
                    int linhasAfetadas = stmt.executeUpdate()
                    deletado = (linhasAfetadas > 0)
                }
            }
            return deletado
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao deletar o candidato no banco de dados.", erro)
        }
    }
}