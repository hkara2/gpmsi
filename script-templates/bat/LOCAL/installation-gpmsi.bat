rem Installation des fichiers locaux de .gpmsi

setlocal EnableExtensions

rem Calculer le GPMSI_HOME
set GPMSI_HOME=%~dp0
rem Enlever \scripts\bat\LOCAL\ (19 caracteres)
set GPMSI_HOME=%GPMSI_HOME:~0,-19%
if not "_" == "_%GPMSI_HOME%" goto :gpmsiok
  set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
:gpmsiok

rem Creation du dossier utilisateur .gpmsi
md %USERPROFILE%\.gpmsi

rem Installation des fichiers de nomenclature utilises dans les commandes
md %USERPROFILE%\.gpmsi\cim
md %USERPROFILE%\.gpmsi\ccam

copy %GPMSI_HOME%\fichiers-ref\cim\cim*.csv %USERPROFILE%\.gpmsi\cim\
copy %GPMSI_HOME%\fichiers-ref\ccam\ccam*.csv %USERPROFILE%\.gpmsi\ccam\

@echo.
@echo --Fin du fichier d'installation--

pause
