package valandur.webapi.misc;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import java.io.*;
import java.util.Locale;

public class WebAPIDiagnosticListener implements DiagnosticListener {

    private Writer writer;

    public void startLog(String logFile) throws FileNotFoundException, UnsupportedEncodingException {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "utf-8"));
    }
    public void stopLog() throws IOException {
        if (writer != null) writer.close();
    }

    @Override
    public void report(Diagnostic diagnostic) {
        try {
            writer.write(diagnostic.getLineNumber() + ":" + diagnostic.getColumnNumber() +
                    " | " + diagnostic.getMessage(Locale.getDefault()));
        } catch (IOException ignored) {}
    }
}
