1) What is Android?
It is an open-sourced operating system that is used primarily on mobile devices, such as
cell phones and tablets. It is a Linux kernel-based system that&#39;s been equipped with rich
components that allow developers to create and run apps that can perform both basic
and advanced functions.
2) What Is the Google Android SDK?
The Google Android SDK is a toolset that developers need in order to write apps on
Android enabled devices. It contains a graphical interface that emulates an Android
driven handheld environment, allowing them to test and debug their codes.
3) What is the Android Architecture?
Android Architecture is made up of 4 key components:
• Linux Kernel
• Libraries
• Android Framework
• Android Applications
4) Describe the Android Framework.
The Android Framework is an important aspect of the Android Architecture. Here you
can find all the classes and methods that developers would need in order to write
applications on the Android environment.
5) What is AAPT?
AAPT is short for Android Asset Packaging Tool. This tool provides developers with the
ability to deal with zip-compatible archives, which includes creating, extracting as well
as viewing its contents.
6) What is the importance of having an emulator within the Android environment?
The emulator lets developers &quot;play&quot; around an interface that acts as if it were an actual
mobile device. They can write and test codes, and even debug. Emulators are a safe
place for testing codes especially if it is in the early design phase.
7) What is the use of an activityCreator?
An activityCreator is the first step towards the creation of a new Android project. It is
made up of a shell script that will be used to create a new file system structure necessary
for writing codes within the Android IDE.
8 ) Describe Activities.
Activities are what you refer to as the window to a user interface. Just as you create
windows in order to display output or to ask for an input in the form of dialog boxes,
activities play the same role, though it may not always be in the form of a user interface.
9) What are Intents?

Intents displays notification messages to the user from within the Android enabled
device. It can be used to alert the user of a particular state that occurred. Users can be
made to respond to intents.
10) Differentiate Activities from Services.

Activities can be closed, or terminated anytime the user wishes. On the other hand,
services are designed to run behind the scenes, and can act independently. Most
services run continuously, regardless of whether there are certain or no activities being
executed.
11) What items are important in every Android project?
These are the essential items that are present each time an Android project is created:
• AndroidManifest.xml
• build.xml
• bin/
• src/
• res/
• assets/
12) What is the importance of XML-based layouts?
The use of XML-based layouts provides a consistent and somewhat standard means of
setting GUI definition format. In common practice, layout details are placed in XML files
while other items are placed in source files.
13) What are containers?
Containers, as the name itself implies, holds objects and widgets together, depending
on which specific items are needed and in what particular arrangement that is wanted.
Containers may hold labels, fields, buttons, or even child containers, as examples.
14) What is Orientation?
Orientation, which can be set using setOrientation(), dictates if the LinearLayout is
represented as a row or as a column. Values are set as either HORIZONTAL or
VERTICAL.
15) What is the importance of Android in the mobile market?
Developers can write and register apps that will specifically run under the Android
environment. This means that every mobile device that is Android enabled will be able
to support and run these apps. With the growing popularity of Android mobile devices,
developers can take advantage of this trend by creating and uploading their apps on the
Android Market for distribution to anyone who wants to download it.
16) What do you think are some disadvantages of Android?
Given that Android is an open-source platform, and the fact that different Android
operating systems have been released on different mobile devices, there&#39;s no clear cut
policy on how applications can adapt with various OS versions and upgrades.
One app that runs on this particular version of Android OS may or may not run on another version. Since mobile devices such as phones and tabs come in different sizes and forms, it poses a challenge for developers to create apps that can adjust correctly to the right screen size and other varying features and specs.”

17) What is adb?
Adb is short for Android Debug Bridge. It allows developers the power to execute
remote shell commands. Its basic function is to allow and control communication
towards and from the emulator port.
18) What are the four essential states of an activity?
• Active – if the activity is at the foreground
• Paused – if the activity is at the background and still visible
• Stopped – if the activity is not visible and therefore is hidden or obscured by
another activity
• Destroyed – when the activity process is killed or completed terminated
19) What is ANR?
ANR is short for Application Not Responding. This is a dialog that appears to
the user whenever an application has been unresponsive for a long period of time.
20) Which elements can occur only once and must be present?
Among the different elements, the and elements must be present and can occur only
once. The rest are optional, and can occur as many times as needed.
21) How are escape characters used as attribute?
Escape characters are preceded by double backslashes. For example, a newline
character is created using ‘\\n&#39;
22) What is the importance of settings permissions in app development?
Permissions allow certain restrictions to be imposed primarily to protect data and code.
Without these, codes could be compromised, resulting to defects in functionality.
23) What is the function of an intent filter?
Because every component needs to indicate which intents they can respond to, intent
filters are used to filter out intents that these components are willing to receive. One or
more intent filters are possible, depending on the services and activities that is going to
make use of it.
24) Enumerate the three key loops when monitoring an activity
• Entire lifetime – activity happens between onCreate and onDestroy
• Visible lifetime – activity happens between onStart and onStop
• Foreground lifetime – activity happens between onResume and onPause
25) When is the onStop() method invoked?

