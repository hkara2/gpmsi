rem A lancer dans un repertorie qui contient les fichiers d'atypies
rem exportes a partir de ePMSI / OVALIDE / ATIH
rem Leur extension doit etre .txt
rem Le parametre 1 doit etre le fichier .out.zip produit par GENRSA
rem Exemple :
rem cd C:\Local\GROUPAGE\2021\M06\210720\DXC\GENRSA
rem C:\hkchse\dev\pmsixml\doc\exemples-de-script\atih-atypies-trouver-nda\atih-atypies-trouver-nda.bat "C:\Users\hkaradimas\AppData\Roaming\ATIH\GENRSA\sauvegarde\910019447.2021.0.21072021225220.out.zip"
setlocal enableextensions
if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

md avec_ndas
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rsa_reperage_atih_sur_fichier_out.groovy
set OUTZIP=%1

for %%I in (*.txt) do %GPMSI_HOME%\gpmsi.bat -script "%SCRIPT%" -a:input_atih "%%I" -a:input_outzip "%OUTZIP%" -a:output "avec_ndas\%%I"




