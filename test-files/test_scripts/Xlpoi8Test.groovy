
val = null

xlpoi {
  input "test-files/in/xl1.xlsx"
  onItem {item-> if (item.linenr == 3) val = item.row.getCellObject('d with space')}
}

return val
