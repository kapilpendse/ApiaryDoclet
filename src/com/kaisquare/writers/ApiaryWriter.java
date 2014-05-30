/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisquare.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaisquare.writers.apiary.ServiceRequest;
import com.kaisquare.writers.apiary.ServiceResponse;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 *
 * @author Kapil Pendse
 */
public class ApiaryWriter {
    private String filename;
    private String host;
    private String title;
    private String description;
    private BufferedWriter bWriter = null;
    private Gson gson = null;

    /**
     * Instantiates an apiary writer.
     * @param outputFile    Path/name of the output file.
     */
    public ApiaryWriter(String outputFile) {
        filename = outputFile;
    }

    /**
     * Sets the HOST for apiary blueprint.
     * @param h     The host e.g. http://api.somewebapp.com/
     */
    public void setHost(String h) {
        host = h;
    }

    /**
     * Sets the title of generated documentation
     * @param t     The title e.g. "Project Xanadu API documentation"
     */
    public void setTitle(String t) {
        title = t;
    }
    
    /**
     * Sets the description of generation documentation
     * @param d     The description paragraph
     */
    public void setDescription(String d) {
        description = d;
    }
    
    /**
     * Creates the output file and writes out the header part of Apiary blueprint.
     * @return true on success, otherwise false
     */
    public boolean begin() {
        try {
            //Sanity check
            if(filename == null || filename.isEmpty()) {
                throw new Exception("File name must be set");
            }
            
            //Ready the output writer
            Charset charset = Charset.forName("UTF-8");
            bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), charset));

            //Set headers to default values if not set explicitly
            if(host == null || host.isEmpty()) {
                host = "http://api.somewebapp.com/";
            }
            if(title == null || title.isEmpty()) {
                title = "Project Name";
            }
            if(description == null || description.isEmpty()) {
                description = "Project Description goes here.";
            }
            
            //Write out the headers
            bWriter.write("HOST: ");
            bWriter.write(host);
            bWriter.newLine();
            bWriter.newLine();
            
            bWriter.write("--- " + title + " ---");
            bWriter.newLine();
            bWriter.newLine();

            bWriter.write("---");
            bWriter.newLine();
            bWriter.write(description);
            bWriter.newLine();
            bWriter.write("---");
            bWriter.newLine();
            bWriter.newLine();

            gson = new GsonBuilder().setPrettyPrinting().create();
            return true;
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Writes out a new section.
     * @param sectionTitle          The section title e.g. Account Management
     * @param sectionDescription    The section description e.g. APIs to manage account settings.
     * @return true on success, otherwise false
     */
    public boolean newSection(String sectionTitle, String sectionDescription) throws Exception {
        try {
            if(sectionTitle == null || sectionTitle.isEmpty()) {
                throw new Exception("Section title must be set");
            }
            if(sectionDescription == null || sectionDescription.isEmpty()) {
                throw new Exception("Section description must be set");
            }

            bWriter.write("--");
            bWriter.newLine();
            bWriter.write(sectionTitle);
            bWriter.newLine();
            bWriter.write(sectionDescription);
            bWriter.newLine();
            bWriter.write("--");
            bWriter.newLine();
            bWriter.newLine();
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Writes out a new service definition.
     * @param title         A one like title for the service e.g. "Login with username and password"
     * @param request       The HTTP request
     * @param responses     Array of all possible HTTP responses
     * @return true on success, otherwise false
     */
    public boolean newService(String title,
            ServiceRequest request,
            ServiceResponse[] responses) throws Exception {
        try {
            if(title == null || title.isEmpty()) {
                throw new Exception("Service title must be set");
            }
            if(request == null ||
                    request.httpMethod == null || request.httpMethod.isEmpty() ||
                    request.uri == null || request.uri.isEmpty()) {
                throw new Exception("Service request information must be set");
            }
            if(responses == null || responses.length == 0) {
                throw new Exception("Service must have at least one response documented");
            }
            for(ServiceResponse r:responses) {
                if(r.httpResponseCode == null || r.httpResponseCode.isEmpty()) {
                    throw new Exception("Service response code must be set");
                }
                if(r.response == null || r.response.isEmpty()) {
                    throw new Exception("Service response string must be set");
                }
            }

            //Service header
            bWriter.write(title);
            bWriter.newLine();
            bWriter.write(request.httpMethod + " " + request.uri);
            bWriter.newLine();
            //Service request
            if(request.contentType != null && !request.contentType.isEmpty()) {
                bWriter.write("> Content-Type: " + request.contentType);
                if(request.charset != null && !request.charset.isEmpty()) {
                    bWriter.write("; charset=" + request.charset);
                }
                bWriter.newLine();
            }
            bWriter.write(gson.toJson(request.inputParameters));
            bWriter.newLine();
            //Service response(s)
            for(int i=0; i<responses.length; i++) {
                if(i > 0) {
                    bWriter.write("+++++");
                    bWriter.newLine();
                }
                bWriter.write("< " + responses[i].httpResponseCode);
                bWriter.newLine();
                if(responses[i].contentType != null && !responses[i].contentType.isEmpty()) {
                    bWriter.write("< Content-Type: " + responses[i].contentType);
                    if(responses[i].charset != null && !responses[i].charset.isEmpty()) {
                        bWriter.write("; charset=" + responses[i].charset);
                    }
                    bWriter.newLine();
                }
                bWriter.write(responses[i].response);
                bWriter.newLine();
            }
            bWriter.newLine();
            return true;
        } catch (Exception e) {
            throw e;
        }
    }
    /**
     * Closes the output file.
     * @return true on success, otherwise false.
     */
    public boolean end() {
        try {
            bWriter.close();
            gson = null;
            return true;
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
}
