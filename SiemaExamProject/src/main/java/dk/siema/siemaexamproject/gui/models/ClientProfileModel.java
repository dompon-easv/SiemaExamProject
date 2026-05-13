package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.*;
import dk.siema.siemaexamproject.bll.exceptions.BackendFailureException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.bll.service.ClientProfileService;
import dk.siema.siemaexamproject.dal.dao.SettingDAO;
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

    public void loadAllClients() throws BackendFailureException {
        clients.setAll(clientProfileService.getAllClients());
    }
    public ObservableList<Client> getClients() {
        return clients;
    }

    public void createClient(Client client) throws BackendFailureException, ValidationException {
        clientProfileService.createClient(client);
        clients.add(client);
    }

    public void deleteClient(Client client) throws BackendFailureException {
        clientProfileService.deleteClient(client);
        clients.remove(client);
    }

    public void updateClient(Client client) throws BackendFailureException {
        clientProfileService.updateClient(client);
        int index = clients.indexOf(client);
        if (index >= 0) {
            clients.set(index, client);
        }
    }

    public void saveNewProfile(ScanningProfile profile) throws BackendFailureException, ValidationException {
        clientProfileService.createProfile(profile);
        allProfiles.add(profile);
    }

    public void deleteProfile(ScanningProfile profile) throws BackendFailureException {
        clientProfileService.deleteProfile(profile);
        masterProfiles.remove(profile);
        allProfiles.remove(profile);
    }

    public void loadAllSettings() throws BackendFailureException {
        settings.setAll(clientProfileService.getAllSettings());
    }
    public ObservableList<Setting> getAllSettings() {
        return settings;
    }

    public ObservableList<ProfileSetting> getPendingSettings() {
        return pendingSettings;
    }

    public void addPendingSetting(ProfileSetting setting) throws ServiceException {
        for (ProfileSetting existing : pendingSettings) {
            if (existing.getSetting().getName().equalsIgnoreCase(setting.getSetting().getName())) {
                throw new ServiceException("The setting '" + setting.getSetting().getName() + "' is already added.");
            }
        }
        pendingSettings.add(setting);

    }

    public void clearPendingSetting() {
        pendingSettings.clear();
    }
    public void removePendingSetting(ProfileSetting setting) {
        if (setting != null) {
            pendingSettings.remove(setting);
        }
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


    public void loadAllProfilesFromService () throws BackendFailureException {
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

    public void updateProfile(ScanningProfile profileToEdit) throws BackendFailureException {
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
