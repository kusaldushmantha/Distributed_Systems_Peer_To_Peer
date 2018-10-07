# distributed-client

<b>To create the jar, from project root</b> 

If you have installed maven use  
  ```mvn clean install```    

otherwise use  
  ```./mvnw clean install``` on unix based os  
  ```./mvnw.cmd clean install``` on windows  

<b>To run</b>  
```
java -jar target/dsapp-1.jar  
```

Application commands

| Command                          | Description                         | Example              |
| -------------------------------- |-------------------------------------|----------------------|
| `reg --ip ip_of_bootstrap_server`     | register to bootstrap server (bs)   | `reg --ip 192.168.43.139` |
| `regl`                           | register to bs in same ip           |                      |
| `unreg`                          | unregister from bootstrap server    |                      |
| `join`                           | sending join commands to neighbours |                      |
| `leave`                          | sending leave commands to neighbours|                      |
| `table`                          | show routing table                  |                      |
| `files`                          | show selected files                 |                      |
| `search --name file_name --hops hops[optional]`| file search from neighbours, hops to go naighbours of neighbour | `search --name moon.jpg --hops 3` |
| `appexit`                        | exit from application followed by 'unreg' and 'leave' |    |
| `apphelp`                        | show application commands           |                      |
| `setport --port port`                   | hange port if registration failed   | `setport --port 5656`       |
