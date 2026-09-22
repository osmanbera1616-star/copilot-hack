# Muhasebe Takip

Jetpack Compose ve Room kullanılarak hazırlanmış, cihaz üzerinde çalışan basit bir muhasebe takip Android uygulaması.

## Özellikler

- Özet ekranında bakiye, toplam gelir/gider ve son işlemler
- Gelir veya gider kaydı ekleme
- Cari hesap (müşteri/tedarikçi) ekleme ve listeleme
- Room SQLite veritabanı ile çevrimdışı kalıcı kayıt
- Türkçe arayüz ve Türk Lirası biçimlendirmesi

## Çalıştırma

1. Projeyi Android Studio ile açın.
2. Android Studio'nun Gradle senkronizasyonunu tamamlamasını bekleyin.
3. API 26 veya üzeri bir emülatör/cihaz seçin.
4. `app` çalıştırma yapılandırmasıyla uygulamayı başlatın.

Komut satırından çalıştırmak için Android Studio'nun ürettiği Gradle wrapper ile Windows'ta:

```powershell
.\gradlew.bat :app:assembleDebug
```

İlk sürüm tek cihaz ve tek kullanıcı varsayımıyla çevrimdışı çalışır. Bulut senkronizasyonu, kullanıcı hesabı, e-fatura ve rapor dışa aktarma sonraki genişletme alanlarıdır.
