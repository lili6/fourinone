package lili.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liguofang on 2015/1/30.
 * 以此类对象模拟一个进程的形式，演示zookeeper分布式锁的实现
 */
public class DistributedClient {
	private static final Logger log = LoggerFactory.getLogger(DistributedClient.class);

	//超时时间
	private static final int SESSION_TIMEOUT=5000;
	//zookeeper server列表
	private String hosts="10.6.155.160:2181";
	private String groupNode="locks";
	private String subNode="sub";

	private ZooKeeper zk;
	//当前client创建的子节点
	private String thisPath;
	//当前client等待的子节点
	private String waitPath;

	private CountDownLatch latch = new CountDownLatch(1);


	private void doSomething() throws Exception {
		try {
			System.out.println("gain lock:" + thisPath);
			Thread.sleep(2000);
			//TODO do something....
		} finally {
			log.debug("finished:{}and will delete this path。。。",thisPath);
			//将thisPath删除，监听ThisPath的client将获得通知
			//相当于释放锁
			zk.delete(this.thisPath,-1);
		}
	}


	public void connectZookeeper() throws Exception {
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent watchedEvent) {
				if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
					latch.countDown();
				}

				//发生waitPath的删除事件
				try {
					if (watchedEvent.getType() == Event.EventType.NodeDeleted &&
							watchedEvent.getPath().equals(waitPath)) {
						doSomething();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}) ;
		log.debug("等待建立zookeeper【{}】连接。。",hosts);
		//等待建立连接
		latch.await();
		//创建1级子节点
		thisPath = zk.create("/"+groupNode,null, ZooDefs.Ids.OPEN_ACL_UNSAFE
				, CreateMode.PERSISTENT);
		//创建子节点，创建子节点，必须得是PERSISTENT存储方式，否则创建失败
		thisPath = zk.create("/"+groupNode+"/"+subNode,null, ZooDefs.Ids.OPEN_ACL_UNSAFE
		, CreateMode.PERSISTENT);
//			zk.create("/node1", "data1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
		//sleep 一下，让结果更清楚
		Thread.sleep(10);
		//此处false，不对/locks的子节点的变化进行监听
		List<String>    childrenNodes= zk.getChildren("/"+groupNode,false);

		if(childrenNodes.size() == 1) {
			doSomething();
		} else {
			String  thisNode = thisPath.substring(("/" + groupNode + "/").length());
			//排序
			Collections.sort(childrenNodes);
			int index = childrenNodes.indexOf(thisNode);
			if (index == -1) {
				//never happened
			}else if (index ==0){//说明thisNode在列表中最小，当前client获得锁
				doSomething();
			}else {
				//获得排名比thisPath前1位的节点
				this.waitPath =  "/" + groupNode + "/" + childrenNodes.get(index - 1);
				// 在waitPath上注册监听器, 当waitPath被删除时, zookeeper会回调监听器的process方法
				zk.getData(waitPath, true, new Stat());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		for(int i =0;i <1; i++) {
			new Thread() {
				public void run() {
					try {
						DistributedClient dl = new DistributedClient();
						dl.connectZookeeper();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		Thread.sleep(Long.MAX_VALUE);
	}

}
