For simplicity, the .target file is created from the Target Platform Definition (.tpd) 
file using the CBI Release Engineering tools found here 'https://github.com/eclipse-cbi/targetplatform-dsl'

The CBI tools, however, are not able to include Maven dependencies as modules so the 
code below needs to be added to the end of the .target file in order to complete the 
target platform definition.

'
	  <location includeSource="true" missingManifest="generate" type="Maven">
		  <dependencies>
			  <dependency>
				  <groupId>org.freemarker</groupId>
				  <artifactId>freemarker</artifactId>
				  <version>2.3.31</version>
				  <type>jar</type>
			  </dependency>
		  </dependencies>
	  </location>
	  <location includeDependencyScope="provided" includeSource="true" missingManifest="generate" type="Maven">
		  <dependencies>
			  <dependency>
				  <groupId>org.apache.jena</groupId>
				  <artifactId>jena-commonsrdf</artifactId>
				  <version>4.4.0</version>
				  <type>jar</type>
			  </dependency>
		  </dependencies>
	  </location>
'
