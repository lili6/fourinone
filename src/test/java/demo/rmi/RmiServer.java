package demo.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.locks.Lock;

/**
 * Created by liguofang on 2015/1/28.
 */
public class RmiServer {

	public static void main(String[] args) {
		try {
			PersonService personService = new PersonServiceImpl();
			//注册通讯端口
			LocateRegistry.createRegistry(6600);
			Naming.rebind("rmi://127.0.0.1:6600/PersonService",personService);
			System.out.println("RMI Server start!");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
