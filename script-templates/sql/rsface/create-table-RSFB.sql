/* Base sur rsface2023b.ods, cree 240903 */
CREATE TABLE RSFB (
ID_RSFB IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1), --Type d'enregistrement na - na - Valeur=B
FINESS VARCHAR(9), --Numéro FINESS d’inscription ePMSI Finess PMSI permettant la transmission sur e-PMSI
FINESG VARCHAR(9), --Numéro FINESS géographique Finess géographique rattaché au finess PMSI
NIA VARCHAR(13), --N° immatriculation assuré Type 2 CP - 12
CNI NUMERIC(2), --Clé du n° immatriculation Type 2 CP - 25
RGB NUMERIC(3), --Rang de bénéficiaire Type 2 CP - 27 - Valeur 000 pour le régime général. A prendre sur l’attestation de droits ou la prise en charge pour autres régimes si présent
NENT NUMERIC(9), --N° d’entrée Type 2 CP - 40 - N° attribué par l'établissement
NII VARCHAR(13), --N° immatriculation individuel Type 2 CP - 50 - A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge.
CNII NUMERIC(2), --Clé du n° immatriculation individuel Type 2 CP - 63 - A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5 de la norme B2
MTT NUMERIC(2), --Mode de traitement Type 3 CP - 39
DPR NUMERIC(3), --Discipline de prestation (ex DMT) Type 3 CP - 41
JETM VARCHAR(1), --Justification exo TM Type 3 CP - 64
SPEX VARCHAR(2), --Spécialité exécutant na - na - Blancs
DSOI DATE, --Date des soins Type 3 CP - 44 - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)M - odification liée au format de la date (JJMMAAAA)
CACT VARCHAR(5), --Code acte Type 3 CP - 56 - 4+1
QTE NUMERIC(3), --Quantité Type 3 CP - 61 - Compléter par « 0 » devant
COEF NUMERIC(6,2), --Coefficient Type 3 CP - 65 - 4+2. Attention format différent par rapport à la norme B2 (3+2): compléter par 0 devant
TPI VARCHAR(1), --Type de prestation intermédiaire Note 1: Valeurs acceptées {1, 2, 3,4 }
CMCO NUMERIC(5,4), --Coefficient MCO Type 3 CP - 71 - 1+4 changement position
F98 VARCHAR(2), --Filler 
PU NUMERIC(7,2), --Prix Unitaire Type 3 CP - 76 - 5+2
MBR NUMERIC(8,2), --Montant Base remboursement Type 3 CP - 83 - 6+2
TXAP NUMERIC(3), --Taux applicable à la prestation Type 3 CP - 91
MRAMO NUMERIC(8,2), --Montant Remboursable par la caisse (AMO) Type 3 CP - 94 - 6+2
MHOT NUMERIC(8,2), --Montant des honoraire (dépassement compris) ou Montant total de la dépense pour PH Type 3 CP - 102 - 6+2 - Montant total de la dépense, dépassement compris (chambre particulière...)
MRAMC NUMERIC(7,2), --Montant remboursable par l'organisme complémentaire (AMC) Type 3 CP - 122 - 5+2
F141 VARCHAR(15), --Filler 
NFACT NUMERIC(9) --Numéro facture Type 2 CP 30 - N° attribué par le partenaire de santé. Il ne doit pas y avoir de N° en double, durant la période à déterminer avec l'assurance maladie. Le numéro de la facture doit etre différent de zéro. 
)
