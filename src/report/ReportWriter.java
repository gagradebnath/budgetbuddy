package report;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Expense;
import service.ExpenseRepository;
import service.Summarizer;


public abstract  class ReportWriter {
    protected final DateTimeFormatter dateFormatter;
    protected final DateTimeFormatter monthFormatter;
    protected ExportReport exportReport;
    public ReportWriter() {
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        this.exportReport = new ExportReport();
    }

    public abstract void writeReport(String filePath, ExpenseRepository repository) throws IOException;

    protected abstract void writeHeader(ExportReport exportReport) throws IOException;

    protected abstract void writeMonthlySummary(ExportReport exportReport, Summarizer summarizer) throws IOException;

    protected abstract void writeCategoryBreakdown(ExportReport exportReport, Summarizer summarizer) throws IOException;

    protected abstract void writeGrandTotal(ExportReport exportReport, Summarizer summarizer) throws IOException;

    protected abstract void writeRecentEntries(ExportReport exportReport, List<Expense> expenses) throws IOException;

    protected void writeFooter(ExportReport exportReport) throws IOException{
        
    }

    protected String formatDate(LocalDate date) {
        return date.format(dateFormatter);
    }

    protected String formatMonth(YearMonth month) {
        return month.format(monthFormatter);
    }

    protected String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }

    protected abstract String createBar(double value, double maxValue);
}
