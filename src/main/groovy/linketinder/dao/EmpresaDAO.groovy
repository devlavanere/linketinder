package linketinder.dao

import linketinder.model.Empresa
import linketinder.model.Competencia
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDAO {

    void inserir(Empresa e) {
        String sql = "INSERT INTO empresas (nome, cnpj, email, senha, pais, cep, descricao) VALUES (?, ?, ?, ?, ?, ?, ?)"

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
                println "Empresa salva no PostgreSQL com sucesso!"
            }
        }
    }

    List<Empresa> listarTodas() {
        List<Empresa> lista = []
        String sql = "SELECT * FROM empresas"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                ResultSet rs = stmt.executeQuery()
                while (rs.next()) {
                    Empresa e = new Empresa()
                    e.id = rs.getInt("id")
                    e.nome = rs.getString("nome")
                    e.cnpj = rs.getString("cnpj")
                    e.email = rs.getString("email")
                    e.pais = rs.getString("pais")
                    e.cep = rs.getString("cep")
                    e.descricao = rs.getString("descricao")

                    // Busca as competências baseadas nas VAGAS dessa empresa!
                    e.competencias = buscarCompetenciasDaEmpresa(conn, e.id)
                    lista.add(e)
                }
            }
        }
        return lista
    }

    private List<Competencia> buscarCompetenciasDaEmpresa(Connection conn, int idEmpresa) {
        List<Competencia> competencias = []
        // Query Avançada: Traz competências distintas através da relação Empresa -> Vaga -> Competência
        String sql = """
            SELECT DISTINCT c.id, c.nome 
            FROM competencias c
            JOIN vaga_competencia vc ON c.id = vc.id_competencia
            JOIN vagas v ON v.id = vc.id_vaga
            WHERE v.id_empresa = ?
        """
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idEmpresa)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                competencias.add(new Competencia(id: rs.getInt("id"), nome: rs.getString("nome")))
            }
        }
        return competencias
    }

    void deletar(String cnpj) {
        String sql = "DELETE FROM empresas WHERE cnpj = ?"

        DatabaseConnection.getConnection().withCloseable { Connection conn ->
            conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                stmt.setString(1, cnpj)
                int linhasAfetadas = stmt.executeUpdate()

                if (linhasAfetadas > 0) {
                    println "Empresa (e suas vagas vinculadas) deletada com sucesso!"
                } else {
                    println "Nenhuma empresa encontrada com o CNPJ informado."
                }
            }
        }
    }
}