package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.dal.exception.DalException;

import java.util.List;

public interface IScanningProfileDAO {

    ScanningProfile add(ScanningProfile profile) throws DalException;

    List<ScanningProfile> getProfilesbyClient(int clientId) throws DalException;

    ScanningProfile getByProfileName(String name) throws DalException;

    void deleteProfile(ScanningProfile profile) throws DalException;

    List<ScanningProfile> getAllProfiles() throws DalException;

    void updateProfile(ScanningProfile profileToEdit) throws DalException;
}
