require: responseCity.js

require: city/city.sc
    module = sys.zb-common

theme: /

    state: Start || modal = true
        q!: $regex</start>
        intent!: /Начать Игру
        script:
            $session = {}
            $client = {}
            $temp = {}
            $response = {}
        a: Привет! Предлагаю сыграть в игру "Города". Кто загадывает город: компьютер или пользователь?
        
        state: User
            intent: /Пользователь
            a: Назовите город
            script:
                $session.keys = Object.keys($Cities);
                $session.prevBotCity = 0;
            go!: /LetsPlayCitiesGame 

        state: Computer
            intent: /Компьютер
            script:
                $session.keys = Object.keys($Cities);
                var city = $Cities[chooseRandCityKey($session.keys)].value.name
                $reactions.answer(city)
                $session.prevBotCity = city
                
            go!: /LetsPlayCitiesGame 

        state: LocalCatchAll
            event: noMatch
            a: Это не похоже на ответ. Попробуйте еще раз.

    state: LetsPlayCitiesGame
        state: CityPattern
            q: * $City *
            script:
                if (isAFullNameOfCity()) {
                    if (checkLetter($parseTree._City.name, $session.prevBotCity) == true
                    || $session.prevBotCity == 0) {
                    var removeCity = findByName($parseTree._City.name, $session.keys, $Cities)
        
                    if (checkCity($parseTree, $session.keys, $Cities) == true) {
                        $session.keys.splice(removeCity, 1)
                        var key = responseCity($parseTree, $session.keys, $Cities)
                        if (key == 0) {
                            $reactions.answer("Я сдаюсь")
                        } else {
                            $reactions.answer($Cities[key].value.name)
                            $session.prevBotCity = $Cities[key].value.name
                            removeCity = findByName($Cities[key].value.name, $session.keys, $Cities)
                            $session.keys.splice(removeCity, 1)
                        }
                    } else $reactions.answer("Этот город уже был назван")
                    }
                } else $reactions.answer("Используйте только полные названия городов")
        
        state: NoMatch
            event: noMatch
            a: Я не знаю такого города. Попробуйте ввести другой город

    state: EndGame
        intent!: /Завершить игру
        a: Очень жаль! Если передумаешь — скажи "давай поиграем"
