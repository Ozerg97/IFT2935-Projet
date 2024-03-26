/**
 * auteurs:Reda Laalej (20041208)
 *         Gaby Nguyen (20188858)
 *         Wen Yin (20179082)
 *
 * nom de projet: Coupe de monde
 *
 * Date: 26-04-2022
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.sql.*;

public class GestionCoupeDuMonde extends JFrame implements ActionListener{

    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "OZ878432**##";

    private Connection conn = connect();

    //Création des tables sql
    private String createSQL =
    		
    		"begin transaction;\n"
    		+ "\n"
    		+ "drop schema if exists coupeDuMonde CASCADE;\n"
    		+ "create schema coupeDuMonde; \n"
    		+ "set search_path to coupeDuMonde;\n"
    		+ "\n"
    		+ "create table coupeDuMonde(   \n"
    		+ "    annee int not null primary key,\n"
    		+ "    vainqueur varchar(50) not null\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table paysReceveur(    \n"
    		+ "    annee int not null,\n"
    		+ "    pays varchar(50) not null,\n"
    		+ "    foreign key(annee) references coupeDuMonde(annee),\n"
    		+ "    primary key(annee)\n"
    		+ "\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Equipe(     \n"
    		+ "	pays varchar(50) not null,\n"
    		+ "    annee int not null,\n"
    		+ "    foreign key (annee) references coupeDuMonde(annee),\n"
    		+ "    primary key (pays, annee)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Personne( \n"
    		+ "    id int not null unique,\n"
    		+ "    nom varchar(50) not null,\n"
    		+ "    prenom varchar(50) not null,\n"
    		+ "    date_de_naissance date not null,\n"
    		+ "    sexe VARCHAR(1) CHECK (sexe IN ('H', 'F')),\n"
    		+ "    primary key(id)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Nationalite(   \n"
    		+ "    id int not null,\n"
    		+ "    nationalite varchar(50) not null,\n"
    		+ "    foreign key(id) references Personne(id),\n"
    		+ "    primary key(id)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Joueur(   \n"
    		+ "    id int not null unique,\n"
    		+ "    annee int not null,\n"
    		+ "    numero int not null,\n"
    		+ "    foreign key (id) references Personne(id),\n"
    		+ "    foreign key (annee) references CoupeDuMonde(annee),\n"
    		+ "    primary key(id, annee)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Staff(   \n"
    		+ "    id int not null unique,\n"
    		+ "    annee int not null,\n"
    		+ "    roles varchar(100) not null,\n"
    		+ "    primary key(id,annee),\n"
    		+ "    foreign key (id) references Personne(id),\n"
    		+ "    foreign key (annee) references CoupeDuMonde(annee)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Arbitre(  \n"
    		+ "    id int not null unique,\n"
    		+ "    fonction varchar(50) CHECK (fonction IN ('Arbitre principal', 'Arbitre assistant')),\n"
    		+ "    primary key(id),\n"
    		+ "    foreign key (id) references Personne(id)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Stade(\n"
    		+ "	nomStade varchar(100) not null ,\n"
    		+ "    ville varchar(100) not null,\n"
    		+ "    pays varchar(100) not null,\n"
    		+ "    capacite int not null,\n"
    		+ "    primary key(nomStade,ville, pays)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Match(\n"
    		+ "    idMatch int not null unique,\n"
    		+ "    annee int not null,\n"
    		+ "    scoreEquipe1 int not null,\n"
    		+ "    scoreEquipe2 int not null,\n"
    		+ "    matchDate date not null,    \n"
    		+ "    matchHeure time not null,\n"
    		+ "    rang varchar(50) not null,\n"
    		+ "    equipe1 varchar(50) not null,\n"
    		+ "    equipe2 varchar(50) not null,\n"
    		+ "    dureeMatch int not null, -- minutes\n"
    		+ "    prolongation int,\n"
    		+ "    penaltyEq1 int not null default 0,\n"
    		+ "    penaltyEq2 int not null default 0,\n"
    		+ "    nomStade varchar(100) not null,\n"
    		+ "    ville varchar(100) not null,\n"
    		+ "    pays varchar(100) not null,\n"
    		+ "    primary key(idMatch),\n"
    		+ "    foreign key (annee) references coupeDuMonde(annee),\n"
    		+ "    foreign key (equipe1, annee) references Equipe(pays, annee),\n"
    		+ "    foreign key (equipe2, annee) references Equipe(pays, annee),\n"
    		+ "    foreign key (nomStade, ville, pays) references Stade(nomStade, ville, pays)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create type Types as enum ('Jaune 1', 'Jaune 2', 'Rouge');\n"
    		+ "\n"
    		+ "create table Carton(\n"
    		+ "    idMatch int not null,\n"
    		+ "    id_arbitre int not null,\n"
    		+ "    id_joueur int not null,\n"
    		+ "    type Types not null,\n"
    		+ "    primary key(idMatch, id_arbitre, id_joueur,type),\n"
    		+ "    foreign key (idMatch) references Match(idMatch),\n"
    		+ "    foreign key (id_arbitre) references Arbitre(id),\n"
    		+ "    foreign key (id_joueur) references Joueur(id)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Confrontation( \n"
    		+ "    idMatch int not null,\n"
    		+ "    equipe1 varchar(50) not null,\n"
    		+ "    equipe2 varchar(50) not null,\n"
    		+ "    annee int not null,\n"
    		+ "    primary key(idMatch, equipe1, equipe2),\n"
    		+ "    foreign key(idMatch) references Match(idMatch),\n"
    		+ "    foreign key (equipe1, annee) references Equipe(pays, annee),\n"
    		+ "    foreign key (equipe2, annee) references Equipe(pays, annee)\n"
    		+ ");\n"
    		+ "\n"
    		+ "create table Performance(\n"
    		+ "    id_joueur int not null,\n"
    		+ "    id_arbitre int not null,\n"
    		+ "    idMatch int not null,\n"
    		+ "    tempsDeJeu int not null default 0,\n"
    		+ "    nb_passes int not null default 0,\n"
    		+ "    assistances int not null default 0,\n"
    		+ "    buts int not null default 0,\n"
    		+ "    primary key(id_joueur, id_arbitre),\n"
    		+ "    foreign key (id_joueur) references Joueur(id),\n"
    		+ "    foreign key (idMatch) references Match(idMatch)\n"
    		+ ");\n"
    		+ " \n"
    		+ "create table Gere(  \n"
    		+ "    idMatch int not null,\n"
    		+ "    id_Arbitre int not null,\n"
    		+ "    primary key(idMatch, id_Arbitre),\n"
    		+ "    foreign key (idMatch) references Match(idMatch),\n"
    		+ "    foreign key(id_Arbitre) references Arbitre(id)\n"
    		+ ");\n"
    		+ "\n"
    		+ "\n";
    		 private String insertSQL =
    		"---------------- INSERTION --------------------------\n"
    		+ "\n"
    		+ "insert into CoupeDuMonde(annee, vainqueur) VALUES\n"
    		+ "(2014, 'Allemagne'),\n"
    		+ "(2018, 'France'),\n"
    		+ "(2022, 'Argentine'),\n"
    		+ "(2010, 'Espagne'),\n"
    		+ "(2006, 'Italie'),\n"
    		+ "(2002, 'Brésil'),\n"
    		+ "(1998, 'France'),\n"
    		+ "(1994, 'Brésil'),\n"
    		+ "(1990, 'Allemagne'),\n"
    		+ "(1986, 'Argentine'),\n"
    		+ "(1982, 'Italie'),\n"
    		+ "(1978, 'Argentine')\n"
    		+ ";\n"
    		+ "\n"
    		+ "insert into paysReceveur(annee, pays) VALUES\n"
    		+ "(2014, 'Bresil'),\n"
    		+ "(2018, 'Russie'),\n"
    		+ "(2022, 'Qatar'),\n"
    		+ "(2010,'Afrique du Sud'),\n"
    		+ "(2006, 'Allemagne'),\n"
    		+ "(2002, 'Corée du Sud'),\n"
    		+ "(1998,'France'),\n"
    		+ "(1994,'Etats-Unis'),\n"
    		+ "(1990, 'Italie'),\n"
    		+ "(1986, 'Mexique'),\n"
    		+ "(1982, 'Espagne'),\n"
    		+ "(1978, 'Argentine')\n"
    		+ ";\n"
    		+ "\n"
    		+ "\n"
    		+ "insert into Personne(id, nom, prenom, date_de_naissance, sexe) VALUES\n"
    		+ "(1, 'Messi', 'Lionel', '1987-06-24','H'),\n"
    		+ "(2, 'Aguero', 'Sergio', '1988-06-02','H'),\n"
    		+ "(3, 'Di Maria', 'Angel', '1988-02-14','H'),\n"
    		+ "(4, 'Icardi', 'Mauro', '1993-02-19','H'),\n"
    		+ "(5, 'Otamendi', 'Nicolas', '1988-02-12','H'),\n"
    		+ "(6, 'Lo Celso', 'Giovani', '1996-04-09','H'),\n"
    		+ "(7, 'Paredes', 'Leandro', '1994-06-29','H'),\n"
    		+ "(8, 'Dybala', 'Paulo', '1993-11-15','H'),\n"
    		+ "(9, 'Acuna', 'Marcos', '1991-10-28','H'),\n"
    		+ "(10, 'Correa', 'Joachim', '1995-08-13','H'),\n"
    		+ "-- France\n"
    		+ "(11, 'Griezmann', 'Antoine', '1991-03-21','H'),\n"
    		+ "(12, 'Mbappe', 'Kylian', '1998-12-20','H'),\n"
    		+ "(13, 'Pogba', 'Paul', '1993-03-15','H'),\n"
    		+ "(14, 'Kante', 'NGolo', '1991-03-29','H'),\n"
    		+ "(15, 'Varane', 'Raphael', '1993-04-25','H'),\n"
    		+ "(16, 'Giroud', 'Olivier', '1986-09-30','H'),\n"
    		+ "(17, 'Lloris', 'Hugo', '1986-12-26','H'),\n"
    		+ "(18, 'Coman', 'Kingsley', '1996-06-13','H'),\n"
    		+ "(19, 'Fekir', 'Nabil', '1993-07-18','H'),\n"
    		+ "(20, 'Dembele', 'Ousmane', '1997-05-15','H'),\n"
    		+ "-- Bresil\n"
    		+ "(21, 'Neymar', 'Junior', '1992-02-05','H'),\n"
    		+ "(22, 'Firmino', 'Roberto', '1991-10-02','H'),\n"
    		+ "(23, 'Alves', 'Daniel', '1983-05-06','H'),\n"
    		+ "(24, 'Jesus', 'Gabriel', '1997-04-03','H'),\n"
    		+ "(25, 'Marcelo', 'Vieira', '1988-05-12','H'),\n"
    		+ "(26, 'Fred', 'Fredinho', '1993-03-05','H'),\n"
    		+ "(27, 'Fabinho', 'Pereira', '1993-10-23','H'),\n"
    		+ "(28, 'Casemiro', 'Santos', '1992-02-23','H'),\n"
    		+ "(29, 'Allan', 'Goes', '1991-01-08','H'),\n"
    		+ "(30, 'Ederson', 'Moares', '1993-08-17','H'),\n"
    		+ "-- Allemagne\n"
    		+ "(31, 'Neuer', 'Manuel', '1986-03-27','H'),\n"
    		+ "(32, 'Kroos', 'Toni', '1990-01-04','H'),\n"
    		+ "(33, 'Gnabry', 'Serge', '1995-07-14','H'),\n"
    		+ "(34, 'Goretzka', 'Leon', '1995-02-06','H'),\n"
    		+ "(35, 'Havertz', 'Kai', '1999-06-11','H'),\n"
    		+ "(36, 'Kimmich', 'Joshua', '1995-02-08','H'),\n"
    		+ "(37, 'Gundogan', 'Ilkay', '1990-10-24','H'),\n"
    		+ "(38, 'Tah', 'Jonathan', '1996-02-11','H'),\n"
    		+ "(39, 'Brandt', 'Julian', '1996-05-02','H'),\n"
    		+ "(40, 'Sane', 'Leroy', '1996-01-11','H'),\n"
    		+ "-- Belgique\n"
    		+ "(41, 'De Bruyne', 'Kevin', '1991-06-28','H'),\n"
    		+ "(42, 'Lukaku', 'Romelu', '1993-05-13','H'),\n"
    		+ "(43, 'Hazard', 'Eden', '1991-01-07','H'),\n"
    		+ "(44, 'Mertens', 'Dries', '1987-05-06','H'),\n"
    		+ "(45, 'Carrasco', 'Yannick', '1993-09-04','H'),\n"
    		+ "(46, 'Tielemans', 'Youri', '1997-05-07','H'),\n"
    		+ "(47, 'Witsel', 'Axel', '1989-01-12','H'),\n"
    		+ "(48, 'Courtois', 'Thibaut', '1992-05-11','H'),\n"
    		+ "(49, 'Alderweireld', 'Toby', '1989-03-02','H'),\n"
    		+ "(50, 'Dendoncker', 'Leander', '1995-04-15','H'),\n"
    		+ "-- Maroc\n"
    		+ "(51, 'Ziyech', 'Hakim', '1993-03-19','H'),\n"
    		+ "(52, 'Haddadi', 'Oussama', '1991-01-28','H'),\n"
    		+ "(53, 'Boussoufa', 'Mbark', '1984-08-15','H'),\n"
    		+ "(54, 'El Ahmadi', 'Karim', '1985-01-27','H'),\n"
    		+ "(55, 'Dirar', 'Nabil', '1985-02-25','H'),\n"
    		+ "(56, 'Ait Bennasser', 'Youssef', '1996-07-07','H'),\n"
    		+ "(57, 'Benatia', 'Mehdi', '1987-04-17','H'),\n"
    		+ "(58, 'Hakimi', 'Achraf', '1998-11-04','H'),\n"
    		+ "(59, 'Fajr', 'Faycal', '1988-08-01','H'),\n"
    		+ "(60, 'Amrabat', 'Nordin', '1987-03-31','H'),\n"
    		+ "-- Japon\n"
    		+ "(61, 'Kagawa', 'Shinji', '1989-03-17','H'),\n"
    		+ "(62, 'Okazaki', 'Shinji', '1986-04-16','H'),\n"
    		+ "(63, 'Inui', 'Takashi', '1988-06-02','H'),\n"
    		+ "(64, 'Honda', 'Keisuke', '1986-06-13','H'),\n"
    		+ "(65, 'Kubo', 'Takefusa', '2001-06-04','H'),\n"
    		+ "(66, 'Kamada', 'Daichi', '1996-08-05','H'),\n"
    		+ "(67, 'Ueda', 'Tomoki', '1996-10-24','H'),\n"
    		+ "(68, 'Harakawa', 'Kensuke', '1997-01-19','H'),\n"
    		+ "(69, 'Nakajima', 'Shoya', '1994-08-23','H'),\n"
    		+ "(70, 'Endo', 'Wataru', '1993-02-09','H'),\n"
    		+ "-- Canada\n"
    		+ "(71, 'Davies', 'Alphonso', '2000-11-02','H'),\n"
    		+ "(72, 'Hoilett', 'Junior', '1990-06-05','H'),\n"
    		+ "(73, 'David', 'Jonathan', '2000-01-14','H'),\n"
    		+ "(74, 'Osorio', 'Jonathan', '1992-06-12','H'),\n"
    		+ "(75, 'Cornelius', 'Derek', '1998-11-28','H'),\n"
    		+ "(76, 'Arfield', 'Scott', '1988-11-01','H'),\n"
    		+ "(77, 'Vitoria', 'Steven', '1987-01-12','H'),\n"
    		+ "(78, 'Henry', 'Kamal', '1993-11-24','H'),\n"
    		+ "(79, 'Akindele', 'Tesho', '1992-03-31','H'),\n"
    		+ "(80, 'Borjan', 'Milan', '1987-10-23','H'),\n"
    		+ "-- Espagne\n"
    		+ "(81, 'Ramos', 'Sergio', '1986-03-30','H'),\n"
    		+ "(82, 'Iniesta', 'Andres', '1984-05-11','H'),\n"
    		+ "(83, 'Isco', 'Alarcon', '1992-04-21','H'),\n"
    		+ "(84, 'Carvajal', 'Dani', '1992-01-11','H'),\n"
    		+ "(85, 'Thiago', 'Alcantara', '1991-04-11','H'),\n"
    		+ "(86, 'Busquets', 'Sergio', '1988-07-16','H'),\n"
    		+ "(87, 'Alba', 'Jordi', '1989-03-21','H'),\n"
    		+ "(88, 'Aspas', 'Iago', '1987-08-01','H'),\n"
    		+ "(89, 'Koke', 'Alarcon', '1992-01-08','H'),\n"
    		+ "(90, 'Rodri', 'Santa', '1996-06-22','H'),\n"
    		+ "-- Senegal\n"
    		+ "(91, 'Mane', 'Sadio', '1992-04-10','H'),\n"
    		+ "(92, 'Koulibaly', 'Kalidou', '1991-06-20','H'),\n"
    		+ "(93, 'Gueye', 'Idrissa', '1989-09-26','H'),\n"
    		+ "(94, 'Ndiaye', 'Badou', '1990-10-27','H'),\n"
    		+ "(95, 'Sarr', 'Ismaila', '1998-02-25','H'),\n"
    		+ "(96, 'Niang', 'Mbaye', '1994-12-19','H'),\n"
    		+ "(97, 'Wague', 'Moussa', '1998-10-04','H'),\n"
    		+ "(98, 'Konate', 'Salif', '1990-08-25','H'),\n"
    		+ "(99, 'Diagne', 'Mbaye', '1991-10-28','H'),\n"
    		+ "(100, 'Cisse', 'Aliou', '1976-03-24','H'),\n"
    		+ "\n"
    		+ "\n"
    		+ "-- Argentine\n"
    		+ "(101, 'Sampaoli', 'Jorge', '1960-03-13','H'),\n"
    		+ "(102, 'Pellegrino', 'Mauricio', '1971-10-05','H'),\n"
    		+ "-- France\n"
    		+ "(103, 'Deschamps', 'Didier', '1968-10-15','H'),\n"
    		+ "(104, 'Henry', 'Thierry', '1977-08-17','H'),\n"
    		+ "-- Bresil\n"
    		+ "(105, 'Tite', 'Vilanova', '1961-05-25','H'),\n"
    		+ "(106, 'Sylvinho', 'Allan', '1974-06-12','H'),\n"
    		+ "-- Allemagne\n"
    		+ "(107, 'Low', 'Joachim', '1960-02-03','H'),\n"
    		+ "(108, 'Flick', 'Hans-Dieter', '1965-02-24','H'),\n"
    		+ "-- Belgique\n"
    		+ "(109, 'Martinez', 'Roberto', '1973-07-13','H'),\n"
    		+ "(110, 'Hazard', 'Thorgan', '1993-03-29','H'),\n"
    		+ "-- Maroc\n"
    		+ "(111, 'Renard', 'Herve', '1968-09-30','H'),\n"
    		+ "(112, 'Zaki', 'Mustapha', '1959-09-01','H'),\n"
    		+ "-- Japon\n"
    		+ "(113, 'Moriyasu', 'Hajime', '1968-08-23','H'),\n"
    		+ "(114, 'Inagaki', 'Masashi', '1972-01-05','H'),\n"
    		+ "-- Canada\n"
    		+ "(115, 'Herdman', 'John', '1975-07-19','H'),\n"
    		+ "(116, 'Glover', 'Mauro', '1987-09-08','H'),\n"
    		+ "-- Espagne\n"
    		+ "(117, 'Enrique', 'Luis', '1970-05-08','H'),\n"
    		+ "(118, 'Hierro', 'Fernando', '1968-03-23','H'),\n"
    		+ "-- Senegal\n"
    		+ "(119, 'Cisse', 'Aliou', '1976-03-24','H'),\n"
    		+ "(120, 'Diouf', 'El Hadji', '1981-01-15','H'),\n"
    		+ "\n"
    		+ "\n"
    		+ "-- Arbitres\n"
    		+ "(121, 'Garcia', 'Luis', '1980-05-20','H'),\n"
    		+ "(122, 'Perez', 'Maria', '1975-02-10','H'),\n"
    		+ "(123, 'Muller', 'Hans', '1984-09-04','H'),\n"
    		+ "(124, 'Schneider', 'Franz', '1978-11-12','H'),\n"
    		+ "(125, 'Rossi', 'Giovanna', '1990-03-25','H'),\n"
    		+ "(126, 'Bianchi', 'Chiara', '1988-07-07','H'),\n"
    		+ "(127, 'Lapez', 'Juan', '1972-01-15','H'),\n"
    		+ "(128, 'Hernandez', 'Sofia', '1995-10-28','H'),\n"
    		+ "(129, 'Gonzalez', 'Carlos', '1982-12-31','H'),\n"
    		+ "(130, 'Alvarez', 'Ana', '1993-06-18','H'),\n"
    		+ "(131, 'Nguyen', 'Hien', '1977-04-02','H'),\n"
    		+ "(132, 'Le', 'Bao', '1986-08-22','H'),\n"
    		+ "(133, 'Kim', 'Min-Jae', '1992-12-05','H'),\n"
    		+ "(134, 'Wang', 'Wei', '1979-03-08','H'),\n"
    		+ "(135, 'Tsunoda', 'Yuki', '1985-09-14','H')\n"
    		+ ";\n"
    		+ "\n"
    		+ "\n"
    		+ "\n"
    		+ "\n"
    		+ "insert into Joueur(id, annee, numero) VALUES\n"
    		+ "(1, 2014, 1),\n"
    		+ "(2, 2018, 2),\n"
    		+ "(3, 2022, 3),\n"
    		+ "(4, 2014, 3),\n"
    		+ "(5, 2018, 4),\n"
    		+ "(6, 2022, 5),\n"
    		+ "(7, 2014, 5),\n"
    		+ "(8, 2018, 6),\n"
    		+ "(9, 2022, 7),\n"
    		+ "(10, 2014, 7),\n"
    		+ "(11, 2018, 8),\n"
    		+ "(12, 2022, 9),\n"
    		+ "(13, 2014, 9),\n"
    		+ "(14, 2018, 10),\n"
    		+ "(15, 2022, 11),\n"
    		+ "(16, 2014, 11),\n"
    		+ "(17, 2018, 12),\n"
    		+ "(18, 2022, 13),\n"
    		+ "(19, 2014, 13),\n"
    		+ "(20, 2018, 14),\n"
    		+ "(21, 2022, 15),\n"
    		+ "(22, 2014, 15),\n"
    		+ "(23, 2018, 16),\n"
    		+ "(24, 2022, 17),\n"
    		+ "(25, 2014, 17),\n"
    		+ "(26, 2018, 18),\n"
    		+ "(27, 2022, 19),\n"
    		+ "(28, 2014, 19),\n"
    		+ "(29, 2018, 20),\n"
    		+ "(30, 2022, 21),\n"
    		+ "(31, 2014, 21),\n"
    		+ "(32, 2018, 22),\n"
    		+ "(33, 2022, 23),\n"
    		+ "(34, 2014, 23),\n"
    		+ "(35, 2018, 24),\n"
    		+ "(36, 2022, 25),\n"
    		+ "(37, 2014, 25),\n"
    		+ "(38, 2018, 26),\n"
    		+ "(39, 2022, 27),\n"
    		+ "(40, 2014, 27),\n"
    		+ "(41, 2018, 28),\n"
    		+ "(42, 2022, 29),\n"
    		+ "(43, 2014, 29),\n"
    		+ "(44, 2018, 30),\n"
    		+ "(45, 2022, 31),\n"
    		+ "(46, 2014, 31),\n"
    		+ "(47, 2018, 32),\n"
    		+ "(48, 2022, 33),\n"
    		+ "(49, 2014, 33),\n"
    		+ "(50, 2018, 34),\n"
    		+ "(51, 2022, 35),\n"
    		+ "(52, 2014, 35),\n"
    		+ "(53, 2018, 36),\n"
    		+ "(54, 2022, 37),\n"
    		+ "(55, 2014, 37),\n"
    		+ "(56, 2018, 38),\n"
    		+ "(57, 2022, 39),\n"
    		+ "(58, 2014, 39),\n"
    		+ "(59, 2018, 40),\n"
    		+ "(60, 2022, 41),\n"
    		+ "(61, 2014, 41),\n"
    		+ "(62, 2018, 42),\n"
    		+ "(63, 2022, 43),\n"
    		+ "(64, 2014, 43),\n"
    		+ "(65, 2018, 44),\n"
    		+ "(66, 2022, 45),\n"
    		+ "(67, 2014, 45),\n"
    		+ "(68, 2018, 46),\n"
    		+ "(69, 2022, 47),\n"
    		+ "(70, 2014, 47),\n"
    		+ "(71, 2018, 48),\n"
    		+ "(72, 2022, 49),\n"
    		+ "(73, 2014, 49),\n"
    		+ "(74, 2018, 50),\n"
    		+ "(75, 2022, 51),\n"
    		+ "(76, 2014, 51),\n"
    		+ "(77, 2018, 52),\n"
    		+ "(78, 2022, 53),\n"
    		+ "(79, 2014, 53),\n"
    		+ "(80, 2018, 54),\n"
    		+ "(81, 2022, 55),\n"
    		+ "(82, 2014, 55),\n"
    		+ "(83, 2018, 56),\n"
    		+ "(84, 2022, 57),\n"
    		+ "(85, 2014, 57),\n"
    		+ "(86, 2018, 58),\n"
    		+ "(87, 2022, 59),\n"
    		+ "(88, 2014, 59),\n"
    		+ "(89, 2018, 60),\n"
    		+ "(90, 2022, 61),\n"
    		+ "(91, 2014, 61),\n"
    		+ "(92, 2018, 62),\n"
    		+ "(93, 2022, 63),\n"
    		+ "(94, 2014, 63),\n"
    		+ "(95, 2018, 64),\n"
    		+ "(96, 2022, 65),\n"
    		+ "(97, 2014, 65),\n"
    		+ "(98, 2018, 66),\n"
    		+ "(99, 2022, 67),\n"
    		+ "(100, 2022, 69)\n"
    		+ ";\n"
    		+ "\n"
    		+ "insert into Equipe(pays, annee) VALUES\n"
    		+ "  ('Argentine', 2014),\n"
    		+ "  ('Argentine', 2018),\n"
    		+ "  ('Argentine', 2022),\n"
    		+ "  ('France', 2014),\n"
    		+ "  ('France', 2018),\n"
    		+ "  ('France', 2022),\n"
    		+ "  ('Bresil', 2014),\n"
    		+ "  ('Bresil', 2018),\n"
    		+ "  ('Bresil', 2022),\n"
    		+ "  ('Allemagne', 2014),\n"
    		+ "  ('Allemagne', 2018),\n"
    		+ "  ('Allemagne', 2022),\n"
    		+ "  ('Belgique', 2014),\n"
    		+ "  ('Belgique', 2018),\n"
    		+ "  ('Belgique', 2022),\n"
    		+ "  ('Maroc', 2014),\n"
    		+ "  ('Maroc', 2018),\n"
    		+ "  ('Maroc', 2022),\n"
    		+ "  ('Japon', 2014),\n"
    		+ "  ('Japon', 2018),\n"
    		+ "  ('Japon', 2022),\n"
    		+ "  ('Canada', 2014),\n"
    		+ "  ('Canada', 2018),\n"
    		+ "  ('Canada', 2022),\n"
    		+ "  ('Espagne', 2014),\n"
    		+ "  ('Espagne', 2018),\n"
    		+ "  ('Espagne', 2022),\n"
    		+ "  ('Senegal', 2014),\n"
    		+ "  ('Senegal', 2018),\n"
    		+ "  ('Senegal', 2022)\n"
    		+ ";\n"
    		+ "\n"
    		+ "insert into Nationalite(id, nationalite) VALUES\n"
    		+ "(1, 'Argentin'),\n"
    		+ "(2, 'Argentin'),\n"
    		+ "(3, 'Argentin'),\n"
    		+ "(4, 'Argentin'),\n"
    		+ "(5, 'Argentin'),\n"
    		+ "(6, 'Argentin'),\n"
    		+ "(7, 'Argentin'),\n"
    		+ "(8, 'Argentin'),\n"
    		+ "(9, 'Argentin'),\n"
    		+ "(10, 'Argentin'),\n"
    		+ "(11, 'Francais'),\n"
    		+ "(12, 'Francais'),\n"
    		+ "(13, 'Francais'),\n"
    		+ "(14, 'Francais'),\n"
    		+ "(15, 'Francais'),\n"
    		+ "(16, 'Francais'),\n"
    		+ "(17, 'Francais'),\n"
    		+ "(18, 'Francais'),\n"
    		+ "(19, 'Francais'),\n"
    		+ "(20, 'Francais'),\n"
    		+ "(21, 'Bresilien'),\n"
    		+ "(22, 'Bresilien'),\n"
    		+ "(23, 'Bresilien'),\n"
    		+ "(24, 'Bresilien'),\n"
    		+ "(25, 'Bresilien'),\n"
    		+ "(26, 'Bresilien'),\n"
    		+ "(27, 'Bresilien'),\n"
    		+ "(28, 'Bresilien'),\n"
    		+ "(29, 'Bresilien'),\n"
    		+ "(30, 'Bresilien'),\n"
    		+ "(31, 'Allemand'),\n"
    		+ "(32, 'Allemand'),\n"
    		+ "(33, 'Allemand'),\n"
    		+ "(34, 'Allemand'),\n"
    		+ "(35, 'Allemand'),\n"
    		+ "(36, 'Allemand'),\n"
    		+ "(37, 'Allemand'),\n"
    		+ "(38, 'Allemand'),\n"
    		+ "(39, 'Allemand'),\n"
    		+ "(40, 'Allemand'),\n"
    		+ "(41, 'Belge'),\n"
    		+ "(42, 'Belge'),\n"
    		+ "(43, 'Francais'),\n"
    		+ "(44, 'Belge'),\n"
    		+ "(45, 'Belge'),\n"
    		+ "(46, 'Belge'),\n"
    		+ "(47, 'Belge'),\n"
    		+ "(48, 'Belge'),\n"
    		+ "(49, 'Belge'),\n"
    		+ "(50, 'Belge'),\n"
    		+ "(51, 'Marocain'),\n"
    		+ "(52, 'Marocain'),\n"
    		+ "(53, 'Francais'),\n"
    		+ "(54, 'Marocain'),\n"
    		+ "(55, 'Marocain'),\n"
    		+ "(56, 'Marocain'),\n"
    		+ "(57, 'Marocain'),\n"
    		+ "(58, 'Marocain'),\n"
    		+ "(59, 'Marocain'),\n"
    		+ "(60, 'Marocain'),\n"
    		+ "(61, 'Japonais'),\n"
    		+ "(62, 'Japonais'),\n"
    		+ "(63, 'Japonais'),\n"
    		+ "(64, 'Japonais'),\n"
    		+ "(65, 'Japonais'),\n"
    		+ "(66, 'Japonais'),\n"
    		+ "(67, 'Japonais'),\n"
    		+ "(68, 'Japonais'),\n"
    		+ "(69, 'Japonais'),\n"
    		+ "(70, 'Japonais'),\n"
    		+ "(71, 'Canadien'),\n"
    		+ "(72, 'Canadien'),\n"
    		+ "(73, 'Marocain'),\n"
    		+ "(74, 'Canadien'),\n"
    		+ "(75, 'Canadien'),\n"
    		+ "(76, 'Canadien'),\n"
    		+ "(77, 'Canadien'),\n"
    		+ "(78, 'Canadien'),\n"
    		+ "(79, 'Canadien'),\n"
    		+ "(80, 'Canadien'),\n"
    		+ "(81, 'Espagnol'),\n"
    		+ "(82, 'Espagnol'),\n"
    		+ "(83, 'Argentin'),\n"
    		+ "(84, 'Espagnol'),\n"
    		+ "(85, 'Espagnol'),\n"
    		+ "(86, 'Espagnol'),\n"
    		+ "(87, 'Espagnol'),\n"
    		+ "(88, 'Espagnol'),\n"
    		+ "(89, 'Espagnol'),\n"
    		+ "(90, 'Espagnol'),\n"
    		+ "(91, 'Senegalais'),\n"
    		+ "(92, 'Senegalais'),\n"
    		+ "(93, 'Senegalais'),\n"
    		+ "(94, 'Senegalais'),\n"
    		+ "(95, 'Senegalais'),\n"
    		+ "(96, 'Senegalais'),\n"
    		+ "(97, 'Senegalais'),\n"
    		+ "(98, 'Senegalais'),\n"
    		+ "(99, 'Senegalais'),\n"
    		+ "(100, 'Senegalais'),\n"
    		+ "\n"
    		+ "(101, 'Argentin'),\n"
    		+ "(102, 'Argentin'),\n"
    		+ "(103, 'Francais'),\n"
    		+ "(104, 'Francais'),\n"
    		+ "(105, 'Portugais'),\n"
    		+ "(106, 'Bresil'),\n"
    		+ "(107, 'Allemand'),\n"
    		+ "(108, 'Allemand'),\n"
    		+ "(109, 'Espagnol'),\n"
    		+ "(110, 'Belge'),\n"
    		+ "(111, 'Francais'),\n"
    		+ "(112, 'Marocain'),\n"
    		+ "(113, 'Japonais'),\n"
    		+ "(114, 'Japonais'),\n"
    		+ "(115, 'Canadien'),\n"
    		+ "(116, 'Canadien'),\n"
    		+ "(117, 'Espagnol'),\n"
    		+ "(118, 'Espagnol'),\n"
    		+ "(119, 'Senegalais'),\n"
    		+ "(120, 'Senegalais'),\n"
    		+ "\n"
    		+ "(121, 'Espagnol'),\n"
    		+ "(122, 'Colombien'),\n"
    		+ "(123, 'Allemand'),\n"
    		+ "(124, 'Neerlandais'),\n"
    		+ "(125, 'Italien'),\n"
    		+ "(126, 'Italien'),\n"
    		+ "(127, 'Espagnol'),\n"
    		+ "(128, 'Uruguayen'),\n"
    		+ "(129, 'Peruvien'),\n"
    		+ "(130, 'Vietnamien'),\n"
    		+ "(131, 'Chinois'),\n"
    		+ "(132, 'Coreen'),\n"
    		+ "(133, 'Chinois'),\n"
    		+ "(134, 'Japonais'),\n"
    		+ "(135, 'Chinois')\n"
    		+ ";\n"
    		+ "\n"
    		+ "\n"
    		+ "insert into Staff(id, annee, roles) VALUES\n"
    		+ "(101 , 2014, 'Entraineur principal'),\n"
    		+ "(102 , 2014, 'Entraineur adjoint'),\n"
    		+ "(103 , 2014, 'Entraineur principal'),\n"
    		+ "(104 , 2014, 'Entraineur adjoint'),\n"
    		+ "(105 , 2014, 'Entraineur principal'),\n"
    		+ "(106 , 2014, 'Entraineur adjoint'),\n"
    		+ "(107 , 2014, 'Entraineur principal'),\n"
    		+ "(108 , 2014, 'Entraineur adjoint'),\n"
    		+ "(109 , 2014, 'Entraineur principal'),\n"
    		+ "(110 , 2014, 'Entraineur adjoint'),\n"
    		+ "(111 , 2014, 'Entraineur principal'),\n"
    		+ "(112 , 2014, 'Entraineur adjoint'),\n"
    		+ "(113 , 2014, 'Entraineur principal'),\n"
    		+ "(114 , 2014, 'Entraineur adjoint'),\n"
    		+ "(115 , 2014, 'Entraineur principal'),\n"
    		+ "(116 , 2014, 'Entraineur adjoint'),\n"
    		+ "(117 , 2014, 'Entraineur principal'),\n"
    		+ "(118 , 2014, 'Entraineur adjoint'),\n"
    		+ "(119 , 2014, 'Entraineur principal'),\n"
    		+ "(120 , 2014, 'Entraineur adjoint')\n"
    		+ ";\n"
    		+ "\n"
    		+ "insert into Arbitre(id, fonction) VALUES\n"
    		+ "(121 ,'Arbitre principal'),\n"
    		+ "(122 ,'Arbitre assistant'),\n"
    		+ "(123 ,'Arbitre assistant'),\n"
    		+ "(124 ,'Arbitre assistant'),\n"
    		+ "(125 ,'Arbitre principal'),\n"
    		+ "(126 ,'Arbitre assistant'),\n"
    		+ "(127 ,'Arbitre assistant'),\n"
    		+ "(128 ,'Arbitre assistant'),\n"
    		+ "(129 ,'Arbitre principal'),\n"
    		+ "(130 ,'Arbitre assistant'),\n"
    		+ "(131 ,'Arbitre assistant'),\n"
    		+ "(132 ,'Arbitre assistant'),\n"
    		+ "(133 ,'Arbitre principal'),\n"
    		+ "(134 ,'Arbitre assistant'),\n"
    		+ "(135, 'Arbitre assistant')\n"
    		+ ";\n"
    		+ "\n"
    		+ "\n"
    		+ "INSERT INTO Stade(nomStade, ville, pays, capacite) VALUES\n"
    		+ "  ('Estadio do Maracana', 'Rio de Janeiro', 'Bresil', 78838),\n"
    		+ "  ('Arena Corinthians', 'Sao Paulo', 'Bresil', 48999),\n"
    		+ "  ('Mineirao', 'Belo Horizonte', 'Bresil', 58000),\n"
    		+ "  ('Arena de Sao Paulo', 'Sao Paulo', 'Bresil', 65000),\n"
    		+ "  ('Estadio Nacional Mane Garrincha', 'Brasilia', 'Bresil', 72000),\n"
    		+ " \n"
    		+ "  ('Luzhniki Stadium', 'Moscou', 'Russie', 81000),\n"
    		+ "  ('Krestovsky Stadium', 'Saint-Petersbourg', 'Russie', 68134),\n"
    		+ "  ('Fisht Olympic Stadium', 'Sotchi', 'Russie', 47659),\n"
    		+ "  ('Ekaterinburg Arena', 'Ekaterinburg', 'Russie', 35696),\n"
    		+ "  ('Rostov Arena', 'Rostov-sur-le-Don', 'Russie', 45145),\n"
    		+ " \n"
    		+ "  ('Lusail Iconic Stadium', 'Lusail', 'Qatar', 80000),\n"
    		+ "  ('Al-Wakrah Stadium', 'Al-Wakrah', 'Qatar', 45000),\n"
    		+ "  ('Education City Stadium', 'Al-Rayyan', 'Qatar', 40000),\n"
    		+ "  ('Al-Bayt Stadium', 'Al-Khor', 'Qatar', 60000),\n"
    		+ "  ('Ras Abu Aboud Stadium', 'Doha', 'Qatar', 40000)\n"
    		+ ";\n"
    		+ "\n"
    		+ "INSERT INTO Match(idMatch, annee, scoreEquipe1, scoreEquipe2, matchDate, matchHeure, rang, equipe1, equipe2, dureeMatch, prolongation, penaltyEq1, penaltyEq2, nomStade, ville, pays)\n"
    		+ "VALUES\n"
    		+ "(136, 2014, 2, 1, '2014-06-12', '17:00:00', 'Phase de poule', 'France', 'Maroc', 92, NULL, 1, 0, 'Estadio do Maracana', 'Rio de Janeiro', 'Bresil'),\n"
    		+ "(137, 2014, 3, 1, '2014-06-13', '13:00:00', 'Phase de poule', 'Espagne', 'Canada', 94, NULL, 0, 0, 'Arena de Sao Paulo', 'Sao Paulo', 'Bresil'),\n"
    		+ "(138, 2014, 2, 2, '2014-06-17', '16:00:00', 'Quarts de finale', 'Bresil', 'Senegal', 93, 31, 1, 1, 'Mineirao', 'Belo Horizonte', 'Bresil'),\n"
    		+ "(139, 2014, 1, 0, '2014-06-20', '16:00:00', 'Demi-finale', 'Japon', 'France', 91, NULL, 0, 0, 'Arena Corinthians', 'Sao Paulo', 'Bresil'),\n"
    		+ "(140, 2014, 1, 0, '2014-06-24', '13:00:00', 'Finale', 'Allemagne', 'Argentine', 97, NULL, 0, 0, 'Estadio do Maracana', 'Rio de Janeiro', 'Bresil'),\n"
    		+ "(141, 2018, 2, 1, '2018-06-15', '18:00:00', 'Phase de poule', 'France', 'Espagne', 94, NULL, 0, 0, 'Rostov Arena', 'Rostov-sur-le-Don', 'Russie'),\n"
    		+ "(142, 2018, 0, 1, '2018-06-17', '21:00:00', 'Phase de poule', 'Allemagne', 'Belgique', 92, NULL, 0, 0, 'Ekaterinburg Arena', 'Ekaterinburg', 'Russie'),\n"
    		+ "(143, 2018, 3, 0, '2018-06-23', '15:00:00', 'Quarts de finale', 'Japon', 'Maroc', 90, NULL, 0, 0, 'Fisht Olympic Stadium', 'Sotchi', 'Russie'),\n"
    		+ "(144, 2018, 1, 0, '2018-06-26', '17:00:00', 'Demi-finale', 'Argentine', 'Bresil', 96, NULL, 0, 0, 'Krestovsky Stadium', 'Saint-Petersbourg', 'Russie'),\n"
    		+ "(145, 2018, 4, 3, '2018-07-06', '17:00:00', 'Finale', 'Allemagne', 'France', 94, NULL, 0, 0, 'Luzhniki Stadium', 'Moscou', 'Russie'),\n"
    		+ "(146, 2022, 2, 1, '2022-11-21', '16:00:00', 'Phase de poule', 'Espagne', 'Argentine', 99, NULL, 0, 0, 'Al-Wakrah Stadium', 'Al-Wakrah', 'Qatar'),\n"
    		+ "(147, 2022, 2, 2, '2022-11-26', '18:00:00', 'Phase de poule', 'Bresil', 'Japon', 112, NULL, 0, 0, 'Al-Bayt Stadium', 'Al-Khor', 'Qatar'),\n"
    		+ "(148, 2022, 1, 1, '2022-12-02', '15:00:00', 'Quarts de finale', 'Senegal', 'Espagne', 104, NULL, 0, 0, 'Education City Stadium', 'Al-Rayyan', 'Qatar'),\n"
    		+ "(149, 2022, 1, 0, '2022-12-08', '15:00:00', 'Demi-finale', 'Maroc', 'Canada', 99, NULL, 0, 0, 'Ras Abu Aboud Stadium', 'Doha', 'Qatar'),\n"
    		+ "(150, 2022, 2, 3, '2022-12-13', '18:00:00', 'Finale', 'France', 'Argentine', 96, 35, 2, 1, 'Lusail Iconic Stadium', 'Lusail', 'Qatar')\n"
    		+ ";\n"
    		+ "\n"
    		+ "INSERT INTO Confrontation(idMatch, equipe1, equipe2, annee)\n"
    		+ "VALUES\n"
    		+ "(136, 'France', 'Maroc', 2014),\n"
    		+ "(137, 'Espagne', 'Canada', 2014),\n"
    		+ "(138, 'Bresil', 'Senegal',2014),\n"
    		+ "(139, 'Japon', 'France',2018),\n"
    		+ "(140, 'Allemagne', 'Argentine',2018),\n"
    		+ "(141, 'France', 'Espagne',2018),\n"
    		+ "(142, 'Allemagne', 'Belgique',2022),\n"
    		+ "(143, 'Japon', 'Maroc',2022),\n"
    		+ "(144, 'Argentine', 'Bresil',2022),\n"
    		+ "(145, 'Allemagne', 'France',2022),\n"
    		+ "(146, 'Espagne', 'Argentine',2018),\n"
    		+ "(147, 'Bresil', 'Japon',2014),\n"
    		+ "(148, 'Senegal', 'Espagne',2018),\n"
    		+ "(149, 'Maroc', 'Canada',2014),\n"
    		+ "(150, 'France', 'Argentine',2022)\n"
    		+ ";\n"
    		+ " \n"
    		+ "insert into Gere(idMatch, id_Arbitre) VALUES\n"
    		+ "(136, 121),\n"
    		+ "(136, 122),\n"
    		+ "(136, 123),\n"
    		+ "(136, 124),\n"
    		+ "\n"
    		+ "(137, 125),\n"
    		+ "(137, 126),\n"
    		+ "(137, 127),\n"
    		+ "(137, 128),\n"
    		+ "\n"
    		+ "(138, 129),\n"
    		+ "(138, 130),\n"
    		+ "(138, 131),\n"
    		+ "(138, 132),\n"
    		+ "\n"
    		+ "(139, 133),\n"
    		+ "(139, 134),\n"
    		+ "(139, 135),\n"
    		+ "(139, 122),\n"
    		+ "\n"
    		+ "(140, 121),\n"
    		+ "(140, 123),\n"
    		+ "(140, 124),\n"
    		+ "(140, 126),\n"
    		+ "\n"
    		+ "(141, 125),\n"
    		+ "(141, 127),\n"
    		+ "(141, 128),\n"
    		+ "(141, 130),\n"
    		+ "\n"
    		+ "(142, 129),\n"
    		+ "(142, 131),\n"
    		+ "(142, 132),\n"
    		+ "(142, 134),\n"
    		+ "\n"
    		+ "(143, 133),\n"
    		+ "(143, 135),\n"
    		+ "(143, 122),\n"
    		+ "(143, 123),\n"
    		+ "\n"
    		+ "(144, 121),\n"
    		+ "(144, 124),\n"
    		+ "(144, 126),\n"
    		+ "(144, 127),\n"
    		+ "\n"
    		+ "(145, 125),\n"
    		+ "(145, 128),\n"
    		+ "(145, 130),\n"
    		+ "(145, 131),\n"
    		+ "\n"
    		+ "(146, 129),\n"
    		+ "(146, 132),\n"
    		+ "(146, 134),\n"
    		+ "(146, 135),\n"
    		+ "\n"
    		+ "(147, 121),\n"
    		+ "(147, 122),\n"
    		+ "(147, 123),\n"
    		+ "(147, 124),\n"
    		+ "\n"
    		+ "(148, 125),\n"
    		+ "(148, 126),\n"
    		+ "(148, 127),\n"
    		+ "(148, 128),\n"
    		+ "\n"
    		+ "(149, 129),\n"
    		+ "(149, 130),\n"
    		+ "(149, 131),\n"
    		+ "(149, 132),\n"
    		+ "\n"
    		+ "(150, 133),\n"
    		+ "(150, 134),\n"
    		+ "(150, 135),\n"
    		+ "(150, 122)\n"
    		+ ";\n"
    		+ "\n"
    		+ "\n"
    		+ "insert into Carton(idMatch, id_arbitre, id_joueur, type) VALUES\n"
    		+ "(136, 121, 52, 'Jaune 1'),\n"
    		+ "(137, 125, 73, 'Jaune 1'),\n"
    		+ "(138, 129, 25, 'Jaune 1'),\n"
    		+ "(139, 133, 61, 'Rouge'),\n"
    		+ "(140, 121, 34, 'Jaune 1'),\n"
    		+ "(141, 125, 14, 'Jaune 1'),\n"
    		+ "(141, 125, 14, 'Jaune 2'),\n"
    		+ "(141, 125, 14, 'Rouge'),\n"
    		+ "(142, 129, 44, 'Jaune 1'),\n"
    		+ "(143, 133, 56, 'Jaune 1'),\n"
    		+ "(144, 121, 5, 'Jaune 1'),\n"
    		+ "(144, 121, 5, 'Jaune 2'),\n"
    		+ "(144, 121, 5, 'Rouge'),\n"
    		+ "(145, 125, 35, 'Jaune 1'),\n"
    		+ "(146, 129, 84, 'Jaune 1'),\n"
    		+ "(147, 121, 24, 'Jaune 1'),\n"
    		+ "(148, 125, 99, 'Jaune 1'),\n"
    		+ "(149, 129, 78, 'Rouge'),\n"
    		+ "(150, 133, 9, 'Jaune 1')\n"
    		+ ";\n"
    		+ "\n"
    		+ "INSERT INTO Performance(id_joueur, id_arbitre, idMatch, tempsDeJeu, nb_passes, assistances, buts)\n"
    		+ "VALUES\n"
    		+ "    (52, 121, 136, 90, 43, 1, 1),\n"
    		+ "    (73, 125, 137, 90, 38, 0, 0),\n"
    		+ "    (25, 129, 138, 45, 27, 2, 0),\n"
    		+ "    (61, 133, 139, 45, 22, 0, 1),\n"
    		+ "    (34, 121, 140, 90, 61, 0, 1),\n"
    		+ "    (14, 125, 141, 90, 55, 1, 1),\n"
    		+ "    (44, 129, 142, 45, 23, 1, 0),\n"
    		+ "    (56, 133, 143, 45, 26, 0, 0),\n"
    		+ "    (5, 121, 144, 90, 59, 0, 1),\n"
    		+ "    (35, 125, 145, 90, 56, 2, 2),\n"
    		+ "    (84, 129, 146, 45, 23, 1, 0),\n"
    		+ "    (24, 121, 147, 45, 30, 2, 0),\n"
    		+ "    (99, 125, 148, 90, 69, 0, 0),\n"
    		+ "    (78, 129, 149, 90, 61, 0, 1),\n"
    		+ "    (9, 133, 150, 45, 32, 0, 2)\n"
    		+ ";\n"
    		+ "\n"
    		+ "-------------------- END INSERTION --------------------------\n"
    		+ "\n"
    		+ "-------------------- REQUESTS ----------------------------\n"
    		+ "\n"
    		+ "\n"
    		+ "\n"
    		+ "\n"
    		+ "------------------- END REQUESTS -----------------------------\n"
    		+ "\n"
    		+ "\n"
    		+ ""
    		+ "commit; ";
            

    //requête des questions 1,2,3,4
    private String requete1 = "SELECT DISTINCT nationalite.nationalite as nationalite" +
            "FROM Performance" +
            "JOIN Joueur ON Performance.num_id_j = Joueur.#num_id"+
            "JOIN Nationalite ON Joueur.#num_id = Nationalite.#num_id"+
            "WHERE Performance.buts > 2 AND Performance.#id_match ="+
            "'match_id';";

    private String requete2 = "SELECT Equipe.pays as pays" +
            "FROM Equipe" +
            " WHERE NOT EXISTS (" +
                "SELECT Coupe_du_monde.annee" +
                    "FROM Coupe_du_monde" +
                        "WHERE NOT EXISTS ("+
                          "SELECT *"+
                          "FROM Equipe AS E"+
                          "WHERE E.annee = Coupe_du_monde.annee AND E.pays = Equipe.pays));";

    private String requete3 = "SELECT Stade.nom_s as stade, Stade.ville as ville, Stade.pays as pays" +
            "FROM Stade" +
            " JOIN Match ON Match.nom_s = Stade.nom_s AND Match.ville = Stade.ville AND Match.#annee_m = 'annee'" +
            "WHERE Match.duree_match >= 90 AND Match.score_e1 <> Match.score_e2 AND Stade.capacite > 50000;";

    private String requete4 = "SELECT Arbitre.num_id as id_arbitre COUNT(DISINCT coupeDuMonde) as nb_finales" +
            " from Arbitre" +
            " JOIN Match ON arbitre.num_id = match.num_id AND Match.annee_m IN (SELECT annee FROM Coupe_du_monde)" +
                "GROUP BY Arbitre.num_id" +
                    "ORDER BY nb_finales DESC"+ 
                    "LIMIT 1";

    private JMenuBar menuBar = new JMenuBar();
    private JPanel panel = new JPanel();
    private Container container = getContentPane();

    private JMenu fichier = new JMenu("Fichier");
    private JMenu affichage = new JMenu("Affichage");
    private JMenu aide = new JMenu("Aide");
    private JLabel titre = new JLabel("Résultat Coupe du monde");
    private JButton question1 = new JButton("Question 1");
    private JButton question2 = new JButton("Question 2");
    private JButton question3 = new JButton("Question 3");
    private JButton question4 = new JButton("Question 4");
    private JTextArea resultat = new JTextArea();
    private JMenuItem quitter = new JMenuItem("Quitter");

    public GestionCoupeDuMonde() throws IOException{

        setTitle("Gestion coupe du monde");

        // Ajouter dans le menuBar
        menuBar.add(fichier);
        menuBar.add(affichage);
        menuBar.add(aide);
        setJMenuBar(menuBar);

        //Ajouter les sous menus
        fichier.add(quitter);

        //Créer le layout et les size
        container.setLayout(null);

        question1.setSize(140,50);
        question1.setLocation(150,150);

        question2.setSize(140,50);
        question2.setLocation(150,300);

        question3.setSize(140,50);
        question3.setLocation(150,450);

        question4.setSize(140,50);
        question4.setLocation(150,600);

        resultat.setSize(1000,850);
        resultat.setLocation(350,150);
        resultat.setFont(new Font("Arial", Font.PLAIN, 16));


        titre.setSize(250,250);
        titre.setLocation(750, 5);

        // Ajouter les items
        container.add(question1);
        container.add(question2);
        container.add(question3);
        container.add(question4);
        container.add(resultat);
        container.add(titre);

        //ajouter les listners
        question1.addActionListener(this);
        question2.addActionListener(this);
        question3.addActionListener(this);
        question4.addActionListener(this);


        setLocation(250, 20);
        setSize(1500, 1000);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    //créer la connnection postgresSQL avec JDBC
    public Connection connect() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connecté aux serveurs PostgreSQL avec succès");

        } catch (SQLException e) {
            System.out.println("Connection échouée");
            e.printStackTrace();
        }
        return conn;
    }

    public void bdCreateInsert() throws SQLException {
        Statement stmt = null;

        stmt = conn.createStatement();
        stmt.executeUpdate(createSQL);
        stmt.executeUpdate(insertSQL);

        stmt.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Statement stmt = null;
        String reponse = "";
        try {
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        /*Question 1  Quelles sont les nationalités des joueurs qui ont marqué plus de deux buts dans un match donné ?*/
        if(e.getSource() == question1){
            try {
                ResultSet requeteQ1 = stmt.executeQuery(requete1);
                reponse = "Quelles sont les nationalités des joueurs qui ont marqué plus de deux buts dans un match donné ? " +
                          "nationalite\n";
                while(requeteQ1.next()){
                    String nationalite = requeteQ1.getString("nationalite");
                    reponse += nationalite + "\n";
                    resultat.setText(reponse);
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        /*Question 2: Quels sont les pays qui ont participé à toutes les éditions de la Coupe du monde ?*/
        if(e.getSource() == question2) {
            try {
                ResultSet requeteQ2 = stmt.executeQuery(requete2);
                reponse = "Quels sont les pays qui ont participé à toutes les éditions de la Coupe du monde ? \n\n"
                        +"pays\n";
                while (requeteQ2.next()) {
                    String pays = requeteQ2.getString("pays");

                    reponse += pays + "\n";

                    resultat.setText(reponse);

                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        /*Question 3: Quels sont les stades qui ont accueilli plus de 50 000 spectateurs lors d'un match éliminatoire de la Coupe du monde ? 
*/
        if(e.getSource() == question3){
            try {
                ResultSet requeteQ3 = stmt.executeQuery(requete3);
                reponse = "Quels sont les stades qui ont accueilli plus de 50 000 spectateurs lors d'un match éliminatoire de la Coupe du monde\n\n ?  "
                		+ "stade, ville, pays  \n";
                while (requeteQ3.next()) {
                    int id = requeteQ3.getInt("id");
                    String stade = requeteQ3.getString("stade");
                    String pays  = requeteQ3.getString("pays");
                    String ville = requeteQ3.getString("ville");
                    reponse += stade + "," + pays + ", " + ville + "\n";
                    resultat.setText(reponse);
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        /*Question 4: Quel est l'arbitre qui a officié lors du plus grand nombre de finales de la Coupe du monde ?*/
        if(e.getSource() == question4){
            try {
                ResultSet requeteQ4 = stmt.executeQuery(requete4);
                reponse = "Quel est l'arbitre qui a officié lors du plus grand nombre de finales de la Coupe du monde ?  \n\n" +
                         "num_id, nb_finales \n";
                while (requeteQ4.next()) {
                	int num_id = requeteQ3.getInt("num_id");
                	int nb_finales = requeteQ3.getInt("nb_finales");
                    reponse += num_id + "," + nb_finales + "\n";
                    resultat.setText(reponse);
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }

    }

    public static void main(String[] args) throws IOException, SQLException {

        GestionCoupeDuMonde maFenetre = new GestionCoupeDuMonde();

        maFenetre.bdCreateInsert();
        maFenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}