A call to onStop method happens when an activity is no longer visible to the user, either
because another activity has taken over or if in front of that activity.
26) Is there a case wherein other qualifiers in multiple resources take precedence
over locale?
Yes, there are actually instances wherein some qualifiers can take precedence over
locale. There are two known exceptions, which are the MCC (mobile country code) and
MNC (mobile network code) qualifiers.
27) What are the different states wherein a process is based?
There are 4 possible states:
• foreground activity
• visible activity
• background activity
• empty process
28) How can the ANR be prevented?
One technique that prevents the Android system from concluding a code that has been
responsive for a long period of time is to create a child thread. Within the child thread,
most of the actual workings of the codes can be placed, so that the main thread runs
with minimal periods of unresponsive times.
29) What role does Dalvik play in Android development?
Dalvik serves as a virtual machine, and it is where every Android application runs.
Through Dalvik, a device is able to execute multiple virtual machines efficiently through
better memory management.
30) What is the AndroidManifest.xml?
This file is essential in every application. It is declared in the root directory and contains
information about the application that the Android system must know before the codes
can be executed.
31) What is the proper way of setting up an Android-powered device for app
development?
The following are steps to be followed prior to actual application development in an
Android-powered device:
-Declare your application as &quot;debuggable&quot; in your Android Manifest.
-Turn on &quot;USB Debugging&quot; on your device.
-Set up your system to detect your device.
32) Enumerate the steps in creating a bounded service through AIDL.
1. create the .aidl file, which defines the programming interface
2. implement the interface, which involves extending the inner abstract Stub class as
well as implanting its methods.
3. expose the interface, which involves implementing the service to the clients.
33) What is the importance of Default Resources?

When default resources, which contain default strings and files, are not present, an
error will occur and the app will not run. Resources are placed in specially named
subdirectories under the project res/ directory.
34) When dealing with multiple resources, which one takes precedence?
Assuming that all of these multiple resources can match the configuration of a
device, the &#39;locale&#39; qualifier almost always takes the highest precedence over the
others.
35) When does ANR occur?
The ANR dialog is displayed to the user based on two possible conditions. One is when
there is no response to an input event within 5 seconds, and the other is when a
broadcast receiver is not done executing within 10 seconds.
36) What is AIDL?
AIDL, or Android Interface Definition Language, handles the interface requirements
between a client and a service so both can communicate at the same level through
interprocess communication or IPC. This process involves breaking down objects into
primitives that Android can understand. This part is required simply because a process
cannot access the memory of the other process.
37) What data types are supported by AIDL?
AIDL has support for the following data types:
-string
-charSequence
-List
-Map
-all native Java data types like int,long, char and Boolean
38) What is a Fragment?
A fragment is a part or portion of an activity. It is modular in a sense that you can move
around or combine with other fragments in a single activity. Fragments are also
reusable.
39) What is a visible activity?
A visible activity is one that sits behind a foreground dialog. It is actually visible to the
user, but not necessarily being in the foreground itself.
40) When is the best time to kill a foreground activity?
The foreground activity, being the most important among the other states, is only killed
or terminated as a last resort, especially if it is already consuming too much memory.
When a memory paging state has been reached by a foreground activity, then it is killed so
that the user interface can retain its responsiveness to the user.
41) Is it possible to use or add a fragment without using a user interface?

Yes, it is possible to do that, such as when you want to create a background behavior
for a particular activity. You can do this by using add(Fragment,string) method to add a
fragment from the activity.
42) How do you remove icons and widgets from the main screen of the Android
device?
To remove an icon or shortcut, press and hold that icon. You then drag it downwards to
the lower part of the screen where a remove button appears.
43) What are the core components under the Android application architecture?
There are 5 key components under the Android application architecture:
- services
- intent
- resource externalization
- notifications
content providers
44) What composes a typical Android application project?
A project under Android development, upon compilation, becomes an .apk file. This apk
file format is actually made up of the AndroidManifest.xml file, application code,
resource files, and other related files.
45) What is a Sticky Intent?
A Sticky Intent is a broadcast from sendStickyBroadcast() method such that the intent
floats around even after the broadcast, allowing others to collect data from it.
46) Do all mobile phones support the latest Android operating system?
Some Android-powered phone allows you to upgrade to the higher Android operating
system version. However, not all upgrades would allow you to get the latest version. It
depends largely on the capability and specs of the phone, whether it can support the
newer features available under the latest Android version.
47) What is portable wi-fi hotspot?
Portable Wi-Fi Hotspot allows you to share your mobile internet connection to other
wireless device. For example, using your Android-powered phone as a Wi-Fi Hotspot,
you can use your laptop to connect to the Internet using that access point.
48) What is an action?
In Android development, an action is what the intent sender wants to do or expected to
get as a response. Most application functionality is based on the intended action.
49) What is the difference between a regular bitmap and a nine-patch image?
In general, a Nine-patch image allows resizing that can be used as background or other
image size requirements for the target device. The Nine-patch refers to the way you can
resize the image: 4 corners that are unscaled, 4 edges that are scaled in 1 axis, and the
middle one that can be scaled into both axes.

50) What language is supported by Android for application development?
The main language supported is Java programming language. Java is the most popular
language for app development, which makes it ideal even for new Android developers
to quickly learn to create and deploy applications in the Android environment.
