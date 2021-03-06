# Document statistics service

Upload Word, PDF or plain text document and get back statistics.
Can run in Kubernetes and interact via REST API.


* [Overview](#text-processor)
* [REST API](#rest-api)
* [Deployment](#deployment)
* [Examples](#examples)
* [Possible improvements](#what-can-be-improved)

## Overview

Document statistics service consist of 3 applications running as independent services.

![alt text][arch]

#### Text processor
Text processor is central part of this system. It's role is to receive documents
and assure that statistics are parsed and then provide those statistics back to client. It is using [spark](http://sparkjava.com/) micro framework as REST server. It can process Word, PDF and plain text documents. Statistics which can be extracted are:

* Paragraph count
* Paragraph maximum length
* Paragraph minimum length
* Paragraph average length
* Word frequency in descending order

Apache Tika is used as text extractor as it was suggested in assignment. Reasoning for using database is explained in **Database** section. From all REST server frameworks I looked into, Spark seems to be the one with the least effort needed to start working with it and least resources foot print and it still offers variety of functions.



#### Tika server
Text retrieved from Apache Tika is in plain text format. At first, using XHTML formatted text seemed as good option because of tagging used in parsed text but after short investigation I noticed that tagging was not consistent between document types so instead I used plain text format which is uniform across different document types. Communication with Tika is handled by TextExtractor.java. No 3rd party frameworks and libraries are used to accomplish communication with Tika since only PUT method is used to upload documents for extraction. For communication with database MongoDB driver for Java is used.

#### Database
If multiple services require statistics being extracted from one document, it
is easier to store this document in this service and assign UUID for this document by which it can be referenced at later point when one of the multiple services decide to retrieve some of this statistics. This way document does not have to be redistributed across services and uploaded over and over again.

I selected MongoDB for storage because NoSQL databases are good fit for storing unstructured data and it was easy to setup and work with.

###### Cache
In scenario where multiple services access document, excessive communication with database needs to be avoided. To solve this issue cache like system is introduced to mitigate database inserts and selects. First, when document is uploaded, it is stored in this cache in which it can reside for 5 seconds. When document is stored in cache for more than 5 seconds without being read/written to it is deleted from this cache and stored in database.

I implemented this cache just because I had this idea in mind and wanted to try it and see how it behaves. Also I wanted to have control over which and when documents are stored in database. In production it would be more than wise to use Redis or similar system.

## REST API

There are two ways how to interact with Text-processor. One is to upload document for statistics extraction and retrieve statistics using **HTTP PUT** method. If this method is used documented is uploaded, statistics are extracted and document is discarded. Another way is to use **HTTP POST, GET and DELETE** methods to **upload** document, **extract** statistics and **delete** document.
When document is uploaded, first it is stored in cache and then it is stored in
database from which can be retrieved later. **POST** and **PUT** methods has to
send a file and use header "Content-type: text/[type]" to tell text-processor how to treat this document. Every response is formatted in **JSON**.

### PUT

```
 PUT    /document/paragraph/count
 PUT    /document/paragraph/length/max
 PUT    /document/paragraph/length/min
 PUT    /document/paragraph/length/avg
 PUT    /document/paragraph/word/frequency
```


### POST
```
 POST   /document
 ```

 ### GET
 ```
 GET    /document/:id/paragraph/count
 GET    /document/:id/paragraph/length/max
 GET    /document/:id/paragraph/length/min
 GET    /document/:id/paragraph/length/avg
 GET    /document/:id/word/frequency
```
### DELETE
```
 DELETE /document/:id
 ```

### Content type

```
text/plain  -> for plain text
text/pdf    -> for PDF files
text/word   -> for Word documents
```

## Deployment
To deploy application using helm go to [containerize/](containerize/) folder and execute [start_up.sh](containerize/start_up.sh) script. To delete all resources execute [tear_down.sh](containerize/tear_down.sh). For Tika and Text-processor I created Dockefiles which can be found in respective folders of services in [containerize/](containerize/) folder. Both images are uploaded to DockerHub and publicly accessible. For MongoDB I used official docker image. 

Text-processor pod has ```NodePort``` which can be used for external communication. Tika server and MongoDB are using ```ClusterIP``` for cluster only communication. In production load balancer cloud be introduced for relying external traffic in to cluster. Or ```ClusterIP``` could be used if this service would be used only within the cluster. All deployments have DNS record within cluster. For Tika it is ```tika-service```, for database it is ```mongodb-service``` and for Text-processor it is ```text-processor-service```. Value after "-" can be overloaded through values.yaml file in respective service folder.

## Examples

##### Upload plain text document and extract all statistics
```curl -X PUT -T example_docs/example.plain http://[service]:[port]/document --header "Content-type: text/plain"```

##### Store plain text document
Response contains UUID for document. This UUID can be used to reference stored document.

```curl -X POST -T example_docs/example.plain http://[service]:[port]/document --header "Content-type: text/plain"```

##### Get all statistics from document which was stored by previous POST API call
```curl -X GET http://[service]:[port]/document/da78c12b-b2ef-472b-804f-97f591ce27ad```

##### Upload plain   text document and extract all statistics
```curl -X DELETE http://[service]:[port]/document/da78c12b-b2ef-472b-804f-97f591ce27ad```

For whole API list look in to **REST API** section. Accepted **Content-type** is one of:
```
text/plain  -> for plain text
text/pdf    -> for PDF files
text/word   -> for Word documents
```

## What can be improved

If usage of database would seem as beneficial for this type of service, proper integration with Kubernetes can be achieved. Instead of using Java MongoDB client, pod would request for storage using Persistent Volume Claim. Production tested cache system could be used and also error handling of text-processor could be improved.
Also, tests are kind of missing.

[arch]: ./diag.png "Architecture"
