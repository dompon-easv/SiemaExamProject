package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.User;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface IUserDAO {

    User add(User entity) throws SQLException;

    void update(User entity) throws SQLException;

    void delete(UUID id) throws SQLException;

    User getById(UUID id) throws SQLException;

    List<User> getAll() throws SQLException;

    User getByUsername(String username) throws SQLException;

    void updatePassword(UUID id, String newHash) throws SQLException;
}