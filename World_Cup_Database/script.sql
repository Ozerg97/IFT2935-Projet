begin transaction;

drop schema if exists coupeDuMonde CASCADE;
create schema coupeDuMonde; 
set search_path to coupeDuMonde;

create table coupeDuMonde(   
    annee int not null primary key,
    vainqueur varchar(50) not null
);

create table paysReceveur(    
    annee int not null,
    pays varchar(50) not null,
    foreign key(annee) references coupeDuMonde(annee),
    primary key(annee)

);

create table Equipe(     
	pays varchar(50) not null,
    annee int not null,
    foreign key (annee) references coupeDuMonde(annee),
    primary key (pays, annee)
);

create table Personne( 
    id int not null unique,
    nom varchar(50) not null,
    prenom varchar(50) not null,
    date_de_naissance date not null,
    sexe VARCHAR(1) CHECK (sexe IN ('H', 'F')),
    primary key(id)
);

create table Nationalite(   
    id int not null,
    nationalite varchar(50) not null,
    foreign key(id) references Personne(id),
    primary key(id)
);

create table Joueur(   
    id int not null unique,
    annee int not null,
    numero int not null,
    foreign key (id) references Personne(id),
    foreign key (annee) references CoupeDuMonde(annee),
    primary key(id, annee)
);

create table Staff(   
    id int not null unique,
    annee int not null,
    roles varchar(100) not null,
    primary key(id,annee),
    foreign key (id) references Personne(id),
    foreign key (annee) references CoupeDuMonde(annee)
);

create table Arbitre(  
    id int not null unique,
    fonction varchar(50) CHECK (fonction IN ('Arbitre principal', 'Arbitre assistant')),
    primary key(id),
    foreign key (id) references Personne(id)
);

create table Stade(
	nomStade varchar(100) not null ,
    ville varchar(100) not null,
    pays varchar(100) not null,
    capacite int not null,
    primary key(nomStade,ville, pays)
);

create table Match(
    idMatch int not null unique,
    annee int not null,
    scoreEquipe1 int not null,
    scoreEquipe2 int not null,
    matchDate date not null,    
    matchHeure time not null,
    rang varchar(50) not null,
    equipe1 varchar(50) not null,
    equipe2 varchar(50) not null,
    dureeMatch int not null, -- minutes
    prolongation int,
    penaltyEq1 int not null default 0,
    penaltyEq2 int not null default 0,
    nomStade varchar(100) not null,
    ville varchar(100) not null,
    pays varchar(100) not null,
    primary key(idMatch),
    foreign key (annee) references coupeDuMonde(annee),
    foreign key (equipe1, annee) references Equipe(pays, annee),
    foreign key (equipe2, annee) references Equipe(pays, annee),
    foreign key (nomStade, ville, pays) references Stade(nomStade, ville, pays)
);

create type Types as enum ('Jaune 1', 'Jaune 2', 'Rouge');

create table Carton(
    idMatch int not null,
    id_arbitre int not null,
    id_joueur int not null,
    type Types not null,
    primary key(idMatch, id_arbitre, id_joueur,type),
    foreign key (idMatch) references Match(idMatch),
    foreign key (id_arbitre) references Arbitre(id),
    foreign key (id_joueur) references Joueur(id)
);

create table Confrontation( 
    idMatch int not null,
    equipe1 varchar(50) not null,
    equipe2 varchar(50) not null,
    annee int not null,
    primary key(idMatch, equipe1, equipe2),
    foreign key(idMatch) references Match(idMatch),
    foreign key (equipe1, annee) references Equipe(pays, annee),
    foreign key (equipe2, annee) references Equipe(pays, annee)
);

create table Performance(
    id_joueur int not null,
    id_arbitre int not null,
    idMatch int not null,
    tempsDeJeu int not null default 0,
    nb_passes int not null default 0,
    assistances int not null default 0,
    buts int not null default 0,
    primary key(id_joueur, id_arbitre),
    foreign key (id_joueur) references Joueur(id),
    foreign key (idMatch) references Match(idMatch)
);
 
create table Gere(  
    idMatch int not null,
    id_Arbitre int not null,
    primary key(idMatch, id_Arbitre),
    foreign key (idMatch) references Match(idMatch),
    foreign key(id_Arbitre) references Arbitre(id)
);


---------------- INSERTION --------------------------

