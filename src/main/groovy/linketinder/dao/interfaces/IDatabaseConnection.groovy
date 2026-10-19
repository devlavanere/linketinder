package linketinder.dao.interfaces

import java.sql.Connection

interface IDatabaseConnection {
    Connection conectar()
}