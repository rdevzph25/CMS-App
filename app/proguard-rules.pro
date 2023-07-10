#Code confusion compression ratio, between 0 and 7, defaults to 5, generally does not need to be changed
-optimizationpasses 5

#Do not use case mixing when confusing. The confused class name is lowercase
-dontusemixedcaseclassnames

#Specify classes that do not ignore non-public Libraries
-dontskipnonpubliclibraryclassmembers

#Without pre-testing, preverify is one of the four steps of proguard
#Android does not require preverify, and removing this step will speed up confusion
-dontpreverify

#Specify the algorithm to use for confusion, followed by a filter
#This filter is Google's recommended algorithm and generally does not change
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#Protect the Annotation in your code from confusion.
-keepattributes *Annotation*

#Avoid confusing generics
-keepattributes Signature

#Keep the line number of the code when throwing an exception, which is easy to locate in exception analysis
-keepattributes SourceFile,LineNumberTable

#Do not skip over non-public classes, which are skipped by default
-dontskipnonpubliclibraryclasses

#This is for the windows operating system, because ProGuard assumes that the operating system used is able to distinguish between two file names that are only case-sensitive
#But windows is not such an operating system
-dontusemixedcaseclassnames

#Keep all local native methods unambiguous
-keepclasseswithmembernames class * {
    native <methods>;
}

#Keep subclasses inherited from Activity, Application These classes
#Because of these subclasses, they are all likely to be called externally
#For example, the first line ensures that all activities'subclasses are not confused
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvier
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends com.android.vending.licensing.ILicensingService

#If you have references to the android-supoort-v4.jar package, you can add the following line
-keep public class com.xxxx.app.ui.fragment.** {*;}

#The method parameter that remains in the Activity is the method of view.
#So we write onClick s in layout without affecting them
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#Enumeration classes cannot be confused
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Keep custom controls (inherited from View) unambiguous
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

#Keep Parcelable serialized classes unambiguous
-keep class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}

#Classes that retain Serializable serialization cannot be confused
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#All classes and their methods under R (Resources) should not be confused
-keep class **.R$* {
    *;
}

#For onXXEvent with callback function, it should not be confused
-keepclassmembers class * {
    void *(**On*Event);
}

## Google AdMob specific rules ##
## https://developers.google.com/admob/android/quick-start ##
 
-keep public class com.google.ads.** {
   public *;
}

-ignorewarnings

-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-keep class com.google.analytics.** { *; }

## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Firebase
-keep class com.google.firebase.** { *; }

# Keep them
-keep class com.pnikosis.** { *; }
-keep class com.sdsmdg.** { *; }

-keep class net.i2p.** { *; }
-keep class org.connectbot.** { *; }

-keep class cn.pedant.SweetAlert.** { *; }

-keep class com.trilead.ssh2.** { *; }
    
