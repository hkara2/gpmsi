
val = null

xlpoi {
  input "test-files/in/xl1.xlsx"
  onItem {item-> if (item.linenr == 5) val = item.row.empty}
}

return val
