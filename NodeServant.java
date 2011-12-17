import Eleccion.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.*;
import java.util.Properties;
import java.math.*;

/**
 *
 * @author Miguel Alejandro
 */
public class NodeServant extends NodePOA {
    private Vector clients = new Vector();
    private int uid, vencedor,pid;
	private int uids[];
	private String mode, status;
	private SimpleQueue send = new SimpleQueue();
	private SimpleQueue receive = new SimpleQueue();
	
    public NodeServant(int uid){
		this.uid = uid;
		this.mode = "active";
		this.status = "unknown";
		this.uids = new int[3];
		this.uids[0] = uid;
		this.uids[1] = -1;
		this.uids[2] = -1;
		this.send.put(uid);
    }

    public int getLeader(){
		return this.vencedor;
    }
	
	public int getSencondUID() {
		if ( mode.equals("active") && receive.isEmpty() == false && uids[1] == -1  ) {
			uids[1] = (Integer)receive.get();
			send.put(uids[1]);
			//send(uids[1]);
			System.out.println("Se obtuvo second UID");
			if ( uids[1] == uids[0] ){
				status = "chosen";
				System.out.println("Status: "+status);
			}
			return 1;
		} else {
			return 0;
		}
	}
	
	public void getThirdUID() {
		if ( mode.equals("active") && receive.isEmpty() == false && uids[1] != -1 && uids[2] == -1  ) {
			uids[2] = (Integer)receive.get();
			System.out.println("Se obtuvo third UID");
		}
	}

	public int advancePhase() {
		if(  mode.equals("active") && uids[2] != -1 && uids[1] > Math.max(uids[0], uids[2]) ) {
			uids[0] = uids[1];
			uids[1] = -1;
			uids[2] = -1;
			System.out.println("avanza fase");
			send.put(uids[0]);	
			return 1;
		} else {
			return 0;
		}
	}
	
	public void becomeRelay() {
		if(  mode.equals("active") && uids[2] != -1 && uids[1] <= Math.max(uids[0], uids[2]) ) {
			mode = "relay";
			System.out.println("mode: relay, ahora. sorry mate! :(");
		}
	}
	
	public void relay() {
		if( mode.equals("relay") && receive.isEmpty() == false ) {
			int x = (Integer)receive.get();
			send.put(x);
			System.out.println("Mandando mensaje relay");
			send(x);
		}
	}
	
	public void send(int v){
		if (send.isEmpty() == false) {
			if(v == send.peek() ){
				int x = (Integer)send.get();
				sendmessage("election:"+x);
			}
		}
	}
	
	public int leader(){
		if(status.equals("chosen")) {
			status = "reported";
			System.out.println("Status: "+status);
			return 1;
		} else {
			return 0;
		}
	}
	
    public void election() {
		//System.out.println("Proceso de eleccion iniciado");
		//System.out.println("Mandando mensaje de eleccion "+uids[0]);
		while (!mode.equals("relay")) {
			send(uids[0]);
			if (getSencondUID() == 1) {
				send(uids[1]);
			}
			getThirdUID();
			if( advancePhase() == 1){
				send(uids[0]);
			}
			becomeRelay();
		}
		while(!status.equals("reported") ){
			relay();
			if ( leader() == 1){
				System.out.println("Mandando mensaje de lider " + uids[1]);
				sendmessage("leader:"+uids[0]); 
				vencedor = uids[0];
				System.out.println("Gane, soy el proceso " + uid + " con uid virtual " + uids[1]);	
			}
		}
	}
	
    public void register(Node nodo) {
		clients.add(nodo);
    }
	
	public void removeNode(Node nodo) {
		clients.remove(nodo);
    }

    public void sendmessage(String msg) {
		Iterator it = clients.iterator();
		while (it.hasNext()) {
			Node ms = (Node)it.next();
			ms.message(msg);
		}
    }
	
	public void message(String msg) {
		String[] temp;
		String delimiter = ":";
		temp = msg.split(delimiter);
		if( temp[0].equals("election") ){
			pid = Integer.parseInt(temp[1]);
			System.out.println("Se recibe mensaje en "+uid+" pid= "+pid);
			receive.put(pid);
			election();
		} else if( temp[0].equals("leader")  ){
			pid = Integer.parseInt(temp[1]);
			if ( uids[1] != pid ){
				status = "reported";
				System.out.println("Status: "+status);
				vencedor = pid;
				sendmessage(msg);
			}
		} 
	}
}