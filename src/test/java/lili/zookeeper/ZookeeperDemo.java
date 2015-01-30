package lili.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * Created by liguofang on 2015/1/30.
 */
public class ZookeeperDemo {
	private static final int TIMEOUT = 3000;
	private static  ZooKeeper zkp = null;

	private void createNode() throws Exception {
		zkp = new ZooKeeper("10.6.155.160:2181", TIMEOUT, null);
		//重新建立会话后node1已经不存在了
		if (zkp.exists("/node1", false) == null) {
			System.out.println("node1 dosn't exists now.");
		}
		//创建SEQUENTIAL节点
		zkp.create("/node-", "same data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		zkp.create("/node-", "same data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		zkp.create("/node-", "same data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		List<String> children = zkp.getChildren("/", null);
		System.out.println("Children of root node:");
		for (String child : children) {
			System.out.println(child);
		}

		zkp.close();
	}


	public static void main(String[] args) throws Exception {
		zkp = new ZooKeeper("10.6.155.160:2181", TIMEOUT, null);

		// 创建一个EPHEMERAL类型的节点，会话关闭后它会自动被删除
		if (zkp.exists("/node1", false) != null) {
			System.out.println("node1 exists now.已经存在,删除之");

			zkp.delete("/node1", -1);// －1的话直接删除，无视版本
		}
			zkp.create("/node1", "data1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);  //按顺序
			zkp.create("/node1/lili", "data1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);  //按顺序

		try {
			// 当节点名已存在时再去创建它会抛出KeeperException(即使本次的ACL、CreateMode和上次的不一样)
			zkp.create("/node2", "data1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch (KeeperException e) {
			System.out.println("KeeperException caught:" + e.getMessage());
		}

		List<String> children = zkp.getChildren("/", null);
		System.out.println("Children of root:");
		for (String child : children) {
			System.out.println(child);
		}
		// 关闭会话
		zkp.close();
	}


}
