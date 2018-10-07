# distributed-client

To create the jar, from project root 

If you have installed maven use  
  ```mvn clean install```    
otherwise use  
  ```./mvnw clean install``` on unix based os  
  ```./mvnw.cmd clean install``` on windows  

To run  
```
java -jar target/dsapp-1.jar  
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
| `files`                          | show selected files                 |                      |
| `search file_name hops[optional]`| file search from neighbours, hops to go naighbours of neighbour | `search moon.jpg 3` |
| `appexit`                        | exit from application followed by 'unreg' and 'leave' |    |
| `apphelp`                        | show application commands           |                      |
| `setport port`                   | hange port if registration failed   | `setport 5656`       |
