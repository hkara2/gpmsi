package fr.gpmsi.da

/**
 * Classe utilitaire pour gérer la correspondance entre un nom de colonne et
 * un autre, par exemple un nom de colonne Excel et un nom de colonne de base de données
 */
class ColumnMapper {
    String[] sourceColumnNames
    String[] targetColumnNames
    def targetColumnNamesBySourceName = [:]
    
    /**
     * Constructeur
     * @param sourceColumnNames Liste des colonnes source
     * @param targetColumnNames Liste des colonnes cible
     */
    ColumnMapper(List<String> sourceColumnNames, List<String> targetColumnNames) {
        this.sourceColumnNames = sourceColumnNames
        this.targetColumnNames = targetColumnNames
        sourceColumnNames.eachWithIndex {name, index ->
            targetColumnNamesBySourceName[name] = targetColumnNames[index]
        }
    }
    
    /**
     * Trouver la colonne cible à partir du nom de la colonne source
     * @param sourceColumnName Le nom de la colonne source
     * @return le nom de la colonne cible ou null si non trouvé
     */
    String getTargetColumnName(String sourceColumnName) {
        return targetColumnNamesBySourceName[sourceColumnName]
    }
    
}
