# Задача 3
Метод, организующий создание публичного плейлиста и добавляющий в него все \
песни, которые пользователь отмечал лайком. Метод должен возвращать в чат \
ссылку на плейлист. Если такой плейлист уже существует, метод должен его \
обновить, прежде чем вернуть ссылку.

Пример:\
user:
/likedsongs
bot:
@onemonday (https://t.me/onemonday) likedsongs: <link>

Функция, возвращающая текущий трек с ссылкой на него.

Пример: \
user:
@spotifynowbot (https://t.me/spotifynowbot) nowplaying
bot:
Now playing: Jadu Heart — Forgotten Ghosts
Songs | Album