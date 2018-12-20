import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue

MutableIssue issue = issue

def projectComponentManager = ComponentAccessor.getProjectComponentManager()
def project = issue.getProjectObject()
def originalComponent = sourceIssue.getComponents().getAt(0).getName()
def component = projectComponentManager.findByComponentName(project.getId(), originalComponent)
issue.setComponent([component])

