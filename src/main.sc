require: city/city.sc
    module = sys.zb-common
require: patterns.sc
theme: /
    state: Start
        q!: $regex</start>
        go!: /Hello
        
    state: Hello
        random:
            a: Привет! Чем могу помочь?
            a: Здравствуй! Спроси меня о чем-нибудь.
            a: Приветствую! Можешь задать мне вопрос.
        
    state: CityExcursions
        q!: * $excursionRequest *
        script:
            # Запрос к API для получения экскурсий в указанном городе
            $temp.response = $http.get("http://217.114.7.99/excursions/?city=${city}", {
                query: {
                    city: $parseTree._City.name
                }
            });

        if: $temp.response.isOk
            # Если данные получены успешно
            if: $temp.response.data.length > 0
                script:
                    # Перемешиваем массив экскурсий
                    function shuffle(array) {
                        for (var i = array.length - 1; i > 0; i--) {
                            var j = Math.floor(Math.random() * (i + 1));
                            var temp = array[i];
                            array[i] = array[j];
                            array[j] = temp;
                        }
                    }
                    shuffle($temp.response.data);

                    # Формирование списка из первых 5 случайных экскурсий
                    var limit = Math.min(5, $temp.response.data.length); // Устанавливаем максимум 5 экскурсий
                    var excursions = [];
                    var buttons = []; // Массив кнопок

                    for (var i = 0; i < limit; i++) {
                        var excursion = $temp.response.data[i];
                        excursions.push((i + 1) + ". " + excursion.name + " - " + excursion.description + " (" + excursion.price + " руб.)");
                        
                        # Добавляем кнопку с названием экскурсии
                        buttons.push({ 
                            text: excursion.name // Текст кнопки
                        });
                    }
                    $temp.excursionsList = excursions.join("\n"); // Собираем список экскурсий с разрывами строк
                    $temp.excursionButtons = buttons; // Сохраняем массив кнопок
                a: Вот экскурсии в городе {{$parseTree._City.name}}:\n{{ $temp.excursionsList }}
                buttons: 
                    "{{ $temp.excursionButtons[0].text }}"
                    "{{ $temp.excursionButtons[1].text }}"
                    "{{ $temp.excursionButtons[2].text }}"
                    "{{ $temp.excursionButtons[3].text }}"
                    "{{ $temp.excursionButtons[4].text }}"
                        
            else:
                a: "В городе {{$parseTree._City.name}} экскурсий не найдено."
        else:
            a: "Не удалось получить список экскурсий. Попробуйте позже."
            
    state: CatchAll
        event!: noMatch
        random:
            a: Простите, я вас не понял.
            a: Извините, я вас не понимаю.
        random:
            a: Переформулируйте, пожалуйста.
            a: Попробуйте сказать по-другому.
