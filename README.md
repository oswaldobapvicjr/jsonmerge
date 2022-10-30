# JSON Merge

[![License](https://img.shields.io/badge/license-apache%202.0-brightgreen.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/oswaldobapvicjr/jsonmerge/Java%20CI%20with%20Maven)](https://github.com/oswaldobapvicjr/jsonmerge/actions/workflows/maven.yml)
[![Coverage](https://img.shields.io/codecov/c/github/oswaldobapvicjr/jsonmerge)](https://codecov.io/gh/oswaldobapvicjr/jsonmerge)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.obvj/jsonmerge-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.obvj/jsonmerge-core)
[![Javadoc](https://javadoc.io/badge2/net.obvj/jsonmerge-core/javadoc.svg)](https://javadoc.io/doc/net.obvj/jsonmerge-core)
[![Vulnerabilities](https://snyk.io/test/github/oswaldobapvicjr/jsonmerge/badge.svg?targetFile=jsonmerge-core/pom.xml)](https://snyk.io/test/github/oswaldobapvicjr/jsonmerge?targetFile=jsonmerge-core/pom.xml)


A utility for merging JSON objects that supports **fine options** and **multiple JSON providers**.

> ℹ️ [Find examples in the wiki.](https://github.com/oswaldobapvicjr/jsonmerge/wiki/Examples/)

---
 
## How it works

Simply pass two previously loaded JSON documents to be merged. The resulting JSON shall contain all exclusive objects from source documents. And in case of key collision (i.e., the same key appears in both documents), the following rules will be applied:

- For **simple values**, such as strings, numbers and boolean values, the value from the highest-precedence JSON document will be selected
- If the value is a **JSON object** in both documents, the two objects will be merged recursively
- If the value is a **JSON array** in both documents, then all distinct elements (i.e., not repeated ones) will be copied to the resulting array, unless a  custom `JsonMergeOption` is provided specifically for that array path
- If the types are **incompatible** in the source JSON documents (e.g.: array in one side and simple value or complex object in the other), then a copy of the object from the highest-precedence document will be selected as fallback

> ℹ️ **Note:** The first JSON document is always considered to have **higher precedence** than the second one.



## Define custom behavior for children elements

The operation accepts an arbitrary number of options for parts of the document defined by [JSONPath Expressions](https://goessner.net/articles/JsonPath/index.html#e2).

```java
JsonMergeOption.onPath("$.params")
          .findObjectsIdentifiedBy("key")
          .thenDoADeepMerge();
```

> :bulb: Call `JsonMergeOption.onPath(String)` then let the API guide you through the additional builder methods.

This is particularly useful to define distinct elements during the merge of an array of objects, such as the following:

```json
{
  "params": [
    {
      "key": "language",
      "value": "pt-BR"
    },
    {
      "key": "country",
      "value": "Brazil"
    }
  ]
}
```

> ℹ️ [Find more examples in the wiki.](https://github.com/oswaldobapvicjr/jsonmerge/wiki/Examples/)


## Use your favorite JSON Provider

The algorithm implemented by **JSON Merge** is provider-agnostic. The actual read/write operations on the JSON objects are delegated to a specialized `JsonProvider` which must be specified during `JsonMerger` creation. This design was chosen to eliminate the need for intermediary JSON object type conversions for the different providers.

The Project supports the most popular JSON providers available in the community today.

![Supported JSON providers](resources/jsonmerge%20-%20Json%20Providers%20diagram%20-%201.2-A.svg)

> :warning: **IMPORTANT:** JSON Merge does **NOT** supply the dependencies tagged as **"optional"** to avoid the burden of unintended transitive dependencies in your application. These dependencies must be resolved by your application if required.

### How to use

#### Using json-smart as JSON Provider

> The choice for those looking for simplicity and good performance

```java
import net.minidev.json.JSONObject;
...
JsonMerger<JSONObject> merger = new JsonMerger<>(JSONObject.class);
                       /* or... new JsonMerger<>(new JsonSmartJsonProvider()); */
````

#### Using Gson as JSON Provider

> Google implementation with enhanced conversion capabilities

```java
import com.google.gson.JsonObject;
...
JsonMerger<JsonObject> merger = new JsonMerger<>(JsonObject.class);
                       /* or... new JsonMerger<>(new GsonJsonProvider()); */
````

#### Using Jackson as JSON Provider

> The #1 JSON library in Maven Central

```java
import com.fasterxml.jackson.databind.JsonNode;
...
JsonMerger<JsonNode> merger = new JsonMerger<>(JsonNode.class);
                     /* or... new JsonMerger<>(new JacksonJsonNodeJsonProvider()); */

````

#### Using json.org as JSON Provider

> The reference implementation for Java

```java
import org.json.JSONObject;
...
JsonMerger<JSONObject> merger = new JsonMerger<>(JSONObject.class);
                       /* or... new JsonMerger<>(new JsonOrgJsonProvider()); */
````

#### Using Vert.x as JSON Provider

> Vert.x is a reactive programming toolkit used by 1K+ projects which uses a dedicated JSON implementation

```java
import io.vertx.core.json.JsonObject;
...
JsonMerger<JsonObject> merger = new JsonMerger<>(JsonObject.class);
                       /* or... new JsonMerger<>(new VertxJsonProvider()); */
````

## JSON Merge CLI

This is a command-line tool for merging JSON files directly from the terminal.

```help
$ java -jar jsonmerge-cli-1.2.0.jar --help

Usage: jsonmerge-cli-1.2.0.jar [-hp] [-t <target>] [-d <exp=key>]... <FILE1> <FILE2>

Parameters:

      <FILE1>                The first file to merge
      <FILE2>                The second file to merge

Options:

  -d, --distinct <exp=key>   Defines one or more distinct keys inside a child path
                             For example: -d $.agents=name
                                          -d $.files=id,version
  -h, --help                 Displays a help message
  -p, --pretty               Generates a well-formatted result file
  -t, --target <target>      The target file name (default: result.json)
```

## Downloading

### JSON Merge Core

If you are using Maven, add `jsonmerge-core` as a dependency in your `pom.xml` file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>jsonmerge-core</artifactId>
    <version>1.2.0</version>
</dependency>
```

> If you use other dependency management systems (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/jsonmerge-core).

### JSON Merge CLI

To use **JSON Merge CLI**, [download the latest version here](https://repo1.maven.org/maven2/net/obvj/jsonmerge-cli/1.2.0/jsonmerge-cli-1.2.0.jar) (JRE 8+ required).

## Contributing

If you want to contribute to the **JSON Merge** project, check the [issues](http://obvj.net/jsonmerge/issues) page, or send an e-mail to [oswaldo@obvj.net](mailto:oswaldo@obvj.net).

**JSON Merge** uses [GitHub Actions](https://docs.github.com/actions) for CI/CD.
