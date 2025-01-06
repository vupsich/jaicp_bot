require: city/city.sc
    module = sys.zb-common

theme: /
    state: Start
        q!: $regex</start>
        a: Начнём.

    state: CityExcursions
        q!: * какие экскурсии есть [в] $City *
        script:
            # Запрос к API для получения 5 случайных экскурсий в указанном городе
            $temp.response = $http.get("http://217.114.7.99/excursions/random/?city_name=" + $parseTree._City.name);

        if: $temp.response.isOk
            # Если данные получены успешно
            if: $temp.response.data.length > 0
                script:
                    var excursions = [];
                    var buttons = []; 

                    # Формирование списка экскурсий и кнопок
                    for (var i = 0; i < $temp.response.data.length; i++) {
                        var excursion = $temp.response.data[i];
                        excursions.push((i + 1) + ". " + excursion.excursion_name + " - " + excursion.excursion_description + " (" + excursion.price + " руб.)");

                        buttons.push({
                            text: excursion.excursion_name + " (ID: " + excursion.excursion_id + ")",
                            payload: { id: excursion.excursion_id } 
                        });
                    }

                    $temp.excursionsList = excursions.join("\n"); 
                    $temp.excursionButtons = buttons; 
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

