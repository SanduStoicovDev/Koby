# Koby

> **Status**: *Under active development*\
> **Target**: Android (API 24 → 34)\
> **Language**: **Java 17**\
> **Architecture**: **MVVM + Clean Architecture**

---

## Project Purpose

**Koby** è un assistente personale che integra **OpenAI GPT‑4o** all’interno di un’app Android nativa.\
Il progetto nasce come *capstone* di tirocinio universitario presso l’Università di Milano‑Bicocca con l’obiettivo di:

1. Applicare rigorosamente il pattern **MVVM** in Java mantenendo un codice **test‑driven** e facilmente manutenibile.
2. Sperimentare l’integrazione simultanea di *AI as a Service* (OpenAI) e *Backend as a Service* (Firebase).
3. Offrire un’esperienza utente fluida, sicura e *accessible‑by‑design* sfruttando le più recenti API Android.

---

## Key Features

| Categoria        | Dettagli                                                                                    |
| ---------------- | ------------------------------------------------------------------------------------------- |
| Conversazione AI | Chat multi‑turno con GPT‑4o (streaming) e suggerimenti contestuali.                         |
| Account Utente   | Registrazione / login via **Email + Password** o **Google Sign‑In** con **Firebase Auth**.  |
| Chat Sync        | Cronologia, messaggi e allegati salvati in **Cloud Firestore** con aggiornamenti real‑time. |
| PDF Insights     | Estrazione testo da PDF via **PDFBox** e prompt “Riassumi / Spiega”.                        |
| Multimedia       | **Speech‑to‑Text** & **Text‑to‑Speech** integrati. (Still Developing)                                         |
| UI Adaptive      | Material 3, *Dark / Light Theme* e layout responsive (phone, tablet, foldable).             |
| Privacy & Safety | Chiavi API cifrate, regole Firestore restrittive, traffico HTTPS.                           |
| Offline Support  | Cache Room dei messaggi e *retry queue* per operazioni inevase. (Still Developing)                           |

---

## Architectural Overview

```text
Presentation (XML/ViewBinding)
      │ intents / state via LiveData
      ▼
ViewModel (Lifecycle‑Aware, testabile)  ← Hilt DI
      │ delega a UseCases
      ▼
Domain (Pure Java, Business Logic)
      │ invocano
      ▼
Repository (Interface)
      ├─ Remote DS   → OpenAI API (Retrofit + OkHttp)
      ├─ Remote DS   → Firebase Auth/Firestore/Storage
      └─ Local DS    → Room DB
```

*Threading*: eseguito su `Executors` dedicati, risultati propagati come `LiveData` o `StateFlow`.\
*Error Handling*: sealed‑class `Result<T>` mappata a UI‑state.

---

## Codebase Layout

```
app/
 └── src/
     ├── main/
     │   ├── java/com/unimib/koby/
     │   │   ├── ui/               # Activity, Fragments, ViewModels
     │   │   ├── data/             # Repositories + DataSources
     │   │   │   ├── service/      # Retrofit services (OpenAI)
     │   │   │   └── source/       # Firebase / Room impl.
     │   │   ├── domain/           # UseCases, mappers, models
     │   │   └── util/             # ServiceLocator, encryption, constants
     │   └── res/                  # layout, navigation, drawable, values
     ├── androidTest/              # Instrumented tests
     └── test/                     # Unit tests
```

*Total Java LOC*: **≈ 2 400** (escluse risorse).

---

## Tech Stack

| Layer       | Librerie                                                                                     |
| ----------- | -------------------------------------------------------------------------------------------- |
| UI          | AndroidX, Material Components, Navigation Component                                          |
| DI          | **Hilt 2.51**                                                                                |
| Async       | Java Concurrency + LiveData                                                                  |
| Data Remote | **Retrofit 2.11**, **OkHttp 5**, **Firebase Auth / Firestore / Storage**, **PDFBox‑Android** |
| Data Local  | **Room 3.2** con SQLCipher                                                                   |
| Images      | **Glide 5**                                                                                  |
| Test        | **JUnit 5**, **Mockito‑Core**, **Firebase TestLab**                                          |

---

## Getting Started

1. **Clone**

```bash
git clone https://github.com/your‑github/koby.git
cd koby
```

2. **Secrets**

- Inserisci `openAiKey` in `gradle.properties`
- Aggiungi `google-services.json` (Firebase) in `app/`

3. **Build & Run**

```bash
./gradlew :app:installDebug
adb shell am start -n com.unimib.koby/.ui.MainActivity
```

---

## Testing

```bash
# Unit
./gradlew testDebug

# Instrumentation (API 34)
./gradlew connectedDebugAndroidTest
```

I report HTML sono generati in `app/build/reports/tests/`.

---

## Roadmap

-

---

## Contributing

1. Fork → Feature branch (`feat/awesome‑thing`)
2. Commit secondo *Conventional Commits*
3. Pull Request con descrizione dettagliata e screenshot/gif
4. Attendi *CI checks* (GitHub Actions) e code review

---

## License

```
MIT License – see LICENSE file
```

---

