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

---

## Define custom behavior for child paths

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

---

## Use the JSON provider of your choice

The merge algorithm implemented by **JSON Merge** is provider-agnostic and depends on a specialized `JsonProvider` to perform the actual read/write operations on the source and target JSON documents.

The project supports the most popular JSON providers available in the community today:

![Supported JSON providers](resources/jsonmerge%20-%20Json%20Providers%20diagram%20-%201.0-A.svg)

---

## Downloading

If you are using Maven, add **JSON Merge** as a dependency to your pom.xml file:

```xml
<dependency>
  <groupId>net.obvj</groupId>
  <artifactId>jsonmerge</artifactId>
  <version>1.0.0</version>
</dependency>
```

If you use other dependency management systems (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/jsonmerge).

---

## Contributing

If you want to contribute to the **JSON Merge** project, check the [issues](http://obvj.net/jsonmerge/issues) page, or send an e-mail to [oswaldo@obvj.net](mailto:oswaldo@obvj.net).

**JSON Merge** uses [GitHub Actions](https://docs.github.com/actions) for CI/CD.
