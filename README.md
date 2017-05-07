# legacy-2005-Java-Webserver-IRCD
Made in 2005/2006. Only for maintains the good memories.

This older project is an IRCD in WebServer format. Was made in Java but the templates support PHP. Today is not useful, but in 2005 was a very funny project to study Java.

The program is a mini-webserver (like Apache, IIS) with partial PHP support. It is also an IRCD server (software that receives connections from IRC clients) with services (ChanServ, NickServ, OperServ) and it is also a WebChat. All integrated.

When you start the program it opens some ServerSockets by listening for ports 6667 and 80 basically. If a browser, such as Firefox or Internet Explorer, connects using port 80, the webserver of the program goes into action and handles the connection to the HTTP protocol by making WebChat made in PHP / Ajax for that client. But if the connection is made by an IRC client (port 6667) then it handles the connection using the IRC protocol.

It's very complex and have some problems too :-) The idea was to keep an IRCD server with registration services and nicknames in the same software, as well as a WebIRC. Too much!

I am proud of this software. It was a huge source of learning in 2005. Obviouly it's not more useful now.
