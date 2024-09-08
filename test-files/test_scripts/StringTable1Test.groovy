package fr.gpmsi.tests
import fr.gpmsi.StringTable

content = '''foo;bar;baz
x;1;11
y;2;22
z;3;33
'''

st = new StringTable()
st.readFrom(new StringReader(content), ';' as char)

v_1_1 = st.getValue(1,1)
println "(1,1) -> $v_1_1"

//fait une sélection sur 2 critères
sel = st.selectRows {row->
  row.foo == 'z' || row.bar == '2'
}

println "selection : $sel"

