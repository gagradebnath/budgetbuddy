dir /s /b *.java > sources.txt
javac -d out @sources.txt
java -cp out TestHarness