insert into CoupeDuMonde(annee, vainqueur) VALUES
(2014, 'Allemagne'),
(2018, 'France'),
(2022, 'Argentine'),
(2010, 'Espagne'),
(2006, 'Italie'),
(2002, 'Brésil'),
(1998, 'France'),
(1994, 'Brésil'),
(1990, 'Allemagne'),
(1986, 'Argentine'),
(1982, 'Italie'),
(1978, 'Argentine')
;

insert into paysReceveur(annee, pays) VALUES
(2014, 'Bresil'),
(2018, 'Russie'),
(2022, 'Qatar'),
(2010,'Afrique du Sud'),
(2006, 'Allemagne'),
(2002, 'Corée du Sud'),
(1998,'France'),
(1994,'Etats-Unis'),
(1990, 'Italie'),
(1986, 'Mexique'),
(1982, 'Espagne'),
(1978, 'Argentine')
;


insert into Personne(id, nom, prenom, date_de_naissance, sexe) VALUES
(1, 'Messi', 'Lionel', '1987-06-24','H'),
(2, 'Aguero', 'Sergio', '1988-06-02','H'),
(3, 'Di Maria', 'Angel', '1988-02-14','H'),
(4, 'Icardi', 'Mauro', '1993-02-19','H'),
(5, 'Otamendi', 'Nicolas', '1988-02-12','H'),
(6, 'Lo Celso', 'Giovani', '1996-04-09','H'),
(7, 'Paredes', 'Leandro', '1994-06-29','H'),
(8, 'Dybala', 'Paulo', '1993-11-15','H'),
(9, 'Acuna', 'Marcos', '1991-10-28','H'),
(10, 'Correa', 'Joachim', '1995-08-13','H'),
-- France
(11, 'Griezmann', 'Antoine', '1991-03-21','H'),
(12, 'Mbappe', 'Kylian', '1998-12-20','H'),
(13, 'Pogba', 'Paul', '1993-03-15','H'),
(14, 'Kante', 'NGolo', '1991-03-29','H'),
(15, 'Varane', 'Raphael', '1993-04-25','H'),
(16, 'Giroud', 'Olivier', '1986-09-30','H'),
(17, 'Lloris', 'Hugo', '1986-12-26','H'),
(18, 'Coman', 'Kingsley', '1996-06-13','H'),
(19, 'Fekir', 'Nabil', '1993-07-18','H'),
(20, 'Dembele', 'Ousmane', '1997-05-15','H'),
-- Bresil
(21, 'Neymar', 'Junior', '1992-02-05','H'),
(22, 'Firmino', 'Roberto', '1991-10-02','H'),
(23, 'Alves', 'Daniel', '1983-05-06','H'),
(24, 'Jesus', 'Gabriel', '1997-04-03','H'),
(25, 'Marcelo', 'Vieira', '1988-05-12','H'),
(26, 'Fred', 'Fredinho', '1993-03-05','H'),
(27, 'Fabinho', 'Pereira', '1993-10-23','H'),
(28, 'Casemiro', 'Santos', '1992-02-23','H'),
(29, 'Allan', 'Goes', '1991-01-08','H'),
(30, 'Ederson', 'Moares', '1993-08-17','H'),
-- Allemagne
(31, 'Neuer', 'Manuel', '1986-03-27','H'),
(32, 'Kroos', 'Toni', '1990-01-04','H'),
(33, 'Gnabry', 'Serge', '1995-07-14','H'),
(34, 'Goretzka', 'Leon', '1995-02-06','H'),
(35, 'Havertz', 'Kai', '1999-06-11','H'),
(36, 'Kimmich', 'Joshua', '1995-02-08','H'),
(37, 'Gundogan', 'Ilkay', '1990-10-24','H'),
(38, 'Tah', 'Jonathan', '1996-02-11','H'),
(39, 'Brandt', 'Julian', '1996-05-02','H'),
(40, 'Sane', 'Leroy', '1996-01-11','H'),
-- Belgique
(41, 'De Bruyne', 'Kevin', '1991-06-28','H'),
(42, 'Lukaku', 'Romelu', '1993-05-13','H'),
(43, 'Hazard', 'Eden', '1991-01-07','H'),
(44, 'Mertens', 'Dries', '1987-05-06','H'),
(45, 'Carrasco', 'Yannick', '1993-09-04','H'),
(46, 'Tielemans', 'Youri', '1997-05-07','H'),
(47, 'Witsel', 'Axel', '1989-01-12','H'),
(48, 'Courtois', 'Thibaut', '1992-05-11','H'),
(49, 'Alderweireld', 'Toby', '1989-03-02','H'),
(50, 'Dendoncker', 'Leander', '1995-04-15','H'),
-- Maroc
(51, 'Ziyech', 'Hakim', '1993-03-19','H'),
(52, 'Haddadi', 'Oussama', '1991-01-28','H'),
(53, 'Boussoufa', 'Mbark', '1984-08-15','H'),
(54, 'El Ahmadi', 'Karim', '1985-01-27','H'),
(55, 'Dirar', 'Nabil', '1985-02-25','H'),
(56, 'Ait Bennasser', 'Youssef', '1996-07-07','H'),
(57, 'Benatia', 'Mehdi', '1987-04-17','H'),
(58, 'Hakimi', 'Achraf', '1998-11-04','H'),
(59, 'Fajr', 'Faycal', '1988-08-01','H'),
(60, 'Amrabat', 'Nordin', '1987-03-31','H'),
-- Japon
(61, 'Kagawa', 'Shinji', '1989-03-17','H'),
(62, 'Okazaki', 'Shinji', '1986-04-16','H'),
(63, 'Inui', 'Takashi', '1988-06-02','H'),
(64, 'Honda', 'Keisuke', '1986-06-13','H'),
(65, 'Kubo', 'Takefusa', '2001-06-04','H'),
(66, 'Kamada', 'Daichi', '1996-08-05','H'),
(67, 'Ueda', 'Tomoki', '1996-10-24','H'),
(68, 'Harakawa', 'Kensuke', '1997-01-19','H'),
(69, 'Nakajima', 'Shoya', '1994-08-23','H'),
(70, 'Endo', 'Wataru', '1993-02-09','H'),
-- Canada
(71, 'Davies', 'Alphonso', '2000-11-02','H'),
(72, 'Hoilett', 'Junior', '1990-06-05','H'),
(73, 'David', 'Jonathan', '2000-01-14','H'),
(74, 'Osorio', 'Jonathan', '1992-06-12','H'),
(75, 'Cornelius', 'Derek', '1998-11-28','H'),
(76, 'Arfield', 'Scott', '1988-11-01','H'),
(77, 'Vitoria', 'Steven', '1987-01-12','H'),
(78, 'Henry', 'Kamal', '1993-11-24','H'),
(79, 'Akindele', 'Tesho', '1992-03-31','H'),
(80, 'Borjan', 'Milan', '1987-10-23','H'),
-- Espagne
(81, 'Ramos', 'Sergio', '1986-03-30','H'),
(82, 'Iniesta', 'Andres', '1984-05-11','H'),
(83, 'Isco', 'Alarcon', '1992-04-21','H'),
(84, 'Carvajal', 'Dani', '1992-01-11','H'),
(85, 'Thiago', 'Alcantara', '1991-04-11','H'),
(86, 'Busquets', 'Sergio', '1988-07-16','H'),
(87, 'Alba', 'Jordi', '1989-03-21','H'),
(88, 'Aspas', 'Iago', '1987-08-01','H'),
(89, 'Koke', 'Alarcon', '1992-01-08','H'),
(90, 'Rodri', 'Santa', '1996-06-22','H'),
-- Senegal
(91, 'Mane', 'Sadio', '1992-04-10','H'),
(92, 'Koulibaly', 'Kalidou', '1991-06-20','H'),
(93, 'Gueye', 'Idrissa', '1989-09-26','H'),
(94, 'Ndiaye', 'Badou', '1990-10-27','H'),
(95, 'Sarr', 'Ismaila', '1998-02-25','H'),
(96, 'Niang', 'Mbaye', '1994-12-19','H'),
(97, 'Wague', 'Moussa', '1998-10-04','H'),
(98, 'Konate', 'Salif', '1990-08-25','H'),
(99, 'Diagne', 'Mbaye', '1991-10-28','H'),
(100, 'Cisse', 'Aliou', '1976-03-24','H'),


