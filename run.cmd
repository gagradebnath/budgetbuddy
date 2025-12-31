dir /s /b *.java > sources.txt
javac -d out @sources.txt
java -cp out TestHarness
java -cp out Main data/expenses.csv

