CREATE DATABASE IF NOT EXISTS gpsystem;
USE gpsystem;

CREATE TABLE Users (
  UserID int NOT NULL AUTO_INCREMENT,
  Username varchar(50) DEFAULT NULL,
  Password varchar(255) DEFAULT NULL,
  UserType varchar(50) DEFAULT NULL,
  PRIMARY KEY (UserID),
  UNIQUE KEY Username (Username),
  CONSTRAINT CHK_UserType CHECK (UserType IN ('Doctor', 'Admin', 'Patient'))
);

CREATE TABLE Doctors (
  DoctorID INT NOT NULL AUTO_INCREMENT,
  FirstName VARCHAR(100) DEFAULT NULL,
  LastName VARCHAR(100) DEFAULT NULL,
  DOB date DEFAULT NULL,
  PhoneNumber VARCHAR(100) DEFAULT NULL,
  Specialisation VARCHAR(100) DEFAULT NULL,
  Gender VARCHAR(50) DEFAULT NULL,
  Email VARCHAR(100) DEFAULT NULL,
  Availability VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (DoctorID)
);



CREATE TABLE Patients (
  PatientID int NOT NULL AUTO_INCREMENT,
  FirstName varchar(255) DEFAULT NULL,
  LastName varchar(255) DEFAULT NULL,
  DOB date DEFAULT NULL,
  Gender varchar(50) DEFAULT NULL,
  Address varchar(255) DEFAULT NULL,
  PhoneNumber varchar(255) DEFAULT NULL,
  Email varchar(255) DEFAULT NULL,
  DoctorID int DEFAULT NULL,
  FOREIGN KEY (DoctorID) REFERENCES Doctors (DoctorID),
  PRIMARY KEY (PatientID)
);


CREATE TABLE ActivityLog (
  LogID int NOT NULL AUTO_INCREMENT,
  UserID int DEFAULT NULL,
  TimeAccessed datetime DEFAULT NULL,
  FeatureAccessed varchar(255) DEFAULT NULL,
  PRIMARY KEY (LogID),
  KEY UserID (UserID),
  CONSTRAINT ActivityLog_ibfk_1 FOREIGN KEY (UserID) REFERENCES Users (UserID)
);
CREATE TABLE Message (
  MessageId int NOT NULL AUTO_INCREMENT,
  UserID int NOT NULL,
  message text NOT NULL,
  IsRead tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (messageId),
  KEY UserID (UserID),
  CONSTRAINT Message_ibfk_1 FOREIGN KEY (UserID) REFERENCES Users (UserID)
);

CREATE TABLE Appointments (
  AppointmentID INT NOT NULL AUTO_INCREMENT,
  PatientID INT NOT NULL,
  DoctorID INT NOT NULL,
  AppointmentDate DATE NOT NULL,
  AppointmentTime TIME NOT NULL,
  PRIMARY KEY (AppointmentID),
  FOREIGN KEY (PatientID) REFERENCES Patients (PatientID),
  FOREIGN KEY (DoctorID) REFERENCES Doctors (DoctorID),
  CONSTRAINT UniqueAppointmentTime UNIQUE (DoctorID, AppointmentDate, AppointmentTime)
);

CREATE TABLE Admins (
  AdminID int NOT NULL AUTO_INCREMENT,
  FirstName varchar(255) DEFAULT NULL,
  LastName varchar(255) DEFAULT NULL,
  DOB date DEFAULT NULL,
  PhoneNumber varchar(255) DEFAULT NULL,
  Email varchar(255) DEFAULT NULL,
  PRIMARY KEY (AdminID)
);