-- Argentine
(101, 'Sampaoli', 'Jorge', '1960-03-13','H'),
(102, 'Pellegrino', 'Mauricio', '1971-10-05','H'),
-- France
(103, 'Deschamps', 'Didier', '1968-10-15','H'),
(104, 'Henry', 'Thierry', '1977-08-17','H'),
-- Bresil
(105, 'Tite', 'Vilanova', '1961-05-25','H'),
(106, 'Sylvinho', 'Allan', '1974-06-12','H'),
-- Allemagne
(107, 'Low', 'Joachim', '1960-02-03','H'),
(108, 'Flick', 'Hans-Dieter', '1965-02-24','H'),
-- Belgique
(109, 'Martinez', 'Roberto', '1973-07-13','H'),
(110, 'Hazard', 'Thorgan', '1993-03-29','H'),
-- Maroc
(111, 'Renard', 'Herve', '1968-09-30','H'),
(112, 'Zaki', 'Mustapha', '1959-09-01','H'),
-- Japon
(113, 'Moriyasu', 'Hajime', '1968-08-23','H'),
(114, 'Inagaki', 'Masashi', '1972-01-05','H'),
-- Canada
(115, 'Herdman', 'John', '1975-07-19','H'),
(116, 'Glover', 'Mauro', '1987-09-08','H'),
-- Espagne
(117, 'Enrique', 'Luis', '1970-05-08','H'),
(118, 'Hierro', 'Fernando', '1968-03-23','H'),
-- Senegal
(119, 'Cisse', 'Aliou', '1976-03-24','H'),
(120, 'Diouf', 'El Hadji', '1981-01-15','H'),


