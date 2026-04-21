package dk.siema.siemaexamproject.dal.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IRepository<T> {
    void add(T entity)throws SQLException;
    //void update(T entity);
    //void delete(int id)throws SQLException;
    //T getById(int id);
    List<T> getAll() throws SQLException;
}
