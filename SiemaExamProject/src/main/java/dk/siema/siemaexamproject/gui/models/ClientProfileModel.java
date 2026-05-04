package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.service.ClientProfileService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientProfileModel {

    private ClientProfileService clientProfileService;

    private final ObservableList<Client> clients = FXCollections.observableArrayList();

    public ClientProfileModel(ClientProfileService clientProfileService) {
        this.clientProfileService = clientProfileService;
    }

    public void loadAllClients() throws ServiceException {
        clients.setAll(clientProfileService.getAllClients());
    }
    public ObservableList<Client> getClients() {
        return clients;
    }

    public void createClient(Client client) throws ServiceException {
        clientProfileService.createClient(client);
        clients.add(client);
    }

    public void deleteClient(Client client) throws ServiceException {
        clientProfileService.deleteClient(client);
        clients.remove(client);
    }

    public void updateClient(Client client) throws ServiceException {
        clientProfileService.updateClient(client);
        int index = clients.indexOf(client);
        if (index >= 0) {
            clients.set(index, client);
        }
    }
}
