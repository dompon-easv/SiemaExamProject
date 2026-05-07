package dk.siema.siemaexamproject.dal.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.bll.exceptions.DalException;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IClientDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO implements IClientDAO {

    @Override
    public Client add(Client client) throws DalException {
        String sql = "INSERT INTO dbo.Clients (name) VALUES (?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getName());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    client.setId(newId);
                }

                return client;
            }
        } catch (SQLException e) {
            throw new DalException("Error inserting new client", e);
        }
    }


    @Override
    public List<Client> getAllClients() throws DalException {
        String sql = "SELECT * FROM dbo.Clients";

        List<Client> clients = new ArrayList<>();

                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        clients.add(mapClient(rs));
                    }
                } catch (SQLException e) {
                    throw new DalException("Error fetching all clients", e);
                }

        return clients;
            }

    @Override
    public Client getByClientName(String name) throws DalException {
        String sql = "SELECT * FROM dbo.Client WHERE name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapClient(rs);
                }
            }
        } catch (SQLException e) {
            throw new DalException("Error fetching Client",e);
        }

        return null;
    }

    @Override
    public void deleteClient(Client client) throws DalException {
        String sql = "DELETE FROM dbo.Clients WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, client.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DalException("Error deleting Client",e);
        }

    }

    @Override
    public void updateClient(Client client) throws DalException {
        String sql = "UPDATE dbo.Clients SET name = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setInt(2, client.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DalException("Error updating Client",e);
        }
    }

    private Client mapClient(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Client(id, name);
    }

}
