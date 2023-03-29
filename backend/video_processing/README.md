### ML microservice

В данном проекте лежит микросервис, который по запросу:

```POST /process```
``` json
{
    "url": <video url",
    "subtitles": <may be null subtitles url>
}
```

запускает обработку видео.

#### Для локального запуска:
1. `pip install -r requirements.txt`
2. `python3 server.py` - ждём пока сервер запустится
3. `python3 client.py <запрос>` - послать запрос.
4. Также либо должен быть запущен бэкенд (core), либо нужно закомментировать код по взаимодействию с ним в файле server.py
   (Все вызовы `requests.post`)

#### Структура проекта:
- В файле `server.py` находится код по сетевому взаимодействию.
- В файле `video.py` - код по процессингу видео. 
- В файлах `model.py` и `small_model.py` - код моделей для Image Caption (
предсказание текста по изображению).
- Файлы `Dockerfile`, `supervisord.conf` - для разворачивания сервера в докер-контейнере.
- __В файле `memory_offloading.ipunb` - код для запуска модели `Salesforce/blip2-opt-2.7b` с использованием memory offloading,
для запуска на небольших gpu (влезает в 12 Гб GPU).__

#### Обработка видео:
1. Скачиваем видео и субтитры.
2. С помощью библиотеки [scenedetect](https://github.com/Breakthrough/PySceneDetect) находим моменты, когда в фильме меняется сцена
3. Для каждого изменения сцены берём кадр где-то спустя секунду с начала сцены. 
4. Если есть субтитры, проверяем, что в текущий момент есть возможность добавить реплику, т. е. есть промежуток около 
2х секунд, когда герои молчат.
5. Если возможность есть, запускаем модель, которая по кадру предсказывает, что на нём происходит. В качестве модели на данный момент
используется [Salesforce/blip2-opt-2.7b](https://huggingface.co/Salesforce/blip2-opt-2.7b), также можно заменить её на менее требовательную
[Salesforce/blip-image-captioning-large](https://huggingface.co/Salesforce/blip-image-captioning-large), если заменить файл `model.py` на
`small_model.py`. Для генерации используется Beam search (см. `model.py`). Написан код для запуска модели на небольшой gpu (`memory_offloading.ipunb`)
6. Сравниваем предсказание с предыдущим (если предыдущее имеется) по метрике похожести текстов METEOR. Если предсказание
"сильно" отличается от предыдущего, это предсказание с таймстемпом кадра и есть новый результат нашего процесса обработки.
7. Как только новый результат получен, высылаем его на бэкенд (функция callback в server.py).
8. Когда видео полностью обработано, высылаем статус для текущего видео PROCESSED.
9. Если при обработке видео произошла ошибка, высылаем статус NOT_PROCESSED.