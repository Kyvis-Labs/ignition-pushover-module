# Pushover Notification Module

An Ignition module that adds support for sending alarm notifications through Pushover. Pushover makes it easy to get real-time notifications on your Android, iPhone, iPad, and Desktop (Android Wear and Apple Watch, too!)

See [Pushover](https://pushover.net/) for more details on the service.

Open Source
---------------

The Pushover module is an open source project distributed under the Apache 2.0 license. Please feel free to download the source code and contribute. 

Getting Started
---------------

1. Download the latest version module (.modl) from [releases](https://github.com/Kyvis-Labs/ignition-pushover-module/releases)
2. Install the module into Ignition 8.1+
3. Add a new alarm notification profile in the Gateway configuration section (under Alarming > Notification)
4. Select Pushover Notification
5. Enter your Pushover API token and user key (see below for more details)
6. Add your Pushover device to the user's contact info
7. Ensure your user is in the on-call roster
8. Use the Pushover profile in your alarm notification pipeline
9. Enjoy!

Obtaining API Token and User Key
---------------

To get started pushing notifications from Ignition, you'll first need to [register](https://pushover.net/apps/build) (a free process) to get an API token. When registering an app, you'll be able to set its name which will be used as a default title for messages, as well as upload an icon that will appear with each message on device clients.

Example Application API Token: ```azGDORePK8gMaC0QOYAMyEEuzJnyUi```

Application tokens are case-sensitive, 30 characters long, and may contain the character set ```[A-Za-z0-9]```.

Once you have an API token for your application, you'll need the user key. As with application API tokens, user keys should be considered private and not disclosed to 3rd parties.
                                                                           
Example User Identifier: ```uQiRzpo4DXghDmr9QzzfQu27cmVRsG```

User Contact Info
---------------

The module adds a new contact info type, called `Pushover Device`. The Pushover Device is the new identifier registered with the Pushover service.

Example User Device Name:  ```droid2```

Make sure to set the contact info for each user you want to notify.

Notification Block Properties
---------------

The profile has 6 properties you can set in the notification block in the alarm notification pipeline:

| Property            | Description                                                                                |
| :-------------------| :------------------------------------------------------------------------------------------|
| `Message`           | The message to send, if no custom alarm message is defined.                                |
| `Throttled Message` | The message to send if throttling is turned.                                               |
| `Title`             | The title of the message in the Pushover app.                                              |
| `Priority`          | The priority of the message, affects how the message is presented to the user.             |
| `Retry`             | The time, in seconds, to retry sending emergency priority messages to the user.            |
| `Expire`            | The time, in seconds, to stop sending emergency priority messages with no acknowledgement. |
| `Sound`             | The sound to play when the message is received.                                            |
| `Test Mode`         | Test mode. When true the message is not sent to Pushover but logged in the console.        |

### `Message`
The `Message` property defines the message to send. The message is dynamic using Ignition's Expression language. Defaults to:

```At {eventTime|hh:mm:ss}, alarm "{name}" at "{displayPath}" transitioned to {eventState}.```

### `Throttled Message`
The `Throttled Message` property defines the throttled message to send when consolidation is turned on. The message is dynamic using Ignition's Expression language. Defaults to:

```{alarmEvents.Count} alarm events have occurred.```

### `Title`
The `Title` property defines the title of the message in the Pushover app. The title is optional. If empty, the app's name in Pushover is used.

### `Priority`
The `Priority` property defines the priority of the message. Send as lowest priority to generate no notification/alert, low priority to always send as a quiet notification, high priority to display as high-priority and bypass the user's quiet hours, or emergency priority to also require confirmation from the user.

### `Retry`
The `Retry` property specifies how often (in seconds) the Pushover servers will send the same notification to the user. In a situation where your user might be in a noisy environment or sleeping, retrying the notification (with sound and vibration) will help get his or her attention. This parameter must have a value of at least 30 seconds between retries. 

### `Expire`
The `Expire` property specifies how many seconds your notification will continue to be retried for (every `Retry` seconds). If the notification has not been acknowledged in expire seconds, it will be marked as expired and will stop being sent to the user. Note that the notification is still shown to the user after it is expired, but it will not prompt the user for acknowledgement. This parameter must have a maximum value of at most 10800 seconds (3 hours).

### `Sound`
The `Sound` property defines the sound to play when the message is received. Users can choose from a number of different default sounds to play when receiving notifications, rather than our standard Pushover tone. Applications can override a user's default tone choice on a per-notification basis.
                                                                             
The sound parameter may be set to one of the following built-in sounds:

- Pushover (default)   
- Bike   
- Bugle   
- Cash Register   
- Classical   
- Cosmic   
- Falling   
- Gamelan   
- Incoming   
- Intermission   
- Magic   
- Mechanical   
- Piano Bar   
- Siren   
- Space Alarm   
- Tug Boat   
- Alien Alarm (long)   
- Climb (long)   
- Persistent (long)   
- Pushover Echo (long)   
- Up Down (long)   
- Vibrate Only
- None (silent)

### `Test Mode`
The `Test Mode` property defines the whether or not to run in test mode. If false, the message is sent normally. If true, the message will only be logged through the Ignition console.

Tag Alarm Properties
---------------

The module provides 1 additional alarm property on each alarm, called `Pushover Notification Custom Message`. If the property is set, it defines a custom message for each individual alarm and overrides the `Message` defined in the notification block. The message is dynamic using Ignition's Expression language.