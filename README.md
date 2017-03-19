# YoutubeFeed

YoutubeFeed is a desktop application that displays a feed of recently uploaded videos from Youtube channels you choose, similarly to how Youtube subscriptions work, except:
- you don't need a Google account,
- its not affected by the frequent Youtube homepage changes,
- you don't need to subscribe to see the channel's videos on the feed.

## Getting Started

Any dependencies are covered by the Maven pom.xml file. Just import everything and compile the sources.

## Prerequisites

- Java 7 or higher
- Swing GUI Toolkit
- Youtube Data API v3
- Google Analytics API

## Usage

The application starts with an empty feed and "Update" and "Channels" buttons on top. The feed is automatically updated on application start and when the channel list changes. "Update" is used to update the feed manually. "Channels" open a window that lets you add or remove channels to display videos from. You can search for new channels to add.


Feed displays video entries with additional information: title, thumbnail, uploader's name, published date and time, and duration. The entries can be clicked to open the default internet browser with the video link.

## Directories

Youtube API v3 requires a Google Developer account and enforces a certain quota for data transfers. I included my credentials file so potential users don't need to make an account. The file is placed in ".oauth_credentials" directory.


The application creates a cache of serialized video entries so it doesn't need to update the feed completely every time. It is located in "cache" directory.


The list of channels is saved in the "channel_ids.txt" file in the main directory.

## Problems

In case of the application not updating properly, deleting the cache directory completely usually fixes it right up. The application use to not support 4k resolutions (Swing doesn't support Windows scalability features). This was circumvented by increasing the font size and window bounds, which now can make it unusable on smaller displays.
