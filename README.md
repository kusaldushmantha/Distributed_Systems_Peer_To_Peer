# distributed-client

<b>To create the jar, from project root</b> 

If you have installed maven use  
  ```mvn clean install -DskipTests```    

otherwise use  
  ```./mvnw clean install -DskipTests``` on unix based os  
  ```./mvnw.cmd clean install  -DskipTests``` on windows  

<b>To run</b>  
```
java -jar target/dsapp-1.jar  
```

<b>Application commands</b>

| Command                          | Description                         | Example              |
| -------------------------------- |-------------------------------------|----------------------|
| `reg --ip ip_of_bootstrap_server`     | register to bootstrap server (bs)   | `reg --ip 192.168.43.139` |
| `regl`                           | register to bs in same ip           |                      |
| `unreg`                          | unregister from bootstrap server    |                      |
| `join`                           | sending join commands to neighbours |                      |
| `leave`                          | sending leave commands to neighbours|                      |
| `table`                          | show routing table                  |                      |
| `files`                          | show selected files                 |                      |
| `search --n file_name --h hops[optional]`| file search from neighbours, hops to go naighbours of neighbour | `search --name moon.jpg --hops 3` |
| `exit`                        | exit from application followed by 'unreg' and 'leave' |    |
| `help`                        | show application commands           |                      |
| `setport --p port`                   | hange port if registration failed   | `setport --port 5656`       |
