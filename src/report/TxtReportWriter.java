package report;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import model.Expense;
import service.ExpenseRepository;
import service.Summarizer;
import util.TextUtils;

/**
 * Writes plain-text expense reports with ASCII formatting.
 */
public class TxtReportWriter extends ReportWriter{
    @Override
    public void writeReport(String filePath, ExpenseRepository repository) throws IOException {
        List<Expense> allExpenses = repository.findAll();
        Summarizer summarizer = new Summarizer(allExpenses);

       
            writeHeader(exportReport);
            writeMonthlySummary(exportReport, summarizer);
            writeCategoryBreakdown(exportReport, summarizer);
            writeGrandTotal(exportReport, summarizer);
            writeRecentEntries(exportReport, allExpenses);
        

        System.out.println("Text report written to: " + filePath);
    }

    @Override
    protected void writeHeader(ExportReport exportReport) throws IOException {
        exportReport.add("=====================================\n");
        exportReport.add("       BUDGETBUDDY EXPENSE REPORT    \n");
        exportReport.add("=====================================\n\n");
    }

    @Override
    protected void writeMonthlySummary(ExportReport exportReport, Summarizer summarizer) throws IOException {
        exportReport.add("MONTHLY SUMMARY\n");
        exportReport.add(TextUtils.separator(60) + "\n");

        Map<YearMonth, Double> monthlyTotals = summarizer.monthlyTotals();
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            String monthStr = formatMonth(entry.getKey());
            String amountStr = formatAmount(entry.getValue());
            exportReport.add(String.format("%-10s : %12s\n", monthStr, amountStr));
        }
        exportReport.add("\n");
    }

    @Override
    protected void writeCategoryBreakdown(ExportReport exportReport, Summarizer summarizer) throws IOException {
        exportReport.add("CATEGORY BREAKDOWN (All Time)\n");
        exportReport.add(TextUtils.separator(60) + "\n");

        Map<String, Double> categoryTotals = summarizer.categoryTotals(null);
        double maxAmount = categoryTotals.values().stream()
                .max(Double::compareTo)
                .orElse(1.0);

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            String amountStr = formatAmount(amount);
            String bar = createBar(amount, maxAmount);
            exportReport.add(String.format("%-15s %12s  %s\n", category, amountStr, bar));
        }
        exportReport.add("\n");
    }

    @Override
    protected void writeGrandTotal(ExportReport exportReport, Summarizer summarizer) throws IOException {
        exportReport.add(TextUtils.separator(60) + "\n");
        exportReport.add(String.format("GRAND TOTAL: %s\n", formatAmount(summarizer.grandTotal())));
        exportReport.add(TextUtils.separator(60) + "\n");
    }

    @Override
    protected void writeRecentEntries(ExportReport exportReport, List<Expense> expenses) throws IOException {
        exportReport.add("\nRECENT ENTRIES (Last 10)\n");
        exportReport.add(TextUtils.separator(60) + "\n");

        int count = 0;
        for (int i = expenses.size() - 1; i >= 0 && count < 10; i--, count++) {
            Expense exp = expenses.get(i);
            String dateStr = formatDate(exp.getDate());
            exportReport.add(String.format("%s  %-12s %10s  %s\n",
                    dateStr,
                    exp.getCategory(),
                    formatAmount(exp.getAmount()),
                    exp.getNotes()));
        }
    }

    @Override
    protected String createBar(double value, double maxValue) {
        return TextUtils.createBar(value, maxValue, 30);
    }
}
