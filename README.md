Implementation Details:
*************************
1. BONUS 1 is handled -> Empty views such as list items (while data is still loading) should have Loading Shimmer aka Skeletons (example1, example2, example3) resembling final views.
2. BONUS 2 is handled. -> Exponential backoff must be used when trying to reload the data. Only for Get User list Use case handled


Technology/Components Used:
****************************
   > Jetpack Compose (ConstraintLayout used in used details), Navigation
   > Compose Paging library with Remote and Local DB cache.
   > Room database
   > Retrofit with OkHttp Client
   > Hilt
   > Android and Unit Test Cases. Used Mockito, Mock for mocking.
   > Used Clean architecture till to my understanding


- All the use cases mentioned in the required is been implemented except BONUS 3
- Used ConstraintLayout in UserDetail Screen, used Compose Paging and other things in User List screen.
- On NO Internet have provided toast and indicator in last list item with retry option.
- Written unit test and Android Unit test for model and classes which has more functionalities, for simple classes left these unit test. Used Mockito, Mockk for mocking in unit test cases.


Open Limitation Observer: (Limitation with git hub appi)
********************************************************
1. After some time app is getting error as 403 call limit has reached from IP. In this case need to wait for few hours to make it to work or need to connect to different wifi. But if we make too many calls then server blocks our IP.

[IMPORTANT]
    To avoid this, after first page losing, click some item and go to User Detail page so those data will be loaded before we get this error


Compilationn details
*********************

Used Kotlin Version: 1.6.10

Used below Android Studio to compile
*************************************
Android Studio Dolphin | 2021.3.1
Build #AI-213.7172.25.2113.9014738, built on September 1, 2022
Runtime version: 11.0.13+0-b1751.21-8125866 aarch64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
macOS 14.5
GC: G1 Young Generation, G1 Old Generation
Memory: 4096M
Cores: 12
Registry:
    external.system.auto.import.disabled=true
    ide.text.editor.with.preview.show.floating.toolbar=false

Non-Bundled Plugins:
    wu.seal.tool.jsontokotlin (3.7.4)



