package report;

import java.io.IOException;
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
            writeHeader(exportReport);
            writeMonthlySummary(exportReport, summarizer);
            writeCategoryBreakdown(exportReport, summarizer);
            writeGrandTotal(exportReport, summarizer);
            writeRecentEntries(exportReport, allExpenses);
            writeFooter(exportReport);
        
        try {
            if(exportReport.saveReport(filePath)  ){
                System.out.println("HTML report written to: " + filePath);
            }
        } catch (Exception ex) {
            System.getLogger(HtmlReportWriter.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    protected void writeHeader(ExportReport exportReport) throws IOException {
        exportReport.add("<!DOCTYPE html>\n");
        exportReport.add("<html>\n<head>\n");
        exportReport.add("<title>BudgetBuddy Expense Report</title>\n");
        exportReport.add("<style>\n");
        exportReport.add("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        exportReport.add("h1 { color: #333; }\n");
        exportReport.add("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n");
        exportReport.add("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        exportReport.add("th { background-color: #4CAF50; color: white; }\n");
        exportReport.add(".bar { background-color: #4CAF50; height: 20px; display: inline-block; }\n");
        exportReport.add(".total { font-weight: bold; font-size: 1.2em; color: #4CAF50; }\n");
        exportReport.add("</style>\n");
        exportReport.add("</head>\n<body>\n");
        exportReport.add("<h1>BudgetBuddy Expense Report</h1>\n");
    }

    @Override
    protected void writeMonthlySummary(ExportReport exportReport, Summarizer summarizer) throws IOException {
        exportReport.add("<h2>Monthly Summary</h2>\n");
        exportReport.add("<table>\n");
        exportReport.add("<tr><th>Month</th><th>Total Amount</th></tr>\n");

        Map<YearMonth, Double> monthlyTotals = summarizer.monthlyTotals();
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            String monthStr = formatMonth(entry.getKey());
            String amountStr = formatAmount(entry.getValue());
            exportReport.add(String.format("<tr><td>%s</td><td>%s</td></tr>\n", monthStr, amountStr));
        }
        exportReport.add("</table>\n");
    }

    @Override
    protected void writeCategoryBreakdown(ExportReport exportReport, Summarizer summarizer) throws IOException {
        exportReport.add("<h2>Category Breakdown (All Time)</h2>\n");
        exportReport.add("<table>\n");
        exportReport.add("<tr><th>Category</th><th>Total Amount</th><th>Visual</th></tr>\n");

        Map<String, Double> categoryTotals = summarizer.categoryTotals(null);
        double maxAmount = categoryTotals.values().stream()
                .max(Double::compareTo)
                .orElse(1.0);

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            String amountStr = formatAmount(amount);
            String barHtml = createBar(amount, maxAmount);
            exportReport.add(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    category, amountStr, barHtml));
        }
        exportReport.add("</table>\n");
    }

    @Override
    protected void writeGrandTotal(ExportReport exportReport, Summarizer summarizer) throws IOException {
        exportReport.add(String.format("<p class=\"total\">Grand Total: %s</p>\n",
                formatAmount(summarizer.grandTotal())));
    }

    @Override
    protected void writeRecentEntries(ExportReport exportReport, List<Expense> expenses) throws IOException {
        exportReport.add("<h2>Recent Entries (Last 10)</h2>\n");
        exportReport.add("<table>\n");
        exportReport.add("<tr><th>Date</th><th>Category</th><th>Amount</th><th>Notes</th></tr>\n");

        int count = 0;
        for (int i = expenses.size() - 1; i >= 0 && count < 10; i--, count++) {
            Expense exp = expenses.get(i);
            String dateStr = formatDate(exp.getDate());
            exportReport.add(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    dateStr,
                    exp.getCategory(),
                    formatAmount(exp.getAmount()),
                    exp.getNotes()));
        }
        exportReport.add("</table>\n");
    }

    @Override
    protected void writeFooter(ExportReport exportReport) throws IOException {
        exportReport.add("</body>\n</html>\n");
    }

    
    @Override
    protected String createBar(double value, double maxValue) {
        int barWidth = (int) Math.round((value * 200) / maxValue);
        return String.format("<div class=\"bar\" style=\"width: %dpx;\"></div>", barWidth);
    }
}
