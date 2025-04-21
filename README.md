<!--☺:encoding=UTF-8:-->

# Groovy PMSI

## Présentation

Gpmsi est un environnement (ensemble de programmes, fichiers et librairies) pour
permettre de scripter (à l'aide de [Groovy](https://groovy-lang.org/)) certaines opérations du PMSI afin de les automatiser. Un des objectifs principaux est de partager facilement les scripts au sein de l'équipe du DIM local ou entre DIMs.

Important : **l'encodage des fichiers est UTF-8** (y compris les sources java et Groovy), SAUF POUR :

- Les fichiers batch windows
- Certains fichiers test et .csv

**Il faut donc utiliser un éditeur qui utilise l'encodage UTF-8**.  
Le bloc-notes de Windows ne le fait pas.

Utiliser un autre éditeur, par exemple [jEdit](https://www.jedit.org)
ou [Notepad++](https://notepad-plus-plus.org/)

Une astuce est de mettre vers le début du fichier encodé en UTF-8 le caractère de smiley : ☺
Ainsi on peut voir du premier coup d'oeil si l'encodage UTF-8 est bien respecté ou non.

## Installation

Récupérer l'archive de distribution `gpmsi-v2.1.zip` depuis GitHub, en allant chercher dans "Releases".

L'installation se fait en dézippant l'archive de distribution dans le répertoire `C:\app\gpmsi` (à créer si n'existe pas)
Il doit y avoir un JDK installé, au minimum version 1.8. OpenJdk convient et fonctionne aussi.
Il n'y a pas besoin d'être administrateur pour installer gpmsi.
Pour plus d'informations, lire le fichier `INSTALLATION.TXT`

## Exécution

L'exécution du script `C:\app\gpmsi\v2.1\gpmsi.bat` dépend de la commande `java`. La méthode la plus
fiable est de définir la variable d'environnement `JAVA_HOME` (en tant que variable utilisateur cela suffit, il 
n'y a pas besoin de la définir en variable système) pour qu'elle pointe sur le JDK que l'on
veut utiliser avec gpmsi.

L'exécution de scripts gpmsi ne nécessite pas d'avoir des droits administrateurs sur la machine.

## Documentation

Il y a un peu de documentation pour installer gpmsi et démarrer, mais
la documentation principale est dans les fichiers javadoc, qui sont extraits directement
du code source, et qui est à jour.
Le reste de la documentation est souvent en retard par rapport à la version distribuée,
mais en constante amélioration ...

Les fichiers de documentation sont dans le répertoire `docs` et ses sous-répertoires.
(la documentation qui est sur GitHub dans /doc n'est pas lisible, elle est destinée à être
ouverte dans le répertoire local où elle est installée. Cependant, sur github pages
il y a une copie de la documentation dans [https://hkara2.github.io/gpmsi/](https://hkara2.github.io/gpmsi/) )

Il faut ouvrir le fichier index.html dans ce répertoire, il donne accès aux documentations
javadoc de gpmsi, et aussi des librairies qui le composent. 

On peut depuis peu trouver aussi cet accès sur github pages, à l'adresse [https://hkara2.github.io/gpmsi/](https://hkara2.github.io/gpmsi/) .

Il y a également un livre (gratuit) sur gpmsi, mais ce livre est encore en cours d'écriture.

En attendant, avec les javadocs, les scripts fournis en exemple et les classes de test on
arrive quand même à bien comprendre comment cela fonctionne.

## Licence

Cet environnement (librairies, scripts, programmes) est distribué sous license Apache 2.0.
Cette licence peut être retrouvée à la racine du projet, et également dans le 
sous-répertoire `doc`.

## Avertissement

Cet environnement est distribué par Harry Karadimas uniquement, et ne dépend pas de,
n'engage pas, une institution telle que l'Assistance Publique Hôpitaux de Paris
ou le Centre Hospitalier Sud Essonne.
Comme indiqué dans la license, 
**la responsabilité du ou des auteurs ne saurait être engagée d'aucune façon**
, et l'utilisation de cet environnement se fait 
**aux risques et périls de l'utilisateur** qui télécharge et utilise l'environnement gpmsi.


