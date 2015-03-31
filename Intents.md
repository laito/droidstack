# Introduction #

Droidstack handles a few intents so that you can integrate it in your own app. Check out [this great article](http://d.android.com/resources/articles/can-i-use-this-intent.html) to learn how you can test if Droidstack is installed. Basically, you would do something among the lines of:

```
// somewhere in a method in your activity
Intent droidstack = new Intent(Intent.ACTION_VIEW, Uri.parse("droidstack://questions/all?endpoint=" + Uri.encode("http://api.stackoverflow.com/")));
List<ResolveInfo> list = getPackageManager().queryIntentActivities(droidstack,
    PackageManager.MATCH_DEFAULT_ONLY);
if (list.size() == 0) {
    // Droidstack is not available
    // Open up the Market application so the user can download it
    Toast.makeText(this, "Please install Droidstack in order to use our app", Toast.LENGTH_SHORT).show();
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=org.droidstack"));
    startActivity(i);
}
else {
    // Droidstack is installed. Use it.
}
```


# The Intents #

## View all questions ##

```
String uri = "droidstack://questions/all" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/");
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View unanswered questions ##

```
String uri = "droidstack://questions/unanswered" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/");
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View a user's profile ##
```
String uri = "droidstack://user" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/") +
    "&uid=180784";
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View a user's reputation changes ##
```
String uri = "droidstack://reputation" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/") +
    "&uid=180784";
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View a user's questions ##

```
String uri = "droidstack://questions/user" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/") +
    "&uid=180784";
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View a user's favorite questions ##

```
String uri = "droidstack://questions/favorites" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/") +
    "&uid=180784";
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View a user's answers ##

```
String uri = "droidstack://answers/user" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/") +
    "&uid=180784";
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## Search ##

```
String uri = "droidstack://questions/search" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/") +
    "&tagged=android"; // you can also use "intitle" and "nottagged"
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View tags ##
```
String uri = "droidstack://tags" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/");
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View users ##
```
String uri = "droidstack://users" +
    "?endpoint=" + Uri.encode("http://api.stackoverflow.com/");
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

## View a specific question ##

```
String uri = "droidstack://question" +
    "?endpoint=" + Uri.encode("http://api.stackapps.com/") +
    "&qid=585";
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
startActivity(i);
```

If you only have an answer id, you can use the `droidstack://question` intent with an `aid` parameter (instead of `qid`) and supply your answer id there.

# Getting results #
If you want the user to pick a question, answer, tag or user and have it returned to your application, you can use any of the `/questions/*`, `/answers/*`, `/tags` and `/users` intents with an `Intent.ACTION_PICK` action. This way, when an item is clicked, the user is taken back to your application and your `onActivityResult` is called with a data intent containing the following extras:

**For question lists:**
  * **id**: int, the id of the selected question
  * **title**: String, the title of the selected question
  * **tags**: String[.md](.md), the tags associated to the selected question
  * **score**: int, the overall score of the selected question
  * **answers**: int, the number of answers the selected question has
  * **views**: int, the number of views the selected question got
  * **accepted**: int, the ID of the accepted answer, if there is one. -1 otherwise

**For answer lists:**
  * **id**: int, the id of the selected answer
  * **qid**: int, the id of the parent question
  * **title**: String, the title of the selected answer
  * **score**: int, the selected answer's overall score
  * **accepted**: boolean, whether the selected answer is the accepted one or not

**For tags:**
  * **name**: String, the tag itself (for example "android")
  * **count**: int, the number of posts tagged with the selected tag

**For users:**
  * **uid**: int, the ID of the user
  * **name**: String, the user's display name
  * **rep**: int, the user's reputation
  * **emailHash**: String, the user's email hash, useful for gravatar avatars

# Now go build cool stuff! #