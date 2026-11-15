# Implementation Steps

1. Install JDK 17 (or JDK 11+). Open CMD and verify:
   `java -version` and `javac -version`.
2. Extract the ZIP to a simple path, for example `C:\CSA09-Assignment3`.
3. Open CMD and move to the extracted project root:
   `cd /d C:\CSA09-Assignment3`
4. Create the output folder:
   `mkdir out`
5. Compile and run Task01:
   `javac -d out Task01\ATM.java`
   `java -cp out ATM 123456 1000`
   Then test invalid inputs: `java -cp out ATM ABC 1000`, `java -cp out ATM 123456 -50`, and `java -cp out ATM 123456 7000`.
6. Compile and run Task02:
   `javac -d out Task02\StorageDemo.java`
   `java -cp out StorageDemo`
7. Compile and run Task03:
   `javac -d out Task03\SalesReport.java`
   `java -cp out SalesReport`
   Open `sample-data\manual-report.txt` and `sample-data\safe-report.txt` and verify they match.
8. Compile and run Task04:
   `javac -d out Task04\ThreadStations.java`
   `java -cp out ThreadStations`
   Repeat at least ten times and record the finish order. For priority tests, set `setPriority()` before `start()`; note that priority is only a scheduling hint.
9. Compile and run Task05:
   `javac -d out Task05\SeatBooking.java`
   `java -cp out SeatBooking`
   Repeat ten times. The synchronized implementation should always report `Seats sold: 10`. For the required unsynchronized experiment, make a separate copy and remove `synchronized`; do not overwrite the final safe version.
10. Compile and run Task06:
    `javac -d out Task06\TokenCounter.java Task06\DeadlockDemo.java`
    `java -cp out TokenCounter`
    `java -cp out DeadlockDemo`
    The expected token sum is 210. The deadlock-safe transfer uses ascending wallet IDs for lock acquisition.
11. Compile and run Task07:
    `javac -d out Task07\ResultProcessor.java`
    `java -cp out ResultProcessor 100` (quick check) or `java -cp out ResultProcessor` (default 10,000-file run).
    Record the four pool timings (1, 2, 4, 8) from your own machine. Repeat if possible and use averages.
12. For every task, capture the console output. Also capture class diagrams/state diagrams and debugger views where requested. Use actual IDE breakpoints for Task03 and actual experiment outputs for Q4/Q5/Q7.
13. Create the report using the supplied answer document as a base. Fill in student names/register numbers, actual timings, run-order table, test results, and GitHub links.
14. Initialize and commit progressively:
    `git init`
    `git add README.md Task01`
    `git commit -m "Complete Task 1 ATM exception handling"`
    Repeat `git add Task02` / commit for each task through Task07, then add the report and screenshots.
15. Create GitHub repository `CSA09-<RegisterNumber>-Assignment3`, push your commits, and add faculty as collaborator if private. Each partner must create and execute their own repository.
