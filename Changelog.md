### Versioning ###
The versioning scheme is as follows: the first part represents the API version used. The second part represents the actual application's version. So "1.0-3" means the application uses the 1.0 API and it's at the third release. Minor releases are given a letter, such as "1.0-3<b>b</b>".

# 1.0-11 #
  * Display reputation in main screen (when signed in)
  * Better notifications (based on previous rep, not on time)

# 1.0-10a #
  * Compatibility with Android 1.6 (again; `android.util.Pair` is API Level 5+)

# 1.0-10 #
  * Basic chat support. Loads the mobile version of the web interface. Login doesn't work on any sites other than the trilogy ones (because apparently Android's browser doesn't support localStorage)

# 1.0-9a #
  * Compatibility with Android 1.6 (`android.app.Activity.onBackPressed()` is API Level 5+)

# 1.0-9 #
  * UI improvements: internalized and improved drawables
  * Usability: Don't load all sites anymore, instead provide a sites picker
  * Bugfix: Make notifications actually work (sorry about that)
  * Other code improvements

# 1.0-8b #
  * Small UI improvements: headers for question & answer lists
  * Bugfixes & improvements

# 1.0-8 #
  * New icon again (people complained it was not representative of the app)
  * Sites list now lists all available sites, with the possibility to "bookmark" some of them
  * Share question feature
  * Some improvements & bugfixes

# 1.0-7 #
  * New users activity, similar to stackoverflow.com/users
  * Picking a user is much simpler, you don't have to know your user ID anymore
  * More intents available for developers. Devs can use Droidstack to have the user pick a certain question, answer, tag or user and use it in their app.

# 1.0-6 #
  * New icon courtesy of androidicons.com
  * Clickable tags everywhere
  * Better search UI, autocomplete for tags
  * New menu option in question lists that allows you to filter questions by tags
  * New tags activity, similar to stackoverflow.com/tags
  * New button when viewing a question that allows you to open it in the browser

# 1.0-5 #
  * View user profiles & rep changes
  * Improved UI: much faster screen orientation changes, some polishing here & there
  * View all of a user's rep changes

# 1.0-4b #
  * New preferences page
  * Customizable page size (number of items to load at a time) and font size (for question viewing)
  * Rep change notifications
  * Bugfixes

# 1.0-3b #
  * Couple bugfixes
  * Improved question display (author rep is properly formatted and you get to see when a question / answer was posted)

# 1.0-3 #
  * Bugfixes, performance improvements
  * Question viewing in landscape mode greatly improved

# 1.0-2 #
  * Searching
  * Responds to `droidstack://` intents (see [Intents](Intents.md))
  * Some bugfixes & improvements

# 1.0-1 #
  * First version released on the Android Market
  * All, Unanswered, User, Favorite questions, user answers, full question viewing.