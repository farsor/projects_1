/**
 * 
 */


let tables = document.getElementById("tableSelect");
tables.addEventListener("click", (event) => {
    let target = event.target;
    console.log("selected");
    switch(target.value){
        case "collections":
            console.log('collections');
            break;
        case "sources":
            console.log('sources');
            break;

        case "entries":
            console.log("entries");
            break;
    }
});

let srcBtn = document.getElementById('getSrcBtn');
srcBtn.addEventListener('click', (event) => {
    if(event.target.innerText === 'Get Sources'){
        let xhr = new XMLHttpRequest();
        xhr.open('GET', 'http://localhost:8080/sources', true);
        xhr.send();
        xhr.onload = function(){
            console.log(this.status);
            if(this.status === 200){ 
                console.log(this.responseText);                   
                let sources = JSON.parse(this.responseText);
                let output = '<table id="table">' + 
                            '<tr>' +
                                '<td id="id">ID</td>' +
                                '<td id=>Collection</td>' +
                                '<td id="id">Source Number</td>' +
                                '<td id=>Call Number</td>' +
                                '<td id="id">Author</td>' +
                                '<td id=>Title</td>' +
                                '<td id="id">Inscription</td>' +
                                '<td id=>Description</td>' +
                            '</tr>';
                ;
                for(const id in sources){
                    output += 
                        '<tr>' +
                          '<td id="id"><a href="topics/topic_5.html">' + sources[id].id + '</a></td>' +
                          '<td id=>' + sources[id].collection + '</td>' +
                          '<td id="id">' + sources[id].sourceNumber + '</td>' +
                          '<td id=>' + sources[id].callNumber + '</td>' +
                          '<td id="id">' + sources[id].author + '</td>' +
                          '<td id=>' + sources[id].title + '</td>' +
                          '<td id="id">' + sources[id].inscription + '</td>' +
                          '<td id=>' + sources[id].description + '</td>' +
                        '</tr>';
                    }
                    output += '</table>';
                    let srcDiv = document.getElementById('srcs');
                    srcDiv.innerHTML = output;
                    srcBtn.innerText = 'Hide Sources';  

            }
        }
    } else{
        document.getElementById('srcs').innerHTML = '';
        srcBtn.innerText = 'Get Sources';
    }
});