# Web-API Serializers
The Web-API turns java objects into json data by using serializers. You can extend the Web-API
by writing your own serializers which help convert java objects into sensible json.

## Guidelines
> All serializers must extend `valandur.webapi.json.serializers.WebAPISerializer`
which itself extends `com.fasterxml.jackson.databind.ser.std.StdSerializer`

> Serializers must be in the `/webapi/serializers` folder (**NOT** the config folder).

> The package name must match the folder structure. If serializers are placed in the 
`webapi/serializers` folder then they must be in the `serializers` package.

> The filename must match the class name, otherwise the Web-API won't find it.

> The generic parameter for the class defines what object you want to serialize. 
Don't forget to add the required import statements.

## Simple Example
A very basic serializer looks like this:

```java
package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class ObjectSerializer extends WebAPISerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    }
}
```

In this case the generic parameter is `Object`. This probably not what you want, so change it 
to the class you want to serialize. Don't forget to change the type of the `value` parameter 
in the `serialize` function as well.

You can find more examples in the [json/serializers](https://github.com/Valandur/Web-API/tree/master/src/main/java/valandur/webapi/json/serializers)
folder of the repository.

## Serializing
Typically when serializing you will use the `JsonGenerator` to create the json of the object.
The `JsonGenerator` class offers quite a few methods to write json, including:
```java
gen.writeStartObject();
gen.writeStringField("id", "The object id");
gen.writeNumberField("amount", 3435);
gen.writeBooleanField("on", true);
gen.writeObjectField("sub", ...);  // This uses other serializers to process the object
gen.writeEndObject();
```
The code above would produce a json output such as:
```json
{
  "id": "The object id",
  "amount": 3435,
  "on": true,
  "sub": {
    ...
  }
}
```
Check with the fasterxml jackson repo for the full specs of the 
[JsonGenerator](https://fasterxml.github.io/jackson-core/javadoc/2.6/com/fasterxml/jackson/core/JsonGenerator.html)

