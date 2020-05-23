# Pikachu skate revived

## Intro

In the early 2000's, I used to frequent pokémon fan sites, and on one I found an online skating game featuring pikachu. Years later, I set out to find the game only to find dead links all over the internet. Finally, thanks to The Wayback Machine, I managed to find the original .jar binary for the game. Instead of trying to run the java applet (deprecated since 2017), I decided to decompile the .jar file to get the original code, and to rewrite the game myself.

The images referred to in the original file were gone so I had to recreate them somehow. Luckily there is a [video on youtube](https://www.youtube.com/watch?v=r_xrXzY-Yhg) of the original gameplay, so I copied all the images from the video (that's why the quality is so marvellous), and used them to make my version.

All credits obviously go to the person who originally made the game. This is just a hobbyist fan project of the authentic game.


## Requirements

If you want just want to play the game, you can do so on [heroku](#)

If you want to clone the project, you need the following software installed:
1. Java 11.0.7
2. Maven 3.6.0

All development and testing has been done on Ubuntu 18.04.

At the moment compilation works with mvn:

    $ cd pikaskate
    $ mvn compile                                       #for simple (non-packaged) compilation
    $ java -cp target/classes/ org.pokemon.app.MainApp  #to run app
    
