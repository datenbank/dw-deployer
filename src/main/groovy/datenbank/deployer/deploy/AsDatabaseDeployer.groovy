package datenbank.deployer.deploy

import java.util.Observable;

class AsDatabaseDeployer extends Observable {
	
	def ASDeploymentWizard = "Microsoft.AnalysisServices.Deployment.exe"
	def asdatabase 
	def xmla	
	def params = []
	
	def server
	
	def cmd
	def output
	def error
		
	def setupCommandPrepare() {
		
		cmd = "${ASDeploymentWizard} ${asdatabase} /a"
		
	}
	
	def setupCommandMakeXmla() {
		
		cmd = "${ASDeploymentWizard} ${asdatabase} /d /o:${xmla}"
		
	}
	
	def setupCommandDeploy() {
		
		cmd = "${ASDeploymentWizard} ${asdatabase}"
		
	}
	
	def setupCommandDeployPowershell() {
		
		cmd = "powershell -file xmlscript.ps1"
		
	}
	
	def createPowershellScript() {
		
		def ps1 = new File('xmlscript.ps1')
		ps1.write('')
		
		ps1 << "Import-Module SQLPS\r\n"
		ps1 << "Invoke-ASCmd -Server ${server} -InputFile ${xmla}\r\n"
		ps1 << "exit\r\n"
	}
	
	
	def prepare() {
		setupCommandPrepare()
		
		def res = cmd.execute()
		res.waitFor()
					
		output = res.in.text
		error = res.err.text
	}
	
	def xmla() {
		setupCommandMakeXmla()
		
		def res = cmd.execute()
		res.waitFor()
					
		output = res.in.text
		error = res.err.text
	}
	
	def deploy() {
		setupCommandDeploy()
		
		def res = cmd.execute()
		res.waitFor()
					
		output = res.in.text
		error = res.err.text
	}
	
	
	
	def deployXmla() {
		createPowershellScript()
		setupCommandDeployPowershell()
		
		def res = cmd.execute()
		res.waitFor()
					
		output = res.in.text
		error = res.err.text
	}
	
	def ready() {
		setChanged()
		notifyObservers()
	}
	
	def String toString() {
		def text = "${cmd}\r\n"
		
		if(output) {
			text <<= "-------------Output------------\r\n"
			text <<= "${output}\r\n"
			text <<= "-------------------------------\r\n"
		}
		if(error) {
			text <<= "-------------Error-------------\r\n"
			text <<= "${error}\r\n"
			text <<= "-------------------------------\r\n"
		}
		
		return text
	}


}
