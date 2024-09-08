@rem Lancement de gpmsi @PROJECT_VERSION@@PROJECT_SUB_VERSION@
@rem Pour les parametres que l'on peut utiliser voir le javadoc de fr.gpmsi.Groovy
@rem
@rem On peut definir des chemins de librairies supplementaires dans la variable GPMSI_XCP qui est ajoutee a la fin du classpath.
@rem C'est utile par exemple pour ajouter des classes de support pour une connexion a d'autres bases de donnees (Oracle par ex.)
@rem ou toute autre librairies supplementaire a utiliser localement.
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
@set CP=%CP%;%GPMSI_HOME%\lib\@GPMSILIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@PMSIXMLLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@AAGBLLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@POILIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@POISCRATCHPLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@POIOOXMLLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@POIOOXMLLITLIB@.jar
@rem set CP=%CP%;%GPMSI_HOME%\lib\@POIOOXMLSCHLIB@.jar
@rem set CP=%CP%;%GPMSI_HOME%\lib\@OOXMLSCHEMASLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@JAVADBFLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CBEANLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CCLILIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CCODECLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CCOLLLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@COMMONSCOMPRESSLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@COMMONSIOLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CLANGLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@COMMONSMATHLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CNETLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CTEXTLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@CLOGLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@LOG4J2CORELIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@LOG4J2APILIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@LOG4J2_1_2_APILIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@H2LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@SLF4JSIMPLELIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@SLF4JAPILIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAPIBASELIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAPISTRUCTURESV23LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAPISTRUCTURESV231LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAPISTRUCTURESV24LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAPISTRUCTURESV25LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAPISTRUCTURESV251LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@JUNITLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@OPENCSVLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@HAMCRESTCORELIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@XBEANLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@SPARSEBITSETLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@ANTLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@ANT_ANTLRLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@ANT_ANTLAUNCHERLIB@.jar

@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVYLIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_ASTBUILDER_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_CLI_COMMONS_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_CLI_PICOCLI_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_CONSOLE_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_DATETIME_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_DATEUTIL_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_GROOVYSH_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_MACRO_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_MACRO_LIBRARY_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_JSON_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_NIO_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_SQL_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_SWING_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_TEMPLATES_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_XML_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_YAML_LIB@.jar
@set CP=%CP%;%GPMSI_HOME%\lib\@GROOVY_ANT_LIB@.jar
@rem set CP=%CP%;%GPMSI_HOME%\lib\@SLF4JLOG4J12LIB@.jar

@if defined GPMSI_XCP set CP=%CP%;%GPMSI_XCP%
@if "%JAVA_HOME%" == "" goto :nojhome
@rem JAVA_HOME est fourni, l'utiliser
@ @START_JAVA@ "%JAVA_HOME%\bin\@JAVA_COMMAND@.exe" -classpath "%CP%" fr.gpmsi.Groovy %*
@goto :end
@
@:nojhome
@rem Pas de JAVA_HOME, on utilise la commande "java" quelle qu'elle soit.
@ @START_JAVA@ @JAVA_COMMAND@ -classpath "%CP%" fr.gpmsi.Groovy %*
@
@:end
