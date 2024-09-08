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
  
    public String getName() {
	  return name;
    }
	public void setName(String name) {
		this.name = name;
	}
	public String getStreet() {
		return theStreet;
	}
	public void setStreet(String street) {
		this.theStreet = street;
	}
	public String getTown() {
		return theTown;
	}
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
	@Override
	public String toString() {
		return "name:"+name+",street:"+theStreet+",town:"+theTown;
	}
}
