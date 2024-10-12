package fr.gpmsi.groovytests;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;
import groovy.lang.Script;

/**
 * Classe de script pour gérer des objets de type MyAddress
 */
public abstract class MyAddresses
extends Script
{
	
  ArrayList<MyAddress> allAddresses = new ArrayList<>();
  
  /**
   * méthode "addresses"
   * @param adrs Les adresses
   */
  public void addresses(MyAddress[] adrs) { System.out.println("Got "+(adrs.length)+" adresses"); }
  
  /**
   * La closure pour printAddressClosure
   */
  public Closure<?> printAddressClosure;
  
  /**
   * Méthode "address" avec arguments, qui ajoute l'adresse dans "allAddresses" et la met dans le "binding".
   * @param name Nom
   * @param street Rue
   * @param town Ville
   */
  public void address(String name, String street, String town) {
	  System.out.println("Adding address using "+name+", "+street+","+town);
	  MyAddress newAddr = new MyAddress();
	  newAddr.setName(name);
	  newAddr.setStreet(street);
	  newAddr.setTown(town);
	  allAddresses.add(newAddr);
	  getBinding().setVariable("allAddresses", allAddresses); //also set as a binding variable
  }

  /**
   * Retourner la variable "allAddresses"
   * @return la variable "allAddresses"
   */
  public List<MyAddress> getAllAddresses() { return allAddresses; }

  /**
   * Exécuter la closure
   * @param dehydratedClos La closure sous forme "déshydratée"
   */
  public void doClosure(Closure<?> dehydratedClos) {
	  Closure<?> clos = dehydratedClos.rehydrate(this, this, this);
	  clos.call();
  }
  
  /**
   * Déclarer l'adresse dans la closure fournie
   * @param dehydratedClosure La closure à exécuter avec l'adresse
   */
  public void declareAddress(Closure<?> dehydratedClosure) {
	  MyAddress newAddress = new MyAddress();
	  Closure<?> clos = dehydratedClosure.rehydrate(newAddress, this, this);
	  clos.setResolveStrategy(Closure.DELEGATE_ONLY);
	  clos.call();
	  allAddresses.add(newAddress);
	  getBinding().setVariable("allAddresses", allAddresses); //also set as a binding variable	  
  }
  
  /**
   * Déclarer la closure qui fera "printAddress"
   * @param dehydratedClosure La closure
   */
  public void printAddress(Closure<?> dehydratedClosure) {
	printAddressClosure = dehydratedClosure.rehydrate(this, this, this);  
  }
  
  /**
   * Impression de toutes les adresses à l'aide de la closure déclarée via "printAddress"
   */
  @SuppressWarnings("unchecked")
  public void printAllAddresses() {
	  allAddresses = (ArrayList<MyAddress>) getBinding().getVariable("allAddresses"); //get back all addresses
	  System.out.println("Printing "+allAddresses.size()+" addresses");
	  for (MyAddress addr : allAddresses) {
		printAddressClosure.call(addr);
	}
  }
  
}
