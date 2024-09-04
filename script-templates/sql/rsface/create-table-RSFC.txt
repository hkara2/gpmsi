/* Base sur rsface2023c.ods, cree 240903 */
CREATE TABLE RSFC (
ID_RSFC IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1), --Type d'enregistrement na na Valeur=C
FINESS VARCHAR(9), --Numéro FINESS d’inscription ePMSI RSS non groupé 1 - Finess PMSI permettant la transmission sur e-PMSI
FINESG VARCHAR(9), --Numéro FINESS géographique Finess géographique rattaché au finess PMSI
NIA VARCHAR(13), --N° immatriculation assuré Type 2 CP 12
CNI NUMERIC(2), --Clé du n° immatriculation Type 2 CP 25
RGB NUMERIC(3), --Rang de bénéficiaire Type 2 CP 27 - Valeur 000 pour le régime général. A prendre sur l’attestation de droits ou la prise en charge pour autres régimes si présent
NENT NUMERIC(9), --N° d’entrée Type 2 CP 40 - N° attribué par l'établissement
NII VARCHAR(13), --N° immatriculation individuel Type 2S CP 50 A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge.
CNII NUMERIC(2), --Clé du n° immatriculation individuel Type 2S CP 63 A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5 de la norme B2
MTT NUMERIC(2), --Mode de traitement Type 4 CP 39
DPR VARCHAR(3), --Discipline de prestation (ex DMT) na na Blancs
JETM VARCHAR(1), --Justification exo TM Type 4 CP 54
SPEX NUMERIC(2), --Spécialité exécutant Type 4 CP 68 Liste des codes : NOEMIE OC entité EXE-SPE annexe 17
DSOI DATE, --Date des soins Type 4 CP 70 Date de l'acte ou de délivrance du médicament.  ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
CACT VARCHAR(5), --Code acte Type 4 CP 76 4+1 - 4 caractéres sont réservés au code prestation, cadré à gauche
QTE NUMERIC(3), --Quantité Type 4 CP 81 Attention format différent : compléter par 0 devant
COEF NUMERIC(6,2), --Coefficient Type 4 CP 83 4+2
DENB NUMERIC(2), --Dénombrement Type 4 CP 89 Compléter par « 0 » devant
PU NUMERIC(7,2), --Prix Unitaire Type 4 CP 91 5+2 
MBR NUMERIC(8,2), --Montant Base remboursement Type 4 CP 98 6+2. Attention format différent : compléter par 0 devant 
TXAP NUMERIC(3), --Taux applicable à la prestation Type 4 CP 105
MRAMO NUMERIC(8,2), --Montant Remboursable par la caisse (AMO) Type 4 CP 108 6+2. Attention format différent : compléter par 0 devant
MHOT NUMERIC(8,2), --Montant des honoraire (dépassement compris) ou Montant total de la dépense pour PH Type 4 CP 115 6+2. Attention format différent : compléter par 0 devant
MRAMC NUMERIC(7,2), --Montant remboursable par l'organisme complémentaire (AMC) Type 4 CP 123 5+2. Attention format différent : compléter par 0 devant
F135 VARCHAR(11), --Filler 
TUFC VARCHAR(2), --Type d’unité fonctionnelle de consultations Note 1  : Valeurs acceptées {01, 02, 03, 04, 05, 06, 07, 08, 09, 10, 11, 12, 13}
CMCO NUMERIC(5,4), --Coefficient MCO Type 3 CP 71 1+4
NFACT NUMERIC(9) --Numéro facture Type 2 CP 30 - N° attribué par le partenaire de santé. Il ne doit pas y avoir de N° en double, durant la période à déterminer avec l'assurance maladie. Le numéro de la facture doit etre différent de zéro. 
)