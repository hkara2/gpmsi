package fr.gpmsi.groovytests;

/**
 * Objet pour tester les syntaxes de closure.
 * @author hkaradimas
 *
 */
public class MyAddress {
    String name;
    String theStreet;
    String theTown;
  
    /**
     * getter
     * @return name
     */
    public String getName() {
	  return name;
    }
    
    /**
     * setter
     * @param name Le nom
     */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * getter
	 * @return street
	 */
	public String getStreet() {
		return theStreet;
	}
	
	/**
	 * setter
	 * @param street La street
	 */
	public void setStreet(String street) {
		this.theStreet = street;
	}
	
	/**
	 * getter
	 * @return town
	 */
	public String getTown() {
		return theTown;
	}

	/**
	 * setter
	 * @param town La town
	 */
	public void setTown(String town) {
		this.theTown = town;
	}
	
	/**
	 * Permet de déclarer une ville par <code>town</code>.
	 * Donc dans une <i>closure</i> cela fait <code>town 'Washington'</code> par exemple.
	 * @param townStr Rue
	 */
	public void town(String townStr) {
		this.theTown = townStr;
	}
	
	/**
	 * Conversion en String
	 */
	@Override
	public String toString() {
		return "name:"+name+",street:"+theStreet+",town:"+theTown;
	}
}
