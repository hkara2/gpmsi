
val = null

xlpoi {
  input "test-files/in/xl1.xlsx"
  onItem {item->
    item.row.newJavaTimeUsed = true //ramener des objets LocalDateTime plutot que des Date  
    if (item.linenr == 3) val = item.row.getCellObject('d with space')
  }
}

return val
