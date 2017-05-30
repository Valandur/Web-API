package valandur.webapi.json;

import javax.tools.Diagnostic;
import java.io.*;
import java.util.Locale;

public class DiagnosticListener implements javax.tools.DiagnosticListener {

    private PrintWriter writer;

    public void startLog(String logFile) {
        if (writer != null)
            this.stopLog();

        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile), "utf-8"));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void stopLog() {
        if (writer != null){
            writer.close();
        }
    }

    public void writeException(Exception e) {
        if (writer == null) return;
        e.printStackTrace(writer);
    }

    @Override
    public void report(Diagnostic diagnostic) {
        if (writer == null) return;

        String line = "[" + diagnostic.getKind().name() + "] ";
        if (diagnostic.getKind() == Diagnostic.Kind.ERROR || diagnostic.getKind() == Diagnostic.Kind.WARNING)
            line += "Line " + diagnostic.getLineNumber() + ":" + diagnostic.getColumnNumber() + " | ";
        line += diagnostic.getMessage(Locale.getDefault()) + System.lineSeparator();
        writer.write(line);
    }
}
