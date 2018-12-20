import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)
log.debug "foo bar"

import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.component.ComponentAccessor

def projectComponentManager = ComponentAccessor.getComponent(ProjectComponentManager)
def projects = projectComponentManager.findAll();
int i = 0;
int elements = 300;
int pagination = 0;
int lowerBoundary = elements * pagination;
int upperBoundary = elements * (pagination + 1);
projects.each { p ->
	if(i >= lowerBoundary && i < upperBoundary)
		log.debug p["name"].toString() + System.lineSeparator();
}
