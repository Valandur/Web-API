# Web-API Serializers
The Web-API turns java objects into json data by using serializers. You can extend the Web-API
by writing your own serializers which help convert java objects into sensible json.


## Table of Contents
1. [Guidelines](#guidelines)
1. [Basics](#basics)
1. [Serializing](#serializing)
    1. [Example](#example)


<a name="guidelines"></a>
## Guidelines
> All serializers must extend `valandur.webapi.json.serializer.WebAPIBaseSerializer`
which itself extends `com.fasterxml.jackson.databind.ser.std.StdSerializer`

> Serializers must be in the `/webapi/serializers` folder (**NOT** the config folder).

> The package name must match the folder structure. If serializers are placed in the 
`webapi/serializers` folder then they must be in the `serializers` package.

> The filename must match the class name, otherwise the Web-API won't find it.

> The generic parameter for the class defines what object you want to serialize. 
Don't forget to add the required import statements.


<a name="basics"></a>
## Basics
A very basic serializer looks like this:

```java
package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ObjectSerializer extends WebAPIBaseSerializer<Object> {
    @Override
    public void serialize(Object value) throws IOException {
    }
}
```

In this case the generic parameter is `Object`. This probably not what you want, so change it 
to the class you want to serialize. Don't forget to change the type of the `value` parameter 
in the `serialize` function as well.

You can find more examples in the [json/serializer](https://github.com/Valandur/Web-API/tree/master/src/main/java/valandur/webapi/json/serializer)
folder of the repository.


<a name="serializing"></a>
## Serializing
Typically when serializing you will have properties for which there already are existing serializers.
For example `String` and all primitive classes are automatically serialized. Most Minecraft 
classes like `BlockState` and `Vector3d` also already have serializers.

> Use the `writeField` and `writeValue` methods when serializing, these ensure that all permissions
are checked correctly. You can call those methods as follows:

```java
writeField(String fieldName, Object value)
writeValue(Object value)

// Or use these if you want to explicitly include or exclude details

writeField(String fieldName, Object value, TriState details)
writeValue(Object value, Tristate details)
```


<a name="example"></a>
### Example
```java
writeStartObject();
writeField("id", "some-random-id");
writeField("amount", 2433);
writeField("on", false);
writeField("block", ..., Tristate.TRUE);
writeEndObject();
```
The code above would produce a json output such as:
```json
{
  "id": "some-random-id",
  "amount": 2433,
  "on": false,
  "block": {
    ...
  }
}
```
