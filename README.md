ApiaryDoclet
============

ApiaryDoclet is a Java doclet implementation that generates an Apiary blueprint from your Java code documentation.


Run
---

### From command line

You have to specify that *javadoc* has to use another doclet than the default doclet by giving the following options to the *javadoc* call :

	javadoc -docletpath <path to>ApiaryDoclet.jar \
		-doclet org.kaisquare.ApiaryDoclet \
        -d destination_directory \
        -apihost "http://api.somehost.org/" \
        -title "a title for your documentation" \
        -description "some lines of description for your documentation."

Example:

	javadoc -docletpath target/gson-2.2.4.jar:target/ApiaryDoclet.jar -doclet com.kaisquare.ApiaryDoclet -sourcepath app/controllers/api/*.java -d target/docs -apihost "http://api.myapp.com" -title "API for my awesome web app" -description "This is top secret. Even double-O seven must not know. Shhh."