-- Arbitres
(121, 'Garcia', 'Luis', '1980-05-20','H'),
(122, 'Perez', 'Maria', '1975-02-10','H'),
(123, 'Muller', 'Hans', '1984-09-04','H'),
(124, 'Schneider', 'Franz', '1978-11-12','H'),
(125, 'Rossi', 'Giovanna', '1990-03-25','H'),
(126, 'Bianchi', 'Chiara', '1988-07-07','H'),
(127, 'Lapez', 'Juan', '1972-01-15','H'),
(128, 'Hernandez', 'Sofia', '1995-10-28','H'),
(129, 'Gonzalez', 'Carlos', '1982-12-31','H'),
(130, 'Alvarez', 'Ana', '1993-06-18','H'),
(131, 'Nguyen', 'Hien', '1977-04-02','H'),
(132, 'Le', 'Bao', '1986-08-22','H'),
(133, 'Kim', 'Min-Jae', '1992-12-05','H'),
(134, 'Wang', 'Wei', '1979-03-08','H'),
(135, 'Tsunoda', 'Yuki', '1985-09-14','H')
;




insert into Joueur(id, annee, numero) VALUES
(1, 2014, 1),
(2, 2018, 2),
(3, 2022, 3),
(4, 2014, 3),
(5, 2018, 4),
(6, 2022, 5),
(7, 2014, 5),
(8, 2018, 6),
(9, 2022, 7),
(10, 2014, 7),
(11, 2018, 8),
(12, 2022, 9),
(13, 2014, 9),
(14, 2018, 10),
(15, 2022, 11),
(16, 2014, 11),
(17, 2018, 12),
(18, 2022, 13),
(19, 2014, 13),
(20, 2018, 14),
(21, 2022, 15),
(22, 2014, 15),
(23, 2018, 16),
(24, 2022, 17),
(25, 2014, 17),
(26, 2018, 18),
(27, 2022, 19),
(28, 2014, 19),
(29, 2018, 20),
(30, 2022, 21),
(31, 2014, 21),
(32, 2018, 22),
(33, 2022, 23),
(34, 2014, 23),
(35, 2018, 24),
(36, 2022, 25),
(37, 2014, 25),
(38, 2018, 26),
(39, 2022, 27),
(40, 2014, 27),
(41, 2018, 28),
(42, 2022, 29),
(43, 2014, 29),
(44, 2018, 30),
(45, 2022, 31),
(46, 2014, 31),
(47, 2018, 32),
(48, 2022, 33),
(49, 2014, 33),
(50, 2018, 34),
(51, 2022, 35),
(52, 2014, 35),
(53, 2018, 36),
(54, 2022, 37),
(55, 2014, 37),
(56, 2018, 38),
(57, 2022, 39),
(58, 2014, 39),
(59, 2018, 40),
(60, 2022, 41),
(61, 2014, 41),
(62, 2018, 42),
(63, 2022, 43),
(64, 2014, 43),
(65, 2018, 44),
(66, 2022, 45),
(67, 2014, 45),
(68, 2018, 46),
(69, 2022, 47),
(70, 2014, 47),
(71, 2018, 48),
(72, 2022, 49),
(73, 2014, 49),
(74, 2018, 50),
(75, 2022, 51),
(76, 2014, 51),
(77, 2018, 52),
(78, 2022, 53),
(79, 2014, 53),
(80, 2018, 54),
(81, 2022, 55),
(82, 2014, 55),
(83, 2018, 56),
(84, 2022, 57),
(85, 2014, 57),
(86, 2018, 58),
(87, 2022, 59),
(88, 2014, 59),
(89, 2018, 60),
(90, 2022, 61),
(91, 2014, 61),
(92, 2018, 62),
(93, 2022, 63),
(94, 2014, 63),
(95, 2018, 64),
(96, 2022, 65),
(97, 2014, 65),
(98, 2018, 66),
(99, 2022, 67),
(100, 2022, 69)
;

