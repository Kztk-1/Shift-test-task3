# Data Filtering Utility

## Описание

Утилита для фильтрации содержимого файлов по типам данных:

* **Целые числа** → `<prefix>integers.txt`
* **Вещественные числа** → `<prefix>floats.txt`
* **Строки** → `<prefix>strings.txt`

## Требования

* **Java 17+**
* **Apache Maven 3.6.0+**

## Зависимости

* Apache Commons CLI 1.5.0
* Lombok 1.18.28

## Сборка

```bash
mvn clean package
```

## Установка и запуск на локальном ПК

1. Убедитесь, что установлен JDK 17 и Apache Maven:

   ```bash
   java -version
   mvn -version
   ```

2. Склонируйте репозиторий:

   ```bash
   git clone https://github.com/your/repo.git
   cd repo
   ```

3. Соберите JAR-файл:

   ```bash
   mvn clean package
   ```

4. Запустите утилиту:

   ```bash
   java -jar target/util.jar [OPTIONS] FILE1 FILE2 ...
   ```

## Использование

```bash
java -jar util.jar [OPTIONS] FILE1 FILE2 ...
```

### Опции

| Опция | Описание                       | Пример        |
| ----- | ------------------------------ | ------------- |
| `-o`  | Директория для выходных файлов | `-o ./output` |
| `-p`  | Префикс имен файлов            | `-p result_`  |
| `-a`  | Режим добавления в файлы       | `-a`          |
| `-s`  | Краткая статистика             | `-s`          |
| `-f`  | Полная статистика              | `-f`          |

## Примеры

1. **Базовая обработка с краткой статистикой**:

   ```bash
   java -jar util.jar -s input1.txt input2.txt
   ```

   Результат: `integers.txt`, `floats.txt`, `strings.txt` в текущей директории.

2. **С префиксом и выводом в другую директорию**:

   ```bash
   java -jar util.jar -o ./output -p result_ -f data.txt
   ```

   Результат: `output/result_integers.txt`, `output/result_floats.txt`, `output/result_strings.txt`.

3. **Режим добавления данных**:

   ```bash
   java -jar util.jar -a -p logs_ data.txt
   ```

   Результат: данные добавляются в существующие файлы `logs_integers.txt`, `logs_floats.txt`, `logs_strings.txt`.

## Выходные файлы

Файлы создаются только при наличии соответствующих данных:

```text
<prefix>integers.txt   # Пример содержимого: 45, 100500
<prefix>floats.txt     # Пример содержимого: 3.1415, -0.001
<prefix>strings.txt    # Пример содержимого: Lorem ipsum, test
```

## Статистика

### Краткая (`-s`)

```text
=== Краткая статистика ===
Всего элементов: 15 (int: 5, float: 3, string: 7)
```

### Полная (`-f`)

```text
=== Полная статистика ===
Strings: count=7, minLen=3, maxLen=45
Integers: count=5, min=-100, max=100500, sum=150000, avg=30000.0
Floats: count=3, min=-0.001, max=3.1415, sum=3.1405, avg=1.0468
```

## Коды возврата

* `0` — Успешное выполнение
* `1` — Ошибка в аргументах командной строки
* `2` — Ошибка обработки файлов

> Ошибки при чтении отдельных файлов не прерывают выполнение утилиты.

## Особенности реализации

* **Определение типа данных**:

  * Целые числа: успешный `Long.parseLong()`
  * Вещественные: успешный `Double.parseDouble()`
  * Остальное → строки
* **Поддержка форматов**:

  * Экспоненциальная запись (например, `1.23E-5`)
  * Отрицательные числа
  * Сохранение оригинального формата значений
* **Граничные случаи**:

  * Пустые строки → тип `STRING`
  * Строки с символами и цифрами (например, `123abc`) → `STRING`
  * Файлы без прав доступа пропускаются без остановки обработки

## Структура проекта

```
src/
├── main/java/org/example/
│   ├── ApplicationRunner.java
│   ├── cli/ArgsParser.java
│   ├── dto/FilterConfig.java
│   ├── engine/DataFilterEngine.java
│   ├── model/
│   │   ├── TotalStats.java
│   │   ├── IntegerStatistic.java
│   │   ├── FloatStatistic.java
│   │   └── StringStatistic.java
│   │   └── TypeStatistic.java
│   │   └── DataType.java
│   └── util/
│       ├── StatisticsPrinter.java
│       └── FileWriter.java
└── resources/
pom.xml
```

## Тестирование

```bash
mvn test
```




