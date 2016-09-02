package datenbank.deployer.deploy

import java.util.Observable;

class SqlDeployer extends Observable {
	
	def sqlcmd = "sqlcmd.exe"
	def sql 
	def server
	
	def params = []
	
	def cmd
	def output
	def error
	
	def defaultParam() {
		addParam("s", "${server}")
		addParam("i", "${sql}")
		
	}

	
	def addParam(param, value) {
		params << "-${param} ${value}"
	}
		
	def setupCommand() {
		defaultParam()
		def paramStr = params.join(' ')
		cmd = "${sqlcmd} ${paramStr}"
		
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
