# distributed-client

Compile the application, From project root 
```
cd src
javac -cp . com/company/Client.java
```

Then run
```
java com.company.Client
```

Application commands

| Command                      | Description                         | Example              |
| ---------------------------- |-------------------------------------|----------------------|
| `reg ip_of_bootstrap_server` | register to bootstrap server        | `reg 192.168.43.139` |
| `unreg`                      | unregister from bootstrap server    |                      |
| `table`                      | show routing table                  |                      |
| `setport new_port`           | change the port                     | `setport 5656`       |
| `join`                       | sending join commands to neighbours |                      |
| `leave`                      | sending leave commands to neighbours|                      |
| `search file_name`           | file search from neighbours         | `search baby.mp3`    |
