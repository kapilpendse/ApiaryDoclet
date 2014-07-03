/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisquare.util;

import java.util.Comparator;

/**
 *
 * @author Kapil Pendse
 */
public class SectionTitle implements Comparator<SectionTitle> {

    public String title;

    public SectionTitle() {
        title = "";
    }

    public SectionTitle(String t) {
        title = t;
    }

    @Override
    public int compare(SectionTitle o1, SectionTitle o2) {
        //if both titles begin with a number, use numeric sort
        try {
            StringBuilder o1NumericString = new StringBuilder();
            for(int i = 0; i < o1.title.length(); i++) {
                char ch = o1.title.charAt(i);
                if(Character.isDigit(ch)) {
                    o1NumericString.append(ch);
                }
            }
            int o1Number = Integer.parseInt(o1NumericString.toString());

            StringBuilder o2NumericString = new StringBuilder();
            for(int i = 0; i < o2.title.length(); i++) {
                char ch = o2.title.charAt(i);
                if(Character.isDigit(ch)) {
                    o2NumericString.append(ch);
                }
            }
            int o2Number = Integer.parseInt(o2NumericString.toString());
            return (o1Number - o2Number);
        } catch (Exception e) {
            //if only one of them or neither begins with a number, use alphabetical sort
            return o1.title.compareTo(o2.title);
        }
    }
    
}
