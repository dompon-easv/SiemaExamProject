package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Box;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.gui.ScannerViewController;

import java.io.File;

public interface IBoxDAO {
    void stageFile(FileEntity fileEntity);
    void saveBox(Box box) throws DalException;
}
