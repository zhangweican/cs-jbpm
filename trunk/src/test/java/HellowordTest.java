
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.api.Deployment;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.leweiyou.jbpm.JBPM;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-jbpm.xml","classpath:applicationContext-service.xml","classpath:spring-mybatis.xml"})
public class HellowordTest {

	@Autowired
	private JBPM jbpm;
	
	/**
	 * 发布流程
	 */
	@Test
	public void testDeploy(){
		//RepositoryService rs = jbpm.getProcessEngine().getRepositoryService();
		
		//String dId = rs.createDeployment().addResourceFromClasspath("jpdl/helloword.jpdl.xml").deploy();
		
		//Assert.assertNotNull(dId);
	}
	
	@Test
	public void testPrintDeploy(){
		RepositoryService repositoryService = jbpm.getRepositoryService();
		List<Deployment> list = repositoryService.createDeploymentQuery().list();
		Assert.assertTrue(list.size() != 0);
		for(Deployment d : list){
			Assert.assertNotNull(d);
			System.out.println("Deployment:" + d.getId() + " " + d.getName());
		}
	}
	
	/**
	 * 测试 1天，经理，同意，结束
	 */
	@Test
	public void testWrite_1_leader(){
		//开始
		ExecutionService es = jbpm.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		Assert.assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 1);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		pi = es.signalExecutionById(pid,"同意");
		Assert.assertTrue(pi.isEnded());
	}
	
	/**
	 * 测试 1天，经理，不同意
	 */
	@Test
	public void testWrite_1_$(){
		//开始
		ExecutionService es = jbpm.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		Assert.assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 1);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("reason", "经理不同意：请假次数太多，已经超过了月量");
		pi = es.signalExecutionById(pid,"驳回",map1);
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("填写申请"));
		Assert.assertTrue(!pi.isEnded());
	}
	
	
	/**
	 * 测试 10天，经理，同意，老板不同意
	 */
	@Test
	public void testWrite_10_$(){
		//开始
		ExecutionService es = jbpm.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		Assert.assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 10);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		pi = es.signalExecutionById(pid,"同意");
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("老板审批"));
		
		//老板审批
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("reason", "老板不同意：项目太紧，过两天请假吧");
		pi = es.signalExecutionById(pid,"驳回",map1);
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("填写申请"));
		Assert.assertTrue(!pi.isEnded());
		
	}
	
	/**
	 * 测试 10天，经理，同意，老板同意，结束
	 */
	@Test
	public void testWrite_10_leader(){
		//开始
		ExecutionService es = jbpm.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		Assert.assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 10);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		pi = es.signalExecutionById(pid,"同意");
		ans = pi.findActiveActivityNames();
		Assert.assertTrue(ans != null && ans.contains("老板审批"));
		
		//老板审批
		pi = es.signalExecutionById(pid,"同意");
		Assert.assertTrue(pi.isEnded());
		
	}
	
	
	
}
