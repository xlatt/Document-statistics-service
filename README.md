# Text processor

Upload Word, PDF or plain text document and get back statistics.
Can run in Kubernetes and interact via REST API.

## Overview

Text processor consist of 3 applications running as independent services.

![alt text][arch]

#### Text processor
Text processor is central part of this system. It's role is to receive documents
and assure that statistics are parsed and then retrieve those statistics back to client. It is using [spark](http://sparkjava.com/) micro framework as REST server. It can process Word, PDF and plain text documents. Statistics which can be retrieved are:

* Paragraph count
* Paragraph maximum length
* Paragraph minimum length
* Paragraph average length
* Word frequency in descending order

Apache Tika is used as text extractor as it was suggested in assignment. Reasoning for using database is explained in **Database** section.

#### Tika server
Text retrieved from Apache Tika is in plain text format. At first, using XHTML formatted text seemed as good option because of tagging used in parsed text but after short investigation I noticed that tagging was not consistent between document types so instead I used plain text format which is uniform across different document types.

#### Database
If multiple services require statistics being extracted from one document, it
is easier to store this document in this service and assign UUID for this document by which it can be referenced at later point when one of the multiple services decide to retrieve some of this statistics. This way document does not have to be redistributed across services and uploaded over and over again.

##### Cache
In scenario where multiple services access document, excessive communication with database needs to be avoided. To solve this issue cache like system is introduced to mitigate database inserts and selects. First, when document is uploaded, it is stored in this cache in which can reside for 5 seconds. When document is stored in cache for more than 5 seconds without being read/written to it is deleted from this cache and stored in database.

## REST API

There are two ways how to interact with Text-processor. One is to upload document for statistics extraction and retrieve statistics using **HTTP PUT** method. If this method is used documented is uploaded, statistics are extracted and document is discarded. Another way is to use **HTTP POST, GET and DELETE** methods to **upload** document, **extract** statistics and **delete** document.
When document is uploaded, first it is stored in cache and then it is stored in
database from which can be retrieved later.

### PUT

```
 PUT  /document/paragraph/count
 PUT  /document/paragraph/length/max
 PUT  /document/paragraph/length/min
 PUT  /document/paragraph/length/avg
 PUT  /document/paragraph/word/frequency
```


### POST
```
 POST   /document
 GET    /document/:id/paragraph/count
 GET    /document/:id/paragraph/length/max
 GET    /document/:id/paragraph/length/min
 GET    /document/:id/paragraph/length/avg
 GET    /document/:id/word/frequency
 DELETE /document/:id
 ```

 ## Examples
[arch]: ./diag.png "Architecture"
