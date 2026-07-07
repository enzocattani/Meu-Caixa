# Regras de ProGuard específicas do Meu Caixa.
# Mantém as entidades do Room para que a reflexão do gerador de código continue funcionando.
-keep class com.meucaixa.app.database.entity.** { *; }
-keepclassmembers class com.meucaixa.app.database.entity.** { *; }

# iText usa reflexão internamente para alguns recursos de fonte/layout.
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**
