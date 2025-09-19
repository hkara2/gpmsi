@rem Lancement de gpmsi @PROJECT_VERSION@@PROJECT_SUB_VERSION@
@rem Pour les parametres que l'on peut utiliser voir le javadoc de fr.gpmsi.Groovy
@rem
@rem On peut definir des chemins de librairies supplementaires dans la variable GPMSI_XCP qui est ajoutee a la fin du classpath.
@rem C'est utile par exemple pour ajouter des classes de support pour une connexion a d'autres bases de donnees (Oracle par ex.)
@rem ou toute autre librairies supplementaire a utiliser localement.
@rem On peut definir des directives java supplementaires dans la variable GPMSI_XJD qui est ajoutee dans la ligne de commande.
@rem Attention a la longueur totale de la ligne de commande qui ne doit pas depasser 8191 caracteres
@rem On se met en mode "setlocal" pour ne pas modifier les variables de la session appelante
@setlocal enableextensions
@rem La variable GPMSI_HOME contient le chemin du repertoire qui contient ce script.
@rem cette variable est disponible dans les scripts Groovy
@rem N.B. cette definition ecrase la definition prealable GPMSI_HOME juste le temps de ce script.
@set GPMSI_HOME=%~dp0
@rem On enleve le '/' final
@set GPMSI_HOME=%GPMSI_HOME:~0,-1%
@rem Le classpath CP commence par le repertoire resources du profil utilisateur, il a priorite sur le reste 
@set CP=%USERPROFILE%\.gpmsi\resources
@set CP=%CP%;%GPMSI_HOME%\lib\*

@if defined GPMSI_XCP set CP=%CP%;%GPMSI_XCP%
@if "%JAVA_HOME%" == "" goto :nojhome
@rem JAVA_HOME est fourni, l'utiliser
@ @START_JAVA@ "%JAVA_HOME%\bin\@JAVA_COMMAND@.exe" %GPMSI_XJD% -classpath "%CP%" fr.gpmsi.Groovy %*
@goto :end
@
@:nojhome
@rem Pas de JAVA_HOME, on utilise la commande "java" quelle qu'elle soit.
@ @START_JAVA@ @JAVA_COMMAND@ %GPMSI_XJD% -classpath "%CP%" fr.gpmsi.Groovy %*
@
@:end
