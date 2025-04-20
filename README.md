## Spring Cloud Stream & Azure Servicebus Emulator - Producer Example

### Prerequisites
- Docker
- docker-compose
- SCS-Receiver (Spring Cloud Stream & Azure Servicebus Emulator Consumer Example)

### Setup
- Create a folder called "servicebus"
- Create a file called "config.json" in "servicebus" folder

> [!NOTE]
> 'Name' field under 'Namespaces' and 'Name' field under 'Queues' are important

```
{
 "UserConfig": {
   "Namespaces": [
     {
       "Name": "sbemulatorns",
       "Queues": [
         {
           "Name": "queue.1",
           "Properties": {
             "DeadLetteringOnMessageExpiration": false,
             "DefaultMessageTimeToLive": "PT1H",
             "DuplicateDetectionHistoryTimeWindow": "PT20S",
             "ForwardDeadLetteredMessagesTo": "",
             "ForwardTo": "",
             "LockDuration": "PT1M",
             "MaxDeliveryCount": 3,
             "RequiresDuplicateDetection": false,
             "RequiresSession": false
           }
         }
       ]
     }
   ],
   "Logging": {
     "Type": "File"
   }
 }
}
```
- Create a file called "docker-compose.yaml" in "servicebus" folder
```
name: microsoft-azure-servicebus-emulator
services:
  emulator:
    container_name: "servicebus-emulator"
    image: mcr.microsoft.com/azure-messaging/servicebus-emulator:latest
    pull_policy: always
    volumes:
      - "${CONFIG_PATH}:/ServiceBus_Emulator/ConfigFiles/Config.json"
    ports:
      - "5672:5672"
      - "5300:5300"
    environment:
      SQL_SERVER: sqledge
      MSSQL_SA_PASSWORD: "${MSSQL_SA_PASSWORD}"  # Password should be same as what is set for SQL Edge  
      ACCEPT_EULA: ${ACCEPT_EULA}
      SQL_WAIT_INTERVAL: ${SQL_WAIT_INTERVAL} # Optional: Time in seconds to wait for SQL to be ready (default is 15 seconds)
    depends_on:
      - sqledge
    networks:
      sb-emulator:
        aliases:
          - "sb-emulator"
  sqledge:
        container_name: "sqledge"
        image: "mcr.microsoft.com/azure-sql-edge:latest"
        networks:
          sb-emulator:
            aliases:
              - "sqledge"
        environment:
          ACCEPT_EULA: ${ACCEPT_EULA}
          MSSQL_SA_PASSWORD: "${MSSQL_SA_PASSWORD}" # To be filled by user as per policy : https://learn.microsoft.com/en-us/sql/relational-databases/security/strong-passwords?view=sql-server-linux-ver16 

networks:
  sb-emulator:
```
- Create a file called "dev.env" in "servicebus" folder and update values according to your setup
```
# Environment file for user defined variables in docker-compose.yml
CONFIG_PATH="/Users/YOUR_USER_NAME/Desktop/servicebus/config.json"

# 2. ACCEPT_EULA: Pass 'Y' to accept license terms for Azure SQL Edge and Azure Service Bus emulator.
# Service Bus emulator EULA : https://github.com/Azure/azure-service-bus-emulator-installer/blob/main/EMULATOR_EULA.txt
# SQL Edge EULA : https://go.microsoft.com/fwlink/?linkid=2139274
ACCEPT_EULA="Y"

# 3. Set a MSSQL_SA_PASSWORD with at least 8 chars (DO NOT LEFT EMPTY)
MSSQL_SA_PASSWORD="YOUR_PASSWORD_HERE"
```
- Run docker-compose file in terminal in "servicebus" folder e.g.
```
cd servicebus 
docker-compose -f docker-compose.yaml --env-file dev.env up
```
- If all goes fine "service emulator" is running
- Start SCS_Sender application on your IDE
- Send a request to its Hello API to send a message to Azure Servicebus e.g.
```
curl localhost:8081/hello/John
```

### References
- https://learn.microsoft.com/en-us/azure/service-bus-messaging/test-locally-with-service-bus-emulator?tabs=docker-linux-container
- https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/developer-guide-overview#spring-cloud-stream-binder-for-azure-service-bus

