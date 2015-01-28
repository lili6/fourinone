package demo.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by liguofang on 2015/1/28.
 */
public class RmiClient {

	public static void main(String[] args) {

		try {
			PersonService personService = (PersonService) Naming.lookup("rmi://127.0.0.1:6600/PersonService");
			List<PersonEntity> personEntityList = personService.GetList();
			for (PersonEntity personEntity:personEntityList){
				System.out.println("ID:" + personEntity.getId()
						+ "  Age:" + personEntity.getAge()
						+ "  Name:" + personEntity.getName());
			}
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
