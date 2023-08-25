
nbLignes = 0

xlpoi {
  onInit {
    println "Testing the opening of Excel file inside nxpoi step"
    input "test-files/in/xl1.xlsx"
  }
  onItem {item->
    nbLignes = item.linenr
  }
  onEnd {
    println "end of file reached."
  }
}

return nbLignes
