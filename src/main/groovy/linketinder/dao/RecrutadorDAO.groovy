package linketinder.dao

import linketinder.dao.interfaces.ICrudDAO
import linketinder.model.Recrutador

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class RecrutadorDAO implements ICrudDAO<Recrutador, Integer> {

    void inserir(Recrutador r) {
        String sql = "INSERT INTO recrutadores (nome, email, cpf, senha, data_nascimento, area_recrutamento, descricao, id_empresa) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        try {
            ConnectionFactory.instance.criarConexao("POSTGRES").withCloseable { Connection conn ->
                conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, r.nome)
                    stmt.setString(2, r.email)
                    stmt.setString(3, r.cpf)
                    stmt.setString(4, r.senha)
                    stmt.setObject(5, r.dataNascimento)
                    stmt.setString(6, r.areaRecrutamento)
                    stmt.setString(7, r.descricao)
                    stmt.setInt(8, r.idEmpresa)

                    stmt.executeUpdate()

                    ResultSet rsKeys = stmt.getGeneratedKeys()
                    if (rsKeys.next()) {
                        r.id = rsKeys.getInt(1)
                    }
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao salvar o recrutador no banco de dados.", erro)
        }
    }

    List<Recrutador> listar() {
        List<Recrutador> lista = []
        String sql = "SELECT * FROM recrutadores"
        try {
            ConnectionFactory.instance.criarConexao("POSTGRES").withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    ResultSet rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(mapearRecrutador(rs))
                    }
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao listar recrutadores do banco de dados.", erro)
        }
        return lista
    }

    List<Recrutador> listarPorEmpresa(Integer idEmpresa) {
        List<Recrutador> lista = []
        String sql = "SELECT * FROM recrutadores WHERE id_empresa = ?"
        try {
            ConnectionFactory.instance.criarConexao("POSTGRES").withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, idEmpresa)
                    ResultSet rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(mapearRecrutador(rs))
                    }
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao listar recrutadores da empresa do banco de dados.", erro)
        }
        return lista
    }

    boolean deletar(Integer id) {
        String sql = "DELETE FROM recrutadores WHERE id = ?"
        try {
            boolean deletado = false
            ConnectionFactory.instance.criarConexao("POSTGRES").withCloseable { Connection conn ->
                conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, id)
                    int linhasAfetadas = stmt.executeUpdate()
                    deletado = (linhasAfetadas > 0)
                }
            }
            return deletado
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao deletar o recrutador no banco de dados.", erro)
        }
    }

    boolean emailJaEmUso(String email) {
        String sqlCandidatos = "SELECT COUNT(*) FROM candidatos WHERE email = ?"
        String sqlEmpresas = "SELECT COUNT(*) FROM empresas WHERE email = ?"
        String sqlRecrutadores = "SELECT COUNT(*) FROM recrutadores WHERE email = ?"

        try {
            ConnectionFactory.instance.criarConexao("POSTGRES").withCloseable { Connection conn ->
                // Verifica em candidatos
                conn.prepareStatement(sqlCandidatos).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, email)
                    ResultSet rs = stmt.executeQuery()
                    if (rs.next() && rs.getInt(1) > 0) return true
                }

                // Verifica em empresas
                conn.prepareStatement(sqlEmpresas).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, email)
                    ResultSet rs = stmt.executeQuery()
                    if (rs.next() && rs.getInt(1) > 0) return true
                }

                // Verifica em recrutadores
                conn.prepareStatement(sqlRecrutadores).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, email)
                    ResultSet rs = stmt.executeQuery()
                    if (rs.next() && rs.getInt(1) > 0) return true
                }

                return false
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao verificar unicidade de e-mail.", erro)
        }
    }

    boolean cpfJaEmUso(String cpf) {
        String sqlCandidatos = "SELECT COUNT(*) FROM candidatos WHERE cpf = ?"
        String sqlRecrutadores = "SELECT COUNT(*) FROM recrutadores WHERE cpf = ?"

        try {
            ConnectionFactory.instance.criarConexao("POSTGRES").withCloseable { Connection conn ->
                // Verifica em candidatos
                conn.prepareStatement(sqlCandidatos).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, cpf)
                    ResultSet rs = stmt.executeQuery()
                    if (rs.next() && rs.getInt(1) > 0) return true
                }

                // Verifica em recrutadores
                conn.prepareStatement(sqlRecrutadores).withCloseable { PreparedStatement stmt ->
                    stmt.setString(1, cpf)
                    ResultSet rs = stmt.executeQuery()
                    if (rs.next() && rs.getInt(1) > 0) return true
                }

                return false
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao verificar unicidade de CPF.", erro)
        }
    }

    private Recrutador mapearRecrutador(ResultSet rs) throws SQLException {
        Recrutador r = new Recrutador()
        r.id = rs.getInt("id")
        r.nome = rs.getString("nome")
        r.email = rs.getString("email")
        r.cpf = rs.getString("cpf")
        r.senha = rs.getString("senha")
        java.sql.Date dbDate = rs.getDate("data_nascimento")
        r.dataNascimento = dbDate != null ? dbDate.toLocalDate() : null
        r.areaRecrutamento = rs.getString("area_recrutamento")
        r.descricao = rs.getString("descricao")
        r.idEmpresa = rs.getInt("id_empresa")
        return r
    }
}
