CREATE DATABASE reimbursement;
USE reimbursement;

CREATE TABLE employees (
                           employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           client_id VARCHAR(50) NOT NULL,
                           full_name VARCHAR(50) NOT NULL,
                           phone_number VARCHAR(50),
                           email VARCHAR(50) NOT NULL,
                           company VARCHAR(50) NOT NULL,
                           position VARCHAR(50) NOT NULL,
                           gender VARCHAR(20) NOT NULL,
                           UNIQUE KEY clientId_unique(client_id),
                           UNIQUE KEY email_unique(email)
) ENGINE InnoDB;

CREATE TABLE reimbursement (
                        reimbursement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        employee_id BIGINT,
                        amount DECIMAL(38,2) NOT NULL,
                        approved VARCHAR(50),
                        currency VARCHAR(10) NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        status BOOLEAN DEFAULT FALSE NOT NULL,
                        date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        date_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        CONSTRAINT FK_reimbursement_clientId FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE ON UPDATE CASCADE


) ENGINE InnoDB;
CREATE TABLE address (
                         address_id VARCHAR(100) PRIMARY KEY,
                         employee_id BIGINT AUTO_INCREMENT ,
                         street VARCHAR(100),
                         city VARCHAR(50),
                         province VARCHAR(50),
                         country VARCHAR(50),
                         postal_code VARCHAR(10),
                         CONSTRAINT FK_address_employeeId FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE ON UPDATE CASCADE
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
