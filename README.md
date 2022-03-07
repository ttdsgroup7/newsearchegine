# News Search Engine 
This project is achieved by Java mainly using SpringBoot.
There are four modules:
* commonutil 
* newsearch 
* newsrecommand
* sparktfidf

Environment: JDK11

## commonutil
It contains some common data type class used for other projects.

## newsearch
Deal with query String using tfidf algorithm and return news result. Provide some
API to obtain related news data.

Command: `nohup java -jar newsearch-1.0.jar  2>&1 &`

## newsrecommand
Achieve three recommend algorithms, Collaborative Filtering (CF),Content-based RecommendationandHotNews Recommendation 
respectively.

And use SpringBoot schedule to run at specified time each day.

Command: `nohup java -jar newsrecommand-1.0.jar  2>&1 &`


## sparktfidf
Using Spark framework to achieve TF-IFD algorithm to speed up 
calculation.

Using Quartz tool to schedule task run at specified time each day

Command: `nohup java -jar SparkTFIDF.jar  2>&1 &`


## Database
Redis: gc.caohongchuan.top:6379 (newsearch, newsrecommand, sparktfidf)

MongoDB: gc2.caohongchuan.top:27017/ttds_group7 (newsearch)

MySQL: gc3.caohongchuan.top:3306/TTDS_group7 (newsearch, newsrecommand, sparktfidf)