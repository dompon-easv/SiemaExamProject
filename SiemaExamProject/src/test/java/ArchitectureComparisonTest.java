import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.bll.service.UserService;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArchitectureComparisonTest {

    @Test
    @DisplayName("Allow Invalid objects creation")
    public void testVulnerableArchitecture_AllowsInvalidObjectCreation(){
        User corrupterUser = new User(
                "",
                "",
                null,
                UserRole.ADMIN);

        assertNotNull(corrupterUser, "The object was created successfully!");
        assertEquals("", corrupterUser.getUsername(), "The object is holding wrong data");

        // THE RESULT:
        // We now have a 'corrupterUser' floating around in our RAM.
        // If the Model adds this to the ObservableList, the UI breaks.
        // If the DAO saves this, the Database breaks.
    }

    @Test
    @DisplayName("Service prevents invalid object creation")
    public void testVulnerableArchitecture_PreventsInvalidObjectCreation(){

        //set up the logic layer
        IUserDAO fakeDao = new FakeUserDAO();
        UserService service = new UserService(fakeDao);

        //ACT 1
        //The UI creates bad object and we acknowledge now the object exists in memory
        User badUser  = new User("","not-notes",null,UserRole.ADMIN);

        /*ACT 2 & ASSERT
        //hand the bad object to service
        //service inspects the object and throws*/

        Exception exception = assertThrows(ValidationException.class,() -> {
            service.createUser(badUser); // passing the bad object
        });

        //prove that service caught the empty username
        assertEquals("Username is required", exception.getMessage());

        /*even though that current architecture allows to create the User object,
        the UserService acts as a strict validator - it caught bad data and threw
        and exception before DAO could reach the database
        */
    }

}
