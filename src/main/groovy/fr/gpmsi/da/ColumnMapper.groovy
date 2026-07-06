package fr.gpmsi.da

/**
 * Classe utilitaire pour gérer la correspondance entre un nom de colonne et
 * un autre, par exemple un nom de colonne Excel et un nom de colonne de base de données
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
