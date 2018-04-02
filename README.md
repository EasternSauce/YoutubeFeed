# YoutubeFeed 2.0
Now written using JavaFX for cleaner UI and easier to manage code!
New features:
- now uses JavaFX
- more responsive GUI
- thumbnails load asynchronously (video list loads way faster, thumbnails show after a while)
- temporarily removed caching videos (was causing many problems)
- code refactored, codebase reduced by 50%

YoutubeFeed is a desktop application that displays a feed of recently uploaded videos from Youtube channels you choose, similarly to how Youtube subscriptions work, except:
- you don't need a Google account (well, you still need a valid Youtube api access, but I'll figure it out eventually),
- its not affected by the frequent Youtube homepage changes,
- you don't need to contribute a Youtube subscription to a channel to keep track of what the channel uploads.

## Getting Started

Any dependencies are covered by the Maven pom.xml file. Just import everything and compile the sources. On the first run you may need to login to the Youtube api connected to the key located in the resources folder. You can get your own from Youtube API site and replace it there.

## Prerequisites

- Java 8
- Youtube Data API v3
- Google Analytics API

## Usage

The application starts with an empty feed and "Refresh" and "Channels" buttons on top. The feed is automatically updated on application start and when the channel list changes. "Refresh" is used to update the feed manually. "Channels" open a window that lets you add or remove channels to display videos from. You can search for new channels to add.


Feed displays video entries with additional information: title, thumbnail, uploader's name, published date and time, and duration. The entries can be clicked to open the default internet browser with the video link.

## Directories

Youtube API v3 requires a Google Developer account and enforces a certain quota for data transfers. The file is placed in ".oauth_credentials" directory.


The list of channel ids is saved in the "userchannels" file in the main directory.