insert into Equipe(pays, annee) VALUES
  ('Argentine', 2014),
  ('Argentine', 2018),
  ('Argentine', 2022),
  ('France', 2014),
  ('France', 2018),
  ('France', 2022),
  ('Bresil', 2014),
  ('Bresil', 2018),
  ('Bresil', 2022),
  ('Allemagne', 2014),
  ('Allemagne', 2018),
  ('Allemagne', 2022),
  ('Belgique', 2014),
  ('Belgique', 2018),
  ('Belgique', 2022),
  ('Maroc', 2014),
  ('Maroc', 2018),
  ('Maroc', 2022),
  ('Japon', 2014),
  ('Japon', 2018),
  ('Japon', 2022),
  ('Canada', 2014),
  ('Canada', 2018),
  ('Canada', 2022),
  ('Espagne', 2014),
  ('Espagne', 2018),
  ('Espagne', 2022),
  ('Senegal', 2014),
  ('Senegal', 2018),
  ('Senegal', 2022)
;

insert into Nationalite(id, nationalite) VALUES
(1, 'Argentin'),
(2, 'Argentin'),
(3, 'Argentin'),
(4, 'Argentin'),
(5, 'Argentin'),
(6, 'Argentin'),
(7, 'Argentin'),
(8, 'Argentin'),
(9, 'Argentin'),
(10, 'Argentin'),
(11, 'Francais'),
(12, 'Francais'),
(13, 'Francais'),
(14, 'Francais'),
(15, 'Francais'),
(16, 'Francais'),
(17, 'Francais'),
(18, 'Francais'),
(19, 'Francais'),
(20, 'Francais'),
(21, 'Bresilien'),
(22, 'Bresilien'),
(23, 'Bresilien'),
(24, 'Bresilien'),
(25, 'Bresilien'),
(26, 'Bresilien'),
(27, 'Bresilien'),
(28, 'Bresilien'),
(29, 'Bresilien'),
(30, 'Bresilien'),
(31, 'Allemand'),
(32, 'Allemand'),
(33, 'Allemand'),
(34, 'Allemand'),
(35, 'Allemand'),
(36, 'Allemand'),
(37, 'Allemand'),
(38, 'Allemand'),
(39, 'Allemand'),
(40, 'Allemand'),
(41, 'Belge'),
(42, 'Belge'),
(43, 'Francais'),
(44, 'Belge'),
(45, 'Belge'),
(46, 'Belge'),
(47, 'Belge'),
(48, 'Belge'),
(49, 'Belge'),
(50, 'Belge'),
(51, 'Marocain'),
(52, 'Marocain'),
(53, 'Francais'),
(54, 'Marocain'),
(55, 'Marocain'),
(56, 'Marocain'),
(57, 'Marocain'),
(58, 'Marocain'),
(59, 'Marocain'),
(60, 'Marocain'),
(61, 'Japonais'),
(62, 'Japonais'),
(63, 'Japonais'),
(64, 'Japonais'),
(65, 'Japonais'),
(66, 'Japonais'),
(67, 'Japonais'),
(68, 'Japonais'),
(69, 'Japonais'),
(70, 'Japonais'),
(71, 'Canadien'),
(72, 'Canadien'),
(73, 'Marocain'),
(74, 'Canadien'),
(75, 'Canadien'),
(76, 'Canadien'),
(77, 'Canadien'),
(78, 'Canadien'),
(79, 'Canadien'),
(80, 'Canadien'),
(81, 'Espagnol'),
(82, 'Espagnol'),
(83, 'Argentin'),
(84, 'Espagnol'),
(85, 'Espagnol'),
(86, 'Espagnol'),
(87, 'Espagnol'),
(88, 'Espagnol'),
(89, 'Espagnol'),
(90, 'Espagnol'),
(91, 'Senegalais'),
(92, 'Senegalais'),
(93, 'Senegalais'),
(94, 'Senegalais'),
(95, 'Senegalais'),
(96, 'Senegalais'),
(97, 'Senegalais'),
(98, 'Senegalais'),
(99, 'Senegalais'),
(100, 'Senegalais'),

