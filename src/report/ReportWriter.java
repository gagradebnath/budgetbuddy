package report;

import java.io.BufferedWriter;
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

    public ReportWriter() {
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
    }

    public abstract void writeReport(String filePath, ExpenseRepository repository) throws IOException;

    protected abstract void writeHeader(BufferedWriter writer) throws IOException;

    protected abstract void writeMonthlySummary(BufferedWriter writer, Summarizer summarizer) throws IOException;

    protected abstract void writeCategoryBreakdown(BufferedWriter writer, Summarizer summarizer) throws IOException;

    protected abstract void writeGrandTotal(BufferedWriter writer, Summarizer summarizer) throws IOException;

    protected abstract void writeRecentEntries(BufferedWriter writer, List<Expense> expenses) throws IOException;

    protected void writeFooter(BufferedWriter writer) throws IOException{
        
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
