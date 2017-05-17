# The simpliest strategy is to not run proguard against your project's own code.
# This doesn't provide the benefits of optimization & obfuscation against your 
# project, but will still strip the libraries. The advantage is that your app will
# work without any subsequent effort. If you choose this strategy, the proguard
# configuration for the project is simply the line below.

#-keep class com.yourpackage.app.** { *; }
-keep class retrofit.** { *; }
-keep class rx.** { *; }


# The first decision is whether or not you want to obfuscate your code. This provides no
# performance benefit but makes it harder for other people to read your source code. 
# Unfortunately obfuscation can cause issues for code that uses reflection or a few other
# techniques. The default is to obfuscate.

-dontobfuscate
-dontwarn com.squareup.okhttp.**


# Additionally you will need to keep specific classes. A common use case is keeping all
# of the models that are JSON parsed using something like Jackson.

-keep class com.yourpackage.app.model.User { *; }
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
        @butterknife.* <methods>;
}

-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
          public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class your.class.** {
          public void set*(***);
          public *** get*();
}




