package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.*;
import dk.siema.siemaexamproject.bll.exceptions.BackendFailureException;
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

    public void loadAllClients()  {
        clients.setAll(clientProfileService.getAllClients());
    }
    public ObservableList<Client> getClients() {
        return clients;
    }

    public void createClient(Client client) {
        clientProfileService.createClient(client);
        clients.add(client);
    }

    public void deleteClient(Client client){
        clientProfileService.deleteClient(client);
        clients.remove(client);
    }

    public void updateClient(Client client)  {
        clientProfileService.updateClient(client);
        int index = clients.indexOf(client);
        if (index >= 0) {
            clients.set(index, client);
        }
    }

    public void saveNewProfile(ScanningProfile profile) {
        clientProfileService.createProfile(profile);
        allProfiles.add(profile);
    }

    public void deleteProfile(ScanningProfile profile) {
        clientProfileService.deleteProfile(profile);
        masterProfiles.remove(profile);
        allProfiles.remove(profile);
    }

    public void loadAllSettings() {
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
        List<ScanningProfile> filteredProfiles = new ArrayList<>();
        for(ScanningProfile profile: masterProfiles)
        {
            if(profile.getClientId() == clientId) filteredProfiles.add(profile);}

        return filteredProfiles;

    }

    public ObservableList<ScanningProfile> getAllProfiles() {
        return allProfiles;
    }
    List<ScanningProfile> profiles = new ArrayList<>();
    List<ScanningProfile> masterProfiles = new ArrayList<>();


    public void loadAllProfilesFromService () {
        this.masterProfiles = clientProfileService.getAllProfiles();
        this.allProfiles.setAll(this.masterProfiles);
    }

    public void filterByClient(int clientId) {
        List<ScanningProfile> filteredProfiles = new ArrayList<>();
        for(ScanningProfile profile: masterProfiles)
        {
            if(profile.getClientId() == clientId) filteredProfiles.add(profile);
        }
        this.allProfiles.setAll(filteredProfiles);
    }

    public void updateProfile(ScanningProfile profileToEdit) {
        clientProfileService.updateProfile(profileToEdit);

        for (int i = 0; i < masterProfiles.size(); i++) {
            if (masterProfiles.get(i).getId() == profileToEdit.getId()) {
                masterProfiles.set(i, profileToEdit);
                break;
            }
    }
        allProfiles.setAll(masterProfiles);
}
}
