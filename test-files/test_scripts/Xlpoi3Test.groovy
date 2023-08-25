
val3 = null

xlpoi {
  onInit {
    //println "Testing the opening of Excel file inside nxpoi step"
    input "test-files/in/xl1.xlsx"
  }
  onItem {item->
    linenr = item.linenr
    if (linenr == 1) return; //ignorer premiere ligne
    v = item.row.é //colonne 'é' doit être trouvée
    if (linenr == 3) val3 = v
  }
  onEnd {
    //println "end of file reached."
  }
}

return val3