(101, 'Argentin'),
(102, 'Argentin'),
(103, 'Francais'),
(104, 'Francais'),
(105, 'Portugais'),
(106, 'Bresil'),
(107, 'Allemand'),
(108, 'Allemand'),
(109, 'Espagnol'),
(110, 'Belge'),
(111, 'Francais'),
(112, 'Marocain'),
(113, 'Japonais'),
(114, 'Japonais'),
(115, 'Canadien'),
(116, 'Canadien'),
(117, 'Espagnol'),
(118, 'Espagnol'),
(119, 'Senegalais'),
(120, 'Senegalais'),

(121, 'Espagnol'),
(122, 'Colombien'),
(123, 'Allemand'),
(124, 'Neerlandais'),
(125, 'Italien'),
(126, 'Italien'),
(127, 'Espagnol'),
(128, 'Uruguayen'),
(129, 'Peruvien'),
(130, 'Vietnamien'),
(131, 'Chinois'),
(132, 'Coreen'),
(133, 'Chinois'),
(134, 'Japonais'),
(135, 'Chinois')
;


insert into Staff(id, annee, roles) VALUES
(101 , 2014, 'Entraineur principal'),
(102 , 2014, 'Entraineur adjoint'),
(103 , 2014, 'Entraineur principal'),
(104 , 2014, 'Entraineur adjoint'),
(105 , 2014, 'Entraineur principal'),
(106 , 2014, 'Entraineur adjoint'),
(107 , 2014, 'Entraineur principal'),
(108 , 2014, 'Entraineur adjoint'),
(109 , 2014, 'Entraineur principal'),
(110 , 2014, 'Entraineur adjoint'),
(111 , 2014, 'Entraineur principal'),
(112 , 2014, 'Entraineur adjoint'),
(113 , 2014, 'Entraineur principal'),
(114 , 2014, 'Entraineur adjoint'),
(115 , 2014, 'Entraineur principal'),
(116 , 2014, 'Entraineur adjoint'),
(117 , 2014, 'Entraineur principal'),
(118 , 2014, 'Entraineur adjoint'),
(119 , 2014, 'Entraineur principal'),
(120 , 2014, 'Entraineur adjoint')
;

insert into Arbitre(id, fonction) VALUES
(121 ,'Arbitre principal'),
(122 ,'Arbitre assistant'),
(123 ,'Arbitre assistant'),
(124 ,'Arbitre assistant'),
(125 ,'Arbitre principal'),
(126 ,'Arbitre assistant'),
(127 ,'Arbitre assistant'),
(128 ,'Arbitre assistant'),
(129 ,'Arbitre principal'),
(130 ,'Arbitre assistant'),
(131 ,'Arbitre assistant'),
(132 ,'Arbitre assistant'),
(133 ,'Arbitre principal'),
(134 ,'Arbitre assistant'),
(135, 'Arbitre assistant')
;


INSERT INTO Stade(nomStade, ville, pays, capacite) VALUES
  ('Estadio do Maracana', 'Rio de Janeiro', 'Bresil', 78838),
  ('Arena Corinthians', 'Sao Paulo', 'Bresil', 48999),
  ('Mineirao', 'Belo Horizonte', 'Bresil', 58000),
  ('Arena de Sao Paulo', 'Sao Paulo', 'Bresil', 65000),
  ('Estadio Nacional Mane Garrincha', 'Brasilia', 'Bresil', 72000),
 
  ('Luzhniki Stadium', 'Moscou', 'Russie', 81000),
  ('Krestovsky Stadium', 'Saint-Petersbourg', 'Russie', 68134),
  ('Fisht Olympic Stadium', 'Sotchi', 'Russie', 47659),
  ('Ekaterinburg Arena', 'Ekaterinburg', 'Russie', 35696),
  ('Rostov Arena', 'Rostov-sur-le-Don', 'Russie', 45145),
 
  ('Lusail Iconic Stadium', 'Lusail', 'Qatar', 80000),
  ('Al-Wakrah Stadium', 'Al-Wakrah', 'Qatar', 45000),
  ('Education City Stadium', 'Al-Rayyan', 'Qatar', 40000),
  ('Al-Bayt Stadium', 'Al-Khor', 'Qatar', 60000),
  ('Ras Abu Aboud Stadium', 'Doha', 'Qatar', 40000)
