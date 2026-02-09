# API Stress Lab

API Stress Lab is a lightweight Java library for API load and stress testing using JUnit 5 annotations.

```java
@Test
@LoadTest(
    url = "https://api.example.com/users",
    method = LoadHttpMethod.GET,
    requests = 10
)
void simpleTest(LoadTestResultDto result) {
    result.printSummary();
}
```

Required on your test class:

```java
@EnableLoadTesting
class ApiTests {}
```

More details and usage examples:  
👉 https://
