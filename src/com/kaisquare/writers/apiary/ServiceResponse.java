/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisquare.writers.apiary;

/**
 *
 * @author Kapil Pendse
 */
public class ServiceResponse {
    /**
     * HTTP response code e.g. 200
     */
    public String httpResponseCode;
    /**
     * Content type header in service request e.g. application/json
     * Optional.
     */
    public String contentType;
    /**
     * Character encoding of service request e.g. UTF-8
     * Optional.
     */
    public String charset;
    public String response;
}
