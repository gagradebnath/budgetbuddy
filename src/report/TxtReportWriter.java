package report;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writeHeader(writer);
            writeMonthlySummary(writer, summarizer);
            writeCategoryBreakdown(writer, summarizer);
            writeGrandTotal(writer, summarizer);
            writeRecentEntries(writer, allExpenses);
        }

        System.out.println("Text report written to: " + filePath);
    }

    @Override
    protected void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("=====================================\n");
        writer.write("       BUDGETBUDDY EXPENSE REPORT    \n");
        writer.write("=====================================\n\n");
    }

    @Override
    protected void writeMonthlySummary(BufferedWriter writer, Summarizer summarizer) throws IOException {
        writer.write("MONTHLY SUMMARY\n");
        writer.write(TextUtils.separator(60) + "\n");

        Map<YearMonth, Double> monthlyTotals = summarizer.monthlyTotals();
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            String monthStr = formatMonth(entry.getKey());
            String amountStr = formatAmount(entry.getValue());
            writer.write(String.format("%-10s : %12s\n", monthStr, amountStr));
        }
        writer.write("\n");
    }

    @Override
    protected void writeCategoryBreakdown(BufferedWriter writer, Summarizer summarizer) throws IOException {
        writer.write("CATEGORY BREAKDOWN (All Time)\n");
        writer.write(TextUtils.separator(60) + "\n");

        Map<String, Double> categoryTotals = summarizer.categoryTotals(null);
        double maxAmount = categoryTotals.values().stream()
                .max(Double::compareTo)
                .orElse(1.0);

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            String amountStr = formatAmount(amount);
            String bar = createBar(amount, maxAmount);
            writer.write(String.format("%-15s %12s  %s\n", category, amountStr, bar));
        }
        writer.write("\n");
    }

    @Override
    protected void writeGrandTotal(BufferedWriter writer, Summarizer summarizer) throws IOException {
        writer.write(TextUtils.separator(60) + "\n");
        writer.write(String.format("GRAND TOTAL: %s\n", formatAmount(summarizer.grandTotal())));
        writer.write(TextUtils.separator(60) + "\n");
    }

    @Override
    protected void writeRecentEntries(BufferedWriter writer, List<Expense> expenses) throws IOException {
        writer.write("\nRECENT ENTRIES (Last 10)\n");
        writer.write(TextUtils.separator(60) + "\n");

        int count = 0;
        for (int i = expenses.size() - 1; i >= 0 && count < 10; i--, count++) {
            Expense exp = expenses.get(i);
            String dateStr = formatDate(exp.getDate());
            writer.write(String.format("%s  %-12s %10s  %s\n",
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
