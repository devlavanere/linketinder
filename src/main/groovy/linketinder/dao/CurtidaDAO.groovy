package linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CurtidaDAO {

    boolean curtirVaga(int idCandidato, int idVaga) {
        try {
            boolean isMatch = false
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                inserirCurtidaCandidato(conn, idCandidato, idVaga)

                if (verificarEmpresaCurtiuCandidato(conn, idCandidato, idVaga)) {
                    isMatch = true
                    registrarMatch(conn, idCandidato, idVaga)
                }
            }
            return isMatch
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao processar a curtida do candidato.", erro)
        }
    }

    boolean curtirCandidato(int idEmpresa, int idCandidato) {
        try {
            boolean isMatch = false
            DatabaseConnection.getConnection().withCloseable { Connection conn ->

                inserirCurtidaEmpresa(conn, idEmpresa, idCandidato)

                List<Integer> vagasComMatch = buscarVagasParaMatch(conn, idEmpresa, idCandidato)

                if (!vagasComMatch.isEmpty()) {
                    isMatch = true
                    vagasComMatch.each { int idVaga ->
                        registrarMatch(conn, idCandidato, idVaga)
                    }
                }
            }
            return isMatch
        } catch (SQLException erro) {
            throw new RuntimeException("Falha ao processar a curtida da empresa.", erro)
        }
    }

    // MÉTODOS PRIVADOS AUXILIARES (Funções Pequenas, SRP e DRY)

    private void inserirCurtidaCandidato(Connection conn, int idCandidato, int idVaga) throws SQLException {
        String sql = "INSERT INTO curtidas_candidatos (id_candidato, id_vaga) VALUES (?, ?) ON CONFLICT DO NOTHING"
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idCandidato)
            stmt.setInt(2, idVaga)
            stmt.executeUpdate()
        }
    }

    private void inserirCurtidaEmpresa(Connection conn, int idEmpresa, int idCandidato) throws SQLException {
        String sql = "INSERT INTO curtidas_empresas (id_empresa, id_candidato) VALUES (?, ?) ON CONFLICT DO NOTHING"
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idEmpresa)
            stmt.setInt(2, idCandidato)
            stmt.executeUpdate()
        }
    }

    private boolean verificarEmpresaCurtiuCandidato(Connection conn, int idCandidato, int idVaga) throws SQLException {
        String sql = """
            SELECT 1 FROM vagas v
            JOIN curtidas_empresas ce ON ce.id_empresa = v.id_empresa
            WHERE v.id = ? AND ce.id_candidato = ? 
        """
        boolean curtiu = false
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idVaga)
            stmt.setInt(2, idCandidato)
            ResultSet rs = stmt.executeQuery()
            curtiu = rs.next()
        }
        return curtiu
    }

    private List<Integer> buscarVagasParaMatch(Connection conn, int idEmpresa, int idCandidato) throws SQLException {
        List<Integer> vagas = []
        String sql = """
            SELECT v.id AS id_vaga FROM vagas v
            JOIN curtidas_candidatos cc ON cc.id_vaga = v.id
            WHERE v.id_empresa = ? AND cc.id_candidato = ?
        """
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idEmpresa)
            stmt.setInt(2, idCandidato)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                vagas.add(rs.getInt("id_vaga"))
            }
        }
        return vagas
    }

    // DRY Aplicado: Esse método é chamado por ambas as lógicas de Match!
    private void registrarMatch(Connection conn, int idCandidato, int idVaga) throws SQLException {
        String sql = "INSERT INTO matches (id_candidato, id_vaga) VALUES (?, ?) ON CONFLICT DO NOTHING"
        conn.prepareStatement(sql).withCloseable { PreparedStatement stmt ->
            stmt.setInt(1, idCandidato)
            stmt.setInt(2, idVaga)
            stmt.executeUpdate()
        }
    }
}