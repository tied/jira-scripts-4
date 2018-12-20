import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)
log.debug "foo bar"

import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.component.ComponentAccessor

long projectId = -1;
def projectManager = ComponentAccessor.getProjectManager();
projectManager.getProjectObjects().each { p ->
	//log.debug (p.getName() + " -> " + p.getId());
	if(p.getName().toString().equals("Nuevos Frontales")) {
		log.debug (p.getName() + " -> " + p.getId());
		projectId = p.getId();
	}
}


def projectComponentManager = ComponentAccessor.getComponent(ProjectComponentManager)
def components = projectComponentManager.findAll();
int i = 0;
int elements = 300;
int pagination = 0;
int lowerBoundary = elements * pagination;
int upperBoundary = elements * (pagination + 1);
def componentList = [];
components.each { p ->
	if(p.getProjectId() == projectId) {
		log.debug p["name"].toString() + System.lineSeparator();
		componentList.add(p["name"].toString());
	}
}

componentList;
