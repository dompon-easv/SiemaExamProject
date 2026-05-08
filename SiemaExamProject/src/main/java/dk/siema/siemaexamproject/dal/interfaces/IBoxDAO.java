package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Box;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.gui.ScannerViewController;

public interface IBoxDAO {
    void saveBox(Box box) throws DalException;
}
