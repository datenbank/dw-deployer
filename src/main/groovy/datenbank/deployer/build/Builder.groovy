package datenbank.deployer.build

import java.util.Observable

class Builder extends Observable {
	
	def devenv = "devenv.com"	
	def slnOrProj
	
	def cmd
	def output
	def error
	

	def setupCommand() {
		cmd = "${devenv} \"${slnOrProj}\" /Build"
	}
	
	
	def build() {
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
