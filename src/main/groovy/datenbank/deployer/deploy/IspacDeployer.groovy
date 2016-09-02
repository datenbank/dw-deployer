package datenbank.deployer.deploy

import java.util.Observable;

class IspacDeployer extends Observable {
	
	def ISDeploymentWizard = "ISDeploymentWizard.exe"
	def ispac 
	def server
	def destinationPath
	
	def params = []
	
	def cmd
	def output
	def error
	
	def defaultParam() {
		addParam("SourcePath", "${ispac}")
		addParam("DestinationServer", "${server}")
		addParam("DestinationPath", "${destinationPath}")
	}
	
	def addVariable(variable, value) {
		
		params << "/Variables:${variable}=${value}"

	}
	
	def addVariable(variable) {
		
		params << "/Variables:${variable}"

	}
	
	def addParam(param, value) {
		params << "/${param}:${value}"
	}
		
	def setupCommand() {
		defaultParam()
		def paramStr = params.join(' ')
		cmd = "${ISDeploymentWizard} /Silent ${paramStr}"
		
	}
	
	def deploy() {
		setupCommand()
		
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
