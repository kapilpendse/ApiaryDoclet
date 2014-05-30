/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisquare;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

/**
 *
 * @author Kapil Pendse
 */
public class SimpleDoclet {
    public static boolean start(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (int i = 0; i < classes.length; i++) {
            System.out.println(classes[i].name());

            Tag[] tags = classes[i].tags();
            for (int j = 0; j < tags.length; j++) {
                System.out.println("  tag: " + tags[j].name());
            }

            FieldDoc fields[] = classes[i].fields();
            for (int j = 0; j < fields.length; j++) {
                System.out.println("  field: " + fields[j].name());
            }
            
            MethodDoc methods[] = classes[i].methods();
            for (int j = 0; j < methods.length; j++) {
                System.out.println("  method: " + methods[j].name());
                Tag[] methodTags = methods[j].tags();
                for (int k = 0; k < methodTags.length; k++) {
                    System.out.println("    tag: " + methodTags[k].name());
                }
            }
        }
        return true;
    }
}
