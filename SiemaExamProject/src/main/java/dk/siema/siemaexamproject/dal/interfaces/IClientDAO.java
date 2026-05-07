package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.DalException;

import java.sql.SQLException;
import java.util.List;

public interface IClientDAO {
    Client add(Client client) throws DalException;

    List<Client> getAllClients() throws DalException;

    Client getByClientName(String name) throws DalException;

    void deleteClient(Client client) throws DalException;

    void updateClient(Client client) throws DalException;
}
