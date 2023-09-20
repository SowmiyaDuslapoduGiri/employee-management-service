CREATE TABLE Employee
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid      UUID NOT NULL UNIQUE,
    email     VARCHAR(255),
    fullName  VARCHAR(255),
    firstName VARCHAR(255),
    lastName  VARCHAR(255),
    birthday  DATE,
    hobby     VARCHAR(255)
);
