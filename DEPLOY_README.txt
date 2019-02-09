Things to take care of while deploying the project on web server:
1. Change the WordNet dictionary in wn_file_properties.xml file in jigsaw.resources directory
	new location :
		/usr/server/apache-tomcat-8.0.12/webapps/Kparser/WEB-INF/classes/module/graph/helper/WordNet......

2. Copy the locally compiled morph directory top the location /module/graph/lib/
	Execute the following command to locally compile morpha:
	sudo gcc -o morpha morpha.yy.c
	
3. Change the parser.rb location in module/graph/rubySource/parser.rb

4. Change the locations of STANFORD_PARSER, MORPHA, VERBSTEM, CLASSPATH, DATAPATH and EVALPATH in module/graph/rubySource/variable.rb
	new location is:
		/usr/server/apache-tomcat-8.0.12/webapps/Kparser/WEB-INF/classes/module/graph/.......

5. Change the access of module/graph/lib/morph (if not executable)
	chmod -R 7 module/graph/lib/morph
	
6. Change the location of kparser log file in .../kparser/WEB-INF/classes/log4j.properties:
   log4j.appender.mainAppender.File=/usr/server/apache-tomcat-8.0.12/kparserLogs/kparser.log
   
7. Restart the server after making the above changes.


