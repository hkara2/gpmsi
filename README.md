<!--☺:encoding=UTF-8:-->

# Groovy PMSI

## Présentation

Gpmsi est un environnement (ensemble de programmes, fichiers et librairies) pour
permettre de scripter (à l'aide de [Groovy](https://groovy-lang.org/)) certaines opérations du PMSI afin de les automatiser. Un des objectifs principaux est de partager facilement les scripts au sein de l'équipe du DIM local ou entre DIMs.

Il est possible par exemple de :
- transformer au format csv des fichier RSS, RSA, VIDHOSP, RHS, FICHCOMP, RSF, RPU, en faisant glisser le fichier sur le raccourci approprié
- transformer un fichier csv au format xlsx avec tous les champs au format texte (très utile pour éviter l'assistant pénible de excel)
- faire des choses plus spécialisées, comme :
  - extraire des numéros de dossiers d'un fichier VIDHOSP
  - repérer les séjours à cheval sur 2 années
  - rattacher à des RPUs le numéro de dossier (nécessite un fichier csv avec les infos des séjours d'urgence)
  - croiser un fichier de RSS ou de RHS avec le VIDHOSP, pour repérer les séjours qui manqueraient dans l'un ou l'autre des fichiers
  - réparer les RHS des séjours où le patient est sorti un lundi, mais le DPI n'a pas bien généré le fichier (c'est le cas pour DxCare, par exemple)
  - fusionner plusieurs fichiers RHS (par exemple pour DxCare le fichier des RHS contenant uniquement les fins de séjour avec le fichier des RHS contenant les séjours en cours, mais sans ceux qui ont commençé l'année précédente)
  - travailler les RPUs (découpages, fusions, ...) en vue d'un export vers le SESAN
  - désanonymiser un fichier de RSA à l'aide du TRA correspondant
- faire tout un tas de scripts maison pour faire face à des situations spécifiques de réparations ou visualisations de fichiers.   

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

### Méthode simple utilisant l'installeur

Vérifier que java est installé (un JRE suffit, OpenJDK fonctionne aussi), minimum version 1.8 .

Récupérer le fichier installeur en allant chercher `gpmsi-2.1.3-install.jar` dans "Releases".

Double-cliquer sur le fichier; cela lancera l'installeur.

### Méthode plus robuste en utilisant le fichier zip de distribution

Récupérer l'archive de distribution `gpmsi-v2.1.3.zip` depuis GitHub, en allant chercher dans "Releases".

Dézipper l'archive de distribution dans le répertoire `C:\app\gpmsi` (à créer si n'existe pas)

Ouvrir le répertoire `C:\app\gpmsi\v2.1` , on doit y trouver un fichier `menu.bat` , double-cliquer sur `menu.bat` cela doit lancer le menu de gpmsi.

Il doit y avoir un JRE installé, au minimum version 1.8.

OpenJre (ou OpenJdk) convient et fonctionne aussi.

Il n'y a pas besoin d'être administrateur pour installer gpmsi.

Pour plus d'informations, lire le fichier `INSTALLATION.TXT`

## Exécution

L'exécution du script `C:\app\gpmsi\v2.1\gpmsi.bat` dépend de la commande `java`. La méthode la plus
fiable pous que `gpmsi.bat` trouve java est de définir la variable d'environnement `JAVA_HOME` (en tant que variable utilisateur cela suffit, il 
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

Il faut ouvrir le fichier `index.html` dans ce répertoire, il donne accès aux documentations
javadoc de gpmsi, et aussi des librairies qui le composent. 

On peut depuis peu trouver aussi cet accès sur github pages, à l'adresse [https://hkara2.github.io/gpmsi/](https://hkara2.github.io/gpmsi/) .

Il y a également un livre (gratuit) sur gpmsi, mais ce livre est encore en cours d'écriture.

Le [site de pmsixml](https://github.com/hkara2/pmsixml) (qui est ce qui a démarré gpmsi, et qui décode les fichiers) est instructif également.

En attendant, avec les javadocs, les scripts fournis en exemple et les classes de test on
arrive quand même à bien comprendre comment cela fonctionne.

## Licence

Cet environnement (librairies, scripts, programmes) est distribué sous license **Apache 2.0**.
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



