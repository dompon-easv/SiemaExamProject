package dk.siema.siemaexamproject;

import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.bll.service.UserService;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.dal.dao.UserDAO;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;

public class TestUserService {

    public static void main(String[] args) {

        try {
            IUserDAO userDAO = new UserDAO();
            UserService userService = new UserService(userDAO);

            User user = new User("Employee", "Employee@test.com", "Employee", UserRole.EMPLOYEE);

            userService.createUser(user);

            System.out.println("User created successfully");

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}

/* Even if not wired to applicationServices or any controller its working as intended
please don´t go changing stuff around without checking whats in the project first.  
*
Database=Siema_Exam_Project1
*/


/*
* create table Users
(
    id            binary(16)   not null
        primary key,
    username      varchar(50)  not null
        unique,
    email         varchar(100) not null
        unique,
    password_hash varchar(255) not null,
    role          varchar(25)  not null
        constraint CK_Users_Role
            check ([role] = 'EMPLOYEE' OR [role] = 'ADMIN')
)
go

*/