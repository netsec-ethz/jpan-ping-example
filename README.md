# JPAN SCMP Ping Example

Sends SCION SCMP echo requests (SCION ping) with JPAN to:

- `64-2:0:9c,fd00::0034:0054:ffff:0000`
- `64-2:0:9c,fd00::0034:0055:ffff:0000`
- `64-2:0:9c,fd00::0035:0054:ffff:0000`
- `64-2:0:9c,fd00::0035:0055:ffff:0000`


## Installing Maven or Java
```bash
sudo apt update
sudo apt install -y openjdk-21-jdk
sudo apt install -y maven
mvn -v
```
## Running the example
```bash
mvn compile exec:java
```