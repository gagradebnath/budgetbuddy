package report;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import model.Expense;
import service.ExpenseRepository;
import service.Summarizer;

/**
 * Writes HTML expense reports with basic inline styling.
 */
public class HtmlReportWriter extends ReportWriter{


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
            writeFooter(writer);
        }

        System.out.println("HTML report written to: " + filePath);
    }

    @Override
    protected void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("<!DOCTYPE html>\n");
        writer.write("<html>\n<head>\n");
        writer.write("<title>BudgetBuddy Expense Report</title>\n");
        writer.write("<style>\n");
        writer.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        writer.write("h1 { color: #333; }\n");
        writer.write("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n");
        writer.write("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        writer.write("th { background-color: #4CAF50; color: white; }\n");
        writer.write(".bar { background-color: #4CAF50; height: 20px; display: inline-block; }\n");
        writer.write(".total { font-weight: bold; font-size: 1.2em; color: #4CAF50; }\n");
        writer.write("</style>\n");
        writer.write("</head>\n<body>\n");
        writer.write("<h1>BudgetBuddy Expense Report</h1>\n");
    }

    @Override
    protected void writeMonthlySummary(BufferedWriter writer, Summarizer summarizer) throws IOException {
        writer.write("<h2>Monthly Summary</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>Month</th><th>Total Amount</th></tr>\n");

        Map<YearMonth, Double> monthlyTotals = summarizer.monthlyTotals();
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            String monthStr = formatMonth(entry.getKey());
            String amountStr = formatAmount(entry.getValue());
            writer.write(String.format("<tr><td>%s</td><td>%s</td></tr>\n", monthStr, amountStr));
        }
        writer.write("</table>\n");
    }

    @Override
    protected void writeCategoryBreakdown(BufferedWriter writer, Summarizer summarizer) throws IOException {
        writer.write("<h2>Category Breakdown (All Time)</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>Category</th><th>Total Amount</th><th>Visual</th></tr>\n");

        Map<String, Double> categoryTotals = summarizer.categoryTotals(null);
        double maxAmount = categoryTotals.values().stream()
                .max(Double::compareTo)
                .orElse(1.0);

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            String amountStr = formatAmount(amount);
            String barHtml = createBar(amount, maxAmount);
            writer.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    category, amountStr, barHtml));
        }
        writer.write("</table>\n");
    }

    @Override
    protected void writeGrandTotal(BufferedWriter writer, Summarizer summarizer) throws IOException {
        writer.write(String.format("<p class=\"total\">Grand Total: %s</p>\n",
                formatAmount(summarizer.grandTotal())));
    }

    @Override
    protected void writeRecentEntries(BufferedWriter writer, List<Expense> expenses) throws IOException {
        writer.write("<h2>Recent Entries (Last 10)</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>Date</th><th>Category</th><th>Amount</th><th>Notes</th></tr>\n");

        int count = 0;
        for (int i = expenses.size() - 1; i >= 0 && count < 10; i--, count++) {
            Expense exp = expenses.get(i);
            String dateStr = formatDate(exp.getDate());
            writer.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    dateStr,
                    exp.getCategory(),
                    formatAmount(exp.getAmount()),
                    exp.getNotes()));
        }
        writer.write("</table>\n");
    }

    @Override
    protected void writeFooter(BufferedWriter writer) throws IOException {
        writer.write("</body>\n</html>\n");
    }

    @Override
    protected String formatDate(LocalDate date) {
        return date.format(dateFormatter);
    }

    @Override
    protected String formatMonth(YearMonth month) {
        return month.format(monthFormatter);
    }

    @Override
    protected String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }

    @Override
    protected String createBar(double value, double maxValue) {
        int barWidth = (int) Math.round((value * 200) / maxValue);
        return String.format("<div class=\"bar\" style=\"width: %dpx;\"></div>", barWidth);
    }
}
