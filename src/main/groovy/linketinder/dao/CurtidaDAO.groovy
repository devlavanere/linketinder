package linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CurtidaDAO {

    // CASO 1: Sandubinha curte a Vaga da Pastelsoft
    boolean curtirVaga(int idCandidato, int idVaga) {
        boolean isMatch = false

        String sqlInsertCurtida = "INSERT INTO curtidas_candidatos (id_candidato, id_vaga) VALUES (?, ?) ON CONFLICT DO NOTHING"

        // Verifica se a empresa dona vaga curtiu o candidato
        String sqlCheckMatch = """
            SELECT 1 FROM vagas v
            JOIN curtidas_empresas ce 
            ON ce.id_empresa = v.id_empresa
            WHERE v.id = ? AND ce.id_candidato = ? 
        """
        String sqlInsertMatch = "INSERT INTO matches (id_candidato, id_vaga) VALUES (?, ?) ON CONFLICT DO NOTHING"

        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                // 1. Grava a curtida
                conn.prepareStatement(sqlInsertCurtida).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, idCandidato)
                    stmt.setInt(2, idVaga)
                    stmt.executeUpdate()
                }

                // 2. Olha pro lado da empresa e verifica se bateu
                conn.prepareStatement(sqlCheckMatch).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, idVaga)
                    stmt.setInt(2, idCandidato)
                    ResultSet rs = stmt.executeQuery()
                    if (rs.next()) isMatch = true
                }

                // 3. Se deu Match, já insere na tabela final!
                if (isMatch) {
                    conn.prepareStatement(sqlInsertMatch).withCloseable { PreparedStatement stmt ->
                        stmt.setInt(1, idCandidato)
                        stmt.setInt(2, idVaga)
                        stmt.executeUpdate()
                    }
                }
            }
        } catch (Exception e) {
            println "Erro ao processar curtida: " + e.getMessage()
        }
        return isMatch
    }

    // CASO 2: Empresa curte o Candidato
    boolean curtirCandidato(int idEmpresa, int idCandidato) {
        boolean isMatch = false

        String sqlInsertCurtida = "INSERT INTO curtidas_empresas (id_empresa, id_candidato) VALUES (?, ?) ON CONFLICT DO NOTHING"

        // Verifica se esse candidato já curtiu ALGUMA vaga que pertence a essa empresa
        String sqlCheckMatch = """
            SELECT v.id AS id_vaga FROM vagas v
            JOIN curtidas_candidatos cc 
            ON cc.id_vaga = v.id
            WHERE v.id_empresa = ? AND cc.id_candidato = ?
        """

        String sqlInsertMatch = "INSERT INTO matches (id_candidato, id_vaga) VALUES (?, ?) ON CONFLICT DO NOTHING"

        try {
            DatabaseConnection.getConnection().withCloseable { Connection conn ->
                // 1. Grava a curtida
                conn.prepareStatement(sqlInsertCurtida).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, idEmpresa)
                    stmt.setInt(2, idCandidato)
                    stmt.executeUpdate()
                }

                // 2. Busca todas as vagas dessa empresa que o candidato já havia curtido
                List<Integer> vagasComMatch = []
                conn.prepareStatement(sqlCheckMatch).withCloseable { PreparedStatement stmt ->
                    stmt.setInt(1, idEmpresa)
                    stmt.setInt(2, idCandidato)
                    ResultSet rs = stmt.executeQuery()
                    while (rs.next()) {
                        vagasComMatch.add(rs.getInt("id_vaga"))
                    }
                }

                // 3. Grava o match para cada vaga que cruzou o interesse!
                if (!vagasComMatch.isEmpty()) {
                    isMatch = true
                    conn.prepareStatement(sqlInsertMatch).withCloseable { PreparedStatement stmt ->
                        vagasComMatch.each { int vagaId ->
                            stmt.setInt(1, idCandidato)
                            stmt.setInt(2, vagaId)
                            stmt.executeUpdate()
                        }
                    }
                }
            }
        } catch (Exception e) {
            println "Erro ao processar curtida: " + e.getMessage()
        }
        return isMatch
    }

}
