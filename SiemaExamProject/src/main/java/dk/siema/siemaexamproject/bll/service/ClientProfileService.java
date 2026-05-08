package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.bll.exceptions.BackendFailureException;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.dal.interfaces.IClientDAO;
import dk.siema.siemaexamproject.dal.interfaces.IScanningProfileDAO;
import dk.siema.siemaexamproject.dal.interfaces.ISettingDAO;

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

    public List<Client> getAllClients() throws BackendFailureException {
        try {
            return clientDAO.getAllClients();
        } catch (DalException e) {
            throw new BackendFailureException("Error fetching clients");
        }
    }

    public Client createClient(Client client) throws BackendFailureException, ValidationException {
        if (client.getName() == null || client.getName().isBlank()) {
            throw new ValidationException("Client name is required");
        }
        try {
            return clientDAO.add(client);
        } catch (DalException e) {
            throw new BackendFailureException("Error creating client");
        }

    }

    public void deleteClient(Client client) throws BackendFailureException {
        try {
            clientDAO.deleteClient(client);
        } catch (DalException e) {
            throw new BackendFailureException("Error deleting client");
        }
    }

    public void updateClient(Client client) throws BackendFailureException {
        try{
            clientDAO.updateClient(client);
        }catch (DalException e)
        {
            throw new BackendFailureException("Error updating client");
        }
    }

    public ScanningProfile createProfile(ScanningProfile profile) throws BackendFailureException, ValidationException {
        if (profile.getName() == null || profile.getName().isBlank()) {
            throw new ValidationException("Profile name is required");
        }
        try {
            return scanningProfileDAO.add(profile);
        } catch (DalException e) {
            throw new BackendFailureException("Error creating profile");
        }

    }

    public void deleteProfile(ScanningProfile profile) throws BackendFailureException {
        try {
            scanningProfileDAO.deleteProfile(profile);
        } catch (DalException e) {
            throw new BackendFailureException("Error deleting profile");
        }
    }

    public List<Setting> getAllSettings() throws BackendFailureException {
        try{
           return settingDAO.getAllSettings();
        } catch (DalException e){
            throw new BackendFailureException("Error fetching settings");
        }
    }

    public List<ScanningProfile> getAllProfiles() throws BackendFailureException {
        try {
            return scanningProfileDAO.getAllProfiles();
        } catch (DalException e) {
            throw new BackendFailureException("Error fetching clients");
        }
    }

    public void updateProfile(ScanningProfile profileToEdit) throws BackendFailureException {
        try{
            scanningProfileDAO.updateProfile(profileToEdit);
        }catch (DalException e)
        {
            throw new BackendFailureException("Error updating client");
        }
    }
}
