package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.User;

import java.sql.SQLException;
import java.util.List;

public interface IClientDAO {
    Client add(Client client) throws SQLException;

    List<Client> getAllClients() throws SQLException;

    Client getByClientName(String name) throws SQLException;

    void deleteClient(Client client) throws SQLException;

    void updateClient(Client client) throws SQLException;
}
