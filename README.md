# calcify

application does not run because mysql dependency is looking for a data source configuration.

can't expose db password and other sensitive data:
 - use jasypt -- https://github.com/ulisesbocchio/jasypt-spring-boot
 - see `src/main/resources/application.yml`
 - IntelliJ, Run/Debug Configurations -> Modify Options -> Add VM options
     - add like so ...
   ```
   -Djasypt.encryptor.password="yourencryptorpasswordhere"
   ```
     - run as normally now
