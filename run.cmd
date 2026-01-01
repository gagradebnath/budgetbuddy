dir /s /b *.java > sources.txt
javac -d out @sources.txt
java -cp out TestHarness
@REM java -cp out Main data/expenses.csv

