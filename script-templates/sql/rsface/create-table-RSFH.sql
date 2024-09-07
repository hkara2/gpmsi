/* Base sur rsface2023h.ods, cree 240904 */
CREATE TABLE RSFH (
ID_RSFH IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1), --Type d'enregistrement NA Valeur=H
FINESS VARCHAR(9), --Numéro FINESS d’inscription ePMSI RSS non groupé 1 - Finess PMSI permettant la transmission sur e-PMSI
FINESG VARCHAR(9), --Numéro FINESS géographique Finess géographique rattaché au finess PMSI
NIA VARCHAR(13), --N° immatriculation assuré Type 2 CP 12
CNI NUMERIC(2), --Clé du n° immatriculation Type 2 CP 25
RGB NUMERIC(3), --Rang de bénéficiaire Type 2 CP 27 - Valeur 000 pour le régime général. A prendre sur l’attestation de droits ou la prise en charge pour autres régimes si présent
NENT NUMERIC(9), --N° d'entrée Type 2 CP 40 - N° attribué par l'établissement
NII VARCHAR(13), --N° immatriculation individuel Type 2S CP 50 A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge.
CNII NUMERIC(2), --Clé du n° immatriculation individuel Type 2S CP 63 A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5 de la norme B2
DDSEJ DATE, --Date de début de séjour Type 3 CP 44 Modification liée au format de la date (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
CUCD NUMERIC(7), --Code UCD Type 3H 49 - Code UCD associé à une nature de prestation PH8, pour les médicaments délivrables en sus du GHS
COEFF NUMERIC(5,4), --Coefficient de fractionnement Type 3H 57 1+4 (10000 par défaut)
PAU NUMERIC(7,2), --Prix d'achat unitaire TTC Type 3H 67 5+2
MUEI NUMERIC(7,2), --Montant unitaire de l'écart indemnisable Type 3H 74 0 par défaut (5+2)
MTEI NUMERIC(7,2), --Montant total de l'écart indemnisable Type 3H 81 5+2
QTE NUMERIC(3), --Quantité Type 3H 88 Nombre d'unités délivrées, par défaut égal à 1 - Compléter par « 0 » devant
MTF NUMERIC(7,2), --Montant total facturé TTC Type 3H 91 0 par défaut (5+2)
INDIC VARCHAR(7), --Indication Liste des codes indications
NFACT NUMERIC(9) --Numéro facture Type 2 CP 30 - N° attribué par le partenaire de santé. Il ne doit pas y avoir de N° en double, durant la période à déterminer avec l'assurance maladie. Le numéro de la facture doit etre différent de zéro. 
)
