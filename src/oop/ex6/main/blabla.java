package oop.ex6.main;
import oop.ex6.CompileErrorException;
import oop.ex6.dataStructures.GlobalVariable;

import java.util.HashMap;

public class blabla {


    public static void main(String[] args) {

        String bla = "   int b=1; ";
        String a = "    int a;";
        String b = "   a=b;";
//        String cond = "true";
        String c = "   int a=b , c=d;";
        try {
            HashMap<String,GlobalVariable> globals = new HashMap<>();
            regexManager.isValidGlobalVariable(bla, globals);
            regexManager.isValidGlobalVariable(a, globals);
            regexManager.isValidGlobalVariable( c, globals);
            System.out.println(globals.isEmpty());
        } catch (CompileErrorException e) {
            System.out.println(222);
        }

//        String[] listcond = cond.split("([|]{2}|[&]{2})");
//        for (int i = 0; i < listcond.length; i++) {
//            System.out.println(listcond[i]);
//        }


    }
}
