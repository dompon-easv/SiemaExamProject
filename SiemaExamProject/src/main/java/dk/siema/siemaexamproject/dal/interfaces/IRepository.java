package dk.siema.siemaexamproject.dal.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IRepository<T, ID> {
    T add(T entity)throws SQLException;
    void update(T entity) throws SQLException;
    void delete(ID id)throws SQLException;
    T getById(ID id) throws SQLException;
    List<T> getAll() throws SQLException;
}
/* Use ONE generic base interface (IRepository), and extend it per entity :) */
