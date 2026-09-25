package linketinder.dao

import linketinder.model.Candidato
import linketinder.model.Competencia

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CandidatoDAO {

    void inserir(Candidato c) {
        String sql = "INSERT INTO candidatos (nome, sobrenome, data_nascimento, email, cpf, pais, cep, descricao, senha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                stmt.setString(1, c.nome)
                stmt.setString(2, c.sobrenome)
                stmt.setObject(3, c.dataNascimento) // LocalDate nativo no JDBC
                stmt.setString(4, c.email)
                stmt.setString(5, c.cpf)
                stmt.setString(6, c.pais)
                stmt.setString(7, c.cep)
                stmt.setString(8, c.descricao)
                stmt.setString(9, c.senha)

                stmt.executeUpdate()
                println "Candidato salvo no PostgreSQL com sucesso!"
            }
        }
    }

    List<Candidato> listarTodos() {
        List<Candidato> lista = []
        String sql = "SELECT * FROM candidatos"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                ResultSet rs = stmt.executeQuery()

                while (rs.next()) {
                    Candidato c = new Candidato()
                    c.id = rs.getInt("id")
                    c.nome = rs.getString("nome")
                    c.sobrenome = rs.getString("sobrenome")

                    // Tratamento para conversão de data do SQL para LocalDate
                    java.sql.Date dbDate = rs.getDate("data_nascimento")
                    c.dataNascimento = dbDate != null ? dbDate.toLocalDate() : null

                    c.email = rs.getString("email")
                    c.cpf = rs.getString("cpf")
                    c.pais = rs.getString("pais")
                    c.cep = rs.getString("cep")
                    c.descricao = rs.getString("descricao")

                    // N:N - Buscando as competências deste candidato
                    c.competencias = buscarCompetenciasDoCandidato(conn, c.id)

                    lista.add(c)
                }
            }
        }
        return lista
    }

    // Método auxiliar privado para resolver o relacionamento N:N
    private List<Competencia> buscarCompetenciasDoCandidato(Connection conn, int idCandidato) {
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
                Competencia comp = new Competencia(
                        id: rs.getInt("id"),
                        nome: rs.getString("nome")
                )
                competencias.add(comp)
            }
        }
        return competencias
    }

    // Adicione este método dentro do CandidatoDAO
    void deletar(String cpf) {
        String sql = "DELETE FROM candidatos WHERE cpf = ?"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                stmt.setString(1, cpf)
                int linhasAfetadas = stmt.executeUpdate()

                if (linhasAfetadas > 0) {
                    println "Candidato deletado com sucesso!"
                } else {
                    println "Nenhum candidato encontrado com o CPF informado."
                }
            }
        }
    }
}