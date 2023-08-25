
val = null

xlpoi {
  input "test-files/in/xl1.xlsx"
  onItem {item->
    linenr = item.linenr
    if (linenr == 6) {
      val = item.row[7] //c'est la colonne 8 si on commence à 1
    }
  }
}

return val
