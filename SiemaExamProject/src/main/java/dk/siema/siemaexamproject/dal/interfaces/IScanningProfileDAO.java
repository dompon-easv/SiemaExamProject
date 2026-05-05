package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ScanningProfile;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public interface IScanningProfileDAO {

    ScanningProfile add(ScanningProfile profile) throws SQLException;

    List<ScanningProfile> getProfilesbyClient(int clientId) throws SQLException;

    ScanningProfile getByProfileName(String name) throws SQLException;

    void deleteProfile(ScanningProfile profile) throws SQLException;

    List<ScanningProfile> getAllProfiles() throws SQLException;
}
