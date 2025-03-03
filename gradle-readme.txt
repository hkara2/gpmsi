☺:encoding=UTF-8: 

Pour construire et construire la distribution : gradle jar dist

Pour construire et distribuer sur c:\app\gpmsi directement, exécuter un batch local.
J'ai créé pour mon usage perso : gradle-gpmsi
(c'est un batch local qui appelle : gradle jar dist
puis fait des copies vers c:\app\gpmsi 
On peut faire un batch similaire qui simplifie les cycles de compilation+déploiement locaux)

Idem mais recopie sur le répertoire partagé qui est sur h: : gradle-gpmsi h
(fait des copies supplementaires)

Pour faire le javadoc : gradle javadoc

Pour faire les tests : gradle tests


Contenu du batch gradle-gpmsi.bat :
--------------------------------------------------------------------------------
rem contruction et deploiement gpmsi
rem si le premier argument est "h", recopie aussi le resultat sur le lecteur h

setlocal enableextensions
echo on
set GPMSI_VER=1.0
set PREV_DIR=%CD%
c:
cd c:\hkgh\gpmsi
call gradle jar dist
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

