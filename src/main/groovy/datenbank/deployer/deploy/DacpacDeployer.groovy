package datenbank.deployer.deploy

import java.util.Observable;

class DacpacDeployer extends Observable {
	
	def SqlPackage = "SqlPackage"
	def dacpac 
	def server
	def databaseName
	
	def params = []
	
	def cmd
	def output
	def error
	
	def defaultParam() {
		addParam("Action", "Publish")
		addParam("SourceFile", "${dacpac}")
		addParam("TargetServerName", "${server}")
		addParam("TargetDatabaseName", "${databaseName}")
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
		cmd = "${SqlPackage} ${paramStr}"
		
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
