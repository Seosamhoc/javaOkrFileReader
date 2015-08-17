/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package okrfilereader;
import java.io.*;
import java.util.*;
/**
 *
 * @author seosamh
 */
public class TestingMaths {
    public static void main(String[] args) {
        // TODO code application logic here
        TestingMaths thisProgram;
        thisProgram = new TestingMaths();
    }
    public TestingMaths(){
        //String result = "" + (Math.round(10.53*100)/100);
        double thingy = (double)Math.round(10.53001*100)/100;
        String result = "" + thingy;
        System.out.println(result);
    }
}
