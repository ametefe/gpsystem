-- Adds the Users to the Users table so thatwe can login to the system and test the message screens.
-- UserID 1
INSERT INTO Users(Username, Password, UserType)
VALUES('sa2090', 'Password1', 'Admin');

INSERT INTO Users(Username, Password, UserType)
VALUES('ms2', 'Password1', 'Doctor');

INSERT INTO Users(Username, Password, UserType)
VALUES('tp3', 'Password1', 'Doctor');

INSERT INTO Users(Username, Password, UserType)
VALUES('jd4', 'Password1', 'Patient');


-- Adding Doctors into the doctors table
INSERT INTO Doctors (DoctorID,FirstName, LastName, dob, PhoneNumber,Specialisation,Gender,Email,Availability)
VALUES (2,'Martin', 'Smith', '1980-01-01', '03453020532','General','Male','MartinSmith@gp.com','[Monday,Tuesday,Wednesday]');

INSERT INTO Doctors (DoctorID,FirstName, LastName, dob, PhoneNumber,Specialisation,Gender,Email,Availability)
VALUES (3,'Taylor', 'Paddington', '1987-03-01', '042864200','General','Female','TaylorPaddingTon@gp.com','[Monday,Wednesday]');

-- Adding Patients into the Patients table

INSERT INTO Patients (PatientID, FirstName, LastName,DOB, Gender, Address ,PhoneNumber, Email)
VALUES (4,'John', 'Davies','1980-01-01','Male','12 High Street','03453020532','maigmwaiog@gmail.com');

-- Adding admin to the admin table
INSERT INTO Admins (AdminID, FirstName, LastName, DOB, PhoneNumber)
VALUES (1,'Samuel', 'Ametefe','2003-01-01','03453020532');
