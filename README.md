# Getir Final Case
This application, developed as the final assignment for the Getir Android Bootcamp, adopts the MVVM (Model-View-ViewModel) architecture. It retrieves necessary data from the provided API and displays it on the UI. Written in Kotlin, it supports multi-language (Turkish and English).

## Tech stack
- Minimum SDK level 24+
- 100% [Kotlin](https://kotlinlang.org/)
- [Hilt for DI](https://dagger.dev/hilt/) - Dependency Injection library.
- [Navigation](https://developer.android.com/guide/navigation): Android Jetpack library for managing navigation.
- [Retrofit](https://square.github.io/retrofit/) - Type-safe HTTP client for Android.
- [OkHttp](https://square.github.io/okhttp/) - HTTP client for Android.
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Kotlin coroutine library for asynchronous programming.
- [Flow](https://developer.android.com/kotlin/flow) - Kotlin's reactive stream library for handling asynchronous data streams.
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android.
- [RecyclerView for ConcatAdapter](https://developer.android.com/reference/androidx/recyclerview/widget/ConcatAdapter) - UI component for displaying large data sets with efficient scrolling
- [Lottie](https://github.com/airbnb/lottie-android) - Animation library by Airbnb for rendering Adobe After Effects animations.
- [Shimmer](https://github.com/facebookarchive/shimmer-android) - Library for adding shimmer effect (loading animation) to Android applications.
- [Room](https://developer.android.com/training/data-storage/room) - SQLite database library for Android.
    - [Room for Testing](https://developer.android.com/training/data-storage/room/testing-db) - Testing components of Room library for database testing.


## Test
The tests primarily focus on Room database operations such as insertion, deletion, and retrieval. Additional tests covering more functionalities are planned for future updates.
  - [Junit](https://junit.org/junit4/) - JUnit is a simple framework to write repeatable tests
  - [Room for Testing](https://developer.android.com/training/data-storage/room/testing-db) - Testing components of Room library for database testing.

## Screenshots
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/21c54d35-eb64-440d-92b3-1154a021ab65" width="360" height="800">
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/3c520bb0-79eb-4fd6-ae22-0639f2c14e84" width="360" height="800">
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/cd01fca2-0875-4d6e-b49c-6991a23f79b8" width="360" height="800">
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/6ac3148a-7122-4985-b8b0-c513382ea31f" width="360" height="800">
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/73743133-ed17-48dd-a81b-8fb06a0449ad" width="360" height="800">
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/fab460a2-aa11-4b81-9811-8f1eb6aa19ae" width="360" height="800">
<img src="https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/9aba88cd-5028-43b5-a6c8-4b7f07360926" width="360" height="800">

## Demo

https://github.com/enesarisoy/Getir-Final-Case/assets/70036852/88c8afc9-e48d-4226-b2b0-f3e643855adf

## Run Locally

Clone the project

```bash
  git clone https://github.com/enesarisoy/Getir-Final-Case.git
```
And run the project.

## License

```

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
