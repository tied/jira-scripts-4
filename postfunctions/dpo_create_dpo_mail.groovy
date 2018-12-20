import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import org.apache.log4j.Level
  
def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def email_patterm = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+"
def summary = issue.summary
def description = issue.description
if(summary.contains("Undelivered Mail Returned to Sender")){
    def firstSubstring = description.split('<')[1]
    log.debug firstSubstring
    def secondSplit = firstSubstring.split('>')[0]
    log.debug secondSplit
    def textCf = customFieldManager.getCustomFieldObjectByName("DPO mail") 
    issue.setCustomFieldValue(textCf, secondSplit)
}