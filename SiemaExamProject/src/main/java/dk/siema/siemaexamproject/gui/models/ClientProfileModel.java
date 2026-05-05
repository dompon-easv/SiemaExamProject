package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.service.ClientProfileService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientProfileModel {

    private ClientProfileService clientProfileService;

    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final ObservableList<ScanningProfile> profiles = FXCollections.observableArrayList();

    public ClientProfileModel(ClientProfileService clientProfileService) {
        this.clientProfileService = clientProfileService;
    }

    public void loadAllClients() throws ServiceException {
        clients.setAll(clientProfileService.getAllClients());
    }
    public ObservableList<Client> getClients() {
        return clients;
    }

    public void createClient(Client client) throws ServiceException {
        clientProfileService.createClient(client);
        clients.add(client);
    }

    public void deleteClient(Client client) throws ServiceException {
        clientProfileService.deleteClient(client);
        clients.remove(client);
    }

    public void updateClient(Client client) throws ServiceException {
        clientProfileService.updateClient(client);
        int index = clients.indexOf(client);
        if (index >= 0) {
            clients.set(index, client);
        }
    }

    public void loadProfilesByClient(int clientId) throws ServiceException {
        if(clientId > 0){
        profiles.setAll(clientProfileService.getProfilesByClient(clientId));
    } else{
        profiles.clear();}
    }

    public ObservableList<ScanningProfile> getProfiles() throws ServiceException {
        return profiles;
    }

    public void createProfile(ScanningProfile profile) throws ServiceException {
        clientProfileService.createProfile(profile);
        profiles.add(profile);
    }

    public void deleteProfile(ScanningProfile profile) throws ServiceException {
        clientProfileService.deleteProfile(profile);
        profiles.remove(profile);
    }




}
