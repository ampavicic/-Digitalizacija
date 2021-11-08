CREATE TABLE Dokument
(
  arhiviran INT NOT NULL,
  id_dokument VARCHAR(10) NOT NULL,
  potpis INT NOT NULL,
  PRIMARY KEY (id_dokument)
);

CREATE TABLE Ponuda
(
  P9 VARCHAR(10) NOT NULL,
  id_dokument VARCHAR(10) NOT NULL,
  PRIMARY KEY (id_dokument),
  FOREIGN KEY (id_dokument) REFERENCES Dokument(id_dokument)
);

CREATE TABLE Racun
(
  R6 VARCHAR(7) NOT NULL,
  id_dokument VARCHAR(10) NOT NULL,
  PRIMARY KEY (id_dokument),
  FOREIGN KEY (id_dokument) REFERENCES Dokument(id_dokument)
);

CREATE TABLE Interni
(
  INT4 VARCHAR(7) NOT NULL,
  id_dokument VARCHAR(10) NOT NULL,
  PRIMARY KEY (id_dokument),
  FOREIGN KEY (id_dokument) REFERENCES Dokument(id_dokument)
);

CREATE TABLE Osoba
(
  id_osoba VARCHAR(10) NOT NULL,
  mail VARCHAR(20) NOT NULL,
  prezime VARCHAR(20) NOT NULL,
  ime VARCHAR(20) NOT NULL,
  password VARCHAR(20) NOT NULL,
  id_dokument VARCHAR(10) NOT NULL,
  PRIMARY KEY (id_osoba),
  FOREIGN KEY (id_dokument) REFERENCES Dokument(id_dokument)
);

CREATE TABLE Uloga
(
  id_uloga VARCHAR(2) NOT NULL,
  naziv VARCHAR(20) NOT NULL,
  id_osoba VARCHAR(10) NOT NULL,
  PRIMARY KEY (id_uloga),
  FOREIGN KEY (id_osoba) REFERENCES Osoba(id_osoba)
);