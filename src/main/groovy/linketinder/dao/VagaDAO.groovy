package linketinder.dao

import linketinder.model.Vaga
import linketinder.model.Competencia
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class VagaDAO {

    // 1. CREATE - Inserir Vaga
    void inserir(Vaga v) {
        String sql = "INSERT INTO vagas (nome, descricao, local, id_empresa) VALUES (?, ?, ?, ?)"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                stmt.setString(1, v.nome)
                stmt.setString(2, v.descricao)
                stmt.setString(3, v.local)
                stmt.setInt(4, v.idEmpresa)

                stmt.executeUpdate()
                println "Vaga salva no PostgreSQL com sucesso!"
            }
        }
    }

    // 2. READ - Listar Vagas (com JOIN para puxar as competências exigidas)
    List<Vaga> listarTodas() {
        List<Vaga> lista = []
        String sql = "SELECT * FROM vagas"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                ResultSet rs = stmt.executeQuery()
                while (rs.next()) {
                    Vaga v = new Vaga()
                    v.id = rs.getInt("id")
                    v.nome = rs.getString("nome")
                    v.descricao = rs.getString("descricao")
                    v.local = rs.getString("local")
                    v.idEmpresa = rs.getInt("id_empresa")

                    // Busca as competências exigidas por esta vaga específica
                    v.competencias = buscarCompetenciasDaVaga(conn, v.id)
                    lista.add(v)
                }
            }
        }
        return lista
    }

    private List<Competencia> buscarCompetenciasDaVaga(Connection conn, int idVaga) {
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

    void deletar(int idVaga) {
        String sql = "DELETE FROM vagas WHERE id = ?"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                stmt.setInt(1, idVaga)
                int linhasAfetadas = stmt.executeUpdate()

                if (linhasAfetadas > 0) {
                    println "Vaga deletada com sucesso!"
                } else {
                    println "Nenhuma vaga encontrada com o ID informado."
                }
            }
        }
    }
}