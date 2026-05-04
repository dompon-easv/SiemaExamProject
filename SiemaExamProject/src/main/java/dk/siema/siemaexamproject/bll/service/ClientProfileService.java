package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.bll.exceptions.DataAccessException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.dal.interfaces.IClientDAO;

import java.sql.SQLException;
import java.util.List;

public class ClientProfileService {

    private final IClientDAO clientDAO;

    public ClientProfileService(IClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public List<Client> getAllClients() throws ServiceException {
        try {
            return clientDAO.getAllClients();
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching clients", e);
        }
    }

    public Client createClient(Client client) throws ServiceException {
        if (client.getName() == null || client.getName().isBlank()) {
            throw new ValidationException("Client name is required");
        }
        try {
            return clientDAO.add(client);
        } catch (SQLException e) {
            throw new DataAccessException("Error creating client", e);
        }

    }

    public void deleteClient(Client client) throws ServiceException {
        try {
            clientDAO.deleteClient(client);
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting client", e);
        }
    }

    public void updateClient(Client client) throws ServiceException {
        try{
            clientDAO.updateClient(client);
        }catch (SQLException e)
        {
            throw new DataAccessException("Error updating client", e);
        }
    }
}
