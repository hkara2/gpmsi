package fr.gpmsi.groovytests;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class MyAddresses extends Script {
  ArrayList<MyAddress> allAddresses = new ArrayList<>();
  public void addresses(MyAddress[] adrs) { System.out.println("Got "+(adrs.length)+" adresses"); }
  public Closure<?> printAddressClosure;
  
  public void address(String name, String street, String town) {
	  System.out.println("Adding address using "+name+", "+street+","+town);
	  MyAddress newAddr = new MyAddress();
	  newAddr.setName(name);
	  newAddr.setStreet(street);
	  newAddr.setTown(town);
	  allAddresses.add(newAddr);
	  getBinding().setVariable("allAddresses", allAddresses); //also set as a binding variable
  }

  public List<MyAddress> getAllAddresses() { return allAddresses; }
  
  public void doClosure(Closure<?> dehydratedClos) {
	  Closure<?> clos = dehydratedClos.rehydrate(this, this, this);
	  clos.call();
  }
  
  public void declareAddress(Closure<?> dehydratedClosure) {
	  MyAddress newAddress = new MyAddress();
	  Closure<?> clos = dehydratedClosure.rehydrate(newAddress, this, this);
	  clos.setResolveStrategy(Closure.DELEGATE_ONLY);
	  clos.call();
	  allAddresses.add(newAddress);
	  getBinding().setVariable("allAddresses", allAddresses); //also set as a binding variable	  
  }
  
  public void printAddress(Closure<?> dehydratedClosure) {
	printAddressClosure = dehydratedClosure.rehydrate(this, this, this);  
  }
  
  @SuppressWarnings("unchecked")
  public void printAllAddresses() {
	  allAddresses = (ArrayList<MyAddress>) getBinding().getVariable("allAddresses"); //get back all addresses
	  System.out.println("Printing "+allAddresses.size()+" addresses");
	  for (MyAddress addr : allAddresses) {
		printAddressClosure.call(addr);
	}
  }
  
}
