/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisquare.writers.apiary;

import java.util.Map;

/**
 *
 * @author Kapil Pendse
 */
public class ServiceRequest {
    /**
     * HTTP method supported by the service e.g. POST
     */
    public String httpMethod;
    /**
     * URI of the service e.g. /api/{bucket}/login
     */
    public String uri;
    /**
     * Content type header in service request e.g. application/json
     * Optional.
     */
    public String contentType;
    /**
     * Character encoding of service request e.g. utf-8
     * Optional.
     */
    public String charset;
    /**
     * List of input parameters along with example values e.g. "user-name", "demo"
     */
    public Map<String,String> inputParameters;
}
