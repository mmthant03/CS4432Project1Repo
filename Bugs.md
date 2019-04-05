Robert Dutile, Myo Min Thant

Bugs:

In this project, we have so far not found any bugs, save that the DROP TABLE command for SQL does not seem to work, which we currently believe to actually be a limitation of SimpleDB itself.

Notes: We cannot get the buffer info printing while running Examples.java. 
We tried with importing simpledb.server.SimpleDB and called the static method bufferMgr().
However, we only get null values from that method. Thus, we ended up unresolving this task.
