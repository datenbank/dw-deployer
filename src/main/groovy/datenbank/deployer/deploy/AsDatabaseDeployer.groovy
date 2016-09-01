package datenbank.deployer.deploy

import java.util.Observable;

class AsDatabaseDeployer extends Observable {
	
	def ASDeploymentWizard = "Microsoft.AnalysisServices.Deployment.exe"
	def asdatabase 
	def xmla	
	def params = []
	
	def cmd
	def output
	def error
		
	def setupCommandPrepare() {
		
		cmd = "${ASDeploymentWizard} ${asdatabase} /a"
		
	}
	
	def setupCommandMakeXmla() {
		
		cmd = "${ASDeploymentWizard} ${asdatabase} /d /o:${xmla}"
		
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
