import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue

def versionManager = ComponentAccessor.getVersionManager()
def projectManager = ComponentAccessor.getProjectManager()
def project = projectManager.getProjectObjByKey(issue.projectObject.key)

def versions = versionManager.getVersions(project)
def newversions = versions.collect()
newversions = newversions.sort({version1, version2 -> version1.releaseDate<=>version2.releaseDate}).findAll{version -> ! version.released }
def versionToUse = newversions.first();
issue.setFixVersions([versionToUse])
