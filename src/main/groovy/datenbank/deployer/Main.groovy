package datenbank.deployer

import datenbank.deployer.deploy.*
import datenbank.deployer.build.Builder
import datenbank.deployer.ui.ConsolePrinter
import datenbank.deployer.util.FileLister


import groovy.util.CliBuilder
import org.apache.commons.cli.Option


class Main {

	static environment = 'development'
	
	static config
	
	static server = "."
	static database = "Default"
	
	static main(args) {
		
		config = new ConfigSlurper(environment).parse(new File('dw-deployer.config').text) 
		
		def pathString = "set PATH=%PATH%;"
		def devenvPath = config.devenv.split('\\\\')
		def z = 1
		devenvPath.each {
			if(devenvPath.size() > z) {
				pathString <<= "${it}\\"
			} else {
				pathString <<= ";"
			}
			z++
		}
		
		def SqlPackagePath = config.SqlPackage.split('\\\\')
		z = 1
		SqlPackagePath.each {
			if(SqlPackagePath.size() > z) {
				pathString <<= "${it}\\"
			} else {
				pathString <<= ";"
			}
			z++
		}
		
		def ISDeployPath = config.ISDeploy.split('\\\\')
		z = 1
		ISDeployPath.each {
			if(ISDeployPath.size() > z) {
				pathString <<= "${it}\\"
			} else {
				pathString <<= ";"
			}
			z++
		}
		
		def ASDeployPath = config.ASDeploy.split('\\\\')
		z = 1
		ASDeployPath.each {
			if(ASDeployPath.size() > z) {
				pathString <<= "${it}\\"
			} else {
				pathString <<= ";"
			}
			z++
		}
		
		def sqlcmdPath = config.sqlcmd.split('\\\\')
		z = 1
		sqlcmdPath.each {
			if(sqlcmdPath.size() > z) {
				pathString <<= "${it}\\"
			} else {
				pathString <<= ";"
			}
			z++
		}
		
		println "$pathString"
		
		def path = new File('path.bat')
		path.write("")
		path << "$pathString"
		
		if(path.exists()) {
			def res = "path.bat".execute()
			res.waitFor()
		} else {
		
		}

		CliBuilder cli = new CliBuilder(usage:'dw-deployer')
		cli.h(longOpt: 'help', 'usage information', required: false)
		cli.b(longOpt: 'build', 'build a .sln or .proj file', required: false, args: 1)
		cli.d(longOpt: 'dacpac', 'deploy a .dacpac file', required: false, args: 1)
		cli.i(longOpt: 'ispac', 'deploy a .ispac file, example; <file>.ispac,/SSISDB/<folder>/<project>', required: false, args: 2, valueSeparator: ',')
		cli.a(longOpt: 'asdatabase', 'deploy a .asdatabase file, example; <file>.asdatabase', required: false, args: 1)
		cli.ap(longOpt: 'asprepare', 'prepare a .asdatabase file, example; <file>.asdatabase', required: false, args: 1)
		cli.x(longOpt: 'xmla', 'make an xmla file from a .asdatabase file, example; <file>.asdatabase,<outfile>.xmla', required: false, args: 2, valueSeparator: ',')
		cli.xd(longOpt: 'xmladeploy', 'deploy an xmla file throught PowerShell script example; <file>.xmla,<file>.ps1', required: false, args: 2,  valueSeparator: ',')
		cli.s(longOpt: 'server', 'server to deploy to', required: false)
		cli.db(longOpt: 'database', 'database to deploy to (for .dacpac files)', required: false)
		cli.v(longOpt: 'variables', 'variables when deloying example; Varable1Name=Value,Varable2Name=Value2', required: false, args: Option.UNLIMITED_VALUES, valueSeparator: ',')
		cli.f(longOpt: 'files', 'list files example; <path>,<extension>', required: false, args: 2, valueSeparator: ',')
		cli.fa(longOpt: 'filesall', 'list files example; <path>', required: false, args: 1)
		cli.p(longOpt: 'print', 'only print commands', required: false)
		cli.sql(longOpt: 'sequel', 'deploy a .sql file', required: false, , args: 1)
		cli.auto(longOpt: 'auto', 'automate it all', required: false, , args: 1)
		OptionAccessor opt = cli.parse(args)
		
		def i = 0
			
		if(config.server) {
			server = config.server
		}		
		if(opt.s) {
			server = opt.s			
		}		
		if(config.database) {
			database = config.config.database
		}		
		if(opt.db) {
			database = opt.db
		}
		
		def cp = new ConsolePrinter()
		
		if(opt.f) {
			
			def files = new FileLister(startPath: opt.fs[0], extension: opt.fs[1])
			files.addObserver(cp)
			files.search()
			files.ready()
			i++
		}
		
		if(opt.fa) {
			
			def files = new FileLister(startPath: opt.fa, extension:'.sln')
			files.addObserver(cp)
			files.search()
			files.ready()
			
			files = new FileLister(startPath: opt.fa, extension:'.dacpac')
			files.addObserver(cp)
			files.search()
			files.ready()
			
			files = new FileLister(startPath: opt.fa, extension:'.ispac')
			files.addObserver(cp)
			files.search()
			files.ready()
			
			files = new FileLister(startPath: opt.fa, extension:'.asdatabase')
			files.addObserver(cp)
			files.search()
			files.ready()
			
			i++
		}
		
		if(opt.auto) {
			
			def files = new FileLister(startPath: opt.auto, extension:'.sln')
			files.search()
			
			files.files.each {
				def build = new Builder(slnOrProj: it)
				build.setupCommand()
				build.addObserver(cp)
				build.ready()
			}
			
			
			
			files = new FileLister(startPath: opt.auto, extension:'.dacpac')			
			files.search()
			files.files.each {
				def deploy = new DacpacDeployer(dacpac: it, server: server, databaseName: it.name.replace('.dacpac', ''))
				deploy.setupCommand()
				deploy.addObserver(cp)
				deploy.ready()
			}
			
			files = new FileLister(startPath: opt.auto, extension:'.ispac')
			files.search()
			files.files.each {
				def deployIspac = new IspacDeployer(ispac: it, server: server, destinationPath: '/SSISDB/<Folder>/'+it.name.replace('.ispac', ''))
				deployIspac.setupCommand()				
				deployIspac.addObserver(cp)
				deployIspac.ready()
				
			}
			
			
			files = new FileLister(startPath: opt.auto, extension:'.asdatabase')
			files.search()
			files.files.each {
				def deployAsDatabase = new AsDatabaseDeployer(asdatabase: it)
				deployAsDatabase.setupCommandDeploy()
				deployAsDatabase.addObserver(cp)
				deployAsDatabase.ready()
				
			}

			
			i++
		}
		
		if(opt.b) {
			def build = new Builder(slnOrProj: opt.b)
			build.addObserver(cp)
			if(opt.p) {
				build.setupCommand()
			} else {
				build.build()
			}			
			
			build.ready()
			i++
		}
		
		if(opt.d) {
			def deploy = new DacpacDeployer(dacpac: opt.d, server: server, databaseName: database)
			
			if(opt.v) {				
				opt.vs.each {
					deploy.addVariable(it)
				}
			}
			
			deploy.addObserver(cp)
			if(opt.p) {
				deploy.setupCommand()
			} else {
				deploy.deploy()
			}			
			deploy.ready()
			i++
		}
		
		if(opt.i) {
			def deployIspac = new IspacDeployer(ispac: opt.is[0], server: server, destinationPath: opt.is[1])
			
			if(opt.v) {
				opt.vs.each {
					deployIspac.addVariable(it)
				}
			}
			
			deployIspac.addObserver(cp)
			if(opt.p) {
				deployIspac.setupCommand()
			} else {
				deployIspac.deploy()
			}
			deployIspac.ready()
			i++
		}
		
		if(opt.ap) {
			def deployAsDatabasePrepare = new AsDatabaseDeployer(asdatabase: opt.ap)
			
			deployAsDatabasePrepare.addObserver(cp)
			if(opt.p) {
				deployAsDatabasePrepare.setupCommandPrepare()
			} else {
				deployAsDatabasePrepare.prepare()
			}
			deployAsDatabasePrepare.ready()
			i++
		}
		
		if(opt.a) {
			def deployAsDatabase = new AsDatabaseDeployer(asdatabase: opt.a)
			
			deployAsDatabase.addObserver(cp)
			if(opt.p) {
				deployAsDatabase.setupCommandDeploy()
			} else {
				deployAsDatabase.deploy()
			}
			deployAsDatabase.ready()
			i++
		}
		
		if(opt.x) {
			def deployAsDatabaseXmla = new AsDatabaseDeployer(asdatabase: opt.xs[0], xmla: opt.xs[1])
			
			deployAsDatabaseXmla.addObserver(cp)
			if(opt.p) {
				deployAsDatabaseXmla.setupCommandMakeXmla()
			} else {
				deployAsDatabaseXmla.xmla()
			}
			deployAsDatabaseXmla.ready()
			i++
		}
		
		if(opt.xd) {
			def deployAsDatabaseXmlaDeploy = new AsDatabaseDeployer(server: server, xmla: opt.xds[0], ps1: opt.xds[1])			
			deployAsDatabaseXmlaDeploy.addObserver(cp)
			if(opt.p) {				
				deployAsDatabaseXmlaDeploy.createPowershellScript()
				deployAsDatabaseXmlaDeploy.setupCommandDeployPowershell()				
			} else {
				deployAsDatabaseXmlaDeploy.deployXmla()
			}
			deployAsDatabaseXmlaDeploy.ready()
			i++
		}
		
		if(opt.sql) {
			def deploySql = new SqlDeployer(sql: opt.sql, server: server)
		
			deploySql.addObserver(cp)
			if(opt.p) {
				deploySql.setupCommand()
			} else {
				deploySql.deploy()
			}
			deploySql.ready()
			i++
		}
		

		if(opt.h || i==0) {
			cli.usage()
		}
	
	}


}
