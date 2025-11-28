# TaskHep

Мобилно приложение за управление на задачи по дисциплината **„Мобилни приложения“**.  
ФН: **2201321095**  
Преподавател: **гл. ас. д-р Георги Пашев**

---

## 1. Идея

TaskHep е минималистично приложение тип **to-do list**, в което потребителят може да:

- създава кратки текстови задачи;
- редактира вече съществуващи задачи;
- изтрива задачи със swipe;
- споделя задача към други приложения;
- персонализира интерфейса чрез избор на тема (light/dark) и accent цвят.

Целта е да се демонстрира пълния CRUD цикъл върху локална база данни и работа с modern Android UI компоненти.

---

## 2. Как работи приложението

### 2.1. Main screen (Task list)

- показва списък от задачи от Room база;
- всяка задача е MaterialCard с рамка според accent цвета;
- swipe наляво → фон + икона „кошче“ + потвърждение за изтриване;
- FAB `+` → нова задача;
- горе вдясно: Dark Mode switch + бутон Settings.

### 2.2. Add / Edit Task

- полета: **Title**, **Description**;
- бутони: **Save**, **Cancel**, **Share**;
- Share → системния share sheet;
- празно заглавие → грешка.

### 2.3. Settings (Accent Color)

- пет цвята: red, green, blue, purple, yellow;
- запис в SharedPreferences;
- UI веднага се обновява.

---

## 3. Архитектура

### 3.1. UI слой
- MainActivity  
- AddEditTaskActivity  
- SettingsActivity  
- TaskAdapter  

### 3.2. Data слой
- Task (Entity)  
- TaskDao (CRUD)  
- TaskDatabase (Room)  
- SharedPreferences  

### 3.3. MVVM слой
- TaskRepository  
- TaskViewModel  

---

## 4. Потребителски поток

1. Стартиране → зареждане на тема и accent цвят  
2. FAB → Add Task  
3. Tap → Edit Task  
4. Swipe → Delete + Confirm dialog  
5. Share → ACTION_SEND  
6. Settings → избор на цвят  

---

## 5. Стъпки за стартиране на проекта

### 5.1. Клониране

```bash
git clone https://github.com/Hephaestusu/MobileApps2025-2203121095.git
cd MobileApps2025-2203121095
````

### 5.2. Отваряне в Android Studio

* File → Open
* Изчакай Gradle Sync

### 5.3. Стартиране

* Run ▶
* избери емулатор или реално устройство

---

## 6. APK

Файлът се намира в:

**/apk/app-release.apk**
---

## 7. Използвани технологии

* Kotlin
* Room Database
* RecyclerView
* Material Components
* SharedPreferences
* MVVM (ViewModel + Repository)
* Intents (Share)
* Espresso UI Tests
* JUnit Unit Tests

---

## 8. Идеи за бъдещи подобрения

* категории задачи
* нотификации
* widget
* cloud sync
* drag & drop подреждане

---

## 9. Скриншотове

