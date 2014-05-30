ApiaryDoclet
============

ApiaryDoclet is a Java doclet implementation that generates an Apiary blueprint from your Java code documentation.


Run
---

### From command line

You have to specify that *javadoc* has to use another doclet than the default doclet by giving the following options to the *javadoc* call :

	javadoc -docletpath <path to>ApiaryDoclet.jar \
		-doclet org.kaisquare.ApiaryDoclet

Example:

	javadoc -docletpath target/gson-2.2.4.jar:target/ApiaryDoclet.jar -doclet com.kaisquare.ApiaryDoclet -sourcepath app/controllers/api/*.java


