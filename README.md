![Logo](./images/logo.png)

# 🎵 ATOM Music app

This is a music application created
using Kotlin and Jetpack Compose for ATOM OS IVI

### Libraries Used
* Media3
* Jetpack Compose
* Retrofit
* Koin
* Coil
* Room
* Jaudio-Tagger

# Features

1. Flexible queueing functionality
2. Tag Editor (mp3, wav, m4a)
3. Create playlists
4. Sleep Timer
5. Create home screen shortcuts for your favorite playlists
6. Synchronized lyrics
7. Automatically fetches synchronized lyrics for songs without lyrics
8. Dynamic Color Scheme, and theming options
9. Blacklist Folders
10. Create a playlist from the current queue 
11. Circular and rectangular home screen widgets (currently in alpha)

# Project modules
```
Music project
├── app
│   ├── src
│   │   ├── androidTest
│   │   ├── main
│   │   └── test
├── build-logic
├── core
│   ├── database
│   ├── model
│   ├── network
│   ├── playback
│   ├── store
│   ├── testing
│   └── ui
├── feature
│   ├── albums
│   ├── nowplaying
│   ├── playlists
│   ├── settings
│   ├── songs
│   ├── tageditor
│   └── widgets
```

# Dependencies

```mermaid
%%{
init: {
'theme': 'neutral'
}
}%%

graph LR
  subgraph gradle 
    build-logic  
  end  
  subgraph application
    app  
  end  
  subgraph core
    database
    model
    network
    playback
    store
    ui
  end
  subgraph feature
    albums
    nowplayings
    playlists
    settings
    songs
    tageditor
    widgets
  end
  app --> albums
  app --> nowplayings
  app --> playlists
  app --> settings
  app --> songs
  app --> tageditor
  app --> widgets
  albums --> ui
  albums --> store
  albums --> model
  albums --> playback
  nowplayings --> ui
  nowplayings --> store
  nowplayings --> model
  nowplayings --> playback
  nowplayings --> network
  playlists --> ui
  playlists --> store
  playlists --> model
  playlists --> playback
  playlists --> database
  settings --> ui
  settings --> store
  settings --> model
  songs --> ui
  songs --> store
  songs --> model
  songs --> playback
  widgets --> ui
  widgets --> store
  widgets --> model
  widgets --> playback
  widgets --> database
  
```
