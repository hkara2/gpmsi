/* Base sur rsface2023l.ods, cree 240904 */
CREATE TABLE RSFL (
ID_RSFL IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1), --Type d'enregistrement NA Valeur=L
FINESS VARCHAR(9), --Numéro FINESS d’inscription ePMSI RSS non groupé - Finess PMSI permettant la transmission sur e-PMSI
FINESG VARCHAR(9), --Numéro FINESS géographique Finess géographique rattaché au finess PMSI
NIA VARCHAR(13), --N° immatriculation assuré Type 2 CP 12
CNI NUMERIC(2), --Clé du n° immatriculation Type 2 CP 25
RGB NUMERIC(3), --Rang de bénéficiaire Type 2 CP 27 - Valeur 000 pour le régime général. A prendre sur l’attestation de droits ou la prise en charge pour autres régimes si présent
NENT NUMERIC(9), --Numéro d'entrée Type 2 CP 40 - N° attribué par l'établissement - Changement position dans le format
NII VARCHAR(13), --N° immatriculation individuel Type 2S CP 50 A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge.
CNII NUMERIC(2), --Clé du n° immatriculation individuel Type 2S CP 63 A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5 de la norme B2
MTT NUMERIC(2), --Mode de traitement Type 4 CP 39
DPR NUMERIC(3), --Discipline de prestation (ex DMT) Type 4 CP 41
DA1 DATE, --Date de l'acte 1 Type 4 B 49 Modification liée au format de la date (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
QA1 NUMERIC(2), --Quantité acte 1 Type 4 B 55 - Un enregistrement de type 4B doit comporter au moins un acte
CA1 NUMERIC(8), --Code acte 1 Type 4 B 57
DA2 DATE, --Date de l'acte 2 Type 4 B 65 Modification liée au format de la date (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
QA2 NUMERIC(2), --Quantité acte 2 Type 4 B 71
CA2 NUMERIC(8), --Code acte 2 Type 4 B 73
DA3 DATE, --Date de l'acte 3 Type 4 B 81 Modification liée au format de la date (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
QA3 NUMERIC(2), --Quantité acte 3 Type 4 B 87
CA3 NUMERIC(8), --Code acte 3 Type 4 B 89
DA4 DATE, --Date de l'acte 4 Type 4 B 97 Modification liée au format de la date (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
QA4 NUMERIC(2), --Quantité acte 4 Type 4 B 103
CA4 NUMERIC(8), --Code acte 4 Type 4 B 105
DA5 DATE, --Date de l'acte 5 Type 4 B 113 Modification liée au format de la date (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
QA5 NUMERIC(2), --Quantité acte 5 Type 4 B 119
CA5 NUMERIC(8), --Code acte 5 Type 4 B 121
NFACT NUMERIC(9) --Numéro de facture Type 2 CP 30 - N° attribué par le partenaire de santé. Il ne doit pas y avoir de N° en double, durant la période à déterminer avec l'assurance maladie. Le numéro de la facture doit etre différent de zéro. 
)
