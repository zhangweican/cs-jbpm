package com.leweiyou.jbpm;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.IdentityService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leweiyou.jbpm.utils.Commons;

/**
 * JBPM 操作类
 * @author Zhangweican
 *
 */
@Component
public class JBPM {
	private static Logger logger = Logger.getLogger(JBPM.class);
	
	@Autowired
	private ProcessEngine processEngine;
	
    private RepositoryService repositoryService ;

    private ExecutionService executionService ;

    private HistoryService historyService ;

    private TaskService taskService ;

    private IdentityService identityService ;
	
	@PostConstruct
	public void init(){
		repositoryService = processEngine.getRepositoryService();
		executionService = processEngine.getExecutionService();
		historyService = processEngine.getHistoryService();
		taskService = processEngine.getTaskService();
		identityService = processEngine.getIdentityService();
		//发布jpdl下的所有配置文件
		if(JBPM.class.getResource("").getPath().contains(".jar!")){
			try {
				JarFile jfile = new JarFile(new File(JBPM.class.getResource("").getPath().split("!")[0].replace("file:/", "")));
				Enumeration<JarEntry> files = jfile.entries();
				while (files.hasMoreElements()) {
					JarEntry entry = (JarEntry) files.nextElement();
					if (entry.getName().startsWith(Commons.PATH_JPDL) && entry.getName().endsWith(".jpdl.xml")){
						try {
							repositoryService.createDeployment().addResourceFromClasspath(entry.getName());
						} catch (RuntimeException e) {
							if(e.getMessage().trim().endsWith("already exists")){
								logger.info("jbdl file were deployed from cs-jbpm.jar:" );
							}else{
								logger.error("deploy jbdl file error from cs-jbpm.jar:", e);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		String path = JBPM.class.getResource("/").getPath() + "../classes/" + Commons.PATH_JPDL;
		File f = new File(path);
		if(f.isDirectory()){
			for(File file : f.listFiles()){
				try {
					repositoryService.createDeployment().addResourceFromFile(file).deploy();
				} catch (RuntimeException e) {
					if(e.getMessage().trim().endsWith("already exists")){
						logger.info("jbdl file were deployed:" + file.getName());
					}else{
						logger.error("deploy jbdl file error:" + file.getName(), e);
					}
				}
			}
		}
			
	}

	
	
	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public ExecutionService getExecutionService() {
		return executionService;
	}

	public HistoryService getHistoryService() {
		return historyService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public IdentityService getIdentityService() {
		return identityService;
	}
	
}
