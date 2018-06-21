package oop.ex6.main;
import java.util.*;

import oop.ex6.CompileErrorException;
import oop.ex6.GlobalVariable;
import oop.ex6.Variable;
import java.util.*;
import java.util.regex.Pattern;

public class blabla {


    public static void main(String[] args) {

        String bla = "   int b=1; ";
        String a = "    int a;";
        String b = "   a=b;";
//        String c = "   int a=b , c=d;";
        try {
            HashMap<String,GlobalVariable> globals = new HashMap<>();
            regexManager.isValidGlobalVariable(bla, globals);
            regexManager.isValidGlobalVariable(a, globals);
//            regexManager.isValidGlobalVariable( c, globals);
//            System.out.println(globals.isEmpty());
        } catch (CompileErrorException e) {
            System.out.println(222);
        }
    }
}
