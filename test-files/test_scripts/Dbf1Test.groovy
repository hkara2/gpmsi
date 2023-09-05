
nbLignes = 0

dbf {
  onInit {
    println "Test de l'ouverture du dbf dans le step"
    input "test-files/in/R_ACTE_FORFAIT.dbf"
  }
  onItem {item->
    nbLignes = item.linenr
  }
  onEnd {
    println "fin de fichier atteinte."
  }
}

return nbLignes
