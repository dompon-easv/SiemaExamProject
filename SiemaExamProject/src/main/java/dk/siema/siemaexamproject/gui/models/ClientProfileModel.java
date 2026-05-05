package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.*;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.service.ClientProfileService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ClientProfileModel {

    private ClientProfileService clientProfileService;

    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final ObservableList<ScanningProfile> allProfiles = FXCollections.observableArrayList();
    private final ObservableList<Setting> settings = FXCollections.observableArrayList();
    private final ObservableList<ProfileSetting> pendingSettings = FXCollections.observableArrayList();

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

    public void getAllProfiles() throws ServiceException {
    List<ScanningProfile> profiles = clientProfileService.getAllProfiles();
    this.allProfiles.clear();
    this.allProfiles.addAll(profiles);
    }

    public void saveNewProfile(ScanningProfile profile) throws ServiceException {
        clientProfileService.createProfile(profile);
        allProfiles.add(profile);
    }

    public void deleteProfile(ScanningProfile profile) throws ServiceException {
        clientProfileService.deleteProfile(profile);
        allProfiles.remove(profile);
    }

    public void loadAllSettings() throws ServiceException {
        settings.setAll(clientProfileService.getAllSettings());
    }
    public ObservableList<Setting> getAllSettings() {
        return settings;
    }

    public ObservableList<ProfileSetting> getPendingSettings() {
        return pendingSettings;
    }
    public void addPendingSetting(ProfileSetting setting) throws ServiceException {
        pendingSettings.add(setting);
    }
    public void clearPendingSetting() {
        pendingSettings.clear();
    }

    public List<ScanningProfile> getProfilesForClient(int clientId) {
        List<ScanningProfile> profiles = new ArrayList<>();
        for(ScanningProfile profile: allProfiles)
        {if(profile.getClientId() == clientId) profiles.add(profile);}

        return profiles;

    }

    public List<ScanningProfile> getAllProfilesList() {
        return new ArrayList<>(allProfiles);
    }
}
