package linketinder.dao

import linketinder.dao.interfaces.ICrudDAO
import linketinder.model.Vaga
import linketinder.model.Competencia

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaDAO implements ICrudDAO<Vaga, Integer> {

    void inserir(Vaga v) {
        String sql = "INSERT INTO vagas (nome, descricao, local, id_empresa) VALUES (?, ?, ?, ?)"
        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, v.nome)
                    stmt.setString(2, v.descricao)
                    stmt.setString(3, v.local)
                    stmt.setInt(4, v.idEmpresa)
                    stmt.executeUpdate()
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao salvar a vaga no banco de dados.", erro)
        }
    }

    List<Vaga> listar() {
        List<Vaga> lista = []
        String sql = "SELECT * FROM vagas"
        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    ResultSet rs = stmt.executeQuery()
                    while (rs.next()) {
                        // Método mapeador (DRY)
                        Vaga vaga = mapearVaga(rs)
                        // A relação N:N pertence à Vaga
                        vaga.competencias = buscarCompetenciasDaVaga(conn, vaga.id)
                        lista.add(vaga)
                    }
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao listar vagas do banco de dados.", erro)
        }
        return lista
    }

    private Vaga mapearVaga(ResultSet rs) throws SQLException {
        Vaga v = new Vaga()
        v.id = rs.getInt("id")
        v.nome = rs.getString("nome")
        v.descricao = rs.getString("descricao")
        v.local = rs.getString("local")
        v.idEmpresa = rs.getInt("id_empresa")
        return v
    }

    private List<Competencia> buscarCompetenciasDaVaga(Connection conn, int idVaga) throws SQLException {
        List<Competencia> competencias = []
        String sql = """
            SELECT c.id, c.nome 
            FROM competencias c
            JOIN vaga_competencia vc ON c.id = vc.id_competencia
            WHERE vc.id_vaga = ?
        """
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idVaga)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                competencias.add(new Competencia(id: rs.getInt("id"), nome: rs.getString("nome")))
            }
        }
        return competencias
    }

    boolean deletar(Integer idVaga) {
        String sql = "DELETE FROM vagas WHERE id = ?"
        try {
            boolean deletado = false
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, idVaga)
                    int linhasAfetadas = stmt.executeUpdate()
                    deletado = (linhasAfetadas > 0)
                }
            }
            return deletado
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao deletar a vaga no banco de dados.", erro)
        }
    }
}