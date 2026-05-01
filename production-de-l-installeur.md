# Production de l'installeur install.jar

Si on reconstruit la distribution gpmsi par la commmande `gradle dist`
on peut maintenant produire un installer IzPack à l'aide du script
`IzPackInstallation.xml` .

Le programme IzPack est à installer séparément. On le trouve sur le site : \
https://izpack.org/

Avec IzPack dans `C:\Program Files\IzPack` , voici la commande pour le lancement :

    cd  C:\hkgh\gpmsi
    "c:\Program Files\IzPack\bin\compile.bat" IzPackInstallation.xml -h "c:\Program Files\IzPack" -b . -o local\gpmsi-2.1.6-install.jar -k standard

L'installeur sera dans le sous-répertoire `local\` . \
(Ce sous-répertoire est ignoré de Git)

