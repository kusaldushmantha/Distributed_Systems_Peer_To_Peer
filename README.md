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

| Command                          | Description                         | Example              |
| -------------------------------- |-------------------------------------|----------------------|
| `reg ip_of_bootstrap_server`     | register to bootstrap server (bs)   | `reg 192.168.43.139` |
| `regl`                           | register to bs in same ip           |                      |
| `unreg`                          | unregister from bootstrap server    |                      |
| `join`                           | sending join commands to neighbours |                      |
| `leave`                          | sending leave commands to neighbours|                      |
| `table`                          | show routing table                  |                      |
| `search file_name hops[optional]`| file search from neighbours, hops to go naighbours of neighbour | `search moon.jpg 3` |
| `exit`                           | exit from application followed by 'unreg' and 'leave' |    |
| `help`                           | show application commands           |                      |
| `setport port`                   | hange port if registration failed   | `setport 5656`       |
