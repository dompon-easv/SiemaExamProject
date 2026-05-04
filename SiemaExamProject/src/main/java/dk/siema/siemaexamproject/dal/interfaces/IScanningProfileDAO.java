package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Client;

import java.sql.SQLException;

public interface IScanningProfileDAO {
    Client add(Client client) throws SQLException;
}
