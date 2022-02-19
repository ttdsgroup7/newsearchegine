# News Search Engine 
This project is achieved by Java mainly using SpringBoot.
There are mainly four modules:
* commonutil 
* newsearch 
* newsrecommand
* sparktfidf
## commonutil
It contains some common data type class used for other projects.

## newsearch
Deal with query String using tfidf value and return news result. Provide some
API to obtain related news data.

Command: `nohup java -jar newsearch-0.0.1-SNAPSHOT.jar  2>&1 &`

## newsrecommand
Achieve three recommend algorithm, Collaborative Filtering (CF),Content-based RecommendationandHotNews Recommendation 
respectively.

And use SpringBoot schedule to run each day at specified time.

Command: `nohup java -jar newsrecommand-0.0.1-SNAPSHOT.jar  2>&1 &`


## sparktfidf
Using Spark framework to achieve TF-IFD algorithm to speed up 
calculation.

Command: `nohup java -jar SparkTFIDF.jar  2>&1 &`