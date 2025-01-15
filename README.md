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

### Lombok/Spring/IntelliJ issue.
lombok in `domain.User` class is not building out the getters and setters correctly causing an error.
The error occurs in the `repository.implementation.UserRepositoryImpl` class.

```java

@Override
public User create(User user) {
    // Check whether the email is unique
    if (getEmailCount(user.getEmail()
            .trim()
            .toLowerCase()) > 0)
        throw new ApiException("Email already in use. Please use a different email and try again.");
}

```

*Compiler complains that `user.getEmail()` is not found*. This points the issue to lombok.

things that did not work:
- enabling annotations
- invalidating VCS cache
- invalidating cache all options
- downgrading project to 3.3.* from 3.4.*
- deleting .idea directory and restarting ide

#### Solution
The solution is a combination of doing `mvn clean install`, followed by attempting to run the application, invalidating the VCS cache, restart IntelliJ and re-run the program, and then go into the "Enable Annotation Processing" settings and doing the steps below:

https://intellij-support.jetbrains.com/hc/en-us/community/posts/23064675521682-Lombok-not-workin-with-Intellij

File > Settings > Build,Execution,Deployment > Compiler > Annotation Processors
```
Ok found the culprit: 

With new project the processor is “obtain from a static jar file” … that do not exist.  

   Switching back to the old default version (option that I have on old project)  it is OK. 

So solution:  Switch to “obtain processors from project classpath”.
```

![Lombok error fix in IntelliJ IDE](imgs_md/lombok_issue_solution_intellij.png)

Processor path to lombok:
```shell
/home/seong/.m2/repository/org/projectlombok/lombok/unknown/lombok-unknown.jar
```

Looking at the issue again. It works after I change it back to the processor path from the project classpath.


---

remember: need mysql db running for the application run

## Postman test

after fixing some errors, we run the following test using Postman.

POST

```
http://localhost:8080/user/register
```

request body raw:
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "johndoe@exampleemail.com",
    "password": "123456"
}
```

**201 Created**
```json
{
    "timeStamp": "2025-01-09T02:45:30.831266092",
    "status": "CREATED",
    "statusCode": 201,
    "message": "User created",
    "data": {
        "user": {
            "id": 6,
            "firstName": "John",
            "lastName": "Doe",
            "email": "johndoe@exampleemail.com",
            "address": null,
            "phone": null,
            "title": null,
            "bio": null,
            "imageUrl": null,
            "enabled": false,
            "createdAt": null,
            "notLocked": true,
            "usingMfa": false
        }
    }
}
```

---

## Banner

replace default Spring application run banner with custom

`resources/banner.txt`

src: https://patorjk.com/software/taag/ 

calvin s
```
┌─┐┌─┐┬  ┌─┐┬┌─┐┬ ┬
│  ├─┤│  │  │├┤ └┬┘
└─┘┴ ┴┴─┘└─┘┴└   ┴ 
```

rubifont
```
 ▗▄▄▖ ▗▄▖ ▗▖    ▗▄▄▖▗▄▄▄▖▗▄▄▄▖▗▖  ▗▖
▐▌   ▐▌ ▐▌▐▌   ▐▌     █  ▐▌    ▝▚▞▘ 
▐▌   ▐▛▀▜▌▐▌   ▐▌     █  ▐▛▀▀▘  ▐▌  
▝▚▄▄▖▐▌ ▐▌▐▙▄▄▖▝▚▄▄▖▗▄█▄▖▐▌     ▐▌  
                                    
```

ansi shadow
```
 ██████╗ █████╗ ██╗      ██████╗██╗███████╗██╗   ██╗
██╔════╝██╔══██╗██║     ██╔════╝██║██╔════╝╚██╗ ██╔╝
██║     ███████║██║     ██║     ██║█████╗   ╚████╔╝ 
██║     ██╔══██║██║     ██║     ██║██╔══╝    ╚██╔╝  
╚██████╗██║  ██║███████╗╚██████╗██║██║        ██║   
 ╚═════╝╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝        ╚═╝   
                                                    


```

soft 

```
                                            
              ,--.      ,--. ,---.          
 ,---. ,--,--.|  | ,---.`--'/  .-',--. ,--. 
| .--'' ,-.  ||  || .--',--.|  `-, \  '  /  
\ `--.\ '-'  ||  |\ `--.|  ||  .-'  \   '   
 `---' `--`--'`--' `---'`--'`--'  .-'  /    
                                  `---'     
```

sub-zero
```
 ______     ______     __         ______     __     ______   __  __    
/\  ___\   /\  __ \   /\ \       /\  ___\   /\ \   /\  ___\ /\ \_\ \   
\ \ \____  \ \  __ \  \ \ \____  \ \ \____  \ \ \  \ \  __\ \ \____ \  
 \ \_____\  \ \_\ \_\  \ \_____\  \ \_____\  \ \_\  \ \_\    \/\_____\ 
  \/_____/   \/_/\/_/   \/_____/   \/_____/   \/_/   \/_/     \/_____/ 
                                                                       
```


---


## Issues with the end of Security Configuration module "Testing Login Method"

result:
```json
{
    "timeStamp": "2025-01-13T23:44:39.732175641",
    "status": "UNAUTHORIZED",
    "statusCode": 401,
    "reason": "You need to log in to access this resource"
}
```

however, in the console,
```shell
2025-01-13T23:44:39.708-05:00  INFO 51040 --- [nio-8080-exec-2] c.c.c.r.i.UserRepositoryImpl             : User found in the database: johndoe@exampleemail.com
2025-01-13T23:44:39.709-05:00  INFO 51040 --- [nio-8080-exec-2] c.c.c.r.i.RoleRepositoryImpl             : Fetching role for the user id: 6
```
Which means, the User and the Role were successfully found in the database.

This is supposed to pass.

After debugging and checking our code against the different code versions (based on Spring Boot 2.7.x and 3.2.2), it is determined that the issue was...

Inside the `UserPrincipal` class, set `isEnabled()` to return **true** so that the user can login and thus we can test the login functionality through Postman:
```java
// inside UserPrincipal 

/**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
//        return this.user.isEnabled();
        return true;
    }
```

***NOTE***: MAKE SURE TO USE THE ENDPOINT URL `http://localhost:8080/user/login` and *NOT* `http://localhost:8080/user/login/`. The latter will cause the login test to fail even though code is all fine. 

## Twilio

Set environment variables inside `application.properties`.
The `application.properties` file should be in the same location as `application.yml` (inside the `resources` directory).
```properties
# Twilio Credentials
twilio.sid="+12345678900"
twilio.auth-token="ABC123"
twilio.from-number="123abc"
```