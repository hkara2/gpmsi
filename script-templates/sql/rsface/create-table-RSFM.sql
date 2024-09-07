/* Base sur rsface2023m.ods, cree 240904 */
CREATE TABLE RSFM (
ID_RSFM IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1), --Type d'enregistrement na Valeur=M
FINESS VARCHAR(9), --Numéro FINESS d’inscription ePMSI RSS non groupé - Finess PMSI permettant la transmission sur e-PMSI
FINESG VARCHAR(9), --Numéro FINESS géographique Finess géographique rattaché au finess PMSI
NIA VARCHAR(13), --N° immatriculation assuré Type 2 CP 12
CNI NUMERIC(2), --Clé du n° immatriculation Type 2 CP 25
RGB NUMERIC(3), --Rang de bénéficiaire Type 2 CP 27 - Valeur 000 pour le régime général. A prendre sur l’attestation de droits ou la prise en charge pour autres régimes si présent
NENT NUMERIC(9), --N° d’entrée Type 2 CP 40 - N° attribué par l'établissement
NII VARCHAR(13), --N° immatriculation individuel Type 2S CP 50 A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge.
CNII NUMERIC(2), --Clé du n° immatriculation individuel Type 2S CP 63 A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5  de la norme B2
MTT NUMERIC(2), --Mode de traitement Type 4 39
DPR NUMERIC(3), --Discipline de prestation (ex DMT) Type 4 41
DA DATE, --Date de l'acte Type 4 70 Modification liée au format de la date (JJMMAAAA) - Date de l'acte ou de délivrance du médicament ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2) 
CCAM VARCHAR(13), --Code CCAM Type 4 M 43 Code CCAM
XTD VARCHAR(1), --Extension documentaire Type 4 M 56
ACT VARCHAR(1), --Activité Type 4 M 57
PHA VARCHAR(1), --Phase Type 4 M 58
MA1 VARCHAR(1), --Modificateur 1 Type 4 M 59
MA2 VARCHAR(1), --Modificateur 2 Type 4 M 60
MA3 VARCHAR(1), --Modificateur 3 Type 4 M 61
MA4 VARCHAR(1), --Modificateur 4 Type 4 M 62
CANP VARCHAR(1), --Code association d'actes non prévue Type 4 M 63
CRSC VARCHAR(1), --Code remboursement sous condition Type 4 M 64
ND1 VARCHAR(2), --N° dent 1 Type 4 M 71 - Obligatoire pour les soins dentaires Utilisation de la norme ISO 3960-1984
ND2 VARCHAR(2), --N° dent 2 Type 4 M 73 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND3 VARCHAR(2), --N° dent 3 Type 4 M 75 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND4 VARCHAR(2), --N° dent 4 Type 4 M 77 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND5 VARCHAR(2), --N° dent 5 Type 4 M 79 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND6 VARCHAR(2), --N° dent 6 Type 4 M 81 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND7 VARCHAR(2), --N° dent 7 Type 4 M 83 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND8 VARCHAR(2), --N° dent 8 Type 4 M 85 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND9 VARCHAR(2), --N° dent 9 Type 4 M 87 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND10 VARCHAR(2), --N° dent 10 Type 4 M 89 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND11 VARCHAR(2), --N° dent 11 Type 4 M 91 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND12 VARCHAR(2), --N° dent 12 Type 4 M 93 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND13 VARCHAR(2), --N° dent 13 Type 4 M 95 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND14 VARCHAR(2), --N° dent 14 Type 4 M 97 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND15 VARCHAR(2), --N° dent 15 Type 4 M 99 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
ND16 VARCHAR(2), --N° dent 16 Type 4 M 101 - Obligatoire pour les soins dentaires si plusieurs numéros de localisation de dents doivent être renseignés
NFACT NUMERIC(9) --Numéro facture Type 2 CP 30 - N° attribué par le partenaire de santé. Il ne doit pas y avoir de N° en double, durant la période à déterminer avec l'assurance maladie. Le numéro de la facture doit etre différent de zéro. 
)
