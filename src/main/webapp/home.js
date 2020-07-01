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
srcBtn.addEventListener('click', () => {
    if(srcBtn.innerText === 'Get Sources'){
        let xhr = new XMLHttpRequest();
        xhr.open('GET', 'http://localhost:8080/collections/sources', true);

        xhr.onload = function(){
            console.log(this.status);
            if(this.status === 200){ 
                console.log(this.responseText);                   
                let sources = JSON.parse(this.responseText);
                let output = '<table>';
                for(const id in sources){
                    output += 
                    
                    // '<ul>' + 
                    //     '<li>ID: ' + sources[id].id + '</li>' + 
                    //     '<li>Name: ' + sources[id].description + '</li>' + 
                    //     '</ul>';

                        '<tr>' +
                          '<td>' + sources[id].id + '</td>' +
                          '<td>' + sources[id].description + '</td>'
                        '</tr>';
                    }
                    output += '</table>';

                    document.getElementById('srcs').innerHTML = output;
                    srcBtn.innerText = 'Hide Sources';     
            }
        }
    xhr.send();
    } else{
        document.getElementById('srcs').innerHTML = '';
        srcBtn.innerText = 'Get Sources';
    }
});