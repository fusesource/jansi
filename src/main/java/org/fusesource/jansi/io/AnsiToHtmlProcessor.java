package org.fusesource.jansi.io;

import java.io.IOException;
import java.io.OutputStream;

public class AnsiToHtmlProcessor extends AnsiProcessor {
    private boolean concealOn = false;
    private HtmlAnsiOutputStream haos;

    AnsiToHtmlProcessor(OutputStream os) {
        super(os);
    }

    public HtmlAnsiOutputStream getHtmlAnsiOutputStream() {
        return haos;
    }

    public void setHtmlAnsiOutputStream(HtmlAnsiOutputStream haos) {
        this.haos = haos;
    }

    @Override
    protected void processSetAttribute(int attribute) throws IOException {
        switch (attribute) {
            case ATTRIBUTE_CONCEAL_ON:
                haos.write("\u001B[8m");
                concealOn = true;
                break;
            case ATTRIBUTE_INTENSITY_BOLD:
                haos.writeAttribute("b");
                break;
            case ATTRIBUTE_INTENSITY_NORMAL:
                haos.closeAttributes();
                break;
            case ATTRIBUTE_UNDERLINE:
                haos.writeAttribute("u");
                break;
            case ATTRIBUTE_UNDERLINE_OFF:
                haos.closeAttributes();
                break;
            case ATTRIBUTE_NEGATIVE_ON:
                break;
            case ATTRIBUTE_NEGATIVE_OFF:
                break;
            default:
                break;
        }
    }

    @Override
    protected void processAttributeReset() throws IOException {
        if (concealOn) {
            haos.write("\u001B[0m");
            concealOn = false;
        }
        haos.closeAttributes();
    }

    @Override
    protected void processSetForegroundColor(int color, boolean bright) throws IOException {
        haos.writeAttribute("span style=\"color: " + HtmlAnsiOutputStream.ANSI_COLOR_MAP[color] + ";\"");
    }

    @Override
    protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
        haos.writeAttribute("span style=\"background-color: " + HtmlAnsiOutputStream.ANSI_COLOR_MAP[color] + ";\"");
    }
}
