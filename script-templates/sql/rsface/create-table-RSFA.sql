/* Base sur rsface2023a.ods, cree 240905 */
CREATE TABLE RSFA (
ID_RSFA IDENTITY PRIMARY KEY, -- generation automatique de la cle
TENR VARCHAR(1) , --Type d'enregistrement - NA -  - Valeur=A
FINESS VARCHAR(9) , --Numéro FINESS d’inscription ePMSI - RSS non groupé -  - 
FINESG VARCHAR(9) , --Numéro FINESS géographique -  -  - 
SEXE VARCHAR(1) , --Sexe - RSS non groupé -  - 
CCIV NUMERIC(1) , --Code civilité - Type 2B - 40 - Recodage : M=1/MME=2:MLE=2/SAN=3
NIA VARCHAR(13) , --N° immatriculation assuré - Type 2 CP - 12 - 
CNI VARCHAR(2) , --Clé du n° immatriculation - Type 2 CP - 25 - 
RGB VARCHAR(3) , --Rang de bénéficiaire - Type 2 CP - 27 - 
NENT VARCHAR(9) , --N° d'entrée - Type 2 CP - 40 - 
NII VARCHAR(13) , --N° immatriculation individuel - Type 2S CP - 50 - A renseigner si l’information est présente sur la carte Vitale, l’attestation de droits ou la prise en charge
CNII VARCHAR(2) , --Clé du n° immatriculation individuel - Type 2S CP - 63 - A prendre sur le même support que le n° immatriculation. Clé à contrôler après la saisie, cf. annexe 5 de la norme B2
IPS VARCHAR(1) , --Indicateur du parcours de soins - Type 2S CP - 121 - Valeurs acceptées {' ', A, B, D, H, J, M, N, O, R, S, T, U}
NOP VARCHAR(1) , --Nature opération - Type 2 CP - 39 - 
NAS VARCHAR(2) , --Nature assurance - Type 2 CP - 77 - Valeurs acceptées {10, 13, 30, 41, 90}
TCOC VARCHAR(2) , --Type de contrat souscrit auprès d'un organisme complémentaire - Type 2 CP - 117 - 
JETM VARCHAR(1) , --Justification d'exonération du TM - Type 2 CP - 79 - 
SFA VARCHAR(1) , --Séjour facturable à l'assurance maladie - Note n°1 -  - Valeurs acceptées {0, 1, 2, 3}
F72 VARCHAR(1) , --Filler -  -  - 
MNFAM NUMERIC(1) , --Motif de non facturation à l’assurance maladie - Note n°2  -  - Valeurs acceptées {' ', 1, 2, 3, 4, 5, 6, 9}
CGR VARCHAR(2) , --Code Gd régime - Type 2 CP - 49 - Valeurs acceptées {' ', 10, 02, 03, 04, 05, 06, 07, 08, 09, 10, 12, 14, 15, 16, 17, 80 ,90 ,91, 92, 93, 94, 95, 96, 99}
DNAIS DATE, --Date Naissance - Type 2 CP - 96 - Modification liée au format de la date (JJMMAAAA)
RNAIS VARCHAR(1) , --Rang de naissance - Type 2 CP - 102 - 
DENT DATE, --Date d'entrée - Type 2 CP - 103 - Modification liée au format de la date (JJMMAAAA)
DSOR DATE, --Date de sortie - Type 2 CP - 109 - Modification liée au format de la date (JJMMAAAA)
CPLR VARCHAR(5) , --Code postal du lieu de résidence du patient - Type 2C - 91 - 
TBRPH NUMERIC(8,2) , --Total Base Remboursement Prestation hospitalière -Type 5 CP - 42 - Total des lignes de type 3 (RSF B et P) : 6+2
TRAPH NUMERIC(8,2) , --Total remboursable AMO Prestation hospitalières -Type 5 CP - 50 - Id 6+2
THFACT NUMERIC(8,2) , --Total honoraire Facturé -Type 5 CP - 58 - Total des lignes de type 4 (RSF C et M) 6+2
THRA NUMERIC(8,2) , --Total honoraire remboursable AM -Type 5 CP - 66 - Id 6+2
TPAAO NUMERIC(8,2) , --Total participation assuré avant OC -Type 5 CP - 74 - Total des lignes de type 3 (RSF B et P) : 6+2
TROPH NUMERIC(8,2) , --Total remboursable OC pour les PH -Type 5 CP - 82 - 6+2
TROHO NUMERIC(8,2) , --Total remboursable OC pour les honoraires -Type 5 CP - 90 - 6+2
MTFAPH NUMERIC(8,2) , --Montant total facturé pour PH -Type 5 CP - 115 - 6+2
F170 VARCHAR(1) , --Filler -  -  - 
PCMU NUMERIC(1) , --Patient bénéficiaire de la CMU - NA -  - 0 : Non | 1 : Oui, laisser à vide si pas d'information
VFID VARCHAR(1) , --Valorisé par FIDES - NA -  - 
CGES VARCHAR(2) , --Code gestion - NA -  - Reporter l’information figurant dans la zone « régime obligatoire » de la carte vitale
F175 VARCHAR(9) , --Filler -  -  - 
NOC VARCHAR(10) , --N° d’organisme complémentaire - Type 2 CP - 119 - L’organisme obligatoire peut selon conventions régler directement la part de l’organisme complémentaire à l’établissement ou à l’assuré ou transmettre l’image de son décompte à l’organisme complémentaire. Lorsque l’établissement pratique un tiers payant sur la part complémentaire, il doit obligatoirement positionner le n° de l’organisme complémentaire, cadré à droite, et complété par des zéros à gauche, ou le top mutualiste «M » en position 128, la zone étant complétée par des blancs.
NAT VARCHAR(9) , --Numéro accident du travail ou date d’accident de droit commun - Type 2 CP - 86 - Obligatoire en cas d’accident. Lorsque la Nature d’Assurance est AT (41) : indiquer le numéro de l’AT (pour le RG, AAMMJJ + code CRAM + clé à contrôler, voir annexe 5) ou la date de l’AT (AAMMJJ, cadrée à droite et complétée par 3 zéros). Lorsque la Nature d'Assurance est AS (10 ou 13): indiquer la date d’accident de droit commun (AAMMJJ, cadrée à droite et complétée par 3 zéros)
MTSFP NUMERIC(8,2) , --Montant total du séjour facturé au patient -Facultatif Indicateur Simphonie Montant facturé au titre de la part patient
REJAMO NUMERIC(1) , --Rejet AMO - Obligatoire Indicateur Simphonie Nombre de fois où FT AMO a été rejetée (0 à 9)
DFAMO DATE, --Date de facturation AMO - Facultatif Indicateur Simphonie Date de l’envoi de la FT AMO
DFAMC DATE, --Date de facturation AMC - Facultatif Indicateur Simphonie Date de l’envoi de la FT AMC
DFPAT DATE, --Date de facturation patient - Facultatif Indicateur Simphonie Date de l’envoi de la FT patient
DPAMO DATE, --Date de paiement AMO - Facultatif Indicateur Simphonie Date à laquelle la FT AMO est payée en totalité (statut S16)
DPAMC DATE, --Date de paiement AMC - Facultatif Indicateur Simphonie Date à laquelle la FT AMC est payée en totalité (statut S16)
DPPAT DATE, --Date de paiement patient - Facultatif Indicateur Simphonie Date à laquelle la FT patient est payée en totalité (statut S16)
SFTAMP NUMERIC(1) , --Statut FT AMO - Obligatoire 0 : avant FT validée (statut S2 à S5) 1 : FT validée (statut S6 à S12, S14, S19, et S20) 2 : FT en NiNi (statut S13) 3 : FT payée (S15+S16+S17+S) 9 : sans objet
SFTAMC NUMERIC(1) , --Statut FT AMC - Obligatoire 0 : avant FT validée (statut S2 à S5) 1 : FT validée (statut S6 à S12, S14, S19, et S20) 2 : FT en NiNi (statut S13) 3 : FT payée (S15+S16+S17+S18) 9 : sans objet
SFTPAT NUMERIC(1) , --Statut FT patient - Obligatoire 0 : avant FT validée (statut S2 à S5) 1 : FT validée (statut S6 à S12, S14, S19, et S20) 2 : FT en NiNi (statut S13) 3 : FT payée (S15+S16+S17+S18) 9 : sans objet
PASOC VARCHAR(3) , --Pays d’assurance social - Code INSEE à 5 chiffres, sans les deux premiers chiffres 99. Ou bien code ISO ISO 3166-1 Alpha-3. Pour renseigner cette donnée, il est important de prendre en compte le contexte assurantiel du patient (et non son pays d’origine).Par exemple, un patient français peut avoir une assurance étrangère (anglaise, américaine ou autre…). De même, un patient américain peut avoir une assurance anglaise ou autre. Le code pays à renseigner est bien le code pays de l’assurance du patient. Mettre 000 ou laisser à blanc quand l’information n’est pas disponible.
IPP VARCHAR(20) , --Numéro d’identification permanent du patient (IPP) - 
INS VARCHAR(15) , --Identifiant national de santé (INS) - 
ART51 VARCHAR(1) , --ART51 - 
NFACT NUMERIC(9)  --Numéro facture - Type 2 CP - 30 - 
)
