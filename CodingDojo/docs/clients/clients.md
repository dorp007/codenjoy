Клиентами мы называем проекты, которые позволяют игроку на ивенте 
с помощью WebSocket подключаться к серверу и играть от имени их героя.
Именно в клиентах пишется код искусственного интеллекта. 

Так как мы используем WebSocket технологию - это позволяет нам получить такие 
преимущества:
- можно играть на любом языке программирования, лишь бы была 
релизована WebSocket библиотека для него.
- можно играть на своей локальной машине (в любимой ide), не загружая код 
никуда на сервер - просто запустил процесс на локали и играешь, 
потушил процесс - герой стоит на месте.
- можно дебажить в production environment - каждую секунду сервер 
запрашивает команду действия для героя у клиента, отправляя ему тестовое 
представление борды (поля) и тут есть возможность остановить debugger код
клиента и исследовать состояние. Все это время бот будет стоять обездвиженным
и игра на сервере естественно не остановится.

Чтобы клиенту максимально упростить жизнь мы разрабатываем новые и поддерживаем
для него существующие клиенты для популярных языков программирования. Каждый клиент
при этом должен: 
- поддерживать все существующие игры
- давать возможность запускаться на чистой системе (без предустановленных программ)
  - из *.bat файлов для Windows
  - из *.sh файлов для Linux/MacOS
  - из Dockerfile для ценителей Docker
Потому эти скрипты умеют:
- загрузить нужные инструменты из Интернета
- установить их в локальную папку (или использовать предустановленные) 
- а затем скомпилировать
- и запустить клиента 
- а так же тесты для него