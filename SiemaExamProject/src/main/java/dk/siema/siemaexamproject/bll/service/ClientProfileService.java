package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.bll.exceptions.DataAccessException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.dal.interfaces.IClientDAO;
import dk.siema.siemaexamproject.dal.interfaces.IScanningProfileDAO;
import dk.siema.siemaexamproject.dal.interfaces.ISettingDAO;

import java.sql.SQLException;
import java.util.List;

public class ClientProfileService {

    private final IClientDAO clientDAO;
    private final IScanningProfileDAO scanningProfileDAO;
    private final ISettingDAO settingDAO;

    public ClientProfileService(IClientDAO clientDAO, IScanningProfileDAO scanningProfileDAO, ISettingDAO settingDAO) {
        this.clientDAO = clientDAO;
        this.scanningProfileDAO = scanningProfileDAO;
        this.settingDAO = settingDAO;
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

    public List<ScanningProfile> getProfilesByClient(int clientId) throws ServiceException {
        try {
            return scanningProfileDAO.getProfilesbyClient(clientId);
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching profiles", e);
        }
    }

    public ScanningProfile createProfile(ScanningProfile profile) throws ServiceException {
        if (profile.getName() == null || profile.getName().isBlank()) {
            throw new ValidationException("Profile name is required");
        }
        try {
            return scanningProfileDAO.add(profile);
        } catch (SQLException e) {
            throw new DataAccessException("Error creating profile", e);
        }

    }

    public void deleteProfile(ScanningProfile profile) throws ServiceException {
        try {
            scanningProfileDAO.deleteProfile(profile);
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting profile", e);
        }
    }

    public List<Setting> getAllSettings() throws ServiceException {
        try{
           return settingDAO.getAllSettings();
        } catch (SQLException e){
            throw new DataAccessException("Error fetching settings", e);
        }
    }
}
