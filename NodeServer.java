import Eleccion.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

/**
 *
 * @author Miguel Alejandro
 */
public class NodeServer {

	static int uid;
	static int numberOfClients;

    static public String getParam(String[] args, String arg) {
    	String result=null;
        for (int i=0; i<args.length; i++) {
        	if (args[i].equals("-"+arg) || args[i].equals("--"+arg)) {
        		result=args[i+1];
        		break;
        	}
        }
        return result;
    }
	
 public static void main (String args []) {
 
 
 try{
 
  // Extract the -id parameter
  uid=Integer.parseInt(getParam(args,"id"));
  numberOfClients=Integer.parseInt(getParam(args,"n"));
 
  //crea e inicializa el ORB
  ORB orb = ORB.init(args, null);

  // consigue la referencia al rootpoa y activa el POAmanager   
  POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
  rootpoa.the_POAManager().activate();

  //crea el servant y registra este con el ORB
  NodeServant ns = new NodeServant(uid);
  rootpoa.activate_object(ns);
  //consigue referenciar el objeto del servant
  org.omg.CORBA.Object ref = rootpoa.servant_to_reference (ns);
  Node href = NodeHelper.narrow(ref);

  // consigue el contexto nombrado del root
  org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
  NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

  // el objeto referenciado en nombre

	NameComponent nc = new NameComponent("node"+uid,"");
    NameComponent component[] = {nc};
	System.out.println("Registering client: "+ uid);
    ncRef.rebind(component, href);

	
	  String input;
	  java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
	
	  // Wait until all remote objects are activated.
	  System.out.println("Wait until all clients show this message, and press enter to initialize");
	  input = in.readLine();
	

	if(uid != 0){
			NameComponent nc1 = new NameComponent("node"+(uid-1),"");
			NameComponent path[] = {nc1};
			org.omg.CORBA.Object objRef1 = ncRef.resolve(path);
			Node vecino = NodeHelper.narrow(objRef1);
			vecino.register(href);
	} 
	
	
	if ( uid == numberOfClients - 1) {
			NameComponent nc2 = new NameComponent("node0","");
			NameComponent path2[] = {nc2};
			org.omg.CORBA.Object objRef2 = ncRef.resolve(path2);
			Node vecino2 = NodeHelper.narrow(objRef2);
			href.register(vecino2);
	}
	
	
   System.out.println("El servidor esta listo y esperando....");
  // espere para la invocacion del cliente
  orb.run();
 } catch (Exception e) {
	System.err.println ("Error: " + e);
	e.printStackTrace(System.out);
 }
 System.out.println("NodeServer Exiting...");
 }
}