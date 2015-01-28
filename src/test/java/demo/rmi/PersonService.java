package demo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by liguofang on 2015/1/28.
 */
public interface PersonService extends Remote {
	public List<PersonEntity> GetList() throws RemoteException;
}
