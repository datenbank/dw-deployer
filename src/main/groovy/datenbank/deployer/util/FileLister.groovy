package datenbank.deployer.util

import java.util.Observable;

class FileLister extends Observable {
	
	def extension = ".sln"
	def startPath = "C:\\"
	
	def files = []
	
	def search() {
		
		new File(startPath).eachFileRecurse { f ->
			
			if(f.name.endsWith(extension))
				files << f
			
		}
		
	}
	
	def ready() {
		setChanged()
		notifyObservers()
	}
	
	def String toString() {
		
		def text = "Path=${startPath}  Extension=${extension}\r\n"
		if(files.size()>0) {
			text <<= "-------------Files-------------\r\n"
			files.each {
				def tmp
				if("$it".length() > 79) {
					tmp = "..."
					tmp <<= "${it}".reverse().take(76).reverse()
				} else {
					tmp = it
				}
				text << "$tmp\r\n"			
			}
			text <<= "-------------------------------\r\n"
		} else {
			text <<= "(No files found)\r\n"
		}
	}

}
