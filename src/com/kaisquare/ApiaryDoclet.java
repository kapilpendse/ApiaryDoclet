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
import com.sun.javadoc.DocErrorReporter;
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
    private static final String OUTPUT_FILE = "blueprint.txt";

    private static final String OPT_DEST_DIR = "-d";
    private static final String OPT_API_HOST = "-apihost";
    private static final String OPT_DOC_TITLE = "-title";
    private static final String OPT_DOC_DESCRIPTION = "-description";

    private static final String DEF_DEST_DIR = "";
    private static final String DEF_API_HOST = "http://api.hostname.org/";
    private static final String DEF_DOC_TITLE = "API documentation of Eyes Only project";
    private static final String DEF_DOC_DESCRIPTION = "Few lines of description of this documentation.";
    
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
    
    private static String readOptionValue(String option, String[][] options) {
        String value = null;
        for (int i = 0; i < options.length; i++) {
            String[] opt = options[i];
            if (opt[0].equalsIgnoreCase(option)) {
                value = opt[1];
            }
        }
        return value;
    }

    private static String getDestinationFilePath(String[][] options) {
        String destFilePath;
        destFilePath = readOptionValue(OPT_DEST_DIR, options);
        if(destFilePath == null) {
            destFilePath = DEF_DEST_DIR;
        }
        if(destFilePath.lastIndexOf("/") < destFilePath.length()) {
            destFilePath = destFilePath + "/";
        }
        destFilePath = destFilePath + OUTPUT_FILE;
        return destFilePath;
    }

    private static String getApiHost(String[][] options) {
        String apiHost;
        apiHost = readOptionValue(OPT_API_HOST, options);
        if(apiHost == null) {
            apiHost = DEF_API_HOST;
        }
        return apiHost;
    }

    private static String getDocumentTitle(String[][] options) {
        String docTitle;
        docTitle = readOptionValue(OPT_DOC_TITLE, options);
        if(docTitle == null) {
            docTitle = DEF_DOC_TITLE;
        }
        return docTitle;
    }

    private static String getDocumentDescription(String[][] options) {
        String docDescription;
        docDescription = readOptionValue(OPT_DOC_DESCRIPTION, options);
        if(docDescription == null) {
            docDescription = DEF_DOC_DESCRIPTION;
        }
        return docDescription;
    }

    public static boolean start(RootDoc root) {

        String destFilePath = getDestinationFilePath(root.options());
        String apiHost = getApiHost(root.options());
        String docTitle = getDocumentTitle(root.options());
        String docDescription = getDocumentDescription(root.options());

        initWriter(destFilePath, apiHost, docTitle, docDescription);

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

    public static int optionLength(String option) {
        switch(option.toLowerCase()) {
            case OPT_DEST_DIR:          //-d DEST_DIR
                return 2;
            case OPT_API_HOST:          //-apihost "http://api.hostname.org/"
                return 2;
            case OPT_DOC_TITLE:         //-title "API documentation of Eyes Only project"
                return 2;
            case OPT_DOC_DESCRIPTION:   //-description "Few lines of description of this documentation."
                return 2;
            default:
                return 0;
        }
    }

    public static boolean validOptions(String options[][], 
                                       DocErrorReporter reporter) {
        boolean foundDestDirOption = false;
        boolean foundApiHostOption = false;
        boolean foundTitleOption = false;
        boolean foundDescriptionOption = false;

        for (int i = 0; i < options.length; i++) {
            String[] opt = options[i];
            switch(opt[0].toLowerCase()) {
                case OPT_DEST_DIR:
                    if(foundDestDirOption) {
                        reporter.printError("Only one " + OPT_DEST_DIR + " option allowed.");
                        return false;
                    } else {
                        foundDestDirOption = true;
                    }
                    break;
                case OPT_API_HOST:
                    if(foundApiHostOption) {
                        reporter.printError("Only one " + OPT_API_HOST + " option allowed.");
                        return false;
                    } else {
                        foundApiHostOption = true;
                    }
                    break;
                case OPT_DOC_TITLE:
                    if(foundTitleOption) {
                        reporter.printError("Only one " + OPT_DOC_TITLE + " option allowed.");
                        return false;
                    } else {
                        foundTitleOption = true;
                    }
                    break;
                case OPT_DOC_DESCRIPTION:
                    if(foundDescriptionOption) {
                        reporter.printError("Only one " + OPT_DOC_DESCRIPTION + " option allowed.");
                        return false;
                    } else {
                        foundDescriptionOption = true;
                    }
                    break;
            }
        }
        return true;
    }
}
