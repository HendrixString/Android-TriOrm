# Android-TriOrm
a 3d database ORM experiment for Android. (used in two commercial projects).
based around small tables concept and JVM Serialization.

## How to use
simply fork or download the project, you can also download and create `.aar` file yourself.

### Explanation
`TriOrm` is a very light, simple and efficient `ORM` with partial `SQL` wrapping, with the following theme:
* every Object (`TriData` extension) is serialized and saved in the database.
* you can only query by three fields: `id`, `time_created` and `type`.
* familiar no fuss Builder pattern to construct database.
* easy API to query, save and load typed objects.
* a very small usage of annotation(Optional).
* no learning curve whatsoever, no Boiler-plate code.
* Database is ready to use in less than couple of minutes.
* everything is typed.
* most of the presistent is carried by JVM serialization and minimal small SQL table.

### construct your Model/Tables
Simply extend `TriData` with the following annotations(Optional).
```
@TriTable(dbName = "myDataBase", tableName = "user")
public class User extends TriData {
    public String firstName = "John";
    public String lastName  = "Dow";

    public User() {
    }

    public User(String id) {
        super(id);
    }
}

@TriTable(dbName = "myDataBase", tableName = "location")
public class Location extends TriData {
    public String city    = "city";
    public String state   = "state";

    public Location() {
    }

 }

```

every `TriData` has the following indexed/query-able properties with getters/setters:
* `setId(..)` - you can set the id or it will be set automatically for you.
* `setType(..)` - some auxiliary field.
* `setTimeCreated(..)` - also set-up for you by default.

### construct your Database
constructing a database takes one line of code
```
new TriDatabase.Builder(this).addTable(User.class).addTable(Location.class).build();

```

and without annotations:
```
new TriDatabase.Builder(this).name("myDataBase").addTable("user", User.class).addTable("location", Location.class).build();

```

### Saving into your Database
Simply invoke the `save()` method on your extended `TriData` object.
```
User user       = new User();

user.setId("theDude");
user.setType("Java programmer");

user.firstName  = "Jimi";
user.lastName   = "Hendrix";

user.save();
```

### loading a single object from your Database
Simply use the `TriOrm.load(..)` Singleton and use your typed object.
```
User user = TriOrm.load(User.class, "theDude");
```

### querying from your Database
Simply use the `TriOrm.query(..)` builder Singleton and use your typed object.
```
ArrayList<User> list_users = TriOrm.query(User.class).timeCreatedFrom(0).timeCreatedTo(100).type("Java programmer").build().query();
```
you can query anything from the three properties: `id`, `timeCreated` and `type`.

### getting an instance of a table

Simply use the `TriOrm.table(..)` Singleton and use your typed object.
With table you can have more options and some sandbox methods.
```
TriTable<User> table = TriOrm.table(User.class);
```

### Important Notes
The presistent layer is built around `JVM Serialization`, therefore
there are important considerations for when evolving your model/tables, beware of the folowing:
* TriData is based on `JVM Serialization`, therefore:
  * adding new fields is compatible
  * deleting/renaming exiting fields is not compatible, Therefore you will have
    to implement your `writeObject/readObject`.
* look here for more info [Durable Java: Serialization](http://macchiato.com/columns/Durable4.html)
* I recommend using primitive types as much as possible when designing the model/table.


### Dependencies

### Terms
* completely free source code. [Apache License, Version 2.0.](http://www.apache.org/licenses/LICENSE-2.0)
* if you like it -> star or share it with others

### Contact Author
* [tomer.shalev@gmail.com](tomer.shalev@gmail.com)
* [Google+ TomershalevMan](https://plus.google.com/+TomershalevMan/about)
* [Facebook - HendrixString](https://www.facebook.com/HendrixString)

