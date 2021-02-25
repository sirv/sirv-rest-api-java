# Sirv REST API SDK for Java

The Sirv REST API let's you perform more than 40 actions on your Sirv account, such as upload, search, delete, move, copy, fetch remote URL etc.

REST API Documentation: https://apidocs.sirv.com/#getting-started

# Get started

To use this Java SDK, specify it as a project dependency in Maven:
 
```
<dependencies>
  <dependency>
    <groupId>sirv.com</groupId>
    <artifactId>sirv-java-sdk-spring</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

# Spring MVC usage example

This SDK can be used in Spring MVC projects.

Example of SDK usage:

```
import com.sirv.RestClient;
import com.sirv.SirvClient;
import com.sirv.SirvClientImpl;
import com.sirv.account.AccountClient;
import com.sirv.account.model.AccountInformation;
import com.sirv.spring.RestTemplateAdapter;

public class Test {
    public static void main(String[] args) {
        // setup credentials
        String clientId = "yourClientId";
        String clientSecret = "yourClientSecret";
 
        //setup sirv client
        RestClient restClient = new RestTemplateAdapter();
        SirvClient sirvClient = new SirvClientImpl(clientId, clientSecret, restClient);
 
        // get accountClient
        AccountClient accountClient = sirvClient.getAccountClient();
        // get account information
        AccountInformation information = accountClient.getInformation();
        System.out.println(information);
    }
}
```

Replace `yourClientSecret` and `yourClientSecret` with the client ID and secret from your Sirv account: https://my.sirv.com/#/account/settings/api

# Token

If the bearer authentication token has expired (after 20 minutes), the system will automatically request a new one.

`sirvClient.getToken()` - get new token
`sirvClient.isAlive()` - check if token is alive

# Errors

If the API returns an error, the SDK returns an exception with details about the error `SirvException class`.

# Folder structure

In this projects' folder structure, `sirvjavasdkcorecontains` contains the abstract SDK code, without integration with a specific REST client.

`account` – Sirv account API
`exception` – SDK exception
`files` - Sirv files API
`json` – JSON config
`model` – authorization of API models
`statistic` - Sirv statistic API
`user` - Sirv user API
`RestClient.java` – interface of REST client
`SirvClient.java` – interface of Sirv client
`SirvClientImpl.java` – implementation of Sirv client

The folder `sirvjavasdkspring` contains the implementation of `RestClient.java`, based upon the Spring _RestTemplate_.
