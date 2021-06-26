package org.fusesource.jansi.utils;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.io.HtmlAnsiOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UtilsAnsiHtml {

    /**
     * Converts the provided {@link Ansi} into a html String. <br>
     * Uses {@link HtmlAnsiOutputStream} to achieve this.
     */
    public String convertAnsiToHtml(Ansi ansi) throws IOException {
        return convertAnsiToHtml(ansi.toString());
    }

    /**
     * Converts the provided ansi String into a html String. <br>
     * Uses {@link HtmlAnsiOutputStream} to achieve this.
     */
    public String convertAnsiToHtml(String ansiString) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try(HtmlAnsiOutputStream hos = new HtmlAnsiOutputStream(os)){
            hos.write(ansiString.getBytes(StandardCharsets.UTF_8));
        }
        os.close();
        return os.toString();
    }

}
