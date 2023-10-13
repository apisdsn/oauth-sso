CREATE DATABASE reimbursement;
USE reimbursement;

CREATE TABLE address (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         address_id VARCHAR(100),
                         street VARCHAR(200),
                         city VARCHAR(100),
                         province VARCHAR(100),
                         country VARCHAR(100),
                         postal_code VARCHAR(10),
                         UNIQUE (address_id)

) ENGINE InnoDB;

CREATE TABLE reimbursement (

                               reimbursement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               client_id VARCHAR(100) NOT NULL,
                               address_id VARCHAR(100) NOT NULL,
                               amount DECIMAL(10,2) NOT NULL,
                               currency VARCHAR(10) NOT NULL,
                               description VARCHAR(255) NOT NULL,
                               status BOOLEAN DEFAULT FALSE NOT NULL,
                               date_created DATETIME NOT NULL,
                               date_updated DATETIME NOT NULL,
                               UNIQUE (client_id, address_id)

) ENGINE InnoDB;

CREATE TABLE employees (
                           employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           client_id VARCHAR(100) NOT NULL,
                           address_id VARCHAR(100) NOT NULL,
                           full_name VARCHAR(50) NOT NULL,
                           phone_number VARCHAR(50),
                           email VARCHAR(50) NOT NULL,
                           company VARCHAR(50) NOT NULL,
                           position VARCHAR(50) NOT NULL,
                           gender VARCHAR(20) NOT NULL,
                           UNIQUE (email, address_id, client_id),
                           CONSTRAINT FK_employees_addressId FOREIGN KEY (address_id) REFERENCES address(address_id),
                           CONSTRAINT FK_reimbursement_clientId FOREIGN KEY (client_id) REFERENCES reimbursement(client_id)

) ENGINE InnoDB;




DROP DATABASE reimbursement;





# CREATE DATABASE reimbursement;
#
# USE reimbursement;
#
# CREATE TABLE address (
#                     address_id VARCHAR(100) NOT NULL,
#                     street VARCHAR(200),
#                     city VARCHAR(100),
#                     province VARCHAR(100),
#                     country VARCHAR(100),
#                     postal_code VARCHAR(10),
#                     PRIMARY KEY (address_id)
#
# ) ENGINE InnoDB;
#
# CREATE TABLE authorities (
#                     id INT AUTO_INCREMENT PRIMARY KEY,
#                     authority VARCHAR(255) NOT NULL
# );
#
#
# CREATE TABLE employees (
#                     employee_id INT NOT NULL AUTO_INCREMENT,
#                     client_id VARCHAR(50) NOT NULL,
#                     address_id VARCHAR(100),
#                     full_name VARCHAR(50) NOT NULL,
#                     phone_number VARCHAR(50),
#                     email VARCHAR(50) NOT NULL,
#                     company VARCHAR(50) NOT NULL,
#                     position VARCHAR(50) NOT NULL,
#                     gender VARCHAR(20) NOT NULL,
#                     PRIMARY KEY (employee_id),
#                     UNIQUE (email, address_id, client_id),
#                     CONSTRAINT FK_employees_clientId FOREIGN KEY (client_id) REFERENCES address(address_id),
#                     CONSTRAINT FK_employees_addressId FOREIGN KEY (address_id) REFERENCES address(address_id)
#
# ) ENGINE InnoDB;
