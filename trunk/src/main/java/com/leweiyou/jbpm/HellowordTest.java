package com.leweiyou.jbpm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.api.Configuration;
import org.jbpm.api.Deployment;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.junit.Before;
import org.junit.Test;

public class HellowordTest {

	private ProcessEngine engine;
	@Before
	public void setUp() throws Exception {
		engine = Configuration.getProcessEngine();
	}

	/**
	 * 发布流程
	 */
	@Test
	public void testDeploy(){
		RepositoryService rs = engine.getRepositoryService();
		String id = rs.createDeployment().addResourceFromClasspath("jpdl/helloword.jpdl.xml").deploy();
		assertNotNull(id);
	}
	
	@Test
	public void testPrintDeploy(){
		RepositoryService repositoryService = engine.getRepositoryService();
		List<Deployment> list = repositoryService.createDeploymentQuery().list();
		assertTrue(list.size() != 0);
		for(Deployment d : list){
			assertNotNull(d);
			System.out.println("Deployment:" + d.getId() + " " + d.getName());
		}
	}
	
	/**
	 * 测试 1天，经理，同意，结束
	 */
	@Test
	public void testWrite_1_leader(){
		//开始
		ExecutionService es = engine.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 1);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		pi = es.signalExecutionById(pid,"同意");
		assertTrue(pi.isEnded());
	}
	
	/**
	 * 测试 1天，经理，不同意
	 */
	@Test
	public void testWrite_1_$(){
		//开始
		ExecutionService es = engine.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 1);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("reason", "经理不同意：请假次数太多，已经超过了月量");
		pi = es.signalExecutionById(pid,"驳回",map1);
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("填写申请"));
		assertTrue(!pi.isEnded());
	}
	
	
	/**
	 * 测试 10天，经理，同意，老板不同意
	 */
	@Test
	public void testWrite_10_$(){
		//开始
		ExecutionService es = engine.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 10);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		pi = es.signalExecutionById(pid,"同意");
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("老板审批"));
		
		//老板审批
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("reason", "老板不同意：项目太紧，过两天请假吧");
		pi = es.signalExecutionById(pid,"驳回",map1);
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("填写申请"));
		assertTrue(!pi.isEnded());
		
	}
	
	/**
	 * 测试 10天，经理，同意，老板同意，结束
	 */
	@Test
	public void testWrite_10_leader(){
		//开始
		ExecutionService es = engine.getExecutionService();
		ProcessInstance pi = es.startProcessInstanceByKey("Helloword");
		Set<String> ans = pi.findActiveActivityNames();
		String pid = pi.getId();
		assertTrue(ans != null && ans.contains("填写申请"));
		
		//填写请假单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("day", 10);
		map.put("reason", "请假");
		pi = es.signalExecutionById(pid, map);
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("经理审批"));
		
		//经理审批
		pi = es.signalExecutionById(pid,"同意");
		ans = pi.findActiveActivityNames();
		assertTrue(ans != null && ans.contains("老板审批"));
		
		//老板审批
		pi = es.signalExecutionById(pid,"同意");
		assertTrue(pi.isEnded());
		
	}
	
	
	
}
