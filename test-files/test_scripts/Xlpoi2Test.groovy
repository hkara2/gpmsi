
val4 = null

xlpoi {
  onInit {
    //println "Testing the opening of Excel file inside nxpoi step"
    input "test-files/in/xl1.xlsx"
  }
  onItem {item->
    linenr = item.linenr
    if (linenr == 1) return; //ignorer premiere ligne
    v = item.row.B
    if (linenr == 4) val4 = v
  }
  onEnd {
    //println "end of file reached."
  }
}

return val4
