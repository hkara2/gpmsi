
val = null

dbf {
  input "test-files/in/R_ACTE_FORFAIT.dbf"
  onItem {item->
    if (item.linenr == 6) val = item.row.ACDT_MODIF
  }
}

return val
