/* Base sur rsface2023p.ods, cree 240904 */
CREATE TABLE RSFP (
ID_RSFP IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1), --Type d'enregistrement na Valeur=P
FINESS VARCHAR(9), --Numéro FINESS d’inscription ePMSI Finess PMSI permettant la transmission sur e-PMSI
FINESG VARCHAR(9), --Numéro FINESS géographique Finess géographique rattaché au finess PMSI
NIA VARCHAR(13), --N° immatriculation assuré Type 2 CP 12
CNI NUMERIC(2), --Clé du n° immatriculation Type 2 CP 25
RGB NUMERIC(3), --Rang de bénéficiaire Type 2 CP 27 - Valeur 000 pour le régime général. A prendre sur l’attestation de droits ou la prise en charge pour autres régimes si présent
NENT NUMERIC(9), --N° d'entrée Type 2 CP 40 Changement position dans le format - N° attribué par l'établissement
NII VARCHAR(13), --N° immatriculation individuel Type 2S CP 50 A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge.
CNII NUMERIC(2), --Clé du n° immatriculation individuel Type 2S CP 63 A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5 de la norme B2
DDSEJ DATE, --Date de début de séjour Type 3 CP 44 Attention format de date différent (JJMMAAAA) - ATTENTION : format différent de la norme B2 (8 car. demandé contre 6 car. norme B2)
CRLPP VARCHAR(13), --Code référence LPP Type 3F 43 - Code de l'article de la LPP
QTE NUMERIC(2), --Quantité Type 3F 70 - Indiquer le nombre d'articles identiques
TRLPP NUMERIC(7,2), --Tarif référence LPP ou Prix Unitaire sur devis Type 3F 72 (5+2)
MTF NUMERIC(7,2), --Montant total facturé Type 3F 79 -0 par défaut (5+2)
PAU NUMERIC(7,2), --Prix d'achat unitaire Type 3F 86 (5+2)
MUEI NUMERIC(7,2), --Montant unitaire de l'écart indemnisable Type 3F 93 (5+2)
MTEI NUMERIC(7,2), --Montant total de l'écart indemnisable Type 3F 100 (5+2)
NFACT NUMERIC(9) --Numéro de facture Type 2 CP 30 - N° attribué par le partenaire de santé. Il ne doit pas y avoir de N° en double, durant la période à déterminer avec l'assurance maladie. Le numéro de la facture doit etre différent de zéro. 
)
