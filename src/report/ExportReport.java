package report;

import java.io.BufferedWriter;

public class ExportReport {
    public StringBuilder content = new StringBuilder();

    public void add(String text) {
        content.append(text);
    }

    public String toString() {
        return content.toString();
    }

    public boolean  saveReport(String fileName) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(fileName))) {
            writer.write(content.toString());
        }
        return true;
    }
}
