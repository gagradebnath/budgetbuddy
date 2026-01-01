package report;

/**
 * Factory class for creating ReportWriter instances.
 * Implements the Factory design pattern to separate object creation logic.
 */
public class ReportWriterFactory {
    
    /**
     * Creates a ReportWriter based on the specified format type.
     *
     * @param format the format type ("txt" or "html")
     * @return the corresponding ReportWriter instance
     * @throws IllegalArgumentException if format is not supported
     */
    public static ReportWriter createReportWriter(String format) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        
        switch (format.toLowerCase()) {
            case "txt":
                return new TxtReportWriter();
            case "html":
                return new HtmlReportWriter();
            default:
                throw new IllegalArgumentException("Unsupported format: " + format + ". Supported formats: txt, html");
        }
    }
}
