package structuralAnalysis;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import util.AbstractClassReader;
import util.ReflectionClassReader;

/**
 * Created by neilwalkinshaw on 24/10/2017.
 */
public class ClassDiagram {

    protected Map<String,String> inheritance;
    protected Map<String,Set<String>> associations;
    //include classes in the JDK etc? Can produce crowded diagrams.
    protected boolean ignoreLibraryClasses;
    protected boolean ignoreInnerClasses;
    protected String signaturePrefix = "";
    protected Set<String> allClassNames;

    /**
     * Instantiating the class will populate the inheritance and association relations.
     * @param root
     */
    public ClassDiagram(String root, boolean ignoreLibs, boolean ignoreInnerClasses,
                        String signaturePrefix){
        this.ignoreLibraryClasses = ignoreLibs;
        this.signaturePrefix = signaturePrefix;
        this.ignoreInnerClasses = ignoreInnerClasses;

        File dir = new File(root);
        ReflectionClassReader rcr = new ReflectionClassReader();

        List<Class<?>> classes = rcr.processDirectory(dir,"");
        inheritance = new HashMap<String, String>();
        associations = new HashMap<String, Set<String>>();
        allClassNames = new HashSet<String>();

        for(Class cl : classes){
            if(includeClass(cl))
                allClassNames.add(getClassName(cl));
        }

        for(Class cl : classes){
            if(!includeClass(cl))
                continue;
            extractInheritanceRelationships(cl);
            extractAssociationRelationships(cl);
        }
    }

    /**
     * For a class cl, identify the fields that are declared within it.
     * Store the fields as a set of String objects, where a String refers
     * to the type of the Field (we are not interested in primitive types,
     * only in fields that correspond to other classes). Use the 'associations'
     * map to store the associations.
     * @param cl
     */
    private void extractAssociationRelationships(Class cl) {
       //ADD YOUR CODE HERE
    }

    /**
     * For a given class cl, if it has a super-class (a parent-class)
     * add an inheritance relation from cl to the parent-class. Use the
     * 'inheritance' map to store this.
     * @param cl
     */
    private void extractInheritanceRelationships(Class cl) {
        //ADD YOUR CODE HERE
    }

    protected boolean includeClass(Class cl){
        if(cl.getPackage()== null){
            if(!signaturePrefix.isEmpty())
                return false;
        }
        else if(!cl.getPackage().getName().startsWith(signaturePrefix))
            return false;
        if (ignoreLibraryClasses) {
            if(!allClassNames.contains(cl.getName()))
                return false;
        }

        return true;
    }

    protected String getClassName(Class cl){
        if(cl.getEnclosingClass()!=null && ignoreInnerClasses)
            return cl.getEnclosingClass().getName();
        else
            return cl.getName();
    }

    public String toString(){
        StringBuffer dotGraph = new StringBuffer();
        dotGraph.append("digraph classDiagram{\n" +
                "graph [splines=ortho, rankdir=BT]\n\n");

        for(String className : allClassNames){
            dotGraph.append("\""+className + "\"[shape = box];\n");
        }

        //Add inheritance relations
        for(String childClass : inheritance.keySet()){
            String from = "\""+childClass +"\"";
            String to = "\""+inheritance.get(childClass)+"\"";
            dotGraph.append(from+ " -> "+to+"[arrowhead = onormal];\n");
        }

        //Add associations
        for(String cls : associations.keySet()){
            Set<String> fields = associations.get(cls);
            for(String field : fields) {
                String from = "\""+cls +"\"";
                String to = "\""+field+"\"";
                dotGraph.append(from + " -> " +to + "[arrowhead = diamond];\n");
            }
        }

        dotGraph.append("}");
        return dotGraph.toString();
    }

    /**
     * Write out the class diagram to a specified file.
     * @param target
     */
    public void writeDot(File target) throws IOException {
        BufferedWriter fw = new BufferedWriter(new FileWriter(target));
        fw.write(toString());
        fw.flush();
        fw.close();
    }


}
