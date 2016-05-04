package com.leweiyou.jbpm;

import java.io.File;

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
		String path = JBPM.class.getResource("/").getPath() + "../classes/" + Commons.PATH_JPDL;
		File f = new File(path);
		if(f.isDirectory()){
			for(File file : f.listFiles()){
				try {
					repositoryService.createDeployment().addResourceFromFile(file).deploy();
				} catch (Exception e) {
					if(e.getMessage().trim().endsWith("already exists")){
						logger.error("jbdl file were deployed:" + file.getName());
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
