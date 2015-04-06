package tests;

import charpov.grader.*;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

class TestHW2{
    public static void main (String[] args) throws Exception {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
	for( String clazz : args){
            if ( !clazz.contains("HW2Grading") ) continue;
            if ( clazz.contains("Sample") ) continue;
            if ( clazz.equals("TestHW2")) continue;
            if ( clazz.equals("HW2Grading0")) continue;
            list.add(Class.forName("tests."+clazz));

        }
        java.util.logging.Logger.getLogger("charpov.grader")
          .setLevel(java.util.logging.Level.WARNING);
	Class<?>[] testList = new Class<?>[list.size()];
        list.toArray(testList);
        Tester tester = new Tester(testList);
	FileOutputStream fos = new FileOutputStream("../run2.out");
        tester.setOutputStream(fos);
        double result = tester.call();
        PrintWriter pw = new PrintWriter(fos, true);
        pw.printf("Total: %2.3f/%s%n", result * tester.getTotalPoints(), 100);
    }
}
