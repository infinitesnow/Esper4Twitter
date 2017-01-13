# Esper4Twitter
This is an application which uses hosebird client and Esper to query a stream of tweets from Twitter. It supports multiple queries loading, multithreaded tweet parsing, configurable logging with a custom format, and different levels of debug.

JavaBeans folder contains JavaBeans to parse JSon format tweet into JavaBeans according to the Twitter documentation.

## Query configuration file syntax:
```
[ FORMAT "<printf-like format expression>" \n ] // Logging format 
select <expression> [as MYVALUE_<name> [, <expression> as MYVALUE_<name2> ...] ] // Query and arguments names
from ...
```
