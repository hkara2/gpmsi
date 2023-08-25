
val = null

xlpoi {
  onInit {
    //println "Testing the opening of Excel file inside nxpoi step"
    input "test-files/in/xl1.xlsx"
  }
  onItem {item->
    linenr = item.linenr
    if (linenr == 1) {
      val = item.row.columnCount
    }
  }
  onEnd {
    //println "end of file reached."
  }
}

return val
