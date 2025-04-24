require: functions.js
  
theme: /

    state: Start
        q!: $regex</start>
        script:
            $session = {};
            $session.words = $env.get("WORDS").split("\n");
            $session.correctCount = 0;
            $session.wrongCount = 0;
        a: Hello! Let's begin by reviewing your vocabulary. Please translate the following English words into Russian
        go!: /Translate

    state: Translate
        script:
            $session.currentWord = $session.words[$reactions.random($session.words.length)];
            $session.currentMeanings = getMeanings($session.currentWord);
            log("!!!RANDOM WORD IS: " + $session.currentWord);
        a: {{$session.currentWord}}

        state: UserAnswer
            q: $regexp_i<^(?!.*\b(конец|финиш|итоги|давай\s+закончим|сколько\s+правильных\s+переводов)\b)[а-яё\s]+$>
            script:
                var input = $parseTree.text;
                log("!!!INPUT IS: " + input);
                for (var i = 0; i < $session.currentMeanings.length; i++) {
                    if (input === $session.currentMeanings[i]) {
                        $reactions.transition("/Translate/UserAnswer/Correct");
                        return;
                    }    
                }
                
                $reactions.transition("/Translate/UserAnswer/Wrong");
                
            state: Correct
                script:
                    $session.correctCount++;
                a: Correct! Nice!
                go!: /Translate/NextWord
                
            state: Wrong
                script:
                    $session.wrongCount++;
                    var ans = "Wrong :( Some of the possible translations for " + $session.currentWord + " are:\n"
                    for (var i = 1; i <= $session.currentMeanings.length; i++) {
                        ans += i + ". " + $session.currentMeanings[i-1] + "\n";    
                    }
                    $reactions.answer(ans);
                go!: /Translate/NextWord
                    
        state: NextWord
            script:
                $session.currentWord = $session.words[$reactions.random($session.words.length)];
                $session.currentMeanings = getMeanings($session.currentWord);
                log("!!!RANDOM WORD IS: " + $session.currentWord);
            a: Next word is {{$session.currentWord}}
    
    state: Finish    
        intent!: /Finish
        a: Correct answers: {{$session.correctCount}}.\nWrong answers: {{$session.wrongCount}}.\n\nGoodbye, see you later!
        
    state: NoMatch || noContext=true
        event!: noMatch
        a: Извините, не понял.