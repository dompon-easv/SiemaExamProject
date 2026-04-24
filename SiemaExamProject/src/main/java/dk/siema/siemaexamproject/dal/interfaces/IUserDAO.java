package dk.siema.siemaexamproject.dal.interfaces;
import dk.siema.siemaexamproject.be.User;
import java.sql.SQLException;
import java.util.UUID;

public interface IUserDAO extends IRepository<User, UUID> {

    User getByUsername(String username) throws SQLException;
    void updatePassword(UUID id, String newHash) throws SQLException;
}
