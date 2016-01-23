# GatechWebCrawler
A simple Android platform Crawler that use BFS to search URLs.
# GatechWebCrawler
A simple Android platform Crawler that use BFS to search URLs.

# 1. Design
## 1.1 SQLite Database
```Java
public static final String TABLE_NAME = "Record";
public static final String COLUMN_ID = "_id";
public static final String COLUMN_URL = "url";

// Database creation sql statement
private static final String DATABASE_CREATE = "create table "
      + TABLE_NAME + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_URL
      + " text not null);";
```
The database only has one table with only 2 columns: '_id' which is automatically increment and URL string.

```Java
//clear table at the beginning of crawling
public void clear();
//check whether the url is duplicate
public boolean checkExists(String url);
//insert url into table
public long insertURL(String url);
```
The database provides three operations: clear, checkExists and insertURL.

## 1.2 Crawler
```Java
//a queue used to do BFS
LinkedList<String> queue = new LinkedList<>();
//clear the database and begin new crawling
db.clear();
db.insertURL(seedUrl);
queue.add(seedUrl);
int count = 0;
//a flag used to stop the crawler
stop = false;
//do BFS search
while(!queue.isEmpty() && count <= limit && !stop) {
    String url = queue.poll();
    //get useful information
    Document doc = null;
    try {
        doc = Jsoup.connect(url).get();
    } catch (Exception e) {
        e.printStackTrace();
        continue;
    }
    //get all links and do BFS crawling
    Elements questions = doc.select("a[href]");
        for(Element link: questions){
        String newUrl = null;
        //check if the given URL contains href and is already in database
        if(count < limit && link.attr("href").contains(filter)
                && !db.checkExists(newUrl = link.attr("abs:href"))) {
            //store the URL into database to avoid parsing again
            db.insertURL(newUrl);
            queue.add(newUrl);
            count++;
        }
    }
}
```
I use BFS to crawl URLs. First, I use JSoup methods to get the whole source code of the seed URL. Then I visit all the HTTP links and put them into queue. In each the while loop, I poll out one URL string. The process ends when count reaches limit or there's not URLs in the queue.