;

INSERT INTO Match(idMatch, annee, scoreEquipe1, scoreEquipe2, matchDate, matchHeure, rang, equipe1, equipe2, dureeMatch, prolongation, penaltyEq1, penaltyEq2, nomStade, ville, pays)
VALUES
(136, 2014, 2, 1, '2014-06-12', '17:00:00', 'Phase de poule', 'France', 'Maroc', 92, NULL, 1, 0, 'Estadio do Maracana', 'Rio de Janeiro', 'Bresil'),
(137, 2014, 3, 1, '2014-06-13', '13:00:00', 'Phase de poule', 'Espagne', 'Canada', 94, NULL, 0, 0, 'Arena de Sao Paulo', 'Sao Paulo', 'Bresil'),
(138, 2014, 2, 2, '2014-06-17', '16:00:00', 'Quarts de finale', 'Bresil', 'Senegal', 93, 31, 1, 1, 'Mineirao', 'Belo Horizonte', 'Bresil'),
(139, 2014, 1, 0, '2014-06-20', '16:00:00', 'Demi-finale', 'Japon', 'France', 91, NULL, 0, 0, 'Arena Corinthians', 'Sao Paulo', 'Bresil'),
(140, 2014, 1, 0, '2014-06-24', '13:00:00', 'Finale', 'Allemagne', 'Argentine', 97, NULL, 0, 0, 'Estadio do Maracana', 'Rio de Janeiro', 'Bresil'),
(141, 2018, 2, 1, '2018-06-15', '18:00:00', 'Phase de poule', 'France', 'Espagne', 94, NULL, 0, 0, 'Rostov Arena', 'Rostov-sur-le-Don', 'Russie'),
(142, 2018, 0, 1, '2018-06-17', '21:00:00', 'Phase de poule', 'Allemagne', 'Belgique', 92, NULL, 0, 0, 'Ekaterinburg Arena', 'Ekaterinburg', 'Russie'),
(143, 2018, 3, 0, '2018-06-23', '15:00:00', 'Quarts de finale', 'Japon', 'Maroc', 90, NULL, 0, 0, 'Fisht Olympic Stadium', 'Sotchi', 'Russie'),
(144, 2018, 1, 0, '2018-06-26', '17:00:00', 'Demi-finale', 'Argentine', 'Bresil', 96, NULL, 0, 0, 'Krestovsky Stadium', 'Saint-Petersbourg', 'Russie'),
(145, 2018, 4, 3, '2018-07-06', '17:00:00', 'Finale', 'Allemagne', 'France', 94, NULL, 0, 0, 'Luzhniki Stadium', 'Moscou', 'Russie'),
(146, 2022, 2, 1, '2022-11-21', '16:00:00', 'Phase de poule', 'Espagne', 'Argentine', 99, NULL, 0, 0, 'Al-Wakrah Stadium', 'Al-Wakrah', 'Qatar'),
(147, 2022, 2, 2, '2022-11-26', '18:00:00', 'Phase de poule', 'Bresil', 'Japon', 112, NULL, 0, 0, 'Al-Bayt Stadium', 'Al-Khor', 'Qatar'),
(148, 2022, 1, 1, '2022-12-02', '15:00:00', 'Quarts de finale', 'Senegal', 'Espagne', 104, NULL, 0, 0, 'Education City Stadium', 'Al-Rayyan', 'Qatar'),
(149, 2022, 1, 0, '2022-12-08', '15:00:00', 'Demi-finale', 'Maroc', 'Canada', 99, NULL, 0, 0, 'Ras Abu Aboud Stadium', 'Doha', 'Qatar'),
(150, 2022, 2, 3, '2022-12-13', '18:00:00', 'Finale', 'France', 'Argentine', 96, 35, 2, 1, 'Lusail Iconic Stadium', 'Lusail', 'Qatar')
;

