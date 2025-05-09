function getMeanings(word) {
    var response = $http.get("https://dictionary.skyeng.ru/api/public/v1/words/search?search=${word}", {
        timeout: 10000,
        query: {
            word: word
        }
    });
    
    var firstWord = response.data[0];
    log("!THE WORD IS: " + firstWord.id + " " + firstWord.text);
    var meanings = [];
    
    for (var i = 0; i < 5; i++) {
        if (firstWord.meanings[i]) {
            meanings[i] = firstWord.meanings[i].translation.text;
        }
    }
    
    log("!THE MEANINGS ARE: " + meanings);
    var result = []
    
    for (var i = 0; i < 5; i++) {
        if (meanings[i]) {
            var temp = meanings[i].split(/(?:, |; )/);
            for (var j = 0; j < temp.length; j++) {
                result.push(temp[j]);
            }
        }
    }
    
    result = result.slice(0, 5);
    
    log("!THE MEANINGS ARE: " + result);
    
    return result;
}