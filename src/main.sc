require: city/city.sc
    module = sys.zb-common

theme: /
    state: Start
        q!: $regex</start>
        a: Начнём.
        
    state: CityExcursions
        q!: * какие экскурсии есть [в] $City *
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
                    # Инициализируем массив для хранения экскурсий
                    $temp.excursionsList = [];
                    
                    # Перебираем данные и сохраняем каждую экскурсию в массив
                    for (var i = 0; i < $temp.response.data.length; i++) {
                        var excursion = $temp.response.data[i];
                        $temp.excursionsList.push({
                            name: excursion.name,
                            description: excursion.description,
                            price: excursion.price
                        });
                    }
    
                    # Формируем строку для вывода построчно
                    $temp.excursionsOutput = "";
                    for (var i = 0; i < $temp.excursionsList.length; i++) {
                        var excursion = $temp.excursionsList[i];
                        $temp.excursionsOutput += (i + 1) + ". " + excursion.name + " - " + excursion.description + " (" + excursion.price + " руб.)\n";
                    }
    
                a: Вот экскурсии в городе {{$parseTree._City.name}}:\n{{ $temp.excursionsOutput }}
            else:
                a: "В городе {{$parseTree._City.name}} экскурсий не найдено."
        else:
            a: "Не удалось получить список экскурсий. Попробуйте позже."
