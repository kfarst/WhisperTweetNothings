# Project 3 - *Whisper Tweet Nothings*

**Whisper Tweet Nothings** is an android app that allows a user to view home and mentions timelines, view user profiles with user timelines, as well as compose and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **28** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] The app includes **all required user stories** from Week 3 Twitter Client
* [x] User can **switch between Timeline and Mention views using tabs**
  * [x] User can view their home timeline tweets.
  * [x] User can view the recent mentions of their username.
* [x] User can navigate to **view their own profile**
  * [x] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [x] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [x] Profile view includes that user's timeline
* [x] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [x] User can view following / followers list through the profile
* [x] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [ ] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [x] User can **"reply" to any tweet on their home timeline**
  * x ] The user that wrote the original tweet is automatically "@" replied in compose
* [x] User can click on a tweet to be **taken to a "detail view"** of that tweet
 * [ ] User can take favorite (and unfavorite) or retweet actions on a tweet
* [x] Improve the user interface and theme the app to feel twitter branded
* [ ] User can **search for tweets matching a particular query** and see results
* [x] Usernames are styled and clickable within tweets [using clickable spans](http://guides.codepath.com/android/Working-with-the-TextView#creating-clickable-styled-spans)

The following **bonus** features are implemented:

* [x] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [x] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [x] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce view boilerplate.
* [ ] User can view their direct messages (or send new ones)

The following **additional** features are implemented:

* [x] Offline app hides tweeting and replying to tweets capabilities.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

**Login, Viewing, and Posting a Tweet**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/0O0Z0g3w3E331l103S46/V2LoginAndTweet.gif?v=5fe8c43d' title='Video Walkthrough' width='' alt='Video Walkthrough' />

**Offline Capabilities**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/3W1o3v2n3M3616021R2W/V2Offline.gif?v=9ed63c59' title='Video Walkthrough' width='' alt='Video Walkthrough' />

**Replying to a Tweet**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/290o2w2U0J3k0x2G2h20/V2ReplyToTweet.gif?v=cbc7ba97' title='Video Walkthrough' width='' alt='Video Walkthrough' />

**Clicking Links in Tweets**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/252c2w231b0e1N1n3W0Q/V2LinksInTweets.gif?v=1aa76e5b' title='Video Walkthrough' width='' alt='Video Walkthrough' />

**View Profiles and Connections**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/1O311A1I3G0j1S00442m/V2ViewProfilesAndConnections.gif?v=d7d2131e' title='Video Walkthrough' width='' alt='Video Walkthrough' />

**Infinite Pagination on All Timelines**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/14011X0K0T2L2P3n2t3t/V2InfinitePagination.gif?v=f15c7897' title='Video Walkthrough' width='' alt='Video Walkthrough' />

**View User Profiles by Clicking on Usernames in Tweets**

<img src='https://d17oy1vhnax1f7.cloudfront.net/items/0A2D0w1R2n253D1a2716/V2ClickOnUsernames.gif?v=6f7107fb' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

- It was difficult keeping the simulator's clock in line with the correct time, causing occasional "future" timestamps

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2016 Kevin Farst

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
