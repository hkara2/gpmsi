package fr.karadimas.gpmsi.da

/**
 * Classe utilitaire pour g�rer la correspondance entre un nom de colonne et
 * un autre
 */
class ColumnMapper {
    String[] sourceColumnNames
    String[] targetColumnNames
    def targetColumnNamesBySourceName = [:]
    
    ColumnMapper(List<String> sourceColumnNames, List<String> targetColumnNames) {
        this.sourceColumnNames = sourceColumnNames
        this.targetColumnNames = targetColumnNames
        sourceColumnNames.eachWithIndex {name, index ->
            targetColumnNamesBySourceName[name] = targetColumnNames[index]
        }
    }
    
    String getTargetColumnName(String sourceColumnName) {
        return targetColumnNamesBySourceName[sourceColumnName]
    }
    
}
