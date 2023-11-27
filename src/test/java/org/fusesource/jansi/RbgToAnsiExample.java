package org.fusesource.jansi;

import org.fusesource.jansi.io.Colors;

public class RbgToAnsiExample {


    public static void main(String[] args){
        System.out.println(Colors.rgbToAnsi(100,200,200));
        System.out.println("text");
    }

}
