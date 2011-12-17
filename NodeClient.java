import Eleccion.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.io.*;

/**
 *
 * @author Miguel Alejandro
 */
public class NodeClient {

public static void main (String args []) {
 try{
    //crea e inicializa el orb
    ORB orb = ORB.init (args, null);
    org.omg.CORBA.Object objRef = orb.resolve_initial_references ("NameService");
    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	NameComponent nc1 = new NameComponent("node0", "");
    NameComponent path[] = {nc1};
	Node n1 = NodeHelper.narrow(ncRef.resolve(path));
	/*
	NameComponent nc11 = new NameComponent("node1", "");
    NameComponent path1[] = {nc11};
	Node n2 = NodeHelper.narrow(ncRef.resolve(path1));
	NameComponent nc2 = new NameComponent("node2", "");
    NameComponent path2[] = {nc2};
	Node n3 = NodeHelper.narrow(ncRef.resolve(path2));
	NameComponent nc3 = new NameComponent("node3", "");
    NameComponent path3[] = {nc3};
	Node n4 = NodeHelper.narrow(ncRef.resolve(path3));
	
	n1.dameid();
		n2.dameid();
	n3.dameid();

		n4.dameid();

	*/
	n1.election();
	int leader = n1.getLeader();
	System.out.println("El lider fue: "+leader);

	
    }catch (Exception e) {
    System.out.println("ERROR:"+ e);
    e.printStackTrace(System.out);
    }
}
}