INSERT INTO Confrontation(idMatch, equipe1, equipe2, annee)
VALUES
(136, 'France', 'Maroc', 2014),
(137, 'Espagne', 'Canada', 2014),
(138, 'Bresil', 'Senegal',2014),
(139, 'Japon', 'France',2018),
(140, 'Allemagne', 'Argentine',2018),
(141, 'France', 'Espagne',2018),
(142, 'Allemagne', 'Belgique',2022),
(143, 'Japon', 'Maroc',2022),
(144, 'Argentine', 'Bresil',2022),
(145, 'Allemagne', 'France',2022),
(146, 'Espagne', 'Argentine',2018),
(147, 'Bresil', 'Japon',2014),
(148, 'Senegal', 'Espagne',2018),
(149, 'Maroc', 'Canada',2014),
(150, 'France', 'Argentine',2022)
;
 
insert into Gere(idMatch, id_Arbitre) VALUES
(136, 121),
(136, 122),
(136, 123),
(136, 124),

(137, 125),
(137, 126),
(137, 127),
(137, 128),

(138, 129),
(138, 130),
(138, 131),
(138, 132),

(139, 133),
(139, 134),
(139, 135),
(139, 122),

(140, 121),
(140, 123),
(140, 124),
(140, 126),

(141, 125),
(141, 127),
(141, 128),
(141, 130),

(142, 129),
(142, 131),
(142, 132),
(142, 134),

(143, 133),
(143, 135),
(143, 122),
(143, 123),

(144, 121),
(144, 124),
(144, 126),
(144, 127),

(145, 125),
(145, 128),
(145, 130),
(145, 131),

(146, 129),
(146, 132),
(146, 134),
(146, 135),

(147, 121),
(147, 122),
(147, 123),
(147, 124),

(148, 125),
(148, 126),
(148, 127),
(148, 128),

(149, 129),
(149, 130),
(149, 131),
(149, 132),

(150, 133),
(150, 134),
(150, 135),
(150, 122)
;


insert into Carton(idMatch, id_arbitre, id_joueur, type) VALUES
(136, 121, 52, 'Jaune 1'),
(137, 125, 73, 'Jaune 1'),
(138, 129, 25, 'Jaune 1'),
(139, 133, 61, 'Rouge'),
(140, 121, 34, 'Jaune 1'),
(141, 125, 14, 'Jaune 1'),
(141, 125, 14, 'Jaune 2'),
(141, 125, 14, 'Rouge'),
(142, 129, 44, 'Jaune 1'),
(143, 133, 56, 'Jaune 1'),
(144, 121, 5, 'Jaune 1'),
(144, 121, 5, 'Jaune 2'),
(144, 121, 5, 'Rouge'),
(145, 125, 35, 'Jaune 1'),
(146, 129, 84, 'Jaune 1'),
(147, 121, 24, 'Jaune 1'),
(148, 125, 99, 'Jaune 1'),
(149, 129, 78, 'Rouge'),
(150, 133, 9, 'Jaune 1')
;

INSERT INTO Performance(id_joueur, id_arbitre, idMatch, tempsDeJeu, nb_passes, assistances, buts)
VALUES
    (52, 121, 136, 90, 43, 1, 1),
    (73, 125, 137, 90, 38, 0, 0),
    (25, 129, 138, 45, 27, 2, 0),
    (61, 133, 139, 45, 22, 0, 1),
    (34, 121, 140, 90, 61, 0, 1),
    (14, 125, 141, 90, 55, 1, 1),
    (44, 129, 142, 45, 23, 1, 0),
    (56, 133, 143, 45, 26, 0, 0),
    (5, 121, 144, 90, 59, 0, 1),
    (35, 125, 145, 90, 56, 2, 2),
    (84, 129, 146, 45, 23, 1, 0),
    (24, 121, 147, 45, 30, 2, 0),
    (99, 125, 148, 90, 69, 0, 0),
    (78, 129, 149, 90, 61, 0, 1),
    (9, 133, 150, 45, 32, 0, 2)
;

-------------------- END INSERTION --------------------------

-------------------- REQUESTS ----------------------------




------------------- END REQUESTS -----------------------------



commit;