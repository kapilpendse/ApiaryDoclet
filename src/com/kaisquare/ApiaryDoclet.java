/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisquare;

import com.kaisquare.writers.ApiaryWriter;
import com.kaisquare.writers.apiary.ApiaryTags;
import com.kaisquare.writers.apiary.ServiceRequest;
import com.kaisquare.writers.apiary.ServiceResponse;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Kapil Pendse
 */
public class ApiaryDoclet {
    private static ApiaryWriter writer = null;

    private static boolean initWriter(String output,
            String host,
            String title,
            String description) {
        writer = new ApiaryWriter(output);
        writer.setHost(host);
        writer.setTitle(title);
        writer.setDescription(description);
        writer.begin();
        return true;
    }

    private static boolean closeWriter() {
        writer.end();
        return true;
    }
    
    private static void processMethod(MethodDoc methodDoc) throws Exception {
        Tag[] methodTags = methodDoc.tags();
        ParamTag[] paramTags = methodDoc.paramTags();

        String servTitle = "";
        String httpMethod = "POST";
        String uri = "";
        HashMap<String,String> params = new HashMap<>();
        ArrayList<String> jsonResponseStrings = new ArrayList<>();
        ArrayList<String> binaryResponseStrings = new ArrayList<>();
        ArrayList<String> htmlResponseStrings = new ArrayList<>();

        for(Tag mt:methodTags) {
            if(mt.name().equalsIgnoreCase(ApiaryTags.SERVICETITLE)) {
                servTitle = mt.text();
            }
            if(mt.name().equalsIgnoreCase(ApiaryTags.HTTPMETHOD)) {
                httpMethod = mt.text();
            }
            if(mt.name().equalsIgnoreCase(ApiaryTags.URI)) {
                uri = mt.text();
            }
            if(mt.name().equalsIgnoreCase(ApiaryTags.RESPONSEJSON)) {
                jsonResponseStrings.add(mt.text());
            }
            if(mt.name().equalsIgnoreCase(ApiaryTags.RESPONSEBINARY)) {
                binaryResponseStrings.add(mt.text());
            }
            if(mt.name().equalsIgnoreCase(ApiaryTags.RESPONSEHTML)) {
                htmlResponseStrings.add(mt.text());
            }
        }
        for(ParamTag pt:paramTags) {
            params.put(pt.parameterName(), pt.parameterComment());
        }
        
        ServiceRequest request = new ServiceRequest();
        request.charset = "UTF-8";
        request.contentType = "application/json";
        request.httpMethod = httpMethod;
        request.inputParameters = params;
        request.uri = uri;
        ArrayList<ServiceResponse> responses = new ArrayList<>();
        for(String r:jsonResponseStrings) {
            ServiceResponse sr = new ServiceResponse();
            sr.charset = "UTF-8";
            sr.contentType = "application/json";
            sr.httpResponseCode = "200";
            sr.response = r;
            responses.add(sr);
        }
        for(String r:binaryResponseStrings) {
            ServiceResponse sr = new ServiceResponse();
            sr.charset = "";
            sr.contentType = "application/octet-stream";
            sr.httpResponseCode = "200";
            sr.response = r;
            responses.add(sr);
        }
        for(String r:htmlResponseStrings) {
            ServiceResponse sr = new ServiceResponse();
            sr.charset = "";
            sr.contentType = "text/html";
            sr.httpResponseCode = "200";
            sr.response = r;
            responses.add(sr);
        }
        writer.newService(servTitle, request, responses.toArray(new ServiceResponse[1]));
    }
    private static void processClass(ClassDoc classDoc) throws Exception {
        Tag[] tags = classDoc.tags();
        String sectionTitle = "";
        String sectionDesc = "";
        for(Tag t:tags) {
            if(t.name().equalsIgnoreCase(ApiaryTags.SECTIONTITLE)) {
                sectionTitle = t.text();
            }
            if(t.name().equalsIgnoreCase(ApiaryTags.SECTIONDESCRIPTION)) {
                sectionDesc = t.text();
            }
        }

        writer.newSection(sectionTitle, sectionDesc);

        MethodDoc methods[] = classDoc.methods();
        for(MethodDoc m:methods) {
            try {
                processMethod(m);
            } catch (Exception e) {
                System.out.println("ApiaryDoclet: " + e.getMessage());
            }
        }
    }
    
    public static boolean start(RootDoc root) {
        final String OUTPUT_FILE = "blueprint.txt";
        final String API_HOST = "http://sandbox.kaisquare.com/";
        final String TITLE = "KAI Unified Platform API Documentation";
        final String DESCRIPTION = "Welcome to the Keuro API documentation. It is intended for " +
                "developers who are writing client side applications for the KUP, including mobile, " +
                "tablet and web client developers and 3rd party integrators. Note that if you " +
                "are using an Enterprise Edition, simply replace the IP address with your server IP " +
                "to work. This documentation was auto-generated by ApiaryDoclet.";
        
        initWriter(OUTPUT_FILE, API_HOST, TITLE, DESCRIPTION);

        ClassDoc[] classes = root.classes();
        for(ClassDoc c:classes) {
            Tag[] tags = c.tags();
            for(Tag t:tags) {
                if(t.name().equalsIgnoreCase(ApiaryTags.PUBLICAPI)) {
                    try {
                        processClass(c);
                    } catch (Exception e) {
                        System.out.println("ApiaryDoclet: " + e.getMessage());
                    }
                }
            }
        }
        
        closeWriter();
        return true;
    }
}
