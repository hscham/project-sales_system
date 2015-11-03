3170 project (1516 T1 Gp 4)
===========================
Grace -- A3, S2, M2
Lok -- A1, A2, S1
Lun -- A4, M1, M3

./subprogram
------------
1. Bash files for saving time typing compile and run commands
    * runJava.sh -- in shell, enter command
      ```
      $ ./runJava.sh <java program name, without .java, e.g. SalesSystem>
      ```
      *When copied to another machine, enter command before use for first time*
      ```
      $ chmod +x runJava.sh
    ```
2. Tested Java programs
    * SalesSystem.java -- for adding finalized functions content, no extra debugging msg
    * CreateTable.java -- create all tables
    * DropTable.java -- drop all tables
    * LoadData.java -- load data from ./sample_data/ **some data entries in part and transactions are deleted because parent key of the rows do not exist (not initialized in corresponding data files)**
      *when executed in directory subprogram/, enter path ../sample_data if same directory tree as this git repo*
    * Basic.java -- could be copy as foundation code of other functions
