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

# 2. User Interface
<img src="https://github.com/Peleus1992/GatechWebCrawler/blob/master/app/src/main/res/drawable/Screenshot_2016-01-23-14-12-09.png" width="200" height="400" />
<img src="https://github.com/Peleus1992/GatechWebCrawler/blob/master/app/src/main/res/drawable/Screenshot_2016-01-23-14-16-36.png" width="200" height="400" />
<img src="https://github.com/Peleus1992/GatechWebCrawler/blob/master/app/src/main/res/drawable/Screenshot_2016-01-23-14-17-37.png" width="200" height="400" />

# 3. Prons and Cons
Prons: This crawler is simple to design. It uses BFS to search URLs while using SQLite database to prevent duplicate URLs. In the BFS, the crawler use JSoup which is Java library for working with real-world HTML. The JSoup can get the document of page and select elements from the document by applying tags or attributes such as "a[href]".

Cons: In this crawler, we can add filter to selectively access web pages. But the filter only applys to the URL itself, not the entire text of web page. In the future, I will do more research on it and I will further the project.

# 4. Statistics

Seed URL | Filter | Total URLs number | Time used (ms) | Speed (pages/min)
---------|--------|-------------------|----------------|------------------
http://www.gatech.edu | gatech.edu | 1000 | 54410 | 1102
http://www.cc.gatech.edu | gatech.edu | 1000 | 68067ms | 802
http://www.cc.gatech.edu | cc.gatech.edu | 1000 | 375854ms | 159

We can see that if we use more specific filter text, the speed would drop down extremely.

# 4. Experience
Through this Web Crawler design, I have learnt valuable lessons. The Web Crawler is quite a useful searching tool. I understand the basic structure of Web Crawler. However, the project is quite different from real-world Web Crawler used by many companies. So I have to continue furthering my project as a future word. I plan to use full-text indexing in order to provide better search function. I also plan to use Hadoop as the future platform for the Web Crawler. Since in Hadoop cluster, not so powerful single PC server gathers and words together to provide great computing power and storage capability.
