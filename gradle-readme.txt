☺:encoding=UTF-8: 

Pour partir de zéro, faire un nettoyage des constructions précédentes : 
gradlew clean
gradlew cleandist

Pour faire le javadoc : gradlew javadoc

Pour faire les tests : gradlew tests

Pour faire les groovydocs avec l'encodage correct : gradlew -Dfile.encoding=utf-8 groovydoc

Ensuite une fois qu'on a fait tout ça on peut lancer la construction finale.

Pour construire le projet et construire la distribution : gradlew dist

La distribution se trouve dans le sous-repertoire "dist".

Pour produire le zip :

Vérifier que dans "dist" il n'y a que le répertoire de sortie (par ex. v2.1).
Effacer tout zip qui y resterait.

Lancer la commande de production du zip : gradlew zip

On la trouve le résultat dans build\distributions, exemple :
build\distributions\gpmsi-2.1.zip

--------------------------------------------------------------------------------
N.B. construction locale (lorsqu'on développe la distribution elle-même de manière
répétée)

Pour construire et distribuer sur c:\app\gpmsi directement, exécuter un batch local.
J'ai créé pour mon usage perso : gradle-gpmsi
(c'est un batch local qui appelle : gradlew dist
puis fait des copies vers c:\app\gpmsi 
On peut faire un batch similaire qui simplifie les cycles de compilation+déploiement locaux)

Idem mais recopie sur le répertoire partagé qui est sur h: : gradle-gpmsi h
(fait des copies supplementaires sur H: qui est un répertoire partagé de l'hôpital)


Contenu du batch gradle-gpmsi.bat :
--------------------------------------------------------------------------------
rem contruction et deploiement gpmsi
rem si le premier argument est "h", recopie aussi le resultat sur le lecteur h

setlocal enableextensions
echo on
set GPMSI_VER=2.1
set PREV_DIR=%CD%
c:
cd c:\hkgh\gpmsi
call gradlew dist
md c:\app\gpmsi\v%GPMSI_VER%
xcopy C:\hkgh\gpmsi\dist\v%GPMSI_VER% c:\app\gpmsi\v%GPMSI_VER% /S/E/V/D/Y
if not "%1" == "h" goto :done
md H:\partage_intersite\ADMINISTRATION\DIM\OUTILS\gpmsi\v%GPMSI_VER%
echo demarrage copie
echo on
xcopy C:\hkgh\gpmsi\dist\v%GPMSI_VER% H:\partage_intersite\ADMINISTRATION\DIM\OUTILS\gpmsi\v%GPMSI_VER%\ /S/E/V/D/Y
:done
cd %PREV_DIR%
--------------------------------------------------------------------------------









