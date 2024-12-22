require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать про экскурсии. Напишите "экскурсии", чтобы увидеть список.

    state: Hello
        intent!: /привет
        a: Привет! Чем могу помочь?

    state: GetExcursions
        intent!: /экскурсии
        a: Получаю список экскурсий, подождите...
        script:
            # Выполняем HTTP-запрос к вашему API
            let response = fetch("http://217.114.7.99:8000/excursions/", {
                method: "GET"
            });

            # Проверяем, успешно ли выполнен запрос
            if (response.ok) {
                let excursions = response.json();  # Парсим ответ от API
                # Формируем список экскурсий
                context.excur_list = excursions.map(e => `${e.name}: ${e.description} (Цена: ${e.price} руб.)`).join("\n");
            } else {
                context.excur_list = "Извините, не удалось получить список экскурсий.";
            }
        next: ShowExcursions

    state: ShowExcursions
        a: Вот доступные экскурсии:
        a: {{$context.excur_list}}

    state: Bye
        intent!: /пока
        a: Пока! Хорошего дня!

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}