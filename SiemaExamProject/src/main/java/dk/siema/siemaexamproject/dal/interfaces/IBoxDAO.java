package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.be.Box;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.gui.ScannerViewController;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IBoxDAO {
    void stageFile(FileEntity fileEntity);
    //void saveBox(Box box) throws DalException;

    void saveBox(Box box, List<ActivityLog> logs) throws DalException;

    void deleteStagedFile(UUID referenceId) throws DalException;
}
